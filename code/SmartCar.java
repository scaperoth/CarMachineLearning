
import java.util.*;
import java.awt.geom.*;
import java.awt.*;


public class SmartCar {
	public final double CAR_MIN_SPACING = 10;
	public final double THETA_THRESHOLD = .1;
	public final double ROTATION_RATE = 10;
	public final double TURNING_ANGLE_DEGREES = 60;
	public final double ROAD_Y_THRESHOLD = 1;

	// The two controls: either (vel,phi) or (acc,phi)
	double acc;       // Acceleration.
	double vel;       // Velocity.
	double phi;       // Steering angle.
	double x;		//X coordinate
	double y;		//Y coordinate
	double theta;
	int lane;
	int newlane;
	boolean changingLanes;
	Road road;

	boolean DEBUG;

	ArrayList<Rectangle2D.Double> obstacles;
	//    SensorPack sensors;

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
	public SmartCar (int lane, double initTheta, double startSpeed, Road thisRoad, boolean debug) {
		//        this.obstacles = obstacles;
		//        this.sensors = sensors;
		//        this.lane = lane;
		this.vel = startSpeed;
		this.phi = 0;
		this.x = 0;
		this.theta = initTheta;
		this.lane = lane;
		this.road = thisRoad;
		this.DEBUG = debug;
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
		ArrayList<SmartCar> cars = road.getCars();
		for(SmartCar c: cars){
			//Car ignores itself
			if(!c.equals(this)) {
				//Check if in the same lane
				if (this.lane == c.lane) {
					//Check if car is in front and too close
					if ((c.x > this.x) && ((c.x - this.x) < CAR_MIN_SPACING)) {
						if (DEBUG) System.out.println("Car detected in front");
						return true;
					}
				}
			}
		}
		return false;

		//use distance function to compare x values of cars
		//change lanes if necessary
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
		if (left) {
			if (lane == 1) {
				if (DEBUG) System.out.println("Cannot change lanes left");
				return false;
			}
			else {
				newlane = lane-1;
				this.phi = -1 *ROTATION_RATE;
			}
		}
		else {
			if (lane == road.numLanes) {
				if (DEBUG) System.out.println("Cannot change lanes right");
				return false;
			}
			else {
				newlane = lane+1;
				this.phi = ROTATION_RATE;
			}
		}

		changingLanes = true;
		return true;
	}

	public void checkChangingLanes() {
		if (this.changingLanes){
			double angleDist = Math.abs(this.theta - TURNING_ANGLE_DEGREES*Math.PI/180);
			double secondDist = Math.abs((Math.PI*2 - this.theta) + TURNING_ANGLE_DEGREES*Math.PI/180);
			if (secondDist < angleDist) angleDist = secondDist;
			
			
			//INCOMPLETE
			//If not at turning angle, keep the same phi
			if (angleDist < THETA_THRESHOLD)
				
			if (Math.abs(this.y - road.getLaneCenter(newlane)) < ROAD_Y_THRESHOLD) {
				//Lane change complete, straighten out
				//			if(this.theta)
			}

			//set phi/vel
			//if y val is target lane y
			//straighten out
			//if straightened out
			//changing lanes = false


		}
		return;
	}

	public boolean straightenedOut() {
		if ((this.theta < THETA_THRESHOLD) || (2*Math.PI - this.theta < THETA_THRESHOLD)) return true;
		else return false;
	}

	/**
	 * adjust to speed to parameter
	 * @param v velocity to change to
	 */
	public void adjustSpeed(double v) {
		if (DEBUG) System.out.println("Velocity changing to: " + v*10 + " mph");
		this.vel = v;
	}


}
