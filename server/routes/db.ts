import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl'

router.get('/children/:id', dbController.children )
router.get('/collections/attributeCount', dbController.getAttributeCount )
router.get('/concepts', dbController.getAllConcepts )
router.get('/cdmconcepts', dbController.getAllCdmConcepts )


export default router;
