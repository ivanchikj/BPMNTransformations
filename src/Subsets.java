import java.util.ArrayList;

// A Java program to print all subsets of a set
class Subsets {


    static ArrayList<ArrayList<Integer>> sets = new ArrayList<>();
    static ArrayList<match> msets = new ArrayList<>();
    static ArrayList<int[]> validCombinations = new ArrayList<>();
//    static ArrayList<Integer> fullset = new ArrayList<>();
    static int fullsetSize;

    // Print all subsets of given set[]
    static void findSubSets (ArrayList<Integer> set) {

        int n = set.size();

        // Run a loop for printing all 2^n 
        // subsets one by one
        for (int i = 0 ; i < (1 << n) ; i++) {
//            System.out.print("{ ");
            ArrayList<Integer> temp = new ArrayList<>();
            // Print current subset 
            for (int j = 0 ; j < n ; j++)

                // (1<<j) is a number with jth bit 1 
                // so when we 'and' them with the 
                // subset number we get which numbers 
                // are present in the subset and which 
                // are not 
                if ((i & (1 << j)) > 0) {
                    temp.add(set.get(j));
                }

//            System.out.println("}");
            sets.add(temp);
        }
    }


    // Driver code
    public static void main (String[] args) {
//        int set[] = {1, 2, 3};
        ArrayList<Integer> fullset = new ArrayList<>();
        fullset.add(1);
        fullset.add(2);
        fullset.add(3);
        fullsetSize = fullset.size();

        findSubSets(fullset);
//        createMatchingSets(sets, fullset);
        Combination c = new Combination(sets.size());
        ArrayList<int[]> combinations = c.combinations;

        removeInvalidCombinations(combinations);
        // recompose the original set from the
        // subsets
//        printMatches(msets);

printValidCombinations();
//        prin(sets);
    }


    private static void printValidCombinations () {
        for (int[] combi : validCombinations){
            for (int i = 0 ; i < combi.length ; i++) {
                System.out.println(sets.get(combi[i]));
                System.out.print("  |  ");
            }
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }


    private static void removeInvalidCombinations (ArrayList<int[]> combinations) {

        for (int[] combi : combinations){
            int flowsInTheCombinationOfSets = 0;
            for (int i = 0 ; i < combi.length ; i++) {
                int flowsInTheSet = sets.get(combi[i]).size();
                flowsInTheCombinationOfSets += flowsInTheSet;
            }
            if (flowsInTheCombinationOfSets == fullsetSize){
                validCombinations.add(combi);
            }
        }

    }

//    private static void findPossibleMatches (ArrayList<ArrayList<Integer>> sets) {
//
//        for (ArrayList<Integer> s1 : sets) {
//            for (ArrayList<Integer> s2 : sets) {
//                boolean overlap = false;
//                for (Integer i : s1) {
//                    for (Integer d : s2) {
//                        if (i == d) {
//                            overlap = true;
//                            System.out.println("Sets " + s1 + " and " +  s2 + "have element " + d + "in common");
//                        }
//                    }
//                }
//
//                if ((!overlap) && (s1.size() + s2.size() == fullsetSize)){
//                    match m = new match(s1, s2);
//                    msets.add(m);
//                }
//            }
//        }
//    }



//    private static void createMatchingSets(ArrayList<ArrayList<Integer>> sets
//    , ArrayList<Integer> fullset){
//    for (ArrayList<Integer> set : sets){
//        matchingSet ms = new matchingSet(set, fullset);
//        msets.add(ms);
//    }
//    }


    private static void prin (ArrayList<ArrayList<Integer>> sets) {

        for (ArrayList<Integer> set : sets) {
            for (Integer i : set) {
                System.out.print(i);
            }

//            System.out.println();
            System.out.println();
            System.out.println();
        }
    }

    private static void printMatches (ArrayList<match> matches){

        System.out.println("CIAO QUANTI MATCH? ");
        System.out.println(matches.size());
        int i = 0;
        for (match m : matches){

            System.out.println();
            System.out.println("Match numero: " + i);
            System.out.println();
            System.out.println();

            show(m.original);
            show(m.matching);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            i++;

        }
    }

    private static void show(ArrayList<Integer> l){
    for (Integer i : l ){
        System.out.print(l);
    }
    }



    public static class match {

        public ArrayList<Integer> original;
        public ArrayList<Integer> matching;

        match (ArrayList<Integer> original, ArrayList<Integer>
        matching) {

            this.original = original;
            this.matching = matching;

        }
    }


}
//Edited from:
//https://www.geeksforgeeks.org/finding-all-subsets-of-a-given-set-in-java/