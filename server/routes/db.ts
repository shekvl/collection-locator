import { Router } from "express";
const router = Router();

import * as dbC from '../controllers/dbC'

router.get('/concepts', dbC.getAllConcepts )
router.get('/relationshipsOfInterest', dbC.getRelationshipsOfInterest )
router.get('/vocabularies', dbC.getSupportedVocabularies )

router.get('/queryAny', dbC.queryAny )
router.get('/queryAll', dbC.queryAll )
router.post('/queryRelationships', dbC.queryRelationships )


export default router;
