import fs from 'fs'
import { parseFile } from 'fast-csv'
import { collection, transaction } from '../tableFunctions'

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
    req.parsed = {
        collections: {
            records: [],
            headers: [],
            perFileCount:[],
            total: 0
        }, attributes: {
            records: [],
            headers: [],
            perFileCount:[],
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
 * Assert that all collection and attributes apply to csv schema (resp. uploaded via correct form-data name)
 */
export function assertFileSchema(req, res, next) {
    const collection = [
        'name',
        'number_of_records',
        'completeness',
        'accuracy',
        'reliability',
        'timeliness',
        'consistancy'
    ]

    const attribute = [
        'collection_name',
        'attribute_name',
        'code',
        'vocabulary_id',
        'completeness',
        'accuracy',
        'reliability',
        'timeliness',
        'consistancy'
    ]

    for (const header of req.parsed.collections.headers) {
        if (!compareArrays(header, collection)) {
            const err = new Error('Files do not apply to collection/attribute schema')
            err.name = 'FILE_SCHEMA_VIOLATION'
            next(err)
        }
    }
    for (const header of req.parsed.attributes.headers) {
        if (!compareArrays(header, attribute)) {
            const err = new Error('Files do not apply to collection/attribute schema')
            err.name = 'FILE_SCHEMA_VIOLATION'
            next(err)
        }
    }

    next()
}

export function assertUniqueCollectionNames(req, res, next) {
    const collectionNames = req.parsed.collections.records.map((c) => c.name)

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
    const collectionNames: [] = req.parsed.collections.records.map((c) => c.name)
    const collectionReferences: [] = req.parsed.attributes.records.map((c) => c.collection_name)

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

    transaction.uploadCollection(req.parsed.collections.records, req.parsed.attributes.records)
        .then(async (collection_ids: any) => {
            const collectionStrings: string[] = []

            const attributeCounts = await collection.getAttributeCount(collection_ids)

            for (const i of attributeCounts.rows)
                collectionStrings.push(`${i.collection_name} (id: ${i.collection_id}, attribute_count: ${i.attribute_count})`)

            res.json({
                parsed: req.parsed,
                message: `${req.parsed.collections.total} New Collections: ${collectionStrings.join(' | ')}`
            })
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
