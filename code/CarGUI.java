// CarGUI.java
//
// Author: Rahul Simha
// Jan, 2008.
//
// To execute:
//   java CarGUI manual     
// or
//   java CarGUI auto
//
// This file has the GUI code. CarSimulator and CarController
// are interfaces. A simulator simulates physical reality (motion),
// while a controller provides automatic control. There is also
// a SensorPack to provide some input to a controller.
//
// NOTE ABOUT COORDINATES: All the control code will use standard
// Cartesian coordinates with the origin in the lower-left corner.
// The GUI code converts to Java's coordinates where necessary.


import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;



public class CarGUI extends JPanel {

    // These are the control "variables" - the meanings differ in each app.
    // For example, in the constant-velocity Dubin car, they are velocities.
    double control1, control2;

    // Initial (x,y) location and orientation.
    double initX=50, initY=50, initTheta=0;

    // Desired end location and attributes at the time of arrival.
    double endX=550, endY=initY, endTheta=0, endV;

    // The GUI uses a simulator and a controller.
    CarSimulator carSim = null;
    CarController carControl;

    // Rectangular axis-aligned obstacles.
    ArrayList<Rectangle2D.Double> obstacles = new ArrayList<Rectangle2D.Double>();

    // Sensors - an instance of SensorPack (created later).
    SensorPack sensorPack = null;
    
    // The time step. 0.1 is a large value. We might reduce it
    // later and also reduce the sleeptime.
    double delT = 0.1;

    // There are two outputs: the time taken, and the amount of 
    // time spent touching an obstacle.
    int numObstacleHits = 0;

    // Display tracks, if desired.
    ArrayList<Point2D.Double> trackPoints = new ArrayList<Point2D.Double>();

    // Animation stuff.
    Thread currentThread;
    boolean isPaused = false;

    // GUI stuff.
    boolean useSliders = true;
    String[] scenes = {"Scene 1", "Scene 2", "Scene 3", "Scene 4", "Scene 5"};
    String[] cars = {"Unicycle", "Unicycle-acc", "Simple", "Simple-acc", "Dubin", "Dubin-acc"};
    String[] grids = {"no grid", "grid-50", "grid-100"};
    String[] tracks = {"no tracks", "tracks"};
    String[] accuracyLevels = {"low-accuracy","high-accuracy"};
    String[] sensors = {"Ground-truth", "Basic-sensor"};
    Color gridColor = Color.lightGray;
    boolean isAccurate = false;
    
    JComboBox sceneBox, carBox, gridBox, trackBox, accuracyBox, sensorBox;
    JTextField xField, yField, thetaField, vField;
    JSlider slider1, slider2;
    String topMessage = "";
    Font msgFont = new Font ("Serif", Font.PLAIN, 15);
    DecimalFormat df = new DecimalFormat ("##.##");
    JTextField controllerField;
    

    ////////////////////////////////////////////////////////////////////////
    // Drawing

    public void paintComponent (Graphics g)
    {
        super.paintComponent (g);

        Graphics2D g2 = (Graphics2D) g;

        // Clear.
        Dimension D = this.getSize();
        g.setColor (Color.white);
        g.fillRect (0,0, D.width,D.height);
        
        // Grid.
        int gridSize = -1;
        String gridStr = (String) gridBox.getSelectedItem ();
        if (gridStr.equals ("grid-50")) {
            gridSize = 50;
        }
        else if (gridStr.equals ("grid-100")) {
            gridSize = 100;
        }
        if (gridSize > 0) {
            g.setColor (gridColor);
            for (int x=0; x<=D.width; x+=gridSize) {
                g.drawLine (x,0, x,D.height);
            }
            for (int y=D.height; y>=0; y-=gridSize) {
                g.drawLine (0,y, D.width,y);
            }
        }
        
        // Target.
        g.setColor (Color.green);
        int drawX = (int)endX - 5;
        int drawY = (int)endY + 5;
        g.fillOval (drawX,D.height-drawY, 10,10);

        // Obstacles.
        g.setColor (Color.red);
        for (Rectangle2D.Double R: obstacles) {
            drawX = (int) R.x;
            drawY = (int) R.y;
            g.fillRect (drawX,D.height-drawY, (int)R.width, (int)R.height);
        }
        
        // Tracks.
        String trackStr = (String) trackBox.getSelectedItem ();
        if ((trackStr.equals ("tracks")) && (trackPoints.size() > 1)) {
            g.setColor (Color.orange);
            Point2D.Double p = trackPoints.get(0);
            for (int i=1; i<trackPoints.size(); i++) {
                Point2D.Double q = trackPoints.get(i);
                int x1 = (int) p.x;
                int y1 = (int) p.y;
                int x2 = (int) q.x;
                int y2 = (int) q.y;
                g.drawLine (x1, D.height-y1, x2, D.height-y2);
                p = q;
            }
        }

        AffineTransform savedTransform = g2.getTransform ();

        // The car draws itself (possibly using an affine transform).
        if (carSim != null) {
            carSim.draw (g2, D);
        }

        if (carControl != null) {
            carControl.draw (g2, D);
        }

        g2.setTransform (savedTransform);

        // Sensors
        if (sensorPack != null) {
            sensorPack.draw (g2, D);
        }
        

        // Top msg.
        g.setColor (Color.black);
        g.drawString (topMessage, 20, 30);
    }
    

