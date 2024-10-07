// Practice slot that can be assigned to

public class PracSlot {
    // Game slots: Day, Time, Game max, Game min
    // Practice slots: Day, Time, Practice max, Practice min
    private final String day;
    private final String startTime;
    private final int pracMax;
    private final int pracMin;

    // index of the slot
    private final int id;

    public PracSlot(int id, String day, String startTime, int pracMax, int pracMin){
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.pracMax = pracMax;
        this.pracMin = pracMin;
    }


    // Getters and setters
    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getPracMax() {
        return pracMax;
    }

    public int getPracMin() {
        return pracMin;
    }

    public int getId() {
        return id;
    }

    /**
     * Print a practice slot identifier
     */
    public void printPracSlot(){
        System.out.println(day +", " + startTime);
    }

}