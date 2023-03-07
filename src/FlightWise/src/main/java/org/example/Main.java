package org.example;
import models.Graph.Graph;
import models.edge.CoordinateEdge;
import models.vertex.CoordinateVertex;
import org.neo4j.driver.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import utils.Points;


public class Main {

    // Inserts into the Neo4J database, all coordinate connections
    public static void createCordinateEdgesAsync(ArrayList<CoordinateVertex> coordinates, Session session){
        String cypherQuery = "MATCH (n:Coordinate {index: $ind1})\n" +
                "MATCH (c:Coordinate {index: $ind2})\n" +
                "CREATE (n)-[r:Connection {distance: $dist, heighRange: $heig}]->(c)";

        for (int i = 0; i < coordinates.size(); i++) {
            CoordinateVertex coordinateVertex = coordinates.get(i);
            try (Transaction tx = session.beginTransaction()) {
                for (int j = 0; j < coordinateVertex.getEdges().size(); j++){
                    CoordinateEdge coordinateEdge = coordinateVertex.getEdges().get(j);
                    tx.run(cypherQuery, Map.of("ind1", coordinateVertex.getIndex(), "ind2", coordinateEdge.targetVertex.getIndex(), "dist", coordinateEdge.distance, "heig", coordinateEdge.heightRange));
                }
                tx.commit();
            }
        }
    }

    // Inserts into the Neo4J database, the final optimal path as a new connection type
    public static void createFinalPathEdges(ArrayList<CoordinateVertex> coordinates, Session session){
        String cypherQuery = "MATCH (n:Coordinate {index: $ind1})\n" +
                "MATCH (c:Coordinate {index: $ind2})\n" +
                "CREATE (n)-[r:Route]->(c)";
        try (Transaction tx = session.beginTransaction()) {
            for (int i = 0; i < coordinates.size() - 1; i++) {
                CoordinateVertex coordinateVertex = coordinates.get(i);
                CoordinateVertex nextVertex = coordinates.get(i+1);
                tx.run(cypherQuery, Map.of("ind1", coordinateVertex.getIndex(), "ind2", nextVertex.getIndex()));
            }
            tx.commit();
        }
    }

    // Updates the Neo4J database, changing the final path vertexes type
    public static void createFinalPathVertexes(ArrayList<CoordinateVertex> coordinates, Session session){
        String cypherQuery = "MATCH (n:Coordinate {index: $ind})\n" +
                "REMOVE n:Coordinate\n" +
                "SET n:NewCoodinate";
        try (Transaction tx = session.beginTransaction()) {
            for (int i = 0; i < coordinates.size() - 1; i++) {
                CoordinateVertex coordinateVertex = coordinates.get(i);
                tx.run(cypherQuery, Map.of("ind", coordinateVertex.getIndex()));
            }
            tx.commit();
        }
    }

    // Inserts into the Neo4J database all the coordinate vertexes
    public static void createCoordinateNodesAsync(ArrayList<CoordinateVertex> coordinates, Session session) {
        String cypherQuery = "CREATE (c:Coordinate {index: $ind, latitude: $lat, longitude: $long, averageHeight: $avgHeight})";

        try (Transaction tx = session.beginTransaction()) {
            for (int i = 0; i < coordinates.size(); i++) {
                CoordinateVertex coordinateVertex = coordinates.get(i);
                Point2D position = coordinateVertex.getPosition();


                tx.run(cypherQuery, Map.of("ind", coordinateVertex.getIndex(), "lat", position.getX(), "long", position.getY(), "avgHeight", coordinateVertex.averageHeight));
            }

            tx.commit();
        }
    }

    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("neo4j+s://ea367293.databases.neo4j.io",
                AuthTokens.basic("neo4j","74OQ-dnMkAEveBhfOKbD1BBTTnvQ4ubORS86TwvT8mo"));

        //region creating cordinate nodes
        Points points = new Points();
        double[][] coordinates = points.Coordinates("dted/rio", -43.4082, -22.1780, 5, 4, 0.0013, 0.0011);


//        ArrayList<Point2D> positionsArray = new ArrayList<>(Arrays.asList(
//                new Point2D.Double(50.0, 30.0),
//                new Point2D.Double(49.995, 30.0),
//                new Point2D.Double(49.990, 30.0),
//                new Point2D.Double(49.985, 30.0),
//                new Point2D.Double(49.980, 30.0),
//                new Point2D.Double(49.975, 30.0),
//                new Point2D.Double(50.0, 29.995),
//                new Point2D.Double(49.995, 29.995),
//                new Point2D.Double(49.990, 29.995),
//                new Point2D.Double(49.985, 29.995),
//                new Point2D.Double(49.980, 29.995),
//                new Point2D.Double(49.975, 29.990),
//                new Point2D.Double(50.0, 29.990),
//                new Point2D.Double(49.995, 29.990),
//                new Point2D.Double(49.990, 29.990),
//                new Point2D.Double(49.985, 29.990),
//                new Point2D.Double(49.980, 29.990),
//                new Point2D.Double(49.975, 29.990)
//        ));
        //endregion


        // Initializes a new Graph()
        Graph newGraph = new Graph();


        // Adds all positions to the new Graph
        for (int i = 0; i < coordinates.length; i++){
            Point2D currentPoint = new Point2D.Double(coordinates[i][1],  coordinates[i][0]);
            CoordinateVertex newCoordinateVertex = new CoordinateVertex(currentPoint, coordinates[i][2]);
            newGraph.addVertex(newCoordinateVertex);
        }


        // Create all vertex edges based on distance
        newGraph.addVertexEdgesByDistance(0.200);

        // Calculates the optimal path between two nodes(vertex)
        newGraph.ASearch(0, 17);

        // Returns the generated optimal path as an ArrayList;
        ArrayList<CoordinateVertex> newList = newGraph.findPath(newGraph.getVertexes().get(17));

        // Send the local Graph structure to neo4J
        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            createCoordinateNodesAsync(newGraph.getVertexes(), session);

            createCordinateEdgesAsync(newGraph.getVertexes(), session);

            createFinalPathEdges(newList, session);

            createFinalPathVertexes(newList, session);
        }

        // Ends the Neo4J session
        driver.close();
    }
}