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
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

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
            //Element inclusiveSplit = (Element) inclusiveGateways.item(i);

//            System.out.println("I'm analyzing inclusive gateway " +
//            inclusiveSplit.getAttribute("id"));

//            System.out.println("Is it a split? : " + model.isASplit
// (inclusiveSplit));

//            System.out.println("Are all outgoing flows of " +
// inclusiveSplit.getAttribute("id") + " ? : " +
// allOutGoingFlowsAreAlwaysTrue(inclusiveSplit, model));

            if (model.isASplit(inclusiveSplit) && allOutGoingFlowsAreAlwaysTrue(inclusiveSplit, model)) {
                TravelAgency ta = new TravelAgency(model, inclusiveSplit);
                Element firstMandatoryDeepSuccessor =
                 ta.firstMandatoryDeepSuccessor;
                if (firstMandatoryDeepSuccessor.getTagName().contains(
                "inclusiveGateway")) {

                    Coordinates oldInclusiveSplitCoordinates =
                     model.getPosition(inclusiveSplit);

                    // creating the substitute element in the position of
                    // the
                    // old one
                    String newParallelSplitId =
                     model.newParallelGateway(oldInclusiveSplitCoordinates);
                    Element newParallelSplit =
                     model.findElemById(newParallelSplitId);
                    model.replaceElement(inclusiveSplit, newParallelSplit);

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
                     model.newParallelGateway(oldInclusiveMergeCoordinates);
                    Element newParallelMerge =
                     model.findElemById(newParallelMergeId);
                    model.replaceElement(firstMandatoryDeepSuccessor,
                     newParallelMerge);
                }
            }
        }
    }


    static class Reverse3Construct {


        Element firstInclusive;
        Element firstInclusiveMeetingPoint;


        Reverse3Construct (Element firstInclusive,
         Element firstInclusiveMeetingPoint) {

            this.firstInclusive = firstInclusive;
            this.firstInclusiveMeetingPoint = firstInclusiveMeetingPoint;
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

            System.out.println("I'm analyzing inclusive gateway " + inclusiveSplit.getAttribute("id"));

            System.out.println("Is it a split? : " + model.isASplit(inclusiveSplit));

            System.out.println("Are all outgoing flows of " + inclusiveSplit.getAttribute("id") + " mutually exclusive ? : " + allOutgoingFlowsAreMutuallyExclusive(inclusiveSplit, model));

            if (model.isASplit(inclusiveSplit) && allOutgoingFlowsAreMutuallyExclusive(inclusiveSplit, model)) {
                TravelAgency ta = new TravelAgency(model, inclusiveSplit);
                Element firstMandatoryDeepSuccessor =
                 ta.firstMandatoryDeepSuccessor;
                if (firstMandatoryDeepSuccessor.getTagName().contains(
                "inclusiveGateway")) {

                    Coordinates oldInclusiveSplitCoordinates =
                     model.getPosition(inclusiveSplit);

                    // creating the substitute element in the position of
                    // the
                    // old one
                    String newExclusiveSplitId =
                     model.newExclusiveGateway(oldInclusiveSplitCoordinates);
                    Element newExclusiveSplit =
                     model.findElemById(newExclusiveSplitId);
                    model.replaceElement(inclusiveSplit, newExclusiveSplit);

                    //Unlike in reverse3a we don't need to delete the split's
                    // outgoing flows.

                    //Now we can substitute also the old merge with an
                    // inclusive gateway.
                    Coordinates oldInclusiveMergeCoordinates =
                     model.getPosition(firstMandatoryDeepSuccessor);

                    String newExclusiveMergeId =
                     model.newExclusiveGateway(oldInclusiveMergeCoordinates);
                    Element newExclusiveMerge =
                     model.findElemById(newExclusiveMergeId);
                    model.replaceElement(firstMandatoryDeepSuccessor,
                     newExclusiveMerge);
                }
            }
        }
    }


    /**
     * Changing inclusive to exclusive.
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

            System.out.println("working on the " + (i + 1) + "nd " +
            "inclusiveGateway");
            System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

            Coordinates oldInclusiveCoordinates = model.getPosition(oldInclusive);

            //creating the substitute element in the position of the old one
            String newExclusiveGatewayId =
             model.newInclusiveGateway(oldInclusiveCoordinates);
            Element newExclusiveGateway =
             model.findElemById(newExclusiveGatewayId);
            //replacing the two elements
            model.replaceElement(oldInclusive, newExclusiveGateway);

            //Here I don't need to distinguish between those that are merges
            // and those that are not.
            //I don't need to touch the incoming/outgoing flows either
        }
    }


    /**
     * TODO do tests
     */
    static void c (Model model, int aggregateBy) throws XPathExpressionException {

        System.out.println("I'm applying rule REVERSE3c to model " + model.name);

        if (aggregateBy < 2) {
            System.out.println();
            System.out.println("aggregateBy must be bigger than 1");
        }

        ArrayList<Reverse3Construct> constructs = new ArrayList<>();

        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName(
        "inclusiveGateway");

        if (inclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse3c: there are no inclusive gateways " + "in" + " this model");
        }

        ArrayList<Element> candidates = new ArrayList<>();
        //for now we only care about those inclusive Gateways that are splits

        for (int i = 0 ; i < inclusiveGatewayInstances.getLength() ; i++) {
            Element candidate = (Element) inclusiveGatewayInstances.item(i);
            if (model.isASplit(candidate) && model.getOutgoingFlows(candidate).size() > aggregateBy) {
                candidates.add(candidate);
            }
        }

        //ok now we have to find out if the firstMeetingPoint of that
        // candidate is also an inclusiveGateway.

        for (Element candidate : candidates) {
            TravelAgency ta = new TravelAgency(model, candidate);
            Element firstMeetingPoint = ta.mandatorySuccessors.get(0);
            if (firstMeetingPoint.getTagName().equals("bpmn:inclusiveGateway")) {
                //ok now I can create a construct.
                System.out.println(ta.paths.size());//UNLOCKTHIS
                System.out.println(candidate.getAttribute("name"));
                Reverse3Construct construct = new Reverse3Construct(candidate
                , firstMeetingPoint);
                constructs.add(construct);
            } else {
                System.out.println(ta.paths.size());//UNLOCKTHIS
                System.out.println(candidate.getAttribute("id"));
                System.out.println(candidate.getAttribute("name"));
                System.out.println(firstMeetingPoint.getAttribute("id"));
                //UNLOCKTHIS
            }
        }

        //now let's go through all constructs and do the necessary changes.
        for (Reverse3Construct construct : constructs) {

            Element firstInclusive = construct.firstInclusive;
            Element firstMeetingPoint = construct.firstInclusiveMeetingPoint;

            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(firstInclusive);
            ArrayList<Element> successors = model.getSuccessors(firstInclusive);

            ArrayList<Element> incomingFlows =
             model.getIncomingFlows(firstMeetingPoint);
            ArrayList<Element> predecessors =
             model.getPredecessors(firstMeetingPoint);

            int numberOfExclusives = outgoingFlows.size() / aggregateBy;
            //Also, since we're dealing with ints, we should add one more
            // prallel in the case there's a remainder
            if (outgoingFlows.size() % aggregateBy != 0) {
                numberOfExclusives++;
            }

            //ok now let's create the first series of exclusiveGateways:
            for (int n = 0 ; n < numberOfExclusives ; n++) {
                ArrayList<Element> mySuccessors = new ArrayList<>();

                //we have to find the successors for the new gateway, among
                // the ones that used to be
                //the successors of the original gateway
                //obviously, the last parallel might not have enough
                // successors to comply with the
                //parameter 'aggregateBy' but might have less
                for (int a = 1 ; a <= aggregateBy ; a++) {
                    if (successors.size() == 0) {
                        //System.out.println("no more successors to get");
                    } else if (outgoingFlows.size() > 0) {
                        mySuccessors.add(successors.get(0)); //the successor
                        // is now a successor of the new parallel
                        successors.remove(0); //it's not a successor of the
                        // original parallel anymore
                    }
                }
                //System.out.println("My successors size " + mySuccessors
                // .size());

                Coordinates position =
                 model.calculatePositionOfNewNode(mySuccessors, firstInclusive);

                String newExclusiveID = model.newExclusiveGateway(position); //TODO
                // make this method accept a 'position'
                // object
                Element newExclusive = model.findElemById(newExclusiveID);

                //newExclusive.setAttribute("name", "NEW");//UNLOCKTHIS

                //now that I have created my exclusive in a sensible position,
                //I can connect it to the original inclusive

                model.newSequenceFlow(firstInclusive.getAttribute("id"),
                 newExclusiveID);

                //now I can connect the elements in mySuccessors to the new
                // exclusive
                for (Element successor : mySuccessors) {

                    Element incomingFlow =
                     model.getIncomingFlows(successor).get(0); //we expect
                    // the successor to have only one incomingFlow of course.
                    model.setSource(incomingFlow.getAttribute("id"),
                     newExclusiveID);
                }

                //one last thing. We want to avoid having "one in, one out"
                // types of gateways, so to avoid this situation we want to
                //do one last check:
                if (model.isUselessGateway(newExclusive)) {
                    model.deleteUselesGateway(newExclusive);
                }
            }

            //now let's create the second series of exclusiveGateways
            for (int m = 0 ; m < numberOfExclusives ; m++) {
                ArrayList<Element> myPredecessors = new ArrayList<>();

                //we have to find the predecessors for the new gateway, among
                // the ones that used to be
                //the predecessors of the original gateway
                //obviously, the last inclusive might not have enough
                // predecessors to comply with the
                //parameter 'aggregateBy' but might have less
                for (int a = 1 ; a <= aggregateBy ; a++) {
                    if (predecessors.size() == 0) {
                        //System.out.println("no more predecessors to get");
                    } else if (incomingFlows.size() > 0) {
                        myPredecessors.add(predecessors.get(0)); //the
                        // predecessor is now a predecessor of the new parallel
                        predecessors.remove(0); //it's not a predecessor of
                        // the original parallel anymore
                    }
                }
                System.out.println("My predecessors size " + myPredecessors.size());
                Coordinates position =
                 model.calculatePositionOfNewNode(myPredecessors,
                  firstMeetingPoint);

                String newExclusiveID = model.newExclusiveGateway(position); //TODO
                // make this method accept a 'position'
                // object
                Element newExclusive = model.findElemById(newExclusiveID);

                //newExclusive.setAttribute("name", "NEW");//UNLOCKTHIS

                //now that I have created my parallel in a sensible position,
                //I can connect it to the original meeting point

                model.newSequenceFlow(newExclusiveID,
                 firstMeetingPoint.getAttribute("id"));

                //now I can connect the element in myPredecessors to the new
                // Parallel

                for (Element predecessor : myPredecessors) {
                    Element incomingFlow =
                     model.getOutgoingFlows(predecessor).get(0); //we expect
                    // the successor to have only one incomingFlow of course.
                    model.setTarget(incomingFlow.getAttribute("id"),
                     newExclusiveID);
                }

                //one last thing. We want to avoid having "one in, one out"
                // types of gateways, so to avoid this situation we want to
                //do one last check:
                if (model.isUselessGateway(newExclusive)) {
                    model.deleteUselesGateway(newExclusive);
                }
            }

            //and finally let's change the type of the starting Inclusive and
            // of the first meeting point:
            model.changeType(firstInclusive, "bpmn:parallelGateway");
            model.changeType(firstMeetingPoint, "bpmn:parallelGateway");
        }
    }


    //TODO DECIDE the ORDER AND EXPLAIN IN THE THESIS
    static void applyRule (Model model, int aggregateBy) throws Exception {

        System.out.println("I'm applying rule REVERSE3 to model " + model.name);
        c(model, aggregateBy);
        a(model);
        b(model);
    }

    //Code from:
    //https://ideone.com/7MhSj3


    /**
     * This takes a BPMN condition in String form and checks if it is a
     * tautology.
     * "1 == 1" returns true
     * "1 != 1" returns false
     * "1 > 2" returns false
     * WARNING! "a == a" returns false!
     * "True" returns true, as "TRUE" and "true"
     * NOTE that any other random word, such as "word" returns false!
     *
     * @param condition the condition of a sequence flow in String form
     * @return true if the condition is a tautology, and false if it's not.
     */
    static boolean areAlwaysTrue (String condition) {

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
                if (! areAlwaysTrue(model.returnConditionString(flow))) {
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
     * This method accepts an array of Strings as an input, containing
     * different conditions.
     * It checks that they are all mutually exclusive.
     * It uses wolfram Alpha's API to do so.
     * For this reason the method checks that it is connected to the internet,
     * if it is not //TODO
     * //TODO Testa bene questa cosa però, sia che manchi internet sia che
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
        if (appid.length() < 3) {
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

        for (Element flow : outgoingFlows) {
            for (Element flow2 : outgoingFlows) {
                if (!(flow.getAttribute("id").equals(flow2.getAttribute("id")))) {
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
                        System.out.println("at least one of the two OF has " +
                         "no" + " " + "condition");
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


}



