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