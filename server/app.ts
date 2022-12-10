import env from 'dotenv'
env.config()

import express from 'express'

const app = express()
const port = 3000

import indexRouter from './routes'
import mockDataRoutes from './routes/mockData'
import dbRouter from './routes/db'

app.use('/', indexRouter)
app.use('/', mockDataRoutes)
app.use('/', dbRouter)

app.listen(port, () => console.log(`Application listening on port ${port}!`))