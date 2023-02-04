import axios from 'axios'
import serialize from '../js_helpers/serialize'

// Possible get request parameters of Athena API
export type AthenaParams = {
    query: string,
    pageSize?: number | string,
    page?: number,
    domain?: string[],
    standardConcept?: string[],
    conceptClass?: string[],
    vocabulary?: string[],
    invalidReason?: string[]
}

// Athena API Documentation: https://github.com/OHDSI/Athena
/**
 * Query Athena API for concepts matching the query string
 * @param params Object with request parameters
 * @returns Matching concepts
 */
export async function queryAthena(params: AthenaParams) {
    try {
        const response = await axios.get('https://athena.ohdsi.org/api/v1/concepts?' + serialize(params))
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}