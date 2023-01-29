import env from 'dotenv'
env.config()

import express from 'express'
import cors from 'cors'
import compression from 'compression'
import helmet from 'helmet'

const app = express()
app.use(express.json())
app.use(cors())

if (app.get("env") === "production") {

    app.use(helmet({
        contentSecurityPolicy: false
    }))

    app.use(compression())

    //serve static public folder with client
    //https://expressjs.com/en/starter/static-files.html
    // app.use(history());
    app.use(express.static("public"));
}

import { setup } from './router'
setup(app)

const port = process.env.SERVER_PORT
app.listen(port, () => console.log(`Application listening on port ${port}!`))