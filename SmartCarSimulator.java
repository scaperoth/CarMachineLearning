import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

public class SmartCarSimulator
{ 
  /**
   * constructor gets passed arraylist of cars
   * each cars attribute is manipulated by the nextstep method
   * and when draw is called, it loops through and "draws" each car
   *
   * 
   */
  double maxHeight = 1000.0D;
  
  double r = 5.0D;
  double d = 4.0D;
  double S = 30.0D;
  double L = 40.0D;
  
  ArrayList<SmartCar> cars;

  double t;

  public SmartCarSimulator(ArrayList<SmartCar> cars)
  {
    //override constructor
    //creating a local list of cars
  }
  

  public void init()
  {

  }
  

  public void draw(Graphics2D g2, Dimension D)
  {
      return;
    
  }
  
  public void nextStep(ArrayList<SmartCar> Cars)
  {
  }
  
  

  public double getDistanceMoved()
  {
    return 0;
  }
  

  public double getTime()
  {
    return 0;
  }
}
  