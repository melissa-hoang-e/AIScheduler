import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

public class OptOrTree {

    private Constraints consts;
    private ParsedStructure s;
    private HashSet<Integer> partAssigns;
    private int assignSize;

    public OptOrTree(Constraints consts, ParsedStructure s, HashSet<Integer> pa) {
        this.consts = consts;
        this.s = s;
        this.partAssigns = pa;

    }

    private boolean checkIfFull(int[] assignment) {
        for (int i = 0; i < assignment.length; i++) {
            if (assignment[i] == 0)
                return false;
        }
        return true;
    }

    private boolean checkIfInPartAssign(int i) {
        return partAssigns.contains(i + 1);
    }

    private int[] makeDeepCopy(int[] current) {
        int[] copy = new int[current.length];
        for (int i = 0; i < copy.length; i++)
            copy[i] = current[i];

        return copy;
    }

    private OrTreeNode possibleChild(int slotToAssign, int[] currentAssign, int currentgpid) {
        OrTreeNode child = new OrTreeNode(); // create new child
        int[] childAssign = makeDeepCopy(currentAssign); // create copy of current assign
        childAssign[currentgpid] = slotToAssign; // assign slot
        child.currentAssignment = childAssign; // set child's assignment array
        child.gpid = currentgpid + 1; // copy gpid over
        return child;
    }

    private void tryPossibleChildrenForGameSlots(Vector<OrTreeNode> fleaf, OrTreeNode currentNode) {
        Vector<GameSlot> gs = s.getGameSlots();
        for (GameSlot g : gs) {
            int gameSlot = g.getId();
            OrTreeNode c = possibleChild(gameSlot, currentNode.currentAssignment, currentNode.gpid);
            boolean passes = consts.checkHardConstraints(c.currentAssignment);

            if (passes) {
                // System.out.println("passed and added to
                // orTree:"+Arrays.toString(c.currentAssignment));
                fleaf.add(c);
            }
        }
    }

    private void tryPossibleChildrenForPracticeSlots(Vector<OrTreeNode> fleaf, OrTreeNode currentNode) {
        Vector<PracSlot> ps = s.getPracticeSlots();
        for (PracSlot p : ps) {
            int pracSlot = p.getId();
            OrTreeNode c = possibleChild(pracSlot, currentNode.currentAssignment, currentNode.gpid);
            boolean passes = consts.checkHardConstraints(c.currentAssignment);

            if (passes) {
                // System.out.println("passed and added to
                // orTree:"+Arrays.toString(c.currentAssignment));
                fleaf.add(c);
            }
        }
    }

    public Fact optimizedSearch(Fact f) {

        Vector<OrTreeNode> fleaf = new Vector<>();
        OrTreeNode startingNode = new OrTreeNode();

        int[] toOpt = f.getAssignment();
        int[] startAssignment = new int[s.getGames().size() + s.getPractices().size()];

        assignSize = startAssignment.length;

        int numOfGames = s.getGames().size();

        startingNode.currentAssignment = startAssignment;
        startingNode.gpid = 0;

        fleaf.add(startingNode);

        OrTreeNode currentNode;

        while (fleaf.size() > 0) {
            currentNode = fleaf.lastElement(); // get next element from fleaf (queue)
            fleaf.remove(currentNode); // remove currentFact from fleaf

            int currentgpid = currentNode.gpid;

            // this means it is fully assigned and can pass hard constraints
            boolean prePass = consts.checkHardConstraints(currentNode.currentAssignment);
            if (currentgpid == assignSize && prePass) {
                Fact optF = new Fact(currentNode.currentAssignment);
                // System.out.println("RETURNED!!!");
                return optF;
            }
            currentNode.currentAssignment[currentgpid] = toOpt[currentgpid]; // set the

            // if is a partAssign we need it to pass regardless
            boolean passes;
            if (checkIfInPartAssign(currentgpid))
                passes = true;
            else
                passes = consts.checkHardConstraints(currentNode.currentAssignment);

            if (passes) {
                boolean isFull = checkIfFull(currentNode.currentAssignment);

                if (isFull) {
                    Fact optF = new Fact(currentNode.currentAssignment);
                    return optF;
                } else {
                    currentNode.gpid = currentgpid + 1;
                    fleaf.add(currentNode);
                }

                // if it doesn't pass then do random values for THAT spot
            } else {
                if (currentgpid < numOfGames) {
                    tryPossibleChildrenForGameSlots(fleaf, currentNode);
                } else {
                    tryPossibleChildrenForPracticeSlots(fleaf, currentNode);
                }
            }
        }

        System.out.println("Optimized OrTree fully exhausted!");
        Fact faultyFact = new Fact(s);
        return faultyFact;

    }
}