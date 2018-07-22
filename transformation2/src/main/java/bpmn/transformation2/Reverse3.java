package bpmn.transformation2;

import java.util.ArrayList;
import java.util.function.ToDoubleBiFunction;

import com.sun.org.apache.xpath.internal.operations.Mod;

import org.apache.taglibs.standard.lang.jstl.AndOperator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

public class Reverse3 {

    /**
     *
     * @param startingModel
     * @throws Exception
     */
    public static void a (Model startingModel) throws Exception {
	Model model = startingModel;
	System.out.println("I'm reverting Rule3a");

	NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");


	if (inclusiveGatewayInstances.getLength() == 0) {
	    System.out.println("Reverse3a: there are no inclusive gateways in this model");
	}

	// going through all of the inclusiveGateways in the model:
	for (int i = 0; i <= inclusiveGatewayInstances.getLength(); i++) {


	    Element oldInclusive = (Element) inclusiveGatewayInstances.item(0); //this will be the element in case
	    //NOTE Why this works?
	    // Reason: After the gateway gets deleted from the model, it also gets deleted from parallelGatewayInstances
	    // For some reason. The problem is that now the element at index 1 is now at index 0.
	    // That's why we use the same index every time until the list is empty.


	    System.out.println("working on the " + (i + 1) + "nd inclusiveGateway");
	    System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

	    String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

	    // creating the substitute element in the position of the old one
	    String newParallelGatewayId = model.newParallelGateway(oldInclusiveCoordinates[0], oldInclusiveCoordinates[1]);
	    Element newParallelGateway = model.findElemById(newParallelGatewayId);
	    model.replaceELement(oldInclusive, newParallelGateway);

	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(newParallelGateway);
	    ArrayList<Element> incomingFlows = model.getIncomingFlows(newParallelGateway);

	    System.out.println("test " + outgoingFlows.size()); //UNLOCKTHIS for testing
	    System.out.println("test " + incomingFlows.size()); //UNLOCKTHIS for testing

	    //TODO ovviamente devo distinguere i gateway che sono anche merge dagli altri
	    //Un modo facile per distinguerli sarebbe dire "se hai solo un outgoingFlow allora sei un merge"
	    //Però in realtà, se io avessi un parallel che ha solo un input e solo un output
	    //allora probabilmente non lo considero un merge. E inoltre vorrei anche appicare la regola su di esso.
	    //Quindi sarebbe meglio se i merge fossero soltanto "i gateway che hanno un solo outgoingFlow ma che hanno più di
	    //un incomingFlow

	    //TODO questa parte deve diventare un metodo "isNotASplit" oppure rispettivamente "isNotAMerge" nella classe model
	    //merges are gateways that have more than one incoming flow
	    //but only one outgoingFlow
	    if (!(incomingFlows.size() > 1 && outgoingFlows.size() == 1)) {
		//then it's not a merge
		//we can thus change its outgoingFlows conditions:
		System.out.println("This is a not a merge. Its outgoingFlows will be changed");
		//TODO this has to become two separate methods
		//removing the condition from the outgoing flows of a split
		for (int f = 0; f < outgoingFlows.size(); f++) {
		    Element flow = outgoingFlows.get(f);
		    Element condition = (Element) flow.getElementsByTagName("bpmn:conditionExpression").item(0); //TODO this should work but test it.
		    flow.removeChild(condition);
		}
	    } else {
		System.out.println("This is a merge. Its outgoingFlows will not be changed");
	    }
	}
    }


    //TODO

    /**
     * Changing inclusive to exclusive.
     * @param startingModel
     * @throws Exception
     */
    public static void b (Model startingModel) throws Exception {
	System.out.println("I'm reverting Rule3b");
	Model model = startingModel;
	// Here I'm creating a list of all the inclusive gateways in the inputModel
	NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");
	System.out.println("number of inclusive gateway instances: " + inclusiveGatewayInstances.getLength());


	if (inclusiveGatewayInstances.getLength() == 0) {
	    System.out.println("Reverse3b: there are no exclusive gateways in this model");
	}

	//TODO the first part of the firstPart of r3a and r3b can be generalized for bot parallel and exclusive gateways

	// going through all of the parallelGateways in the model:
	for (int i = 0; i <= inclusiveGatewayInstances.getLength(); i++) {

	    Element oldInclusive = (Element) inclusiveGatewayInstances.item(0); //this will be the element in case
	    //NOTE Why this works?
	    // Reason: After the gateway gets deleted from the model, it also gets deleted from inclusiveGatewayInstances
	    // For some reason. The problem is that now the element at index 1 is now at index 0.
	    // That why we use the same index every time until the list is empty.

	    System.out.println("working on the " + (i + 1) + "nd inclusiveGateway");
	    System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

	    String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

	    //creating the substitute element in the position of the old one
	    String newExclusiveGatewayId = model.newInclusiveGateway(oldInclusiveCoordinates[0], oldInclusiveCoordinates[1]);
	    Element newExclusiveGateway = model.findElemById(newExclusiveGatewayId);
	    //replacing the two elements
	    model.replaceELement(oldInclusive, newExclusiveGateway);

	    //Here I dont need to distinguish between those that are merges and those that are not.
	    //I dont need to touch the incoming/outgoing flows either
	}
    }

