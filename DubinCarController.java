
import java.util.*;
import java.awt.geom.*;
import java.awt.*;


public class DubinCarController implements CarController {

    // Angular velocities.
    double mu1=0, mu2=1;
    ArrayList<Rectangle2D.Double> obstacles;

    // Are the two controls accelerators?
    boolean isAccelModel;


    public void init (double initX, double initY, double initTheta, double endX, double endY, double endTheta, ArrayList<Rectangle2D.Double> obstacles, SensorPack sensors)
    {
        this.obstacles = obstacles;
    }
    

    public double getControl (int i)
    {
        if (i == 1) {
            return mu1;
        }
        else if (i == 2) {
            return mu2;
        }
        else {
            System.out.println ("ERROR: DubinCarController.getControl(): incorrect input");
            return 0;
        }
    }


    public void move ()
    {
    }
    
    public void draw (Graphics2D g2, Dimension D)
    {
    }
    

}
