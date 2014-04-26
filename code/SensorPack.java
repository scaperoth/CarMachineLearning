
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


public interface SensorPack {
    
    public void init (double initX, double initY, double initTheta, ArrayList<Rectangle2D.Double> obstacles);
    
    public double getX ();
    
    public double getY ();
    
    public double getTheta ();

    public double getTime ();

    public void doSensing (double x, double y, double theta, double time);

    public void draw (Graphics2D g2, Dimension D);
    
}
