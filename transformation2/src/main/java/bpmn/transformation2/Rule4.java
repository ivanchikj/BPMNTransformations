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

public class Rule4 {

    // TODO javadoc of this? Explaining the rule4
    public static void rule4(BpmnModelInstance inputModel) {
	// Part1: from paraGateway to double input
	// Here I'm creating a collection of all the Gateways in the inputModel
	Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class);

	// The following counter serves me to understand if I'm reading the inputModel
	// correctly.
	int parallelGatewayCounter = 0;

	// The idea behind the first part is
	// basically for each ParaGat i want to get the incoming flows, the outgoing
	// flows, and then change the target of the incoming flows to the target of the
	// outgoing flows
	// to connect the parent elements to the child elements directly.
	for (Gateway oldGateway : GatewayInstances) {
	    if (oldGateway instanceof ParallelGateway /* || oldGateway instanceof ExclusiveGateway */) {
		// Collection<SequenceFlow> outgoingFlows = oldGateway.getOutgoing();
		// for (SequenceFlow outgoingFlow : outgoingFlows) {
		// outgoingFlow.setSource((FlowNode) oldGateway.getPreviousNodes()); //the
		// source of the outgoing flow will be the parent element of the gateway.
		// outgoingFlow.setTarget(arg0);
		// //outgoingFlow.getChildElementsByType(UserTask.class); //TODO ask Ana,
		// why I
		// can put UserTask but not class? I fear I will have to write the same
		// method
		// for every task type.
		// }
		Collection<SequenceFlow> incomingFlows = oldGateway.getIncoming();
		for (SequenceFlow incomingFlow : incomingFlows) {
		    incomingFlow.setTarget((FlowNode) oldGateway.getOutgoing()); // TODO
		}
		// TODO
	    }
	}

	// Part2: from exclusiveGateway to double output

	// Part3: from inclGateway to double output
    }

    
}
