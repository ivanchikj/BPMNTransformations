package bpmn.transformation2.Backup;

import java.io.File;
import java.util.Scanner;

import javax.swing.text.Document;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.bpmn.instance.Process;

public class Rule4 {


    /**
     * This method tries to apply "rule 4" to a model given as input.
     * TODO give a reference where one could learn what's rule4
     * TODO explain the expected behavior of the program and also the way it should react to 'bad' inputs
     * @param inputModel
     */
    public static void applyRule(BpmnModelInstance inputModel) {

	
	Collection <Process> Processes = inputModel.getModelElementsByType(Process.class); 
	//ASKANA can an xml file contain more than one process? If so in the main class I should have a loop going through all processes

	Process process = Processes.iterator().next();
	System.out.println("WORKING ON PROCESS : " +  process.getId());

	int gatCounter = 0;
	int paraGatCounter = 0;
	int succedingNodesCounter = 0;
	// Here I'm creating a collection of all the Gateways in the inputModel
	Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class); 

	for (Gateway gateway : GatewayInstances) {
	    gatCounter ++;
	    System.out.println("GatNumber " + gatCounter);
	    gateway.setName("This is a Gateway"); //UNLOCKTHIS used only for testing

	    //TODO simplify this in one single "if"

	    if (gateway instanceof ParallelGateway) {
		paraGatCounter++;
		System.out.println(" Found the " + paraGatCounter + "nd ParalleGateway");
		gateway.setName("This is a parallel gateway");

		
		
		//Verifying first part of rule 4: if a parallelGateway has more than one task immediately preceding him,
		//or something other than a task, then rule 4 is not applicable.

		Collection <SequenceFlow> incomingSequenceFlows = gateway.getIncoming(); 	

		System.out.println("Number of incomingflows : " + incomingSequenceFlows.size());
		//printing all of the incoming sequence flows'ids
		for (SequenceFlow flow : incomingSequenceFlows) {System.out.println(flow.getId());}

		//Checking if our parallel gateway has more than one predecessor:
		boolean hasSinglePredecessor = false;
		try {
		    FlowNode previousNode = gateway.getPreviousNodes().singleResult(); 
		    hasSinglePredecessor = true;
		    System.out.println("Does it have a single predecessor? : " +  hasSinglePredecessor);
		} catch (BpmnModelException e) {
		    //this exception should only happen when a node has more than one immediate predecessor
		    System.out.println("I've ignored a node that has more than one incoming flow");
		}
		
		if (incomingSequenceFlows.size() == 1 && hasSinglePredecessor) {
		    FlowNode previousNode = gateway.getPreviousNodes().list().iterator().next();
		    Task task = process.getChildElementsByType(Task.class).iterator().next();
		    //this only works because at this point I know I only have one predecessor
		    //an alternative would be to put this whole piece of code inside a "try" block so I can use previousNode inside it normally. (like in line 71)
		    //but i don't think it would be particularly better.
		    previousNode.setName("previousNode"); //UNLOCKTHIS 
		    if (previousNode instanceof Task) {
			//after the gateway i can have as many immediate successors as i want, to whatever element i want (task, gateway, etc...).
			Query <FlowNode> successiveNodes = gateway.getSucceedingNodes();
			java.util.List <FlowNode> successiveNodesList = successiveNodes.list(); //to use the 'for' i need to transform the query into a list //TODO try to use an iterator instead?
			gateway.setName("Test2");
			
			//I make a list of all the (incoming) flows that attached my gateway to its predecessor, so that i can later delete them
			//NOTE that creating the list now instead of later serves to distinguish them from the newly created ones
			//NOTE that I cannot delete the outgoing flows instead, because I want to keep the existing conditions that they might have
			Collection <SequenceFlow> flowsToDelete = gateway.getIncoming();
			System.out.println("FlowsToDelete Size: " + flowsToDelete.size());
			
			// I now attach the succedingNode directly to the predecessor of my gateway.
			for (FlowNode succedingNode : successiveNodesList) { //TODO if a Node has two incoming
			    succedingNodesCounter++;
			    succedingNode.setName("Test3"); //UNLOCKTHIS used only for testing purposes
			    System.out.println("	Succeding nodes: " + succedingNodesCounter);
			    Collection <SequenceFlow> outgoingFlows = succedingNode.getIncoming(); //Note that i call the collection "outgoing" because I'm looking at them from the perspective of the gateway, not the task
			    //TODO if a task has one more incoming flow that it's not coming from the gateway, I need to ignore it.
			    //I can now safely delete my gateway
			    process.removeChildElement(gateway);
			    Main.bpmndiItemsToDelete.add(gateway.getId());

			    
			    //I can also delete the now the previously identified flows that attached the gateway to its predecessor 
			    for (SequenceFlow flowToDelete : flowsToDelete) { 
				System.out.println("I'm deleting the node : " + flowToDelete.getId());
				System.out.println(flowToDelete.getId());
				process.removeChildElement(flowToDelete);
				Main.bpmndiItemsToDelete.add(flowToDelete.getId());
			    }
    
			    for (SequenceFlow flow : outgoingFlows) {

				flowChangeSource(inputModel, process, flow, previousNode); 

				previousNode.setName("PredecessorName");//UNLOCKTHIS used only for testing purposes
				flow.setName("Those should not exist");//UNLOCKTHIS used only for testing purposes
			    }
			}
		    }
		}
	    }
	}
    }

    
 // Part2: from exclusiveGateway to double output

 // Part3: from inclGateway to double output
    
    
    
    
}