    ////////////////////////////////////////////////////////////////////////
    // Animation

    void reset ()
    {
        numObstacleHits = 0;

	// We'll add track points starting from the initial location.
        trackPoints = new ArrayList<Point2D.Double> ();
        trackPoints.add (new Point2D.Double (initX, initY));

        setScene ();

        // Must add boundaries only after setScene()
        Dimension D = this.getSize();
        obstacles.add (new Rectangle2D.Double(0,2,D.width,10)); // bottom
        obstacles.add (new Rectangle2D.Double(0,D.height+8,D.width,10)); //top
        obstacles.add (new Rectangle2D.Double(D.width-2,D.height,10,D.height));//right
        obstacles.add (new Rectangle2D.Double(-8,D.height,10,D.height));//left

	String sensorStr = (String) sensorBox.getSelectedItem ();
	if (sensorStr.equals("Ground-truth")) {
	    sensorPack = new GroundTruthSensor ();
	}
	else if (sensorStr.equals("Basic-sensor")) {
	    sensorPack = new BasicSensorPack ();
	}
        sensorPack.init (initX, initY, initTheta, obstacles);
	sensorPack.doSensing (initX, initY, initTheta, 0);

        // This must follow setScene() and sensors.
        setCar ();
        
        String accStr = (String) accuracyBox.getSelectedItem ();
        if (accStr.equals ("high-accuracy")) {
            isAccurate = true;
        }
        else {
            isAccurate = false;
        }
        
	// Start the animation.
        isPaused = false;
        stopAnimationThread ();
        this.repaint ();
    }
    

    void stopAnimationThread ()
    {
        if (currentThread != null) {
            currentThread.interrupt ();
            currentThread = null;
        }
    }
    


    void setScene ()
    {
        obstacles = new ArrayList<Rectangle2D.Double>();
        String sceneStr = (String) sceneBox.getSelectedItem ();
        if (sceneStr.equals("Scene 1")) {
            try {
                endX = Double.parseDouble (xField.getText());
                endY = Double.parseDouble (yField.getText());
            }
            catch (NumberFormatException e) {
                endX = 550;
                endY = 50;
            }
        }
        else if (sceneStr.equals("Scene 2")) {
            endX = 50;
            endY = 250;
        }
        else if (sceneStr.equals("Scene 3")) {
            endX = 500;
            endY = 50;
            obstacles.add (new Rectangle2D.Double(150,100,100,100));
        }
        else if (sceneStr.equals("Scene 4")) {
            endX = 290;
            endY = 350;
            obstacles.add (new Rectangle2D.Double(50,400,200,150));
            obstacles.add (new Rectangle2D.Double(100,200,200,150));
            obstacles.add (new Rectangle2D.Double(320,450,50,400));
        }
        else if (sceneStr.equals("Scene 5")) {
            // We'll ignore end point for now.
            endX = -100;
            endY = -100;
            obstacles.add (new Rectangle2D.Double(100,200,100,100));
            obstacles.add (new Rectangle2D.Double(200,200,80,50));
            obstacles.add (new Rectangle2D.Double(280,200,100,100));
        }
        else {
            System.out.println ("ERROR: scene choice");
            System.exit(0);
        }

        xField.setText (""+endX);
        yField.setText (""+endY);
        thetaField.setText (""+endTheta);
        vField.setText (""+endV);
        
    }
    

    void setCar ()
    {
        String carStr = (String) carBox.getSelectedItem ();
        if (carStr.equals ("Unicycle")) {
            carSim = new SimpleCarSimulator (false, true);
        }
        else if (carStr.equals ("Unicycle-acc")) {
            carSim = new SimpleCarSimulator (true, true);
        }
        else if (carStr.equals ("Simple")) {
            carSim = new SimpleCarSimulator (false, false);
        }
        else if (carStr.equals ("Simple-acc")) {
            carSim = new SimpleCarSimulator (true, false);
        }
        else if (carStr.equals ("Dubin")) {
            carSim = new DubinCarSimulator (false);
        }
        else if (carStr.equals ("Dubin-acc")) {
            carSim = new DubinCarSimulator (true);
        }
        carSim.init (initX, initY, initTheta, obstacles);
        if (! useSliders) {
            carControl.init (initX, initY, initTheta, endX, endY, 0, obstacles, sensorPack);
        }
    }
    


