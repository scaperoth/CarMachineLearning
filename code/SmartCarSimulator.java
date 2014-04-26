import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

public class CopCarSimulator implements CopSimulator
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
  
  ArrayList<SmartCar>;

  double t;
  

  public CopCarSimulator(ArrayList<SmartCar> Cars)
  {
    //override constructor
  }
  

  public void init(double initcopX, double initcopY, double initperpX, double initperpY)
  {
    this.copx = initcopX;
    this.copy = initcopY;

    this.perpx = initperpX;
    this.perpy = initperpY;

    this.t = 0.0D;
    this.distMoved = 0.0D;
  }
  

  public double getCopX()
  {
    return this.copx;
  }
  
  public double getPerpX()
  {
    return this.perpx;
  }

  public double getCopY()
  {
    return this.copy;
  }

  public double getPerpY()
  {
    return this.perpy;
  }
  
  public double getCopV()
  {
    return this.copv;
  }
  
  public double getPerpV()
  {
    return this.perpv;
  }

  public void draw(Graphics2D g2, Dimension D)
  {
    int i = (int)this.copx;
    int j = (int)this.copy;
    AffineTransform localAffineTransform1 = AffineTransform.getRotateInstance(-this.theta, i, D.height - j);
    g2.setTransform(localAffineTransform1);
    
      g2.setColor(Color.cyan);
      g2.fillOval(i-15, D.height - j - 8, 30, 16);
      g2.setColor(Color.black);
      g2.drawLine(i, D.height - j, i + 15, D.height - j);


      return;
    
  }
  
  public void nextStep(double control1, double control2, double delT)
  {
    this.copv = control1;
    this.perpv = control2;
    double d1 = delT * this.copv * Math.cos(this.theta);
    this.copx += d1;
    double d2 = delT * this.copv * Math.sin(this.theta);
    this.copy += d2;

    double d3 = delT * this.perpv * Math.cos(this.theta);
    this.perpx += d3;
    double d4 = delT * this.perpv * Math.sin(this.theta);
    this.perpy += d4;
    
    this.t += delT;
    this.distMoved += Math.sqrt(d1 * d1 + d2 * d2);
  }
  
  

  public double getDistanceMoved()
  {
    return this.distMoved;
  }
  

  public double getTime()
  {
    return this.t;
  }
}
  