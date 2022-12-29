import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbCtrl'

router.get('/children/:id', dbController.children )


export default router;
