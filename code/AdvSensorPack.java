
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


public class AdvSensorPack extends BasicSensorPack {
    
    // For distance calculations.
    static double epsilon = 0.0001;

    // Current location, orientation and time. 
    double x, y, theta, time;

    ArrayList<Rectangle2D.Double> obstacles;
    
    // Number of sensors.
    int numSonar = 8;

    Point2D.Double[] sonarPoints;
    double[] sonarDistances;
    


    public void init (double initX, double initY, double initTheta, ArrayList<Rectangle2D.Double> obstacles)
    {
        x = initX;
        y = initY;
        theta = initTheta;
        this.obstacles = obstacles;
    }
    

    public double getTrueX ()
    {
        return x;
    }
    
    public double getTrueY ()
    {
        return y;
    }
    
    public double getTrueTheta ()
    {
        return theta;
    }
    
    
    public double getX ()
    {
        return x;
    }
    
    
    public double getY ()
    {
        return y;
    }
    
    
    public double getTheta ()
    {
        return theta;
    }
    

    public double getTime ()
    {
        return time;
    }
    

    public void doSensing (double x, double y, double theta, double time)
    {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.time = time;

        updateSensors ();
    }
    
    void updateSensors ()
    {
        // Each time, we create a new set of readings.
        sonarPoints = new Point2D.Double [numSonar];
        sonarDistances = new double [numSonar];
        
        for (int i=0; i<numSonar; i++) {

            // Compute angles from current orientation.
            double angle = theta + i*(2.0*Math.PI/numSonar);

            // Find closest obstacle point.
            Point2D.Double bestPoint = null;
            double bestDist = Double.MAX_VALUE;

            for (Rectangle2D.Double R: obstacles) {
                // Make line segments for each side of each obstacle.
                Line2D.Double[] L = new Line2D.Double [4];
                L[0] = new Line2D.Double (R.x, R.y, R.x+R.width, R.y);
                L[1] = new Line2D.Double (R.x, R.y, R.x, R.y-R.height);
                L[2] = new Line2D.Double (R.x, R.y-R.height, R.x+R.width, R.y-R.height);
                L[3] = new Line2D.Double (R.x+R.width, R.y, R.x+R.width, R.y-R.height);
                // For each of these, compute intersection point along angle, if it exists.
                for (int k=0; k<4; k++) {
                    Point2D.Double p = intersect (x, y, angle, L[k]);
                    if (p != null) {
                        double d = dist (x,y, p.x,p.y);
                        if (d < bestDist) {
                            bestDist = d;
                            bestPoint = p;
                        }
                    }
                }

            } // end-for-obstacles.

            sonarPoints[i] = bestPoint;
            sonarDistances[i] = bestDist;

        } // end-outerfor
        
    }



    double angleFix (double a)
    {
        // Make each angle an angle between 0 and 2*PI.
        //** Note: this code can be optimized.
        if (a < 0) {
            while (a < 0) {
                a = a + 2*Math.PI;
            }
        }
        else if (a > 2*Math.PI) {
            while (a > 2*Math.PI) {
                a = a - 2*Math.PI;
            }
        }
        return a;
    }
    


    Point2D.Double intersect (double x, double y, double angle, Line2D.Double L)
    {
        // Key idea: if the given angle is between the angles of the lines 
        // from (x,y) to the endpoints of L, then there will be an intersection.
        // However, there are angle conventions to worry about, and pathological
        // cases (e.g., the line is at the same angle).

        // Find angle to each end point, converting from Java angles
        // (which are between 0 and PI or 0 and -PI) to [0,2*PI].
        double a1 = Math.atan2 (L.y1-y, L.x1-x);
        double a2 = Math.atan2 (L.y2-y, L.x2-x);
        
        // Convert.
	angle = angleFix (angle);
        a1 = angleFix (a1);
        a2 = angleFix (a2);

        // Now order the angles.
        double max = a1;
        double min = a2;
        if (a2 > max) {
            max = a2;
            min = a1;
        }
        
        boolean isBetw = isBetween (angle, min, max);
        
        if (! isBetw) {
            // Then, there's no intersection point.
            return null;
        }

        // Otherwise, compute the intersection point.

        double d1 = dist (x,y, L.x1, L.y1);
        double d2 = dist (x,y, L.x2, L.y2);

        // Check if the angle is the same - pathological case.
        if (Math.abs(min - max) < epsilon) {
            // Find the closer point.
            if (d1 < d2) {
                return new Point2D.Double (L.x1, L.y1);
            }
            else {
                return new Point2D.Double (L.x2, L.y2);
            }
        }

        // First, we'll create a point on the other side of the edge
        // at least a distance maxD away.
        double maxD = d1;
        if (d2 > maxD) {
            maxD = d2;
        }
        double x3 = x + maxD * Math.cos (angle);
        double y3 = y + maxD * Math.sin (angle);
        Line2D.Double L2 = new Line2D.Double (x,y, x3,y3);

        // This method computes the actual intersection point.
        return lineSegIntersect (L, L2);
    }
    

