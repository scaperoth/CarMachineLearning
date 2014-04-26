
import java.util.*;
import java.awt.geom.*;
import java.awt.*;


public class TrafficSim implements CarController {


    ArrayList<SmartCar> Cars;

    // Sensors - an instance of SensorPack (created later).
    SensorPack sensorPack = null;

    ArrayList<Rectangle2D.Double> obstacles;
    SensorPack sensors;

    // Rectangular axis-aligned obstacles.
    ArrayList<Rectangle2D.Double> obstacles = new ArrayList<Rectangle2D.Double>();

    // Is the first control an accelerator?
    boolean isAccelModel = false;

    // Animation stuff.
    Thread currentThread;
    boolean isPaused = false;

    public void TrafficSim(ArrayList<Rectangle2D.Double> obstacles) {
        this.obstacles = obstacles;
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

        Road.draw (g2, D);

        sensorPack.draw (g2, D);

        // Top msg.
        g.setColor (Color.black);
        g.drawString (topMessage, 20, 30);
    }




    ////////////////////////////////////////////////////////////////////////
    // Animation

    void reset () {


        // We'll add track points starting from the initial location.
        trackPoints = new ArrayList<Point2D.Double> ();
        trackPoints.add (new Point2D.Double (initX, initY));

        setScene ();

        // Must add boundaries only after setScene()
        Dimension D = this.getSize();

        sensorPack = new BasicSensorPack ();
        sensorPack.init (initX, initY, initTheta);
        sensorPack.doSensing (initX, initY, initTheta, 0);

        // This must follow setScene() and sensors.
        setCar ();


        // Start the animation.
        isPaused = false;
        stopAnimationThread ();
        this.repaint ();
    }


    void setCar ()
    {
        //creates road
        //calls getCars() from road
        //creates sim from car list
        //creates controllers from car list
        
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

            topMessage = "Time: " + df.format(carSim.getTime());
            this.repaint ();

            try {
                Thread.sleep (200);
            } catch (InterruptedException e) {
                break;
            }
        } //endwhile

        topMessage = "Time: " + df.format(carSim.getTime());
        this.repaint ();
    }



    boolean nextStep () {
        // In manual mode, the sliders set the values of contro11, 2.
        // In auto mode, the code in the controller does.

        // foreach car...
        carControl.move ();

        checkControls ();


        // Now that the controls are known, apply them and
        // get the new position.
        //
        //pass cars to next step
        carSim.nextStep (Cars);

        //remove car if goes too far

        return done;
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


    void makeFrame () {
        JFrame frame = new JFrame ();
        frame.setSize (1000, 700);
        frame.setTitle ("Car GUI and Simulator");
        Container cPane = frame.getContentPane();
        cPane.add (makeBottomPanel(), BorderLayout.SOUTH);
        cPane.add (this, BorderLayout.CENTER);
        
        frame.setVisible (true);
    }

    ////////////////////////////////////////////////////////////////////////
    // Main

    public static void main (String[] argv) {

        TrafficSim gui = new TrafficSim ();
       
        gui.makeFrame ();
    }

}
