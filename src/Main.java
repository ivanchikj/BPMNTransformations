import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

//TODO fare dei test per testare il parsing delle stringhe.

public class Main {


    private static Scanner reader = new Scanner(System.in);


    public static void main (String[] args) throws Exception {

        //noinspection ResultOfMethodCallIgnored
        new File("temp").mkdirs();
        String input = "";
        // input = "testing"; //UNLOCKTHIS used only to go in the manual
        // testing method
        readInput(input);
    }


    /**
     * This method allows me to easily test any public method
     * Activated only by unlocking comment at line 21
     */
    @SuppressWarnings ("RedundantThrows")
    private static void manualTest () throws Exception {
        //INSERT MANUAL TEST HERE
    }


    /**
     * Asks the user for input
     *
     * @return user input as a string
     */
    private static String askForInput () {

        System.out.println("Please enter the path of the file(s) you want to "
         + "transform.");
        System.out.println("Type 'help' to display the list of parameters or "
         + "if this is your first time using the program.");
        return reader.nextLine();
    }


    private static void printInfo () {

        String n = System.getProperty("line.separator");
        String mail = "realityhas@gmail.com";//TODO

        String info = "";
        info += n + "For questions and suggestions write to " + mail;

        System.out.println(info);
    }


    /**
     * Analyze input and decide
     * If we want to ask the user or not (maybe we are testing an hardcoded
     * input)
     * If we want to print the user guide or not
     * Try to create an execution based on the provided input
     *
     * @param input user-provided String containing the path of the input
     *              model(s) and optional parameters
     */
    private static void readInput (String input) throws Exception {

        System.out.println("Analyzing user input: " + input);

        if (input.equals("")) { //This is always expected to be true except
            // when testing something.
            input = askForInput();
            readInput(input);
        } else if (input.equals("testing")) {
            System.out.println("This is a manual test!");
            manualTest();
            reader.close();
        } else if (input.equalsIgnoreCase("help")) {
            printHelp();
            input = askForInput();
            readInput(input);
        } else if (input.equalsIgnoreCase("clear")) {
            deleteTemp();
        } else if (input.equalsIgnoreCase("info")) {
            printInfo();
        } else {
            try {
                analyzeInput(input);
                //Execution execution = new Execution(input);
            } catch (Exception e) {
                System.out.println("There was a problem reading the provided "
                 + "input.");
                e.printStackTrace();
            }
        }
        reader.close();
    }


    /**
     * This won't delete subfolders. This is the desired behavior however, as
     * it should never happen that there is a subfolder in the folder temp,
     * and if there is it means that the user put it there manually so we
     * wouldn't want to delete it anyway.
     */
    private static void deleteTemp () {

        File temp = new File("temp");
        String[] entries = temp.list();
        for (String s : entries) {
            File currentFile = new File(temp.getPath(), s);
            //noinspection ResultOfMethodCallIgnored
            currentFile.delete();
        }
        System.out.println("The temp folder has been emptied.");
    }


    private static void analyzeInput (String input) throws IOException,
     SAXException, ParserConfigurationException {

        boolean isAFolder;
        String singleFilepath;
        String folderPath = findPathFromString(input);
        ArrayList<Model> startingModels = new ArrayList<>();
        boolean permutations;
        boolean recursive;
        ArrayList<Parameter> parameters;
        //searchForRecursive();
        //searchForPermutations();

        Document doc = tryToOpenAFile(input);
        if (doc == null) { //Could not find a file to be opened in the input
            // string
            isAFolder = true; // ^ this means it must be a folder.
            startingModels = initializeModels(folderPath);
        } else {
            isAFolder = false;

            singleFilepath = doc.getDocumentURI(); // TODO test this
            startingModels.add(initializeModel(singleFilepath));
        }

        parameters = initializeParams(input);

        boolean[] executionType = findExecutionType(input);
        permutations = executionType[0];
        recursive = executionType[1];

//        private String removePathFromInput() {
//            //I'm removing the path from the input, to have just the string
// with the parameters.a
//            String params;
//
//            if (isAFolder) {
//                params = this.input.replaceAll(folderPath, "");
//            } else {
//                params = this.input.replaceAll(singleFilepath, "");
//            }
//            return params;
//        }

        //System.out.println("ParamString: " + paramString);

        //initializeParams(paramString);

        try {
            //TODO qui controlla che le regole non siano opposte:
            //areRulesOpposite()
            //Scrivi un syserr e poi lascia all'utente la possibilità di
            // continuare.
            new Execution(input, isAFolder, startingModels, folderPath,
             permutations, recursive, parameters);
        } catch (Exception e) {
            System.err.println("Was not able to create an execution with the "
             + "input provided");
            e.printStackTrace();
        }
    }


