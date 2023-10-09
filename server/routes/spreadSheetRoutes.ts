import { Router } from "express";
const router = Router();

import * as spreadSheet from '../controllers/spreadSheetC';

router.get('/dataset', spreadSheet.downloadAttributes)
router.get('/collection', spreadSheet.downloadCollection)


export default router;