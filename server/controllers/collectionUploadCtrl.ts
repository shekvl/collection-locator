import fs from 'fs'
import { parseFile } from 'fast-csv'
import { attribute, collection, transaction } from '../tableFunctions'

const options = {
    headers: true,
    delimiter: ';',
    objectMode: true,
    ignoreEmpty: true,
    trim: true,
}

/**
 * Parse uploaded csv files
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


    //TODO check whether collection names unique
    //TODO check whether attributes only refer to given collection names

    Promise.all(promises)
        .then(() => next())
        .catch(err => console.log(err))
}

/**
 * Insert parsed data into db
 */
export function insertRecords(req, res, next) {

    transaction.uploadCollection(req.parsed.collections, req.parsed.attributes)
        .then((dict) => {
            res.send(`uploaded (collections: ${req.parsed.collectionCount}, attributes: ${req.parsed.attributeCount}, dict: ${JSON.stringify(dict)})`)
        })
        .catch()
        .finally(next())
}


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