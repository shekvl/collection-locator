import { Router } from 'express'
const router = Router()


function assertCsvMimetype(req, file, callback) {
    if (file.mimetype !== 'text/csv') {
        const error = new Error("Wrong file type")
        error.name = "CSV_FILE_TYPES"
        return callback(error, false)
    }

    callback(null, true)
}

import multer from 'multer'
const upload = multer({
    dest: 'uploads',
    fileFilter: assertCsvMimetype,
})
const uploadFiles = upload.fields([
    { name: 'collections' }, //TODO implement minCount on clientside: required
    { name: 'attributes' }
])

import { parseCsv, insertRecords, deleteFiles, assertUniqueCollectionNames, assertCollectionReferencesProvided } from '../controllers/collectionUploadCtrl'


router.post('/collection',
    uploadFiles,
    parseCsv,
    assertUniqueCollectionNames,
    assertCollectionReferencesProvided,
    insertRecords,
    deleteFiles)


/**
 * Exception Middleware
 * Through multer uploaded files are deleted
 */
router.use((err, req, res, next) => {

    if (err.name === "CSV_FILE_TYPES") {
        res.status(422).json({ err: err.message + ': Files in csv format expected' })
    } else if (err.name === "UNIQUE_NAME_VIOLATION") {
        res.status(422).json({ err: err.message })
    } else if (err.name === "UNKNOWN_REFERENCE_EXCEPTION") {
        res.status(422).json({ err: err.message })
    } else if (err.name === "FAST_CSV_PARSING_EXCEPTION") {
        res.status(422).json({ err: 'csv parser: ' + err.message })
    } else if (err.name === 'POSTGRES_EXCEPTION') {
        res.status(422).json({ err: 'postgres: ' + err.message })
    } else {
        res.status(500).json({ err: err.message })
    }

    deleteFiles(req, res, next)
})

export default router;