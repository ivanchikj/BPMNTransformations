import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class Rule3 {

    //TODO apply will actually become just a (simple) method calling
    // other specific methods referring to part 1, part 2 of rule 3 etc
    //this method will be renamed and be called by said method. 

    //TODO fai un metodo che prende un elemento e ritorna il
    // firstMandatoryMeeting point, mettilo in model anche se ovviamente usa
    // i metodi di TravelAgency


    public static void a (Model model) throws Exception {

        System.out.println("I'm applying rule RULE3a to model " + model.name);

        // Here I'm creating a list of all the parallel gateways in the
        // inputModel
        ArrayList<Element> parallelGatewayInstances =
         model.findElementsByType("parallelGateway");

        System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.size());

        if (parallelGatewayInstances.size() == 0) {
            System.out.println("RULE3a: there are no parallel gateways in " + "this model");
        } else {
            // going through all of the parallelGateways in the model:
            for (Element oldParallel : parallelGatewayInstances) {

                System.out.println("The id of the element is " + oldParallel.getAttribute("id"));

                Coordinates oldParallelCoordinates =
                 model.getPosition(oldParallel);

                String newInclusiveGatewayId =
                 model.newInclusiveGateway(oldParallelCoordinates);
                Element newInclusiveGateway =
                 model.findElemById(newInclusiveGatewayId);

                model.replaceElement(oldParallel, newInclusiveGateway);

                ArrayList<Element> outgoingFlows =
                 model.getOutgoingFlows(newInclusiveGateway);
                ArrayList<Element> incomingFlows =
                 model.getIncomingFlows(newInclusiveGateway);

                //splits are gateways that have more than one outgoing flow
                //but only one incoming flows
                if (model.isASplit(newInclusiveGateway)) {

                    //then it's a split
                    //we can thus change its outgoingFlows conditions:
                    System.out.println("This is a not a merge. Its " +
                    "outgoingFlows will be changed");
                    //TODO this has to become two separate methods
                    //one that changes the condition and takes a string as an
                    //input
                    //inside model.java
                    //And one that creates a bpmn:conditionExpression
                    //element, inside newNode
                    for (Element flow : outgoingFlows) {
                        //this Cast only works because there are no line
                        // breaks as nodes.
                        //I should redo this with a condition to assure that
                        // it's not the case
                        //flow.setAttribute("name", "1==1"); // UNLOCKTHIS
                        // this is just for the demo

                        Element condition = model.doc.createElement("bpmn" +
                        ":conditionExpression");
                        condition.setAttribute("xsi:type", "bpmn" +
                        ":tFormalExpression");
                        condition.appendChild(model.doc.createTextNode("1==1"));
                        flow.appendChild(condition);
                    }
                } else {
                    System.out.println("This is a merge. Its outgoingFlows " + "will not be changed");
                }
            }
        }
    }


    static void b (Model model) throws Exception {

        System.out.println("I'm applying rule RULE3b to model " + model.name);
        // Here I'm creating a list of all the parallel gateways in the
        // inputModel
        ArrayList<Element> exclusiveGatewayInstances =
         model.findElementsByType("exclusiveGateway");
        System.out.println("number of exclusive gateway instances: " + exclusiveGatewayInstances.size());

        if (exclusiveGatewayInstances.size() == 0) {
            System.out.println("RULE3b: there are no exclusive gateways in " + "this model");
        } else {

            //TODO the first part of the firstPart of rule 3 can be
            // generalized for both parallel and exclusive gateways

            // going through all of the parallelGateways in the model:
            for (Element oldExclusive : exclusiveGatewayInstances) {

//

                System.out.println("working on " + oldExclusive.getAttribute(
                "id"));
                System.out.println("The id of the element is " + oldExclusive.getAttribute("id"));

                Coordinates oldParallelCoordinates =
                 model.getPosition(oldExclusive);

                // creating the substitute element in the position of the old
                // one
                String newInclusiveGatewayId =
                 model.newInclusiveGateway(oldParallelCoordinates);
                Element newInclusiveGateway =
                 model.findElemById(newInclusiveGatewayId);
                model.replaceElement(oldExclusive, newInclusiveGateway);

                //Here I dont need to distinguish between those that are
                // merges and those that are not.
                //I dont need to touch the incoming/outgoing flows either
            }
        }
    }


    private static class Rule3cConstruct {


        Element firstParallel;
        Element firstMeetingPoint;
        ArrayList<Element> exclusiveSuccessors; //the exclusive-gateway
        // successors of the firstParallel
        ArrayList<Element> exclusivePredecessors; // the exclusive-gateway
        // successors of the firstMeetingPoint


        Rule3cConstruct (Element firstParallel,
         Element firstMeetingPoint,
          ArrayList<Element> exclusiveSuccessors,
           ArrayList<Element> exclusivePredecessors) {

            this.firstParallel = firstParallel;
            this.firstMeetingPoint = firstMeetingPoint;
            this.exclusiveSuccessors = exclusiveSuccessors;
            this.exclusivePredecessors = exclusivePredecessors;
        }


        void printInfo () {

            System.out.println("Rule 3c Construct composed of:");
            System.out.println("First parallel: " + firstParallel.getAttribute("id"));
            System.out.println("First meeting point" + firstMeetingPoint.getAttribute("id"));
            System.out.println("Exclusive successors : ");
            printArray(exclusiveSuccessors);
            System.out.println("Exclusive predecessors: ");
            printArray(exclusivePredecessors);
        }


        private void printArray (ArrayList<Element> arrayList) {

            for (Element el : arrayList) {
                System.out.println(el.getAttribute("id"));
            }
        }


    }


    //NOTE this has to execute before the other part of rule3 because
    //otherwise it will never be applicable (no parallel gateways will be found)
    static void c (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE3c to model " + model.name);

        ArrayList<Rule3cConstruct> constructs = new ArrayList<>();

        System.out.println("Going through every parallel Element in the " +
        "gateway");

        ArrayList<Element> parallelGatewayInstances =
         model.findElementsByType("parallelGateway");
        //but we only want splits
        ArrayList<Element> parallelGatewaySplitInstances = new ArrayList<>();

        for (Element parallelGat : parallelGatewayInstances) {
            System.out.println("Analyzing parallel gat: " + parallelGat.getAttribute("id"));

//            System.out.println(parallelGat.getAttribute("id"));
//            System.out.println("OF: ");
//            ArrayList<Element> ofs = model.getOutgoingFlows(parallelGat);
//            for (Element of : ofs) {
//                System.out.println(of.getAttribute("id"));
//            }
//            System.out.println("SUCC: ");
//            ArrayList<Element> succs = model.getSuccessors(parallelGat);
//            for (Element s : succs){
//                System.out.println(s.getAttribute("id"));
//            }

            if (model.isASplit(parallelGat)) {
                System.out.println("ParallelGat: " + parallelGat.getAttribute("id") + " is a split");
                parallelGatewaySplitInstances.add(parallelGat);
            }
        }

        for (Element parallelGat : parallelGatewaySplitInstances) {

            //first we have to check that all paths from the starting
            // parallel gateway meet at the same parallel gateway
            TravelAgency parallelTA = new TravelAgency(model, parallelGat);
            //parallelTA.printPaths();

            if (parallelTA.firstMandatorySuccessor != null) {

                Element firstParallelMP =
                 parallelTA.firstMandatorySuccessor;

                // This always works
                // because we meet at the merge anyway. If there's no merge,
                // the model must have more than one end, and this is the
                // exact spot
                // where the model divides in two paths that never meet.
                // If that is the case, rule3c cannot be applied to that
                // parallel
                // anyway.

                System.out.println("The first meeting point is " + firstParallelMP.getAttribute("id"));

                //of course it has to be a parallel for
                // the rule to be applied
                if (firstParallelMP.getTagName().equals(model.style(
                "parallelGateway"))) {

                    System.out.println("All paths from the parallel " + parallelGat.getAttribute("id") + " meet in the same parallel: " + firstParallelMP.getAttribute("id"));

                    // we now have to check that all followers of our Parallel
                    // Gateway are exclusive Gateways
                    //and that every predecessor of the meeting point
                    //is also an exclusive gateway.
                    if (areAllSuccessorsExclusiveGateways(parallelGat, model) && areAllPredecessorsExclusiveGateway(firstParallelMP, model)) {

                        ArrayList<Element> parallelGatSuccessors =
                         model.getSuccessors(parallelGat);

                        ArrayList<Element> mpPredecessors =
                         model.getPredecessors(firstParallelMP);

                        System.out.println("I'm creating a " + "new " +
                        "construct");
                        Rule3cConstruct construct =
                         new Rule3cConstruct(parallelGat, firstParallelMP,
                          parallelGatSuccessors, mpPredecessors);
                        constructs.add(construct);
                    }
                }
            }

        }
        applyRule3c(constructs, model);
    }


    private static boolean areAllSuccessorsExclusiveGateways (Element parallelGat, Model model) throws XPathExpressionException {

        ArrayList<Element> successors = model.getSuccessors(parallelGat);
        System.out.println(parallelGat.getAttribute("id"));
        for (Element s : successors) {
            System.out.println(s.getAttribute("id"));
        }

        for (Element successor : successors) {
            if (! successor.getTagName().equals(model.style("exclusiveGateway"
            ))) {

                System.out.println("I have found a successor of " + parallelGat.getAttribute("id") + " that is not an exclusive gateway");
                System.out.println("It is successor : " + successor.getAttribute("id"));
                return false; //I have found a successor that is not an
                // exclusive
                // gateway
            }
        }
        System.out.println("All successors of " + parallelGat.getAttribute(
        "id") + "are exclusive gateways");
        return true;
    }


    private static boolean areAllPredecessorsExclusiveGateway (Element firstParallelMP, Model model) throws XPathExpressionException {

        ArrayList<Element> predecessors =
         model.getPredecessors(firstParallelMP);

        for (Element predecessor : predecessors) {
            if (! predecessor.getTagName().equals(model.style(
            "exclusiveGateway"))) {
                System.out.println("I have found a predecessor of " + firstParallelMP.getAttribute("id") + " that is not an exclusive gateway");
                return false; //I have found a predecessor that is not an
                // exclusive
                // gateway
            }
        }
        return true;
    }


    /**
     * Once we have find all the parts of the model that we want to
     * transform, we can actually transform them.
     *
     * @param constructs an arraylist of all the construct (a series of BPMN
     *                   Elements connected in such a way to make the
     *                   application of rule 3c possible) on which to apply
     *                   rule 3c
     */
    private static void applyRule3c (ArrayList<Rule3cConstruct> constructs,
     Model model) throws XPathExpressionException {

        for (Rule3cConstruct construct : constructs) {
            construct.printInfo(); //UNLOCKTHIS
            Element firstParallel = construct.firstParallel;
            Element firstMeetingPoint = construct.firstMeetingPoint;

            Coordinates c1 = model.getPosition(firstParallel);
            Coordinates c2 = model.getPosition(firstMeetingPoint);

            String newInclusiveID = model.newInclusiveGateway(c1);
            String newMPID = model.newInclusiveGateway(c2);
//
            String oldParallelID = firstParallel.getAttribute("id");
            String oldMPID = firstMeetingPoint.getAttribute("id");
//            //I need those because when replacing the elements, the ID of the
//            // old element remains.
//

            for (Element successor : construct.exclusiveSuccessors) {
                ArrayList<Element> outgoingFlows =
                 model.getOutgoingFlows(successor);
                for (Element flow : outgoingFlows) {
                    model.setSource(flow.getAttribute("id"), oldParallelID);
                }
                String succID = successor.getAttribute("id");
                model.deleteIncomingFlows(successor);
                System.out.println("PIZZA");
                System.out.println("Sto cancellando: " + succID);
                model.delete(succID);
            }

            for (Element predecessor : construct.exclusivePredecessors) {
                ArrayList<Element> incomingFlows =
                 model.getIncomingFlows(predecessor);
                for (Element flow : incomingFlows) {
                    model.setTarget(flow.getAttribute("id"), oldMPID);
                }
                String predID = predecessor.getAttribute("id");

                model.deleteOutgoingFlows(predecessor);

                System.out.println("PIZZA");
                System.out.println("Sto cancellando: " + predID);
                model.delete(predID);
            }

            Element newInclusive = model.findElemById(newInclusiveID);
            Element newMP = model.findElemById(newMPID);

            model.replaceElement(firstParallel, newInclusive);

            model.replaceElement(firstMeetingPoint, newMP);

            //let's clear the resulting multiple empty paths among the two
            // inclusive but one

            //keepOnlyOneEmptyPathAmong(newInclusive, newMP, model);
            //TODO keepOnlyOneEmptyPathAmong works, but
            //the program is correct even without it.
            //Actually without it it might even be slightly better from
            //an execution point of view.
        }
    }


    //if we merge the empty paths and different conditions are present, we
    // have to merge their conditions as well.
    // We should add an || between them when we merge them.
    private static void keepOnlyOneEmptyPathAmong (Element startingPoint,
     Element firstMeetingPoint, Model model) throws XPathExpressionException {

        String resultingCondition = "";
        ArrayList<Element> outgoingFlows =
         model.getOutgoingFlows(startingPoint);
        ArrayList<Element> emptyFlowsAmong = new ArrayList<>();

        for (Element flow : outgoingFlows) {
//            System.out.println("Is this path empty?");
            String idOfTarget = model.getTarget(flow).getAttribute("id");
            String idOfMeetingPoint = firstMeetingPoint.getAttribute("id");
            if (idOfTarget.equals(idOfMeetingPoint)) {
                System.out.println("I've found an empty path"); //UNLOCKTHIS
                emptyFlowsAmong.add(flow);

                //if it has a condition, let's add it to the
                //condition string that the remaining empty path has.
                if (model.hasCondition(flow)) {

                    //if that's the first condition i don't have to put || in
                    // front.
                    if (resultingCondition.length() == 0) {
                        resultingCondition =
                         resultingCondition + model.returnConditionString(flow);
                    } else {
                        //it it's not the first condition I have to put || in
                        // between the two.
                        resultingCondition =
                         resultingCondition + " || " + model.returnConditionString(flow);
                    }
                }
            }
        }

        for (int i = 1 ; i < emptyFlowsAmong.size() ; i++) { // 'i' starts
            // from 1 because I will keep one of the flows.
            model.delete(emptyFlowsAmong.get(i).getAttribute("id"));
        }

        System.out.println("CIAOOOOOOO");
        System.out.println(resultingCondition.toString());
        String condition = resultingCondition.toString();
        //I will now have only one remaining flow
        //let's add the resultingCondition to it.
        model.applyCondition(emptyFlowsAmong.get(0), condition);
    }


    private static ArrayList<Element> exclusiveGatSuccessors (Element parallelGat, Model model) throws XPathExpressionException {

        ArrayList<Element> exclusiveGatSuccessors = new ArrayList<>();
        ArrayList<Element> successors = model.getSuccessors(parallelGat);

        for (Element successor : successors) {
            if (successor.getTagName().equals(model.style("exclusiveGateway"))) {
                exclusiveGatSuccessors.add(successor);
            }
        }
        return exclusiveGatSuccessors;
    }


    private static ArrayList<Element> exclusiveGatPredecessors (Element parallelGatMeetingPoint, Model model) throws XPathExpressionException {

        ArrayList<Element> exclusiveGatPredecessors = new ArrayList<>();
        ArrayList<Element> predecessors =
         model.getPredecessors(parallelGatMeetingPoint);

        for (Element predecessor : predecessors) {
            if (predecessor.getTagName().equals(model.style("exclusiveGateway"
            ))) {
                exclusiveGatPredecessors.add(predecessor);
            }
        }
        return exclusiveGatPredecessors;
    }


    private static ArrayList<Element> outgoingFlowsPointingToExclusiveGateways (Element element, Model model, Element target) throws XPathExpressionException {

        ArrayList<Element> outgoingFlowsPointingtoExclGats = new ArrayList<>();

        ArrayList<Element> outGoingFlows = model.getOutgoingFlows(element);

        System.out.println("Number of outgoingFlows from element: " + element.getAttribute("id") + "IS: " + outGoingFlows.size());

        System.out.println("TARGET ID:");
        System.out.println(target.getAttribute("id"));
        for (Element flow : outGoingFlows) {
            System.out.println("Analyzing flow " + flow.getAttribute("id"));
            System.out.println("FLOW POINTS TO:");
            System.out.println(model.getTarget(flow).getAttribute("id"));

            if (model.getTarget(flow).getAttribute("id").equals(target.getAttribute("id"))) {
                outgoingFlowsPointingtoExclGats.add(flow);
            }
        }

        return outgoingFlowsPointingtoExclGats;
    }


    private static ArrayList<Element> findExclusiveMeetingPoint (Element startingPoint, Element firstParallelMeetingPoint, Model model) throws XPathExpressionException {

        TravelAgency ta = new TravelAgency(model, startingPoint);
        ArrayList<Element> mandatoryDeepSuccessors =
         ta.getMandatorySuccessors();
        ArrayList<Element> appropriateExclusiveMeetingPoints =
         new ArrayList<>();
        ArrayList<Element> firstParallelMeetingPointPredecessors =
         model.getPredecessors(firstParallelMeetingPoint);

        for (Element mandatoryDeepSuccessor : mandatoryDeepSuccessors) {
            if (firstParallelMeetingPointPredecessors.contains(mandatoryDeepSuccessor)) { //I don't know if this works, maybe I have to check the ID
                System.out.println("Yes it's a predecessor");
                appropriateExclusiveMeetingPoints.add(mandatoryDeepSuccessor);
            } else {
                System.out.println("this is not a predecessor");
            }
        }

        return appropriateExclusiveMeetingPoints;
    }


    private static boolean theyMeetInFrontOfTheFirstParallelMeetingPoint (Element startingPoint, Element firstParallelMeetingPoint, Model model) throws XPathExpressionException {

        return findExclusiveMeetingPoint(startingPoint,
         firstParallelMeetingPoint, model).size() > 0;
    }


    /**
     * TODO spiega l'ordine nella tesi
     *
     * @param model the model that will be transformed.
     */
    public static void all (Model model) throws Exception {
        //System.out.println("I'm applying rule RULE3 to model " + model.name);

        c(model);
        a(model);
        b(model);
    }


}

//	// create a collection of ParallelGateways ("A"):
//	Collection<ParallelGateway> parallelGatewayInstances = inputModel
// .getModelElementsByType(ParallelGateway.class);
//
//	//First i'looking for the necessary conditions to apply rule 3b
//	// FOR RULE 3B TO BE APPLICABLE:
//	// the parallel gateway can have 1 or more exclusive gateway as child
// elements
//	// (but only exclusive gateways; if there's something else, we stop and
// go to
//	// the next Parallel Gateway).
//	for (ParallelGateway parallelGateway : parallelGatewayInstances) {
//	    // ASKANA why is this a "query" instead of a collection as usual?
// ASKANA what is a FlowNode?
//	    Query<FlowNode> succedingNodes = parallelGateway.getSucceedingNodes();
//	    String a = succedingNodes.toString();
//	    System.out.println(a);
//	}
// the subsequent exclusive gateways ("B") have one or more conditional tasks as
// child element ("C").

// Both those tasks and the exclusive gateway must be connected to another
// exclusive gateway ("D"). (But not more than one i guess ? ASKANA)