    private static ArrayList<Parameter> initializeParams (String input) {

        String paramString = findParameters(input);
        String str = paramString.replaceAll(" ", "");
        ArrayList<Parameter> parameters = new ArrayList<>();

        System.out.println("String : " + str);
        //let's separate the parameters between each other
        ArrayList<String> paramStrings = new ArrayList<>();

        while (str.indexOf("-") != str.lastIndexOf("-")) {
            System.out.println(str);
            String param;
            int firstDash = str.indexOf("-") + 1;
            int nextDash = str.indexOf("-", firstDash + 1);

            param = str.substring(firstDash, nextDash);

            //System.out.println(" " + param);

            paramStrings.add(param);
            //System.out.println(" : " + str);
            str = str.substring(param.length() + 1);
            //System.out.println("       " + str);
        }

        if (str.contains("-") && str.indexOf("-") == str.indexOf("-")) {
            //let's add the last one:
            String lastParam = str.substring(str.indexOf("-") + 1);
            paramStrings.add(lastParam);
        }

        System.out.println("I've finished separating the parameters in " +
        "different strings");

        for (String param : paramStrings) {
            if (param.contains("*")) { // it means we have to use a different
                // constructor because we have an aggregateBy param
                int aggregateBy =
                 Integer.parseInt(param.substring(param.indexOf("*") + 1));
                System.out.println("AggregateBY " + aggregateBy);
                param = param.substring(0, param.indexOf("*"));
                Parameter parameter = new Parameter(param, aggregateBy);
                //Finally transforming our string into a Parameter
                parameters.add(parameter); //Adding it to the list of parameters
            } else {
                Parameter parameter = new Parameter(param); //Finally
                // transforming our string into a Parameter
                parameters.add(parameter); //Adding it to the list of parameters
            }
        }
        return parameters;
    }


    /**
     * This method just find the parameters part of the input string. The
     * parameter part starts after the first round bracket and ends before
     * the first one.
     *
     * @param input the input provided by the user
     * @return the part of the input that contains only the parameters
     */
    private static String findParameters (String input) {

        return input.substring(input.indexOf('('), (input.indexOf(')')));
    }


    /**
     * @param input the input provided by the user
     * @return the part of the input that contains only the path
     */
    private static String findPathFromString (String input) {

        System.out.println();

        return input.substring(0, input.lastIndexOf('/') + 1);
    }


    /**
     * TODO test this
     * TODO if this works delete "searchFormPermutations()" and
     * "searchForRecursive()"
     * <p>
     * <p>
     * This method tries to find which execution type it has to do among
     * these possibilities:
     * YPYR || YRYP (Yes Perm Yes Rec)
     * NPNR || NRNP (No Perm No Rec)
     * YPNR || NRYP (Yes Perm No Rec)
     * NPYR || YRNP (No Perm Yes Rec)
     * <p>
     * In practice, I'm trying to find one among those 4 string types.
     *
     * @param input the input provided by the user
     * @return a boolean[] where position 0 refers to permutations and
     * position 1 refers to recursivity
     */
    private static boolean[] findExecutionType (String input) {

        boolean permutations = false;
        boolean recursive = false;

        if (input.toLowerCase().contains("ypyr") || input.toLowerCase().contains("yryp")) {
            permutations = true;
            //System.out.println("HEY PERMUTATIONS TRUE");
            recursive = true;
//            System.out.println("HEY RECURSIVE TRUE");
        } else if (input.toLowerCase().contains("npnr") || input.toLowerCase().contains("nrnp")) {
            //noinspection ConstantConditions
            permutations = false;
//            System.out.println("HEY PERMUTATIONS FALSE");
            recursive = false;
//            System.out.println("HEY RECURSIVE FALSE");
        } else if (input.toLowerCase().contains("ypnr") || input.toLowerCase().contains("nryp")) {
            permutations = true;
//            System.out.println("HEY PERMUTATIONS TRUE");
            recursive = false;
//            System.out.println("HEY RECURSIVE FALSE");
        } else if (input.toLowerCase().contains("npyr") || input.toLowerCase().contains("yrnp")) {
            //noinspection ConstantConditions
            permutations = false;
//            System.out.println("HEY PERMUTATIONS FALSE");
            recursive = true;
//            System.out.println("HEY RECURSIVE TRUE");
        } else {
            System.out.println("Your input line is missing the part that " +
            "defines the execution type.");
            System.out.println("Type 'help' to learn how to compose an input");
            askForInput();
        }
        boolean[] permutationRecursive = new boolean[2];
        permutationRecursive[0] = permutations;
        permutationRecursive[1] = recursive;

        return permutationRecursive;
    }

//    private static ArrayList<Parameter> rulePool(String input) {
//        ArrayList<Parameter> rulePool = new ArrayList<>();
//        if (input.toLowerCase().indexOf('(') == -1) {
//            System.out.println("The input provided doesn't comply with the
// instructions, type 'help' to see the instructions");
//            askForInput();
//            return null;
//        }
//        return rulePool;
//    }


