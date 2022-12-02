import env from 'dotenv'
env.config()

import express from 'express'

const app = express()
const port = 3000

import { pool } from './postgres'
import * as cdm from './cdm_queries'



// pool
//     .query(cdm.test)
//     .then((res) => console.log(res.rows[0]))
//     .catch((err) => console.error('Error executing query', err.stack))

app.get('/', (req, res) => res.send("Welcome to setting up Node.js project tutorial!"))

app.listen(port, () => console.log(`Application listening on port ${port}!`))