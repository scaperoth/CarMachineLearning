import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.text.*;

public class SmartCarSimulator {
    /**
     * constructor gets passed arraylist of cars
     * each cars attribute is manipulated by the nextstep method
     * and when draw is called, it loops through and "draws" each car
     *
     *
     */

    double speedtranslation = 10.0;

    double maxHeight = 1000.0D;

    double r = 5.0D;
    double d = 4.0D;
    double S = 30.0D;
    double L = 40.0D;
    ArrayList<SmartCar> removelist = new ArrayList<SmartCar>();
    Road road;
    ArrayList<SmartCar> cars;
    int numImages = 4;
    double t;
    DecimalFormat df = new DecimalFormat("##");

    UniformRandom random = new UniformRandom();

    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

    public SmartCarSimulator(ArrayList<SmartCar> cars, Road road, int numCarColors, double speedtranslation) {
        this.cars = cars;
        this.road = road;
        this.t = 0.0;
        this.numImages = numCarColors;
        this.speedtranslation = speedtranslation;

        for (int i = 0; i < numImages; i++) {
            try {
                images.add(ImageIO.read(new File("images/car" + (i + 1) + ".png")));
            } catch (IOException ex) {
                // handle exception...
                System.err.println("FILE NOT FOUND");
            }
        }

        //override constructor
        //creating a local list of cars
    }


    public double getX(SmartCar car) {
        return 0;
    }

    public double getY(SmartCar car) {
        return 0;
    }

    public double getTheta(SmartCar car) {
        return 0;
    }

    public void init() {

    }


    public void draw(Graphics2D g2, Dimension D, Graphics g) {
        for (SmartCar thiscar : cars) {
            int i = (int)thiscar.x;
            int j = (int)thiscar.y;
            AffineTransform localAffineTransform1 = AffineTransform.getRotateInstance(-thiscar.theta, i, D.height - j);

            if (thiscar.x < D.width + thiscar.width) {

                g2.setTransform(localAffineTransform1);
                BufferedImage image = images.get(thiscar.color);
                g2.drawImage(image, i - (int)thiscar.width / 2 , D.height - j - (int)(thiscar.height / 2), null);
 
                if (thiscar.isSpeeder) {

                    g2.setColor(Color.red);
                    g2.setStroke(new BasicStroke(3));
                    int cartop = D.height - j - (int)thiscar.height / 2;
                    g2.drawLine(i, cartop, i, cartop - 10);
                    g2.drawLine(i, cartop, i + 4, cartop - 4);
                    g2.drawLine(i, cartop, i - 4, cartop - 4);
                    g2.drawString(df.format(thiscar.vel * speedtranslation) + "", i - 7, cartop - 15);

                } else if (thiscar.isGettingSpeeder) {

                    g2.setColor(Color.green);
                    g2.setStroke(new BasicStroke(3));
                    int cartop = D.height - j - (int)thiscar.height / 2;
                    g2.drawLine(i, cartop, i, cartop - 10);
                    g2.drawLine(i, cartop, i + 4, cartop - 4);
                    g2.drawLine(i, cartop, i - 4, cartop - 4);
                    g2.drawString(df.format(thiscar.vel * speedtranslation) + "", i - 7, cartop - 15);

                }else {
                    g2.setColor(Color.gray);
                    g2.setStroke(new BasicStroke(3));
                    int cartop = D.height - j - (int)thiscar.height / 2;
                    g2.drawLine(i, cartop, i, cartop - 10);
                    g2.drawLine(i, cartop, i + 4, cartop - 4);
                    g2.drawLine(i, cartop, i - 4, cartop - 4);
                    g2.drawString(df.format(thiscar.vel * speedtranslation) + "", i - 7, cartop - 15);
                }

                /**g2.setColor(Color.cyan);
                  g2.fillOval(i - 15, D.height - j - 8, 30, 16);
                g2.setColor(Color.black);
                g2.drawLine(i, D.height - j, i + 15, D.height - j);
                 */

            } else {
                removelist.add(thiscar);
            }

        }

        
        return;

    }

    public void nextStep(double deltaTime) {
        
        this.t += deltaTime;
        for (SmartCar thiscar : cars) {

            double d1 = deltaTime * thiscar.vel * Math.cos(thiscar.theta);
            thiscar.x += d1;
            double d2 = deltaTime * thiscar.vel * Math.sin(thiscar.theta);
            thiscar.y += d2;

            thiscar.phi = (0.15707963267948966D * thiscar.phi);

            double d3 = deltaTime * thiscar.phi;

            thiscar.theta += d3;
            thiscar.theta = angleFix(thiscar.theta);


            thiscar.distMoved += Math.sqrt(d1 * d1 + d2 * d2);

        }
    }


    public double getDistanceMoved() {
        return 0;
    }

    double angleFix(double theta) {
        if (theta < 0.0D) {
            while (theta < 0.0D) {
                theta += 6.283185307179586D;
            }
        }
        if (theta > 6.283185307179586D) {
            while (theta > 6.283185307179586D) {
                theta -= 6.283185307179586D;
            }
        }
        return theta;
    }

    public void resetClock() {
        this.t = 0;
    }

    public double getTime() {
        return this.t;
    }
}
