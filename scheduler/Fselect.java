import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.*;

public class Fselect{

    Rules extensionRules;
    private final double acceptableRange = 0.3;
    private final Random rand = new Random();

    public Fselect(ParsedStructure s){
        extensionRules = new Rules(s);
    }

    public void selectDelete(State A, int threshold){

        ArrayList<Pair<Fact,Integer>> rankedPenalties = Rules.ranked(A);
        int sizeRP = rankedPenalties.size();

        // choosing 20% of these
        int deletionCutoff = (int)(sizeRP * 0.2);
        int index = sizeRP - 1;
        int deleted = 0;

        // remove facts that are above threshold and within 20% target
        while (deleted < deletionCutoff){
            Pair<Fact, Integer> p = rankedPenalties.get(index);
            Fact f = p.getFirst();
            int currentRank = p.getSecond();
            if (currentRank >= threshold) {
                extensionRules.delete(A, f);
                deleted++;
            }
            index--;
        }
    }

    public void selectMutate(State A){

        // an array of facts whose penalties are ranked. <-- need to delete the corresponding penalty value too though **
        ArrayList<Pair<Fact,Integer>> rankedMutatePenalties = Rules.ranked(A);

        int rankedSize = rankedMutatePenalties.size();
        int acceptable = (int)(rankedSize * acceptableRange) + 1; // to always select at least 1

        System.out.println("acceptable is "+acceptable);
        int chosen = rand.nextInt(acceptable);

        //the individual with the lowest penalty value (since ranked is organized from lowest to highest penalty value)
        Fact parentMutation  = rankedMutatePenalties.get(chosen).getFirst();

        extensionRules.mutate(parentMutation, A);
    }

    public void selectSwapMutate(State A){

        //an array of facts whose penalties are ranked.
        ArrayList<Pair<Fact,Integer>> rankedSMPenalties = Rules.ranked(A);

        int rankedSize = rankedSMPenalties.size();
        int acceptable = (int)(rankedSize * acceptableRange) + 1; // to always select at least 1

        int chosen = rand.nextInt(acceptable);

        //the individual with the lowest penalty value (since ranked is organized from lowest to highest penalty value)
        Fact parent = rankedSMPenalties.get(chosen).getFirst();

        extensionRules.swapMutate(parent, A);
    }

    //changed as per the feedback, no longer choosing the two with the lowest pen values as parents
    public void selectCrossover(State A){

        int chosen1, chosen2;

        // an array of facts whose penalties are ranked.
        ArrayList<Pair<Fact,Integer>> rankedSMPenalties = Rules.ranked(A);

        //figuring out how many facts there are
        int rankedSize = rankedSMPenalties.size();

        // cutoff point of facts to choose
        int acceptable = (int)(rankedSize * acceptableRange) + 1; // +2 to always select at least 2


        // find range values such that c1 != c2
        do {
            chosen1 = rand.nextInt(Math.max(acceptable, rankedSize));
            chosen2 = rand.nextInt(Math.max(acceptable, rankedSize));
        } while (chosen1 == chosen2);

        // getting the parents for Crossover
        Fact parent1 = rankedSMPenalties.get(chosen1).getFirst();
        Fact parent2 = rankedSMPenalties.get(chosen2).getFirst();

        extensionRules.crossOver(parent1, parent2, A);

    }
    public void selectRandom(State A){
//        ArrayList<Pair<Fact,Integer>> rankedRandomPenalties = new ArrayList<>();
//
//        //an array of facts whose penalties are ranked.
//        rankedRandomPenalties = Rules.ranked(A);

        Constraints consts = A.getConsts();
        HashSet<Integer> partAssign = A.getPartAssigns();

        // predetermined amount of times to call or-tree
        int max = 20;
        int min = 5;
//        int N= extensionRules.random(min, max);

        int N = 5;
        ArrayList<Fact> initFacts = new ArrayList<>();

        Fact startingFact = new Fact(A.problemFacts);
        int[] partAssignmentArr = startingFact.getAssignment();

        OrTreeSearch initOrTree = new OrTreeSearch(A.problemFacts,consts,partAssignmentArr,partAssign);
        Vector<OrTreeNode> fleaf = new Vector<>();

        while (initFacts.size() < N){
            Fact f = initOrTree.searchProcess(fleaf);
            if (f == null) break;
            initFacts.add(f);

        }

        for (Fact f : initFacts){
            A.getFacts().add(f);
        }

    }

}
