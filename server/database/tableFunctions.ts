import { pool } from './postgres'


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

        const client = await pool.connect() //client needed for transaction
        let dict = {} //{"collection_name": collection_id}

        try {
            await client.query('BEGIN')

            //insert collections //metadata quality fields are not required and may therefore be null
            for (const collection of collections) {
                let result = await client.query('select id from insert_record_collection($1, $2, $3, $4, $5, $6, $7, $8, $9)', [
                    collection.name,
                    'test biobank', //institution_id //TODO: user input
                    collection.number_of_records,
                    collection.completeness || null,
                    collection.accuracy || null,
                    collection.reliability || null,
                    collection.timeliness || null,
                    collection.consistancy || null,
                    1 //added_by //TODO: user input
                ])

                const name = collection.name
                const id = result.rows[0].id

                dict[name] = id
            }

            //insert attributes //metadata quality fields are not required and may therefore be null
            //attribute should only occure once in attribute table but can match several attribute_concept records
            for (const attribute of attributes) {
                let result = await client.query('select * from attribute where collection_id=$1 and attribute_name=$2', [
                    dict[attribute.collection_name],
                    attribute.attribute_name,
                ])

                let existingAttribute = result.rows.length > 0

                if (!existingAttribute) {
                    result = await client.query('select id from insert_record_attribute($1, $2, $3, $4, $5, $6, $7)', [
                        dict[attribute.collection_name],
                        attribute.attribute_name,
                        attribute.completeness || null,
                        attribute.accuracy || null,
                        attribute.reliability || null,
                        attribute.timeliness || null,
                        attribute.consistancy || null,
                    ])
                }

                await client.query('select insert_record_attribute_concept($1, $2, $3)', [
                    result.rows[0].id,
                    attribute.code,
                    attribute.vocabulary_id
                ])
            }

            await client.query('COMMIT')
            return Object.values(dict)

        } catch (err) {
            await client.query('ROLLBACK')
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

        let allFound = false
        while (!allFound) {
            let extended_ids = Array.from(concept_ids)

            //include 'Maps to' concepts
            extended_ids = await this.complementMaps(extended_ids)
            // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))

            //include descendent concepts
            extended_ids = await this.complementDescendents(extended_ids)
            // console.log(extended_ids.filter((c) => !concept_ids.includes(c)))


            new Set(extended_ids).size === new Set(concept_ids).size
                ? allFound = true
                : concept_ids = extended_ids
        }

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