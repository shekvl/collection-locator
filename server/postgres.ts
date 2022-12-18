import { Pool } from 'pg'

export const pool = new Pool({
    host: process.env.PG_HOST,
    user: process.env.PG_USER,
    port: process.env.PG_PORT,
    database: process.env.PG_DATABASE,
    password: process.env.PG_PASSWORD,
})

// const query = {
//     text: `INSERT INTO users(name, email) VALUES($1, $2)`,
//     values: ['brianc', 'brian.m.carlson@gmail.com'],
// }

// pool
//     .query('SELECT * from $1', ['cdm.test'])
//     .then((res) => console.log(res.rows[0].name))
//     .catch((err) => console.error('Error executing query', err.stack))

// pool.end().then(() => console.log('pool has ended')) //TODO execute on exiting server