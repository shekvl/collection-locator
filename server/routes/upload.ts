import { Router } from 'express'
const router = Router()

import { uploadFiles } from '../controllers/uploadCtrl'
import { parseCsv, insertRecords, deleteFiles, assertUniqueCollectionNames, assertCollectionReferencesProvided, assertFileSchema } from '../controllers/collectionUploadCtrl'

router.post(
    '/collection',
    uploadFiles,
    parseCsv,
    assertFileSchema,
    assertUniqueCollectionNames,
    assertCollectionReferencesProvided,
    insertRecords,
    deleteFiles
)

/**
 * Error handler
 * Through multer uploaded files are deleted
 */
router.use((err, req, res, next) => {

    if (err.name === "CSV_FILE_TYPES") {
        res.status(422).json({ message: err.message + ': Files in csv format expected' })
    } else if (err.name === "UNIQUE_NAME_VIOLATION") {
        res.status(422).json({ message: err.message })
    } else if (err.name === "UNKNOWN_REFERENCE_EXCEPTION") {
        res.status(422).json({ message: err.message })
    } else if (err.name === "FAST_CSV_PARSING_EXCEPTION") {
        res.status(422).json({ message: 'csv parser: ' + err.message })
    } else if (err.name === 'POSTGRES_EXCEPTION') {
        res.status(422).json({ message: 'postgres: ' + err.message })
    } else if (err.name === 'FILE_SCHEMA_VIOLATION') {
        res.status(422).json({ message: 'csv parser: ' + err.message })
    } else {
        res.status(500).json({ message: err.message })
    }

    deleteFiles(req, res, next)
})

export default router;