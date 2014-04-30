
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;


public class Road {

    int numCars = 0;
    int numLanes = 2;
    double laneWidth;
    //As a simulator value (0-10)
    double speedLimit; 
    ArrayList<SmartCar> cars = new ArrayList<SmartCar>();
    boolean DEBUG;
    double roadTop;
    double roadBottom;
    double windowHeight, windowWidth;

    Color roadColor = Color.DARK_GRAY;
    Color lineColor = Color.yellow;

    /**
     * inits variables for road
     * @param numCars    [description]
     * @param speedLimit [description]
     * @param numLanes   [description]
     * @param laneWidth  [description]
     */
    public Road(double speedLimit, int numLanes, double laneWidth, boolean debug) {
        this.speedLimit = speedLimit;
        this.numLanes = numLanes;
        this.laneWidth = laneWidth;
        this.DEBUG = debug;
    }

    /**
     * adds a car to the road
     * @param car  [description]
     * @param lane [description]
     */
    public boolean add(SmartCar car) {
        cars.add(car);
        numCars++;
        return true;
    }

    /**
     * removes car from road
     * @param car  [description]
     * @param lane [description]
     */
    public boolean remove(SmartCar car) {
        if (cars.isEmpty()) {
            if (DEBUG) System.out.println("Remove car failed: No cars left in arrayList");
            return false;
        } else {
            cars.remove(car);
            numCars--;
            return true;
        }
    }

    /**
     * adds a car to the road
     * @param car  [description]
     * @param lane [description]
     */
    public void removeCars() {
        cars.clear();
    }

    /**
     * draws the road and the lanes
     * @param g2 [description]
     * @param D  [description]
     */
    public void draw (Graphics2D g2, Dimension D) {
        windowHeight = D.height;
        windowWidth = D.width;
        roadTop =(windowHeight / 2)-((numLanes/2)+laneWidth);
        roadBottom = roadTop + (numLanes * laneWidth);

        g2.setColor (roadColor);
        g2.fillRect (0, (int)roadTop, D.width, (int)(numLanes * laneWidth));

        Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {12}, 0);
        for (int i = 1; i < numLanes; i++) {
            Line2D line = new Line2D.Double(0, (int)(i * laneWidth + roadTop), D.width, (int)(i * laneWidth + roadTop));
            g2.setColor (lineColor);
            g2.setStroke(drawingStroke);
            g2.draw(line);
        }

    }

    /**
     * [getX description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getX(SmartCar car) {
        if (cars.contains(car)) {
            return car.x;
        } else {
            if (DEBUG) System.out.println("getX failed: Car not in arraylist");
            return -1;
        }
    }

    /**
     * [getY description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getY(SmartCar car) {
        if (cars.contains(car)) {
            return car.y;
        } else {
            if (DEBUG) System.out.println("getY failed: Car not in arraylist");
            return -1;
        }
    }

    /**
     * [getVel description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getVel(SmartCar car) {
        if (cars.contains(car)) {
            return car.vel;
        } else {
            if (DEBUG) System.out.println("getVel failed: Car not in arraylist");
            return -1;
        }
    }

    /**
     * [getAccel description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getAccel(SmartCar car) {
        if (cars.contains(car)) {
            return car.acc;
        } else {
            if (DEBUG) System.out.println("getAccel failed: Car not in arraylist");
            return -1;
        }
    }
    /**
     * [getTheta description]
     * @param  Car [description]
     * @return     [description]
     */
    public double getTheta(SmartCar car) {
        if (cars.contains(car)) {
            return car.theta;
        } else {
            if (DEBUG) System.out.println("getTheta failed: Car not in arraylist");
            return -1;
        }
    }

    /**
     * [getLane description]
     * @param  Car [description]
     * @return     [description]
     */
    public int getLane(SmartCar car) {
        if (cars.contains(car)) {
            return car.lane;
        } else {
            if (DEBUG) System.out.println("getLane failed: Car not in arraylist");
            return -1;
        }
    }

    /**
     * calculate center of lane
     * @param  Car [description]
     * @return     [description]
     */
    public double getLaneCenter(int lane) {
        if (lane < 1 || lane > numLanes) {
            if (DEBUG) System.out.println("getLaneCenter failed: Lane does not exist");
            return -1;
        } else return windowHeight - (roadTop + (lane - 1) * laneWidth + (laneWidth / 2));
    }

    /**
     * [getLane description]
     * @param  Car [description]
     * @return     [description]
     */
    public boolean isSpeeding(SmartCar car) {
        if (cars.contains(car)) {
            if (car.vel > speedLimit) return true;
            else return false;
        } else {
            if (DEBUG) System.out.println("isSpeeding failed: Car not in arraylist");
            return false;
        }
    }

    /**
     * [getCars description]
     * @return [description]
     */
    public ArrayList<SmartCar> getCars() {
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
    public int getNumLanes() {
        return numLanes;
    }

    /**
     * [getLane description]
     * @param  Car [description]
     * @return     [description]
     */
    public int getNumCars() {
        return cars.size();
    }

    /**
     * returns true if another car is
     * changing to possible new lane
     * to lock lane
     * @return [description]
     */
    public boolean isChangingToLane(int newlane) {
        if (newlane < 1 || newlane > 3) {
            if (DEBUG) System.out.println("isChangingToLane failed: newLane does not exist");
            return false;
        } else {
            for (SmartCar c : cars) {
                if (c.changingLanes && c.newlane == newlane) return true;
                else;
            }
            return false;
        }
    }

    double dist (double x1, double y1, double x2, double y2) {
        return Math.sqrt ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

	public int getNewLane(SmartCar c) {
		if (c.changingLanes){
		return c.newlane;
		}
		else return -1;
	}
	
	public int openLaneforSpeeder(SmartCar speeder){
		for(int i=1; i<=numLanes; i++) {
			boolean laneHasCar = false;
			for(SmartCar c: cars) {
				if(c.lane == i) laneHasCar = true;
			}
			if (!laneHasCar) return i;
		}
		return 0;
	}
}

