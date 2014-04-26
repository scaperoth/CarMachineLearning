
import java.util.*;

/**
 * Interface <code>PlanningProblem</code> must be implemented for
 * any problem that is to be included in the planning demo. The problem
 * must also extend JPanel (to draw itself). See MazeProblem as an example.
 * The interface is also used by algorithms, which need to get the
 * start state, for example.
 *
 */

public interface PlanningProblem {

    /**
     * Return the start state.
     *
     * @return a <code>State</code> value
     */

    public State getStartState ();



    /**
     * Once a plan is created, the GUI will give the plan to the "problem"
     * instance, allowing it to interact with "next" buttons.
     *
     * @param plan a <code>LinkedList<State></code> value
     */

    public void setPlan (LinkedList<State> plan);



    /**
     * Method <code>getNeighbors()</code> should return the neighboring
     * states to the given state. Obviously, this is problem dependent
     * and is hence appropriate for the "problem" instance to implement.
     *
     * @param state a <code>State</code> value
     * @return an <code>ArrayList<State></code> value
     */

    public ArrayList<State> getNeighbors (State state);



    /**
     * Does the given state satisfy the goal? Originally, we created a
     * a list of final states, but this doesn't work for some problems
     * like the Arm problem because it's difficult to compute the
     * whole state in a final configuration.
     *
     * @param state a <code>State</code> value
     * @return a <code>boolean</code> value
     */

    public boolean satisfiesGoal (State state);


    /**
     * Method <code>next()</code> is called by the GUI to allow stepping
     * through a plan. This is an opportunity to draw the next state
     * in the plan.
     *
     */

    public void next ();
    

    /**
     * Method <code>drawState()</code> is optional. This can be
     * used by an algorithm to draw the current state, and is useful
     * for debugging.
     *
     * @param state a <code>State</code> value
     */

    public void drawState (State state);

}
