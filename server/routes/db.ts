import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl'

router.get('/children/:id', dbController.children )
router.get('/collections/attributeCount', dbController.getAttributeCount )
router.get('/concepts', dbController.getAllConcepts )
router.get('/cdmconcepts', dbController.getAllCdmConcepts )
router.get('/queryRelationships', dbController.getQueryRelationships )
router.get('/ontologies', dbController.getOntologies )
router.get('/queryAny', dbController.queryAny )
router.post('/queryRelationships', dbController.queryRelationships ) //body necessary


export default router;