    /**
     * We use this method if we are getting a folder
     */
    private static ArrayList<Model> initializeModels (String folderPath) throws IOException, SAXException, ParserConfigurationException {

        ArrayList<Model> models = new ArrayList<>();
        System.out.println("Opening a folder at: " + folderPath);
        //List<String> bmpnXMLFiles = new ArrayList<>();
        File dir = new File(folderPath);
        System.out.println("I've found " + Objects.requireNonNull(dir.listFiles()).length + " files inside the folder");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith((".bpmn.xml")) || file.getName().endsWith((".bpmn"))) {
                Model model = initializeModel(file.getPath());
                models.add(model);
            }
        }
        return models;
    }


    private static Model initializeModel (String filepath) throws ParserConfigurationException, SAXException, IOException {

        System.out.println("Opening a file at: " + filepath);
        File file = new File(filepath);

        if (file.getName().endsWith((".bpmn.xml")) || file.getName().endsWith((".bpmn"))) { //TODO decidi se questo è il modo giusto di trovare i file
            return new Model(file.getPath());
        }
        return null;
    }


    /**
     * This method tries to find a file inside the user provided input string.
     * To better distinguish between the path and the parameters it tries to
     * open the first letter of the string.
     * After it fails, it tries to read the first two letters and so on until
     * it finds a path and successfully opens
     * a file. If it cannot open a file, it returns null;
     * Thi is also useful because it can open both files with a bpmn.xml
     * extension or just .xml extension or any extension.
     */
    private static Document tryToOpenAFile (String input) throws ParserConfigurationException {

        StringBuilder str = new StringBuilder();
        DocumentBuilderFactory docFactory =
         DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc;
        for (int i = 0 ; i < input.length() ; i++) {
            str.append(input.charAt(i));
            try {

                // System.out.println("Trying to open file at position: " +
                // str);

                doc = docBuilder.parse(str.toString());
                //System.out.println("I've found a single file: " + doc
                // .getElementsByTagName("startEvent").item(0).getTextContent
                // ());
                //this.isAFolder = false;
                //this.singleFilepath = str;
                return doc; // it returns something successfully
            } catch (Exception e) { //I expect a bunch FileNotFoundExceptions
                // until I can open one.
                // System.out.println("Failed to open file at position " + str);
                if (str.length() == input.length()) { //this means I got to
                    // the end and I haven''
                    System.out.println("I've reached the end of the string " + "without finding a single file.");
                    //this.isAFolder = true;
                    return null; //will be null
                }
            }
        }
        //this.isAFolder = true;
        return null; //will be null if it didn't find a file successfully.
    }


    /**
     * TODO this works but has false positives.
     * This method wants to avoid having infinite loops of trying to apply a
     * rule and
     * then immediately afterwards its opposite etc.
     * TODO this check is only applied when recursive behavior is on.
     * Otherwise it's not needed because infinite
     * loops can only happen when recursive behavior is on.
     * Moreover, it doesn't stop the program. It merely asks the user if
     * he/she wishes to continue.
     */
    private boolean areRulesOpposite (Parameter parameter1,
     Parameter parameter2) {

        String p1 = parameter1.rule;
        String p2 = parameter2.rule;

        return p1.equals("r" + p2) || p2.equals("r" + p1);
    }

    //TODO:
//    Idea: since the help section is quite long, have a printHelp() method
// that just calls other methods.
//
//            this way you can allow the user to print just the parameter
// section, the example section, the behavior section etc...
//
//
//    Include all instructions in the help section in the readme.

    //TODO magari usare il metodo string e text separator per avere un codice
    // più ordinato.


    //@formatter:off
    private static void printHelp () {

        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println(" - - - - - - - - BPMN TRANSFORMATION TOOL GUIDE - - - - - - - - - ");
        System.out.println();
        System.out.println("The program accepts BPMN 2.0 models provided in ");
        System.out.println("a '.bpmn.xml' or '.xml' format ");
        System.out.println();
        System.out.println("The program accepts BPMN models whose tags are ");
        System.out.println("written in the Signavio or Camunda styles");
        System.out.println();
        System.out.println("You can either provide a single file or a whole folder,");
        System.out.println("in which case all of the bpmn files contained ");
        System.out.println("will be treated.");
        System.out.println();
        System.out.println("The resulting files will be saved ");
        System.out.println("in a subfolder of the current ");
        System.out.println("path called 'output' alongside ");
        System.out.println("a report of the execution.");
        System.out.println();
        System.out.println();
        System.out.println("SELECTING PERMUTATIONS/RECURSIVE BEHAVIOR: - - - - - - - - - - - -");
        System.out.println();
        System.out.println("You have to chose whether to apply ");
        System.out.println("permutations of the chosen rules.");
        System.out.println("(i.e. apply those same rules in different orders and combinations)");
        System.out.println("or not.");
        System.out.println("You also have to decide whether to activate the recursive behavior or not.");
        System.out.println("(i.e. allow for one rule to be applied multiple times."); //TODO controllare che ci sia un check nel programma per impedire che una regola e il suo apposto siano nella pool.
        System.out.println("After the path of the file just add one of these 4 different options: ");
        System.out.println();
        System.out.println("        'YPYR' or 'YRYP'   (i.e. Yes Perm. Yes " +
         "Rec.)");
        System.out.println("        'NPNR' or 'NRNP'   (i.e. No Perm. No Rec.");
        System.out.println("        'YPNR' or 'NRYP'   (i.e. Yes Perm. No Rec.)");
        System.out.println("        'NPYR' or 'YRNP'   (i.e. No Perm. Yes Rec.)");
        System.out.println();
        System.out.println();
//
        //System.out.println("WARNING: DO NOT TURN ON PERMUTATIONS WHEN A RULE AND ITS OPPOSITE ARE INSIDE THE PARAMETER POOL");
        //System.out.println(" ARE BOTH IN THE POOL OF RULES TO BE APPLIED TO AVOID INFINITE LOOPS.");
        System.out.println("THE PARAMETERS:- - - - - - - - - - - - - - - - - - - - - - - - - -");
        System.out.println();
        System.out.println("After defining the behavior of the program");
        System.out.println(" you can choose which rules to apply.");
        System.out.println("Of course, if permutations are turned on, ");
        System.out.println("the order in which you write the rules doesn't matter.");
        System.out.println();
        System.out.println();
        System.out.println("LIST OF POSSIBLE PARAMETERS: - - - - - - - - - - - - - - - - - - -");
        System.out.println();
        System.out.println();
        for (int i = 0 ; i < Parameter.validParameters.length ; i++) {
            System.out.print("-" + Parameter.validParameters[i] + " ");
        }//the list of parameters is extrapolated from the actual array of strings, otherwise we could
        //have differences between the list in help and the actual list
        System.out.println();
        System.out.println();
        System.out.println("THE PARAMETER 'aggregateBY':");
        System.out.println();
        System.out.println("in the case of the rules r1, r2 and r3c it's possible to decide how to aggregate the");
        System.out.println("sequenceFlows of the original gateway. E.g. 2by2, 3by3 etc. The default value is 2by2");
        System.out.println("To decide how to aggregate the gateways simply add and asterisk '*' after the name of the rule");
        System.out.println("EXAMPLE: r1*3");
        System.out.println();
        System.out.println();

        System.out.println("All parameters are to be preceded by a dash '-'");
        System.out.println("The parameters are to be placed inside a couple of curly braces and preceded by a dash, example: ");
        System.out.println("(-1 -r3*2)");
        System.out.println("NOTE that if you leave the brackets empty the ");
        System.out.println("default set of rules that will be applied is {1,2,3,4}");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("- - - - - - - - - - - - - EXAMPLE INPUTS - - - - - - - - - - - - -");
        System.out.println();
        System.out.println("	 ./ExampleFolder/exampleModel.bpmn.xml YPYR (-1 -R2*3 -3a)"); //TODO così non funziona. Fai in modo che gli spazi non contino
        System.out.println("	 ./ExampleFolder/ NPNR (-1-r2*5)");
        System.out.println("	 ./ExampleFolder/ YRNP (-1 -2 -3 -4)");
        System.out.println("	 ./ExampleFolder/ NPYR (-2-4)");

        System.out.println();
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        System.out.println();
        System.out.println();
        System.out.println();
//@formatter:on
        //TODO add more details on the behavior of the program, like in the
        // slides. (EG mention recursivity, the behavior when having no
        // parameters etc)
        //TODO insert an URL with a detailed guide (could be the readme on
        // github)
        //In that guide the transformations done by the rules (with Ana's
        // images) should be displayed
    }


}
