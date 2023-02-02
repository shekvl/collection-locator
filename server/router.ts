import indexR from './routes'
import spreadSheetR from './routes/spreadSheet'
import dbR from './routes/db'
import uploadeR from './routes/upload'

export function setup(app: any) {
    app.use('/', indexR)
    app.use('/db', dbR)
    app.use('/random', spreadSheetR)
    app.use('/upload', uploadeR)

    // default error handler
    app.use((err, req, res, next) => {
        if (process.env.NODE_ENV == 'production') {
            res.sendStatus(500)
        } else {
            res.status(500).json({ message: err.message })
        }
    })
}
