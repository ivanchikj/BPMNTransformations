package bpmn.transformation2;

import java.io.File;
import java.util.Scanner;
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
	//ASKANA But if that's the case, then i cannot simply change the "applyRule" methods to use a process instead of an inputModela s a parameter
	//ASKANA because the bpmndi is not contained in the process.
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
	    gateway.setName("Test1"); //UNLOCKTHIS used only for testing
	    if (gateway instanceof ParallelGateway) {
		paraGatCounter++;
		System.out.println("  ParaGat:" + paraGatCounter);
		Query <FlowNode> previousNodesQuery = gateway.getPreviousNodes();  	
		//First part of rule 4: if a parallelGateway has more than one task preceding him,
		//or something other than a task, then rule 4 is not applicable.
		boolean success = false;
		try {
		    //ASKANA I put a 'try' here because the method singleResult fails when a node has two immediate predecessors.
		    FlowNode previousNode = previousNodesQuery.singleResult();
		    //If the method fails it means we have two immediate predecessors so we already know the rule is it's not applicable anyway.
		    //Otherwise I use this boolean to tell my program to go forward
		    success= true;
		    
		    if (success && previousNode instanceof Task) {  
			//after the gateway i can have as many immediate successors as i want, to whatever element i want (task, gateway, etc...).
			Query <FlowNode> successiveNodes = gateway.getSucceedingNodes();
			java.util.List <FlowNode> successiveNodesList = successiveNodes.list(); //to use the 'for' i need to transform the query into a list //TODO try to use an iterator instead?
			gateway.setName("Test2"); //UNLOCKTHIS used only for testing
			//I make a list of all the (incoming) flows that attached my gateway to it's predecessor, so that i can later delete them
			//Creating the list now serves to distinguish them from the newly created ones
			//NOTE that I cannot delete the outgoing flows instead, because I want to keep the existing conditions that they might have
			Collection <SequenceFlow> flowsToDelete = gateway.getIncoming();
			System.out.println("FlowsToDelete Size: " + flowsToDelete.size());
			// I now attach the previous node directly to the predecessor of my gateway
			for (FlowNode succedingNode : successiveNodesList) { //TODO if a Node has two incoming
			    succedingNodesCounter++;
			    succedingNode.setName("Test3");//UNLOCKTHIS used only for testing purposes
			    System.out.println("	Succeding nodes: " + succedingNodesCounter);
			    Collection <SequenceFlow> outgoingFlows = succedingNode.getIncoming(); //Note that i call the collection "outgoing" because I'm looking at them from the perspective of the gateway, not the task
			    //TODO if a task has one more incoming flow that it's not coming from the gateway, I need to ignore it.
			    //I can now safely delete my gateway
			    //process.removeChildElement(gateway);			    
			    
			    
			    //I can also delete the now the previously identified flows that attached the gateway to its predecessor 
			    for (SequenceFlow flowToDelete : flowsToDelete) { 
				System.out.println("Addio");
				System.out.println(flowToDelete.getId());
				//process.removeChildElement(flowToDelete);
				}
			    //System.out.println("FlowsToDelete Size after test: " + flowsToDelete.size());
			    //System.out.println("Flows to actually delete : " + gateway.getIncoming().size());
			    for (SequenceFlow flow : outgoingFlows) {
				flow.builder();
				flow.setSource(previousNode);;//ASKANA this does not work
				previousNode.setName("test4");//UNLOCKTHIS used only for testing purposes
				flow.setName("Test5");//UNLOCKTHIS used only for testing purposes
			    }
			}


		    }
		} catch (BpmnModelException e) {
		    //this exception should happen when a node has more than one immediate predecessor
		    System.out.println("I've ignored a node that has more than one incoming flow");
		} finally {
		    //TODO remember to finish it (and edit the report) after rule 4 is complete, not just the first part.
		    Main.rulesApplied.concat("_R4");
		}
	    }
	}
    }
}


// Part2: from exclusiveGateway to double output

// Part3: from inclGateway to double output