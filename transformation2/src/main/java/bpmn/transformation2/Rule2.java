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
    public static Model applyRule(Model startingModel) throws XPathExpressionException {
	System.out.println("I'm applying Rule1");
	Model model = startingModel;
	//TODO make it such that it works with every gateway, not just exclusive ones.
	NodeList gatewayInstances = model.doc.getElementsByTagName("bpmn:exclusiveGateway");
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
		ArrayList<Element> successors = model.getSuccessors(gateway); //successors will be eliminated
		for (int j = 0; j < successors.size(); j++) {


		    //Let's add the conditions of the incomingFlows to the OutgoingFlows




		    Element succ = successors.get(j);
		    ArrayList<Element> flowsToDelete = model.getIncomingFlows(succ); //this is also the outGoing flow of the gateway at hand. It should be only one.
		    ArrayList<Element> flowsToKeep = model.getOutgoingFlows(succ);  
		    String firstPartofCondition = "";
		    String secondPartofCondition = "";
		    //deleting all the flows (it should be only one) from the item to be deleted to its successor:
		    for (int k = 0; k < flowsToDelete.size(); k++) {
			model.delete(flowsToDelete.get(k).getAttribute("id"));
			firstPartofCondition = returnConditionString(flowsToDelete.get(k));//TODO This works only because I have only one outGoing flow. Otherwise it should be outside of the for loop.
		    }
		    //changing the targets of all their sequence flows
		    for (int k = 0; k < flowsToKeep.size(); k++) {
			Element flowToKeep = flowsToKeep.get(k);
			model.setSource(flowToKeep.getAttribute("id"), gateway.getAttribute("id"));

			String newCondition = firstPartofCondition; // the inizialization value should not matter.

			secondPartofCondition = returnConditionString(flowToKeep);
			// if both flows have a condition, I merge them with an AND in the middle.
			// if else, I only keep the non - empty one.
			if (firstPartofCondition != "" && secondPartofCondition != "") { 
			    //changing the condition of that outgoing flow:
			    newCondition = generateCondition(firstPartofCondition, secondPartofCondition);

			} else if (firstPartofCondition != "" && secondPartofCondition == "") {
			    newCondition = firstPartofCondition;
			} else if (firstPartofCondition == "" && secondPartofCondition != "") {
			    newCondition = secondPartofCondition;
			}

			//Adding the newCondition
			System.out.println(newCondition);
			returnConditionElement(flowToKeep).setTextContent(newCondition);
		    }
		    //finally deleting the gateways
		    model.delete(succ.getAttribute("id"));

		}
	    }
	}
	return model;
    }



    private static boolean isApplicable(Model model, Element gateway) throws XPathExpressionException {

	System.out.println("I'm checking if gateway " +  gateway.getAttribute("id") + " is suitable for rule 2:");

	ArrayList<Element> successors = model.getSuccessors(gateway);
	String type =  gateway.getTagName();

	boolean sameType = true;
	boolean innerMost = false;
	boolean noMergesInPreds = true; //all successors (that will be deleted) have to be splits

	for (int j = 0; j < successors.size(); j++) {

	    //checking that all successors are of the same type:
	    Element successor = successors.get(j);
	    System.out.println("I'm analyzing predecessor " + successor.getAttribute("id"));
	    if (successors.get(j).getTagName() != type ){
		System.out.println("The gateway " + gateway.getAttribute("id") + " is succeded by something that is not a gateway of the same type.");
		System.out.println("The rule2 cannot be applied on that gateway");
		sameType = false;
	    } else {
		System.out.println("I haven't found a single successor of " + gateway.getAttribute("id") +  " that is not a " + type);
		//sameType remains true
	    }

	    //Checking that we are on the innermost level. I.e. that every successor is not succeded by a gateway of the same type himself 
	    ArrayList<Element> succsOfSucc = model.getSuccessors(successors.get(j)); //the predecessors of the predecessor

	    for (int i = 0; i < succsOfSucc.size(); i++) {
		Element succOfSucc = succsOfSucc.get(i);
		System.out.println("Im analyzing successor " + succOfSucc.getAttribute("id") + " of the successor " + successor.getAttribute("id"));

		if (succsOfSucc.get(i).getTagName() != type) {
		    System.out.println("I have found a successor that is not a gateway of the same type!");
		    innerMost = true;
		}


		//Checking that all predecessors are splits
		if (model.isAMerge(successor)) {
		    //TODO this includes 1in 1out gateways. Decide what to do with merges. I would do it more resistant.
		    System.out.println("I have found a predecessor that is not a split but rather a merge!");  
		    noMergesInPreds = false;
		}
	    }
	}
	//Finally if all conditions are true, return true.
	if ( sameType && innerMost && noMergesInPreds) {
	    System.out.println
	    ("All predecessors are of same type AND they are all splits AND we are on the innermost level");
	    return true;
	} else { 
	    return false;
	}
    }




    /**
     * This checks whether a sequenceFlow has a condition or not.
     * If it has not, then there's no need to generate one and append it to the new flows.
     * TODO delete this method and use returnCondition instead
     * @return
     */
    public boolean hasCondition(Element sequenceFlow) {
	NodeList children =  sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO

	boolean hasCondition = false;

	if (children.getLength() > 0) {
	    hasCondition = true; 
	}
	return hasCondition;
    }

    public static String returnConditionString (Element sequenceFlow) {
	Element conditionElement = returnConditionElement(sequenceFlow);
	String condition = conditionElement.getTextContent();
	return condition;
    }

    public static Element returnConditionElement (Element sequenceFlow) {
	NodeList children = sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO
	if (children.getLength() > 1) {
	    System.err.println("How can an array have more than one condition children?");
	}
	return (Element) children.item(0);
    }

    /**
     * 
     * AS for now, this method just adds an AND between the first gateway's flow's condition and the second's.
     * @return the new condition
     */
    public static String generateCondition(String firstCondition, String secondCondition) {
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
	    ArrayList<Element> children = 
		    (ArrayList<Element>) sequenceFlow.getElementsByTagName("bpmn:conditionExpression"); //TODO
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
