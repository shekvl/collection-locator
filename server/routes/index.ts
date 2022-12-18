import { Router } from "express";
const router = Router();

router.get('/', (req, res) => res.send("Welcome to setting up Node.js project tutorial!"))


export default router;