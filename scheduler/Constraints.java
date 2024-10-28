import java.util.*;

// Constraints-checking information and methods
public class Constraints {

    private ParsedStructure s;

    private Vector<GameSlot> gameslots;
    private Vector<PracSlot> pracslots;
    private Vector<Game> games;
    private Vector<Practice> practices;

    private List<Map.Entry<Integer, Integer>> notcompatible;
    private List<Map.Entry<Integer, Integer>> unwanted;
    private int numGames;
    private int numPracs;
    private int numGameSlots;
    private int numPracSlots;
    private int adminDay;
    private String cmsaU12T1S;
    private String cmsaU13T1S;
    private String cmsaU12T1;
    private String cmsaU13T1;
    private int specialPracticeBooking;

    private HashSet<String> tierCheck;

    private ArrayList<Integer> ut12IDs = new ArrayList<>();
    private ArrayList<Integer> ut13IDs = new ArrayList<>();
    private int specialPracIndex;

    /**
     * The constructor to get those static information
     *
     * @param s
     */
    public Constraints(ParsedStructure s, int adminDay, int specialPractice) {

        this.adminDay = adminDay;
        this.specialPracticeBooking = specialPractice;
        this.s = s;
        // Retrieve information from the parsed structure

        notcompatible = s.getNotcompatible();
        unwanted = s.getUnwanted();

        gameslots = s.getGameSlots();
        pracslots = s.getPracticeSlots();
        games = s.getGames();
        practices = s.getPractices();

        numGames = s.getGames().size();
        numPracs = s.getPractices().size();
        numGameSlots = s.getGameSlots().size();
        numPracSlots = s.getPracticeSlots().size();

        tierCheck = new HashSet<>();
        tierCheck.add("U15");
        tierCheck.add("U16");
        tierCheck.add("U17");
        // tierCheck.add("U18");
        tierCheck.add("U19");

        cmsaU12T1S = "CMSAU12T1S";
        cmsaU13T1S = "CMSAU13T1S";

        cmsaU12T1 = "CMSAU12T1";
        cmsaU13T1 = "CMSAU13T1";

        // Record CMSAU12 gp indices
        for (int i = 0; i < numGames + numPracs; i++) {

            // if is a game
            if (i < numGames) {
                Game g = games.get(i);
                String title = g.getBody() + g.getTier();
                if (title.equals(cmsaU12T1))
                    ut12IDs.add(i);
                // else has to be a practice
            } else {
                Practice p = practices.get(i - numGames);
                Game pg = p.getGame();
                String title = pg.getBody() + pg.getTier();
                if (title.equals(cmsaU12T1))
                    ut12IDs.add(i);
            }
        }

        // Record CMSAU13 gp indices
        for (int i = 0; i < numGames + numPracs; i++) {

            // if is a game
            if (i < numGames) {
                Game g = games.get(i);
                String title = g.getBody() + g.getTier();
                if (title.equals(cmsaU13T1))
                    ut13IDs.add(i);

                // else has to be a practice
            } else {
                Practice p = practices.get(i - numGames);
                Game pg = p.getGame();
                String title = pg.getBody() + pg.getTier();
                if (title.equals(cmsaU13T1))
                    ut13IDs.add(i);
            }
        }

        // search for special practice time
        for (PracSlot ps : pracslots) {
            if (ps.getDay().equals("TU") && ps.getStartTime().equals("18:00")) {
                this.specialPracticeBooking = ps.getId();
                break;
            }
        }

        // search for admin time, 0 if not found
        for (GameSlot gs : gameslots) {
            if (gs.getDay().equals("TU") && gs.getStartTime().equals("11:00")) {
                this.adminDay = gs.getId();
                break;
            }
        }
    }

    public boolean checkGameMax(int[] assignment) {
        for (GameSlot g : gameslots) {
            int count = 0;
            for (int i = 0; i < numGames; i++) {
                int assign = assignment[i];
                if (assign == g.getId())
                    count++;
            }
            if (count > g.getGameMax())
                return false;
        }
        return true;
    }

    public boolean checkPracticeMax(int[] assignment) {
        for (PracSlot p : pracslots) {
            int count = 0;
            for (int i = numGames; i < assignment.length; i++) {
                int assign = assignment[i];
                if (assign == p.getId())
                    count++;
            }
            if (count > p.getPracMax())
                return false;
        }
        return true;
    }

