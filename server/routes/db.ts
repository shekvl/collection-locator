import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl'

router.get('/children/:id', dbController.children )
router.get('/collections/attributeCount', dbController.getAttributeCount )


export default router;
