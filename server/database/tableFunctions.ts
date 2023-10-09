import {pool} from './postgres'

export const qualityModel = {

    async toDB(characteristics, metrics, characteristic_aggregations, metric_aggregations)
    {
        const client = await pool.connect() //client needed for transaction

        let res = {
            characteristic_ids: {},
            metric_ids: {},
            characteristic_aggregation_ids: {},
            metric_aggregation_ids: {}
        };

        try {
            await client.query('BEGIN')

            for (const characteristic of characteristics) {
                let result = await client.query('insert into quality_characteristic (name, friendly_name, description, added_by) values ($1, $2, $3, $4) returning id', [
                    characteristic.name,
                    characteristic.friendly_name,
                    characteristic.description,
                    1 //added_by //TODO: user input
                ])

                const name = characteristic.name

                // console.log(result);

                const characteristic_id = result.rows[0].id

                res.characteristic_ids[name] = characteristic_id

            }


            //insert collections //metadata quality fields are not required and may therefore be null
            for (const metric of metrics) {
                let result = await client
                    .query('insert into quality_metric (name, friendly_name, description, metric_level, is_default, quality_characteristic_id, added_by) values ($1, $2, $3, $4, $5, $6, $7) returning id', [
                        metric.name,
                        metric.friendly_name,
                        metric.description,
                        metric.level,
                        metric.is_default,
                        res.characteristic_ids[metric.quality_characteristic],
                        1 //added_by //TODO: user input
                    ])

                const name = metric.name
                const metric_id = result.rows[0].id

                res.metric_ids[name] = metric_id
            }

            for (const characteristic_aggregation of characteristic_aggregations) {
                let result = await client.query('insert into quality_characteristic_aggregation (name, friendly_name, is_default, description, added_by) values ($1, $2, $3, $4, $5) returning id', [
                    characteristic_aggregation.name,
                    characteristic_aggregation.friendly_name,
                    characteristic_aggregation.is_default,
                    characteristic_aggregation.description,
                    1 //added_by //TODO: user input
                ])

                const name = characteristic_aggregation.name

                const characteristic_aggregation_id = result.rows[0].id

                res.characteristic_aggregation_ids[name] = characteristic_aggregation_id
            }

            for (const metric_aggregation of metric_aggregations) {
                let result = await client.query('insert into quality_metric_aggregation (name, friendly_name, is_default, description, added_by) values ($1, $2, $3, $4, $5) returning id', [
                    metric_aggregation.name,
                    metric_aggregation.friendly_name,
                    metric_aggregation.is_default,
                    metric_aggregation.description,
                    1 //added_by //TODO: user input
                ])

                const name = metric_aggregation.name

                const metric_aggregation_id = result.rows[0].id

                res.metric_aggregation_ids[name] = metric_aggregation_id

            }

            await client.query('COMMIT')
            return res;

        } catch (err) {
            await client.query('ROLLBACK')
            console.error(err);
            throw err
        } finally {
            client.release()
        }


    },

}

