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

	//TODO scrivi nella tesi 
	//Ogni volta che vedi una task che ha due incoming, mettici un exclusive gateway merge davanti

	System.out.println("I'm reverting rule 4b");

	Model model = startingModel;

	//TODO spiegare nella tesi perché non si può applicare alle altre cose che non sono task
	NodeList tasks = model.doc.getElementsByTagName("bpmn:task");

	for (int i = 0; i < tasks.getLength(); i++) {
	    Element task = (Element) tasks.item(i);
	    ArrayList<Element> incomingFlows = model.getIncomingFlows(task);

	    //if it has more than one incoming Flow then we can add a parallel in there
	    if (incomingFlows.size()>1) {

		//Before creating the new exclusive gateway, we have to calculate its future position

		ArrayList<Element> predecessors = model.getPredecessors(task);

		String[] position = model.calculatePositionOfNewNode(predecessors, task);

		String newExclusiveID = model.newExclusiveGateway(position[0], position[1]);

		//finally here's the newly created exclusive gateway
		Element newExclus = model.findElemById(newExclusiveID);

		//let's change the two incoming flows of our task to have the exclusive element as their Target
		for (Element flow : incomingFlows) {
		    model.setTarget(flow.getAttribute("id"), newExclusiveID);
		}

		//let's now create a new flow from the task to the new exclusive
		String newFlowID = model.newSequenceFlow(newExclusiveID, task.getAttribute("id"));

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
