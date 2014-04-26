
import java.util.*;
import java.awt.geom.*;
import java.awt.*;


public class Road {
	int numCars = 0;
	int numLanes =2;
	double laneWidth;
	double speedLimit;
	//ArrayList<SmartCar> cars;

	/**
	 * inits variables for road
	 * @param numCars    [description]
	 * @param speedLimit [description]
	 * @param numLanes   [description]
	 * @param laneWidth  [description]
	 */
	public Road(int numCars, double speedLimit, int numLanes, double laneWidth){
		this.numCars = numCars;
		this.speedLimit = speedLimit;
		this.numLanes = numLanes;
		this.laneWidth = laneWidth;
	}

	/**
	 * adds a car to the road
	 * @param car  [description]
	 * @param lane [description]
	 */
	public void add(SmartCar car){	
		//cars.add(car);
	}

	/**
	 * removes car from road
	 * @param car  [description]
	 * @param lane [description]
	 */
	public void remove(SmartCar Car){	
		//get index of car and remove
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
    public double getX(SmartCar Car){
        return 0;
    }   

    /**
     * [getY description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getY(SmartCar Car){
        return 0;   
    }

    /**
     * [getVel description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getVel(SmartCar Car){
        return 0;
    }

    /**
     * [getAccel description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getAccel(SmartCar Car){
        return 0;
    }

    /**
     * [getTheta description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getTheta(SmartCar Car){
        return 0;
    }

    /**
     * [getLane description]
     * @param  Car [description]
     * @return     [description]
     */
    public int getLane(SmartCar Car){
        return 0;
    }

    /**
     * calculate center of lane
     * @param  Car [description]
     * @return     [description]
     */
    public double getLaneY(int lane){
        return 0;
    }

    /**
     * [getLane description]
     * @param  Car [description]
     * @return     [description]
     */
    public double isSpeeding(SmartCar Car){
    	return 0;
    }

    /**
     * [getCars description]
     * @return [description]
     
    public ArrayList<SmartCar> getCars(){
        return ;
    }	
    */
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
    	return false;

    }
}