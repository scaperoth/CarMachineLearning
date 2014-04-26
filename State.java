
import java.util.*;

/**
 * This abstract class represents a generic problem state. The two
 * variables are used in cost-based algorithms such as A*.
 *
 */

public abstract class State {

    /**
     * This is filled in by code specific to the problem.
     */
    public double costFromStart = 0;

    
    /**
     * This is filled in by code specific to the problem.
     */
    public double estimatedCostToGoal = 0;



    /**
     * An implementation must return the parent node in order for
     * an algorithm to build the path backwards from the goal state.
     *
     * @return a <code>State</code> value
     */

    public abstract State getParent ();



    /**
     * Useful in algorithms to check whether we've visited a state before.
     * For continuous-state algorithms, one may need to implement this
     * with some epsilon-distance measure.
     *
     * @param obj an <code>Object</code> value
     * @return a <code>boolean</code> value
     */

    public abstract boolean equals (Object obj);



    /**
     * Useful in debugging.
     *
     * @return a <code>String</code> value
     */

    public abstract String toString ();
}
