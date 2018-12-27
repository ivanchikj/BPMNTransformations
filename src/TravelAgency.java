import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;


public class TravelAgency {

    //TODO add a check for loops, because when there's one the program crashes.

    public Model model;
    private Element startingPoint;
    ArrayList<ArrayList<Element>> paths;
    //private ArrayList<String> visited;
    //private ArrayList<String> toVisit;
    //private ArrayList<Element> startingList;
    ArrayList<Element> mandatorySuccessors;
    Element firstMandatorySuccessor;
    //private int pastID;


    /**
     * Constructor with startingPoint
     * TODO
     * Then initialize the variables using the methods I wrote.
     */
    TravelAgency (Model model, Element startingPoint) throws XPathExpressionException {

        this.model = model;
        this.startingPoint = startingPoint;

        //this.startingList = fromElementToArrayList();

        this.paths = new ArrayList<>();

        //visited = new ArrayList<>();
        //toVisit = new ArrayList<>();

        //this.pastID = 0;

        //Past past = new Past(startingList, pastID++);

        //getPathsFrom(past);

        //getPathsFrom(startingList);
        getPaths(startingPoint);

        mandatorySuccessors = new ArrayList<>();
        getMandatorySuccessors();

        if (mandatorySuccessors.size() > 0) {
            this.firstMandatorySuccessor = mandatorySuccessors.get(0);
        }

        //printPaths();//UNLOCKTHIS for testing

    }


    /**
     * TODO scrivi la docum
     * Spiega che se non è specificato lo startingPoint, inizi dallo start
     */
    public TravelAgency (Model model) throws XPathExpressionException {

        this.model = model;

        //this.visited = new ArrayList<>();
        //toVisit = new ArrayList<>();

        this.paths = new ArrayList<>();

        //Finding the start element
        //Element start = (Element) model.doc.getElementsByTagName
        // ("bpmn:startEvent").item(0);

        this.startingPoint = model.findElementsByType("startEvent").get(0);

        //this.startingList = fromElementToArrayList();

        //Past past = new Past(startingList, pastID++);

        //getPathsFrom(past);

        //getPathsFrom(startingList);
        getPaths(startingPoint);

        //getPaths();

        mandatorySuccessors = new ArrayList<>();
        getMandatorySuccessors();

        //printPaths();//UNLOCKTHIS for testing

    }


    private void getPaths (Element startingPoint) throws XPathExpressionException {

        ArrayList<Element> path = new ArrayList<>();
        getPathsFromPast(startingPoint, path);
    }


    private void getPathsFromPast (Element start, ArrayList<Element> past) throws XPathExpressionException {

        past.add(start);
        //String id = start.getAttribute("id");
        //System.out.println("I'm visiting element " + id); //UNLOCKTHIS
        ArrayList<Element> successors = model.getSuccessors(start);

        if (successors.size() == 0) {
            //System.out.println("this is the end: " + id);
            paths.add(past);
        } else {
            for (Element successor : successors) {
                if (!past.contains(successor)) { //to avoid infinite loops
                    ArrayList<Element> newPastArrayList =
                     (ArrayList<Element>) past.clone();
                    getPathsFromPast(successor, newPastArrayList);
                }
            }
        }
    }

//    /**
//     * Used only for testing
//     */
//    private void printVisited() {
//        System.out.println("VISITED SO FAR: ");
//        for (String id : visited) {
//            System.out.println("               " + id);
//        }
//    }

//    private void visit(Element element) {
//        System.out.println("I'm visiting: " + element.getAttribute("id"));
//        visited.add(element.getAttribute("id"));
//        //toVisit.remove(element.getAttribute("id"));
//    }

//    /**
//     * This serves when starting getPathsFrom() from an arbitrary element
// instead of the start
//     * I need to transform it into an arrayList to be able to use it as an
// input to
//     * getPathsFrom()
//     *
//     **/
//    private ArrayList<Element> fromElementToArrayList() {
//        ArrayList<Element> list = new ArrayList<Element>();
//        list.add(startingPoint);
//        return list;
//    }

//    private Element getLast(ArrayList<Element> past) {
//        return past.get(past.size() - 1);
//    }


    /**
     * From the list of possible paths, discover mandatory elements
     * The way we do it is we copy the first path in the paths array
     * Then we check which elements are repeated in all paths. Those that are
     * not repeated get removed.
     * Note that we use IDs instead of element types, as otherwise we could have
     * the impression that two paths have a deepSuccessor in common while it
     * is not the same exact element.
     */
    ArrayList<Element> getMandatorySuccessors () throws XPathExpressionException {

        ArrayList<String> firstPathIDs;

        //Adding the first path's element in ID form
        firstPathIDs = fromElementsToIDs(paths.get(0));

        //Going through all the other paths
        for (int i = 1 ; i < paths.size() ; i++) {

            ArrayList<String> idForm = fromElementsToIDs(paths.get(i));

            //Only keeping those that are repeated in all paths
            firstPathIDs.retainAll(idForm);
        }

        mandatorySuccessors = fromIDsToElements(firstPathIDs);

        //of course we remove the starting point because we know that will
        // always be in common:
        mandatorySuccessors.remove(0);

        //printMandatorySuccessors(); //UNLOCKTHIS for testing

        return mandatorySuccessors;
    }


