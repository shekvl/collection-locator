const express = require('express')
const cors = require('cors')
const bodyParser = require('body-parser')

const app = express()

//Middleware
app.use(bodyParser.json())
app.use(cors())

const datasets = require('./routes/api/queryController')
app.use('/api', datasets)

const port = process.env.PORT || 5001

app.listen(port, () => console.log(`Server started on port ${port}`))