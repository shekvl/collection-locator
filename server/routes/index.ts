import { Router } from "express";
const router = Router();

router.get('/', (req, res) => res.send("The Collection Locator Server is up and running..."))

router.use((err, req, res, next) => {
    res.status(500).json({ message: err.message })
})

export default router;