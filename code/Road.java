
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;


public class Road {
	int numCars = 0;
	int numLanes =2;
	double laneWidth;
	double speedLimit; //As a simulator value (0-10)
	ArrayList<SmartCar> cars;
	boolean DEBUG;
	double roadTop;
	double roadBottom;

	/**
	 * inits variables for road
	 * @param numCars    [description]
	 * @param speedLimit [description]
	 * @param numLanes   [description]
	 * @param laneWidth  [description]
	 */
	public Road(double speedLimit, int numLanes, double laneWidth, double sLimit, boolean debug){
		this.speedLimit = speedLimit;
		this.numLanes = numLanes;
		this.laneWidth = laneWidth;
		this.speedLimit = sLimit;
		this.DEBUG = debug;
	}

	/**
	 * adds a car to the road
	 * @param car  [description]
	 * @param lane [description]
	 */
	public boolean add(SmartCar car){	
		cars.add(car);
		numCars++;
		return true;
	}

	/**
	 * removes car from road
	 * @param car  [description]
	 * @param lane [description]
	 */
	public boolean remove(SmartCar car){	
		if (cars.isEmpty()) {
			if(DEBUG) System.out.println("Remove car failed: No cars left in arrayList");
			return false;
		}
		else {
			cars.remove(car);
			numCars--;
			return true;
		}
	}

	/**
	 * draws the road
	 * @param g2 [description]
	 * @param D  [description]
	 */
	public void draw (Graphics2D g2, Dimension D) {

	}

	/**
	 * [getX description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getX(SmartCar car){
		if (cars.contains(car)) {
			return car.x;
		}
		else {
			if (DEBUG) System.out.println("getX failed: Car not in arraylist");
			return -1;
		}
	}   

	/**
	 * [getY description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getY(SmartCar car){
		if (cars.contains(car)) {
			return car.y;
		}
		else {
			if (DEBUG) System.out.println("getY failed: Car not in arraylist");
			return -1;
		}
	} 

	/**
	 * [getVel description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getVel(SmartCar car){
		if (cars.contains(car)) {
			return car.vel;
		}
		else {
			if (DEBUG) System.out.println("getVel failed: Car not in arraylist");
			return -1;
		}
	}

	/**
	 * [getAccel description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getAccel(SmartCar car){
		if (cars.contains(car)) {
			return car.acc;
		}
		else {
			if (DEBUG) System.out.println("getAccel failed: Car not in arraylist");
			return -1;
		}
	}
	/**
	 * [getTheta description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getTheta(SmartCar car){
		if (cars.contains(car)) {
			return car.theta;
		}
		else {
			if (DEBUG) System.out.println("getTheta failed: Car not in arraylist");
			return -1;
		}
	}

	/**
	 * [getLane description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public int getLane(SmartCar car){
		if (cars.contains(car)) {
			return car.lane;
		}
		else {
			if (DEBUG) System.out.println("getLane failed: Car not in arraylist");
			return -1;
		}
	}

	/**
	 * calculate center of lane
	 * @param  Car [description]
	 * @return     [description]
	 */
	public double getLaneCenter(int lane){
		if (lane < 1 || lane > numLanes) {
			if (DEBUG) System.out.println("getLaneCenter failed: Lane does not exist");
			return -1;
		}
		else return (roadTop + 50*lane-25);
	}

	/**
	 * [getLane description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public boolean isSpeeding(SmartCar car){
		if (cars.contains(car)) {
			if (car.vel > speedLimit) return true;
			else return false;
		}
		else {
			if (DEBUG) System.out.println("isSpeeding failed: Car not in arraylist");
			return false;
		}
	}

	/**
	 * [getCars description]
	 * @return [description]
	 */
	public ArrayList<SmartCar> getCars(){
		if (cars.isEmpty() && DEBUG) System.out.println("in getCars: cars arraylist is empty!");
		return cars;
	}	

	/**
	 * next step method will call to check if offroad
	 * @param  x [description]
	 * @param  y [description]
	 * @return   [description]
	 */
	public boolean offRoad (double x, double y) {
		return false;
	}

	/**
	 * [getLane description]
	 * @param  Car [description]
	 * @return     [description]
	 */
	public int getNumLanes(){
		return numLanes;
	}

	/**
	 * returns true if another car is 
	 * changing to possible new lane 
	 * to lock lane
	 * @return [description]
	 */
	public boolean isChangingToLane(int newlane){
		if (newlane < 1 || newlane > 3) {
			if (DEBUG) System.out.println("isChangingToLane failed: newLane does not exist");
			return false;
		}
		else {
			for(SmartCar c: cars) {
				if (c.changingLanes && c.newlane == newlane) return true;
				else;
			}
			return false;
		}
	}

	double dist (double x1, double y1, double x2, double y2)
	{
		return Math.sqrt ((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

}

