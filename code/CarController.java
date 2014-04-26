
import java.util.*;
import java.awt.*;
import java.awt.geom.*;


public interface CarController {

    public void init (double initX, double initY, double initTheta, double endX, double endY, double endTheta, ArrayList<Rectangle2D.Double> obstacles, SensorPack sensors);

    public void draw (Graphics2D g2, Dimension D);

    public void move ();

    public double getControl  (int i);
    
}
