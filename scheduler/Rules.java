import java.util.*;

public class Rules {

    private Random rand;
    private ParsedStructure struct;
    private HashSet<Integer> partAssigned;

    public Rules(ParsedStructure s) {
        this.rand = new Random();
        this.struct = s;
        this.partAssigned = new HashSet<>();
        List<Map.Entry<Integer, Integer>> parts = struct.getPartAssign();

        // add each gpid to a hashset
        for (Map.Entry<Integer, Integer> p : parts) {
            partAssigned.add(p.getKey());
        }
    }

    public static ArrayList<Pair<Fact, Integer>> ranked(State A) {
        int len = A.getFacts().size();
        ArrayList<Fact> _facts = new ArrayList<>();

        ArrayList<Pair<Fact, Integer>> rankings = new ArrayList<>();

        for (Fact f : A.getFacts()) {
            int rank = Penalty.calcPenalty(f.getAssignment(), A);
            if (f == null)
                System.out.println("a fact here in ranking is null!");
            Pair<Fact, Integer> p = new Pair<>(f, rank);
            rankings.add(p);
        }

        // from
        // https://stackoverflow.com/questions/5113006/sort-a-vector-of-custom-objects
        // sort by comparing the penalty for each
        // this might be in ascending order, so might need to switch around
        Collections.sort(rankings, new Comparator<Pair<Fact, Integer>>() {
            @Override
            public int compare(Pair<Fact, Integer> o1, Pair<Fact, Integer> o2) {
                return o1.getSecond().compareTo(o2.getSecond());
            }
        });

        return rankings;

    }

    private boolean checkUnique(Fact curr, State A) {
        int[] currAssign = curr.getAssignment();
        int size = currAssign.length;

        for (int j = 0; j < size; j++)
            if (currAssign[j] == 0)
                return false;

        // loop over each fact
        for (Fact f : A.getFacts()) {
            int count = 0;
            int[] fAssign = f.getAssignment();
            for (int i = 0; i < size; i++) {
                if (currAssign[i] == fAssign[i])
                    count++;
            }

            // check if it is an exact copy
            if (count == size)
                return false;
        }

        return true;
    }

    private int returnNewRandomSpot(int start, int end) {
        int spot;

        // continue finding a new spot to mutate/swap while random spot is in part
        // assign
        do {
            spot = random(start, end);
        } while (partAssigned.contains(spot));
        return spot;
    }

    public void crossOver(Fact f1, Fact f2, State A) {

        Fact crossOverChild = new Fact(struct);

        int size = crossOverChild.getAssignment().length;

        int gORp, slot;
        for (int i = 1; i <= size; i++) {

            // if a part assignment, just copy from f1 since this is the same as f2
            if (partAssigned.contains(i)) {
                slot = f1.getSlotForAssignment(i);
                crossOverChild.set1Assign(i, slot);

                // else choose randomly assignments from f1 and f2
            } else {
                gORp = rand.nextInt(2);

                // choose from f1
                if (gORp == 1) {
                    slot = f1.getSlotForAssignment(i);
                    crossOverChild.set1Assign(i, slot);

                    // choose from f2
                } else {
                    slot = f2.getSlotForAssignment(i);
                    crossOverChild.set1Assign(i, slot);
                }
            }
        }

        // check if it meets the hard constraints
        if (A.getConsts().checkHardConstraints(crossOverChild.getAssignment())) {

            // make sure it is unique
            if (checkUnique(crossOverChild, A))
                A.getFacts().add(crossOverChild);

        } else {
            OptOrTree checkCorrectness = new OptOrTree(A.getConsts(), A.problemFacts, A.getPartAssigns());
            Fact correctedFact = checkCorrectness.optimizedSearch(crossOverChild);
            if (correctedFact == null)
                System.out.println("Null fact added by accident here");

            // make sure it is unique
            if (checkUnique(correctedFact, A))
                A.getFacts().add(correctedFact);
        }

    }

