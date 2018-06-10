package bpmn.transformation2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.javassist.tools.framedump;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NEWRule3 {



    //TODO applyRule will actually become just a (simple) method calling other specific methods referring to part 1, part 2 of rule 3 etc
    //this method will be renamed and be called by said method. 

    public static void firstPart(Model model) throws Exception {

	// TODO decide on this:
	// Here I'm creating a list of all the parallel gateways in the inputModel
	NodeList parallelGatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	NodeList exclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:exclusivegateway");

	// The following counters serves us to understand if I'm reading the inputModel
	// correctly.
	int parallelGatewayCounter = 0;
	int exclusiveGatewayCounter = 0;
	int outflowsFromParaGateCounter = 0;

	if (parallelGatewayInstances.getLength() == 0) {System.out.println("RULE3: there are no parallel gateways in this model");}
	if (exclusiveGatewayInstances.getLength() == 0) {System.out.println("RULE3: there are no exclusive gateways in this model");}

	//TODO the first part of the firstPart of rule 3 can be generalized for bot parallel and exclusive gateways

	// going through all of the parallelGateways in the model:
	for (int i = 0; i < parallelGatewayInstances.getLength(); i++) {
	    //this will be the element in case
	    Element oldParallel = (Element) parallelGatewayInstances.item(i);

	    parallelGatewayCounter++;

	    System.out.println("working on the " + parallelGatewayCounter + "nd parallelGateway");
	    System.out.println("The id of the element is " + oldParallel.getAttribute("id") );

	    String[] oldParallelCoordinates = model.getPosition(oldParallel);

	    // creating the substitute element in the position of the old one
	    String newInclusiveGatewayId = model.newNode("bpmn:inclusiveGateway", oldParallelCoordinates[0], oldParallelCoordinates[1]);
	    Element newInclusiveGateway = model.findElemById(newInclusiveGatewayId);
	    model.replaceELement(oldParallel, newInclusiveGateway);

	    //TODO this has to become two separate methods
	    //one that changes the condition and takes a string as an input
	    //inside model.java
	    //And one that creates a bpmn:conditionExpression element, inside newNode
	    NodeList outgoingFlows = model.getOutgoingFlows(newInclusiveGateway);
	    for (int f = 0; f < outgoingFlows.getLength(); f ++) {
		Element element = (Element) outgoingFlows.item(f);
		element.setAttribute("name", "1==1"); // TODO this is just for the demo
		Element condition = model.doc.createElement("bpmn:conditionExpression");
		condition.setAttribute("xsi:type", "bpmn:tFormalExpression");
		condition.appendChild(model.doc.createTextNode("1==1"));
		element.appendChild(condition);
	    }



	}

	//going through all of the exclusiveGateways in the model:
	for (int i = 0; i < exclusiveGatewayInstances.getLength(); i++) {
	    //this will be the element in case
	    Element oldExclusive = (Element) exclusiveGatewayInstances.item(i);

	    exclusiveGatewayCounter++;

	    System.out.println("working on the " + exclusiveGatewayCounter + "nd exclusiveGateway");
	    System.out.println("The id of the element is " + oldExclusive.getAttribute("id") );

	    String[] oldExclusiveCoordinates = model.getPosition(oldExclusive);

	    // creating the substitute element in the position of the old one
	    String newInclusiveGatewayId = model.newNode("bpmn:inclusiveGateway", oldExclusiveCoordinates[0], oldExclusiveCoordinates[1]);
	    Element newInclusiveGateway = model.findElemById(newInclusiveGatewayId);
	    model.replaceELement(oldExclusive, newInclusiveGateway);
	}

    }

    // TODO integrate this into the first part of rule3
    // NOTE that this has to execute before the other part of rule3 because
    // otherwise it will never be applicable (no parallel gateways will be found)
    // TODO create some bpmn graphs to test this
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