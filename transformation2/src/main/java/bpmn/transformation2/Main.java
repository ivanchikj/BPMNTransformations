package bpmn.transformation2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.joda.time.LocalDate;

public class Main {

    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = ""; //TODO public should be avoided, right?

    public static void main(String[] args) throws IOException {

	//String path = askForPath(); //UNLOCKTHIS to ask the user for a path

	// Used to test Rule3
	// TODO create ad hoc xml files with camunda modeler

	// USED to test Rule4
	//part1:
	//String path = "./TestGraphs/Rule4a.bpmn.xml";

	//Used to test the deleting System
	String path = "./TestGraphs/deletingTest.bpmn.xml";

	//reading a file 
	File bpmnFile = new File(path);


	System.out.println("Trying to open file " + path); //this will be useful when opening a folder with multiple files

	// this variable is used later to replace the filename with the new one more
	// easily.
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");
	// Will be used when naming the output file (inside method writeModeltoFile)

	// Creating output model
	BpmnModelInstance inputModelInstance = Bpmn.readModelFromFile(bpmnFile);

	//UNLOCKTHIS Rule4.applyRule(inputModelInstance);
	Trials.deletingTest(inputModelInstance);


	//figuring out the folder in which the file is located
	String folderPath = getFolderFromPath(path);

	// Writing the output model to file
	writeModeltoFile(inputModelInstance, filename, folderPath);
	
	//writeXMLModeltoFile(inputModelInstance, filename, folderPath);
	
	//UNLOCKTHIS writeReportToFile(report,folderPath);
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
	//getting today's date
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	String timeAndDate  = dateFormat.format(new Date());
	//saving the report as a txt
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "Report" + timeAndDate + ".txt"));
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
	String location = folderPath + "/output/" +  filename + rulesApplied + "DELETINGTEST" + ".bpmn.xml";
	File file = new File(location);
	file.getParentFile().mkdirs();
	System.out.println(location);
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	Bpmn.writeModelToFile(file, ModelInstance);
    }
    /**
     * Used to bypass Camunda's 'writeModelToFile' validation
     * 
     * @param ModelInstance
     * @param filename
     * @param folderPath
     * @throws IOException 
     */
    public static void writeXMLModeltoFile(BpmnModelInstance ModelInstance, String filename, String folderPath) throws IOException {
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	String xml = ModelInstance.;
	//TODO the following lines are exactly the same as the ones in WriteReport method. Maybe i should put them in a separate method.
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "UNVALIDATED XML.txt"));
	writer.write(xml);
	writer.close( );
    }

}
