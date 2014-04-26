
import java.util.*;

/**
 * Interface <code>Planner</code> needs to be implemented by any
 * algorithm that wants to perform state-based planning.
 *
 */

public interface Planner {

    /**
     * The algorithm will be given the problem and a start state, and should
     * return a list of states describing the path from start to goal.
     * The first state needs to be the start state, and the last needs
     * to be the goal state. Every pair of successive states must be
     * "neighboring" states.
     *
     * @param problem a <code>PlanningProblem</code> value
     * @param start a <code>State</code> value
     * @return a <code>LinkedList<State></code> value
     */

    public LinkedList<State> makePlan (PlanningProblem problem, State start);

}
