import axios from './axios'

/**
 * Sent post request with files as form data in the body
 * @param collections Form data field for collections
 * @param attributes Form data field for attributes
 * @returns Collection details on success
 */
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

/**
 * Sent post request with files as form data in the body
 * @param qualityModels Form data field for collections
 * @returns Collection details on success
 */
export async function postQualityModelFiles(qualityModels: any) {
    try {
        const formData = new FormData()
        qualityModels.forEach((c: any) => {
            formData.append('qualityModels', c)
        })
        const response = await axios.post('api/upload/quality-models', formData)
        return { success: true, message: response.data.message }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.response.data.message }
    }
}