    /**
     * TODO
     * @param startingModel
     * @throws XPathExpressionException 
     */
    public static Model c (Model startingModel, int aggregateBy) throws XPathExpressionException{


	if (aggregateBy < 2) {
	    System.out.println();
	    System.out.println("aggregateBy must be bigger than 1");
	    return startingModel;
	}

	Model model = startingModel;

	ArrayList<Reverse3cConstruct> constructs = new ArrayList<Reverse3cConstruct>();


	NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");

	if (inclusiveGatewayInstances.getLength() == 0) {
	    System.out.println("Reverse3c: there are no inclusive gateways in this model" );
	}

	ArrayList<Element> candidates = new ArrayList<Element>();
	//for now we only care about those inclusive Gateways that are splits

	for (int i = 0; i < inclusiveGatewayInstances.getLength(); i++) {
	    Element candidate = (Element) inclusiveGatewayInstances.item(i);
	    if(model.isASplit(candidate) && model.getOutgoingFlows(candidate).size() > aggregateBy) {
		candidates.add(candidate);
	    }

	}


	//ok now we have to find out if the firstMeetingPoint of that candidate is also an inclusiveGateway.

	for (Element candidate : candidates) {
	    TravelAgency ta = new TravelAgency(model, candidate);
	    Element firstMeetingPoint = ta.mandatoryDeepSuccessors.get(0);
	    if (firstMeetingPoint.getTagName().equals("bpmn:inclusiveGateway")) {
		//ok now I can create a construct.
		System.out.println("TUTTO BENeeeeeeeeeeeeeeeeeeeeeE");
		System.out.println(ta.paths.size());//UNLOCKTHIS
		System.out.println(candidate.getAttribute("name"));
		Reverse3cConstruct construct = new Reverse3cConstruct(candidate, firstMeetingPoint);
		constructs.add(construct);
	    } else {
		System.out.println("MAMMA MIAAAAAAAAAAAAAAAA TI DEVI SPAVENTAREEEEE");
		System.out.println(ta.paths.size());//UNLOCKTHIS
		System.out.println(candidate.getAttribute("id"));
		System.out.println(candidate.getAttribute("name"));
		System.out.println(firstMeetingPoint.getAttribute("id")); //UNLOCKTHIS
	    }

	}
	
	//now let's go through all constructs and do the necessary changes.
	for (Reverse3cConstruct construct : constructs) {
	    
	    Element firstInclusive = construct.firstInclusive;
	    Element firstMeetingPoint = construct.firstInclusiveMeetingPoint;

	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(firstInclusive);
	    ArrayList<Element> successors = model.getSuccessors(firstInclusive);

	    ArrayList<Element> incomingFlows = model.getIncomingFlows(firstMeetingPoint);
	    ArrayList<Element> predecessors = model.getPredecessors(firstMeetingPoint);

	    int numberOfExclusives = outgoingFlows.size()/aggregateBy;
	    //Also, since we're dealing with ints, we should add one more prallel in the case there's a remainder
	    if (outgoingFlows.size()%aggregateBy != 0) {
		numberOfExclusives++;
	    }


	    //ok now let's create the first series of exclusiveGateways:
	    for (int n = 0; n < numberOfExclusives; n++) {
		ArrayList<Element> mySuccessors = new ArrayList<Element>();


		//we have to find the successors for the new gateway, among the ones that used to be 
		//the successors of the original gateway
		//obviously, the last parallel might not have enough successors to comply with the
		//parameter 'aggregateBy' but might have less
		for (int a = 1; a <= aggregateBy; a++) {
		    if (successors.size() == 0) {
			System.out.println("no more successors to get");
		    } else if (outgoingFlows.size() > 0) {
			mySuccessors.add(successors.get(0)); //the successor is now a successor of the new parallel
			successors.remove(0); //it's not a successor of the original parallel anymore
		    }
		}
		System.out.println("My successors SIZEEEEEE " + mySuccessors.size());
		
		String[] position = model.calculatePositionOfNewNode(mySuccessors, firstInclusive);

		String newExclusiveID = model.newExclusiveGateway(position[0],position[1]); //TODO make this method accept a 'position' object
		Element newExclusive = model.findElemById(newExclusiveID);


		newExclusive.setAttribute("name", "NUOVO");//UNLOCKTHIS

		//now that I have created my exclusive in a sensible position,
		//I can connect it to the original inclusive

		model.newSequenceFlow(firstInclusive.getAttribute("id"), newExclusiveID);

		//now I can connect the elements in mySuccessors to the new exclusive
		for (Element successor : mySuccessors) {
		   
		    Element incomingFlow = model.getIncomingFlows(successor).get(0); //we expect the successor to have only one incomingFlow of course.
		    model.setSource(incomingFlow.getAttribute("id"), newExclusiveID);
		}

		//one last thing. We want to avoid having "one in, one out" types of gateways, so to avoid this situation we want to 
		//do one last check:
		if (model.isUselessGateway(newExclusive)) {
		    model.deleteUselesGateway(newExclusive);
		}

	    }



	    //now let's create the second series of exclusiveGateways
	    for (int m = 0; m < numberOfExclusives; m++) {
		ArrayList<Element> myPredecessors = new ArrayList<Element>();


		//we have to find the predecessors for the new gateway, among the ones that used to be 
		//the predecessors of the original gateway
		//obviously, the last inclusive might not have enough predecessors to comply with the
		//parameter 'aggregateBy' but might have less
		for (int a = 1; a <= aggregateBy; a++) {
		    if (predecessors.size() == 0) {
			System.out.println("no more predecessors to get");
		    } else if (incomingFlows.size() > 0) {
			myPredecessors.add(predecessors.get(0)); //the predecessor is now a predecessor of the new parallel
			predecessors.remove(0); //it's not a predecessor of the original parallel anymore
		    }
		}
		System.out.println("My predecessors SIZEEEEEE " + myPredecessors.size());
		String[] position = model.calculatePositionOfNewNode(myPredecessors, firstMeetingPoint);

		String newExclusiveID = model.newExclusiveGateway(position[0],position[1]); //TODO make this method accept a 'position' object
		Element newExclusive = model.findElemById(newExclusiveID);


		newExclusive.setAttribute("name", "NUOVO");//UNLOCKTHIS

		//now that I have created my parallel in a sensible position,
		//I can connect it to the original meetingpoint

		model.newSequenceFlow(newExclusiveID, firstMeetingPoint.getAttribute("id"));

		//now I can connect the element in myPredecessors to the new Parallel

		for (Element predecessor : myPredecessors) {
		    Element incomingFlow = model.getOutgoingFlows(predecessor).get(0); //we expect the successor to have only one incomingFlow of course.
		    model.setTarget(incomingFlow.getAttribute("id"), newExclusiveID);
		}

		//one last thing. We want to avoid having "one in, one out" types of gateways, so to avoid this situation we want to 
		//do one last check:
		if (model.isUselessGateway(newExclusive)) {
		    model.deleteUselesGateway(newExclusive);
		}

	    }

	    //and finally let's change the type of the starting Inclusive and of the first meeting poin:
	    model.changeType(firstInclusive, "bpmn:parallelGateway");
	    model.changeType(firstMeetingPoint,"bpmn:parallelGateway");

	}




	//	Reverse3c:
	//
	//	    Quando hai un inclusiveGateway che ha il
	//	    firstMeetingPoint con un altro inclusiveGateway
	//
	//
	//	    In quel caso puoi creare un exclusiveGateway
	//	    subito dopo e il numero di exclusiveGateways
	//	    che puoi creare è dato da un parametro
	//	    Ma non può essere più alto del 
	//
	//	   numero di outGoingFlows del primo inclusive / 2
	//	   (perché ogni nuovo gateway deve avere almeno due outgoingFlows)


	return model;
    }

}
