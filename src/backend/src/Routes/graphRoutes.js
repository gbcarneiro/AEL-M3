import express from "express";
import graphController from "../Controller/graphController.js";

const { getGraph, createNode, getFinalPath } = graphController;

const router = express.Router();

// GET /api/graph
router.get("/", getFinalPath);
router.post("/", createNode);

export default router;