export const collection = {

    /**
     * Get Number of attribute annotations for each collection
     * @param collection_ids Array of collection ids
     * @returns Id, name and attribute count for each collection
     */
    async attributeCount(collection_ids: number[]): Promise<any> {
        return pool.query('select * from get_attribute_counts($1)', [collection_ids])
    },

    /**
     * Insert collections and their corresponding attributes in a transaction
     * @param collections collection records
     * @param attributes attribute records
     * @returns Collection ids of the inserted collections
     */
    async toDb(collections, attributes) {

        // console.log(collections)

        const client = await pool.connect() //client needed for transaction
        let dict = {} //{"collection_name": collection_id}

        async function handleCollectionMetric(quality, collection_id) {
            // console.log(quality)
            let result = await client.query('select * from quality_metric where name=$1', [
                quality.name
            ])
            if (result.rows.length > 0) {
                let metric_id = result.rows[0].id;
                await client.query('insert into quality_metric_collection (quality_metric_id, collection_id, quality_metric_value_for_collection, added_by) values ($1, $2, $3, $4)', [
                    metric_id,
                    collection_id,
                    quality.value.replace(",","."),
                    1 //added_by //TODO: user input
                ])
            } else {
                //throw "no such collection metric: " + quality.name;
            }
        }

        async function handleCharacteristicForCollection(
            quality, category_name, aggregation_name, collection_id) {
            let quality_characteristic_name = category_name;
            let result_char = await client.query('select * from quality_characteristic where name=$1', [
                quality_characteristic_name
            ])
            if (result_char.rows.length > 0) {
                let characteristic_id = result_char.rows[0].id;

                let result_aggr = await client.query('select * from quality_metric_aggregation where name=$1', [
                    aggregation_name
                ])
                if (result_aggr.rows.length > 0) {
                    let aggregation_id = result_aggr.rows[0].id;

                    await client.query('insert into quality_characteristic_collection (quality_characteristic_id, collection_id, aggregation_id, quality_characteristic_value_for_collection, added_by) values ($1, $2, $3, $4, $5)', [
                        characteristic_id,
                        collection_id,
                        aggregation_id,
                        quality.value.replace(",","."),
                        1 //added_by //TODO: user input
                    ])
                } else {
                    //throw "no such aggregation: " + aggregation_name;
                }
            } else {
                //throw "no such quality characteristic: " + quality_characteristic_name;
            }
        }

        async function handleQualityForCollection(quality, aggregation_name, collection_id) {
            let result = await client.query('select * from quality_characteristic_aggregation where name=$1', [
                aggregation_name
            ])
            if (result.rows.length > 0) {
                let aggregation_id = result.rows[0].id;

                await client.query('insert into quality_value_for_collection (collection_id, aggregation_id, quality_value_for_collection, added_by) values ($1, $2, $3, $4)', [
                    collection_id,
                    aggregation_id,
                    quality.value.replace(",","."),
                    1 //added_by //TODO: user input
                ])
            } else {
                //throw "no such aggregation: " + aggregation_name;
            }

        }

        async function handleAttributeMetric(quality, attribute_id) {
            let result = await client.query('select * from quality_metric where name=$1', [
                quality.name
            ])
            if (result.rows.length > 0) {
                let metric_id = result.rows[0].id;
                await client.query('insert into quality_metric_attribute (quality_metric_id, attribute_id, quality_metric_value_for_attribute, added_by) values ($1, $2, $3, $4)', [
                    metric_id,
                    attribute_id,
                    quality.value.replace(",","."),
                    1 //added_by //TODO: user input
                ])
            } else {
                //throw "no such attribute metric: " + quality.name;
            }

        }

        async function handleCharacteristicForAttribute(quality, category_name, aggregation_name, attribute_id) {
            let quality_characteristic_name = category_name;
            let result_char = await client.query('select * from quality_characteristic where name=$1', [
                quality_characteristic_name
            ])
            if (result_char.rows.length > 0) {
                let characteristic_id = result_char.rows[0].id;

                let result_aggr = await client.query('select * from quality_metric_aggregation where name=$1', [
                    aggregation_name
                ])
                if (result_aggr.rows.length > 0) {
                    let aggregation_id = result_aggr.rows[0].id;

                    await client.query('insert into quality_characteristic_attribute (quality_characteristic_id, attribute_id, aggregation_id, quality_characteristic_value_for_attribute, added_by) values ($1, $2, $3, $4, $5)', [
                        characteristic_id,
                        attribute_id,
                        aggregation_id,
                        quality.value.replace(",","."),
                        1 //added_by //TODO: user input
                    ])
                } else {
                    //throw "no such aggregation: " + aggregation_name;
                }
            } else {
                //throw "no such quality characteristic: " + quality_characteristic_name;
            }

        }

        async function handleQualityForAttribute(quality, aggregation_name, attribute_id) {
            let result = await client.query('select * from quality_characteristic_aggregation where name=$1', [
                aggregation_name
            ])
            if (result.rows.length > 0) {
                let aggregation_id = result.rows[0].id;

                await client.query('insert into quality_value_for_attribute (attribute_id, aggregation_id, quality_value_for_attribute, added_by) values ($1, $2, $3, $4)', [
                    attribute_id,
                    aggregation_id,
                    quality.value.replace(",","."),
                    1 //added_by //TODO: user input
                ])
            } else {
                //throw "no such aggregation: " + aggregation_name;
            }

        }

        try {
            await client.query('BEGIN')

            //insert collections //metadata quality fields are not required and may therefore be null
            for (const collection of collections) {

                // console.log(collection.name)

                let result1 = await client.query('insert into collection (name, institution_id, number_of_records, added_by) values ($1, $2, $3, $4) returning id', [
                    collection.name,
                    'test biobank', //institution_id //TODO: user input
                    collection.number_of_records,
                    1 //added_by //TODO: user input
                ])

                const name = collection.name
                const collection_id = result1.rows[0].id

                dict[name] = collection_id

                for (const quality of collection.qualities) {
                    if (! quality.name.includes(":")) {
                        // collection metric
                        await handleCollectionMetric(quality, collection_id);
                    } else {
                        // aggregation
                        let names = quality.name.split(":");
                        let category_name = names[0];
                        let aggregation_name = names[1];
                        if (category_name !== "quality") {
                            // quality characteristic for collection
                            await handleCharacteristicForCollection(quality, category_name, aggregation_name, collection_id);
                        } else {
                            // integrated collection quality
                            await handleQualityForCollection(quality, aggregation_name, collection_id) // TODO: quality
                        }

                    }
                }

            }

            for (const attribute of attributes) {

                // console.log(attribute)

                let result2 = await client.query(
                    'insert into attribute (collection_id, attribute_name) values ($1, $2) returning id', [
                    dict[attribute.collection_name],
                    attribute.name
                ])

                let attribute_id = result2.rows[0].id;

                for (let concept of attribute.concepts) {
                    await client.query('insert into attribute_concept (attribute_id, code, vocabulary_id) values($1, $2, $3)', [
                        attribute_id,
                        concept.code,
                        concept.vocabulary_id
                    ])

                }

                for (const quality of attribute.qualities) {
                    if (! quality.name.includes(":")) {
                        // attribute metric
                        await handleAttributeMetric(quality, attribute_id);
                    } else {
                        // aggregation
                        let names = quality.name.split(":");
                        let category_name = names[0];
                        let aggregation_name = names[1];
                        if (category_name !== "quality") {
                            // quality characteristic for attribute
                            await handleCharacteristicForAttribute(quality, category_name, aggregation_name, attribute_id);
                        } else {
                            // integrated attribute quality
                            await handleQualityForAttribute(quality, aggregation_name, attribute_id) // TODO: quality
                        }

                    }
                }


            }

            await client.query('COMMIT')
            return Object.values(dict)

        } catch (err) {
            await client.query('ROLLBACK')
            console.log(err)
            throw err
        } finally {
            client.release()
        }
    },
}