    void go ()
    {
        if (isPaused) {
            isPaused = false;
            return;
        }
        
        stopAnimationThread ();    // To ensure only one thread.
        
        currentThread = new Thread () {
                public void run () 
                {
                    animate ();
                }
                
        };
        currentThread.start();
    }
    


    void pause () 
    {
        isPaused = true;
    }
    


    void animate ()
    {
        while (true) {

            if (! isPaused) {
		boolean done = nextStep ();
		if (done) {
		    System.out.println ("DONE!");
		    break;
		}
            }

            topMessage = "Time: " + df.format(carSim.getTime()) + "  #hits=" + numObstacleHits;
            this.repaint ();

            try {
                Thread.sleep (200);
            }
            catch (InterruptedException e){
                break;
            }
        } //endwhile

        topMessage = "Time: " + df.format(carSim.getTime()) + "  #hits=" + numObstacleHits;
        this.repaint ();
    }


    boolean nextStep ()
    {
	// In manual mode, the sliders set the values of contro11, 2.
	// In auto mode, the code in the controller does.
	if (! useSliders) {
	    carControl.move ();
	    control1 = carControl.getControl (1);
	    control2 = carControl.getControl (2);
	    checkControls ();
	}

	// Now that the controls are known, apply them and
	// get the new position.
	carSim.nextStep (control1, control2, delT);
	double x = carSim.getX();
	double y = carSim.getY();
	double v = carSim.getV();
	double theta = carSim.getTheta();
	double t = carSim.getTime();

	// Apply sensors to new position.
	sensorPack.doSensing (x, y, theta, t);

	// Update data.
	trackPoints.add (new Point2D.Double (x, y));
	if (carSim.hitObstacle()) {
	    numObstacleHits ++;
	}
	
	// See if it's reached the end.
	double d = Math.sqrt ((x-endX)*(x-endX) + (y-endY)*(y-endY));
	boolean done = false;
	if (isAccurate) {
	    if ((d < 6) && (Math.abs(theta-endTheta) < 0.1) && (Math.abs(v-endV) < 1.0)) {
		done = true;
	    }
	}
	else {
	    if (d < 25) {
		done = true;
	    }
	}

	return done;
    }


    void checkControls ()
    {
	// Make sure both are within (-10, 10) range.
	if (control1 < -10) {
	    control1 = -10;
	}
	if (control1 > 10) {
	    control1 = 10;
	}
	if (control2 < -10) {
	    control2 = -10;
	}
	if (control2 > 10) {
	    control2 = 10;
	}
    }


    void loadController ()
    {
        if (useSliders) {
            return;
        }
        try {
            String className = controllerField.getText().trim();
            CarController c = (CarController)(Class.forName(className)).newInstance();
            carControl = c;
            topMessage = className + " loaded";
            this.repaint ();
        }
        catch (Exception e) {
            System.out.println (e);
            topMessage = "Could not load or instantiate controller";
        }
    }
    


    ////////////////////////////////////////////////////////////////////////
    // GUI construction

    JPanel makeBottomPanel ()
    {
        JPanel panel = new JPanel ();
        
        panel.setLayout (new GridLayout (2,1));
        JPanel sPanel = makeSetupPanel ();
        sPanel.setBorder (BorderFactory.createTitledBorder ("  Set up  "));
        panel.add (sPanel);
        JPanel cPanel = makeControlPanel ();
        cPanel.setBorder (BorderFactory.createTitledBorder ("  Drive  "));
        panel.add (cPanel);

        return panel;
    }
    