    public boolean checkTimeOverlaps(int a, int b, int[] assignment) {
        // helper function to check if games and or practices are scheduled at the same
        // time
        String timeA = "";
        String timeB = "";
        double a_start, a_end;
        double b_start, b_end;
        int typeA, typeB; // 1 means g, 0 means p

        String dayA = "", dayB = "";
        if (a < numGames) {
            GameSlot gs = gameslots.get(assignment[a] - 1); // need to -1 since we start from 1
            timeA = gs.getStartTime();
            dayA = gs.getDay();
            typeA = 1;

            // Parse string and compute the end time
            double[] time = parseTime(dayA, timeA, typeA);
            a_start = time[0];
            a_end = time[1];
        } else {
            PracSlot ps = pracslots.get(assignment[a] - numGameSlots - 1);
            timeA = ps.getStartTime();
            dayA = ps.getDay();
            typeA = 0;

            double[] time = parseTime(dayA, timeA, typeA);
            a_start = time[0];
            a_end = time[1];
        }
        if (b < numGames) {
            GameSlot gs = gameslots.get(assignment[b] - 1);
            timeB = gs.getStartTime();
            dayB = gs.getDay();
            typeB = 1;

            double[] time = parseTime(dayB, timeB, typeB);
            b_start = time[0];
            b_end = time[1];
        } else {
            PracSlot ps = pracslots.get(assignment[b] - numGameSlots - 1);
            timeB = ps.getStartTime();
            dayB = ps.getDay();
            typeB = 0;

            double[] time = parseTime(dayB, timeB, typeB);
            b_start = time[0];
            b_end = time[1];
        }
        // handle MO game and practice
        if (dayA.equals("MO") && dayB.equals("MO")) {
            return !isOverlap(a_start, a_end, b_start, b_end);
        }
        // handle Friday gp overlaps
        if (dayA.equals("MO") && dayB.equals("FR")) {
            if (typeA == 0)
                return true;
            return !isOverlap(a_start, a_end, b_start, b_end);
        }
        if (dayA.equals("FR") && dayB.equals("MO")) {
            if (typeB == 0)
                return true;
            return !isOverlap(a_start, a_end, b_start, b_end);
        }
        // handle Tuesday Thursday gp overlaps
        if (dayA.equals("TU") && dayB.equals("TU")) {
            return !isOverlap(a_start, a_end, b_start, b_end);
        }
        return true;
    }

    private double[] parseTime(String day, String time, int type) {

        double[] result = new double[2];
        double start;
        double end;

        String[] parts;
        parts = time.split(":");
        String hour = parts[0];

        // TU game
        if (day.equals("TU") && type == 1) {

            String minutes = parts[1];

            if (minutes.equals("30")) {
                start = Double.parseDouble(hour) + 0.5;
            } else {
                start = Double.parseDouble(hour);
            }
            end = start + 1.5;
        }

        // FR practice
        else if (day.equals("FR")) {
            start = Double.parseDouble(hour);
            end = start + 2;
        }

        // MW gp or TU p
        else {
            start = Double.parseDouble(hour);
            end = start + 1;
        }

        result[0] = start;
        result[1] = end;
        return result;
    }

