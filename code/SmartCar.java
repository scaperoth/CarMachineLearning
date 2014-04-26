
import java.util.*;
import java.awt.geom.*;
import java.awt.*;


public class SmartCar {

    // The two controls: either (vel,phi) or (acc,phi)
    double acc;       // Acceleration.
    double vel;       // Velocity.
    double phi;       // Steering angle.
    double x, y, theta;
    double distMoved;
    int lane;
    int newlane;
    boolean changingLanes;

    ArrayList<Rectangle2D.Double> obstacles;
    SensorPack sensors;

    // Is the first control an accelerator?
    boolean isAccelModel = false;

    /**
     * [init description]
     * @param initX     [description]
     * @param initY     [description]
     * @param initTheta [description]
     * @param endX      [description]
     * @param endY      [description]
     * @param endTheta  [description]
     * @param obstacles [description]
     * @param sensors   [description]
     */
    public SmartCar (double initX, double initY, double initTheta) {
        this.obstacles = obstacles;
        this.sensors = sensors;
        this.lane = lane;
        this.vel = 10;
        this.phi = 0;
        this.x = initX;
        this.y = initY;
        this.theta = initTheta;
        this.distMoved = 0.0;
    }

    /**
     * [getControl description]
     * @param  i [description]
     * @return   [description]
     */
    public double getControl (int i) {
        if (i == 1) {
            if (isAccelModel) {
                return acc;
            } else {
                return vel;
            }
        } else if (i == 2) {
            return phi;
        }
        return 0;
    }


    public void move () {
        vel = 10;
        // This is where you adjust the control values.
        //
        //if (changinglanes) {
            //checkChangingLanes;
       // } else {
            //everything else
            //
            // logic:
            // 
            // am i about to hit a car
            // should i change lanes?
            // tooCloseToCar()
            // 
            //if any car is speeding and not i'm changing lanes
            //is speeder in my lane
            //are/is other car(s) in the other lane(s)?
            //
            //do i need to change lanes to stop speeder?
            //
        //}

    }

    /**
     * changes lanes for car
     * returns false if not possible (or fails)
     * updates lane value
     * @return [description]
     */
    public boolean tooCloseToCar() {
        //use distance function to compare x values of cars
        //change lanes if necessary
        return true;
    }

    public void draw (Graphics2D g2, Dimension D) {
        // If you want do draw something on the screen (e.g., a path)
        // this is where you do it. Remember to convert to Java coordinates:
        //    yJava = D.height - y;
        //    paramGraphics2D.setColor(Color.cyan);
    }

    /**
     * changes lanes for car
     * returns false if not possible (or fails)
     * updates lane value
     * @return [description]
     */
    public boolean changeLanes(boolean left) {
        changingLanes = true;
        //newlane = lane+-1;
        //update lane
        return true;
    }

    public void checkChangingLanes() {
        //set phi/vel
        //if y val is target lane y
        //straighten out
        //if straightened out
        //changing lanes = false


        return;
    }

    public boolean straightenedOut() {
        return true;
    }

    /**
     * adjust to speed to parameter
     * @param v velocity to change to
     */
    public void adjustSpeed(double v) {

    }

    double dist (double x1, double y1, double x2, double y2)
    {
        return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

}
