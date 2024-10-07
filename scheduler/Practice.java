import java.util.Vector;

// Practices to be assigned

public class Practice {

    // Practices to be assigned
    // ex. CSMA U13T3 DIV 01 PRC 01
    public final Game game;
    private final int prcId;

    // index of the game/practice to assign
    public final int id;

    // part assign to a slot
    private PracSlot partAssign;

    private boolean isOpn = false;

    public Practice(int id, Game game, int prcId) {
        this.id = id;
        this.game = game;
        this.prcId = prcId;
    }

    // setters and getters

    public Game getGame() {
        return game;
    }

    public int getId() {
        return id;
    }

    public int getPrcId() {
        return prcId;
    }

    /**
     * Print a practice identifier
     */
    public void printPractice() {
        game.printGame();
        if (isOpn) {
            System.out.print(" OPN " + prcId);
        } else {
            System.out.print(" PRC " + prcId);
        }
    }

    public void setOpn() {
        this.isOpn = true;
    }

    public boolean getOpn() {
        return this.isOpn;
    }
}