
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class ScaperothUnicycleCarController implements CarController {

    int DRAWSOLUTION = 1;
    int CARHEIGHT = 20;
    int CARWIDTH = 33;
    // The two controls: either (vel,phi) or (acc,phi)
    double acc;       // Acceleration.
    double vel;       // Velocity.
    double phi;       // Steering angle.
    double endX;
    double endY;
    double startX;
    double startY;
    int direction = -1;
    int movement_granularity = 1;
    int goal_granularity = 1;
    ScaperothUnicycleState currentstate = null;
    ScaperothUnicycleState start;
    // Initialize.
    LinkedList<State> frontier = new LinkedList<State>();
    LinkedList<State> visitedStates = new LinkedList<State>();
    LinkedList<State> final_solution = new LinkedList<State>();
    int numMoves = 0;
    int count = 0;
    // Limit the total number of expansions.
    static int maxMoves = 1000;
    double currX;
    double currY;
    int counter = 0;
    Graphics2D g2;
    Dimension D;
    ArrayList<Rectangle2D.Double> obstacles;
    SensorPack sensors;
    // Is the first control an accelerator?
    boolean isAccelModel = false;

    public void init(double initX, double initY, double initTheta, double endX, double endY, double endTheta, ArrayList<Rectangle2D.Double> obstacles, SensorPack sensors) {
        this.sensors = sensors;
        this.obstacles = obstacles;
        this.sensors = sensors;
        this.endX = endX;
        this.endY = D.height - endY;

        this.startX = initX;
        this.startY = D.height - initY;

        this.currX = initX;
        this.currY = D.height - initY;

        start = new ScaperothUnicycleState(null, 100, (int) startX, (int) startY);

        // The start node is the first one to place in frontier.
        frontier.add(start);


    }

    public double getControl(int i) {
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

    public void move() {
        // This is where you adjust the control values.
        int movetoX;
        int movetoY;

        counter++;
        this.currX = this.sensors.getX();
        this.currY = D.height - this.sensors.getY();
        this.endY = this.endY - this.D.height;
        this.endX = this.endX - this.D.width;

        movetoX = (int) this.currX;
        movetoY = (int) this.currY;
        
        //don't remove a state if turning
        if (phi == 0) {
            ScaperothUnicycleState solNode = (ScaperothUnicycleState) final_solution.removeFirst();
            System.out.println(solNode.x);
            System.out.println(solNode.y);

            movetoX = solNode.x;
            movetoY = solNode.y;
        }

        moveto(movetoX, movetoY);

        //draw(this.g2,this.D);
        //ScaperothUnicycleCarController
        //this.hitsObstacle((int) this.currX, (int) this.currY);
    }

    void moveto(int x, int y) {
        int old_direction = direction;
        
        /**
         */
        //north
        if (this.currY - movement_granularity == y) {
            direction = 0;
            System.out.println("COUNTER: "+counter);
            counter=0;
            vel = 0;
            if(old_direction ==3)
                phi = -10;   
            else phi = 10;   
            
        } //south
        else if (this.currY + movement_granularity == y) {
            direction = 1;
            System.out.println("COUNTER: "+counter);
            
            counter=0;            
            vel = 0;
            if(old_direction ==2)
                phi = -10;   
            else phi = 10; 

        } //east
        else if (this.currX + movement_granularity == x) {
            direction = 2;
            System.out.println("COUNTER: "+counter);
            counter=0;
            vel = 0;   
            if(old_direction ==0)
                phi = -10;   
            else phi = 10;

        } //west
        else if (this.currX - movement_granularity == x) {
            direction = 3;
            System.out.println("COUNTER: "+counter);
            counter=0;   
            vel = 0;
            if(old_direction ==1)
                phi = -10;   
            else phi = 10;
        }
        

        if (direction == 0) {
            System.out.println("North: " + this.sensors.getTheta());
            if (this.sensors.getTheta() >= ((Math.PI / 2) - .05) && this.sensors.getTheta() <= ((Math.PI / 2) + .05)) {
                vel = 10;
                phi = 0;
            }
        } else if (direction == 1) {
            System.out.println("South: " + this.sensors.getTheta());
            if (this.sensors.getTheta() >= ((3 * Math.PI) / 2) - .05 && this.sensors.getTheta() <= ((3 * Math.PI) / 2) + .05) {
                vel = 10;
                phi = 0;
            }
        } else if (direction == 2) {
            System.out.println("East: " + this.sensors.getTheta());
            if ((this.sensors.getTheta() >= 0 && this.sensors.getTheta() <= 0 + .02)
                    || (this.sensors.getTheta() >= (Math.PI * 1.98) && this.sensors.getTheta() <= (Math.PI * 2))) {
                vel = 10;
                phi = 0;
            }
        } else if (direction == 3) {
            System.out.println("West: " + this.sensors.getTheta());
            if (this.sensors.getTheta() >= Math.PI - .05 && this.sensors.getTheta() <= Math.PI + .05) {
                vel = 10;
                phi = 0;
            }
        }
    }

    public void draw(Graphics2D g2, Dimension D) {

        this.g2 = g2;
        this.D = D;
        // If you want do draw something on the screen (e.g., a path)
        // this is where you do it. Remember to convert to Java coordinates:
        //    yJava = D.height - y;

        int y1Java = (int) (this.currY);
        int y2Java = (int) (this.endY);
        int x1Java = (int) (this.currX);
        int x2Java = (int) (this.endX);


        //this.g2.drawString("x: " + x1Java + " y:" + y1Java, x1Java, y1Java);

        if (count == 1) {
            final_solution = testsolution();
            //trash first value
            ScaperothUnicycleState solNode = (ScaperothUnicycleState) final_solution.removeFirst();
            /**
             *
             *
             * for (State s : final_solution) { ScaperothUnicycleState m =
             * (ScaperothUnicycleState) s; //System.out.println("X: " + sol.x);
             * g2.drawLine(m.x, m.y, m.parent.x, m.parent.y);
             *
             * }
             */
        }
        count++;

        /**
         * for (Rectangle2D.Double R : obstacles) { this.g2.drawString("x: " +
         * R.x + " y:" + (D.height - R.y), (int) R.x, (int) (this.D.height -
         * R.y)+20); }
         */
        //g2.drawLine (x1Java, y1Java, x2Java, y2Java);
    }

    boolean hitsObstacle(int x, int y) {
        for (Rectangle2D.Double R : obstacles) {
            if (intersects(R, x, y)) {
                //System.out.println("OH NO!@!!!!!");
                return true;
            }
        }
        return false;
    }

    boolean intersects(Rectangle2D.Double R, int x, int y) {
        //System.out.println("Checking for intersect...");
        Rectangle r = new Rectangle((int) R.x, (int) (D.height - R.y), (int) R.width, (int) R.height);

        if (r.intersects(new Rectangle(x - CARWIDTH / 2, y - CARHEIGHT / 2, CARWIDTH, CARHEIGHT))) {
            //System.out.println("Woah");
            return true;
        } else {
            return false;
        }
    }

    boolean closerThanParent(ScaperothUnicycleState parent, double x, double y) {

        double distance = distancer(x, y);

        if (parent == null) {
            return true;
        }

        double parent_distance = distancer(parent.x, parent.y);

        if (distance >= 0 && distance < parent_distance) {
            return true;
        } else {
            return false;
        }
    }

    double distancer(double x, double y) {
        double first_var = Math.pow((this.endX) - (x), 2);
        double second_var = Math.pow((this.endY - (y)), 2);

        double dist = first_var + second_var;
        /**
         * *
         * System.out.println("c^2 = ((" + (x) + ")-" + (y) + ")^2+((" +
         * (this.endX) + ")-" + (this.endY) + ")^2 = " + dist);
         * System.out.println("c^2 = (" + first_var + ")+(" + second_var + ") =
         * " + dist); System.out.println();
         */
        if (!hitsObstacle((int) x, (int) y)) {

            return dist;
        } else {
            return -1;
        }

    }

    LinkedList<State> testsolution() {

        while (numMoves < maxMoves) {

            // If nothing to explore, we're done.
            if (frontier.size() == 0) {
                System.out.println("Empty frontier");
                return null;
            }

            // Get first node in frontier and expand.
            State currentState = frontier.removeLast();

            if (satisfiesGoal(currentState)) {
                return makeSolution(currentState);
            }

            numMoves++;

            // Put current state in visited list.
            visitedStates.add(currentState);

            // Expand current state (look at its neighbors) and place in frontier.
            ArrayList<State> neighbors = getNeighbors(currentState);
            for (State s : neighbors) {
                if (!visitedStates.contains(s)) {  // Need memory to avoid repeats.
                    frontier.addLast(s);
                }
            }

            if (numMoves % 100 == 0) {
                // System.out.println("After " + numMoves + ": |F|=" + frontier.size() + "  |V|=" + visitedStates.size());
            }

            if (numMoves > 1) {
                ScaperothUnicycleState m = (ScaperothUnicycleState) currentState;
                g2.drawLine(m.x, m.y, m.parent.x, m.parent.y);
            }
        } // endwhile
        //ScaperothUnicycleCarController

        return null;
    }

    LinkedList<State> makeSolution(State goalState) {
        LinkedList<State> solution = new LinkedList<State>();
        solution.add(goalState);

        // Start from the goal and work backwards, following
        // parent pointers.
        State currentState = goalState;
        while (currentState.getParent() != null) {
            solution.addFirst(currentState.getParent());
            currentState = currentState.getParent();
        }

        System.out.println("BFS: Solution of length=" + solution.size() + " found with cost=" + goalState.costFromStart + " after " + numMoves + " moves");

        return solution;
    }

    ArrayList<State> getNeighbors(State state) {
        ScaperothUnicycleState m = (ScaperothUnicycleState) state;
        ArrayList<State> neighbors = new ArrayList<State>();
        double closest_so_far = distancer(this.startX, this.startY);
        double current_distance = 0;

        // North
        current_distance = distancer(m.x , m.y- movement_granularity);
        if (!hitsObstacle(m.x, m.y - movement_granularity)&&current_distance<closest_so_far) {
            ScaperothUnicycleState m2 = new ScaperothUnicycleState(m, m.N, m.x, m.y - movement_granularity);

            current_distance = distancer(m2.x, m2.y - movement_granularity);

            if (closerThanParent(m2.parent, m2.x, m2.y-movement_granularity) ) {
                m2.costFromStart = m.costFromStart + 1;
                m2.estimatedCostToGoal = goalCost(m2.x, m2.y- movement_granularity);
                neighbors.add(m2);

            }else if(current_distance<=closest_so_far){
                m2.costFromStart = m.costFromStart + 2;
                m2.estimatedCostToGoal = goalCost(m2.x, m2.y- movement_granularity);
                neighbors.add(m2);
                closest_so_far = distancer(m.x, m.y - movement_granularity);
            }
        }

        // South.
        current_distance = distancer(m.x , m.y+ movement_granularity);
        if (!hitsObstacle(m.x, m.y + movement_granularity)&&current_distance<closest_so_far) {
            ScaperothUnicycleState m2 = new ScaperothUnicycleState(m, m.N, m.x, m.y + movement_granularity);

            

            if (closerThanParent(m2.parent, m2.x, m2.y+ movement_granularity)) {
                m2.costFromStart = m.costFromStart + 1;
                m2.estimatedCostToGoal = goalCost(m2.x, m2.y+ movement_granularity);
                neighbors.add(m2);

                closest_so_far = distancer(m.x, m.y + movement_granularity);
            }
        }

        // East.
        current_distance = distancer(m.x + movement_granularity, m.y);
        if (!hitsObstacle(m.x + movement_granularity, m.y)&&current_distance<closest_so_far) {
            ScaperothUnicycleState m2 = new ScaperothUnicycleState(m, m.N, m.x + movement_granularity, m.y);

           

            if (closerThanParent(m2.parent, m2.x+ movement_granularity, m2.y)) {

                m2.costFromStart = m.costFromStart + 1;
                m2.estimatedCostToGoal = goalCost(m2.x + movement_granularity, m2.y);
                neighbors.add(m2);

                closest_so_far = distancer(m.x + movement_granularity, m.y);
            }
        }

        // West.
        current_distance = distancer(m.x - movement_granularity, m.y);
        if (!hitsObstacle(m.x - movement_granularity, m.y)&&current_distance<closest_so_far) {

            ScaperothUnicycleState m2 = new ScaperothUnicycleState(m, m.N, m.x - movement_granularity, m.y);

            if (closerThanParent(m2.parent, m2.x-movement_granularity, m2.y) ) {

                m2.costFromStart = m.costFromStart + 1;
                m2.estimatedCostToGoal = goalCost(m2.x-movement_granularity, m2.y);
                neighbors.add(m2);

                closest_so_far = distancer(m.x - movement_granularity, m.y);
            }
        }
        
        
        
        return neighbors;
    }

    boolean satisfiesGoal(State state) {
        ScaperothUnicycleState m = (ScaperothUnicycleState) state;
        if ((m.x >= endX - goal_granularity && m.x <= endX + goal_granularity) && (m.y >= endY - goal_granularity && m.y <= endY + goal_granularity)) {
            return true;
        }
        return false;

    }

    double goalCost(int x, int y) {
        double d = Math.sqrt((endX - x) * (endX - x) + (endY - y) * (endY - y));
        return d;
    }
}
