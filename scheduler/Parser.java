import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import static java.lang.Integer.parseInt;

// Parse the input file to get the probelm structure

public class Parser {

    // The initial structure
    static ParsedStructure s;

    // index for both game slots and practice slots (start at 1)
    int idS = 1;

    // index for both games and practices (start at 1)
    int id = 1;

    public Parser(String filename) throws Exception {
        // initialize
        s = new ParsedStructure();

        s.setGameslots(new Vector<GameSlot>());

        s.setPracslots(new Vector<PracSlot>());

        s.setGames(new Vector<Game>());

        s.setPractices(new Vector<Practice>());

        s.setNotcompatible(new ArrayList<>());

        s.setUnwanted(new ArrayList<>());

        s.setPreference(new ArrayList<>());

        s.setPair(new ArrayList<>());

        s.setPartAssign(new ArrayList<>());

        // Read the file
        FileReader fr = new FileReader(filename);

        BufferedReader br = new BufferedReader(fr);

        parse(br);
    }

    /**
     * Parse the input file by looking for the headers
     * 
     * @param br
     * @throws Exception
     */
    public void parse(BufferedReader br) throws Exception {
        String line;
        while ((line = br.readLine()) != null) {

            String trimmedLine = line.trim();

            switch (trimmedLine) {
                case "Name:":
                    parseName(br);
                    break;
                case "Game slots:":
                    parseGameSlots(br);
                    break;
                case "Practice slots:":
                    parsePracticeSlots(br);
                    break;
                case "Games:":
                    parseGames(br);
                    break;
                case "Practices:":
                    parsePractices(br);
                    break;
                case "Not compatible:":
                    parseNotCompatible(br);
                    break;
                case "Unwanted:":
                    parseUnwanted(br);
                    break;
                case "Preferences:":
                    parsePreferences(br);
                    break;
                case "Pair:":
                    parsePairs(br);
                    break;
                case "Partial assignments:":
                    parsePartAssign(br);
                    break;
                default:
                    break;
                /**
                 * try {
                 * throw new Exception(String.format("Cannot parse line: %s\n", trimmedLine));
                 * } catch (Exception e) {
                 * throw new RuntimeException(e);
                 * }
                 **/

            }
        }
    }

