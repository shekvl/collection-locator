import { Router } from "express";
const router = Router();

//download example csv file
router.get('/csv', (req, res) => res.send("csv!"))






export default router;