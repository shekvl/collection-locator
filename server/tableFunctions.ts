//interface - call db functions

import { pool } from './postgres'


//call tablefunctions TODO
export const omop = {

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


// get jason object with meta data field of specific concept (or patch?)

//one function for generating end downloading csv of data
//one generating and directly adding to collection