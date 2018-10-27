import java.io.PrintWriter;
import java.io.StringWriter;


class Transformation {

    //private Model startingModel;
    Model resultingModel;
    Parameter parameter;
    String outcome; //can be "successful" or "unsuccessful" or the appropriate exception.
    //NOTE it's used only in the report. Usually we use the boolean 'successful' to check if a transf was successfull:
    boolean successful; //if the resulting models are different, it's a success, otherwise it's a failure.
    Exception exception;

//    public Transformation(Model startingModel, Parameter parameter) throws IOException, SAXException, ParserConfigurationException {
//        this.startingModel = startingModel;
//        this.parameter = parameter;
//        createResultingModel();
//
//        applyTheRule();
//    }

//    public void createResultingModel() throws ParserConfigurationException, SAXException, IOException {
//        this.resultingModel = new Model(startingModel.path); //instead of cloning, we simply open the original file another time.
//    }


    //TODO if the resulting file does not pass the BPMN test, then it's not a success it's a fail. Thus the program will fail gracefully, but that will be written in the report differently.

    Transformation(Model startingModel, Parameter parameter) {

//        this.startingModel = new Model(startingModel.path);
        //this.startingModel = startingModel;
        this.parameter = parameter;
        this.successful = false; //default value
        this.resultingModel = startingModel.cloneModel();
        try {

            applyTheRule();

            boolean different = TravelAgency.modelsAreDifferent(resultingModel, startingModel);
            boolean valid = true; // TODO ADD A CHECK to see if the model is valid from a BPMN point of view

            if (different && valid) {
                this.successful = true;
                //System.out.println("The transformation has been successful!");
                outcome = "successful";
//                resultingModel.name = resultingModel.name + parameter.rule; //Let's update the name so the resulting file will have the added rule at the end.
            } else {
                outcome = "unsuccessful";
            }
        } catch (Exception e) {
            this.exception = e;
            this.successful = false;
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            //adding additional details:
            outcome = "There was a problem when trying to apply rule " + parameter.rule + "*" + parameter.aggregateBy + " to model " + startingModel + " stackTrace: " + System.getProperty("line.separator") + stackTrace;

            System.out.println("There was an error while trying to apply Rule " + parameter.rule + " to model: " + startingModel.path);
            System.err.println(stackTrace);
        }


    }

    /**
     * Applies the appropriate rule depending on the parameter
     */
    private void applyTheRule() throws Exception {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("IM APPLYING RULE " + this.parameter.rule + " ON MODEL: " + this.resultingModel.path);
        System.out.println();
        if (parameter.rule.equals("1")) {
            Rule1.applyRule(resultingModel);
            System.out.println();
        }

        if (parameter.rule.equals("2")) {
            Rule2.applyRule(resultingModel);
            System.out.println();
        }

        if (parameter.rule.equals("3")) {
            Rule3.all(resultingModel);
            System.out.println();
        }

        if (parameter.rule.equals("3a")) {
            Rule3.a(resultingModel);

        }

        if (parameter.rule.equals("3b")) {
            Rule3.b(resultingModel);
        }

        if (parameter.rule.equals("3c")) {
            Rule3.c(resultingModel);
        }

        if (parameter.rule.equals("4")) {
            Rule4.all(resultingModel);
        }

        if (parameter.rule.equals("4a")) {
            Rule4.a(resultingModel);
        }

        if (parameter.rule.equals("4b")) {
            Rule4.b(resultingModel);
        }

        if (parameter.rule.equals("4c")) {
            Rule4.c(resultingModel);
        }

        if (parameter.rule.equals("R1")) {
            Reverse1.applyRule(resultingModel, parameter.aggregateBy);
        }

        if (parameter.rule.equals("R2")) {
            Reverse2.applyRule(resultingModel, parameter.aggregateBy);
        }

        if (parameter.rule.equals("R3")) {
            Reverse3.applyRule(resultingModel, parameter.aggregateBy);
        }

        if (parameter.rule.equals("R3a")) {
            Reverse3.a(resultingModel);
        }

        if (parameter.rule.equals("R3b")) {
            Reverse3.b(resultingModel);
        }

        if (parameter.rule.equals("R3c")) {
            Reverse3.c(resultingModel, parameter.aggregateBy);
        }

        if (parameter.rule.equals("R4")) {
            Reverse4.all(resultingModel);
        }

        if (parameter.rule.equals("R4a")) {
            Reverse4.a(resultingModel);
        }

        if (parameter.rule.equals("R4b")) {
            Reverse4.b(resultingModel);
        }

        if (parameter.rule.equals("R4c")) {
            Reverse4.c(resultingModel);
        }

        System.out.println("IM DONE APPLYING RULE " + this.parameter.rule + " ON MODEL: " + this.resultingModel.path);

        this.resultingModel.addRule(parameter);
        //System.out.println(this.resultingModel.name);
        this.resultingModel.addOutputInPath();
    }
}