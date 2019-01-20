import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Reverse3 {


    /**
     * @param model the Model that will be changed
     */
    public static void a (Model model) throws Exception {

        System.out.println("I'm applying rule REVERSE3a to model " + model.path);
        ArrayList<Element> inclusiveGateways = model.findElementsByType(
        "inclusiveGateway");

        if (inclusiveGateways.size() == 0) {
            System.out.println("Reverse3a: there are no inclusive gateways " + "in this model");
        }

        for (Element inclusiveSplit : inclusiveGateways) {
            Element process = (Element) inclusiveSplit.getParentNode();
            //Element inclusiveSplit = (Element) inclusiveGateways.item(i);

//            System.out.println("I'm analyzing inclusive gateway " +
//            inclusiveSplit.getAttribute("id"));

//            System.out.println("Is it a split? : " + model.isASplit
// (inclusiveSplit));

//            System.out.println("Are all outgoing flows of " +
// inclusiveSplit.getAttribute("id") + " ? : " +
// allOutGoingFlowsAreAlwaysTrue(inclusiveSplit, model));

            if (model.isASplit(inclusiveSplit) && allOutGoingFlowsAreAlwaysTrue(inclusiveSplit, model)) {

                Element firstMandatoryDeepSuccessor =
                 model.getFirstMandatoryMeetingPoint(inclusiveSplit);
                if (firstMandatoryDeepSuccessor.getTagName().contains(
                "inclusiveGateway")) {

                    Coordinates oldInclusiveSplitCoordinates =
                     model.getPosition(inclusiveSplit);

                    // creating the substitute element in the position of
                    // the
                    // old one
                    String newParallelSplitId =
                     model.newParallelGateway(oldInclusiveSplitCoordinates,
                      process);
                    Element newParallelSplit =
                     model.findElemById(newParallelSplitId);
                    model.replaceElement(inclusiveSplit, newParallelSplit, process);

                    ArrayList<Element> outgoingFlows =
                     model.getOutgoingFlows(newParallelSplit);

                    for (Element flow : outgoingFlows) {
                        if (model.hasCondition(flow)) {
                            Element condition =
                             model.returnConditionElement(flow);
                            flow.removeChild(condition);
                        }
                    }

                    //Now we can substitute also the old merge with a
                    // parallel gateway.
                    Coordinates oldInclusiveMergeCoordinates =
                     model.getPosition(firstMandatoryDeepSuccessor);

                    String newParallelMergeId =
                     model.newParallelGateway(oldInclusiveMergeCoordinates, process);
                    Element newParallelMerge =
                     model.findElemById(newParallelMergeId);
                    model.replaceElement(firstMandatoryDeepSuccessor,
                     newParallelMerge, process);
                }
            }
        }
    }


    static class Reverse3TargetStructure {


        Element firstInclusive;
        Element firstMeetingPoint;


        Reverse3TargetStructure (Element firstInclusive,
         Element firstMeetingPoint) {

            this.firstInclusive = firstInclusive;
            this.firstMeetingPoint = firstMeetingPoint;
        }


        void printStructure () {

            System.out.println("The first Inclusive" + firstInclusive.getAttribute("id"));
            System.out.println("The first incl meet Point " + firstMeetingPoint.getAttribute("id"));
        }


    }


    /**
     * @param model the Model that will be changed
     */
    public static void b (Model model) throws Exception {

        System.out.println("I'm applying rule REVERSE3b to model " + model.path);
        ArrayList<Element> inclusiveGateways = model.findElementsByType(
        "inclusiveGateway");

        if (inclusiveGateways.size() == 0) {
            System.out.println("Reverse3a: there are no inclusive gateways " + "in this model");
        }

        for (Element inclusiveSplit : inclusiveGateways) {
            //Element inclusiveSplit = (Element) inclusiveGateways.item(i);
            Element process = (Element) inclusiveSplit.getParentNode();

            System.out.println("I'm analyzing inclusive gateway " + inclusiveSplit.getAttribute("id"));

            System.out.println("Is it a split? : " + model.isASplit(inclusiveSplit));

            System.out.println("Are all outgoing flows of " + inclusiveSplit.getAttribute("id") + " mutually exclusive ? : " + allOutgoingFlowsAreMutuallyExclusive(inclusiveSplit, model));

            if (model.isASplit(inclusiveSplit) && allOutgoingFlowsAreMutuallyExclusive(inclusiveSplit, model)) {
                TravelAgency ta = new TravelAgency(model, inclusiveSplit);
                Element firstMandatoryDeepSuccessor =
                 ta.firstMandatorySuccessor;
                if (firstMandatoryDeepSuccessor.getTagName().contains(
                "inclusiveGateway")) {

                    Coordinates oldInclusiveSplitCoordinates =
                     model.getPosition(inclusiveSplit);

                    // creating the substitute element in the position of
                    // the
                    // old one
                    String newExclusiveSplitId =
                     model.newExclusiveGateway(oldInclusiveSplitCoordinates,
                      process);
                    Element newExclusiveSplit =
                     model.findElemById(newExclusiveSplitId);
                    model.replaceElement(inclusiveSplit, newExclusiveSplit,
                     process);

                    //Unlike in reverse3a we don't need to delete the split's
                    // outgoing flows.

                    //Now we can substitute also the old merge with an
                    // inclusive gateway.
                    Coordinates oldInclusiveMergeCoordinates =
                     model.getPosition(firstMandatoryDeepSuccessor);

                    String newExclusiveMergeId =
                     model.newExclusiveGateway(oldInclusiveMergeCoordinates, process);
                    Element newExclusiveMerge =
                     model.findElemById(newExclusiveMergeId);
                    model.replaceElement(firstMandatoryDeepSuccessor,
                     newExclusiveMerge, process);
                }
            }
        }
    }


    /**
     * Changing inclusive to exclusive.
     * TODO delete this method
     */
    static void backup (Model model) throws Exception {

        System.out.println("I'm applying rule REVERSE3b to model " + model.name);
        // Here I'm creating a list of all the inclusive gateways in the
        // inputModel
        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName(
        "inclusiveGateway");
        System.out.println("number of inclusive gateway instances: " + inclusiveGatewayInstances.getLength());

        if (inclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse3b: there are no exclusive gateways " + "in this model");
        }

        //TODO the first part of the firstPart of r3a and r3b can be
        //generalized for bot parallel and exclusive gateways

        // going through all of the parallelGateways in the model:
        for (int i = 0 ; i <= inclusiveGatewayInstances.getLength() ; i++) {

            Element oldInclusive =
             (Element) inclusiveGatewayInstances.item(0); //this will be the
            // element in case
            // NOTE Why this works?
            // Reason: After the gateway gets deleted from the model, it also
            // gets deleted from inclusiveGatewayInstances
            // For some reason. The problem is that now the element at index
            // 1 is now at index 0.
            // That why we use the same index every time until the list is
            // empty.
            Element process = (Element) oldInclusive.getParentNode();


            System.out.println("working on the " + (i + 1) + "nd " +
            "inclusiveGateway");
            System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

            Coordinates oldInclusiveCoordinates =
             model.getPosition(oldInclusive);

            //creating the substitute element in the position of the old one
            String newExclusiveGatewayId =
             model.newInclusiveGateway(oldInclusiveCoordinates, process);
            Element newExclusiveGateway =
             model.findElemById(newExclusiveGatewayId);
            //replacing the two elements
            model.replaceElement(oldInclusive, newExclusiveGateway, process);

            //Here I don't need to distinguish between those that are merges
            // and those that are not.
            //I don't need to touch the incoming/outgoing flows either
        }
    }


    /**
     * TODO fai metodo che crea i gruppi di flow, non mettere quelli vuoti
     * insieme
     * e controlla le condizioni con WA.
     * <p>
     * TODO controlla anche quando calcoli la posizione dei nuovi
     * exclGateway, secondo me la calcoli troppo presto, e per quello ce ne
     * sono tanti nella stessa posizione.
     */
    static void c (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE3c to model " + model.name);


        ArrayList<Reverse3TargetStructure> targets = new ArrayList<>();

        ArrayList<Element> inclusiveGatewayInstances =
         model.findElementsByType("inclusiveGateway");

        if (inclusiveGatewayInstances.size() == 0) {
            System.out.println("Reverse3c: there are no inclusive gateways " + "in" + " this model");
        }

        ArrayList<Element> candidates = new ArrayList<>();
        //for now we only care about those inclusive Gateways that are splits

        for (Element candidate : inclusiveGatewayInstances) {

            if (model.isASplit(candidate)) {
                candidates.add(candidate);
            }
//            else {
//
//                // TODO c'è un'altro modo invece che non trasformarlo del
//                // tutto in questo caso? Magari posso avere un valore
//                // provvisorio di aggregateBy che diventa uguale al numero di
//                // outgoingFLows.
//
//            }
        }

        // Now we have to find out if the firstMeetingPoint of that
        // candidate is also an inclusiveGateway.
        // (It should always be)

        for (Element candidate : candidates) {
            TravelAgency ta = new TravelAgency(model, candidate);
            Element firstMeetingPoint = ta.firstMandatorySuccessor;
            if (firstMeetingPoint.getTagName().equals(model.style(
            "inclusiveGateway"))) { // I could use contains.
                //ok now I can create a target structure.
                //System.out.println(ta.paths.size());//UNLOCKTHIS
                //System.out.println(candidate.getAttribute("name"));
                Reverse3TargetStructure target =
                 new Reverse3TargetStructure(candidate, firstMeetingPoint);
                targets.add(target);
            } else {

//                System.out.println(ta.paths.size());//UNLOCKTHIS
//                System.out.println(candidate.getAttribute("id"));
//                System.out.println(candidate.getAttribute("name"));
//                System.out.println(firstMeetingPoint.getAttribute("id"));

            }
        }

//        System.out.println("I've found : " + targets);
//        for (Reverse3TargetStructure rc : targets) {
//            rc.printStructure();
//        }

        //now let's go through all targets and do the necessary changes.
        for (Reverse3TargetStructure structure : targets) {
            doReverse3c(structure, model);
        }
    }

    //TODO afterwards, check if there's still the possibility that there are
    // 1in-1out gateways and delete them. Questo si chiama isUselessGateway
    // in un'altra regola. Prima copialo qua poi mettilo in model.


    /**
     * This method tries to avoid putting empty paths in the same gateway.
     *
     * @param flows
     * @param model
     * @param aggregateBy
     */
    private static void createTuples (ArrayList<Element> flows, Model model,
     int aggregateBy) throws XPathExpressionException {
        //let's separate empty flows from non empty flows

//        ArrayList<Element> nonEmptyFlows = new ArrayList<>();
//        ArrayList<Element> EmptyFlows = new ArrayList<>();
//        for (Element flow : flows){
//            TravelAgency ta = new TravelAgency(model,model.getSource(flow));
//            if (ta.firstMandatorySuccessor.getAttribute("id").equals(model
//            .getTarget()))
//
//        }

    }


    private static void doReverse3c (Reverse3TargetStructure target,
     Model model) throws XPathExpressionException {

        Element inclusive = target.firstInclusive;
        Element mp = target.firstMeetingPoint;
        Element process = (Element) inclusive.getParentNode();
        ArrayList<Element> outFlows = model.getOutgoingFlows(inclusive);
        ArrayList<Element> inFlows = model.getIncomingFlows(mp);

        ArrayList<ArrayList<Element>> flowGroups = groupFlows(outFlows, model);

        //Let's calculate how many exclusive gateway we have to create on the
        //'left side'.
        if (flowGroups.size() < 2) {
            return; //it means the program wasn't able to find a satisfactory
            // grouping.
        }
        int i = 0;
        for (ArrayList<Element> group : flowGroups) {
            // to chose a position for the exclusive gateway, we must first
            // have the position of the successors, which are the targets of
            // the sequence flows in this group.
            ArrayList<Element> succsOfGroup = new ArrayList<>();

            for (Element flow : group) {
                flow.setAttribute("name", "G: " + i);
                Element t = model.getTarget(flow);
                succsOfGroup.add(t);
            }

            Coordinates c = model.calculatePositionOfNewNode(inclusive,
             succsOfGroup);
            String newExclGatSplitID = model.newExclusiveGateway(c, process);
            //the flows must now have the new exclusive gat as a source.
            for (Element flow : group) {
                model.setSource(flow.getAttribute("id"), newExclGatSplitID);
            }

            // Now we must create an exclusive merge for our group.
            // First we must collect all inFlows that have a match in our
            // exclusive.
            ArrayList<Element> inFlowsThatMatch = new ArrayList<>();
            for (Element flow : inFlows) {

                for (Element outFlow : group) {
                    if (isAMatch(outFlow, flow, model)) {
                        inFlowsThatMatch.add(flow);
                    }
                }
            }

            //Now we must collect all their sources to place the exclusive
            // gateway merge in the correct position
            ArrayList<Element> predsOfGroup = new ArrayList<>();

            for (Element flow : inFlowsThatMatch) {
                flow.setAttribute("name", "G: " + i);
                Element t = model.getSource(flow);
                predsOfGroup.add(t);
            }

            Coordinates c2 = model.calculatePositionOfNewNode(predsOfGroup, mp);
            String newExclGatMergeID = model.newExclusiveGateway(c2, process);

            //the flows must now have the new exclusive gat as a source.
            for (Element flow : inFlowsThatMatch) {
                model.setTarget(flow.getAttribute("id"), newExclGatMergeID);
            }
//it is possible that some outgoingFlows are empty, if such they
            //are not included in the matching system.
            for (Element flow : group) {
                for (Element inFlow : inFlows) {
                    String ID1 = flow.getAttribute("id");
                    String ID2 = inFlow.getAttribute("id");
                    if (ID1.equals(ID2)) {
                        //i have found an empty flow.
                        model.setTarget(ID1, newExclGatMergeID);
                    }

                }
            }
            //now we must create two new sequence flows, one from the
            //inclusive to the split, and one from the split to the mp.
            model.newSequenceFlow(inclusive.getAttribute("id"),
             newExclGatSplitID, process);
            model.newSequenceFlow(newExclGatMergeID, mp.getAttribute("id"), process);

            i++;
        }

        model.changeType(inclusive, "parallelGateway");
        model.changeType(mp, "parallelGateway");
    }


    /**
     * to see if two flows are part of the same path, we check if the
     * source of the second is either the target of the first flow,
     * or part of it's mandatory successors.
     *
     * @param fFlow
     * @param sFlow
     * @param model
     * @return
     * @throws XPathExpressionException
     */
    static boolean isAMatch (Element fFlow, Element sFlow, Model model) throws XPathExpressionException {

        Element target = model.getTarget(fFlow);
        Element source = model.getSource(sFlow);

        if (target.getAttribute("id").equals(source.getAttribute("id"))) {
            return true;
        }
        TravelAgency ta = new TravelAgency(model, target);
        for (Element e : ta.mandatorySuccessors) {
            String id = e.getAttribute("id");
            if (id.equals(source.getAttribute("id"))) {
                return true;
            }
        }
        return false;
    }


    static void apply (Model model, int aggregateBy) throws Exception {

        System.out.println("I'm applying rule REVERSE3 to model " + model.name);
        c(model);
        a(model);
        b(model);
    }


    /**
     * This takes a BPMN condition in String form and checks if it is a
     * tautology.
     * "1 == 1" returns true
     * "1 != 1" returns false
     * "1 > 2" returns false
     * WARNING! "a == a" returns false!
     * but "'a' == 'a'" returns true.
     * "True" returns true, as "TRUE" and "true"
     * NOTE that any other random word, such as "word" returns false!
     *
     * @param condition the condition of a sequence flow in String form
     * @return true if the condition is a tautology, and false if it's not.
     */
    static boolean isAlwaysTrue (String condition) {

        String c = condition.toLowerCase();
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            Object result = engine.eval(c);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }


    private static boolean allOutGoingFlowsAreAlwaysTrue (Element oldInclusive, Model model) throws XPathExpressionException {

        ArrayList<Element> outgoingFlows = model.getOutgoingFlows(oldInclusive);

        for (Element flow : outgoingFlows) {
            if (model.hasCondition(flow)) {
                if (! isAlwaysTrue(model.returnConditionString(flow))) {
                    System.out.println(flow.getAttribute("id") + " is not a " + "tautology");
                    return false; //there is at least one that is not always
                    // true
                }
            } else {
                System.out.println("there's at least one outgoing flow of " + oldInclusive.getAttribute("id") + " that has no condition");
                return false; // we should in principle never end up here,
                // because all outgoing flows should always have conditions
                // but still, if it has no condition it cant be always true.
            }
        }
        return true; // I haven't found a single outgoing flow that is not
        // always true.
    }


    /**
    *
    * TODO I thought about it and it should be different. An empty flow and
     *      a conditional flow should always be considered mutually
     *      exclusive, but
     *      two empty flows should not.
     *
     * This method accepts an array of Strings as an input, containing
     * different conditions.
     * It checks that they are all mutually exclusive.
     * It uses wolfram Alpha's API to do so.
     * For this reason the method checks that it is connected to the internet,
     * if it is not
     * //TODO Testa bene questa cosa sia che manchi internet sia che
     * //TODO manchi appID.
     * then it returns false and the transformation doesn't happen.
     *
     * @return false if they are not mutually exclusive, true if they are.
     */
    static boolean areMutuallyExclusive (String condition1,
     String condition2) throws IOException {

        String c1 = condition1.toLowerCase().replaceAll("\"", "");
        String c2 = condition2.toLowerCase().replaceAll("\"", "");
        String answer = "";
        String question = c1 + " && " + c2;

        String appid = ""; //OPTIONAL insert your appID here.
        if (appid.length() < 3) { //if it is there it is longer than 3 chars.
            appid = Main.checkWAAppID();
        }

        System.out.println("QUESTION: " + question);
        String url = "http://api.wolframalpha.com/v2/query?appid=" + appid +
        "&input" + "=" + URLEncoder.encode(question, "UTF-8");
        //System.out.println(url);
        URL wolframAlpha = new URL(url);
        URLConnection q = wolframAlpha.openConnection();
        BufferedReader in =
         new BufferedReader(new InputStreamReader(q.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();

        //System.out.println(sb.toString());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document =
             builder.parse(new InputSource(new StringReader(sb.toString())));
            NodeList pods = document.getElementsByTagName("pod");
            for (int i = 0 ; i < pods.getLength() ; i++) {
                Element pod = (Element) pods.item(i);
                if (pod.getAttribute("title").equals("Solutions")) {
                    Node plaintext =
                     pod.getElementsByTagName("plaintext").item(0);
                    System.out.println(plaintext.getTextContent());
                    answer = plaintext.getTextContent();
                } else if (pod.getAttribute("title").equals("Result")) {
                    Node plaintext =
                     pod.getElementsByTagName("plaintext").item(0);
                    System.out.println(plaintext.getTextContent());
                    answer = plaintext.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        answer = answer.toLowerCase();
        if (answer.contains("exist")) {
            //If I asked an impossible equation (e.g. "a>0 && a <0")
            //then WA would answer me "No solutions exist"

            //Note that the previous version of this IF included also
            //|| answer.contains("false")
            //But ultimately it din't make sense, because I risked including
            //occasions where one condition was always false and one was
            // always true.
            // I don't consider those to be mutually exclusive.
            return true;
        }
        return false;
    }


    private static boolean allOutgoingFlowsAreMutuallyExclusive (Element oldInclusive, Model model) throws XPathExpressionException {

        System.out.println("I'm analyzing gateway " + oldInclusive.getAttribute("id"));
        System.out.println("To see if all its outgoing flows are");
        System.out.println("mutually exclusive");

        ArrayList<Element> outgoingFlows = model.getOutgoingFlows(oldInclusive);

        return allFlowsAreMutuallyExclusive(outgoingFlows, model);
    }


    /**
     * TODO
     * @param flows
     * @param model
     * @return
     */

    private static boolean allFlowsAreMutuallyExclusive (ArrayList<Element> flows, Model model) {

        for (Element flow : flows) {
            for (Element flow2 : flows) {
                if (! (flow.getAttribute("id").equals(flow2.getAttribute("id")))) {
                    //Obviously I don't want to compare a flow with itself
//                because that will never be mutually exclusive with itself.

                    System.out.println("I'm comparing the conditions of flow:");
                    System.out.println(flow.getAttribute("id") + " and flow " + flow2.getAttribute("id"));
                    if (model.hasCondition(flow) && model.hasCondition(flow2)) {
                        try {
                            if (! areMutuallyExclusive(model.returnConditionString(flow), model.returnConditionString(flow2))) {
                                System.out.println("WA said they are not MEx");
                                return false; //if there's at least a couple
                                // that
                                // is not
                                // mutually exclusive, I can't apply
                                // Reverse3a on
                                // this
                                // inclusive split.
                            }
                        } catch (IOException e) {
                            System.out.println("WA had a problem");
                            return false;
                        }
                    } else {
                        System.out.println("at least one of the two OF has " + "no" + " " + "condition");
                        return false; //at least one flow has no condition.
                        //this means they can't be all mutually exclusive.
                    }
                }
            }
        }
        System.out.println("All OFs are MutuallyExclusive");
        return true;
        //I wasn't able to find a couple of outgoing flows that wasn't
        // mutually exclusive.
    }


    private static ArrayList<ArrayList<Element>> groupFlows (ArrayList<Element> flows, Model model) throws XPathExpressionException {

        ArrayList<String> flowsID = new ArrayList<>();
        for (Element flow : flows) {
            flowsID.add(flow.getAttribute("id"));
        }

        FlowGrouper fg = new FlowGrouper(flowsID);
        Set<Set<Set<String>>> possibleGroupings = fg.groups;
        Set<Set<Set<String>>> finalGroupings =
         returnFinalGroupings(possibleGroupings, model);
        System.out.println("FinalGroupings: " + finalGroupings.size());
        FlowGrouper.printFlows(finalGroupings);

        ArrayList<ArrayList<Element>> finalGroupingsArray = new ArrayList<>();
        finalGroupingsArray = fromSetToArray(finalGroupings, model);
        return finalGroupingsArray;
    }


    //TODO
    // provvisorio solo perché per testare i metodi era più
    // facile usare gli ID che gli elementi
    private static ArrayList<ArrayList<Element>> fromSetToArray (Set<Set<Set<String>>> finalGropings, Model model) throws XPathExpressionException {

        ArrayList<ArrayList<Element>> finalGroupingsArray = new ArrayList<>();
        for (Set<Set<String>> grouping : finalGropings) {

            for (Set<String> setOFlows : grouping) {
                ArrayList<Element> flowsSet = new ArrayList<>();
                for (String id : setOFlows) {
                    flowsSet.add(model.findElemById(id));
                }
                finalGroupingsArray.add(flowsSet);
            }
        }
        return finalGroupingsArray;
    }


    private static Set<Set<Set<String>>> returnFinalGroupings (Set<Set<Set<String>>> possibleGroupings, Model model) throws XPathExpressionException {

        Set<Set<Set<String>>> finalGroupings = new HashSet<>();

        for (Set<Set<String>> possibleGrouping : possibleGroupings) {
            boolean isGood = true; //it means that all the inner sets of
            // flows are mutually exclusive.
            // or that at least an inner set is smaller than two.

            for (Set<String> set : possibleGrouping) {
                if (set.size() < 2) {
                    //There's a set smaller than two in this grouping, so
                    // it's not good.
                    isGood = false;
                }
                ArrayList<Element> elementsFlows = new ArrayList<>();
                //TODO
                // provvisorio solo perché per testare i metodi era più
                // facile usare gli ID che gli elementi
                for (String s : set) {
                    elementsFlows.add(model.findElemById(s));
                }

                if (! allFlowsAreMutuallyExclusive(elementsFlows, model)) {
                    isGood = false;
                }
            }

            if (isGood) {
                finalGroupings.add(possibleGrouping);
                return finalGroupings; //as soon as i find a good one, I
                // return as I dont'need more than one.
            }
        }

        return finalGroupings;
    }
//    private static void groupFlows (ArrayList<Element> flows, Model model) {
//
//        int n = 2; //we start by dividing it in two groups.
//        //int remainderGroupSize = flows.size() - normalGroupSize*(n-1);
//
//        ArrayList<ArrayList<Element>> groups = new ArrayList<>();
//        //will store the final groups, (if we are able to create them).
//
//        boolean areOk = false; //True if all the flow's conditions are
//        // mutually exclusive in all groups.
//
//        // Tries until it finds a satisfactory grouping, or until it is not
//        // possible to further divide the flows.
////        while ((!areOk) && (normalGroupSize > 1)){
////            ArrayList<Element> temp = genPerm(flows, size);
////
////            areOk = true; //true unless we find a group in which they are
// not
////            // mEx.
////
////            allFlowsAreMutuallyExclusive(temp, model);
////            n++;
////        }
//
//    }
}



