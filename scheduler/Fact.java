import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;


// Fact of the set-based model

public class Fact {

    // The list of game and practices assignment by index
    // ex. assignment[1] = 2 means games/prac[1] is assigned to game/pracslots[2]
    // unassigned games and practices will have gameAssign[1] = 0

    private int[] assignment;


    public Fact(ParsedStructure s) {

        // initialize the assignment to 0s with length of games + practices
        assignment = new int[s.getGames().size() + s.getPractices().size()];

        // assign those assignments specified in partAssign
        List<Map.Entry<Integer, Integer>> partAssignList = s.getPartAssign();

        for (int i = 0; i < partAssignList.size(); i++) {
            int gpId = partAssignList.get(i).getKey();
            int slotId = partAssignList.get(i).getValue();
            set1Assign(gpId, slotId);
        }
    }

    public Fact(int[] assignment) {
        this.assignment = assignment;
    }


    public void set1Assign(int gpId, int slotId) {
        assignment[gpId - 1] = slotId;
    }

    public int[] getAssignment() {
        return assignment;
    }

    public int getSlotForAssignment(int gpId) {
        return assignment[gpId - 1];
    }

    // For testing
    public void printFact(ParsedStructure s) {
        Vector<Game> games = s.getGames();
        Vector<GameSlot> gameSlots = s.getGameSlots();
        for (int i = 0; i < games.size(); i++) {
            games.get(i).printGame();
            System.out.print(" : ");
            if (assignment[i] == 0) {
                System.out.println("$");
            } else {

                System.out.print((assignment[i] - 1)+" ");
                gameSlots.get(assignment[i] - 1).printGameSlot();
            }
        }

        Vector<Practice> practices = s.getPractices();
        Vector<PracSlot> pracSlots = s.getPracticeSlots();
        for (int i = 0; i < practices.size(); i++) {
            practices.get(i).printPractice();
            System.out.print(" : ");
            if (assignment[i + games.size()] == 0) {
                System.out.println("$");
            } else {
                pracSlots.get(assignment[i + games.size()] - 1 - gameSlots.size()).printPracSlot();
            }
        }
    }
}
