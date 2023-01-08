//interface - call db functions

import { getOntologies } from './controllers/dbCtrl'
import { pool } from './postgres'


export const omop = { //TODO

    /**
     * some description
     * @param concept_id adfasdf
     * @returns
     */
    async fetchChildren(concept_id: number): Promise<any> {
        return pool.query('select * from get_children($1)', [concept_id])
    },

    /**
     *
     * @param concept_id
     * @returns
     */
    async dsdf(concept_id: number): Promise<any> {
        // return pool.query('select * from get_children($1)', [concept_id])
    }


}


export const collection = {

    async getAttributeCount(collection_ids: number[]): Promise<any> {
        console.log(typeof collection_ids, collection_ids)
        return pool.query('select * from get_attribute_counts($1)', [collection_ids])
    },

}

export const concepts = {

    async all(): Promise<any> {
        return pool.query('select * from concept')
    },

    async allFromCdm(): Promise<any> {
        return pool.query('select concept_id from cdm.concept where vocabulary_id = \'LOINC\'')
    },
}

export const query = {

    async getQueryRelationships(group: string, vocabulary_id: string): Promise<any> {
        return pool.query('select name, distinct_values from query_relationship where "group" = $1 and vocabulary_id = $2', [group, vocabulary_id])
    },

    async getOntologies(): Promise<any> {
        return pool.query('select * from ontology')
    },

    async queryAny(concept_ids): Promise<any> {
        return pool.query('select * from queryAny($1)', [concept_ids])
    },

    async queryAttributes(collection_ids): Promise<any> {
        return pool.query('select * from queryAttributes($1)', [collection_ids])
    },

}

export const attributes = {


}

export const transaction = {

    /**
     * Transaction inserting collections and their attributes
     * @param collections
     * @param attributes
     * @returns collection ids of inserted collections
     */
    async uploadCollection(collections, attributes) {

        const client = await pool.connect()
        let dict = {}

        try {
            await client.query('BEGIN')

            for (const collection of collections) {
                let result = await client.query('select id, name from insert_record_collection($1, $2, $3, $4, $5, $6, $7, $8, $9)', [
                    collection.name,
                    'biobank klagenfurt', //institution_id,
                    collection.number_of_records,
                    collection.completeness || null,
                    collection.accuracy || null,
                    collection.reliability || null,
                    collection.timeliness || null,
                    collection.consistancy || null,
                    1 //added_by TODO get from GUI
                ])

                const name = result.rows[0].name
                const id = result.rows[0].id

                dict[name] = id
            }

            for (const attribute of attributes) {
                let result = await client.query('select id from insert_record_attribute($1, $2, $3, $4, $5, $6, $7)', [
                    dict[attribute.collection_name],
                    attribute.attribute_name,
                    attribute.completeness || null,
                    attribute.accuracy || null,
                    attribute.reliability || null,
                    attribute.timeliness || null,
                    attribute.consistancy || null,
                ])

                console.log(result.rows[0].id,
                    attribute.code,
                    attribute.vocab)
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

// get json object with meta data field of specific concept (or patch?)

//one function for generating end downloading csv of data
//one generating and directly adding to collection

export const generate = {

    async dataset(size: number, collection_name?: string): Promise<any> {
        if (typeof collection_name !== 'undefined') {
            return pool.query('select * from generate_dataset($1, $2)', [collection_name, size])
        } else {
            return pool.query('select * from generate_dataset($1)', [size])
        }
    },

}