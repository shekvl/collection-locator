import { Router } from "express";
const router = Router();

import * as dbC from '../controllers/dbC'
import {queryCollectionsByQuality} from "../controllers/dbC";

router.get('/concepts', dbC.getAllConcepts )
router.get('/relationshipsOfInterest', dbC.getRelationshipsOfInterest )
router.get('/vocabularies', dbC.getSupportedVocabularies )
router.get('/qualityCharacteristics', dbC.getQualityCharacteristics )

router.get('/queryAny', dbC.queryAny )
router.get('/queryAll', dbC.queryAll )
router.get('/queryCollectionsByQuality', dbC.queryCollectionsByQuality )
router.post('/queryRelationships', dbC.queryRelationships )


export default router;
