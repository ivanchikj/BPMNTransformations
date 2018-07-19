package bpmn.transformation2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import javax.el.StaticFieldELResolver;
import javax.print.Doc;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.javassist.tools.framedump;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CosNaming._BindingIteratorImplBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Rule3 {



    //TODO applyRule will actually become just a (simple) method calling other specific methods referring to part 1, part 2 of rule 3 etc
    //this method will be renamed and be called by said method. 

    public static void a(Model model) throws Exception {
	System.out.println("I'm applying Rule3a");

	// Here I'm creating a list of all the parallel gateways in the inputModel
	NodeList parallelGatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.getLength());


	if (parallelGatewayInstances.getLength() == 0) {System.out.println("RULE3a: there are no parallel gateways in this model");}

	//TODO the first part of the firstPart of rule 3 can be generalized for bot parallel and exclusive gateways

	// going through all of the parallelGateways in the model:
	for (int i = 0; i <= parallelGatewayInstances.getLength(); i++) {

	    Element oldParallel = (Element) parallelGatewayInstances.item(0); //this will be the element in case
	    //NOTE Why this works?
	    // Reason: After the gateway gets deleted from the model, it also gets deleted from parallelGatewayInstances
	    // For some reason. The problem is that now the element at index 1 is now at index 0.
	    // That why we use the same index every time until the list is empty.


	    System.out.println("working on the " + (i+1) + "nd parallelGateway");
	    System.out.println("The id of the element is " + oldParallel.getAttribute("id") );

	    String[] oldParallelCoordinates = model.getPosition(oldParallel);

	    // creating the substitute element in the position of the old one
	    String newInclusiveGatewayId = model.newInclusiveGateway(oldParallelCoordinates[0], oldParallelCoordinates[1]);
	    Element newInclusiveGateway = model.findElemById(newInclusiveGatewayId);
	    model.replaceELement(oldParallel, newInclusiveGateway); 

	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(newInclusiveGateway);
	    ArrayList<Element> incomingFlows = model.getIncomingFlows(newInclusiveGateway);

	    System.out.println("test " + outgoingFlows.size());
	    System.out.println("test " + incomingFlows.size());

	    //ASKANA ovviamente devo distinguere i gateway che sono anche merge dagli altri
	    //Un modo facile per distinguerli sarebbe dire "se hai solo un outgoingFlow allora sei un merge"
	    //Però in realtà, se io avessi un parallel che ha solo un input e solo un output
	    //allora probabilmente non lo considero un merge. E inoltre vorrei anche appicare la regola su di esso.
	    //Quindi sarebbe meglio se i merge fossero soltanto "i gateway che hanno un solo outgoingFlow ma che hanno più di
	    //un incomingFlow

	    //merges are gateways that have more than one incoming flow
	    //but only one outgoingFlow
	    if (!(incomingFlows.size() > 1 && outgoingFlows.size() == 1 )) {
		//then it's not a merge
		//we can thus change its outgoingFlows conditions:
		System.out.println("This is a not a merge. Its outgoingFlows will be changed");
		//TODO this has to become two separate methods
		//one that changes the condition and takes a string as an input
		//inside model.java
		//And one that creates a bpmn:conditionExpression element, inside newNode
		for (int f = 0; f < outgoingFlows.size(); f++) {
		    Element flow = outgoingFlows.get(f);
		    //this Cast only works because there are no line breaks as nodes. 
		    //I should redo this with a condition to assure that it's not the case
		    flow.setAttribute("name", "1==1"); // TODO this is just for the demo
		    Element condition = model.doc.createElement("bpmn:conditionExpression");
		    condition.setAttribute("xsi:type", "bpmn:tFormalExpression");
		    condition.appendChild(model.doc.createTextNode("1==1"));
		    flow.appendChild(condition);
		}
	    } else { System.out.println("This is a merge. Its outgoingFlows will not be changed");}
	}

    }

    public static void b (Model model) throws Exception {
	System.out.println("I'm applying Rule3b");

	// Here I'm creating a list of all the parallel gateways in the inputModel
	NodeList exclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:exclusiveGateway");
	System.out.println("number of exclusive gateway instances: " + exclusiveGatewayInstances.getLength());


	if (exclusiveGatewayInstances.getLength() == 0) {System.out.println("RULE3b: there are no exclusive gateways in this model");}

	//TODO the first part of the firstPart of rule 3 can be generalized for both parallel and exclusive gateways

	// going through all of the parallelGateways in the model:
	for (int i = 0; i <= exclusiveGatewayInstances.getLength(); i++) {

	    Element oldExclusive = (Element) exclusiveGatewayInstances.item(0); //this will be the element in case
	    //NOTE Why this works?
	    // Reason: After the gateway gets deleted from the model, it also gets deleted from parallelGatewayInstances
	    // For some reason. The problem is that now the element at index 1 is now at index 0.
	    // That why we use the same index every time until the list is empty.
	    // TODO decide if I want to change that to be more intuitive


	    System.out.println("working on the " + (i+1) + "nd exclusiveGateway");
	    System.out.println("working on " + oldExclusive.getAttribute("id"));
	    System.out.println("The id of the element is " + oldExclusive.getAttribute("id") );

	    String[] oldParallelCoordinates = model.getPosition(oldExclusive);

	    // creating the substitute element in the position of the old one
	    String newInclusiveGatewayId = model.newInclusiveGateway(oldParallelCoordinates[0], oldParallelCoordinates[1]);
	    Element newInclusiveGateway = model.findElemById(newInclusiveGatewayId);
	    model.replaceELement(oldExclusive, newInclusiveGateway); 

	    //Here I dont need to distinguish between those that are merges and those that are not.
	    //I dont need to touch the incoming/outgoing flows either
	}
    }

    //NOTE this has to execute before the other part of rule3 because
    //otherwise it will never be applicable (no parallel gateways will be found)
    //TODO qua dovrebbe funzionare senza fare niente, ma nella tesi ricordati di spiegare 
    //che ci sono anche le conditions
    public static Model c (Model startingModel) throws XPathExpressionException, TransformerConfigurationException, ParserConfigurationException, SAXException, IOException, TransformerException {
	System.out.println( " I'm applying Rule3c");

	Model model = startingModel;

	System.out.println("Going through every parallel Element in the gateway");

	NodeList parallelGatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	//but we only want splits
	ArrayList<Element> parallelGatewaySplitInstances = new ArrayList<Element>();

	for (int j = 0; j < parallelGatewayInstances.getLength(); j++) {
	    Element parallelElement = (Element) parallelGatewayInstances.item(j);
	    if (model.getOutgoingFlows(parallelElement).size() > 1) { //TOOD replace it with the future methods isAsplit or isAmerge
		parallelGatewaySplitInstances.add(parallelElement);
	    }
	}



	for (Element parallelGat : parallelGatewaySplitInstances) {


	    //first we have to check that all paths from the starting parallel gateway meet at the same parallel gateway
	    TravelAgency parallelTA = new TravelAgency(model, parallelGat);
	    Element firstParallelMeetingPoint = parallelTA.mandatoryDeepSuccessors.get(0); //This always works because we meet at the end anyway
	    //TODO what happens when I have more than one end... it should return false but it should not break.
	    //Right now I guess it would break.

	    System.out.println("The first meeting point is " + firstParallelMeetingPoint.getAttribute("id"));


	    if (firstParallelMeetingPoint.getTagName() == "bpmn:parallelGateway") { //of course it has to be a parallel.

		System.out.println("All paths from the parallel " + parallelGat.getAttribute("id") + " meet in the same parallel: " + firstParallelMeetingPoint.getAttribute("id"));

		//we now have to check that all followers of our Parallel Gateway are exclusive Gateways
		ArrayList<Element> exclusiveGatSuccessors = exclusiveGatSuccessors(parallelGat, model);
		ArrayList<Element> successorsOfParallel = model.getSuccessors(parallelGat);

		parallelGat.setAttribute("name", "primo passo startingPoint");

		//we now also have to check that all predecessors of our firstParallelMeetingPoint are exclusive gateways
		ArrayList<Element> exclusiveGatPredecessors = exclusiveGatPredecessors(firstParallelMeetingPoint, model);
		ArrayList<Element> predecessorsOfFirstMeetingPoint = model.getPredecessors(firstParallelMeetingPoint);

		firstParallelMeetingPoint.setAttribute("name", "primo passo meeting");

		System.out.println("Number of predecessors of the firstParallelMeetingPoint " + predecessorsOfFirstMeetingPoint.size());
		System.out.println("Number of exclusive predecessors " + exclusiveGatPredecessors.size());


		if (exclusiveGatSuccessors.size() == successorsOfParallel.size() && exclusiveGatPredecessors.size() == predecessorsOfFirstMeetingPoint.size()) {
		    System.out.println("Every follower of the parallel gateway " + parallelGat.getAttribute("id") + " is an exclusive gateway");
		    System.out.println("Every predecessor of the parallel gateway " + firstParallelMeetingPoint.getAttribute("id") + " is an exclusive gateway");

		    //if every follower of the original parallell is an exclusive gat.
		    //AND
		    //every predecessor of the first meeting point is is exclusive gat. we can now check the next condition


		    //the next condition is that no matter which path i take from my the exclusive gateways that follow the first parallel,
		    //I always end up in another exclusive gateway that is a predecessor of the first parallel meeting point

		    for (Element exclusiveGatSuccessorOfTheParallel : exclusiveGatSuccessors) {
			//using the exclusiveGat as a starting point i want to see if the first meeting point of
			//every path that gets out of it meets in another exclusiveGateway that is also a predecessor of the firstParallelMeetingPoint

			if (theyMeetInFrontOfTheFirstParallelMeetingPoint(exclusiveGatSuccessorOfTheParallel, firstParallelMeetingPoint, model)) {

			    Element exclusiveGatPredecessorOfTheFirstParallelMeetingPoint = findExclusiveMeetingPoint(exclusiveGatSuccessorOfTheParallel, firstParallelMeetingPoint, model).get(0);

			    //TODO DELETE THIS ArrayList<Element> emptyOutgoingFlows = outgoingFlowsPointingToExclusiveGateways(exclusiveGatSuccessorOfTheParallel, model, firstExclusiveMeetingPoint);

			    //				    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(exclusiveGatSuccessorOfTheParallel);
			    //				    System.out.println("Number of outgoingFlows: " + outgoingFlows.size());
			    ////				    System.out.println("Number of empty outgoingFlows: " + emptyOutgoingFlows.size());
			    //				    if (emptyOutgoingFlows.size() > 0 && outgoingFlows.size() - emptyOutgoingFlows.size() <= 1) {
			    //					//there's at least one outgoingFlow && maximum one non-empty flow 
			    //					System.out.println("there's at least one outgoingFlow && maximum one non-empty flow"); //add details about your starting point
			    //


			    //OK now every condition has been met and we can apply the rule


			    //First, let's transform our original parallel gateway and it's matching final gateways
			    //into inclusive gateways.
			    parallelGat.setAttribute("name", "CandidateParallel");

			    firstParallelMeetingPoint.setAttribute("name", "first parallel meeting point");

			    exclusiveGatSuccessorOfTheParallel.setAttribute("name", "valid exclusive");

			    exclusiveGatPredecessorOfTheFirstParallelMeetingPoint.setAttribute("name","first exclusive meeting point");

			    
			    //CONTINUA DA QUA li hai trovati tutti.

			    //Ora il problema è che se cambi le cose adesso rompi tutto. Perché al secondo giro i metodi che hai usato non
			    //trovano più gli elementi giusti.
			    //Devi quindi salvarti tutto e poi modificare le cose alla fine.
			    //Per ogni trasformazione bisogna salvarsi:
			    //il primo parallel e l'ultimo, e tutti gli exclusive iniziali separati dagli exclusive finali. Meglio creare un oggetto.





			}
		    }

		}
	    }
	}
	
	//applyRule3c here
	
	return model;
    }

    
    /**
     * 
     * @param constructs
     */
    private static void applyRule3c (ArrayList<Rule3cConstruct> constructs) {
	
	
    }
    
    
    private static ArrayList<Element> exclusiveGatSuccessors(Element parallelGat, Model model) throws XPathExpressionException {

	ArrayList<Element> exclusiveGatSuccessors = new ArrayList<Element>();
	ArrayList<Element> successors = model.getSuccessors(parallelGat);

	for (Element successor : successors) {
	    if (successor.getTagName().equals("bpmn:exclusiveGateway" )){
		exclusiveGatSuccessors.add(successor);
	    }
	}
	return exclusiveGatSuccessors;
    }

    private static ArrayList<Element> exclusiveGatPredecessors(Element parallelGatMeetingPoint, Model model) throws XPathExpressionException{
	ArrayList<Element> exclusiveGatPredecessors = new ArrayList<Element>();
	ArrayList<Element> predecessors = model.getPredecessors(parallelGatMeetingPoint);

	for (Element predecessor : predecessors) {
	    if (predecessor.getTagName().equals("bpmn:exclusiveGateway" )){
		exclusiveGatPredecessors.add(predecessor);
	    }
	}
	return exclusiveGatPredecessors;
    }

    private  static ArrayList<Element> outgoingFlowsPointingToExclusiveGateways (Element element, Model model, Element target) throws XPathExpressionException {
	ArrayList<Element> outgoingFlowsPointingtoExclGats = new ArrayList<Element>();

	ArrayList<Element> outGoingFlows = model.getOutgoingFlows(element);

	System.out.println("Number of outgoingFlows from element: " + element.getAttribute("id") + "IS: " + outGoingFlows.size() );

	System.out.println("TARGET ID:");
	System.out.println(target.getAttribute("id"));
	for(Element flow : outGoingFlows) {
	    System.out.println("Analyzing flow " + flow.getAttribute("id"));
	    System.out.println("FLOW POINTS TO:");
	    System.out.println(model.getTarget(flow).getAttribute("id"));

	    if (model.getTarget(flow).getAttribute("id").equals(target.getAttribute("id"))){
		outgoingFlowsPointingtoExclGats.add(flow);
	    }
	}

	return outgoingFlowsPointingtoExclGats;
    }


    private static ArrayList<Element> findExclusiveMeetingPoint (Element startingPoint, Element firstParallelMeetingPoint, Model model) throws XPathExpressionException {
	TravelAgency ta = new TravelAgency(model, startingPoint);
	ArrayList<Element> mandatoryDeepSuccessors = ta.getMandatoryDeepSuccessors();
	ArrayList<Element> appropriateExclusiveMeetingPoints = new ArrayList<Element>();
	ArrayList<Element> firstParallelMettingPointPredecessors = model.getPredecessors(firstParallelMeetingPoint);

	for (Element mandatoryDeepSuccessor : mandatoryDeepSuccessors) {
	    if (firstParallelMettingPointPredecessors.contains(mandatoryDeepSuccessor)){ //I dont know if this works, maybe I have to check the ID
		System.out.println("Yes it's a predecessor");
		appropriateExclusiveMeetingPoints.add(mandatoryDeepSuccessor);
	    } else {
		System.out.println("this is not a predecessor");
	    }

	}


	return appropriateExclusiveMeetingPoints;
    }


    private static boolean theyMeetInFrontOfTheFirstParallelMeetingPoint (Element startingPoint, Element firstParallelMeetingPoint, Model model) throws XPathExpressionException {

	if ( findExclusiveMeetingPoint(startingPoint, firstParallelMeetingPoint, model).size() > 0) {
	    return true;
	} else {
	    return false;
	}

    }

}
//	// create a collection of ParallelGateways ("A"):
//	Collection<ParallelGateway> parallelGatewayInstances = inputModel.getModelElementsByType(ParallelGateway.class);
//
//	//First i'looking for the necessary conditions to apply rule 3b
//	// FOR RULE 3B TO BE APPLICABLE:
//	// the parallel gateway can have 1 or more exclusive gateway as child elements
//	// (but only exclusive gateways; if there's something else, we stop and go to
//	// the next Parallel Gateway).
//	for (ParallelGateway parallelGateway : parallelGatewayInstances) {
//	    // ASKANA why is this a "query" instead of a collection as usual? ASKANA what is a FlowNode?
//	    Query<FlowNode> succedingNodes = parallelGateway.getSucceedingNodes();
//	    String a = succedingNodes.toString();
//	    System.out.println(a);
//	}
// the subsequent exclusive gateways ("B") have one or more conditional tasks as
// child element ("C").

// Both those tasks and the exclusive gateway must be connected to another
// exclusive gateway ("D"). (But not more than one i guess ? ASKANA)
