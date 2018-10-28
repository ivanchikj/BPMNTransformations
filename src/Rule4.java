

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;

public class Rule4 {


    public static void a (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE4a to model " + model.name);

        NodeList parallelGatewayInstances = model.doc.getElementsByTagName(
        "bpmn:parallelGateway");
        System.out.println("Number of parallelGateways in the model: " + parallelGatewayInstances.getLength());
        int parallelGatewayCounter = 0;

        for (int i = 0 ; i < parallelGatewayInstances.getLength() ; i++) {
            Element oldParallelGateway =
             (Element) parallelGatewayInstances.item(i);
            parallelGatewayCounter++;

            System.out.println("working on the " + parallelGatewayCounter +
            "nd parallelGateway");
            System.out.println("working on " + oldParallelGateway.getAttribute("id"));
            System.out.println("The id of the element is " + oldParallelGateway.getAttribute("id"));

            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(oldParallelGateway);
            ArrayList<Element> incomingFlows =
             model.getIncomingFlows(oldParallelGateway);

            if ((incomingFlows.size() == 1)) { //TODO check if this
                // conditions is actually a solid way to distinguish merges
                System.out.println(oldParallelGateway.getAttribute("id") + " "
                 + "has only one incoming flow and the rule 4a can be applied");

                //let's find out the predecessor
                ArrayList<Element> predecessors =
                 model.getPredecessors(oldParallelGateway);
                Element predecessor = predecessors.get(0); //we know there's
                // gonna be only one

                //let's connect the successors to it's predecessor
                //TODO think what happens if one of the arrows has attributes
                // . I think it works but let's write this in the thesis.
                for (Element outgoingFlow : outgoingFlows) {
                    model.setSource(outgoingFlow.getAttribute("id"),
                     predecessor.getAttribute("id"));
                }
                //let's remember to delete the useless sequenceFlow
                model.delete(incomingFlows.get(0).getAttribute("id"));

                model.delete(oldParallelGateway.getAttribute("id"));
                //TODO
                // make the method "delete" take an Element as an input
            } else {
                System.out.println(oldParallelGateway.getAttribute("id") + " "
                 + "has more than one incoming flow thus rule4a cannot be " + "applied!");
            }
        }
    }


    static void b (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE4b to model " + model.name);
        //We use the same way that we used in rule3a to distinguish
        //merges. The difference is that now we WANT merges.

        NodeList exclusiveGatewayInstances = model.doc.getElementsByTagName(
        "bpmn:exclusiveGateway");
        System.out.println("Number of exclusiveGateways in the model: " + exclusiveGatewayInstances.getLength());
        int exclusiveGatewayCounter = 0;
        if (exclusiveGatewayInstances.getLength() == 0) {
            System.out.println("RULE4b: there are no exclusive gateways in " + "this model");
        }

        for (int i = 0 ; i < exclusiveGatewayInstances.getLength() ; i++) {
            //this will be the element in case
            Element oldExclusiveGateway =
             (Element) exclusiveGatewayInstances.item(i);

            exclusiveGatewayCounter++;

            System.out.println("working on the " + exclusiveGatewayCounter +
            "nd exclusiveGateway");
            System.out.println("working on " + oldExclusiveGateway.getAttribute("id"));
            System.out.println("The id of the element is " + oldExclusiveGateway.getAttribute("id"));

            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(oldExclusiveGateway);
            ArrayList<Element> incomingFlows =
             model.getIncomingFlows(oldExclusiveGateway);

            //merges are gateways that have more than one incoming flow
            //but only one outgoingFlow
            if ((incomingFlows.size() > 1 && outgoingFlows.size() == 1)) {
                //TODO check if this conditions is actually a solid way to
                // distinguish merges
                System.out.println(oldExclusiveGateway.getAttribute("id") +
                " Is a merge exclusive gateway and will be deleted!");

                //let;s find out the successor:
                ArrayList<Element> successors =
                 model.getSuccessors(oldExclusiveGateway);
                Element successor = successors.get(0);//we know it's gonna be
                // only one successor, otherwise it would not be a merge
                //let's delete the outGoingFlow
                model.delete(outgoingFlows.get(0).getAttribute("id")); //we
                // know it's gonna be only one outgoing flow, otherwise it
                // would not be a merge

                //let's connect it's predecessor to one of its followers
                for (Element incomingFlow : incomingFlows) { //TODO
                    // think what happens if one of the arrows has attributes
                    // . I
                    // think it works but let's write this in the thesis.
                    model.setTarget(incomingFlow.getAttribute("id"),
                     successor.getAttribute("id"));
                }

                //let's delete the merge exclusive gateway
                //NOTE if i delete it before changing the target of flows
                // that were previousy attached to it,
                //the program will have a problem because it will try to
                // delete child elements from
                //an element that doesnt exist anymore
                //TODO see if you want to change this behavior.
                //Maybe you want to create another version of SetSource /
                // SetTarget that does not delete child elements?
                //it would be useful when deleting elements like in this
                // instance, instead of simply changing the direction of a flow
                model.delete(oldExclusiveGateway.getAttribute("id"));
            } else {
                System.out.println(oldExclusiveGateway.getAttribute("id") +
                " Is a merge exclusive gateway!");
            }
        }
    }