    private boolean isOverlap(double start_a, double end_a, double start_b, double end_b) {
        if (start_a == start_b) {
            return true;
        } else {

            if (start_a < start_b && end_a > start_b) {
                return true;
            }

            if (start_a > start_b && start_a < end_b) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNonCompatible(int[] assignment) {

        for (Map.Entry<Integer, Integer> pair : notcompatible) {

            // index = gpID - 1
            int a = pair.getKey() - 1;
            int b = pair.getValue() - 1;

            // ignore 0, otherwise always failed
            if (assignment[a] == 0 || assignment[b] == 0)
                continue;

            if (assignment[a] == assignment[b])
                return false;
            if (!checkTimeOverlaps(a, b, assignment))
                return false;
        }

        return true;
    }

    public boolean checkUnwanted(int[] assignment) {

        // C4, check that there isn't an unwanted assignment
        for (Map.Entry<Integer, Integer> pair : unwanted) {
            int a = pair.getKey();
            int s = pair.getValue();
            // note: index = gpId - 1, gpID start from 1 !
            if (assignment[a - 1] == s)
                return false;
        }

        return true;
    }

    // all for evening games
    public boolean checkEveningGame(int gID, int gsID) {

        // check if a game starts in the evening
        Game g = games.get(gID);
        if (g.getDiv() < 90)
            return true;

        GameSlot gs = gameslots.get(gsID);
        String startTime = gs.getStartTime();

        String hour_s = startTime.split(":")[0];
        int hour = Integer.parseInt(hour_s);
        return hour >= 18;
    }

    public boolean checkEvening(int[] assignment) {
        for (int i = 0; i < numGames; i++) {
            if (assignment[i] == 0)
                continue;
            if (!checkEveningGame(i, assignment[i] - 1))
                return false;
        }
        return true;
    }

    public boolean checkU15toU19(int[] assignment) {

        // if games in tiers U15 to U19 have the same slot

        for (int i = 0; i < numGames; i++) {

            if (assignment[i] == 0)
                continue;

            String tierA = games.get(i).getTier();
            String justAUXX = "";
            justAUXX += tierA.charAt(0);
            justAUXX += tierA.charAt(1);
            justAUXX += tierA.charAt(2);
            if (!(tierCheck.contains(justAUXX)))
                continue;

            for (int j = 0; j < numGames; j++) {

                if (i == j)
                    continue;
                if (assignment[j] == 0)
                    continue;

                String tierB = games.get(j).getTier();
                String justBUXX = "";
                justBUXX += tierB.charAt(0);
                justBUXX += tierB.charAt(1);
                justBUXX += tierB.charAt(2);
                if (!(tierCheck.contains(justBUXX)))
                    continue;

                if (assignment[i] == assignment[j])
                    return false;

            }
        }

        return true;
    }

    public boolean checkNotOnAdminDay(int[] assignment) {
        // check for games only (very easy check)
        if (adminDay == 0)
            return true;
        for (int i = 0; i < numGames; i++) {
            if (assignment[i] == adminDay)
                return false;
        }
        return true;
    }

    public boolean checkUT12(int[] assignment) {

        // No need to check if no CMSA U12T1 if (specialPracticeBooking == 0) return
        // true;
        if (ut12IDs.isEmpty())
            return true;

        // special practice time

        // Ensure no CMSA UT12 gp assign or overlap to special practice booking
        for (int i : ut12IDs) {

            int assign = assignment[i];
            if (assign == 0)
                continue;

            if (i < numGames) {
                // check overlapping with TU 18:00
                GameSlot gs = gameslots.get(assign - 1);
                String day = gs.getDay();
                String start = gs.getStartTime();

                if (day.equals("TU")) {
                    if (start.equals("17:00") || start.equals("18:30")) {
                        return false;
                    }
                }
            } else {
                if (assignment[i] == specialPracticeBooking) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkUT13(int[] assignment) {
        // No need to check if no CMSA U13T1 if (specialPracticeBooking == 0) return
        // true;
        if (ut13IDs.isEmpty())
            return true;

        for (int i : ut13IDs) {
            int assign = assignment[i];
            if (assign == 0)
                continue;
            if (i < numGames) {
                // check overlapping with TU 18:00
                GameSlot gs = gameslots.get(assign - 1);
                String day = gs.getDay();
                String start = gs.getStartTime();

                if (day.equals("TU")) {
                    if (start.equals("17:00") || start.equals("18:30")) {
                        return false;
                    }
                }
            } else {
                if (assignment[i] == specialPracticeBooking) {
                    return false;
                }
            }
        }
        return true;
    }

    // C3
    public boolean checkGamesAndPracOverlap(int[] assignment) {
        // C12, checking that games and practices belonging to the same league (?)
        // do not have time overlaps between their games and practices
        int i, j;
        for (i = 0; i < assignment.length; i++) {
            if (assignment[i] == 0)
                continue;
            int gORp1;
            String fullTitle1 = "";
            // check if is a game , +1 for proper
            if (i < numGames) {
                Game g = games.get(i);
                fullTitle1 = g.getBody() + g.getTier() + g.getDiv();
                gORp1 = 1;

                // else is a pratice
            } else {
                Game p = practices.get(i - numGames).getGame();
                fullTitle1 = p.getBody() + p.getTier() + p.getDiv();
                gORp1 = 0;
            }
            for (j = 0; j < assignment.length; j++) {
                if (i == j || assignment[j] == 0)
                    continue;
                int gORp2;
                String fullTitle2 = "";
                if (j < numGames) {
                    Game g = games.get(j);
                    fullTitle2 = g.getBody() + g.getTier() + g.getDiv();
                    gORp2 = 1;
                    // else is a pratice
                } else {
                    Game p = practices.get(j - numGames).getGame();
                    fullTitle2 = p.getBody() + p.getTier() + p.getDiv();
                    gORp2 = 0;
                }
                // if both have the same full body, and one is a game and one is a practice
                if (fullTitle1.equals(fullTitle2) && gORp1 != gORp2) {
                    if (!checkTimeOverlaps(i, j, assignment))
                        return false;
                }
            }
        }
        return true;
    }

    public boolean checkHardConstraints(int[] assignment) {

        // C1. check gameMax for each game
        if (!checkGameMax(assignment))
            return false;

        // C2. check practiceMax for each practice
        if (!checkPracticeMax(assignment))
            return false;

        // C3. check that games and practices are not at the same time
        if (!checkGamesAndPracOverlap(assignment))
            return false;

        // C4. check not compatible,
        if (!checkNonCompatible(assignment))
            return false;

        // C5. check unwanted, if true, unwanted is detected
        if (!checkUnwanted(assignment))
            return false;

        // C6. check evening for Div >= 9
        if (!checkEvening(assignment))
            return false;

        // C7. for game only, check U15/16/17/19
        if (!checkU15toU19(assignment))
            return false;

        // C8. if index < numGames, assignment[index] != index of tuesday 11:00-12:30
        if (!checkNotOnAdminDay(assignment))
            return false;

        // C9. csmaU12t1s and cmsaU13T1S have special booking
        // shouldn't return true if one is still not assigned

        // C10. check that csmaU12t1s and csmaU12t1 don't have any conflicts
        if (!checkUT12(assignment))
            return false;

        // C11. check that csmaU13t1s and csmaU13t1 don't have any conflicts
        if (!checkUT13(assignment))
            return false;

        return true;
    }
}