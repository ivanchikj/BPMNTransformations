import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class Rule3 {

    //TODO applyRule will actually become just a (simple) method calling
    // other specific methods referring to part 1, part 2 of rule 3 etc
    //this method will be renamed and be called by said method. 


    public static void a (Model model) throws Exception {

        System.out.println("I'm applying rule RULE3a to model " + model.name);

        // Here I'm creating a list of all the parallel gateways in the
        // inputModel
        NodeList parallelGatewayInstances =
         model.doc.getElementsByTagName(model.style("parallelGateway"));
        System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.getLength());

        if (parallelGatewayInstances.getLength() == 0) {
            System.out.println("RULE3a: there are no parallel gateways in " + "this model");
        } else {
            // going through all of the parallelGateways in the model:
            for (int i = 0 ; i <= parallelGatewayInstances.getLength() ; i++) {

                Element oldParallel =
                 (Element) parallelGatewayInstances.item(0); //this will
                // be the element in case
                //NOTE Why this works?
                // Reason: After the gateway gets deleted from the model, it
                // also gets deleted from parallelGatewayInstances
                // For some reason. The problem is that now the element at
                // index 1 is now at index 0.
                // That why we use the same index every time until the list
                // is empty.

                //System.out.println("working on the " + (i + 1) + "nd " +
                //"parallelGateway");

                //System.out.println("The id of the element is " +
                // oldParallel.getAttribute("id"));

                String[] oldParallelCoordinates =
                 model.getPosition(oldParallel);

                // TODO invece di creare un nuovo elemento e rimpiazzarlo, non
                // posso semplicemente cambiare il tagname?

                // creating the substitute element in the position of the old
                // one
                String newInclusiveGatewayId =
                 model.newInclusiveGateway(oldParallelCoordinates[0],
                  oldParallelCoordinates[1]);
                Element newInclusiveGateway =
                 model.findElemById(newInclusiveGatewayId);

                model.replaceELement(oldParallel, newInclusiveGateway);

                ArrayList<Element> outgoingFlows =
                 model.getOutgoingFlows(newInclusiveGateway);
                ArrayList<Element> incomingFlows =
                 model.getIncomingFlows(newInclusiveGateway);

                //TODO provare a usare isASplit
                //splits are gateways that have more than one outgoing flow
                //but only one incoming flows
                if (incomingFlows.size() == 1 && outgoingFlows.size() > 1) {

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
        NodeList exclusiveGatewayInstances =
         model.doc.getElementsByTagName(model.style("exclusiveGateway"));
        System.out.println("number of exclusive gateway instances: " + exclusiveGatewayInstances.getLength());

        if (exclusiveGatewayInstances.getLength() == 0) {
            System.out.println("RULE3b: there are no exclusive gateways in " + "this model");
        } else {

            //TODO the first part of the firstPart of rule 3 can be
            // generalized for both parallel and exclusive gateways

            // going through all of the parallelGateways in the model:
            for (int i = 0 ; i <= exclusiveGatewayInstances.getLength() ; i++) {

                Element oldExclusive =
                 (Element) exclusiveGatewayInstances.item(0); //this will be
                // the element in case
                //NOTE Why this works?
                // Reason: After the gateway gets deleted from the model, it
                // also gets deleted from parallelGatewayInstances
                // For some reason. The problem is that now the element at
                // index 1 is now at index 0.
                // That why we use the same index every time until the list
                // is empty.
                // TODO decide if I want to change that to be more intuitive

                System.out.println("working on the " + (i + 1) + "nd " +
                "exclusiveGateway");
                System.out.println("working on " + oldExclusive.getAttribute(
                "id"));
                System.out.println("The id of the element is " + oldExclusive.getAttribute("id"));

                String[] oldParallelCoordinates =
                 model.getPosition(oldExclusive);

                // creating the substitute element in the position of the old
                // one
                String newInclusiveGatewayId =
                 model.newInclusiveGateway(oldParallelCoordinates[0],
                  oldParallelCoordinates[1]);
                Element newInclusiveGateway =
                 model.findElemById(newInclusiveGatewayId);
                model.replaceELement(oldExclusive, newInclusiveGateway);

                //Here I dont need to distinguish between those that are
                // merges and those that are not.
                //I dont need to touch the incoming/outgoing flows either
            }
        }
    }


    private static class Rule3cConstruct {


        Element firstParallel;
        Element firstParallelMeetingPoint;
        ArrayList<Element> exclusiveSuccessors; //the exclusive-gateway
        // successors of the firstParallel
        ArrayList<Element> exclusivePredecessors; // the exclusive-gateway
        // successors of the firstParallelMeetingPoint


        Rule3cConstruct (Element firstParallel,
         Element firstParallelMeetingPoint,
          ArrayList<Element> exclusiveSuccessors,
           ArrayList<Element> exclusivePredecessors) {

            this.firstParallel = firstParallel;
            this.firstParallelMeetingPoint = firstParallelMeetingPoint;
            this.exclusiveSuccessors = exclusiveSuccessors;
            this.exclusivePredecessors = exclusivePredecessors;
        }


    }


    //NOTE this has to execute before the other part of rule3 because
    //otherwise it will never be applicable (no parallel gateways will be found)
    //TODO qua dovrebbe funzionare senza fare niente, ma nella tesi ricordati
    // di spiegare
    //che ci sono anche le conditions
    static void c (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE3c to model " + model.name);

        ArrayList<Rule3cConstruct> constructs = new ArrayList<>();

        System.out.println("Going through every parallel Element in the " +
        "gateway");

        NodeList parallelGatewayInstances = model.doc.getElementsByTagName(
        "parallelGateway");
        //but we only want splits
        ArrayList<Element> parallelGatewaySplitInstances = new ArrayList<>();

        for (int j = 0 ; j < parallelGatewayInstances.getLength() ; j++) {
            Element parallelElement =
             (Element) parallelGatewayInstances.item(j);
            if (model.getOutgoingFlows(parallelElement).size() > 1) { //TOOD
                // replace it with the future methods isAsplit or isAmerge
                parallelGatewaySplitInstances.add(parallelElement);
            }
        }

        for (Element parallelGat : parallelGatewaySplitInstances) {

            //first we have to check that all paths from the starting
            // parallel gateway meet at the same parallel gateway
            TravelAgency parallelTA = new TravelAgency(model, parallelGat);
            //parallelTA.printPaths();
            Element firstParallelMeetingPoint =
             parallelTA.mandatoryDeepSuccessors.get(0); //This always works
            // because we meet at the end anyway
            // TODO what happens when I have more than one end... it should
            // return false but it should not break.
            //NOTE the name 'firstParallelMeetingPoint' might make it seem
            // like there's another meeting point that comes before
            //the parallel but that is not a parallel. In fact, the first
            // meeting point HAS to be a parallel for us to be able to apply
            // the rule.

            System.out.println("The first meeting point is " + firstParallelMeetingPoint.getAttribute("id"));

            //Every parallel gat has a list of exclusivegateway successors
            // that we will need to save in our construct
            ArrayList<Element> candidateExclusiveSuccessors = new ArrayList<>();
            //But also every firstMeetingPoint has a list of exclusivegateway
            // successors that we will need to save in our construct
            ArrayList<Element> candidateExclusivePredecessors =
             new ArrayList<>();

            if (firstParallelMeetingPoint.getTagName().equals("bpmn" +
            ":parallelGateway")) { //of course it has to be a parallel for
                // the rule to be applied

                System.out.println("All paths from the parallel " + parallelGat.getAttribute("id") + " meet in the same parallel: " + firstParallelMeetingPoint.getAttribute("id"));

                //we now have to check that all followers of our Parallel
                // Gateway are exclusive Gateways
                ArrayList<Element> exclusiveGatSuccessors =
                 exclusiveGatSuccessors(parallelGat, model);
                ArrayList<Element> successorsOfParallel =
                 model.getSuccessors(parallelGat);

                parallelGat.setAttribute("name", "primo passo startingPoint");

                //we now also have to check that all predecessors of our
                // firstParallelMeetingPoint are exclusive gateways
                ArrayList<Element> exclusiveGatPredecessors =
                 exclusiveGatPredecessors(firstParallelMeetingPoint, model);
                ArrayList<Element> predecessorsOfFirstMeetingPoint =
                 model.getPredecessors(firstParallelMeetingPoint);

                firstParallelMeetingPoint.setAttribute("name",
                 "primo passo " + "meeting");

                System.out.println("Number of predecessors of the " +
                "firstParallelMeetingPoint " + predecessorsOfFirstMeetingPoint.size());
                System.out.println("Number of exclusive predecessors " + exclusiveGatPredecessors.size());

                if (exclusiveGatSuccessors.size() == successorsOfParallel.size() && exclusiveGatPredecessors.size() == predecessorsOfFirstMeetingPoint.size()) {
                    System.out.println("Every follower of the parallel " +
                    "gateway " + parallelGat.getAttribute("id") + " is an " + "exclusive gateway");
                    System.out.println("Every predecessor of the parallel " + "gateway " + firstParallelMeetingPoint.getAttribute("id") + " is an exclusive gateway");

                    //if every follower of the original parallell is an
                    // exclusive gat.
                    //AND
                    //every predecessor of the first meeting point is is
                    // exclusive gat. we can now check the next condition

                    //the next condition is that no matter which path i take
                    // from my the exclusive gateways that follow the first
                    // parallel,
                    //I always end up in another exclusive gateway that is a
                    // predecessor of the first parallel meeting point

                    for (Element exclusiveGatSuccessorOfTheParallel :
                     exclusiveGatSuccessors) {
                        //using the exclusiveGat as a starting point i want
                        // to see if the first meeting point of
                        //every path that gets out of it meets in another
                        // exclusiveGateway that is also a predecessor of the
                        // firstParallelMeetingPoint

                        if (theyMeetInFrontOfTheFirstParallelMeetingPoint(exclusiveGatSuccessorOfTheParallel, firstParallelMeetingPoint, model)) {

                            Element exclusiveGatPredecessorOfTheFirstParallelMeetingPoint = findExclusiveMeetingPoint(exclusiveGatSuccessorOfTheParallel, firstParallelMeetingPoint, model).get(0);

                            //exclusiveGatSuccessorOfTheParallel.setAttribute
                            // ("name", "valid exclusive");
                            candidateExclusiveSuccessors.add(exclusiveGatSuccessorOfTheParallel); //let's add our gateway to the list of exclusives that succeed the first parallel

                            //exclusiveGatPredecessorOfTheFirstParallelMeetingPoint.setAttribute("name","first exclusive meeting point");
                            candidateExclusivePredecessors.add(exclusiveGatPredecessorOfTheFirstParallelMeetingPoint); //let's add our gateway to the list of exclusives that preceed the first parallel meeting point
                            //parallelGat.setAttribute("name",
                            // "CandidateParallel");

                            //if all gateways following the first parallel
                            //and all gateways preceeding the first meeting
                            // point
                            //are in the list of gateways that are ready to
                            // accept the construct,
                            //then the firstParallel and the
                            // firstMeetingPoint are ready to accept the rule
                            // as well.
                            //NOTE that this condition is never met in the
                            // first pass, it can only become true after
                            //all exclusivegateways have been put in the
                            // exclusiveGatewaysuccessors/predecessors arrays
                            // respectively

                            System.out.println("Successors of first parallel "
                             + "size: " + successorsOfParallel.size());
                            System.out.println("candidate succesors: " + candidateExclusiveSuccessors.size());

                            System.out.println("Predecessors of meetin " +
                            "point:" + " " + predecessorsOfFirstMeetingPoint.size());
                            System.out.println("candidate successors: " + candidateExclusiveSuccessors.size());

                            if (successorsOfParallel.size() == candidateExclusiveSuccessors.size() && predecessorsOfFirstMeetingPoint.size() == candidateExclusivePredecessors.size()) {
                                System.out.println("I'm creating a new " +
                                "construct");
                                Rule3cConstruct construct =
                                 new Rule3cConstruct(parallelGat,
                                  firstParallelMeetingPoint,
                                   candidateExclusiveSuccessors,
                                    candidateExclusivePredecessors);
                                constructs.add(construct);
                            }
                        }
                    }
                }
            }
        }

        applyRule3c(constructs, model);
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
            Element firstParallel = construct.firstParallel;
            Element firstMeetingPoint = construct.firstParallelMeetingPoint;

            String firstParallelID = firstParallel.getAttribute("id");
            String firstMeetingPointID = firstMeetingPoint.getAttribute("id");

            //firstParallel.setAttribute("name", "PRIMO PARALLEL"); UNLOCKTHIS
            //firstMeetingPoint.setAttribute("name","PARALLEL MEETING POINT")
            // ;UNLOCKTHIS

            model.changeType(firstParallel, "bpmn:inclusiveGateway");
            model.changeType(firstMeetingPoint, "bpmn:inclusiveGateway");

            for (Element successor : construct.exclusiveSuccessors) {
                //successor.setAttribute("name", "valid successor");UNLOCKTHIS
                ArrayList<Element> outgoingFlows =
                 model.getOutgoingFlows(successor);

                for (Element flow : outgoingFlows) {
                    model.setSource(flow.getAttribute("id"), firstParallelID);
                }
                //before deleting the exclusive gateway, we have to delete
                // all it's incoming flows
                model.deleteIncomingFlows(successor);
                model.delete(successor.getAttribute("id"));
            }
            for (Element predecessor : construct.exclusivePredecessors) {
                //predecessor.setAttribute("name", "valid predecessor");
                // UNLOCKTHIS

                ArrayList<Element> incomingFlows =
                 model.getIncomingFlows(predecessor);
                for (Element flow : incomingFlows) {
                    model.setTarget(flow.getAttribute("id"),
                     firstMeetingPointID);
                }
                //before deleting the exclusive gateway, we have to delete
                // all it's outgoing flows
                model.deleteOutgoingFlows(predecessor);
                model.delete(predecessor.getAttribute("id"));
            }

//            Element newInclusiveStartingPoint = model.findElemById
// (firstMeetingPointID);
//            Element newInclusiveMeetingPoint = model.findElemById
// (firstParallelID);

            //let's clear the resulting multiple empty paths among the two
            // inclusive but one

            //keepOnlyOneEmptyPathAmong(newInclusiveStartingPoint,
            // newInclusiveMeetingPoint, model);
            //TODO this method doesn't work right now.
            //the program is not incorrect without it, but it could look better.
        }
    }

    //TODO this method doesn't work right now.
    //the program is not incorrect without it, but it could look better.


    //TODO another thing that we have to consider is that
    //if we merge the empty paths and different conditions are present, we
    // have to merge them.
    //this time instead of adding an && we should add an ||
    private static void keepOnlyOneEmptyPathAmong (Element startingPoint,
     Element firstMeetingPoint, Model model) throws XPathExpressionException {

        ArrayList<Element> outgoingFlows =
         model.getOutgoingFlows(startingPoint);
        ArrayList<Element> emptyFlowsAmong = new ArrayList<>();
        System.out.println("PROVA " + outgoingFlows.size()); //TODO This
        // prints the wrong number of paths, that's why it doesn't work

        for (Element flow : outgoingFlows) {
            System.out.println("Is this path empty?");
            String idOfTarget = model.getTarget(flow).getAttribute("id");
            String idOfMeetingPoint = firstMeetingPoint.getAttribute("id");
            if (idOfTarget.equals(idOfMeetingPoint)) {
                System.out.println("I've found an empty path"); //UNLOCKTHIS
                emptyFlowsAmong.add(flow);
            }
        }

        for (int i = 1 ; i < emptyFlowsAmong.size() ; i++) { // 'i' starts
            // from 1 because I will keep one of the flows. //TODO scrivi nella
            // tesi che fai sta cosa
            model.delete(emptyFlowsAmong.get(i).getAttribute("id"));
        }
    }


    private static ArrayList<Element> exclusiveGatSuccessors (Element parallelGat, Model model) throws XPathExpressionException {

        ArrayList<Element> exclusiveGatSuccessors = new ArrayList<>();
        ArrayList<Element> successors = model.getSuccessors(parallelGat);

        for (Element successor : successors) {
            if (successor.getTagName().equals("bpmn:exclusiveGateway")) {
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
            if (predecessor.getTagName().equals("bpmn:exclusiveGateway")) {
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
         ta.getMandatoryDeepSuccessors();
        ArrayList<Element> appropriateExclusiveMeetingPoints =
         new ArrayList<>();
        ArrayList<Element> firstParallelMettingPointPredecessors =
         model.getPredecessors(firstParallelMeetingPoint);

        for (Element mandatoryDeepSuccessor : mandatoryDeepSuccessors) {
            if (firstParallelMettingPointPredecessors.contains(mandatoryDeepSuccessor)) { //I dont know if this works, maybe I have to check the ID
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
