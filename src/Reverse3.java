import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class Reverse3 {


    /**
     * TODO errore grave. Devo modificare solo se le condizioni sono true.
     * Quindi prima devo trovare gli split, vedere se le loro condizioni sono
     * entrambe true, e poi trovare il loro merge, magari con getPath.
     * Magari fare un metodo che si chiama "findFirstMeetingPoint", se non
     * l'ho già fatto in rule
     *
     * @param model the Model that will be changed
     */
    public static void a (Model model) throws Exception {

        System.out.println("I'm applying rule REVERSE3a to model " + model.path);

        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName(
        "inclusiveGateway");

        if (inclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse3a: there are no inclusive gateways " + "in this model");
        }

        // going through all of the inclusiveGateways in the model:
        for (int i = 0 ; i <= inclusiveGatewayInstances.getLength() ; i++) {
            Element oldInclusive = (Element) inclusiveGatewayInstances.item(0);
            //this will be the
            // element in case
            // NOTE Why this works?
            // Reason: After the gateway gets deleted from the model, it also
            // gets deleted from parallelGatewayInstances
            // For some reason. The problem is that now the element at index
            // 1 is now at index 0.
            // That's why we use the same index every time until the list is
            // empty.

            System.out.println("working on the " + (i + 1) + "nd " +
            "inclusiveGateway");
            System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

            String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

            // creating the substitute element in the position of the old one
            String newParallelGatewayId =
             model.newParallelGateway(oldInclusiveCoordinates[0],
              oldInclusiveCoordinates[1]);
            Element newParallelGateway =
             model.findElemById(newParallelGatewayId);
            model.replaceELement(oldInclusive, newParallelGateway);

            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(newParallelGateway);
            ArrayList<Element> incomingFlows =
             model.getIncomingFlows(newParallelGateway);

            //System.out.println("test " + outgoingFlows.size()); //UNLOCKTHIS
            // for testing
            //System.out.println("test " + incomingFlows.size()); //UNLOCKTHIS
            // for testing

            //merges are gateways that have more than one incoming flow
            //but only one outgoingFlow //TODO provare a usare isASplit
            if (incomingFlows.size() == 1 && outgoingFlows.size() > 1) {
                //then it's not a merge
                //we can thus change its outgoingFlows conditions:
                System.out.println("This is a not a merge. Its outgoingFlows "
                 + "will be changed");
                //TODO this has to become two separate methods
                //removing the condition from the outgoing flows of a split
                for (Element flow : outgoingFlows) {
                    Element condition = (Element) flow.getElementsByTagName(
                    "conditionExpression").item(0); //TODO this should work
                    // but test it.
                    flow.removeChild(condition);
                }
            } else {
                System.out.println("This is a merge. Its outgoingFlows will " + "not be changed");
            }
        }
    }


    /**
     * Changing inclusive to exclusive.
     */
    static void b (Model model) throws Exception {

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

            String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

            //creating the substitute element in the position of the old one
            String newExclusiveGatewayId =
             model.newInclusiveGateway(oldInclusiveCoordinates[0],
              oldInclusiveCoordinates[1]);
            Element newExclusiveGateway =
             model.findElemById(newExclusiveGatewayId);
            //replacing the two elements
            model.replaceELement(oldInclusive, newExclusiveGateway);

            //Here I don't need to distinguish between those that are merges
            // and those that are not.
            //I don't need to touch the incoming/outgoing flows either
        }
    }


    //TODO usalo anche in 3a.
    public static class Reverse3Construct {


        Element firstInclusive;
        Element firstInclusiveMeetingPoint;


        Reverse3Construct (Element firstInclusive,
         Element firstInclusiveMeetingPoint) {

            this.firstInclusive = firstInclusive;
            this.firstInclusiveMeetingPoint = firstInclusiveMeetingPoint;
        }

        //    public Reverse3cConstruct() {
        //     }

        //    public void setFirstInclusive(Element firstInclusive) {
        // this.firstInclusive = firstInclusive;
        //    }

        //    public void setFirstInclusiveMeetingPoint(Element
        // firstInclusiveMeetingPoint) {
        // this.firstInclusiveMeetingPoint = firstInclusiveMeetingPoint;
        //    }

        //    public boolean isComplete() {
        // if (firstInclusive.equals(null) || firstInclusiveMeetingPoint
        // .equals(null)) {
        //     return false;
        // } else {
        //     return true;
        // }
        //    }
    }


    /**
     * todo
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
            Element firstMeetingPoint = ta.mandatoryDeepSuccessors.get(0);
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

                String[] position =
                 model.calculatePositionOfNewNode(mySuccessors, firstInclusive);

                String newExclusiveID = model.newExclusiveGateway(position[0]
                , position[1]); //TODO make this method accept a 'position'
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
                String[] position =
                 model.calculatePositionOfNewNode(myPredecessors,
                  firstMeetingPoint);

                String newExclusiveID = model.newExclusiveGateway(position[0]
                , position[1]); //TODO make this method accept a 'position'
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
     * NOTE that any random word, such as "word" returns false!
     *
     * @param condition the condition of a sequence flow in String form
     * @return true if the condition is a tautology, and false if it's not.
     */
    static boolean isItATautology (String condition) {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            Object result = engine.eval(condition);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }


    static boolean allOutGoingFlowsAreTautolgies (Element oldInclusive,
     Model model) throws XPathExpressionException {

        ArrayList<Element> outgoingFlows = model.getOutgoingFlows(oldInclusive);

        for (Element flow : outgoingFlows) {
            if (model.hasCondition(flow)) {
                if (! isItATautology(model.returnConditionString(flow))) {
                    return false; //there is at least one that is not a
                    // tautology
                }
            } else {
                return false; // we should in principle never end up here,
                // because all outgoing flows should always have conditions
                // but still, if it has no condition it cant be a tautology.
            }
        }
        return true; // I haven't found a single outgoing flow that is not a
        // tautology.
    }


}
