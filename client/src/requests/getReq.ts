import axios from 'axios'

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
        return response.data.map((a: any) => a.concept_id.toString())
    } catch (error: any) {
        console.trace(error)
        return error
    }
}


export async function getQueryRelationships(group: string, vocabulary_id: string) {
    try {
        const response = await axios.get('/db/queryRelationships', { params: { group: group, vocabulary_id: vocabulary_id } })
        return response.data
    } catch (error: any) {
        console.trace(error)
        return error
    }
}

export async function getOntologies() {
    try {
        const response = await axios.get('/db/ontologies')
        return response.data
    } catch (error: any) {
        console.trace(error)
        return error
    }
}

export async function queryAny(concept_ids: any) {
    try {
        const response = await axios.get('/db/queryAny', { params: {concept_ids}})
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.response.data.message }
    }
}

export async function queryRelationships(vocabulary_id:string, relationships:any) {
    try {
        const response = await axios.post('/db/queryRelationships', {
            vocabulary_id,
            relationships
        })
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.response.data.message }
    }
}