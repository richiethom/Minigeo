package eu.jacquet80.minigeo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Segment of a linear shape, such as a road.
 *
 * @author Christophe Jacquet
 */
@Builder
@Data
@ToString(of = {"pointA", "pointB"})
public class Segment {
    private static final double EARTH_RADIUS = Point.a;
    private static final Stroke BASIC_STROKE = new BasicStroke();

    private final Point pointA;
    private final Point pointB;
    private final Color color;
    @Builder.Default
    private final Stroke stroke = BASIC_STROKE;

    public double bearing() {
        double lat1Rad = Math.toRadians(pointA.getLatitude());
        double lon1Rad = Math.toRadians(pointA.getLongitude());
        double lat2Rad = Math.toRadians(pointB.getLatitude());
        double lon2Rad = Math.toRadians(pointB.getLongitude());

        double deltaLon = lon2Rad - lon1Rad;

        double y = Math.sin(deltaLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon);

        double bearingRad = Math.atan2(y, x);
        double bearingDeg = Math.toDegrees(bearingRad);

        // Convert bearing to range [0, 360)
        return (bearingDeg + 360) % 360;
    }

    // ... (Same as before)

    // Check if a point is within a specified distance of a line segment
    public double distanceFromLine(Point point3) {
        double min = pointA.distanceTo(point3);
        double b = pointB.distanceTo(point3);
        //double b1 = distanceFromSegment(point3);
        return Math.min(min, b);
    }

    private double distanceFromSegment(Point pointC) {
        // Convert latitude and longitude to Cartesian coordinates
        double[] a_ = toCartesian(pointA);
        double[] b_ = toCartesian(pointB);
        double[] c_ = toCartesian(pointC);

        double[] G = vectorProduct(a_, b_);
        double[] F = vectorProduct(c_, G);
        double[] t = vectorProduct(G, F);

        double[] doubles = fromCartsian(multiplyByScalar(normalize(t), EARTH_RADIUS));
        final Point nearestPointOnSegment = new Point(doubles[0], doubles[1]);
        return pointC.distanceTo(nearestPointOnSegment);
    }

    private static double[] fromCartsian(double[] coord){
        double[] result = new double[2];
        result[0] = Math.toDegrees(Math.asin(coord[2] / EARTH_RADIUS));
        result[1] = Math.toDegrees(Math.atan2(coord[1], coord[0]));

        return result;
    }

    private static double[] normalize(double[] t) {
        double length = Math.sqrt((t[0] * t[0]) + (t[1] * t[1]) + (t[2] * t[2]));
        double[] result = new double[3];
        result[0] = t[0]/length;
        result[1] = t[1]/length;
        result[2] = t[2]/length;
        return result;
    }

    private static double[] multiplyByScalar(double[] normalize, double k) {
        double[] result = new double[3];
        result[0] = normalize[0]*k;
        result[1] = normalize[1]*k;
        result[2] = normalize[2]*k;
        return result;
    }

    private double[] toCartesian(Point p) {
        double lat1 = p.getLatitude();
        double lon1 = p.getLongitude();
        double x1 = EARTH_RADIUS * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lon1));
        double y1 = EARTH_RADIUS * Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lon1));
        double z1 = EARTH_RADIUS * Math.sin(Math.toRadians(lat1));

        return new double[]{x1, y1, z1};
    }

    private static double[] vectorProduct (double[] a, double[] b){
        double[] result = new double[3];
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];

        return result;
    }

}
