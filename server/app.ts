import env from 'dotenv'
env.config()

import express from 'express'

const app = express()
const port = 3001
app.use(express.json());

import indexRoutes from './routes'
import generateRoutes from './routes/generate'
import dbRoutes from './routes/db'
import uploadeRoutes from './routes/upload'

app.use('/', indexRoutes)
app.use('/generate', generateRoutes)
app.use('/db', dbRoutes)
app.use('/upload', uploadeRoutes)

app.listen(port, () => console.log(`Application listening on port ${port}!`))