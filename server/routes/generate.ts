import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl';


router.get('/dataset', dbController.generateDataset)
router.get('/collection', dbController.generateCollectionSheet)


export default router;