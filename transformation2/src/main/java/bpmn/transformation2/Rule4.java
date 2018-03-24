package bpmn.transformation2;

import java.io.File;
import java.util.Scanner;
import java.util.Collection;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
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
	// Here I'm creating a collection of all the Gateways in the inputModel
	Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class);

	// The following counter serves me to understand if I'm reading the inputModel
	// correctly.
	int parallelGatewayCounter = 0;
	//	int applicableGatewaysCounter = 0;
	//	int 
	for (Gateway gateway : GatewayInstances) {
	    if (gateway instanceof ParallelGateway) {
		Query<FlowNode> previousNodesQuery = gateway.getPreviousNodes();//TODO  	

		//First part of rule 4: if a parallelGateway has more than one task preceding him,
		//or something other than a task, then rule 4 is not applicable.
		FlowNode previousNode = previousNodesQuery.singleResult();
		//ASKANA Should I put a 'try' here because the method singleResult fails when a node has two immediate predecessors?
		//If the method fails it means we have two immediate predecessors so we already know the rule is it's not applicable anyway.
		if (previousNodesQuery.count() == 1 && previousNode instanceof Task) {
		    // after the gateway, i can have as many immediate successors as i want, to whatever element i want (task, gateway, etc...).
		    Query<FlowNode> successiveNodes = gateway.getSucceedingNodes();
		    // I now attach the previous node directly to the successors:
		    java.util.List<FlowNode> successiveNodesList = successiveNodes.list();
		    for (FlowNode succedingNode : successiveNodesList) {
			Collection<SequenceFlow> incomingFlows = succedingNode.getIncoming();
			for ( SequenceFlow flow : incomingFlows ) {
			    flow.setSource(previousNode);
			}

		    }
		}
	    }



	    for (FlowNode node : previousNodesList ) {
		String nodeId = node.getId();
		System.out.println(nodeId);
	    }
	}
    }

    // Part2: from exclusiveGateway to double output

    // Part3: from inclGateway to double output
}


}
