//interface - call db functions

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


}

export const attribute = {


}

export const transaction = {

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
                    collection.completeness,
                    collection.accuracy,
                    collection.reliability,
                    collection.timeliness,
                    collection.consistancy,
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
                    attribute.completeness,
                    attribute.accuracy,
                    attribute.reliability,
                    attribute.timeliness,
                    attribute.consistancy,
                ])

                await client.query('select insert_record_attribute_concept($1, $2, $3)', [
                    result.rows[0].id,
                    attribute.code,
                    attribute.vocab
                ])
            }

            await client.query('COMMIT')
            return dict

        } catch (err) {
            await client.query('ROLLBACK')
            throw err
        } finally {
            client.release()
        }
    }
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