package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.el.StaticFieldELResolver;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.javassist.tools.framedump;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CosNaming._BindingIteratorImplBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    // NOTE this has to execute before the other part of rule3 because
    // otherwise it will never be applicable (no parallel gateways will be found)
    //TODO qua dovrebbe funzionare senza fare niente, ma nella tesi ricordati di spiegare 
    //che ci sono anche le conditions
    public static Model c (Model startingModel) throws XPathExpressionException {
	System.out.println( " I'm applying Rule3c");

	Model model = startingModel;
	
	System.out.println("Going through every parallel Element in the gateway");
	
	NodeList parallelGatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	//but we only want splits
	ArrayList<Element> parallelGatewaySplitInstances = new ArrayList<Element>();
	
	for (int j = 0; j < parallelGatewayInstances.getLength(); j++) {
	    Element parallelElement = (Element) parallelGatewayInstances.item(j);
	    if (model.getOutgoingFlows(parallelElement).size() > 1) { //TOOD check this condition and replace it with the future methods isAsplit or isAmerge
		parallelGatewaySplitInstances.add(parallelElement);
	    }
	}
	
	
	
	for (Element parallelGat : parallelGatewaySplitInstances) {

	    //first we have to check that all paths from the starting parallel gateway meet at the same parallel gateway
	    //TODO this "getTheFirstMandatoryDeepSuccessor" thing could become it's own method, as it is used here later
	    //with he exclusive gateways
	    TravelAgency parallelTA = new TravelAgency(model, parallelGat);
	    Element firstParallelMeetingPoint = parallelTA.mandatoryDeepSuccessors.get(0);
	    
	    System.out.println("The first meeting point is " + firstParallelMeetingPoint.getAttribute("id"));
	    
	    //This always works because we meet at the end anyway
	    //TODO what happens when I have more than one end... it should return false but it should not break.
	    //Right now I guess it would break.


	    if (firstParallelMeetingPoint.getTagName() == "bpmn:parallelGateway") {
		System.out.println("All paths from the parallel " + parallelGat.getAttribute("id") + " meet in the same parallel");

		//we now have to check that all followers of our Parallel Gateway are exclusive Gateways
		ArrayList<Element> exclusiveGatSuccessors = exclusiveGatSuccessors(parallelGat, model);
		ArrayList<Element> successors = model.getSuccessors(parallelGat);


		if (exclusiveGatSuccessors.size() == successors.size()) {
		    System.out.println("Every follower of the parallel gateway " + parallelGat.getAttribute("id") + " is an exclusive gateway");
		    //if every follower is exclusive we can check the next condition

		    //the next condition is that no matter which path i take from my exclusive gateway,
		    //I always end up in another exclusive gateway.

		    //that is, the first element of the mandatoryDeepSuccessors of the exclusive gateway, should be another 
		    //exclusive gateway

		    for (Element exclusiveGatSuccessorOfTheParallel : exclusiveGatSuccessors) {
			//using the exclusiveGat as a starting point i want to see if the first meeting point of
			//every path that gets out of it meets in another exclusiveGateway.

			TravelAgency exclusiveTA = new TravelAgency(model, exclusiveGatSuccessorOfTheParallel);
			Element firstExclusiveMeetingPoint = exclusiveTA.mandatoryDeepSuccessors.get(0); //0 because we only care about the first


			if (firstExclusiveMeetingPoint.getTagName().equals("bpmn:exclusiveGateway")) {
			    System.out.println("All paths from the exclusiveGat " + exclusiveGatSuccessorOfTheParallel.getAttribute("id") + " meet at the same exclusiveGateway");

			    //the next condition is that every parallel has at least an "empty" outgoingFlow 
			    //that is, the pointing towards an exclusive gateway and that that exclusive gateway will be the same one the other paths point to.
			    //NOTE that if there are only empty flows all pointing to the same exclusive gateway, then that is also acceptable.
			    //TODO all the empty flows across exclusive gateways will be deleted but one.
			    //TODO scrivere nella tesi che in realtà la vera condizione non è che siano vuoti, ma che siano uguali tra di loro
			    //però l'unico modo per vedere se sono uguali sarebbe giudicarli dal nome, ma non mi sembra una buona soluzione //ASKANA

			    ArrayList<Element> emptyOutgoingFlows = outgoingFlowsPointingToExclusiveGateways(exclusiveGatSuccessorOfTheParallel, model, firstExclusiveMeetingPoint);

			    //But it's not enough to have at least an empty outgoing flow, we also want that every exclusive
			    //gateway has MAXIMUM one non-empty flow. (Where we define non-empty has having a successor that is not the 
			    //exclusive gateway (where all this paths will end up anyway)

			    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(exclusiveGatSuccessorOfTheParallel);
			    System.out.println("Number of outgoingFlows: " + outgoingFlows.size());
			    System.out.println("Number of empty outgoingFlows: " + emptyOutgoingFlows.size());
			    if (emptyOutgoingFlows.size() > 0 && outgoingFlows.size() - emptyOutgoingFlows.size() <= 1) {
				//there's at least one outgoingFlow && maximum one non-empty flow 
				System.out.println("there's at least one outgoingFlow && maximum one non-empty flow"); //add details about your starting point

				//OK now every condition has been met and we can apply the rule


				//First, let's transform our original parallel gateway and it's matching final gateways
				//into inclusive gateways.
				parallelGat.setAttribute("name", "PROVA");
				//TODO continua da qua


			    }
			}
		    }

		}
	    }
	}
	return model;
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

    private  static ArrayList<Element> outgoingFlowsPointingToExclusiveGateways (Element element, Model model, Element target) throws XPathExpressionException {
	ArrayList<Element> outgoingFlowsPointingtoExclGats = new ArrayList<Element>();

	ArrayList<Element> outGoingFlows = model.getOutgoingFlows(element);

	for(Element flow : outGoingFlows) {
	    if (model.getTarget(flow).getAttribute("id").equals(target.getAttribute("id"))){
		outgoingFlowsPointingtoExclGats.add(flow);
	    }
	}

	return outgoingFlowsPointingtoExclGats;
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








}