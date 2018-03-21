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
//import org.camunda.bpm.model.bpmn.instance.Task;
//ASKANA This doesn't work. It is probably the reason why some methods that are applicable to other elements don't work for tasks.
import org.camunda.bpm.model.bpmn.instance.UserTask;
//ASKANA UserTask on the other hand works. But the problem is that I want to distinguish Task from other types of elements, but I want to have a way to
//Address ALL types of tasks, not just UserTasks. (Otherwise I will have to rewrite the same code once for every type of task.)

import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;

public class Rule3 {
    // this keeps track of the rules that have been applied, in the right order.
    private static String rulesApplied = "";

    public static void main(String[] args) {

	// TODO having a way to use all of the bpmn files that are found inside a single
	// folder.
	// TODO have a way to let the user provide some parameters when launching the
	// program, for example choosing which rules to apply, in which order, where to
	// output the files, if we want to have a single file as input or a whole
	// folder... etc...
	// TODO make a report containing informations about each generated file.
	// TODO find a way to organize the program in multiple classes. I don't really
	// have objects, so am I just complicating things by having multiple classes?

	// String path = askForPath(); //Unlock this to ask the user for a path

	String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
	// String path =
	// "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2.Parallel_R1R3.bpmn.xml"
	// String path =
	// "/Users/rubenfolini/Desktop/Archive/Exclusive/3.1.Exclusive_Multiple.bpmn";

	File bpmnFile = new File(path);

	// this variable is used later to replace the filename with the new one more
	// easily.
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");
	// Will be used when naming the output file (inside method writeModeltoFile)

	// Creating output model
	BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);

	rule3(inputModelInstance);

