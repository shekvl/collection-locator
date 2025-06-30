import { Router } from "express";
const router = Router();

import indexR from './routes'
import spreadSheetR from './routes/spreadSheetRoutes'
import dbR from './routes/dbRoutes'
import uploadeR from './routes/uploadRoutes'


router.use('/', indexR)
router.use('/db', dbR)
router.use('/random', spreadSheetR)
router.use('/upload', uploadeR)

// default error handler
router.use((err, req, res, next) => {
    if (process.env.NODE_ENV == 'production') {
        res.sendStatus(500)
    } else {
        res.status(500).json({ message: err.message })
    }
})


export default router;
