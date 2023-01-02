import axios from 'axios'

const port = 3001
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