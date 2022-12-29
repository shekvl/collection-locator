import { Router } from 'express'
const router = Router()


//TODO check file upload content (size, format, content).. maybe csv parser enough
function fileFilter(req, file, callback) {
    if (file.mimetype !== 'text/csv') {
        const error = new Error("Wrong file type")
        error.name = "CSV_FILE_TYPES"
        return callback(error, false)
    }

    // if() csv.. TODO validate csv format, not just mimetype or maybe not necessary csv parser

    callback(null, true)
}

//TODO ERROR MESSAGES... !!!

import multer from 'multer'
const upload = multer({
    dest: 'uploads',
    fileFilter,
})
const uploadFiles = upload.fields([
    { name: 'collections' }, //TODO implement minCount on clientside: required
    { name: 'attributes' }
])

import { parseCsv, insertRecords, deleteFiles } from '../controllers/collectionUploadCtrl'


router.post('/collection', uploadFiles, parseCsv, insertRecords, deleteFiles)


router.use((err, req, res, next) => {

    if (err.name === "CSV_FILE_TYPES") {
        res.status(422).json({ err: 'Files in csv format expected' })
        return
    }
})

export default router;