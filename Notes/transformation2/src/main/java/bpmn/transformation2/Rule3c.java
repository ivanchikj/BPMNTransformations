package bpmn.transformation2;

import java.util.ArrayList;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Rule3c {


    /*
     * Scrivere modo di identificare le coppie parallel seguito da inclus
     * 
     * Scrivere modo d identificare le coppie inclusive seguito da parallel
     * 
     * Scrivere modo di navigare tutto il graph a partire da un element arbitrario usando i metodi getoutgoing e di
     * storare tutti gli id in un array. Un'idea potrebbe essere di fare un ashmap, e per ogni elemento indicare di quale branch fa parte.
     * Quindi un elemento potrebbe fare parte della branch 1 poi il seguente anche 1
     * Poi c'è un gateway e quindi abbiamo la branch 11 e la branch 12 e così via.
     * 
     * 
     * 
     */



    //First of all I have to find instances of inclusive gateways that are followed ONLY by exclusive gateways
    //The for all those inclusive gateways I have to find if they have one empty path that goes into another exclusive
    //gateway that is a merge. 
    //Then I have to find if the outgoing


    public static void Rule3c(Model model) throws XPathExpressionException{

	NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway"); //TODO maybe to method wrapper that returns Elements instead of nodes?

	if (inclusiveGatewayInstances.getLength() == 0 ){
	    System.out.println("there are not inclusive gateways in this model");
	    //TODO is there a way to get out here immediately?
	    //Probably only when i insert the return boolean functionality
	}

	for (int i = 0; i < inclusiveGatewayInstances.getLength(); i++) {	    
	    //Let's check that the successors of our inclusiveGat are all exclusiveGats
	    Element inclusiveGateway = (Element) inclusiveGatewayInstances.item(i);
	    
	    boolean intruders = wrongSuccessors(model, inclusiveGateway);
	    
	    if (!intruders) {
		//GO ON FROM THERE
	    }
	}

    }
  

    /**
     * Let's check that the successors of our inclusiveGat are all exclusiveGats
     * This is useful to build the list of potential gateways at the beginning of the construct for rule 3
     * @param model
     * @param inclusiveGateway
     * @return
     * @throws XPathExpressionException
     */
    public static boolean wrongSuccessors(Model model, Element inclusiveGateway) throws XPathExpressionException {

	ArrayList<Element> successors = model.getSuccessors(inclusiveGateway);
	
	//Let's assume that for now there are no "intruders" (items that are not exclusiveGats)
	boolean intruders = false;
	for (int j = 0; j < successors.size(); j++) {
	    Element elementToCheck = successors.get(j);
	    if (!elementToCheck.getTagName().equals("bpmn:inclusiveGateway")) {
		intruders = true; //we have found an item that is not an exclusiveGat
	    }
	}
	return intruders;
    }
    
    
    /**
     * Let's check that the predecessors of our inclusiveGat are all exclusiveGats
     * This is useful to build the list of potential gateways at the end of the construct for rule 3
     * @param model
     * @param inclusiveGateway
     * @return
     * @throws XPathExpressionException
     */
    public static boolean wrongPredecessors(Model model, Element inclusiveGateway) throws XPathExpressionException {
	ArrayList<Element> predecessors = model.getPredecessors(inclusiveGateway);
	
	boolean intruders = false;
	
	for (int i = 0; i < predecessors.size(); i++) {
	    Element elementToCheckElement = predecessors.get(i);
		    if (!elementToCheckElement.getTagName().equals("bpmn:inclusiveGateway")) {
			intruders = true;
		    }
	}
	return intruders;
    }

}
