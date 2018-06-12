package bpmn.transformation2;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.omg.PortableInterceptor.SUCCESSFUL;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NEWRule4 {

    public static void b(Model model) throws XPathExpressionException {
	System.out.println("I'm applying Rule4b");
	//We use the same way that we used in rule3a to distinguish
	//merges. The difference is that now we WANT merges.

	NodeList exclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:exclusiveGateway");
	System.out.println("Number of exclusiveGateways in the model: " + exclusiveGatewayInstances.getLength());
	int exclusiveGatewayCounter = 0;
	if (exclusiveGatewayInstances.getLength() == 0) {System.out.println("RULE4b: there are no exclusive gateways in this model");}

	for (int i = 0; i < exclusiveGatewayInstances.getLength(); i++) {
	    //this will be the element in case
	    Element oldExclusiveGateway = (Element) exclusiveGatewayInstances.item(i);// TODO inquire the reason why this does not work in rule3a

	    exclusiveGatewayCounter++;

	    System.out.println("working on the " + exclusiveGatewayCounter + "nd parallelGateway");
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
	    for (int f = 0; f < incomingFlows.size(); f++) {
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
    }
}