package bpmn.transformation2;
import java.io.File;
import java.util.Scanner;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;

public class Rule3 {

	public static void main(String[] args) {

		//String path = askForPath(); //Unlock this to ask the user for a path

		String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
		//String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2 Parallel_R1R3.bpmn.xml"
		//String path = "/Users/rubenfolini/Desktop/Archive/Exclusive/3.1.Exclusive_Multiple.bpmn";
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
		//TODO decide on this:
		//Here I'm creating a collection of all the Gateways in the inputModelInstance
		Collection<Gateway> GatewayInstances = inputModel.getModelElementsByType(Gateway.class);

		//The following counters serves us to understand if I'm reading the inputModel correctly.
		int exclusiveGatewayCounter = 0;
		int parallelGatewayCounter = 0;
		int outflowsFromParaGateCounter = 0;

		//going through all of the parallelGateways in the model:
		for(Gateway oldGateway : GatewayInstances){

			//This is used later. I can't evaluate it after execution because they will all be Inclusive anyway. I have to remember what they are now.
			boolean isParallel = false; //TODO isn't there a better way to do that?
			boolean isExclusive = false;			

			//Since now this method is generic, I have to make sure the gateway is either Parallel or Exclusive, otherwise I don't have to do anything.
			//If both counters remain to zero, I might even want to later print an error message saying that I didn't find any gateways where I could apply the rule 3.			
			if (oldGateway instanceof ParallelGateway) { isParallel = true; } else if (oldGateway instanceof ExclusiveGateway) { isExclusive = true; }
			if (isExclusive || isParallel) {
				//creating the substitute element
				InclusiveGateway newInclusiveGateway = inputModel.newInstance(InclusiveGateway.class); // TODO would be nice to generalize this substitution part as well, the problem Is that I need inputModel to create a new InclusiveGateway? 

				//giving it the "id" of the old gateway
				newInclusiveGateway.setAttributeValue("id", oldGateway.getAttributeValue("id"), true);

				//finally replacing the element with its substitute.
				oldGateway.replaceWithElement(newInclusiveGateway);
				newInclusiveGateway.updateAfterReplacement();

				if (isParallel) {
					//I use this just for counting. E.G. I know that in this model there are 6 ParallelGateways so I expect C to go to 6
					parallelGatewayCounter++;
					System.out.println("Parallel Gateway Number " + parallelGatewayCounter);

					//Now for each newly created InclusiveGateway I'm going to add conditional expressions that always evaluate to TRUE
					//First I'm adding all of them to a collection:
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
				} else if (isExclusive){
					//I use this just for counting. E.G. I know that in this model there are 6 ExlsusiveGateways so I expect C to go to 6
					exclusiveGatewayCounter++;
					System.out.println("Exclusive Gateway Number " + exclusiveGatewayCounter);
				}
				//I use this if I could not apply rule 3. TODO would be nice to have a way to be sure that the program actually didn't touch anything. I have to reorganize.	
				if ((parallelGatewayCounter == 0) && (exclusiveGatewayCounter == 0)) {System.out.println("I tried applying rule 3 but I haven't found any parallel or exclusive gateways");}

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