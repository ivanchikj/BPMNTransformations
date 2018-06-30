package bpmn.transformation2;

import java.util.ArrayList;

import javax.ws.rs.NotAcceptableException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.ibatis.ognl.OgnlContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NEWRule1 {


    //TODO applyRule will actually become just a (simple) method calling other specific methods
    //TODO this rule (as every other one) will return false or true depending if it applied the rule or not.
    //putting returns is also a way to interrupt the execution of the rule mid-method by returning false
    @SuppressWarnings("null") //TODO do I have to delete this?
    public static void applyRule(Model model) throws Exception {
	System.out.println("I'm applying Rule1");

	// Here I'm creating a list of all the parallel gateways in the inputModel
	// TODO this could be a method that returns a list of elements
	// TODO make it such that it returns all gateways, not just parallel ones.
	NodeList parallelGatewayInstances = model.doc.getElementsByTagName("bpmn:parallelGateway");
	System.out.println("number of parallel gateway instances: " + parallelGatewayInstances.getLength());

	if (parallelGatewayInstances.getLength() == 0) {System.out.println("RULE4: there are no parallel gateways in this model");
	//RETURN FALSE
	}

	ArrayList<Element> readyToBeChangedGateways = new ArrayList<Element>();
	//NOTE with this rule
	//I cannot do edits in real time otherwise the program would behave recursively depending on which 
	//elements it analyzes first.

	//going through all of the parallelGateways in the model:
	for (int i = 0; i < parallelGatewayInstances.getLength(); i++) {

	    Element gateway = (Element) parallelGatewayInstances.item(i); //this will be the element in case



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
		ArrayList<Element> predecessors = model.getPredecessors(gateway); //predecessors will be eliminated
		for (int j = 0; j < predecessors.size(); j++) {

		    Element pred = predecessors.get(j);
		    ArrayList<Element> flowsToDelete = model.getOutgoingFlows(pred); //this is also the incoming flow of the gateway at hand
		    ArrayList<Element> flowsToKeep = model.getIncomingFlows(pred);

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



    /**
     * This method checks if the rule1 is applicable on a given parallel gateway.
     * That is, if the gateway is preceded only by other gateways of the same type, AND those gateways are not preceded
     * by a gateway of the same type themselves AND those gateways are 
     * then the rule is applicable.
     * @param model
     * @param parallelGateway
     * @return
     * @throws XPathExpressionException 
     */
    public static boolean isApplicable (Model model, Element parallelGateway) throws XPathExpressionException {
	System.out.println("I'm checking if gateway " +  parallelGateway.getAttribute("id") + " is suitable for rule 3:");

	ArrayList<Element> predecessors = model.getPredecessors(parallelGateway);
	String type =  parallelGateway.getTagName();

	boolean sameType = true;
	boolean innerMost = false;
	boolean noSplitsInPreds = true; //all predecessors (that will be deleted) have to be merges

	for (int j = 0; j < predecessors.size(); j++) {

	    //checking that all predecessors are of the same type:
	    Element predecessor = predecessors.get(j);
	    System.out.println("I'm analyzing predecessor " + predecessor.getAttribute("id"));
	    if (predecessors.get(j).getTagName() != type ){
		System.out.println("The gateway " + parallelGateway.getAttribute("id") + " is preceeded by something that is not a gateway of the same type.");
		System.out.println("The rule1 cannot be applied on that gateway");
		sameType = false;
	    } else {
		System.out.println("I haven't found a single predecessor of " + parallelGateway.getAttribute("id") +  " that is not a parallel gateway");
		//sameType remains true
	    }


	    //Checking that we are on the innermost level. I.e. that every predecessor is not preceded by a gateway of the same type himself 
	    ArrayList<Element> predsOfPred = model.getPredecessors(predecessors.get(j)); //the predecessors of the predecessor

	    for (int i = 0; i < predsOfPred.size(); i++) {
		Element predOfPred = predsOfPred.get(i);
		System.out.println("Im analyzing predecessor " + predOfPred.getAttribute("id") + " of the predecessor " + predecessor.getAttribute("id"));
		
		if (predsOfPred.get(i).getTagName() != type) {
		    System.out.println("I have found a predecessor that is not a gateway of the same type!");
		    innerMost = true;
		}
		
		//Checking that all predecessors are splits
		if (!model.isAMerge(predecessor)) {
		    System.out.println("I have found a predecessor that is not a merge but rather a split!"); 
		    noSplitsInPreds = false;
		}
	    }
	}

	//Finally if both conditions are true, return true.
	if ( sameType && innerMost && noSplitsInPreds) {
	    System.out.println("All predecessors are of same type AND they are all merges AND we are on the innermost level");
	    return true;
	} else { 
	    return false;
	}

    }
}