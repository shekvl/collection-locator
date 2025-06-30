import axios from './axios'
import { springAxios } from './axios';
import serialize from '../js_helpers/serialize'

/**
 * Get all annotation concepts
 * @returns Array of Concept IDs
 */
export async function getAllConcepts() {
    try {
        const response = await axios.get('api/db/concepts')
        return response.data.map((a: any) => a.concept_info.toString())
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
        concept_ids = real_ids(concept_ids);
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
        concept_ids = real_ids(concept_ids);
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
        // console.log(from1);
        // console.log(to1);
        concept_ids = real_ids(concept_ids);

        const response = await axios.get('api/db/queryCollectionsByAttributeQuality?'+
            serialize({ concept_ids }) + '&from1=' + from1 + "&to1=" + to1 + "&qid=" + qid);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error)
        return { success: false, message: error.message }
    }
}

// -------- New (Spring) Definition endpoints --------

export async function countDefinitions() {
    try {
        const response = await springAxios.get('/definitions/count');
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function listDefinitions() {
    try {
        const response = await springAxios.get('/api/definitions');
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function forAutoComplete(q: string) {
    try {
        const response = await springAxios.get('/api/cdmconceptcautocomplete?q='+q);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}


export async function getDefinition(id: string) {
    try {
        const response = await springAxios.get(`/definitions/${id}/json`);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function createDefinition(data: any) {
    try {
        const response = await springAxios.post('/definitions/new', data);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function updateDefinition(id: string, data: any) {
    try {
        const response = await springAxios.put(`/definitions/${id}/edit`, data);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function updateDefinitionDirect(id: string, data: any) {
    try {
        data.columns = data.columns.map((col : { cdm: any, name: string }) => ({
            codeText: col.cdm.id,
            name: col.name,
            cdm: col.cdm
        }));

        const response = await springAxios.put(`/api/definitions/${id}`, data);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function createDefinitionDirect(data: any) {
    try {
        data.columns = data.columns.map((col : { cdm: any, name: string }) => ({
            codeText: col.cdm.id,
            name: col.name,
            cdm: col.cdm
        }));

        const response = await springAxios.post(`/api/definitions`, data);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function deleteDefinition(id: string) {
    try {
        const response = await springAxios.delete(`/api/definitions/${id}`);
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function getXmlSchema() {
    try {
        const response = await springAxios.get('/api/getXmlSchema',
            { responseType: 'blob' });
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function getXmlForId(id: string) {
    try {
        const response = await springAxios.get('/definitions/'+id+'/download/xml',
            { responseType: 'blob' });
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function getJsonForId(id: string) {
    try {
        const response = await springAxios.get('/definitions/'+id+'/download/json',
            { responseType: 'blob' });
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export async function getDefinitionForId(id: string) {
    try {
        const response = await springAxios.get('/definitions/'+id+'/download/json',
            );
        return { success: true, message: response.data.message, data: response.data }
    } catch (error: any) {
        console.trace(error);
        return { success: false, message: error.message };
    }
}

export function real_id(input: string | number | undefined): number | undefined {
    // If it’s already a number (or undefined), just return it.
    if (input === undefined || typeof input === 'number') {
        return input
    }

    // Otherwise it’s a string: grab text before any colon
    const idx = input.indexOf(':')
    const raw = idx >= 0 ? input.slice(0, idx) : input

    // Convert to number. If that’s NaN, return undefined.
    const parsed = Number(raw)
    return Number.isNaN(parsed) ? undefined : parsed
}
export function real_ids(concept_ids: string[]) {
    // console.log(concept_ids);
    const prefixSet = new Set(concept_ids.map(real_id));
    return Array.from(prefixSet);
}
