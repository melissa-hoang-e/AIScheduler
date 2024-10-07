// Game slot that can be assigned to

public class GameSlot{
    // Game slots: Day, Time, Game max, Game min
    private final String day;
    private final String startTime;
    private final int gameMax;
    private final int gameMin;

    // index of the slot
    private final int id;

    public GameSlot(int id, String day, String startTime, int gameMax, int gameMin) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.gameMax = gameMax;
        this.gameMin = gameMin;
    }

    // Getters and setters
    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getGameMax() {
        return gameMax;
    }

    public int getGameMin() {
        return gameMin;
    }

    public int getId() {
        return id;
    }

    /**
     * Print a game slot identifier
     */
    public void printGameSlot() {
        System.out.println(day + ", " + startTime);
    }
}