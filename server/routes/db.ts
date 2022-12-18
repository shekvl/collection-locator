import { Router } from "express";
const router = Router();

import * as dbController from '../controllers/dbController'

router.get('/children/:id', dbController.children )


export default router;