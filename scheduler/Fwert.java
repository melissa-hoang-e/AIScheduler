import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.*;

public class Fwert{

    private Fselect selector;
    private final int FACTLIMIT = 30;

    public  Fwert(ParsedStructure s){
        selector = new Fselect(s);
    }

    public void Fwert(State A, ParsedStructure s){
        int length = A.getFacts().size();
        int sumOfPenalties = 0;

        //sum of all penalty values
        for (Fact f: A.getFacts()) {
            sumOfPenalties =+ Penalty.calcPenalty(f.getAssignment(), A);
        }

        if (length == 0) {
            selector.selectRandom(A);
            if (A.getFacts().size() == 0){
                System.out.println("No valid solution found for the hard constraints.");
                System.exit(0);
            }
        } else if (length > FACTLIMIT) {
            selector.selectDelete(A,sumOfPenalties / length);

        //choosing something a certain percentage of the time
        //source: https://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
        //source: https://stackoverflow.com/questions/5516917/java-do-something-x-percent-of-the-time
        } else {
            Random random = new Random();
            int prob = random.nextInt(10);

            // 50% of the time, Crossover is selected
            if (prob <= 4 && A.getFacts().size()>1) {
                selector.selectCrossover(A);

            // 30% of the time, Mutate is selected
            } else if (prob >= 5 && prob <= 7) {
                selector.selectMutate(A);

            // 20% of the time, SwapMuatate is selected
            } else if (prob >= 8 && prob <= 9) {
                selector.selectSwapMutate(A);
            } else{
                selector.selectMutate(A);
            }
        }
    }
}