    JPanel makeSetupPanel ()
    {
        JPanel panel = new JPanel ();

	panel.setLayout (new GridLayout(2,1));

	JPanel topPart = new JPanel ();
	JPanel bottomPart = new JPanel ();
	
        carBox = new JComboBox (cars);
        topPart.add (carBox);
        sceneBox = new JComboBox (scenes);
        topPart.add (sceneBox);
        gridBox = new JComboBox (grids);
        topPart.add (gridBox);
        trackBox = new JComboBox (tracks);
        topPart.add (trackBox);
        accuracyBox = new JComboBox (accuracyLevels);
        topPart.add (accuracyBox);
        sensorBox = new JComboBox (sensors);
        topPart.add (sensorBox);
        topPart.add (new JLabel ("  "));
        JPanel smallP = new JPanel ();
        //smallP.setBorder (BorderFactory.createTitledBorder(" Controller class name "));
        JButton loadB = new JButton ("load-controller:");
        smallP.add (loadB);
        loadB.addActionListener (
            new ActionListener () 
            {
                public void actionPerformed (ActionEvent a) 
                {
                    loadController ();
                }
            }
        );
        controllerField = new JTextField (10);
        smallP.add (controllerField);
        topPart.add (smallP);

        bottomPart.add (new JLabel (" End-X:"));
        xField = new JTextField (5);
        xField.setText (""+endX);
        bottomPart.add (xField);
        bottomPart.add (new JLabel (" End-Y:"));
        yField = new JTextField (5);
        yField.setText (""+endY);
        bottomPart.add (yField);
        bottomPart.add (new JLabel (" End-Theta:"));
        thetaField = new JTextField (5);
        thetaField.setText (""+endTheta);
        bottomPart.add (thetaField);
        bottomPart.add (new JLabel (" End-V:"));
        vField = new JTextField (5);
        vField.setText (""+endV);
        bottomPart.add (vField);


	panel.add (topPart);
	panel.add (bottomPart);

        return panel;
    }


    JPanel makeControlPanel ()
    {
        JPanel panel = new JPanel ();
        
        panel.add (new JLabel ("C1: "));
	slider1 = new JSlider (-10, 10, 0);
	slider1.setMajorTickSpacing(5);
	slider1.setMinorTickSpacing(1);
	slider1.setPaintTicks(true);
	slider1.setPaintLabels(true);
	slider1.addChangeListener (
	   new ChangeListener ()
	   {
	       public void stateChanged (ChangeEvent c)
	       {
		   control1 = slider1.getValue ();
	       }
	   }
        );
	panel.add (slider1);

        panel.add (new JLabel ("  C2: "));
	slider2 = new JSlider (-10, 10, 0);
	slider2.setMajorTickSpacing(5);
	slider2.setMinorTickSpacing(1);
	slider2.setPaintTicks(true);
	slider2.setPaintLabels(true);
	slider2.addChangeListener (
	   new ChangeListener ()
	   {
	       public void stateChanged (ChangeEvent c)
	       {
		   control2 = slider2.getValue ();
	       }
	   }
        );
	panel.add (slider2);

        panel.add (new JLabel ("  "));
	JButton resetB = new JButton ("Reset");
	resetB.addActionListener (
	   new ActionListener () {
		   public void actionPerformed (ActionEvent a)
		   {
		       reset ();
		   }
           }
        );
	panel.add (resetB);

        panel.add (new JLabel ("  "));
	JButton goB = new JButton ("Go");
	goB.addActionListener (
	   new ActionListener () {
		   public void actionPerformed (ActionEvent a)
		   {
		       go ();
		   }
           }
        );
	panel.add (goB);

        panel.add (new JLabel ("  "));
	JButton pauseB = new JButton ("Pause");
	pauseB.addActionListener (
	   new ActionListener () {
		   public void actionPerformed (ActionEvent a)
		   {
		       pause ();
		   }
           }
        );
	panel.add (pauseB);


        panel.add (new JLabel ("  "));
	JButton quitB = new JButton ("Quit");
	quitB.addActionListener (
	   new ActionListener () {
		   public void actionPerformed (ActionEvent a)
		   {
		       System.exit(0);
		   }
           }
        );
	panel.add (quitB);
        
        return panel;
    }
    


    void makeFrame ()
    {
        JFrame frame = new JFrame ();
        frame.setSize (1000, 700);
        frame.setTitle ("Car GUI and Simulator");
        Container cPane = frame.getContentPane();
        cPane.add (makeBottomPanel(), BorderLayout.SOUTH);
        cPane.add (this, BorderLayout.CENTER);
	if (! useSliders) {
	    slider1.setEnabled (false);
	    slider2.setEnabled (false);
	}
        frame.setVisible (true);
    }


    ////////////////////////////////////////////////////////////////////////
    // Main

    public static void main (String[] argv)
    {
        if ( (argv == null) || (argv.length != 1) ) {
            System.out.println ("Usage: java CarGUI manual\nOr: java DubinCarGUI auto");
            System.exit (0);
        }
        CarGUI gui = new CarGUI ();
        if (argv[0].equals ("manual")) {
            gui.useSliders = true;
        }
        else {
            gui.useSliders = false;
        }
        gui.makeFrame ();
    }
    

}