    private Fact copyExistingFact(Fact f) {
        /*
         * create a deep copy of an existing fact
         */
        Fact newFact = new Fact(struct);
        int currentAssignSize = f.getAssignment().length;

        for (int i = 1; i <= currentAssignSize; i++) {
            int slot = f.getSlotForAssignment(i);
            newFact.set1Assign(i, slot);
        }
        return newFact;
    }

    public void swapMutate(Fact f, State A) {
        // System.out.println("Swap mutate on ");
        f.printFact(struct);

        int gp1, gp2;

        // create new facts that are deep copies
        Fact swapMutated = copyExistingFact(f);

        // get size of games
        int gamePortion = struct.getGames().size();

        int assignSize = f.getAssignment().length;

        // start of 1 because gpid's start at 1, and +1 need for size too to include the
        // end id
        gp1 = returnNewRandomSpot(1, assignSize + 1);

        // swap mutate on games if id is within size of games
        if (gp1 <= gamePortion) {
            gp2 = returnNewRandomSpot(1, gamePortion + 1);

            // else swap mutate on practices
        } else {

            // +1 to both to get a correct practice id
            gp2 = returnNewRandomSpot(gamePortion + 1, assignSize + 1);
        }

        // get the slots currently assigned
        int assigned1 = f.getSlotForAssignment(gp1);
        int assigned2 = f.getSlotForAssignment(gp2);

        // do the swap
        swapMutated.set1Assign(gp1, assigned2);
        swapMutated.set1Assign(gp2, assigned1);

        // check if it meets the hard constraints
        if (A.getConsts().checkHardConstraints(swapMutated.getAssignment())) {

            // make sure it is unique
            if (checkUnique(swapMutated, A))
                A.getFacts().add(swapMutated);

        } else {
            OptOrTree checkCorrectness = new OptOrTree(A.getConsts(), A.problemFacts, A.getPartAssigns());
            Fact correctedFact = checkCorrectness.optimizedSearch(swapMutated);
            if (correctedFact == null)
                System.out.println("Null fact added by accident here");

            // make sure it is unique
            if (checkUnique(correctedFact, A))
                A.getFacts().add(correctedFact);
        }

    }

    public void mutate(Fact f, State A) {
        // System.out.println("Mutate on");
        f.printFact(struct);

        int gp;
        int size = f.getAssignment().length;
        Fact mutated = copyExistingFact(f); // create new fact

        // get index of a game or practice
        // start of 1 because gpid's start at 1, and +1 need for size too to include the
        // end id
        gp = returnNewRandomSpot(1, size + 1);

        int gamePortion = struct.getGames().size();
        int gpSlotId, randPos;

        // if is a game (+1 to gamePortion to ensure included in possible game id's)
        if (gp < gamePortion + 1) {
            // get a random game
            int gameslotsize = struct.getGameSlots().size();
            randPos = rand.nextInt(gameslotsize);
            gpSlotId = struct.getGameSlots().get(randPos).getId();

            // else is a practice
        } else {
            // get a random practice
            int pracslotsize = struct.getPracticeSlots().size();
            randPos = rand.nextInt(pracslotsize);
            gpSlotId = struct.getPracticeSlots().get(randPos).getId();
        }

        mutated.set1Assign(gp, gpSlotId); // +1 because of the -1 in setAssign to map to correct index

        // check if it meets the hard constraints
        if (A.getConsts().checkHardConstraints(mutated.getAssignment())) {

            // make sure it is unique
            if (checkUnique(mutated, A))
                A.getFacts().add(mutated);
        } else {
            OptOrTree checkCorrectness = new OptOrTree(A.getConsts(), A.problemFacts, A.getPartAssigns());
            Fact correctedFact = checkCorrectness.optimizedSearch(mutated);
            if (correctedFact == null)
                System.out.println("Null fact added by accident here");

            // make sure it is unique
            if (checkUnique(correctedFact, A))
                A.getFacts().add(correctedFact);
        }
    }

    public void delete(State A, Fact f) {
        A.getFacts().remove(f);
    }

    public int random(int min, int max) {
        // https://www.baeldung.com/java-generating-random-numbers-in-range
        return rand.nextInt(max - min) + min;
    }
}