//HERE???!

// functionallity somwhere in a route TODO
import { omop } from '../tableFunctions'


export const children = (req, res) => {

    //37069099
    omop.fetchChildren(req.params.id)
        .then((table) => {
            res.send(table.rows[0]); //define return value; empty set => undefined
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //general error message?
        })
}
