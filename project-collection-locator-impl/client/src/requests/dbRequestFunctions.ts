import axios from './axios'
import serialize from '../js_helpers/serialize'

/**
 * Get all annotation concepts
 * @returns Array of Concept IDs
 */
export async function getAllConcepts() {
    try {
        const response = await axios.get('api/db/concepts')
        return response.data.map((a: any) => a.concept_id.toString())
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

/**
 * Get relationships of interest specified by set name and vocabulary id
 * @param set Name of the relationship set
 * @param vocabulary_id Vocabulary ID
 * @returns Result table
 */
export async function getRelationshipsOfInterest(set: string, vocabulary_id: string) {
    try {
        const response = await axios.get('api/db/relationshipsOfInterest', {
            params: { set, vocabulary_id }
        })
        return response.data
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

/**
 * Get supported vocabularies
 * @return Vocabulary Ids
 */
export async function getVocabularies() {
    try {
        const response = await axios.get('api/db/vocabularies')
        return response.data
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

export async function getQualityCharacteristics() {
    try {
        const response = await axios.get('api/db/qualityCharacteristics')
        return response.data
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}


/**
 * Query collections that contain any of the specified concepts (or equivalent concepts)
 * @returns Collections
 */
export async function queryAny(concept_ids: any) {
    try {
        const response = await axios.get('api/db/queryAny?' + serialize({ concept_ids }))
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

/**
 * Query collections that contain each of the specified concepts (or equivalent concepts)
 * @returns Collections
 */
export async function queryAll(concept_ids: any) {
    try {
        const response = await axios.get('api/db/queryAll?' + serialize({ concept_ids }))
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

/**
 * Query collections that contain concepts to which all specified relationships apply
 * @param relationships Object with relationship_ids and associated lists with possible values
 * @returns Collections IDs
 */
export async function queryRelationships(vocabulary_id: string, relationships: any) {
    try {
        const response = await axios.post('api/db/queryRelationships', {
            vocabulary_id,
            relationships
        })
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

export async function queryCollectionsByQuality(from1: any, to1: any, qid: any) {
    try {
        console.log(from1);
        console.log(to1);
        const response = await axios.get('api/db/queryCollectionsByQuality?from1=' + from1 + "&to1="+to1+"&qid="+qid);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

export async function queryCollectionsByAttributeQuality(concept_ids: any, from1: any, to1: any, qid: any) {
    try {
        console.log(from1);
        console.log(to1);
        const response = await axios.get('api/db/queryCollectionsByAttributeQuality?'+
            serialize({ concept_ids }) + '&from1=' + from1 + "&to1=" + to1 + "&qid=" + qid);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}
