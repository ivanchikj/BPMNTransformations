package bpmn.transformation2;

import java.util.Collection;

import javax.xml.xpath.XPathExpressionException;

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
	int outflowsFromParaGateCounter = 0;

	if (parallelGatewayInstances.getLength() == 0) {System.out.println("RULE3: there are no parallel gateways in this model");}
	if (exclusiveGatewayInstances.getLength() == 0) {System.out.println("RULE3: there are no exclusive gateways in this model");}

	// going through all of the parallelGateways in the model:
	for (int i = 0; i < parallelGatewayInstances.getLength(); i++) {
	    //this will be the element in case
	    Element oldParallel = (Element) parallelGatewayInstances.item(i);

	    parallelGatewayCounter++;

	    System.out.println("working on the " + parallelGatewayCounter + "nd parallelGateway");
	    System.out.println("The id of the element is " + oldParallel.getAttribute("id") );



	    String[] oldParallelCoordinates = model.getPosition(oldParallel);

	    //TODO the following code lines has been done in a rush, it would be better to create a new method 
	    //called "subsituteElement" to use in this occasions




	    // creating the substitute element in the position of the old one
	    String newInclusiveGatewayId = model.newNode("bpmn:inclusiveGateway", oldParallelCoordinates[0], oldParallelCoordinates[1]);

	    //Now I have the new element as an ID String
	    //It's useful to find it as an Element because now I will change it (along with other properties)
	    Element newInclusiveGateway = model.findElemById(newInclusiveGatewayId);

	    //It's also useful to have the bpmndi ready to edit:

	    Element newInclusiveGatewayBPMNDI = model.findBPMNDI(newInclusiveGatewayId);
	    

	    //let's save the ID of the old element
	    String oldId = oldParallel.getAttribute("id");

	    //let's save the child elements of the oldGateway
	    //those child elements will contain the oldGateway's incoming and outgoing sequenceFlows
	    NodeList oldGatewayChildElements = oldParallel.getChildNodes();

	    //let's append the children to the new gateway
	    for (int n = 0; n < oldGatewayChildElements.getLength(); n++) {
		Node childNode = oldGatewayChildElements.item(n);
		newInclusiveGateway.appendChild(childNode);
		System.out.println("I'm appendind child called " + childNode.getTextContent());
	    }

	    //let's delete the old element
	    model.delete(oldId);


	    //let's change the ID of the new element to be equal to the id of the old one
	    newInclusiveGateway.setAttribute("id", oldId);
	    newInclusiveGatewayId = newInclusiveGateway.getAttribute("id");

	    //let's remember to change the id of the BPMNDI as well:
	    newInclusiveGatewayBPMNDI.setAttribute("bpmnElement", newInclusiveGatewayId);
	    newInclusiveGatewayBPMNDI.setAttribute("id", newInclusiveGatewayId + "_di");
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