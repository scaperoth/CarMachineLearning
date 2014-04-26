
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GroundTruthSensor implements SensorPack {
    
    double x, y, theta, time;
    ArrayList<Rectangle2D.Double> obstacles;
    
    public void init (double initX, double initY, double initTheta, ArrayList<Rectangle2D.Double> obstacles)
    {
        x = initX;
        y = initY;
        theta = initTheta;
        this.obstacles = obstacles;
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

    }
    
    public void draw (Graphics2D g2, Dimension D)
    {
    }
    
}
