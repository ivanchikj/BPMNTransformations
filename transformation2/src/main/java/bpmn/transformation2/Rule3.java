package bpmn.transformation2;
import java.io.File;
import java.util.Scanner;
import java.util.Collection;

import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class Rule3 {

	public static void main(String[] args) {

		//String path = askForPath();
		
		String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
		//String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2 Parallel_R1R3.bpmn.xml"
		//TODO having a way to use all of the bpmn files that are found inside a single folder.
		File bpmnFile = new File(path);

		String filename = bpmnFile.getName().replace(".bpmn.xml", ""); //this variable is used later to replace it with the new one more easily. 

		//Creating input and output models
		BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);

		//Applying rule 3 to outputModelInstance:
		rule3(inputModelInstance);
		//TODO I don't need outputModelInstance, i can just save the old instance as a different file.


		//Writing the output model to file
		writeModeltoFile(inputModelInstance, filename);
	}

	//This lets the user decide the path of the file
	//TODO provide exceptions? What happens if it's not the correct path?
	public static String askForPath() {
		Scanner reader = new Scanner(System.in);
		System.out.println("Please enter the location of the bpmn file you wan to transform:");
		String filePath = reader.next();
		reader.close();
		return filePath;
	}

	public static void rule3 (BpmnModelInstance inputModel) {

		//Here I'm creating a collection of all the parallelGateways in the outputModelInstance (which for now its identical to the starting model)
		Collection<ParallelGateway> parallelGatewayInstances = inputModel.getModelElementsByType(ParallelGateway.class);

		//The following counters serves us to understand if I'm reading the inputModel correctly.
		int parallelGatewayCounter = 0; 
		int outflowsFromParaGateCounter = 0;

		//going through all of the parallelGateways in the model:
		for(ParallelGateway oldParallelGateway : parallelGatewayInstances){

			//I use this just for counting. E.G. I know that in this model there are 6 ParaGateways so I expect C to go to 6
			parallelGatewayCounter++;
			System.out.println("Parallel Gateway Number " + parallelGatewayCounter);

			//creating the substitute element
			InclusiveGateway newInclusiveGateway = inputModel.newInstance(InclusiveGateway.class);
			//giving it the "id" of the old gateway
			newInclusiveGateway.setAttributeValue("id", oldParallelGateway.getAttributeValue("id"), true);

			//finally replacing the element with its substitute.
			oldParallelGateway.replaceWithElement(newInclusiveGateway);
			newInclusiveGateway.updateAfterReplacement();			
			
			//Now for each newly created InclusiveGateway I'm going to add conditional expressions that always evaluate to TRUE
			//first I'm adding all of them to a collection:
			Collection<SequenceFlow> outgoingFlowsInstances = newInclusiveGateway.getOutgoing();
			for(SequenceFlow outgoingFlow : outgoingFlowsInstances) {
				
				//I use this just for counting. E.G. I know that the first ParallelGateway has two outgoing flows, so i expect this number to go to 2.
				outflowsFromParaGateCounter++;
				System.out.println("    Outflows: " + outflowsFromParaGateCounter);
				
				//I'm creating a new conditionExpression:
				ConditionExpression trueConditionExpression = inputModel.newInstance(ConditionExpression.class);
				trueConditionExpression.setType("tFormalExpression");
				//As the name suggest, the condition must always be true:
				trueConditionExpression.setTextContent("${1 == 1}");
				//I'm adding the condition to my outgoingFlow
				outgoingFlow.setConditionExpression(trueConditionExpression);
			}
		}
	}
	public static void writeModeltoFile (BpmnModelInstance ModelInstance, String filename) {
		//Validate Model
		//Bpmn.validateModel(ModelInstance);
		//TODO use also Ana's test at https://github.com/camunda/camunda-engine-unittest 

		//Write to file
		File file = new File("/Users/rubenfolini/Desktop/Archive/Parallel/" + filename + "RULE 3" + ".bpmn.xml");
		//TODO I will have to find a way to automatically append to the filename the rules that have been applied. For now this works because I only have one.
		Bpmn.writeModelToFile(file, ModelInstance);
	}

}