import java.util.*;
import java.util.stream.Collectors;

public class FlowGrouper {


    ArrayList<String> flows;
    Set<Set<Set<String>>> groups;


    FlowGrouper (ArrayList<String> flows) {

        this.flows = flows;
        this.groups = recurseGroups(flows, new ArrayList<>(), 0);
//        printFlows();
    }

    static public void printFlows(Set<Set<Set<String>>> groups){

    for(Set<Set<String>> s : groups){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        for (Set<String> l : s){
            System.out.println(l);
        }
    }

    }


// Code in this class is edited from:
//https://codereview.stackexchange
// .com/questions/171049/all-possible-groups-of-combinations-of-array


    public Set<Set<Set<String>>> recurseGroups (List<String> initialArray,
     List<Set<String>> currentGroups, int i) {

        if (i == initialArray.size()) {
            // Deep copy current group to save its content
            Set<Set<String>> copy =
             currentGroups.stream().map(HashSet::new).collect(Collectors.toSet());

            return Collections.singleton(copy);
        }

        Set<Set<Set<String>>> result = new HashSet<>();

        for (int j = i ; j < initialArray.size() ; ++ j) {
            // Add a singleton group with the i-th integer
            // And generate groups with the remaining elements
            Set<String> newGroup =
             new HashSet<>(Collections.singletonList(initialArray.get(i)));
            currentGroups.add(newGroup);
            result.addAll(recurseGroups(initialArray, currentGroups, i + 1));
            currentGroups.remove(newGroup);

            // Add the i-th integer to each previous existing groups
            // And generate groups with the remaining elements
            for (int k = 0 ; k < currentGroups.size() ; ++ k) {
                currentGroups.get(k).add(initialArray.get(i));
                result.addAll(recurseGroups(initialArray, currentGroups,
                 i + 1));
                currentGroups.get(k).remove(initialArray.get(i));
            }
        }

        return result;
    }

    // Calling line
}
