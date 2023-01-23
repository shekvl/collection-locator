
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


export const uploadFiles = upload.fields([
    { name: 'collections' },
    { name: 'attributes' }
])