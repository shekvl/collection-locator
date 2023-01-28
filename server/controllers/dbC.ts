
import * as tf from '../database/tableFunctions'

/**
 * Get all cdm concept ids that are referenced by annotations stored in the collection locator
 */
export const getAllConcepts = (req, res, next) => {
    tf.locator.allConcepts()
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            next(err)
        })
}

/**
 * Get relationships belonging to a group of the relationships_of_interest table
 * @param req Request body containing the `group` and the `vocabulary id`
 */
export const getRelationshipsOfInterest = (req, res, next) => {
    tf.locator.getRelationshipsOfInterest(req.query.group, req.query.vocabulary_id)
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            next(err)
        })
}

/**
 * Get all cdm vocabularies currently supported by the collection locator
 */
export const getSupportedVocabularies = (req, res, next) => {
    tf.locator.getSupportedVocabularies()
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            next(err)
        })
}

/**
 * Get all collections that contain `ANY` concept of the given concept id list extended by mappable and descendent concepts.
 * @req Request object containing the `concept ids`
 */
export const queryAny = (req, res, next) => {
    tf.query.any(req.query.concept_ids)
        .then((result) => {
            const collection_ids = Array.from(new Set(result.rows?.map((r) => r.id)))
            const collections = result.rows
            tf.query.attributes(collection_ids)
                .then((result) => {
                    const attributes = result.rows
                    res.send({ collections, attributes });
                })
        })
        .catch((err) => {
            next(err)
        })
}

/**
 * Get all collections that contain `ALL` of the given concept ids or respectively a mappable or descendent concept instead of a given concept and their attributes.
 * @param Request object containing the `concept ids`
 */
export const queryAll = (req, res, next) => {
    tf.query.all(req.query.concept_ids)
        .then((result) => {
            const collection_ids = Array.from(new Set(result.rows?.map((r) => r.id)))
            const collections = result.rows
            tf.query.attributes(collection_ids)
                .then((result) => {
                    const attributes = result.rows
                    res.send({ collections, attributes });
                })
        })
        .catch((err) => {
            next(err)
        })
}

/**
 * Get concept ids of the given vocabulary that are associated to ALL relationships with ANY of the given values for each relationship.
 * @req Request object containing the `vocabulary id` and the `relationship object` (from body)
 */
export const queryRelationships = (req, res, next) => {
    tf.query.relationships(req.body.vocabulary_id, req.body.relationships)
        .then((result) => {
            res.send(result.rows.map((r) => r.concept_id));
        })
        .catch((err) => {
            next(err)
        })
}
