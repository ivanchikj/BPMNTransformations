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

public class Main {

    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = ""; //TODO public should be avoided, right?

    public static void main(String[] args) {
	// TODO having a way to use all of the bpmn files that are found inside a single
	// folder.
	// TODO have a way to let the user provide some parameters when launching the
	// program, for example choosing which rules to apply, in which order, where to
	// output the files, if we want to have a single file as input or a whole
	// folder... etc...

	// TODO / ASKANA find a way to organize the program in multiple classes. I don't really
	// have objects, so am I just complicating things by having multiple classes?

	String path = askForPath(); //Unlock this to ask the user for a path
	
	// Used to test R3
	// String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
	// String path ="/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2.Parallel_R1R3.bpmn.xml"
	// String path ="/Users/rubenfolini/Desktop/Archive/Exclusive/3.1.Exclusive_Multiple.bpmn";

	//reading a file 
	File bpmnFile = new File(path);

	// this variable is used later to replace the filename with the new one more
	// easily.
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");
	// Will be used when naming the output file (inside method writeModeltoFile)

	// Creating output model
	BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);

	Rule3.rule3(inputModelInstance);

	// Writing the output model to file
	writeModeltoFile(inputModelInstance, filename);
	writeReportToFile();
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

    	//The idea is to update the report every time i do something
  	//and then save it at the end. But is that considered bad practice?
  	//The report variable would need to be modified
  	//by many different methods
    public static void writeReportToFile() {
	
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
