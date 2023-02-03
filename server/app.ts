import env from 'dotenv'
env.config()

import express from 'express'
import cors from 'cors'
import compression from 'compression'
import helmet from 'helmet'
import path from 'path'

const app = express()
app.use(express.json())
app.use(cors())

if (app.get("env") === "production") {

    app.use(helmet({
        contentSecurityPolicy: false
    }))

    app.use(compression())

    //serve static public folder with client (https://expressjs.com/en/starter/static-files.html)
    app.use('/', express.static(path.join(__dirname, 'public/client')));
}

import apiRoutes from './router'
app.use('/api', apiRoutes)

const port = process.env.SERVER_PORT
app.listen(port, () => console.log(`Application listening on port ${port}!
Open http://localhost:3000`))