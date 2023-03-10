import express from "express";
import graphRoutes from "./Routes/graphRoutes.js";
import cors from "cors";

const app = express();
app.use(cors());

app.use(express.json());
app.use("/graph", graphRoutes);

app.listen(4000, () => {
    console.log('listening on port 4000');
});
