import axios from "axios";
import { BASE_URL } from "../Constants";
import { IGraphLocation } from "../types";

export const getPath = async (pathID: string) => {
  const response = await axios.get(`${BASE_URL}/graph/${pathID}`);
  const data = response.data;
  const path: IGraphLocation[] = data;
  return path;
};

export const createPath = async (
  originPoints: [number, number],
  destinationPoints: [number, number]
) => {
  const response = await axios.post(`${BASE_URL}/graph`, {
    entryPoints: originPoints,
    exitPoints: destinationPoints,
    exclusionPoints: [],
    intermediatePoints: [],
  });
  const data = response.data;
  return data.routeID;
};

export const checkRouteStatus = async (routeID: string) => {
  const response = await axios.get(
    `${BASE_URL}/graph/checkRouteStatus/${routeID}`
  );
  const data = response.data;
  return data.status;
};
