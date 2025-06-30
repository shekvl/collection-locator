// import axios from 'axios'
//
// const port = process.env.SERVER_PORT || 5001
// axios.defaults.baseURL = `http://localhost:${port}`
//
// export default axios


// src/axios.ts
import axios from 'axios'

// Node.js backend (port 5001)
export const nodeAxios = axios.create({
    baseURL: `http://localhost:${process.env.NODE_SERVER_PORT || 5001}`
});

// Spring Boot backend (port 8084)
export const springAxios = axios.create({
    baseURL: `http://localhost:${process.env.SPRING_SERVER_PORT || 8084}`
});

export const node2Axios = axios.create({
    baseURL: `http://localhost:${process.env.SPRING_SERVER_PORT || 5002}`
});

// default export for existing node-based calls
export default nodeAxios;
