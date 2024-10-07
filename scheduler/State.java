import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// A state of the search

public class State {

    // A state is a list of facts
    private final ArrayList<Pair<Fact, Integer>> factsAndPens;
    private ArrayList<Fact> justFacts;
    public ParsedStructure problemFacts;
    private HashMap<String, Integer> penValues;
    private HashMap<String, Integer> weightValues;
    private HashSet<Integer> partAssign;

    private Constraints consts;

    public State(ParsedStructure s,
                 HashMap<String, Integer> pens,
                 HashMap<String, Integer> wVals,
                 HashSet<Integer> pA,
                 Constraints c){
        this.factsAndPens = new ArrayList<Pair<Fact, Integer>>();
        this.justFacts = new ArrayList<Fact>();
        this.problemFacts = s;
        this.penValues = pens;
        this.weightValues = wVals;
        this.partAssign = pA;
        this.consts = c;
    }

    // getters & setters
    public ArrayList<Pair<Fact,Integer>> getFactsAndPens() {
        return factsAndPens;
    }

    public ArrayList<Fact> getFacts() {return justFacts; }

    public void addRegularFact(Fact f) { this.justFacts.add(f);}

    public void addNewFact(Pair<Fact, Integer> f){
        this.factsAndPens.add(f);
        this.justFacts.add(f.getFirst());
    }

    public int getPenValue(String _key){
        return penValues.get(_key);
    }

    public int getWeightValue(String _key){ return weightValues.get(_key);}

    public HashSet<Integer> getPartAssigns() {return partAssign; }

    public Constraints getConsts(){ return consts; }
}
