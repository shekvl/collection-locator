import axios from 'axios'

const port = 3000
axios.defaults.baseURL = `http://localhost:${port}`
// axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*'
// axios.defaults.headers.post

export async function postCollectionFiles(collections: any, attributes: any) {
    try {
        const formData = new FormData()
        collections.forEach((c: any) => {
            formData.append('collections', c)
        })
        attributes.forEach((a: any) => {
            formData.append('attributes', a)
        })
        const response = await axios.post('api/upload/collections', formData)
        return { success: true, message: response.data.message }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.response.data.message }
    }
}