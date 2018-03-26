package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.joda.time.LocalDate;

public class Main {

    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = ""; //TODO public should be avoided, right?

    public static void main(String[] args) throws IOException {
	// TODO having a way to use all of the bpmn files that are found inside a single
	// folder.
	// TODO have a way to let the user provide some parameters when launching the
	// program, for example choosing which rules to apply, in which order, where to
	// output the files, if we want to have a single file as input or a whole
	// folder... etc...

	// TODO / ASKANA find a way to organize the program in multiple classes. I don't really
	// have objects, so am I just complicating things by having multiple classes?
	//UNLOCKTHIS:
	//String path = askForPath(); //Unlock this to ask the user for a path
	
	// Used to test R3
	// String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.1.Parallel_Multiple.bpmn.xml";
	// String path = "/Users/rubenfolini/Desktop/Archive/Parallel/1.2.2.Parallel_R1R3.bpmn.xml"
	// String path = "/Users/rubenfolini/Desktop/Archive/Exclusive/3.1.Exclusive_Multiple.bpmn";
	
	// Used to test R3partb
	// TODO create ad hoc xml file with camunda modeler
	
	
	//Used to test R4Part1
	String path = "/Users/rubenfolini/Desktop/Test.bpmn.xml";

	//reading a file 
	File bpmnFile = new File(path);
	
	
	System.out.println("Trying to open file " + path); //this will be useful when opening a folder with multiple files
	
	// this variable is used later to replace the filename with the new one more
	// easily.
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");
	// Will be used when naming the output file (inside method writeModeltoFile)

	// Creating output model
	BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);
	
	Rule4.applyRule(inputModelInstance);
	
	//figuring out the folder in which the file is located
	String folderPath = getFolderFromPath(path);
		
	// Writing the output model to file
	writeModeltoFile(inputModelInstance, filename, folderPath);
	writeReportToFile(report,folderPath);
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
    //This method finds the path of the folder of the file. Used to save 
    public static String getFolderFromPath(String path) {
	int index=path.lastIndexOf('/');
	String folderpath = path.substring(0,index+1);
	return folderpath;
    }
    	//ASKANA
    	//The idea is to update the report every time i do something
  	//and then save it at the end. But is that considered bad practice?
  	//The "report" variable would need to be modified
  	//by many different methods
    public static void writeReportToFile(String report, String folderPath) throws IOException {
	LocalDate timestamp = LocalDate.now();
	BufferedWriter writer = new BufferedWriter( new PrintWriter(folderPath + "Report" + timestamp +".txt"));
	writer.write(report);
	writer.close( );
	
    }
    public static void writeModeltoFile(BpmnModelInstance ModelInstance, String filename, String folderPath) {
	//Validate Model UNLOCKTHIS (disabled for testing purposes)
	//NOTE even with the following line disabled, it still refuses to save a malformed bpmn graph:
	//writemodeltofile probably already includes validateModel inside.
	//Bpmn.validateModel(ModelInstance);
	// TODO use also Ana's test at
	// https://github.com/camunda/camunda-engine-unittest

	// Write to file
	// TODO "rulesApplied" never gets updated. Why? Shoul i provide get and set methods? I guess there's a simpler way.
	String location = folderPath + filename + rulesApplied + "RULE4TEST" + ".bpmn.xml";
	File file = new File(location);
	System.out.println(location);
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	Bpmn.writeModelToFile(file, ModelInstance);
    }

}
