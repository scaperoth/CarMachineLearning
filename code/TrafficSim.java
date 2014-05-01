import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;


public class TrafficSim extends JPanel {
    public final double MAXSPEED = 10.0;
    public final double MINSPEED = 5.0;
    public final double SPEEDLIMIT = 7.5;
    public final double SPEEDTRANSLATION = 10;

    int numLanes = 2;
    int maxNumCars = 10;
    
    
    int currNumCars = 1;
    double startSpeed = 10; //Not used????
    double sumSpeeds = 0;
    double avgSpeed = 0;

    double numofSpeeders = 0;
    double percentSpeeders = 0;

    boolean DEBUG = false;
    double laneWidth = 30;
    double speederSpeed = 10;

    //number of different car images available
    int numCarColors = 6;

    //SmartCarSimulator carSim = null;
    //Arraylist<SmartCar> cars;
    Road roadControl;
    SmartCarSimulator carSim = null;
    ArrayList<SmartCar> cars;

    // Animation stuff.
    Thread currentThread;
    boolean isPaused = false;
    double javaWidth;

    double initx, inity, initTheta;

    // The time step. 0.1 is a large value. We might reduce it
    // later and also reduce the sleeptime.
    double delT = .1;
    int sleeptime = 10;

    DecimalFormat df = new DecimalFormat("##.##");
    String topMessage = "";
    String avgCarMessage = "";
    String numCarMessage = "";
    String percentSpeederMsg = "";

    UniformRandom random = new UniformRandom();
    double thisTime = 0;
    double nextWaitTime = 0;
    double minWaitTime = delT*3;
    double maxWaitTime = 1;


    int isSpeeder = random.uniform(0, 1);

    public void TrafficSim() {

    }

    ////////////////////////////////////////////////////////////////////////
    // Drawing

    public void paintComponent (Graphics g) {
        super.paintComponent (g);

        Graphics2D g2 = (Graphics2D) g;

        // Clear.
        Dimension D = this.getSize();

        javaWidth = D.width;
        g.setColor (Color.white);
        g.fillRect (0, 0, D.width, D.height);

        AffineTransform savedTransform = g2.getTransform ();

        if (roadControl != null) {
            //System.out.println("Draw Road");
            roadControl.draw(g2, D);
        }

        if (carSim != null) {
            carSim.draw(g2, D, g);
        }

        g2.setTransform (savedTransform);
        // Top msg.
        g.setColor (Color.black);

        g.drawString (topMessage, 20, 30);

        avgSpeed = sumSpeeds / currNumCars;

        g.drawString (avgCarMessage, 100, 30);
        g.drawString (numCarMessage, 100, 50);
        g.drawString (percentSpeederMsg, 100, 70);

    }

    ////////////////////////////////////////////////////////////////////////
    // Animation

    void reset () {
        //setScene ();
        clearMetrics();
        roadControl.removeCars();

        // Must add boundaries only after setScene()
        Dimension D = this.getSize();
        thisTime = 0;
        nextWaitTime = 0;

        //set first car...
        addNewCar(0);

        // This must follow setScene() and sensors.
        setCar ();
        carSim.resetClock();
        // Start the animation.
        isPaused = false;
        stopAnimationThread ();
        this.repaint ();
    }

    void clearMetrics() {

        currNumCars = 1;
        avgSpeed = 0;
        sumSpeeds = 0;
    }
    void setCar () {
        //creates road
        //calls getCars() from road
        //creates sim from car list
        //creates controllers from car list
        cars = roadControl.getCars();
        carSim = new SmartCarSimulator(cars, roadControl, numCarColors, SPEEDTRANSLATION);

    }



    void stopAnimationThread () {
        if (currentThread != null) {
            currentThread.interrupt ();
            currentThread = null;
        }
    }


    void go () {
        if (isPaused) {
            isPaused = false;
            return;
        }

        stopAnimationThread ();    // To ensure only one thread.

        currentThread = new Thread () {
            public void run () {
                animate ();
            }

        };
        currentThread.start();
    }

    void pause () {
        isPaused = true;
    }