    private void parseName(BufferedReader br) throws IOException {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            s.setName(line);
        }
    }

    private void parseGameSlots(BufferedReader br) throws IOException {
        String line;

        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));
            String day = lineList.get(0).trim();
            String startTime = lineList.get(1).trim();
            int gameMax = parseInt(lineList.get(2).trim());
            int gameMin = parseInt(lineList.get(3).trim());

            GameSlot gameSlot = new GameSlot(idS, day, startTime, gameMax, gameMin);
            s.addGameslot(gameSlot);
            idS++;

        }
    }

    /**
     * Parse practice slots
     * 
     * @param br
     * @throws IOException
     */
    private void parsePracticeSlots(BufferedReader br) throws IOException {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));
            String day = lineList.get(0).trim();
            String startTime = lineList.get(1).trim();
            int pracMax = parseInt(lineList.get(2).trim());
            int pracMin = parseInt(lineList.get(3).trim());

            PracSlot pracSlot = new PracSlot(idS, day, startTime, pracMax, pracMin);

            s.addPracslot(pracSlot);

            idS++;
        }
    }

    /**
     * Parse games
     * 
     * @param br
     * @throws IOException
     */
    private void parseGames(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split("\\s+"));
            String body = lineList.get(0).trim();
            String tier = lineList.get(1).trim();
            int div = parseInt(lineList.get(3).trim());

            Game game = new Game(id, body, tier, div);

            s.addGame(game);
            id++;
        }
        for (Game g : s.getGames()) {
            if (g.body.compareTo("CMSA") == 0 && (g.tier.compareTo("U12T1") == 0 || g.tier.compareTo("U13T1") == 0)) {
                String tier = g.tier + "S";
                Game game1 = new Game(id, g.body, tier, g.div);
                Practice specialPrac = new Practice(id, game1, 0);
                s.addPractice(specialPrac);
                PracSlot specialPracSlot = null;
                id++;
                for (PracSlot p : s.getPracticeSlots()) {
                    if (p.getDay().compareTo("TU") == 0 && p.getStartTime().compareTo("18:00") == 0) {
                        specialPracSlot = p;
                        break;
                    }
                }
                if (specialPracSlot == null) {
                    throw new Exception("Special practice does not have required practice slot");
                } else {
                    java.util.Map.Entry<Integer, Integer> partAssign = new java.util.AbstractMap.SimpleEntry<>(id,
                            specialPracSlot.getId());
                    s.addPartAssign(partAssign);
                }

            }
        }

    }

    /**
     * Parse practices
     * 
     * @param br
     * @throws Exception
     */
    private void parsePractices(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split("\\s+"));
            String body = lineList.get(0).trim();
            String tier = lineList.get(1).trim();
            int div = getDiv(lineList);

            Game game = null;
            try {
                game = findGame(body, tier, div);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            int prcId = 0;
            try {
                prcId = getPrcId(lineList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Practice practice = new Practice(id, game, prcId);
            if (lineList.get(2).trim().compareTo("DIV") == 0) {
                if (lineList.get(4).trim().compareTo("OPN") == 0) {
                    practice.setOpn();
                }
            } else {
                if (lineList.get(2).trim().compareTo("OPN") == 0) {
                    practice.setOpn();
                }
            }

            s.addPractice(practice);

            if (body.compareTo("CMSA") == 0 && (tier.compareTo("U12T1S") == 0 || tier.compareTo("U13T1S") == 0)) {
                System.out.println("Found special practice.");
                s.addSpecialPractices(practice);
                PracSlot specialPracSlot = null;
                for (PracSlot p : s.getPracticeSlots()) {
                    if (p.getDay().compareTo("TU") == 0 && p.getStartTime().compareTo("18:00") == 0) {
                        specialPracSlot = p;
                        break;
                    }
                }
                if (specialPracSlot == null) {
                    throw new Exception("Special practice does not have required practice slot");
                } else {
                    java.util.Map.Entry<Integer, Integer> partAssign = new java.util.AbstractMap.SimpleEntry<>(id,
                            specialPracSlot.getId());
                    s.addPartAssign(partAssign);
                }

            }

            id++;
        }
    }

    /**
     * Parse not compatible
     * 
     * @param br
     * @throws Exception
     */
    private void parseNotCompatible(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));
            for (int i = 0; i < lineList.size(); i++) {
                lineList.set(i, lineList.get(i).trim());
            }
            int[] id = new int[2];

            for (int i = 0; i < lineList.size(); i++) {
                List<String> gamePracList = Arrays.asList(lineList.get(i).split("\\s+"));
                String body = gamePracList.get(0).trim();
                String tier = gamePracList.get(1).trim();
                int div = getDiv(gamePracList);
                int pracId;

                if (isPrac(gamePracList)) {
                    pracId = getPrcId(gamePracList);
                    Practice practice = findPractice(body, tier, div, pracId);
                    id[i] = practice.getId();
                } else {
                    Game game = findGame(body, tier, div);
                    id[i] = game.getId();
                }
            }
            java.util.Map.Entry<Integer, Integer> notcompPair = new java.util.AbstractMap.SimpleEntry<>(id[0], id[1]);
            s.addNotCompatible(notcompPair);
        }
    }

    /**
     * Parse unwanted
     * 
     * @param br
     * @throws Exception
     */
    private void parseUnwanted(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));

            for (int i = 0; i < lineList.size(); i++) {
                lineList.set(i, lineList.get(i).trim());
            }

            List<String> gamePracList = Arrays.asList(lineList.get(0).split("\\s+"));

            String body = gamePracList.get(0).trim();
            String tier = gamePracList.get(1).trim();
            int div = getDiv(gamePracList);
            int pracId;
            int id_gp, id_slot;

            if (isPrac(gamePracList)) {
                pracId = getPrcId(gamePracList);
                Practice practice = findPractice(body, tier, div, pracId);
                id_gp = practice.getId();
                String day = lineList.get(1);
                String time = lineList.get(2);
                PracSlot pracSlot = findPracticeSlot(day, time);
                id_slot = pracSlot.getId();
            } else {
                Game game = findGame(body, tier, div);
                id_gp = game.getId();
                String day = lineList.get(1);
                String time = lineList.get(2);
                GameSlot gameSlot = findGameSlot(day, time);
                id_slot = gameSlot.getId();
            }

            java.util.Map.Entry<Integer, Integer> unwanted = new java.util.AbstractMap.SimpleEntry<>(id_gp, id_slot);
            s.addUnwanted(unwanted);
        }
    }

    /**
     * Parse preferences
     * 
     * @param br
     * @throws Exception
     */
    private void parsePreferences(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));

            for (int i = 0; i < lineList.size(); i++) {
                lineList.set(i, lineList.get(i).trim());
            }

            int val = parseInt(lineList.get(3).trim());

            List<String> gamePracList = Arrays.asList(lineList.get(2).split("\\s+"));
            for (int i = 0; i < gamePracList.size(); i++) {
                gamePracList.set(i, gamePracList.get(i).trim());
            }
            String body = gamePracList.get(0).trim();
            String tier = gamePracList.get(1).trim();
            int div = getDiv(gamePracList);
            int pracId;
            int id_gp, id_slot;

            if (isPrac(gamePracList)) {
                pracId = getPrcId(gamePracList);
                Practice practice = findPractice(body, tier, div, pracId);
                id_gp = practice.getId();
                String day = lineList.get(0);
                String time = lineList.get(1);
                PracSlot pracSlot = findPracticeSlot(day, time);
                id_slot = pracSlot.getId();
            } else {
                Game game = findGame(body, tier, div);
                id_gp = game.getId();
                String day = lineList.get(0);
                String time = lineList.get(1);
                GameSlot gameSlot = findGameSlot(day, time);
                id_slot = gameSlot.getId();
            }

            Triplet<Integer, Integer, Integer> preference = new Triplet<Integer, Integer, Integer>(id_slot, id_gp, val);
            s.addPreference(preference);
        }

    }

    /**
     * Parse pairs
     * 
     * @param br
     * @throws Exception
     */
    private void parsePairs(BufferedReader br) throws Exception {
        String line;
        while (!(line = br.readLine().trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));
            for (int i = 0; i < lineList.size(); i++) {
                lineList.set(i, lineList.get(i).trim());
            }
            int[] id = new int[2];

            for (int i = 0; i < lineList.size(); i++) {
                List<String> gamePracList = Arrays.asList(lineList.get(i).split("\\s+"));
                String body = gamePracList.get(0).trim();
                String tier = gamePracList.get(1).trim();
                int div = getDiv(gamePracList);
                int pracId;

                if (isPrac(gamePracList)) {
                    pracId = getPrcId(gamePracList);
                    Practice practice = findPractice(body, tier, div, pracId);
                    id[i] = practice.getId();
                } else {
                    Game game = findGame(body, tier, div);
                    id[i] = game.getId();
                }
            }
            java.util.Map.Entry<Integer, Integer> pair = new java.util.AbstractMap.SimpleEntry<>(id[0], id[1]);
            s.addPair(pair);
        }
    }

    /**
     * Parse the part assign
     * 
     * @param br
     * @throws Exception
     */
    private void parsePartAssign(BufferedReader br) throws Exception {
        String line;
        while ((line = br.readLine()) != null && !(line.trim()).isEmpty()) {
            List<String> lineList = Arrays.asList(line.split(","));

            for (int i = 0; i < lineList.size(); i++) {
                lineList.set(i, lineList.get(i).trim());
            }

            List<String> gamePracList = Arrays.asList(lineList.get(0).split("\\s+"));
            for (int i = 0; i < gamePracList.size(); i++) {
                gamePracList.set(i, gamePracList.get(i).trim());
            }
            String body = gamePracList.get(0).trim();
            String tier = gamePracList.get(1).trim();
            int div = getDiv(gamePracList);
            int pracId;
            int id_gp, id_slot;

            if (isPrac(gamePracList)) {
                pracId = getPrcId(gamePracList);
                Practice practice = findPractice(body, tier, div, pracId);
                id_gp = practice.getId();
                String day = lineList.get(1);
                String time = lineList.get(2);
                PracSlot pracSlot = findPracticeSlot(day, time);
                id_slot = pracSlot.getId();
            } else {
                Game game = findGame(body, tier, div);
                id_gp = game.getId();
                String day = lineList.get(1);
                String time = lineList.get(2);
                GameSlot gameSlot = findGameSlot(day, time);
                id_slot = gameSlot.getId();
            }

            java.util.Map.Entry<Integer, Integer> partAssign = new java.util.AbstractMap.SimpleEntry<>(id_gp, id_slot);
            s.addPartAssign(partAssign);
        }
    }

    /**
     * identify the game based on body and tier
     * 
     * @param body
     * @param tier
     * @return game
     * @throws Exception
     */
    private Game findGame(String body, String tier, int div) throws Exception {
        Vector<Game> games = s.getGames();
        for (int i = 0; i < games.size(); i++) {

            if (body.equals(games.get(i).body) &&
                    tier.equals(games.get(i).tier) &&
                    (div == games.get(i).getDiv() || div == 0)) {

                return games.get(i);
            }
        }
        throw new Exception("Cannot find game identifier: " + body + " " + tier);
    }

    /**
     * Given the body, tier, division, and pracId find the corresponding practice
     * 
     * @param body
     * @param tier
     * @param div
     * @param pracId
     * @return the practice
     * @throws Exception
     */
    private Practice findPractice(String body, String tier, int div, int pracId) throws Exception {
        Vector<Practice> practices = s.getPractices();
        for (int i = 0; i < practices.size(); i++) {
            if (body.equals(practices.get(i).game.body)
                    && (tier.equals(practices.get(i).game.tier))
                    && (pracId == practices.get(i).getPrcId())) {
                return practices.get(i);
            }
        }
        throw new Exception("Cannot find practice identifier: " + tier + " practice id: " + pracId);
    }

    /**
     * Given the day and time, find the game slot
     * 
     * @param day
     * @param time
     * @return the game slot
     * @throws Exception
     */
    private GameSlot findGameSlot(String day, String time) throws Exception {
        Vector<GameSlot> gameSlots = s.getGameSlots();
        for (int i = 0; i < gameSlots.size(); i++) {
            if (day.equals(gameSlots.get(i).getDay())
                    && (time.equals(gameSlots.get(i).getStartTime()))) {
                return gameSlots.get(i);
            }
        }
        throw new Exception("Cannot find gameslot: " + day + time);
    }

    /**
     * Given the day and time, find the practice slot
     * 
     * @param day
     * @param time
     * @return the practice slot
     * @throws Exception
     */
    private PracSlot findPracticeSlot(String day, String time) throws Exception {
        Vector<PracSlot> pracSlots = s.getPracticeSlots();
        for (int i = 0; i < pracSlots.size(); i++) {
            if (day.equals(pracSlots.get(i).getDay())
                    && (time.equals(pracSlots.get(i).getStartTime()))) {
                return pracSlots.get(i);
            }
        }
        throw new Exception("Cannot find pracslot: " + day + " " + time);
    }

    /**
     * get the practice id given the line of string
     * 
     * @param lineList the line of the string
     * @return the practice id
     * @throws Exception
     */
    private int getPrcId(List<String> lineList) throws Exception {
        for (int i = 0; i < lineList.size(); i++) {
            if (lineList.get(i).equals("PRC") || lineList.get(i).equals("OPN")) {
                return parseInt(lineList.get(i + 1).trim());
            }
        }
        throw new Exception("Cannot find practice id");
    }

    /**
     * Given the list of string, find the division id
     * 
     * @param lineList the list of String
     * @return the division id or 0 if no division id
     */
    private int getDiv(List<String> lineList) {
        for (int i = 0; i < lineList.size(); i++) {
            if (lineList.get(i).trim().equals("DIV")) {
                return parseInt(lineList.get(i + 1).trim());
            }
        }
        // Set div to 0 if not provided
        return 0;
    }

    /**
     * Check if the list of string if for a practice or not
     * 
     * @param gamePracList
     * @return true if is practice
     */
    private Boolean isPrac(List<String> gamePracList) {
        for (int i = 0; i < gamePracList.size(); i++) {
            if (gamePracList.get(i).equals("PRC") || gamePracList.get(i).equals("OPN")) {
                return true;
            }
        }
        return false;
    }

    public static ParsedStructure getS() {
        return s;
    }
}