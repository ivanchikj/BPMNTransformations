package bpmn.transformation2;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Rule2 {


    /**
     * Rule 2 in the normal direction:
     * @param model
     * @throws XPathExpressionException 
     */
    public void applyRule(Model model) throws XPathExpressionException {
	System.out.println("I'm applying Rule1");

	//TODO make it such that it works with every gateway, not just parallel ones.
	NodeList gatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	System.out.println("number of " + " gateway instances: " + gatewayInstances.getLength()); //TODO make it work with every gateway type.

	if (gatewayInstances.getLength() == 0) {System.out.println("RULE4: there are no gateways in this model");
	//RETURN FALSE
	}

	ArrayList<Element> readyToBeChangedGateways = new ArrayList<Element>();
	//NOTE with this rule
	//I cannot do edits in real time otherwise the program would behave recursively depending on which 
	//elements it analyzes first.


	//going through all of the Gateways in the model:
	for (int i = 0; i < gatewayInstances.getLength(); i++) {

	    Element gateway = (Element) gatewayInstances.item(i); //this will be the element in case

	    System.out.println("working on the " + (i) + "nd parallelGateway");
	    System.out.println("working on " + gateway.getAttribute("id"));
	    System.out.println("The id of the element is " + gateway.getAttribute("id") );

	    if (isApplicable(model, gateway)) {
		readyToBeChangedGateways.add(gateway); //I will do the edits at the end to avoid recursive behavior
	    }
	}


	if (readyToBeChangedGateways.size() > 0 ){
	    for (int g = 0; g < readyToBeChangedGateways.size(); g++) {
		Element gateway = readyToBeChangedGateways.get(g);
		ArrayList<Element> successors = model.getSuccessors(gateway); //predecessors will be eliminated
		for (int j = 0; j < successors.size(); j++) {

		    Element succ = successors.get(j);
		    ArrayList<Element> flowsToDelete = model.getIncomingFlows(succ); //this is also the incoming flow of the gateway at hand
		    ArrayList<Element> flowsToKeep = model.getOutgoingFlows(succ);

		    //deleting all the flows (it should be only one) from the item to be deleted to its successor:
		    for (int k = 0; k < flowsToDelete.size(); k++) {
			model.delete(flowsToDelete.get(k).getAttribute("id"));
		    }
		    //changing the targets of all their sequence flows
		    for (int k = 0; k < flowsToKeep.size(); k++) {
			model.setTarget(flowsToKeep.get(k).getAttribute("id"), gateway.getAttribute("id"));
		    }
		    //finally deleting the gateways
		    model.delete(pred.getAttribute("id"));

		}
	    }
	}
    }



    private boolean isApplicable(Model model, Element gateway) {
	// TODO Auto-generated method stub
	return false;
    }




    /**
     * This checks whether a sequenceFlow has a condition or not.
     * If it has not, then there's no need to generate one and append it to the new flows.
     * @return
     */
    public boolean hasCondition(Element sequenceFlow) {
	ArrayList<Element> children = (ArrayList<Element>) sequenceFlow.getElementsByTagName("bpmn:conditionExpression");

	boolean hasCondition = false;

	if (children.size() > 0) {
	    hasCondition = true;
	}
	return hasCondition;
    }


    /**
     * 
     * AS for now, this method just adds an AND between the first gateway's flow's condition and the second's.
     * TODO maybe in the future I will find a way to better mix those conditions
     * But I think just finding the "OR" and splitting it like in the slides
     * is impossible.
     * @return the new condition
     */
    public String generateCondition(String firstCondition, String secondCondition) {
	String newCondition = firstCondition + " && " + secondCondition;
	return newCondition;

    }

    /**
     * TODO maybe put this in the method class
     * If a condition is present, it deletes it and then adds the new one inside.
     */
    public void applyCondition(Model model, Element sequenceFlow, String condition) {
	if (hasCondition(sequenceFlow)) { 
	    // getting all the conditions of a sequenceFlow
	    ArrayList<Element> children = (ArrayList<Element>) sequenceFlow.getElementsByTagName("bpmn:conditionExpression");
	    //removing all the previous conditions:
	    for (int i = 0; i < children.size(); i++) {
		sequenceFlow.removeChild(children.get(i));
	    }
	}
	Element conditionElement = model.doc.createElement("bpmn:conditionExpression");
	    conditionElement.setAttribute("xsi:type", "bpmn:tFormalExpression");
	    conditionElement.appendChild(model.doc.createTextNode(condition));
	    sequenceFlow.appendChild(conditionElement);
    }
}
