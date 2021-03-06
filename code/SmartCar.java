
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

import javax.swing.text.html.MinimalHTMLWriter;


public class SmartCar {
	public final double CAR_MIN_SPACING = 10;
	public final double THETA_THRESHOLD = .1;
	public final double ROTATION_RATE = 25;
	public final double TURNING_ANGLE_DEGREES = 60;
	public final double ROAD_Y_THRESHOLD = 1;
	public final double STRAIGHTEN_ADJUSTMENT = 0;
	public final double DECEL_RATE = .66;
	public final double CLOSEST_SPEEDER_THRESH = 400;
	public final double COVERED_THRESHOLD = 200;
	public final double SPEEDER_CAUGHT_TIME = 1;
	//	public final double JERK_VALUE = .2;

	private boolean accelMode = true; //Developer setting

	// The two controls: either (vel,phi) or (acc,phi)
	double acc;       // Acceleration.
	double vel;       // Velocity.
	double phi;       // Steering angle.
	double x;       //X coordinate
	double y;       //Y coordinate
	double theta;
	int lane;
	int newlane;
	double oldVel;
	double targetVel; //Velocity car wants to maintain
	double distMoved;
	boolean changingLanes;
	boolean isStraightening;
	Road road;
	boolean isSpeeder;
	boolean isGettingSpeeder;
	boolean isCaught;
	double timeCaught;

	double width = 34;
	double height = 16;

	int color = 1;
	int numCarColors= 4;

	boolean DEBUG;

	ArrayList<Rectangle2D.Double> obstacles;
	//    SensorPack sensors;



	UniformRandom random = new UniformRandom();

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
	public SmartCar (int lane, double initTheta, double startSpeed, Road thisRoad, boolean debug, boolean isSpeeder, int numCarColors) {
		//        this.obstacles = obstacles;
		//        this.sensors = sensors;
		//        this.lane = lane;
		this.vel = this.targetVel = this.oldVel = startSpeed;
		this.acc = 0;
		this.phi = 0;
		this.x = -width;
		this.theta = initTheta;
		this.lane = lane;
		this.road = thisRoad;
		this.y = road.getLaneCenter(lane);

		this.isStraightening = false;
		this.changingLanes = false;
		this.isGettingSpeeder = false;
		this.isSpeeder = isSpeeder;
		this.numCarColors = numCarColors;
		this.DEBUG = debug;
		distMoved = 0.0;
		
		this.isCaught = false;
		timeCaught = 0;

		this.color = UniformRandom.uniform(0,numCarColors-1);

	}

	/**
	 * [getControl description]
	 * @param  i [description]
	 * @return   [description]
	 */
	public double getControl (int i) {
		if (i == 1) {
			return vel;
		} else if (i == 2) {
			return phi;
		}
		return 0;
	}


