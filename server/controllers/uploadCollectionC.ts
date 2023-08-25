import multer from 'multer'
import fs from 'fs'
import { parseFile } from 'fast-csv'
import * as tf from '../database/tableFunctions'
import * as schema from '../database/schemas'

/**
 * Filter by mimetype to prevent upload of non csv files
 * @param req Request object
 * @param file File to be uploaded
 * @param callback Callback passed by multer
 */
function assertCsvMimetype(req, file, callback) {
    //console.log(file);
    if (file.mimetype !== 'text/csv' && file.mimetype !== 'application/vnd.ms-excel') {
        const error = new Error("Wrong file type")
        error.name = "CSV_FILE_TYPES"
        return callback(error, false)
    }

    callback(null, true)
}

const upload = multer({
    dest: 'uploads',
    fileFilter: assertCsvMimetype,
})

/**
 * Multer Middleware to upload files. Accepted form-data field names are specified.
 */
export const uploadFilesCollections = upload.fields([
    { name: 'collections' },
    { name: 'attributes' }
])

const options = {
    headers: true,
    delimiter: ';',
    objectMode: true,
    ignoreEmpty: true,
    trim: true,
}

/**
 * Midleware to parse uploaded csv files.
 * @side_effect `Collections` and `attributes` are attached to req.
 */
export function parseCsvCollections(req, res, next) {
    req.parsed = {
        collections: {
            records: [],
            headers: [],
            perFileCount: [],
            total: 0
        }, attributes: {
            records: [],
            headers: [],
            perFileCount: [],
            total: 0
        }
    }

    const promises = []

    for (const file of req.files.collections) { //parse collections
        let promise = new Promise((resolve, reject) => {

            parseFile(file.path, options)
                .on('error', err => {
                    reject(err)
                })
                .on('headers', row => req.parsed.collections.headers.push(row))
                .on('data', row => req.parsed.collections.records.push(row))
                .on('end', (rowCount: number) => {
                    req.parsed.collections.perFileCount.push(rowCount)
                    req.parsed.collections.total += rowCount
                    resolve(null)
                })
        })

        promises.push(promise)
    }


    for (const file of req.files.attributes) { //parse attributes
        let promise = new Promise((resolve, reject) => {

            parseFile(file.path, options)
                .on('error', err => {
                    reject(err)
                })
                .on('headers', row => req.parsed.attributes.headers.push(row))
                .on('data', row => req.parsed.attributes.records.push(row))
                .on('end', (rowCount: number) => {
                    req.parsed.attributes.perFileCount.push(rowCount)
                    req.parsed.attributes.total += rowCount
                    resolve(null)
                })
        })

        promises.push(promise)
    }

    Promise.all(promises)
        .then(() => next())
        .catch(err => {
            err.name = 'FAST_CSV_PARSING_EXCEPTION'
            next(err)
        })
}

const compareArrays = (a: any[], b: any[]) => {
    return a.toString() === b.toString();
};


/**
 * Middleware to assert specific csv schema for collections and attributes. Collection or attribute files may match a schema, but may have been uploaded via an incorrect form-data field name.
 * @param req Request object containing parsed collection and attribute records
 */
export function assertContentSchema(req, res, next) {


    for (const header of req.parsed.collections.headers) {
        if (!compareArrays(header, schema.collection)) {
            const err = new Error('Files do not apply to collection/attribute schema')
            err.name = 'FILE_SCHEMA_VIOLATION'
            return next(err)
        }
    }
    for (const header of req.parsed.attributes.headers) {
        if (!compareArrays(header, schema.attribute)) {
            const err = new Error('Files do not apply to collection/attribute schema')
            err.name = 'FILE_SCHEMA_VIOLATION'
            return next(err)
        }
    }

    next()
}

/**
 * Middleware to assert that all collections uploaded together have a unique name
 * @param req Request object containing parsed collection and attribute records
 */
export function assertDistinctCollectionNames(req, res, next) {
    const collectionNames = req.parsed.collections.records.map((c) => c.name)

    if (collectionNames.length !== new Set(collectionNames).size) {
        const err = new Error('Distinct collection names required')
        err.name = 'UNIQUE_NAME_VIOLATION'
        return next(err)
    }

    next()
}

/**
 * Middleware to assert that all attribute records reference a collection name of the collections uploaded together
 * @param req Request object containing parsed collection and attribute records
 */
export function assertCollectionReferencesOkay(req, res, next) {
    const collectionNames: [] = req.parsed.collections.records.map((c) => c.name)
    const collectionReferences: [] = req.parsed.attributes.records.map((c) => c.collection_name)

    const unknownReference = collectionReferences.filter((ref) => !collectionNames.includes(ref))

    if (unknownReference.length != 0) {
        const err = new Error('Attributes must refer to collection given in uploades collection files')
        err.name = 'UNKNOWN_REFERENCE_EXCEPTION'
        return next(err)
    }

    next()
}


/**
 * Middleware to insert parsed collection and attribute records into the database. It sends a response with detailed information about the uploaded collections.
 * @param req Request object containing parsed collection and attribute records
 */
export function insertRecordsCollections(req, res, next) {

    tf.collection.toDb(req.parsed.collections.records, req.parsed.attributes.records)
        .then(async (collection_ids: any) => {
            const collectionDetails: string[] = []
            const attributeCounts = await tf.collection.attributeCount(collection_ids)

            for (const i of attributeCounts.rows)
                collectionDetails.push(`${i.collection_name} (id: ${i.collection_id}, attribute_count: ${i.attribute_count})`)

            res.json({
                parsed: req.parsed,
                message: `${req.parsed.collections.total} New Collections: ${collectionDetails.join(' | ')}`
            })

            next()
        })
        .catch(err => {
            err.name = 'POSTGRES_EXCEPTION'
            next(err)
        })
    // .finally(next())
}

/**
 * Delete files uploaded by multer
 * @param req Request object containing uploaded files
 */
export async function deleteFilesCollections(req, res, next) {

    const fields: object[][] = Object.values(req.files)

    for await (const field of fields) {
        field.forEach((file: any) => {
            fs.unlink(file.path, (err) => {
                if (err) {
                    next(err)
                } else {
                    // console.log("File removed:", file.path)
                }
            })
        })
    }
}