export const cdm = {}

export const locator = {

    /**
     * Get all cdm concept ids that are referenced by annotations stored in the collection locator
     * @returns Concepts ids
     */
    async allConcepts(): Promise<any> {
        return pool.query('select concept_id from concept')
    },

    /**
     * Get relationships belonging to a set of the relationships_of_interest table
     * @param set set of the relationships_of_interest table
     * @param vocabulary_id cdm vocabulary id
     * @returns Concept relationships with name and distinct values
     */
    async getRelationshipsOfInterest(set: string, vocabulary_id: string): Promise<any> {
        return pool.query('select set, name, relationship_id, distinct_values, vocabulary_id from relationship_of_interest where set = $1 and vocabulary_id = $2', [set, vocabulary_id])
    },

    /**
     * Get all cdm vocabularies currently supported by the collection locator
     * @returns List of vocabularies
     */
    async getSupportedVocabularies(): Promise<any> {
        return pool.query('select * from supported_vocabulary')
    },

    async getQualityCharacteristics(): Promise<any> {
        return pool.query('select id, friendly_name, name from quality_characteristic')
    },

}

export const query = {

    /**
     * Complement list of concept ids with concepts that can be directly mapped to any of the given concepts
     * @param concept_ids Array of concept ids as integers
     * @returns `concept_ids` extended by additional equivalent concept ids
     */
    async complementMaps(concept_ids) {
        const mapsResult = await pool.query('select * from get_maps($1)', [concept_ids])
        concept_ids = Array.from(
            new Set(
                concept_ids.concat(mapsResult.rows.map((r) => r.concept_id))
            )
        )

        return concept_ids
    },

    /**
     * Complement list of concept ids with concepts that are descendents of the given concepts
     * @param concept_ids Array of concept ids as integers
     * @returns `concept_ids` extended by additional descendent concept ids
     */
    async complementDescendents(concept_ids) {
        const descResult = await pool.query('select * from get_descendents($1)', [concept_ids])
        concept_ids = Array.from(
            new Set(
                concept_ids.concat(descResult.rows.map((r) => r.concept_id))
            )
        )

        return concept_ids
    },

    /**
     * Get all collections that contain `ANY` concept of the given concept id list extended by mappable and descendent concepts.
     * @param concept_ids Array of concept ids or single concept id
     * @returns Collections containing `ANY` of the given concept ids
     */
    async any(concept_ids): Promise<any> { //TODO: send back details on search procedure (used concepts etc.)
        if (!concept_ids) {
            return []
        } else if (typeof concept_ids !== 'object') { //create array of single value
            concept_ids = [concept_ids]
        }

        //parse strings to integer,so the Set works properly
        concept_ids = concept_ids.map(c => parseInt(c))
        // console.log(concept_ids)

        // let allFound = false
        // while (!allFound) {
        let extended_ids = Array.from(concept_ids)

        //include 'Maps to' concepts
        extended_ids = await this.complementMaps(extended_ids)
        // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))

        //include descendent concepts
        extended_ids = await this.complementDescendents(extended_ids)
        // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))

        concept_ids = extended_ids

        // new Set(extended_ids).size === new Set(concept_ids).size
        //     ? allFound = true
        //     : concept_ids = extended_ids
        // }

        //get collections containing any of passed concept_ids
        return pool.query('select * from query_any($1)', [concept_ids])
    },

    /**
     * Get all collections that contain `ALL` of the given concept idd or respectively a mappable or descendent concept instead of a given concept by performing an intersection of `ANY` subqueries.
     * @param concept_ids Array of concept ids or single concept id
     * @returns Collections containing `ANY` of the given concept ids
     */
    async all(concept_ids): Promise<any> { //TODO: send back details on search procedure (used concepts etc.)
        if (!concept_ids) {
            return []
        } else if (typeof concept_ids !== 'object') {
            concept_ids = [concept_ids]
        }

        //parse strings to integer, so the Set works properly
        concept_ids = concept_ids.map(c => parseInt(c))

        let collection_ids = []
        let collection = undefined

        //Intersection of ANY queries for each concept id
        for (const concept_id of concept_ids) {
            collection = await this.any(concept_id)

            if (collection_ids.length == 0) {
                collection_ids = collection.rows.map((row) => row.id)
            } else {
                let intersection = collection.rows.filter((row) => collection_ids.includes(row.id))
                if (intersection.length == 0) {
                    return []
                } else {
                    collection_ids = intersection.map((row) => row.id)
                }
            }
        }

        return collection.rows.filter((row) => collection_ids.includes(row.id))
    },

    /**
     * Get all attributes for each of the specified collections
     * @param collection_ids Array of collection ids
     * @returns Attribute records
     */
    async attributes(collection_ids): Promise<any> {
        return pool.query('select * from query_attributes($1)', [collection_ids])
    },

    async collections_by_quality(from, to, qid) : Promise<any> {
        if (from == "null")
            return this.collections_by_quality_to(to, qid);
        if (to == "null")
            return this.collections_by_quality_from(from, qid);
        return this.collections_by_quality_between(from, to, qid);
    },

    async collections_by_quality_between(from, to, qid): Promise<any> {
        return pool.query('select * from query_collections_by_quality($1,$2,$3)', [from, to, qid])
    },

    async collections_by_quality_from(from, qid): Promise<any> {
        return pool.query('select * from query_collections_by_quality_from($1,$2)', [from, qid])
    },

    async collections_by_quality_to(to, qid): Promise<any> {
        return pool.query('select * from query_collections_by_quality_to($1,$2)', [to, qid])
    },

    async collections_by_attribute_quality(concept_ids, from, to, qid): Promise<any> { //TODO: send back details on search procedure (used concepts etc.)
        if (!concept_ids) {
            return []
        } else if (typeof concept_ids !== 'object') { //create array of single value
            concept_ids = [concept_ids]
        }

        //parse strings to integer,so the Set works properly
        concept_ids = concept_ids.map(c => parseInt(c))
        // console.log(concept_ids)

        // let allFound = false
        // while (!allFound) {
        let extended_ids = Array.from(concept_ids)

        //include 'Maps to' concepts
        extended_ids = await this.complementMaps(extended_ids)
        // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))

        //include descendent concepts
        extended_ids = await this.complementDescendents(extended_ids)
        // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))

        concept_ids = extended_ids

        // new Set(extended_ids).size === new Set(concept_ids).size
        //     ? allFound = true
        //     : concept_ids = extended_ids
        // }

        //get collections containing any of passed concept_ids
        // return pool.query('select * from query_any($1)', [concept_ids])

        if (from == "null")
            return this.collections_by_attribute_quality_to(concept_ids, to, qid);
        if (to == "null")
            return this.collections_by_attribute_quality_from(concept_ids, from, qid);
        return this.collections_by_attribute_quality_between(concept_ids, from, to, qid);

    },

    async collections_by_attribute_quality_between(concept_ids, from, to, qid): Promise<any> {
        return pool.query('select * from query_collections_by_attribute_quality_any($1,$2,$3,$4)', [concept_ids, from, to, qid])
    },

    async collections_by_attribute_quality_from(concept_ids, from, qid): Promise<any> {
        return pool.query('select * from query_collections_by_attribute_quality_any_from($1,$2,$3)', [concept_ids, from, qid])
    },

    async collections_by_attribute_quality_to(concept_ids, to, qid): Promise<any> {
        return pool.query('select * from query_collections_by_attribute_quality_any_to($1,$2,$3)', [concept_ids, to, qid])
    },


    /**
     * Get all quality values for each of the specified collections
     * @param collection_ids Array of collection ids
     * @returns Quality records
     */
    async collection_quality_values(collection_ids): Promise<any> {
        return pool.query('select * from get_quality_values_for_collections($1)', [collection_ids])
    },

    async attribute_quality_values(collection_ids): Promise<any> {
        return pool.query('select * from get_quality_values_for_attributes($1)', [collection_ids])
    },


    /**
     * Get concept ids of the given vocabulary that are associated to `ALL` relationships with `ANY` of the given values for each relationship. Returns empty Array if no relationship objects is specified.
     *
     * let exampleRelationships = [
       { relationship_id: 'Has property', concept_names: ['Finding', 'Time (e.g. seconds)', 'Ratio'] },
       { relationship_id: 'Has scale type', concept_names: ['Doc', 'Qn'] },
       ]
     *
     * @param vocabulary_id cdm vocabulary id
     * @param relationships Array of objects containing a relationship id and possible values as concept names
     * @returns Array of concept ids
     */
    async relationships(vocabulary_id: string, relationships: { relationship_id: any, concept_names: any }[]): Promise<any> {
        const subqueries = []
        for (const r of relationships) {
            //select all concept ids in the collection locator that have the given relationship with another concept in the omop cdm and that has one of the given values
            let subquery = `
                select c.concept_id
                from concept c, cdm.concept_relationship cr, cdm.concept cc
                where c.vocabulary_id = \'${vocabulary_id}\'
                and cr.concept_id_1 = c.concept_id
                and cr.concept_id_2 = cc.concept_id
                and cr.relationship_id = \'${r.relationship_id}\'
                and cc.concept_name = Any(Array[${r.concept_names.map((r) => `\'${r}\'`)}])
                `
            subqueries.push(subquery)
        }
        const query = subqueries.join(' intersect ')
        return pool.query(query)
    },
}

export const generate = {

    /**
     * Generate dataset (i.e. a random set of attributes forming a collection) with optionally specified name and `size` collection attributes
     * @param size number of attributes
     * @param collection_name name of the generated collection
     * @returns Table of randomly generated collection attributes
     */
    async dataset(size: number, collection_name?: string): Promise<any> {
        if (typeof collection_name !== 'undefined') {
            return pool.query('select * from generate_dataset($1, $2)', [collection_name, size])
        } else {
            return pool.query('select * from generate_dataset($1)', [size])
        }
    },

}
