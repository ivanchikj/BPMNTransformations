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
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class Rule3 {

	public static void main(String[] args) {

		String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
		//String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2 Parallel_R1R3.bpmn.xml"

		File bpmnFile = new File(path);

		String filename = bpmnFile.getName().replace(".bpmn.xml", ""); //this will be used later to replace it with the new one more easily. 

		//Creating input and output models
		BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);
		BpmnModelInstance outputModelInstance = Bpmn.readModelFromFile(bpmnFile); // here I can't just say output = input because those are object and it would just copy the address right?

		//Applying rule 3 to outputModelInstance:
		rule3(inputModelInstance,outputModelInstance);
		//TODO I don't need outputModelInstance, i can just save the old instance as a different file.


		//Writing the output model to file
		writeModeltoFile(outputModelInstance, filename);
	}
	public void askForPath() {
		
	}
	public static void rule3 (BpmnModelInstance inputModel, BpmnModelInstance outputModel) {

		/*Why do I have to extract the ParallelGatway type from an instance of a model? 
		Don't I have an abstract "idea" of what a ParallelGateway is?*/ 
		/*And why does it works even if I'm getting a type from a model that has no gateway of the requested type?*/

		//Extracting the types of elements that i need

		//TODO when I'm done, remove the ones that I didn't use

		//Here I'm creating a collection of all the parallelGateways in the outputModelInstance (which for now its identical to the starting model)
		Collection<ParallelGateway> parallelGatewayInstances = outputModel.getModelElementsByType(ParallelGateway.class);
		
		int parallelGatewayCounter = 0; 
		int outflowsFromParaGateCounter = 0;
		
		//going through all of the parallelGateways in the model:
		for(ModelElementInstance oldParallelGateway : parallelGatewayInstances){
			
			//I use this just for counting. E.G. I know that in this model there are 6 ParaGateways so I expect C to go to 6
			parallelGatewayCounter++;
			//System.out.println("Parallel Gateway Number " + parallelGatewayCounter);

			//creating the substitute element
			InclusiveGateway newInclusiveGateway = outputModel.newInstance(InclusiveGateway.class);
			//BpmnModelElementInstance newInclusiveGateway =  outputModel.newInstance(inclusiveGatewayType);
			//giving it the "id" of the old gateway
			newInclusiveGateway.setAttributeValue("id", oldParallelGateway.getAttributeValue("id"), true);
			//finally replacing the element with its substitute.
			oldParallelGateway.replaceWithElement(newInclusiveGateway);
			//After this point, what should I use to look for the outgoing flows? The Old or the New?
			
			newInclusiveGateway.updateAfterReplacement();			
			//Finding All Outgoing Flows ***THIS DOES NOT WORK:*** :
			
			//Collection<ModelElementInstance> outgoingFlowsInstances = oldParallelGateway.getChildElementsByType(sequenceFlowType);
			Collection<SequenceFlow> outgoingFlowsInstances = newInclusiveGateway.getOutgoing();
			//Here I'm trying to understand what getChildElement does. Does it returns the flows or the elements that follow? It doesnt seem to do either.
			for(ModelElementInstance outgoingFlows : outgoingFlowsInstances) {
				outflowsFromParaGateCounter++;
				System.out.println(" Outflows From Gateway number " + parallelGatewayCounter+ ": " + outflowsFromParaGateCounter);
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