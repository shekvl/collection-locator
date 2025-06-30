import * as tf from '../database/tableFunctions'
import { gaussianRandom, uniformRandomInt } from '../random';
const csv = require("fast-csv");
import * as schema from '../database/schemas'

/**
 * Create randomly generated attribute records and start download
 * @param req Request object containing the `number of attributes` to be generated, the optional default `filename` of the download and the also optional `collection name` of the referenced collection
 * @param res Response object to which the created file content is piped to
 */
export const downloadAttributes = (req, res) => {
    const size: number = Number.parseInt(req.query.size) || 10
    const filename: string = req.query.filename || 'dataset.csv'
    const collection_name: string = req.query.collection_name

    tf.generate.dataset(size, collection_name)
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
}

/**
 * Create a randomly generated collection and start download
 * @param req Request object containing optional default `filename` of the download and the `collection name` of the generated collection
 * @param res Response object to which the created file content is piped to
 */
export const downloadCollection = (req, res) => {
    const filename: string = req.query.filename || 'collection.csv'
    const collection_name: string = req.query.collection_name || filename
    res.type("csv")
    res.header("content-disposition", "attachment; filename=\"" + filename + "\"")

    const csvStream = csv.format({
        headers: true,
        delimiter: ';',
    });
    csvStream.pipe(res);

    const headers = schema.collection
    csvStream.write(headers)

    const fields = [collection_name,
        uniformRandomInt(20, 2500),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1),
        gaussianRandom(0.7, 1)]
    csvStream.write(fields)

    csvStream.end()
}