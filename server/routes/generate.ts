import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl';



//one function for generating end downloading csv of data (table function to generat collection and directly add as collection)
router.get('/dataset', dbController.generateDataset)
router.get('/collection', dbController.generateCollectionSheet)


export default router;