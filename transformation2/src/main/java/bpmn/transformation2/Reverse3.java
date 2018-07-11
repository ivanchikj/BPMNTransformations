package bpmn.transformation2;

import java.util.ArrayList;
import java.util.function.ToDoubleBiFunction;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

public class Reverse3 {

    /**
     *
     * @param startingModel
     * @throws Exception
     */
    public static void a (Model startingModel) throws Exception {
        Model model = startingModel;
        System.out.println("I'm reverting Rule3a");

        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");


        if (inclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse3a: there are no inclusive gateways in this model");
        }

        // going through all of the inclusiveGateways in the model:
        for (int i = 0; i <= inclusiveGatewayInstances.getLength(); i++) {


            Element oldInclusive = (Element) inclusiveGatewayInstances.item(0); //this will be the element in case
            //NOTE Why this works?
            // Reason: After the gateway gets deleted from the model, it also gets deleted from parallelGatewayInstances
            // For some reason. The problem is that now the element at index 1 is now at index 0.
            // That's why we use the same index every time until the list is empty.


            System.out.println("working on the " + (i + 1) + "nd inclusiveGateway");
            System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

            String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

            // creating the substitute element in the position of the old one
            String newParallelGatewayId = model.newParallelGateway(oldInclusiveCoordinates[0], oldInclusiveCoordinates[1]);
            Element newParallelGateway = model.findElemById(newParallelGatewayId);
            model.replaceELement(oldInclusive, newParallelGateway);

            ArrayList<Element> outgoingFlows = model.getOutgoingFlows(newParallelGateway);
            ArrayList<Element> incomingFlows = model.getIncomingFlows(newParallelGateway);

            System.out.println("test " + outgoingFlows.size()); //UNLOCKTHIS for testing
            System.out.println("test " + incomingFlows.size()); //UNLOCKTHIS for testing

            //TODO ovviamente devo distinguere i gateway che sono anche merge dagli altri
            //Un modo facile per distinguerli sarebbe dire "se hai solo un outgoingFlow allora sei un merge"
            //Però in realtà, se io avessi un parallel che ha solo un input e solo un output
            //allora probabilmente non lo considero un merge. E inoltre vorrei anche appicare la regola su di esso.
            //Quindi sarebbe meglio se i merge fossero soltanto "i gateway che hanno un solo outgoingFlow ma che hanno più di
            //un incomingFlow

            //TODO questa parte deve diventare un metodo "isNotASplit" oppure rispettivamente "isNotAMerge" nella classe model
            //merges are gateways that have more than one incoming flow
            //but only one outgoingFlow
            if (!(incomingFlows.size() > 1 && outgoingFlows.size() == 1)) {
                //then it's not a merge
                //we can thus change its outgoingFlows conditions:
                System.out.println("This is a not a merge. Its outgoingFlows will be changed");
                //TODO this has to become two separate methods
                //removing the condition from the outgoing flows of a split
                for (int f = 0; f < outgoingFlows.size(); f++) {
                    Element flow = outgoingFlows.get(f);
                    Element condition = (Element) flow.getElementsByTagName("bpmn:conditionExpression").item(0); //TODO this should work but test it.
                    flow.removeChild(condition);
                }
            } else {
                System.out.println("This is a merge. Its outgoingFlows will not be changed");
            }
        }
    }


    //TODO

    /**
     * Changing inclusive to exclusive.
     * @param startingModel
     * @throws Exception
     */
    public static void b (Model startingModel) throws Exception {
        System.out.println("I'm reverting Rule3b");
        Model model = startingModel;
        // Here I'm creating a list of all the inclusive gateways in the inputModel
        NodeList inclusiveGatewayInstances = model.doc.getElementsByTagName("bpmn:inclusiveGateway");
        System.out.println("number of inclusive gateway instances: " + inclusiveGatewayInstances.getLength());


        if (inclusiveGatewayInstances.getLength() == 0) {
            System.out.println("Reverse3b: there are no exclusive gateways in this model");
        }

        //TODO the first part of the firstPart of r3a and r3b can be generalized for bot parallel and exclusive gateways

        // going through all of the parallelGateways in the model:
        for (int i = 0; i <= inclusiveGatewayInstances.getLength(); i++) {

            Element oldInclusive = (Element) inclusiveGatewayInstances.item(0); //this will be the element in case
            //NOTE Why this works?
            // Reason: After the gateway gets deleted from the model, it also gets deleted from inclusiveGatewayInstances
            // For some reason. The problem is that now the element at index 1 is now at index 0.
            // That why we use the same index every time until the list is empty.

            System.out.println("working on the " + (i + 1) + "nd inclusiveGateway");
            System.out.println("The id of the element is " + oldInclusive.getAttribute("id"));

            String[] oldInclusiveCoordinates = model.getPosition(oldInclusive);

            //creating the substitute element in the position of the old one
            String newExclusiveGatewayId = model.newInclusiveGateway(oldInclusiveCoordinates[0], oldInclusiveCoordinates[1]);
            Element newExclusiveGateway = model.findElemById(newExclusiveGatewayId);
            //replacing the two elements
            model.replaceELement(oldInclusive, newExclusiveGateway);

            //Here I dont need to distinguish between those that are merges and those that are not.
            //I dont need to touch the incoming/outgoing flows either
        }
    }

    /**
     * TODO
     * @param startingModel
     */
    public static void c (Model startingModel){
    //TODO
    }

}
