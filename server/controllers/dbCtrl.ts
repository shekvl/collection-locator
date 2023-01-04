
import { omop, generate, collection, concepts, query } from '../tableFunctions'
import { gaussianRandom, uniformRandomInt } from '../random';
const csv = require("fast-csv");


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


export const generateDataset = (req, res) => {
    const size: number = Number.parseInt(req.query.size) || 10
    const filename: string = req.query.filename || 'dataset.csv'
    const collection_name: string = req.query.collection_name

    generate.dataset(size, collection_name)
        .then((table) => {
            res.type("csv")
            res.header("content-disposition", "attachment; filename=\"" + filename + "\"")

            const csvStream = csv.format({
                headers: true,
                delimiter: ';',
            });
            csvStream.pipe(res);

            const headers = table.fields.map(field => field.name)
            csvStream.write(headers)

            const rows = table.rows
            rows.forEach(row => csvStream.write(row))

            csvStream.end()
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //general error message?
        })
}

export const generateCollectionSheet = (req, res) => {
    const filename: string = req.query.filename || 'collection.csv'
    const collection_name: string = req.query.collection_name || filename
    res.type("csv")
    res.header("content-disposition", "attachment; filename=\"" + filename + "\"")

    const csvStream = csv.format({
        headers: true,
        delimiter: ';',
    });
    csvStream.pipe(res);

    const headers = ['name',
        'number_of_records',
        'completeness',
        'accuracy',
        'reliability',
        'timeliness',
        'consistancy']
    csvStream.write(headers)

    const fields = [collection_name,
        uniformRandomInt(20, 2500),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1)]
    csvStream.write(fields)

    csvStream.end()
}


export const getAttributeCount = (req, res) => {
    collection.getAttributeCount(req.query.collection_ids)
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //TODO
        })
}


export const getAllConcepts = (req, res) => {
    concepts.all()
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //TODO
        })
}

export const getAllCdmConcepts = (req, res) => {
    concepts.allFromCdm()
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //TODO
        })
}

export const getQueryRelationships = (req, res) => {
    query.getQueryRelationships(req.query.group, req.query.vocabulary_id)
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //TODO
        })
}

export const getOntologies = (req, res) => {
    query.getOntologies()
        .then((table) => {
            res.send(table.rows);
        })
        .catch((err) => {
            console.error('Error executing query', err.stack) //TODO
        })
}
