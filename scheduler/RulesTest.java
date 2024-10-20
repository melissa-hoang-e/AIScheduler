import java.util.List;

public class RulesTest {

    private boolean runTest;
    public RulesTest(boolean setTest){
        runTest = setTest;
    }

    private void printCurrentFacts(State A, ParsedStructure s){
        List<Fact> currentFacts = A.getFacts();
        for (Fact f : currentFacts){
            f.printFact(s);
        }
    }

    public void test(State A){
        if(!runTest)
            return;

        ParsedStructure s = A.problemFacts;
        Rules extensionRules = new Rules(s);

        System.out.println("\n\n---------- TESTING RULES ----------\n");

        // test mutate
        System.out.println("---------- Testing mutate ----------");
//        System.out.println("Before:");
//        printCurrentFacts(A,s);

        Fact f = A.getFacts().get(0);

        extensionRules.mutate(f,A);

//        System.out.println("After:");
//        printCurrentFacts(A,s);


        // test swapMutate
        System.out.println("---------- Testing swapMutate: ----------");
//        System.out.println("Before:");
//        printCurrentFacts(A,s);
//
//        Fact f1 = A.getFacts().get(0);
//        Fact f2 = A.getFacts().get(1);
//
//        extensionRules.swapMutate(f1,f2,A);
//
//        System.out.println("After:");
//        printCurrentFacts(A,s);

        // test crossover

        System.out.println("---------- Testing crossOver: ----------");
        System.out.println("Before:");
        printCurrentFacts(A,s);

        Fact f1 = A.getFacts().get(0);
        Fact f2 = A.getFacts().get(1);

        extensionRules.crossOver(f1,f2,A);

        System.out.println("After:");
        printCurrentFacts(A,s);


        // test random
        System.out.println("---------- Testing random: ----------");
//        int res = Rules.random(2,8);
//        System.out.println("Random result is: "+res);

    }
}