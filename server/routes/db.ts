import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl'

// router.get('/children/:id', dbController.children )
// router.get('/collections/attributeCount', dbController.getAttributeCount )
router.get('/concepts', dbController.getAllConcepts )
// router.get('/cdmconcepts', dbController.getAllCdmConcepts )
router.get('/relationshipsOfInterest', dbController.getRelationshipsOfInterest )
router.get('/vocabularies', dbController.getSupportedVocabularies )
router.get('/queryAny', dbController.queryAny )
router.get('/queryAll', dbController.queryAll )
router.post('/queryRelationships', dbController.queryRelationships ) //body necessary


export default router;