    boolean isBetween (double angle, double angleToPoint1, double angleToPoint2)
    {
        // This is not as straightforward as it might seem because of
        // the different cases.
	if (angleToPoint2 - angleToPoint1 < Math.PI) {
	    // Does not cut across from 4th to 1st quad.
	    if ( (angle >= angleToPoint1) && (angle <= angleToPoint2) ) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	
	// Otherwise, it does cut, which gives us the difficult case.
	if (angle < Math.PI/2) {
	    // Angle is in the first quad.
	    if (angle <= angleToPoint1) {
		return true;
	    }
	    else {
		return false;
	    }
	}
	else {
	    // Angle is in 4th quad.
	    if (angle >= angleToPoint2) {
		return true;
	    }
	    else {
		return false;
	    }
	}
    }



    Point2D.Double lineSegIntersect (Line2D.Double La, Line2D.Double Lb)
    {
        // We will compute the intersection point in the usual way:
        // write each in y=mx+c form, and solve for the intersection
        // point. However, we have to worry about the pathalogical 
        // cases: the lines are in the same direction, or when one
        // of them is vertical (m=infinity).

        // See if one of them is vertical
        if ( Math.abs(La.x1 - La.x2) < epsilon ) {
            // La is vertical.
            if ( Math.abs(Lb.x1 - Lb.x2) < epsilon ) {
                // Both vertical
                return null;
            }
            // Lb is not, so make its equation.
            double c1 = La.x1;
            double m2 = (Lb.y2 - Lb.y1) / (Lb.x2 - Lb.x1);
            double c2 = Lb.y1 - m2 * Lb.x1;
            // Handle this case separately.
            return extLineIntersectVertical (c1, m2, c2);
        }

        // See if the other is.
        if ( Math.abs(Lb.x1 - Lb.x2) < epsilon ) {
            // La can't be vertical because we've checked.
            double c1 = Lb.x1;
            double m2 = (La.y2 - La.y1) / (La.x2 - La.x1);
            double c2 = La.y1 - m2 * La.x1;
            return extLineIntersectVertical (c1, m2, c2);
        }
        
        // If neither are vertical, it's straightforward.
        double m1 = (La.y2 - La.y1) / (La.x2 - La.x1);
        double c1 = La.y1 - m1 * La.x1;
        double m2 = (Lb.y2 - Lb.y1) / (Lb.x2 - Lb.x1);
        double c2 = Lb.y1 - m2 * Lb.x1;

        // We'll parcel out this computation since it's useful.
        return lineSegIntersectSlope (m1, c1, m2, c2);
    }
    

    Point2D.Double lineSegIntersectSlope (double m1, double c1, double m2, double c2)
    {
        // We KNOW the intersection is proper, so no need for vertical checks.
        if (Math.abs(m1-m2) < epsilon) {
            // Parallel.
            return null;
        }

        double x = (c2-c1) / (m1-m2);
        double y = m1*x + c1;
        return new Point2D.Double (x,y);
    }



    Point2D.Double extLineIntersectVertical (double c1, double m2, double c2)
    {
        // The first line is vertical: x=c1. Second is normal: y=m2*x+c2
        // Solve: y = m2*c1 + c2.
        double x = c1;
        double y = m2*c1 + c2;
        return new Point2D.Double (x,y);
    }



    public void draw (Graphics2D g2, Dimension D)
    {
        if (sonarPoints == null) {
            return;
        }

        // Draw the sensor points from theta.
        g2.setColor (Color.pink);
        for (int i=0; i<numSonar; i++) {
            if (sonarPoints[i] == null) {
                continue;
            }
            double angle = theta + i*(2.0*Math.PI/numSonar);
            // Draw from (x,y) to sonarPoints[i].
            int x1 = (int) x;
            int y1 = (int) y;
            int x2 = (int) sonarPoints[i].x;
            int y2 = (int) sonarPoints[i].y;
            g2.drawLine (x1, D.height-y1, x2, D.height-y2);
	    //g2.drawOval (x2-5,D.height-y2-5,10,10);
        }
    }


    double dist (double x1, double y1, double x2, double y2)
    {
        return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
    

    String printStr (Line2D.Double L)
    {
        String str = "[" + L.x1 + "," + L.y1 + ", " + L.x2 + "," + L.y2 + "]";
        return str;
    }
    
    
}
