package bpmn.transformation2;

import java.io.File;
import java.util.Scanner;
import java.util.Collection;

import org.camunda.bpm.engine.task.Task;
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
import org.camunda.bpm.model.bpmn.instance.UserTask;

public class Rule4 {

    // TODO javadoc of this? Explaining the rule4

    public static void applyRule(BpmnModelInstance inputModel) {
	// Part1: from paraGateway to double input
	int gatCounter = 0;
	int paraGatCounter = 0;
	int succedingNodesCounter = 0;
	// Here I'm creating a collection of all the Gateways in the inputModel
	Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class); 
	for (Gateway gateway : GatewayInstances) {
	    gatCounter ++;
	    System.out.println("GatNumber " + gatCounter);
	    if (gateway instanceof ParallelGateway) {
		paraGatCounter++;
		System.out.println("  ParaGat:" + paraGatCounter);
		Query<FlowNode> previousNodesQuery = gateway.getPreviousNodes();//TODO  	
		//First part of rule 4: if a parallelGateway has more than one task preceding him,
		//or something other than a task, then rule 4 is not applicable.
		boolean success = false;
		try {
		    FlowNode previousNode = previousNodesQuery.singleResult();
		    success= true;
		    //ASKANA Should I put a 'try' here because the method singleResult fails when a node has two immediate predecessors?
		    //If the method fails it means we have two immediate predecessors so we already know the rule is it's not applicable anyway.
		    if (success && previousNode instanceof Task) {
			// after the gateway, i can have as many immediate successors as i want, to whatever element i want (task, gateway, etc...).
			Query<FlowNode> successiveNodes = gateway.getSucceedingNodes();
			// I now attach the previous node directly to the successors:
			java.util.List<FlowNode> successiveNodesList = successiveNodes.list();
			for (FlowNode succedingNode : successiveNodesList) {
			    succedingNodesCounter++;
			    System.out.println("	Succeding nodes: " + succedingNodesCounter);
			    Collection<SequenceFlow> incomingFlows = succedingNode.getIncoming();
			    for ( SequenceFlow flow : incomingFlows ) {
				flow.setSource(previousNode);
			    }
			}
		    }
		} catch (BpmnModelException e) {
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