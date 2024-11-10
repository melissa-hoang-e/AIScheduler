import java.util.ArrayList;
import java.util.List;

public class FwertFselectTest {

    private  boolean test;
    public FwertFselectTest(boolean t){
        test = t;
    }

    private void printCurrentFacts(State A, ParsedStructure s){
        List<Fact> currentFacts = A.getFacts();
        for (Fact f : currentFacts){
            f.printFact(s);
        }
    }

    public void test(State A){
        if(!test)
            return;
        ParsedStructure s = A.problemFacts;

        int mutateTimes = 6;
        int crossOverTimes = 4;

        Fselect applyFselect = new Fselect(s);

        int i;
        for (i=0; i < mutateTimes; i++){
            applyFselect.selectMutate(A);
        }
        for (i=0; i < crossOverTimes; i++){
            applyFselect.selectCrossover(A);
        }

        System.out.println("------ BEFORE ------");
        printCurrentFacts(A, s);
        System.out.println("============================");
        System.out.println("============================");
        ArrayList<Pair<Fact,Integer>> rankedPenalties = Rules.ranked(A);

        for (Pair<Fact,Integer> rp : rankedPenalties){
            System.out.println("Ranking is: " + rp.getSecond());
        }

        System.out.println("------ AFTER ------");

        applyFselect.selectDelete(A, 40);

        // re calc rankings
        rankedPenalties = Rules.ranked(A);
        for (Pair<Fact,Integer> rp : rankedPenalties){
            System.out.println("Ranking is: " + rp.getSecond());
        }

    }
}
