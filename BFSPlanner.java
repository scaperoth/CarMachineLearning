// BFSPlanner.java
//
// Author: Rahul Simha
// Jan, 2008
//
// Implements a straightforward Breadth-First Search of
// the state space, starting from the given start-state.
// The overall number of explorations is limited by maxMoves,
// which can be modified for larger searches.
// NOTE: the implementation below is only for illustration, which
// is why we're using a linkedlist for the frontier and visitedStates.
// Obviously, better data structures are available, and will result
// in stronger performance.

import java.util.*;


public class BFSPlanner implements Planner {

    // Limit the total number of expansions.
    static int maxMoves = 100000;

    // The frontier = all those states that have been generated but not
    // yet explored (visited).
    LinkedList<State> frontier;

    // The list of all states that have been visited.
    LinkedList<State> visitedStates;

    // Count # moves.
    int numMoves = 0;
    

    public LinkedList<State> makePlan (PlanningProblem problem, State start)
    {
        // Initialize.
	frontier = new LinkedList<State> ();
	visitedStates = new LinkedList<State> ();
	numMoves = 0;

        // The start node is the first one to place in frontier.
	frontier.add (start);

	while (numMoves < maxMoves) {

            // If nothing to explore, we're done.
	    if (frontier.size() == 0) {
		break;
	    }

	    // Get first node in frontier and expand.
	    State currentState = frontier.removeFirst ();

            // If we're at a goal node, build the solution.
	    if (problem.satisfiesGoal (currentState)) {
		return makeSolution (currentState);
	    }

	    numMoves ++;

	    // Put current state in visited list.
	    visitedStates.add (currentState);

	    // Expand current state (look at its neighbors) and place in frontier.
	    ArrayList<State> neighbors = problem.getNeighbors (currentState);
	    for (State s: neighbors) {
		if ( ! visitedStates.contains (s) ) {  // Need memory to avoid repeats.
		    frontier.addLast (s);
		}
	    }

	    if (numMoves % 100 == 0) {
		System.out.println ("After " + numMoves + ": |F|=" + frontier.size() + "  |V|=" + visitedStates.size());
	    }

	} // endwhile

	System.out.println ("BFS: No solution found after " + numMoves + " moves");
	return null;
    }


    public LinkedList<State> makeSolution (State goalState)
    {
	LinkedList<State> solution = new LinkedList<State> ();
	solution.add (goalState);

        // Start from the goal and work backwards, following
        // parent pointers.
	State currentState = goalState;
	while (currentState.getParent() != null) {
	    solution.addFirst (currentState.getParent());
	    currentState = currentState.getParent();
	}

	System.out.println ("BFS: Solution of length=" + solution.size() + " found with cost=" + goalState.costFromStart + " after " + numMoves + " moves");

	return solution;
    }    

}
