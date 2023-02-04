import axios from 'axios'

const port = process.env.SERVER_PORT || 5001
axios.defaults.baseURL = `http://localhost:${port}`

export default axios