import java.util.*;
import java.util.stream.Collectors;

class FlowGrouper {


    ArrayList<String> flows;
    Set<Set<Set<String>>> groups;


    FlowGrouper (ArrayList<String> flows) {

        this.flows = flows;
        this.groups = recurseGroups(flows, new ArrayList<>(), 0);
//        printFlows();
    }


    static void printFlows (Set<Set<Set<String>>> groups) {

        for (Set<Set<String>> s : groups) {
//            System.out.println();
//            System.out.println();
//            System.out.println();
            for (Set<String> l : s) {
//                System.out.println(l);
            }
        }
    }


    // Code in this class is edited from:
//https://codereview.stackexchange
// .com/questions/171049/all-possible-groups-of-combinations-of-array
    private Set<Set<Set<String>>> recurseGroups (List<String> initialArray,
     List<Set<String>> currentGroups, int i) {

        if (i == initialArray.size()) {

            Set<Set<String>> copy =
             currentGroups.stream().map(HashSet::new).collect(Collectors.toSet());

            return Collections.singleton(copy);
        }

        Set<Set<Set<String>>> result = new HashSet<>();

        for (int j = i ; j < initialArray.size() ; ++ j) {

            Set<String> newGroup =
             new HashSet<>(Collections.singletonList(initialArray.get(i)));
            currentGroups.add(newGroup);
            result.addAll(recurseGroups(initialArray, currentGroups, i + 1));
            currentGroups.remove(newGroup);

            for (int k = 0 ; k < currentGroups.size() ; ++ k) {
                currentGroups.get(k).add(initialArray.get(i));
                result.addAll(recurseGroups(initialArray, currentGroups,
                 i + 1));
                currentGroups.get(k).remove(initialArray.get(i));
            }
        }
        return result;
    }


}