	public void move () {
		oldVel = vel;

		//If changing lanes or straightening, maintain current vel and phi values
		if (changingLanes) checkChangingLanes();
		else if (isStraightening) {
			//If straightened out, stop rotating
			if (straightenedOut()) {
				phi = 0;
				isStraightening = false;
			}
		}
		//If not changing lanes or straightening, do logic
		else {
			phi = 0;
			//
			//			if (isSpeeder){}
			//			else{}
		}

		SmartCar closeCar = tooCloseToCar();
		//Finally, check if about to hit something
		if (closeCar != null) {
			//Speeder will first try to change lanes (if not doing so already)
			if (this.isSpeeder && !changingLanes) {
				if (!changeLanes(true)) {
					if (!changeLanes(false)) {
						//If unable to change lanes (both left and right return false), car is caught, slow down
						if(!isCaught) { 
							timeCaught = road.trafficSim.time;
							isCaught = true;
						}
						
						//Is has been caught for long enough, speeder becomes a "law abider"
						if ((road.trafficSim.time - timeCaught) > SPEEDER_CAUGHT_TIME) {
							this.isSpeeder = false;
							targetVel = road.speedLimit;
						}
						if(accelMode) goSpeed(road.getVel(closeCar));
						else slowDown();
					}
					else isCaught = false;
				}
				else isCaught = false;
			}

			else {
				//If not speeder, just slow down if near car
				if(accelMode) goSpeed(road.getVel(closeCar));
				else slowDown();
				if (DEBUG) System.out.println("Law abider slowing down. Vel now " + vel*10 + "mph");

			}
		}
		else {

			//If not about to hit car, return to targetVelocity
			//			if(isSpeeder || !isGettingSpeeder){

			if (accelMode) goSpeed(targetVel);
			else vel = targetVel;


			if(DEBUG)System.out.println("Returning to target velocity. Vel now " + vel*10 + "mph");
		}
		//		}

		//Catch speeder logic
		if(!isSpeeder) {
			//Step 1: See if there is a speeder behind you, get closest
			SmartCar speeder = findSpeeder();
			//if there is a speeder, slow down until you are directly in front
			if (speeder != null) {
				isGettingSpeeder = true;


				//If there is a car directly behind you that is not a speeder, try to move towards speeder by changing lanes
				//If unable, maintain speed
				if(frontOfCar() && !frontOfSpeeder(speeder, false)) {

					if (road.getLane(speeder) > lane) {

						//changeLanes(false) will return false if unable to change. Otherwise does the change
						if (!changeLanes(false)) {
							//Maintaining speed by doing nothing
							if(DEBUG) System.out.println("Cant change lanes right to get speeder") ;
						}
					}
					else {
						if (!changeLanes(true)) {
							//Maintaining speed
							if (DEBUG) System.out.println("Cant change lanes left to get speeder") ;
						}
					}


				}
				//Else if not in front of a speeder in another lane, slow down
				else if (!frontOfSpeeder(speeder, true)) {
					//Check if there is someone in front of speeder. If not, change lanes if possible
					if (!speederLaneCovered(speeder)) {
						if(this.lane > road.getLane(speeder)) changeLanes(true);
						else changeLanes(false);
					}
					//else slow down
					else {

						if(accelMode) goSpeed(oldVel*DECEL_RATE);
						else slowDown();
					}

				}

				else {
					//Maintain speed 
					if(accelMode) goSpeed(oldVel);
				}

			}
			//else if no speeder, return to target speed
			else {
				isGettingSpeeder = false;
				//				if(vel > targetVel) slowDown();
				//				else if (vel < targetVel) speedUp();
			}
		}//End catch speeder
	}

	//Check if car is in front of any car
	private boolean frontOfCar() {
		//For each car, return true if in front and in same lane
		for (SmartCar c: road.getCars()) {
			if (!c.equals(this) && (Math.abs(this.x - (road.getX(c) + width)) < CAR_MIN_SPACING) && (road.getLane(c) == this.lane)) return true;
		}
		//If no car returned true, return false
		return false;
	}

	//Check if in front of speeder. If anyLane = false, speeder must be in same lane. Otherwise any lane
	private boolean frontOfSpeeder(SmartCar speeder, boolean anyLane) {
		if (Math.abs(this.x -(road.getX(speeder) + width)) < CAR_MIN_SPACING) return (anyLane || (road.getLane(speeder) == this.lane));
		else return false;
	}

	/**
	 * changes lanes for car
	 * returns false if not possible (or fails)
	 * updates lane value
	 * @return [description]
	 */
	public SmartCar tooCloseToCar() {
		ArrayList<SmartCar> cars = road.getCars();
		for (SmartCar c : cars) {
			//Car ignores itself
			if (!c.equals(this)) {
				//Check if in the same lane
				if ((this.lane == road.getLane(c)) || ((this.newlane == road.getLane(c)) && this.changingLanes)|| (this.lane == road.getNewLane(c))) {
					//Check if car is in front and too close
					if ((road.getX(c) > this.x) && ((road.getX(c) - (this.x + width)) < CAR_MIN_SPACING)) {
						if (DEBUG) System.out.println("Car detected in front");
						return c;
					}
				}
			}
		}
		return null;

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
		int deltaLane = -1; //If turning left, decrease lane
		if(!left) deltaLane = 1; //If right, increase lane

		if (!changingLanes && !isStraightening) {
			if (lane == 1 && left) {
				if (DEBUG) System.out.println("Cannot change lanes left");
				return false;
			}
			else if (lane == road.numLanes && !left) {
				if (DEBUG) System.out.println("Cannot change lanes right");
				return false;
			}

			else {
				//Check if there is a car in the next lane or about to change into lane				
				for (SmartCar c: road.getCars()) {
					//Ignore self
					if(!c.equals(this)) {
						//If car from list is in the lane trying to change to or changing to that lane
						if(road.getLane(c)==this.lane+deltaLane || (c.changingLanes && (c.newlane == this.lane+deltaLane))) {
							//Return false if in the range of X coordinates (checks in front as well slightly further)
							if((road.getX(c) > this.x-1.5*width) && road.getX(c) < this.x+2*width) {
								if(DEBUG) System.out.println("Cannot change lanes left due to car occupying space");
								return false;
							}
						}
					}
				}

				//If function hasn't returned false by this point, lane change is possible

				newlane = lane + deltaLane;
				this.phi = -1 * deltaLane * ROTATION_RATE;
				changingLanes = true;
				return true;
			}
		}

		//If currently turning or straightening, unable to change lanes
		if (DEBUG) System.out.println("Unable to change lanes: Car is currently changing or straightening");
		return false;
	}