    private ArrayList<String> fromElementsToIDs (ArrayList<Element> elementList) {

        ArrayList<String> idList = new ArrayList<>();

        for (Element element : elementList) {
            idList.add(element.getAttribute("id"));
        }
        return idList;
    }


    private ArrayList<Element> fromIDsToElements (ArrayList<String> idList) throws XPathExpressionException {

        ArrayList<Element> elementList = new ArrayList<>();

        for (String id : idList) {
            elementList.add(model.findElemById(id));
        }

        return elementList;
    }


    /**
     * Used for testing
     */
    @SuppressWarnings ("unused")
    private void printMandatorySuccessors () {

        System.out.println("PRINTING THE MANDATORY DEEP SUCCESSORS: ");
        for (Element element : mandatorySuccessors) {
            System.out.println("                       " + element.getAttribute("id"));
        }
    }


    /**
     * Used for testing
     */
    @SuppressWarnings ("unused")
    public void printPaths () {

        System.out.println("======= PRINTING PATHS =======");
        System.out.println("STARTING POINT: " + startingPoint.getAttribute(
        "id"));
        int i = 1;
        for (ArrayList<Element> path : paths) {
            System.out.println("-----Path number: " + i + "-----");
            for (Element element : path) {
                System.out.println(element.getAttribute("id"));
            }
            i++;
        }
    }


    /**
     * Used for testing
     */
    @SuppressWarnings ("unused")
    private void printNumberOfPaths () {

        System.out.println("THE NUMBER OF PATHS FROM STARTING POINT: " + startingPoint.getAttribute("id") + " is: " + paths.size());
    }


    static boolean modelsAreDifferent (Model a, Model b) throws XPathExpressionException {

        System.out.println("I'm comparing two models: " + a.name + " and " + b.name);
//        System.out.println("I'm comparing two models: " + a.path + " and "
// + b.path);

        TravelAgency taA = new TravelAgency(a);
        TravelAgency taB = new TravelAgency(b);

        ArrayList<ArrayList<Element>> pathsA = taA.paths;
        ArrayList<ArrayList<Element>> pathsB = taB.paths;

        //First of all, if the two models have a different number of paths,
        // we can stop
        //right here knowing that they must be different
        if (pathsA.size() != pathsB.size()) {
            System.out.println("The two models have a different number of " + "paths so they must be different.");
            return true;
        }

        ArrayList<ArrayList<String>> pathsAInTypes = new ArrayList<>();
        ArrayList<ArrayList<String>> pathsBInTypes = new ArrayList<>();

        //transforming all the paths in A into sequences of types
        //instead of paths of
        for (ArrayList<Element> path : pathsA) {
            pathsAInTypes.add(fromElementToTypes(path));
        }

        //transforming all the paths in B into sequences of types
        //instead of paths of
        for (ArrayList<Element> path : pathsB) {
            pathsBInTypes.add(fromElementToTypes(path));
        }

        //let's see if for every type sequence in A i can find a match in B.
        for (ArrayList<String> pathInTypes : pathsAInTypes) {
            if (! foundAMatch(pathInTypes, pathsBInTypes)) {
                System.out.println("The models: " + a.name + " and " + b.name + " are different");
                return true; //i have one path which doesn't have a match.
                // The models must be different.
            }
        }

        //we also have to check in the opposite direction, that is, if every
        // type sequence in B has a match in A:
        for (ArrayList<String> pathInTypes : pathsBInTypes) {
            if (! foundAMatch(pathInTypes, pathsAInTypes)) {
                System.out.println("The models: " + a.name + " and " + b.name + " are different");
                return true; //i have one path which doesn't have a match.
                // The models must be different.
            }
        }

        System.out.println("The models: " + a.name + " and " + b.name + " are"
         + " equal");
        return false;
    }


    @SuppressWarnings ("BooleanMethodIsAlwaysInverted")
    private static boolean foundAMatch (ArrayList<String> pathInTypes,
     ArrayList<ArrayList<String>> AllPathsInTypes) {

        //let's go through every path in allPathsInTypes and see if there's
        // one identical to pathInTypes
        for (ArrayList<String> path : AllPathsInTypes) {

            if (path.equals(pathInTypes)) {
                return true;
            }
        }
        return false;
    }

    //ASKANA do I also have to compare flows instead of just nodes?
    //I guess I do.
    //But maybe I don't because I have no rule that changes just flows
    // without changing nodes.


    /**
     * This method changes an array of elements into an array of tag types
     *
     * @param path a path in the BPMN sense from an arbitrary node to the end
     *             of the path.
     * @return the provided path as a sequence of types.
     */
    private static ArrayList<String> fromElementToTypes (ArrayList<Element> path) {

        ArrayList<String> sequenceOfTypes = new ArrayList<>();

        for (Element element : path) {
            sequenceOfTypes.add(element.getTagName());
        }

        return sequenceOfTypes;
    }


}
