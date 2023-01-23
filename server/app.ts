import env from 'dotenv'
env.config()

import express from 'express'
import cors from 'cors'

const app = express()
app.use(express.json());
app.use(cors())

import {setup} from './router'
setup(app)

const port = process.env.SERVER_PORT
app.listen(port, () => console.log(`Application listening on port ${port}!`))