import axios from 'axios'
import serialize from '../js_helpers/serialize'

const port = 3000
axios.defaults.baseURL = `http://localhost:${port}`
// axios.defaults.headers.post

// export async function getAttributeCount(collectionIds: any[]) {
//     try {
//         const response = await axios.get('/db/collections/attributeCount', { params: collectionIds })
//         return response.data.message
//     } catch (error: any) {
//         console.trace(error)
//         return error
//     }
// }


export async function getAllConcepts() {
    try {
        const response = await axios.get('/db/concepts')
        console.log(response)
        return response.data.map((a: any) => a.concept_id.toString())
    } catch (error: any) {
        console.trace(error)
        return error
    }
}


export async function getRelationshipsOfInterest(group: string, vocabulary_id: string) {
    try {
        const response = await axios.get('/db/relationshipsOfInterest', {
            params: { group, vocabulary_id }
        })
        return response.data
    } catch (error: any) {
        console.trace(error)
        return error
    }
}

export async function getVocabularies() {
    try {
        const response = await axios.get('/db/vocabularies')
        return response.data
    } catch (error: any) {
        console.trace(error)
        return error
    }
}

export async function queryAny(concept_ids: any) {
    try {
        const response = await axios.get('/db/queryAny?' + serialize({ concept_ids }))
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

export async function queryAll(concept_ids: any) {
    try {
        const response = await axios.get('/db/queryAll?' + serialize({ concept_ids }))
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

export async function queryRelationships(vocabulary_id: string, relationships: any) {
    try {
        const response = await axios.post('/db/queryRelationships', {
            vocabulary_id,
            relationships
        })
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}
