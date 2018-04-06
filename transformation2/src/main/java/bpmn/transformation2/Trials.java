package bpmn.transformation2;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;


//This class is just for tests, and will later be deleted

public class Trials {

    //    public static void main(String[] args) throws IOException {
    //	String prova = "prova prova" + '\n' +  "prova a capo";
    //	String folderPath = "/Users/rubenfolini/Desktop/";
    //	writeReportToFile(prova, folderPath);
    //    }
    //    public static void writeReportToFile(String report, String folderPath) throws IOException {
    //	LocalDateTime timestamp = LocalDateTime.now();
    //	BufferedWriter writer = new BufferedWriter( new PrintWriter(folderPath + "Report" + timestamp +".txt"));
    //	writer.write(report);
    //	writer.close( );
    //
    //    }

    public static void deletingTest (BpmnModelInstance inputModel) {

	Collection <Process> Processes = inputModel.getModelElementsByType(Process.class); 
	//ASKANA can an xml file contain more than one process? If so in the main class I should have a loop going through all processes
	//ASKANA But if that's the case, then i cannot simply change the "applyRule" methods to use a process instead of an inputModela s a parameter
	//ASKANA because the bpmndi is not contained in the process.
	Process process = Processes.iterator().next();
	System.out.println("WORKING ON PROCESS : " +  process.getId());


	Collection <Task> tasks = inputModel.getModelElementsByType(Task.class);


	Collection <SequenceFlow> sequenceFlows = process.getChildElementsByType(SequenceFlow.class);

	int counter = 1;
	for (SequenceFlow flow : sequenceFlows) {
	    System.out.println(flow.getId());
	    flow.setName("" + counter);
	    counter++;
	}

	Task task = tasks.iterator().next();

	Collection <SequenceFlow> outgoingFlows = task.getOutgoing();

	SequenceFlow outFlow = outgoingFlows.iterator().next();
	task.getOutgoing().remove(outFlow); //ASKANA this does not work
	System.out.println("removing flow : " + outFlow.getName());

	Collection <EndEvent> endEvent = process.getChildElementsByType(EndEvent.class);
	EndEvent end = endEvent.iterator().next();
	end.setName("Fine");

	for (SequenceFlow flow : sequenceFlows) {
	    if (flow.getName().toString().equals("3")) {
		System.out.println("Found it");
		flow.setName("Found it"); //This works
		flow.setTarget(task); //ASKANA this does not work
	    }

	}
    }
}