	// Writing the output model to file
	writeModeltoFile(inputModelInstance, filename);
    }

    // This lets the user decide the path of the file
    // TODO provide exceptions? What happens if it's not the correct path?
    public static String askForPath() {
	Scanner reader = new Scanner(System.in);
	System.out.println("Please enter the location of the bpmn file you wan to transform:");
	String filePath = reader.next();
	reader.close();
	return filePath;
    }

    public static void rule1(BpmnModelInstance inputModel) {
    } // TODO

    public static void rule2(BpmnModelInstance inputModel) {
    } // TODO

    // TODO javadoc of rule 3? How do I do a javadoc?
    public static void rule3(BpmnModelInstance inputModel) {
	// TODO decide on this:
	// Here I'm creating a collection of all the Gateways in the inputModel
	Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class);

	// The following counters serves us to understand if I'm reading the inputModel
	// correctly.
	int exclusiveGatewayCounter = 0;
	int parallelGatewayCounter = 0;
	int outflowsFromParaGateCounter = 0;

	// going through all of the parallelGateways in the model:
	for (Gateway oldGateway : GatewayInstances) {

	    // This is used later. I can't evaluate it after execution because they will
	    // all
	    // be Inclusive anyway. I have to remember what they are now.
	    boolean isParallel = false; // TODO isn't there a better way to do that?
	    boolean isExclusive = false;

	    // Since now this method is generic, I have to make sure the gateway is either
	    // Parallel or Exclusive, otherwise I don't have to do anything.
	    // If both counters remain to zero, I might even want to later print an error
	    // message saying that I didn't find any gateways where I could apply the rule
	    // 3.
	    if (oldGateway instanceof ParallelGateway) {
		isParallel = true;
	    } else if (oldGateway instanceof ExclusiveGateway) {
		isExclusive = true;
	    }
	    if (isExclusive || isParallel) {
		// creating the substitute element
		InclusiveGateway newInclusiveGateway = inputModel.newInstance(InclusiveGateway.class);
		// TODO would be nice to generalize this
		// substitution part as well, the problem
		// Is that I need inputModel to create a new
		// InclusiveGateway?

		// giving it the "id" of the old gateway
		newInclusiveGateway.setAttributeValue("id", oldGateway.getAttributeValue("id"), true);

		// finally replacing the element with its substitute.
		oldGateway.replaceWithElement(newInclusiveGateway);
		newInclusiveGateway.updateAfterReplacement();

		if (isParallel) {
		    // I use this just for counting. E.G. I know that in this model there
		    // are 6
		    // ParallelGateways so I expect C to go to 6
		    parallelGatewayCounter++;
		    System.out.println("Parallel Gateway Number " + parallelGatewayCounter);

		    // Now for each newly created InclusiveGateway I'm going to add
		    // conditional
		    // expressions that always evaluate to TRUE
		    // First I'm adding all of them to a collection:
		    Collection<SequenceFlow> outgoingFlowsInstances = newInclusiveGateway.getOutgoing();
		    for (SequenceFlow outgoingFlow : outgoingFlowsInstances) {
			// I use this just for counting. E.G. I know that the first
			// ParallelGateway has
			// two outgoing flows, so i expect this number to go to 2.
			outflowsFromParaGateCounter++;
			System.out.println("    Outflows: " + outflowsFromParaGateCounter);
			// I'm creating a new conditionExpression:
			ConditionExpression trueConditionExpression = inputModel.newInstance(ConditionExpression.class);
			trueConditionExpression.setType("tFormalExpression");
			// As the name suggest, the condition must always be true:
			trueConditionExpression.setTextContent("${1 == 1}");
			// I'm adding the condition to my outgoingFlow
			outgoingFlow.setConditionExpression(trueConditionExpression);
		    }
		} else if (isExclusive) {
		    // I use this just for counting. E.G. I know that in this model there
		    // are 6
		    // ExclusiveGateways so I expect C to go to 6
		    exclusiveGatewayCounter++;
		    System.out.println("Exclusive Gateway Number " + exclusiveGatewayCounter);
		}
		// I use this if I could not apply rule 3.
		// TODO would be nice to have a way to
		// be sure that the program actually didn't touch anything. For example
		// comparing the old model with the new one.
		if ((parallelGatewayCounter == 0) && (exclusiveGatewayCounter == 0)) {
		    System.out
			    .println("I tried applying rule 3 but I haven't found any parallel or exclusive gateways");
		} else {
		    System.out.println("I applied rule 4");
		    rulesApplied.concat("_R3");
		}
	    }
	}
    }

    // TODO integrate this into the first part of rule3
    // it has to execute before the other part of rule3 because otherwise it will
    // never be applicable
    // TODO create some bpmn graphs to test this
    public static void rule3b(BpmnModelInstance inputModel) {
	// create a collection of ParallelGateways ("A")
	Collection<ParallelGateway> parallelGatewayInstances = inputModel.getModelElementsByType(ParallelGateway.class);
	// FOR RULE 3B TO BE APPLICABLE:
	// the parallel gateway can have 1 or more exclusive gateway as child elements
	// (but only exclusive gateways; if there's something else, we stop and go to
	// the next Parallel Gateway).
	for (ParallelGateway parallelGateway : parallelGatewayInstances) {
	    // ASKANA why is this a "query" instead of a collection as usual?
	    Query<FlowNode> succedingNodes = parallelGateway.getSucceedingNodes();
	}
	// the subsequent exclusive gateways ("B") have one or more conditional tasks as
	// child element ("C").

	// Both those tasks and the exclusive gateway must be connected to another
	// exclusive gateway ("D"). (But not more than one i guess ? ASKANA)
    }

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

    public static void writeModeltoFile(BpmnModelInstance ModelInstance, String filename) {
	// Validate Model
	// Bpmn.validateModel(ModelInstance);
	// TODO use also Ana's test at
	// https://github.com/camunda/camunda-engine-unittest

	// Write to file
	File file = new File("/Users/rubenfolini/Desktop/Archive/Parallel/" + filename + rulesApplied + ".bpmn.xml");
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	Bpmn.writeModelToFile(file, ModelInstance);
    }

}