    void animate () {
        while (true) {

            if (! isPaused) {
                boolean done = nextStep ();
                if (done) {
                    System.out.println ("DONE!");
                    break;
                }
            }
            double time = carSim.getTime() / (200 / sleeptime) ;
            topMessage = "Time: " + df.format(time);
            numCarMessage = "# of Cars: " + currNumCars;
            avgCarMessage = "Avg Speed: " + df.format(SPEEDTRANSLATION * avgSpeed) + " mph";
            percentSpeederMsg = df.format(percentSpeeders) + "% speeders";

            if (roadControl.getNumCars() < maxNumCars) {
                addNewCar(time);

            }
            this.repaint ();

            try {
                Thread.sleep (sleeptime);
            } catch (InterruptedException e) {
                break;
            }
        } //endwhile

        //topMessage = "Time: " + df.format(carSim.getTime());
        this.repaint ();
    }


    public void addNewCar(double time) {
        if (time >= thisTime + nextWaitTime) {
            double speed = 0;
            SmartCar newCar;
            if (isSpeeder >= 1) {
                speed = random.uniform(MINSPEED, SPEEDLIMIT);
                newCar = new SmartCar((int)random.uniform(1, numLanes), initTheta, speed, roadControl, DEBUG, false, numCarColors);
            } else {
                speed = random.uniform(SPEEDLIMIT + 1.0, MAXSPEED);
                newCar = new SmartCar((int)random.uniform(1, numLanes), initTheta, speed, roadControl, DEBUG, true, numCarColors);
                numofSpeeders++;
                percentSpeeders = 100*(numofSpeeders/currNumCars);
            }
            roadControl.add(newCar);
            nextWaitTime = random.uniform(minWaitTime, maxWaitTime);
            thisTime = time;
            currNumCars++;
            sumSpeeds += speed;

            isSpeeder = random.uniform(0, 2);
        }

    }


    boolean nextStep () {
        // In manual mode, the sliders set the values of contro11, 2.
        // In auto mode, the code in the controller does.

        // foreach car...
        //carControl.move ();
        for (SmartCar thiscar : cars) {
            thiscar.move();

        }



        //checkControls ();


        // Now that the controls are known, apply them and
        // get the new position.
        //
        //pass cars to next step
        carSim.nextStep (delT);
        for (SmartCar i : carSim.removelist) {
            roadControl.remove(i);
            //System.out.println("Remove: "+i);
        }
        carSim.removelist.clear();
        //remove car if goes too far

        return false;
    }


    JPanel makeControlPanel () {
        JPanel panel = new JPanel ();
        panel.add (new JLabel ("  "));
        JButton resetB = new JButton ("Reset");
        resetB.addActionListener (
        new ActionListener () {
            public void actionPerformed (ActionEvent a) {
                reset ();
            }
        }
        );
        panel.add (resetB);

        panel.add (new JLabel ("  "));
        JButton goB = new JButton ("Go");
        goB.addActionListener (
        new ActionListener () {
            public void actionPerformed (ActionEvent a) {
                go ();
            }
        }
        );
        panel.add (goB);

        panel.add (new JLabel ("  "));
        JButton pauseB = new JButton ("Pause");
        pauseB.addActionListener (
        new ActionListener () {
            public void actionPerformed (ActionEvent a) {
                pause ();
            }
        }
        );
        panel.add (pauseB);


        panel.add (new JLabel ("  "));
        JButton quitB = new JButton ("Quit");
        quitB.addActionListener (
        new ActionListener () {
            public void actionPerformed (ActionEvent a) {
                System.exit(0);
            }
        }
        );
        panel.add (quitB);

        return panel;
    }

    public void loadController() {

        roadControl = new Road(SPEEDLIMIT, numLanes, laneWidth, DEBUG, this);

    }

    void makeFrame () {
        JFrame frame = new JFrame ();
        frame.setResizable(false);
        frame.setSize (1000, 700);
        frame.setTitle ("Traffic GUI and Simulator");
        Container cPane = frame.getContentPane();
        cPane.add (makeControlPanel(), BorderLayout.SOUTH);
        cPane.add (this, BorderLayout.CENTER);
        loadController();
        frame.setVisible (true);
    }

    ////////////////////////////////////////////////////////////////////////
    // Main

    public static void main (String[] argv) {

        TrafficSim gui = new TrafficSim ();

        gui.makeFrame ();
    }

}
