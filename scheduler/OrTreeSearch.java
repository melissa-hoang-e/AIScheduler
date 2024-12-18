import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Vector;

public class OrTreeSearch {
    private Constraints consts;
    private ParsedStructure s;
    private int assignmentLen;
    private HashSet<Integer> partAssigns;
    private boolean wasPreviouslyStarted;
    private int[] partAssignment;

    public OrTreeSearch(ParsedStructure struct, Constraints c, int[] partAssignment, HashSet<Integer> pa) {
        s = struct;
        consts = c;
        this.partAssignment = partAssignment;
        this.wasPreviouslyStarted = false;
        this.partAssigns = pa;
    }

    private void initializeSearch(Vector<OrTreeNode> fleaf) {
        int[] startAssignment = new int[s.getGames().size() + s.getPractices().size()];
        startAssignment = partAssignment;
        assignmentLen = startAssignment.length;

        // create the starting node and add it to fleaf
        OrTreeNode startingNode = new OrTreeNode();
        startingNode.currentAssignment = startAssignment; // assign empty for start
        startingNode.gpid = 0; // set to 0 for first index

        fleaf.add(startingNode);
        this.wasPreviouslyStarted = true;
    }

    private boolean checkAllFilled(OrTreeNode n) {
        // if gpid is equal to length of assignment array +1 then it is filled
        return n.gpid == assignmentLen;
    }

    private boolean checkIfIsPartAssign(int gpid) {
        // return true if gpid is a part assign
        return partAssigns.contains(gpid + 1);
    }

    private int[] makeDeepCopy(int[] current) {
        int[] copy = new int[current.length];
        for (int i = 0; i < copy.length; i++)
            copy[i] = current[i];

        return copy;
    }

    private void addNewChildren(Vector<OrTreeNode> fleaf, int slotToAssign, int[] currentAssign, int currentgpid) {
        OrTreeNode child = new OrTreeNode(); // create new child
        int[] childAssign = makeDeepCopy(currentAssign); // create copy of current assign
        childAssign[currentgpid] = slotToAssign; // assign slot
        child.currentAssignment = childAssign; // set child's assignment array
        child.gpid = currentgpid + 1; // inc gpid for child
        fleaf.add(child); // add child to fleaf
    }

    private boolean checkIfFull(int[] assignment) {
        for (int i = 0; i < assignment.length; i++) {
            if (assignment[i] == 0)
                return false;
        }
        return true;
    }

    public Fact searchProcess(Vector<OrTreeNode> fleaf) {
        // to make sure we can save the state of fleaf or start properly if this is the
        // first time running
        if (!wasPreviouslyStarted)
            initializeSearch(fleaf);

        // use to check if a gpid is a game, else is a practice
        int numOfGames = s.getGames().size();

        // for looking at the next node from the queue
        OrTreeNode currentNode = null;

        // while there are leaves to search over continue search of OrTree
        while (fleaf.size() > 0) {

            currentNode = fleaf.lastElement(); // get next element from fleaf (queue)
            fleaf.remove(currentNode); // remove currentFact from fleaf

            int[] currentAssignment = currentNode.currentAssignment; // get current assignment of pending fact
            boolean constsMet = consts.checkHardConstraints(currentAssignment); // check if it meets hard constraints

            // if the hard constraints haven't been met just ignore and don't add any
            // children to the queue
            if (constsMet) {

                // first check if all games and practices have a slot assigned
                boolean completeFact = checkIfFull(currentNode.currentAssignment);
                int currentgpid = currentNode.gpid; // get the current gpid

                // if yes then ready this fact for optimization, and we don't have to add
                // anymore
                if (completeFact) {
                    Fact fact = new Fact(currentNode.currentAssignment);
                    return fact;

                    // else create new children from the current node to be added to search
                    // else check if current gpid to look at is a game or practice
                    // if gpid < numOfGames then it has to be a game, else is a practice
                }

                // if the slot is not zero then is a part assignment we don't want changed
                if (checkIfIsPartAssign(currentgpid)) {
                    currentNode.gpid = currentgpid + 1;
                    fleaf.add(currentNode);

                } else if (currentgpid < numOfGames) {

                    Vector<GameSlot> gs = s.getGameSlots();
                    for (GameSlot g : gs) {
                        int gameSlot = g.getId(); // assign to slot
                        addNewChildren(fleaf, gameSlot, currentAssignment, currentgpid); // add child to fleaf
                    }
                } else {

                    Vector<PracSlot> ps = s.getPracticeSlots();
                    for (PracSlot p : ps) {
                        int pracSlot = p.getId();
                        addNewChildren(fleaf, pracSlot, currentAssignment, currentgpid);
                    }
                }
            }
        }
        // if null is returned then that means it has exhausted the search
        return null;
    }

}