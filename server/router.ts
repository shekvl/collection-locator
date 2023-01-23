import indexRoutes from './routes'
import generateRoutes from './routes/generate'
import dbRoutes from './routes/db'
import uploadeRoutes from './routes/upload'

export function setup(app: any) {
    app.use('/', indexRoutes)
    app.use('/generate', generateRoutes)
    app.use('/db', dbRoutes)
    app.use('/upload', uploadeRoutes)
}