	public void checkChangingLanes() {
		if (this.changingLanes) {
			double angleDist = Math.abs(this.theta - TURNING_ANGLE_DEGREES * Math.PI / 180);
			double secondDist = Math.abs((Math.PI * 2 - this.theta) + TURNING_ANGLE_DEGREES * Math.PI / 180);
			if (secondDist < angleDist) angleDist = secondDist;


			//If at turning angle, set phi to zero
			if (angleDist > THETA_THRESHOLD) {
				phi = 0;
			}

			//If at y, rotate back to theta = 0;
			if (Math.abs(this.y - road.getLaneCenter(newlane) - STRAIGHTEN_ADJUSTMENT) < ROAD_Y_THRESHOLD) {
				isStraightening = true;
				changingLanes = false;
				this.lane = newlane;

				//Find shortest rotation back to zero (will be the reverse direction it rotated to turn)
				if (theta > Math.PI) phi = ROTATION_RATE;
				else phi = -ROTATION_RATE;
			}
		}
		return;
	}

	public boolean straightenedOut() {
		if ((this.theta < THETA_THRESHOLD) || (2 * Math.PI - this.theta < THETA_THRESHOLD)) return true;
		else return false;
	}


	private SmartCar findSpeeder(){
		SmartCar closest = null;
		double dist = road.windowWidth;
		for (SmartCar c: road.getCars()) {
			//Check if car is a speeder and if behind me
			if(c.isSpeeder && (road.getX(c) < (this.x + width/2))) {
				//Check is this speeder is closer then current closest
				double toThis = this.x - road.getX(c);
				if((closest == null)|| toThis < dist) {
					closest = c;
					dist = toThis;
				}
			}
		}
		if ((this.x - road.getX(closest)) < CLOSEST_SPEEDER_THRESH) return closest;
		else return null;
	}

	private void slowDown(){
		//		acc -= JERK_VALUE*road.trafficSim.delT;
		//		acc = DECEL_RATE;
		if(accelMode) vel -= acc*road.trafficSim.delT;
		else vel = DECEL_RATE*oldVel;
		if (DEBUG) System.out.println("Slowing down to " + vel + " mph.");

	}

	private void speedUp(){;
	//		acc += JERK_VALUE*road.trafficSim.delT;
	//		acc = DECEL_RATE;
	if (accelMode) vel += acc*road.trafficSim.delT;			
	else vel =(2-DECEL_RATE)*oldVel;

	if(DEBUG) System.out.println("Speeding up to " + vel + " mph.");
	}

	private void goSpeed(double targetSpeed) {
		if (targetSpeed < 0) targetSpeed = 0;

		double deltaVel = Math.abs(targetSpeed - this.oldVel);
		//		acc = deltaVel*JERK_VALUE;
		acc = deltaVel;


		if (this.oldVel > targetSpeed) slowDown();
		else if (this.oldVel < targetSpeed) speedUp();
	}

	private boolean speederLaneCovered(SmartCar speeder){
		for (SmartCar c: road.getCars()) {
			if(road.getLane(c) == road.getLane(speeder) && !c.isSpeeder){
				if((road.getX(c) > road.getX(speeder)) && ((road.getX(c) - road.getX(speeder)) < COVERED_THRESHOLD)) return true;
			}
		}
		//If has not returned true, return false
		return false;
	}

}

