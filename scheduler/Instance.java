import java.util.ArrayList;
import java.util.Arrays;

// A search instance of the model

public class Instance {

    // the desired score / penalty of the search
    public static final int GOAL_PEN = 0;

    // the penalty of the current instance
    public int currPenalty;

    // true if final state
    public boolean finalState = false;

    // state of the instance
    private final State state;

    private Fact solution = null;

    /**
     * the search instance given the state
     * 
     * @param state
     */
    public Instance(State state) {

        this.state = state;

    }

    /**
     * check if reach goal
     * 
     * @return true if reach goal
     */
    public boolean goal() {
        // state.getFacts().get(0).printFact(state.problemFacts);
        if (state.getFacts().isEmpty()) {
            currPenalty = Integer.MAX_VALUE;
        } else {
            ArrayList<Pair<Fact, Integer>> ranked = Rules.ranked(state);
            currPenalty = ranked.get(0).getSecond();
        }
        return !state.getFacts().isEmpty() && (currPenalty <= GOAL_PEN || finalState);
    }

    /**
     * get the solution fact
     * 
     * @return solution
     */
    public Fact getSolution() {
        System.out.println("Facts are: ");
        for (Fact f : state.getFacts()) {
            System.out.println(Arrays.toString(f.getAssignment()));
        }
        System.out.println("Number of facts after: " + state.getFacts().size());
        if (state.getFacts().size() != 0) {
            ArrayList<Pair<Fact, Integer>> ranked = Rules.ranked(state);
            currPenalty = ranked.get(0).getSecond();
            solution = ranked.get(0).getFirst();
            System.out.println("====================================");
            System.out.println("Eval-value: " + currPenalty);

        }

        return solution;
    }

    public void printPen() {

    }
}