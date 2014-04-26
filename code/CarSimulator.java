
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;


public interface CarSimulator {

    public void init (double initX, double initY, double initTheta, ArrayList<Rectangle2D.Double> obstacles);
    
    public void draw (Graphics2D g2, Dimension D);
    
    public void nextStep (double control1, double control2, double delT);
    
    public double getX ();
    
    public double getY ();

    public double getV ();
    
    public double getTheta ();

    public double getTime ();

    public boolean hitObstacle ();

    public double getDistanceMoved ();

}
