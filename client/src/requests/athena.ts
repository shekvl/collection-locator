import axios from 'axios'
import serialize from '../js_helpers/serialize'

const port = 3000
axios.defaults.baseURL = `http://localhost:${port}`
// axios.defaults.headers.post


export type AthenaParams = {
    query: string,
    pageSize?: number | string,
    domain?: string[],
    standardConcept?: string[],
    conceptClass?: string[],
    vocabulary?: string[],
    invalidReason?: string[]
}

//https://github.com/OHDSI/Athena
export async function queryAthena(params: AthenaParams) {
    try {
        const response = await axios.get('https://athena.ohdsi.org/api/v1/concepts?' + serialize(params))

        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}