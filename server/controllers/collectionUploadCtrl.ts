import fs from 'fs'
import { parseFile } from 'fast-csv'
import { transaction } from '../tableFunctions'

const options = {
    headers: true,
    delimiter: ';',
    objectMode: true,
    ignoreEmpty: true,
    trim: true,
}

/**
 * Parse uploaded csv files. Collections, attributes, collection count and attribute count get attached to req.
 */
export function parseCsv(req, res, next) {
    req.parsed = {}
    req.parsed.collections = []
    req.parsed.attributes = []
    req.parsed.collectionCount = 0
    req.parsed.attributeCount = 0
    const promises = []

    for (const file of req.files.collections) { //parse collections
        let promise = new Promise((resolve, reject) => {

            parseFile(file.path, options)
                .on('error', err => {
                    reject(err)
                })
                .on('data', row => req.parsed.collections.push(row))
                .on('end', (rowCount: number) => {
                    req.parsed.collectionCount += rowCount
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
                .on('data', row => req.parsed.attributes.push(row))
                .on('end', (rowCount: number) => {
                    req.parsed.attributeCount += rowCount
                    resolve(null)
                })
        })

        promises.push(promise)
    }

    Promise.all(promises)
        .then(() => next())
        .catch(err => {
            console.log(err.name)
            err.name = 'FAST_CSV_PARSING_EXCEPTION'
            console.log(err.name)
            next(err)
        })
}


export function assertUniqueCollectionNames(req, res, next) {
    const collectionNames = req.parsed.collections.map((c) => c.name)

    if (collectionNames.length !== new Set(collectionNames).size) {
        const err = new Error('Distinct collection names required')
        err.name = 'UNIQUE_NAME_VIOLATION'
        next(err)
    } else {
        next()
    }
}

/**
 * Assert all attributes reference collection name of upload file
 */
export function assertCollectionReferencesProvided(req, res, next) {
    const collectionNames: [] = req.parsed.collections.map((c) => c.name)
    const collectionReferences: [] = req.parsed.attributes.map((c) => c.collection_name)

    const unknownReference = collectionReferences.filter((ref) => !collectionNames.includes(ref))

    if (unknownReference.length != 0) {
        const err = new Error('Attributes must refer to collection given in uploades collection files')
        err.name = 'UNKNOWN_REFERENCE_EXCEPTION'
        next(err)
    } else {
        next()
    }
}


/**
 * Insert parsed data into db
 */
export function insertRecords(req, res, next) {

    transaction.uploadCollection(req.parsed.collections, req.parsed.attributes)
        .then((dict) => {
            res.send(`uploaded (collections: ${req.parsed.collectionCount}, attributes: ${req.parsed.attributeCount}, dict: ${JSON.stringify(dict)})`)
        })
        .catch(err => {
            err.name = 'POSTGRES_EXCEPTION'
            next(err)
        })
        .finally(next())
}

/**
 * Delete all files uploaded by multer
 */
export async function deleteFiles(req, res, next) {

    const fields: object[][] = Object.values(req.files)

    for await (const field of fields) {
        field.forEach((file: any) => {
            fs.unlink(file.path, (err) => {
                if (err) {
                    console.log(err)
                } else {
                    console.log("File removed:", file.path)
                }
            })
        })
    }
}
