package bpmn.transformation2;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.mail.handlers.image_gif;

import javax.print.Doc;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class Reverse4 {


    //TODO scrivi nella tesi ogni volta che vedi una task che ha due outgoingFlows, crea un parallel davanti e usa quello come split"

    public static Model a(Model startingModel) throws XPathExpressionException {
	System.out.println("I'm reverting rule 4a");

	Model model = startingModel;

	//TODO spiegare nella tesi perché non si può applicare alle altre cose.
	NodeList tasks = model.doc.getElementsByTagName("bpmn:task");

	for (int i = 0; i < tasks.getLength(); i++) {
	    Element task = (Element) tasks.item(i);
	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(task);

	    //if it has more than one outgoing Flow then we can add a parallel in there
	    if (outgoingFlows.size()>1) {
		
		//Before creating the new parallel gateway, we have to calculate its future position
		
		ArrayList<Element> successors = model.getSuccessors(task);
		
		String[] position = model.calculatePositionOfNewNode(task, successors);
		
		String newParallelID = model.newParallelGateway(position[0], position[1]);
		
		//finally here's the newly created parallel gateway
		Element newParall = model.findElemById(newParallelID);
		
		//let's change the two outgoing flows of our task to have the parallel element as their source
		for (Element flow : outgoingFlows) {
		    model.setSource(flow.getAttribute("id"), newParallelID);
		}
		
		//let's now create a new flow from the task to the new parallel
		String newFlowID = model.newSequenceFlow(task.getAttribute("id"), newParallelID);
		
	    }

	}


	return model;
    }



    public static Model b(Model startingModel) throws XPathExpressionException {
	System.out.println("I'm applying Rule4b");
	//We use the same way that we used in rule3a to distinguish
	//merges. The difference is that now we WANT merges.

	Model model = startingModel;

	NodeList exclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:exclusiveGateway");
	System.out.println("Number of exclusiveGateways in the model: " + exclusiveGatewayInstances.getLength());
	int exclusiveGatewayCounter = 0;
	if (exclusiveGatewayInstances.getLength() == 0) {System.out.println("RULE4b: there are no exclusive gateways in this model");}

	for (int i = 0; i < exclusiveGatewayInstances.getLength(); i++) {
	    //this will be the element in case
	    Element oldExclusiveGateway = (Element) exclusiveGatewayInstances.item(i);

	    exclusiveGatewayCounter++;

	    System.out.println("working on the " + exclusiveGatewayCounter + "nd exclusiveGateway");
	    System.out.println("working on " + oldExclusiveGateway.getAttribute("id"));
	    System.out.println("The id of the element is " + oldExclusiveGateway.getAttribute("id") );

	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(oldExclusiveGateway);
	    ArrayList<Element> incomingFlows = model.getIncomingFlows(oldExclusiveGateway);

	    //merges are gateways that have more than one incoming flow
	    //but only one outgoingFlow
	    if ((incomingFlows.size() > 1 && outgoingFlows.size() == 1 )) { //TODO check if this conditions is actually a solid way to distinguish merges
		System.out.println(oldExclusiveGateway.getAttribute("id")+ " Is a merge exclusive gateway and will be deleted!");


		//let;s find out the successor:
		ArrayList<Element> successors = model.getSuccessors(oldExclusiveGateway);
		Element successor = successors.get(0);//we know it's gonna be only one successor, otherwise it would not be a merge
		//let's delete the outGoingFlow
		model.delete(outgoingFlows.get(0).getAttribute("id")); //we know it's gonna be only one outgoing flow, otherwise it would not be a merge

		//let's connect it's predecessor to one of its followers
		for (int f = 0; f < incomingFlows.size(); f++) { //TODO think what happens if one of the arrows has attributes. I think it works but let's write this in the thesis.
		    model.setTarget(incomingFlows.get(f).getAttribute("id"), successor.getAttribute("id"));
		}

		//let's delete the merge exclusive gateway
		//NOTE if i delete it before changing the target of flows that were previousy attached to it,
		//the program will have a problem because it will try to delete child elements from 
		//an element that doesnt exist anymore
		//TODO see if you want to change this behavior.
		//Maybe you want to create another version of SetSource / SetTarget that does not delete child elements?
		//it would be useful when deleting elements like in this instance, instead of simply changing the direction of a flow
		model.delete(oldExclusiveGateway.getAttribute("id"));

	    } else {
		System.out.println(oldExclusiveGateway.getAttribute("id")+ " Is a merge exclusive gateway!");
	    }
	}

	return model;
    }


    public static Model c(Model startingModel) throws XPathExpressionException {
	System.out.println("I'm applying rule 4c");

	Model model = startingModel;

	NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");
	System.out.println("Number of inclusiveGateways in the model: " + inclusiveGatewayInstances.getLength());
	int inclusiveGatewayCounter = 0;

	for (int i = 0; i < inclusiveGatewayInstances.getLength(); i++) {
	    Element oldInclusiveGateway = (Element) inclusiveGatewayInstances.item(i);
	    inclusiveGatewayCounter++;

	    System.out.println("working on the " + inclusiveGatewayCounter + "nd inclusiveGateway");
	    System.out.println("working on " + oldInclusiveGateway.getAttribute("id"));
	    System.out.println("The id of the element is " + oldInclusiveGateway.getAttribute("id") );

	    ArrayList<Element> outgoingFlows = model.getOutgoingFlows(oldInclusiveGateway);
	    ArrayList<Element> incomingFlows = model.getIncomingFlows(oldInclusiveGateway);

	    if ((incomingFlows.size() == 1)) { //TODO check if this conditions is actually a solid way to distinguish merges
		System.out.println(oldInclusiveGateway.getAttribute("id")+ " has only one incoming flow and the rule 4a can be applied");

		//let's find out the predecessor
		ArrayList<Element> predecessors = model.getPredecessors(oldInclusiveGateway);
		Element predecessor = predecessors.get(0); //we know there's gonna be only one

		//let's connect the successors to it's predecessor
		//TODO think what happens if one of the arrows has attributes. I think it works but let's write this in the thesis.
		for (int f = 0; f < outgoingFlows.size(); f++) {
		    Element outgoingFlow = outgoingFlows.get(f);
		    model.setSource(outgoingFlow.getAttribute("id"), predecessor.getAttribute("id"));
		    Element condition = model.doc.createElement("bpmn:conditionExpression");
		    condition.setAttribute("xsi:type", "bpmn:tFormalExpression");
		    condition.setAttribute("language", "");
		    outgoingFlow.appendChild(condition);
		}
		//let's remember to delete the useless sequenceFlow
		model.delete(incomingFlows.get(0).getAttribute("id"));
		model.delete(oldInclusiveGateway.getAttribute("id"));//TODO make the method "delete" take an Element as an input

	    } else {
		System.out.println(oldInclusiveGateway.getAttribute("id") + " has more than one incoming flow thus rule4a cannot be applied!");
	    }
	}
	return model;
    }
}
