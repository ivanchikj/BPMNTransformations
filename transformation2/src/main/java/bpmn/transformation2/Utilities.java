package bpmn.transformation2;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class Utilities {

    
    public static void createFlow (BpmnModelInstance inputModel, Process process, FlowNode target, FlowNode source) {

 	source.builder().connectTo(target.getId()).done();
 	//TODO BPMNDI part

     }
     //TODO those two methods should be in a different class because they will be used often
     //TODO find a way to copy every attribute of a flow, not just the condition
     public static void flowChangeTarget (BpmnModelInstance inputModel, Process process, SequenceFlow oldFlow, FlowNode newTarget) {
 	//ASKANA the method createElement does not exist, even though i copied it from the camunda website:
 	//https://docs.camunda.org/manual/7.8/user-guide/model-api/bpmn-model-api/create-a-model/
 	//SequenceFlow newFlow = createElement(process, oldFlow.getId(), SequenceFlow.class);
 	//TODO if this method works, we can remove inputModel from the parameters.
 	
 	//TODO edit the bpmndi here.
 	SequenceFlow newFlow = inputModel.newInstance(SequenceFlow.class);

 	//newFlow.setId(oldFlow.getId());
 	newFlow.setConditionExpression(oldFlow.getConditionExpression());
 	newFlow.setSource(oldFlow.getSource());
 	newFlow.setTarget(newTarget);

 	newFlow.setName("PROVA");//UNLOCKTHIS used for testing
 	System.out.println("I tried changing the Target of the node  : " + oldFlow.getId()); //UNLOCKTHIS
 	process.removeChildElement(oldFlow);
 	
 	//TODO BPMNDI part
     }

     public static void flowChangeSource (BpmnModelInstance inputModel, Process process, SequenceFlow oldFlow, FlowNode newSource) {

 	SequenceFlow newFlow = inputModel.newInstance(SequenceFlow.class);

 	//newFlow.setId(oldFlow.getId());
 	//newFlow.setConditionExpression(oldFlow.getConditionExpression()); //TODO see why this creates problems. Maybe if it doesn't have a conditionExpression it fails.
 	newFlow.setSource(newSource);
 	newFlow.setTarget(oldFlow.getTarget());

 	newFlow.setName("PROVA");//UNLOCKTHIS used for testing
 	System.out.println("I tried changing the Source of the node : " + oldFlow.getId()); //UNLOCKTHIS
 	//process.removeChildElement(oldFlow);
 	
 	//TODO BPMNDI part
     
     }
    
}
