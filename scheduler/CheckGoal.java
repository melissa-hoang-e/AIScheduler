import java.util.ArrayList;

public class CheckGoal {
    // call fwert --> which calls fselect --> which calls the rules
    // the rules return the updated state --> which we check and loop if goal isn't
    // met
    private Fwert fwert;
    private int iter = 0;

    public CheckGoal(ParsedStructure s) {
        fwert = new Fwert(s);
    }

    private void debug(State A) {
        ArrayList<Fact> current = A.getFacts();
        for (Fact f : current) {
            f.printFact(A.problemFacts);
        }
    }

    public void CheckGoal(Instance instance, State A, ParsedStructure s) {
        int limit = 100;
        while (!instance.goal() && iter < limit) {

            fwert.Fwert(A, s);
            iter++;

        }

        Fact f = instance.getSolution();
        f.printFact(s);

    }
}