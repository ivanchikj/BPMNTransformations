import java.io.File;
import java.util.Collection;

import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class Prova2 {

	public static void main(String[] args) {

		String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
		//String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2 Parallel_R1R3.bpmn.xml"
		File bpmnFile = new File(path);
		String filename = bpmnFile.getName().replace(".bpmn.xml", "");
		BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);
		BpmnModelInstance outputModelInstance = Bpmn.readModelFromFile(bpmnFile); // here I cant just say output = input because those are object and it would just copy the address right?
		rule3(inputModelInstance,outputModelInstance);
		writeModeltoFile(outputModelInstance, filename);
	}

	public static void rule3 (BpmnModelInstance inputModel, BpmnModelInstance outputModel) {

		/*Why do I have to extract the ParallelGatway type from an instance of a model? 
		Don't I have an abstract "idea" of what a ParallelGateway is?*/ 
		ModelElementType parallelGatewayType = inputModel.getModel().getType(ParallelGateway.class);
		/*Why does it works even if I'm getting a type from a model that has no gateway of the requested type?*/
		ModelElementType inclusiveGatewayType = inputModel.getModel().getType(InclusiveGateway.class);
		//ModelElementType exclusiveGatewayType = inputModelInstance.getModel().getType(ExclusiveGateway.class);
		ModelElementType sequenceFlowType = inputModel.getModel().getType(SequenceFlow.class);
		ModelElementType activityType = inputModel.getModel().getType(Activity.class);
		ModelElementType scriptTaskType = inputModel.getModel().getType(ScriptTask.class);
		//ModelElementType startEventType = modelInstance.getModel().getType(StartEvent.class);

		//Here I'm creating a collection of all the parallelGateways in the outputModelInstance (which for now its identical to the starting model	
		Collection<ModelElementInstance> parallelGatewayInstances = outputModel.getModelElementsByType(parallelGatewayType);


		int parallelGatewayCounter = 0;
		int outflowsFromParaGateCounter = 0;
		for(ModelElementInstance oldParallelGateway : parallelGatewayInstances){
			//I use this just for counting. I know that in this model there are 6 ParaGateways so I know that I'm reading something if C goes to 6
			parallelGatewayCounter++;
			System.out.println("Parallel Gateway Number " + parallelGatewayCounter);

			//creating the substitute			
			BpmnModelElementInstance newInclusiveGateway =  outputModel.newInstance(inclusiveGatewayType);
			newInclusiveGateway.setAttributeValue("id", oldParallelGateway.getAttributeValue("id"), true);
			//finally replacing the element with its substitute.
			oldParallelGateway.replaceWithElement(newInclusiveGateway);
			
			oldParallelGateway.updateAfterReplacement();
			newInclusiveGateway.updateAfterReplacement();
			//Finding All Outgoing Flows ***THIS DOES NOT WORK:***
			//Collection<ModelElementInstance> outgoingFlowsInstances = oldParallelGateway.getChildElementsByType(sequenceFlowType);
			//Collection<ModelElementInstance> outgoingFlowsInstances = newInclusiveGateway.getChildElementsByType(sequenceFlowType);
			Collection<ModelElementInstance> outgoingFlowsInstances = oldParallelGateway.getChildElementsByType(scriptTaskType);
			for(ModelElementInstance outgoingFlows : outgoingFlowsInstances) {
				outflowsFromParaGateCounter++;
				System.out.println(" Outflows From Gateway number " + parallelGatewayCounter+ ": " + outflowsFromParaGateCounter);
			}
		}
	}
	public static void writeModeltoFile (BpmnModelInstance ModelInstance, String filename) {
		//Validate Model
		Bpmn.validateModel(ModelInstance);
		// Write to file
		File file = new File("/Users/rubenfolini/Desktop/Archive/Parallel/" + filename + "RULE 3" + ".bpmn.xml");
		Bpmn.writeModelToFile(file, ModelInstance);
	}

}