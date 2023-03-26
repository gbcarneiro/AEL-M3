package models.Scorer;

import models.vertex.IVertex;

import java.awt.geom.Point2D;


/*
* The haversine formula determines the great-circle distance between two points on a sphere given
* their longitudes and latitudes.
* */
public class Haversine implements IScorer {
    @Override
    public double computeCost(IVertex from, IVertex to) {
        double R = 6372.8; // Earth's Radius, in kilometers

        double fromLat = from.getPosition().getX();
        double fromLong = from.getPosition().getY();

        double toLat = to.getPosition().getX();
        double toLong = to.getPosition().getY();

        double dLat = Math.toRadians(toLat - fromLat);
        double dLon = Math.toRadians(toLong - fromLong);
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);

        double a = Math.pow(Math.sin(dLat / 2),2)
                + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    public double computeDistanceByPoint2D(Point2D point1, Point2D point2) {
        double R = 6372.8; // Earth's Radius, in kilometers

        double fromLat = point1.getX();
        double fromLong = point1.getY();

        double toLat = point2.getX();
        double toLong = point2.getY();

        double dLat = Math.toRadians(toLat - fromLat);
        double dLon = Math.toRadians(toLong - fromLong);
        double lat1 = Math.toRadians(fromLat);
        double lat2 = Math.toRadians(toLat);

        double a = Math.pow(Math.sin(dLat / 2),2)
                + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
