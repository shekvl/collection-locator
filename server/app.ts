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

if (process.env.NODE_ENV === "production") {

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
app.listen(port, () => {
    console.log('Server available at:', '\x1b[36m\b', `http://localhost:${port}/api`, '\x1b[0m')
    
    if (process.env.NODE_ENV === "production") {
        console.log('Client available at:', '\x1b[36m\b', `http://localhost:${port}`, '\x1b[0m')
    }
})
