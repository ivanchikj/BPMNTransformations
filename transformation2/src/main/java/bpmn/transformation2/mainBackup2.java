public class Main {

    // TODO: find a better place to put those in:
    // this keeps track of the rules that have been applied, in the right order.
    public static String rulesApplied = "";
    public static String report = ""; //TODO a different class for the report 

    public static void main(String[] args) throws Exception {

	//TODO create ad hoc models to test all of my XML functions
	//String input = "./TestGraphs/rule1parallel.bpmn.xml";
	//String input = "./TestGraphs/rule1recursive.bpmn.xml";
	//String input = "./TestGraphs/rule2exclusiveConditions.bpmn.xml";
	//String input = "./TestGraphs/Rule3a.bpmn.xml";
	//String input = "./TestGraphs/Rule3b.bpmn.xml";
	//String input = "./TestGraphs/Rule4a.bpmn.xml";
	//String input = "./TestGraphs/Rule4b.bpmn.xml";
	//String input = "./TestGraphs/Rule4c.bpmn.xml";
	//String input = "./TestGraphs/TravelingTest.bpmn.xml";
	String input = "./TestGraphs/multiple/"; //Testing more than one file.
	
	
	if ( input.equals("") ){ //This is always expected to be true except when testing something.
	    input = askForInput();
	}
	
	if (input.equals("help")) {
	    printHelp();
	} else { 
	    //TODO create a new execution object? And then proceed.
	    //The next lines in this class have to go somewhere else probably.
	}
	
	String[] orderedInput = findPathandParameters(input);
	String path = orderedInput[0];
	String parameters = orderedInput[1];
	// reading a file
	File bpmnFile = new File(path);

	System.out.println("Trying to open file " + path);

	// this variable is used later to replace the filename with the new one more
	// easily. (inside method writeModeltoFile)
	String filename = bpmnFile.getName().replace(".bpmn.xml", "");

	// figuring out the folder in which the file is located
	String folderPath = getFolderFromPath(path);
	Model model = new Model(path);

	System.out.println("I'm applying the rules! " );
	
	applyRules(model, parameters);

	//Writing the output model to file
	writeToFile(model, filename, folderPath);

    }
    private static void printHelp() {
	// TODO print detailed help with all parameters.
	// NOTE it's better if the list of parameters is estrapolated from the actual array of strings, otherwise we could
	// have differenced between the list in help and the actual list	
    }
    /**
     * TODO to be completed / checked
     * TODO see if there's a common way to do this, since it's something typical
     * This separates the filepath from the parameters
     * in the resulting array, the filepath is in the first position [0]
     * while the parameters are in the second position [1]
     * @param input
     * @return
     */
    public static String[] findPathandParameters (String input) {
	String[] pathAndParameters = new String[2];
	if (input.contains("-")) { 
	    pathAndParameters[0] = input.substring(0, input.indexOf("-")); //PATH
	    pathAndParameters[1] = input.substring(input.indexOf("-")+1, input.length()); //PARAMETERS
	} else {
	    System.out.println("No parameters selected");
	    pathAndParameters[0] = input;
	    pathAndParameters[1] = "";
	}
	
	return pathAndParameters;
    }
    /**
     * TODO this method should consider the parameters provided by the users and decide which rules to apply in which order.
     * @param model
     * @param parameters
     * @throws Exception 
     */
    public static void applyRules (Model model, String parameters) throws Exception {

//	String newNodeId = model.newNode("bpmn:task", "0", "200");
//
//	String seqFlow = ((Element) model.doc.getElementsByTagName("bpmn:sequenceFlow").item(0)).getAttribute("id");
//	System.out.println("The id of the first sequenceFlow is " + seqFlow );
//	Element newNodeElement = model.findElemById(newNodeId);
//	System.out.println("The id of the newNode is " + newNodeId);
//	System.out.println("The id of the newNode is " + newNodeElement.getAttribute("id"));
//	model.setTarget(seqFlow, newNodeId);
//	//model.setSource(seqFlow, newNodeId);
	
	//NEWRule1.applyRule(model);
	//Rule2.applyRule(model);
	//NEWRule3.a(model);
	//NEWRule3.b(model);
	//NEWRule4.a(model);
	//NEWRule4.b(model);
	//NEWRule4.c(model);
	
	Element primo = model.findElemById("ExclusiveGateway_1cok4ag"); //test to start the travel agency from an arbitrary point
	
	TravelAgency travelAgency = new TravelAgency(model, primo);
	
	//TravelAgency travelAgency = new TravelAgency(model);
	//travelAgency.getPaths();
	//travelAgency.printPaths();
	//travelAgency.getMandatoryDeepSuccessors();
	
    }

    // This lets the user decide the path of the file
    public static String askForInput() {
	Scanner reader = new Scanner(System.in);
	System.out.println("Please enter the location of the bpmn file you wan to transform:");
	String input = reader.next();
	reader.close();
	return input;
    }

    // This method finds the path of the folder of the file. Used to save.
    public static String getFolderFromPath(String path) {
	int index = path.lastIndexOf('/');
	String folderpath = path.substring(0, index + 1);
	return folderpath;
    }

    /**
     * 
     * @param report
     * @param folderPath
     * @throws IOException
     */
    public static void writeReportToFile(String report, String folderPath) throws IOException {
	// getting today's date
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	String timeAndDate = dateFormat.format(new Date());
	// saving the report as a txt
	BufferedWriter writer = new BufferedWriter(new PrintWriter(folderPath + "Report" + timeAndDate + ".txt"));
	writer.write(report);
	writer.close();
    }
    /**
     * 
     * @param ModelInstance
     * @param filename
     * @param folderPath
     * @throws IOException 
     * @throws TransformerException 
     */
    public static void writeToFile(Model model, String filename, String folderPath) throws IOException, TransformerException {
	// TODO I will have to find a way to automatically append to the filename the
	// rules that have been applied. For now this works because I only have one.
	String newFilePath = folderPath + "output/" + filename + rulesApplied + "TESTTESTTEST" + ".bpmn.xml";
	
	model.saveToFile(newFilePath);
    }

}
