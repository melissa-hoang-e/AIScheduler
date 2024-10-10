public class Assigned {
    boolean assigned = false;
    public boolean assignGame(int[] assignment, Game G, GameSlot GS) {
        // games
        int gameID = G.getId();
        if (assignment[gameID] == GS.getId()){
            assigned = true;
        }
        return assigned;
    }

    public boolean assignPractice(int[] assignment, Practice P, PracSlot PS){
        // practices
        int practiceID = P.getId();
        if(assignment[practiceID] == PS.getId()){
            assigned = true;
        }
        return assigned;
    }
}