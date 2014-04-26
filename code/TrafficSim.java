import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;


public class TrafficSim extends JPanel{
    //traffic details
    int maxNumCars = 1;
    double speedLimit = 5.0;
    int numLanes = 2;
    double laneWidth = 10;

    //SmartCarSimulator carSim = null;
    //Arraylist<SmartCar> cars;
    Road roadControl;

    // Animation stuff.
    Thread currentThread;
    boolean isPaused = false;


    String topMessage = "";

    public void TrafficSim() {
        
    }

    ////////////////////////////////////////////////////////////////////////
    // Drawing

    public void paintComponent (Graphics g) {
        super.paintComponent (g);

        Graphics2D g2 = (Graphics2D) g;

        // Clear.
        Dimension D = this.getSize();
        g.setColor (Color.white);
        g.fillRect (0, 0, D.width, D.height);

        AffineTransform savedTransform = g2.getTransform ();

        if (roadControl != null) {
            roadControl.draw(g2, D);
        }

        //if (carSim != null) {
          //  carSim.draw(g2, D);
        //}


        g2.setTransform (savedTransform);
        // Top msg.
        g.setColor (Color.black);
        
        g.drawString (topMessage, 20, 30);
    }




    ////////////////////////////////////////////////////////////////////////
    // Animation

    void reset () {

        //setScene ();

        // Must add boundaries only after setScene()
        Dimension D = this.getSize();

        // This must follow setScene() and sensors.
        setCar ();

        // Start the animation.
        isPaused = false;
        stopAnimationThread ();
        this.repaint ();
    }


    void setCar () {
        //creates road
        //calls getCars() from road
        //creates sim from car list
        //creates controllers from car list
        //cars = roadControl.getCars();
        //carSim = new SmartCarSimulator(cars);

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

            //topMessage = "Time: " + df.format(carSim.getTime());
            this.repaint ();

            try {
                Thread.sleep (200);
            } catch (InterruptedException e) {
                break;
            }
        } //endwhile

        //topMessage = "Time: " + df.format(carSim.getTime());
        this.repaint ();
    }



    boolean nextStep () {
        // In manual mode, the sliders set the values of contro11, 2.
        // In auto mode, the code in the controller does.

        // foreach car...
        //carControl.move ();

        //checkControls ();


        // Now that the controls are known, apply them and
        // get the new position.
        //
        //pass cars to next step
        //carSim.nextStep (Cars);

        //remove car if goes too far

        return true;
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

    public void loadController(){

        roadControl = new Road(maxNumCars, speedLimit, numLanes, laneWidth);
        //SmartCar newCar = newSmartCar(initx, inity, initTheta);
        //roadControl.add(newCar);
    }

    void makeFrame () {
        JFrame frame = new JFrame ();
        frame.setSize (1000, 700);
        frame.setTitle ("Car GUI and Simulator");
        Container cPane = frame.getContentPane();
        //cPane.add (makeBottomPanel(), BorderLayout.SOUTH);
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
