package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.javassist.tools.framedump;
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
    public static void thirdPart (Model model) {
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





}