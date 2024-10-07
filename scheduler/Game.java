import java.util.Vector;

// Games to be assigned

public class Game {

    // ex. CSMA U13T3 DIV 01
    public final String body;
    public final String tier;
    public final int div;

    // index of the game/practice to assign
    public final int id;

    public Game(int id, String body, String tier, int div) {
        this.id = id;
        this.body = body;
        this.tier = tier;
        this.div = div;
    }

    // setters and getters
    public String getBody() {
        return body;
    }

    public String getTier() {
        return tier;
    }

    public int getDiv() {
        return div;
    }

    public int getId() {
        return id;
    }

    /**
     * Print a game identifier
     */
    public void printGame() {
        System.out.print(
                body + " " +
                        tier + " " +
                        "DIV " +
                        div);
    }
}