const express = require('express')
const queryService = require('../../services/queryService')

const router = express.Router()

//Get data (collections and definitions)
router.post('/data', async (req, res) => {
    try{
        const data = req.body
        console.log(data)
        const datasets = await queryService.getData(data)
        if (datasets.length > 0) {
            res.send(datasets)
        }
        else throw new Error('No data was found for your search!')
    }
    catch(e) {
        console.error(e)
        res.status(404).json({error: e.message})
    }
})

//Save data (collections and definitions)
router.post('/saveData', async (req, res) => {
    try {
        const messageFromSave = await queryService.saveData()
        // const data = req.anonymizations
        console.log(res)
        res.status(200).json({message: messageFromSave });
        // const datasets = await queryService.saveData(data)
    }
    catch(e) {
        console.error(e)
        res.status(404).json({error: e.message})
    }
})

router.get('/concept', async (req, res) => {
    try {
        const concept = await queryService.getConcept(req.query.concept)
        if (concept.length > 0) {
            res.send(concept)
        }
        else throw new Error('concept does not exist!')
    }
    catch (e) {
        console.error('Controller: concept does not exist!')
        res.status(404).json({error: e.message})
        //throw 'Controller: concept does not exist!'
    }
})

router.get('/concepts', async (req,res) => {
    try {
        const concepts = await queryService.getConcpets()
        console.error(concepts);
        if (concepts.length > 0) {
            res.send(concepts)
        }
        else throw new Error('something went wrong when fetching concepts')
    }
    catch (e) {
        console.error('Controller: something went wrong when fetching concepts')
        res.status(404).json({error: e.message})
        //throw 'Controller: something went wrong when fetching concepts'
    }
})

module.exports = router
