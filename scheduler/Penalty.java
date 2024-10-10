import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Penalty {


    public static int calcPenalty(int[] assign, State A){

        ParsedStructure s = A.problemFacts;
        Vector<GameSlot> gameSlots = s.getGameSlots();
        int totalPen = 0;
        int i;

        // calc minimum game penalty
        int totalMinPen = 0;
        for(GameSlot g : gameSlots){
            int count = 0;
            for(i=0; i < assign.length; i++){
                if (g.getId() == assign[i]) count++;
            }
            int gameMin = g.getGameMin();
            if (count < gameMin)
                totalMinPen += (gameMin - count) * A.getPenValue("penGameMin");
        }

        // calc minimum practice penalty
        for (PracSlot p : s.getPracticeSlots()){
            int count =0;
            for(i=0; i< assign.length; i++){
                if (p.getId() == assign[i]) count++;
            }
            int pracMin = p.getPracMin();
            if (count < pracMin)
                totalMinPen += (pracMin - count) * A.getPenValue("penPracMin");

        }

        totalMinPen *= A.getWeightValue("weightMinFilled");

        // calc preference penalty
        int totalPrefPen = 0;
        for (Triplet<Integer,Integer,Integer> pref : s.getPreferences()){
            int slotId = pref.getFirst();
            int gpId = pref.getSecond();
            int val = pref.getThird();

            if(assign[gpId-1] != slotId) totalPrefPen += val;

        }

        totalPrefPen *= A.getWeightValue("weightPref");

        // calc not paired penalty
        int totalPairPen = 0;
        List<Map.Entry<Integer, Integer>> pairs = A.problemFacts.getPairs();

        for (Map.Entry<Integer, Integer> p : pairs){
            int p1 = p.getKey();
            int p2 = p.getValue();
            if (assign[p1-1] != assign[p2-1]) {
                totalPairPen += A.getPenValue("penNotPaired");

            }

        }
        totalPairPen *= A.getWeightValue("weightPair");

        // calc section penalty
        int totalSecPen = 0;
        Vector<Game> games = A.problemFacts.getGames();

        // loop over games
        int j;
        for(i=0; i < games.size(); i++){
            Game gOuter = games.get(i);
            String tierOuter = gOuter.getTier();
            for (j=i+1; j < games.size(); j++){
                Game gInner = games.get(j);

                if (gOuter.equals(gInner)) continue; // skip over itself in the inner loop

                String tierInner = gInner.getTier();

                if (tierInner.equals(tierOuter) && assign[gInner.getId()-1] == assign[gOuter.getId()-1])
                    totalSecPen += A.getPenValue("penSection");

            }

        }

        totalSecPen *= A.getWeightValue("weightSecDiff");

        totalPen = totalMinPen + totalPrefPen + totalPairPen + totalSecPen;

        return totalPen;
    }
}