    static void c (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE4c to model " + model.name);

        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName(
        "bpmn:inclusiveGateway");
        System.out.println("Number of inclusiveGateways in the model: " + inclusiveGatewayInstances.getLength());
        int inclusiveGatewayCounter = 0;

        for (int i = 0 ; i < inclusiveGatewayInstances.getLength() ; i++) {
            Element oldInclusiveGateway =
             (Element) inclusiveGatewayInstances.item(i);
            inclusiveGatewayCounter++;

            System.out.println("working on the " + inclusiveGatewayCounter +
            "nd inclusiveGateway");
            System.out.println("working on " + oldInclusiveGateway.getAttribute("id"));
            System.out.println("The id of the element is " + oldInclusiveGateway.getAttribute("id"));

            ArrayList<Element> outgoingFlows =
             model.getOutgoingFlows(oldInclusiveGateway);
            ArrayList<Element> incomingFlows =
             model.getIncomingFlows(oldInclusiveGateway);

            if ((incomingFlows.size() == 1)) { //TODO check if this
                // conditions is actually a solid way to distinguish merges
                System.out.println(oldInclusiveGateway.getAttribute("id") +
                " has only one incoming flow and the rule 4a can be applied");

                //let's find out the predecessor
                ArrayList<Element> predecessors =
                 model.getPredecessors(oldInclusiveGateway);
                Element predecessor = predecessors.get(0); //we know there's
                // gonna be only one

                //let's connect the successors to it's predecessor
                //TODO think what happens if one of the arrows has attributes
                // . I think it works but let's write this in the thesis.
                for (Element outgoingFlow : outgoingFlows) {
                    model.setSource(outgoingFlow.getAttribute("id"),
                     predecessor.getAttribute("id"));
                    Element condition = model.doc.createElement("bpmn" +
                    ":conditionExpression");
                    condition.setAttribute("xsi:type", "bpmn" +
                    ":tFormalExpression");
                    condition.setAttribute("language", "");
                    outgoingFlow.appendChild(condition);
                }
                //let's remember to delete the useless sequenceFlow
                model.delete(incomingFlows.get(0).getAttribute("id"));
                model.delete(oldInclusiveGateway.getAttribute("id"));//TODO
                // make the method "delete" take an Element as an input
            } else {
                System.out.println(oldInclusiveGateway.getAttribute("id") +
                " has more than one incoming flow thus rule4a cannot be " +
                "applied!");
            }
        }
    }


    /**
     * TODO controlla l'ordine
     * TODO spiega come hai scelto l'ordine
     *
     * @param model the model that will be transformed.
     */
    public static void all (Model model) throws XPathExpressionException {

        System.out.println("I'm applying rule RULE4 to model " + model.name);
        a(model);
        b(model);
        c(model);
    }


}

