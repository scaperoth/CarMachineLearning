
import java.util.*;

/**
 * Each instance of <code>PuzzleState</code> describes a single state
 * of the Puzzle problem. 
 *
 * @see State
 */

public class ScaperothUnicycleState extends State {
// Pointer to parent. This needs to be set by the appropriate problem,
    // in this case, in MazeProblem.
    ScaperothUnicycleState parent = null;

    // Size.
    int N=-1;

    // Location.
    int x=-1, y=-1;


    public ScaperothUnicycleState (ScaperothUnicycleState parent, int N, int x, int y)
    {
	this.parent=parent; this.N=N;  this.x=x;  this.y=y;
    }


    public State getParent ()
    {
	return parent;
    }


    public boolean equals (Object obj)
    {
	if (! (obj instanceof ScaperothUnicycleState) ) {
	    return false;
	}
	ScaperothUnicycleState m = (ScaperothUnicycleState) obj;
	if ((m.x==x) && (m.y==y) ) {
	    return true;
	}
	return false;
    }


    public String toString ()
    {
	String str = "MazeState: [N=" + N + ", x=" + x + ", y=" + y + ", cost="+ costFromStart + " est=" + estimatedCostToGoal + "]";
	return str;
    }
}
