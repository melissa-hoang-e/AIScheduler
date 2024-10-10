import java.util.List;
import java.util.Map;
import java.util.Vector;

// The structure of the problem parsed

public class ParsedStructure {
    private String name;
    private Vector<GameSlot> gameslots;
    private Vector<PracSlot> pracslots;

    private static Vector<Game> games;

    private Vector<Practice> practices;

    private List<java.util.Map.Entry<Integer, Integer>> notcompatible;

    private List<java.util.Map.Entry<Integer, Integer>> unwanted;

    private List<Triplet<Integer, Integer, Integer>> preference;

    private List<java.util.Map.Entry<Integer, Integer>> pair;

    private List<java.util.Map.Entry<Integer, Integer>> partAssign;

    private Vector<Practice> specialPractices = new Vector<>();

    public ParsedStructure() {

    }

    public void addSpecialPractices(Practice practice) {
        this.specialPractices.add(practice);
    }

    public Vector<Practice> getSpecialPractices() {
        return this.specialPractices;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameslots(Vector<GameSlot> gameslots) {
        this.gameslots = gameslots;
    }

    public void setPracslots(Vector<PracSlot> pracslots) {
        this.pracslots = pracslots;
    }

    public void setGames(Vector<Game> games) {
        this.games = games;
    }

    public void setPractices(Vector<Practice> practices) {
        this.practices = practices;
    }

    public void setNotcompatible(List<Map.Entry<Integer, Integer>> notcompatible) {
        this.notcompatible = notcompatible;
    }

    public void setUnwanted(List<Map.Entry<Integer, Integer>> unwanted) {
        this.unwanted = unwanted;
    }

    public void setPreference(List<Triplet<Integer, Integer, Integer>> preference) {
        this.preference = preference;
    }

    public void setPair(List<Map.Entry<Integer, Integer>> pair) {
        this.pair = pair;
    }

    public void setPartAssign(List<Map.Entry<Integer, Integer>> partAssign) {
        this.partAssign = partAssign;
    }

    public void addGameslot(GameSlot gameslot) {
        this.gameslots.add(gameslot);
    }

    public void addPracslot(PracSlot pracslot) {
        this.pracslots.add(pracslot);
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public void addPractice(Practice practice) {
        this.practices.add(practice);
    }

    public void addNotCompatible(java.util.Map.Entry<Integer, Integer> pair) {
        this.notcompatible.add(pair);
    }

    public void addUnwanted(java.util.Map.Entry<Integer, Integer> pair) {
        this.unwanted.add(pair);
    }

    public void addPreference(Triplet<Integer, Integer, Integer> preference) {
        this.preference.add(preference);
    }

    public void addPair(java.util.Map.Entry<Integer, Integer> pair) {
        this.pair.add(pair);
    }

    public void addPartAssign(java.util.Map.Entry<Integer, Integer> partAssign) {
        this.partAssign.add(partAssign);
    }

    // setters & getters
    public Vector<Game> getGames() {
        return games;
    }

    public Vector<Practice> getPractices() {
        return practices;
    }

    public Vector<GameSlot> getGameSlots() {
        return gameslots;
    }

    public Vector<PracSlot> getPracticeSlots() {
        return pracslots;
    }

    public List<Map.Entry<Integer, Integer>> getPartAssign() {
        return partAssign;
    }

    public List<Triplet<Integer, Integer, Integer>> getPreferences() {
        return preference;
    }

    public List<Map.Entry<Integer, Integer>> getPairs() {
        return pair;
    }

    public List<java.util.Map.Entry<Integer, Integer>> getNotcompatible() {
        return notcompatible;
    }

    public List<java.util.Map.Entry<Integer, Integer>> getUnwanted() {
        return unwanted;
    }

    // for testing
    public void printStructure() {
        System.out.println("Parsed Structure: ");
        System.out.println("Name: \n" + name);

        System.out.println("Game Slots: ");
        for (int i = 0; i < gameslots.size(); i++) {
            System.out.println(
                    gameslots.get(i).getId() + " " +
                            gameslots.get(i).getDay() + " " +
                            gameslots.get(i).getStartTime() + " " +
                            gameslots.get(i).getGameMax() + " " +
                            gameslots.get(i).getGameMin());
        }

        System.out.println("Practice Slots: ");
        for (int i = 0; i < pracslots.size(); i++) {
            System.out.println(
                    pracslots.get(i).getId() + " " +
                            pracslots.get(i).getDay() + " " +
                            pracslots.get(i).getStartTime() + " " +
                            pracslots.get(i).getPracMax() + " " +
                            pracslots.get(i).getPracMin());
        }

        System.out.println("Games: ");
        for (int i = 0; i < games.size(); i++) {
            System.out.println(games.get(i).getId() + " " +
                    games.get(i).getBody() + " " +
                    games.get(i).getTier() + " " +
                    "DIV " +
                    games.get(i).getDiv());
        }

        System.out.println("Practices: ");
        for (int i = 0; i < practices.size(); i++) {
            System.out.println(
                    practices.get(i).getId() + " " +
                            practices.get(i).getGame().getBody() + " " +
                            practices.get(i).getGame().getTier() + " " +
                            practices.get(i).getPrcId());
        }

        System.out.println("Not compatible: ");
        for (int i = 0; i < notcompatible.size(); i++) {
            System.out.println(notcompatible.get(i));
        }

        System.out.println("Unwanted: ");
        for (int i = 0; i < unwanted.size(); i++) {
            System.out.println(unwanted.get(i));
        }

        System.out.println("Preference: ");
        for (int i = 0; i < preference.size(); i++) {
            System.out.println(preference.get(i).getFirst() + " " +
                    preference.get(i).getSecond() + " " +
                    preference.get(i).getThird());
        }

        System.out.println("Pair: ");
        for (int i = 0; i < pair.size(); i++) {
            System.out.println(pair.get(i));
        }

        System.out.println("Part Assign: ");
        for (int i = 0; i < partAssign.size(); i++) {
            System.out.println(partAssign.get(i));
        }
    }

}