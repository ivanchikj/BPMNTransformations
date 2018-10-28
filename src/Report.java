import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Report {


    public String time;
    private String text;
    private String header;
    private String resultingModels;
    private String failedTransformations;
    //TODO fai in modo che il report venga tutto ricomposto alla fine.


    Report (Execution execution) {

        this.header = "";
        this.time = execution.executionMoment;

        //let's write the header
        String newline = System.getProperty("line.separator");
        header += "Execution time: " + time + newline;
        header += newline;
        header += "Original input: " + execution.input + newline;
        header += newline;
        header += "Recursive: " + execution.recursive;
        header += newline;
        header += "Permutations: " + execution.permutations + newline;
        header += newline;
        header += "Input Models: " + newline;
        for (Model startingModel : execution.startingModels) {
            header += startingModel.path + newline;
        }
        header += newline;
        header += newline;
        header += newline;
        for (Model resultingModel : execution.resultingModels) {
            header += resultingModel.path + newline;
        }
        header += newline;
        header += newline;
        header += newline;
        header += "List of rules: " + newline;
        for (Parameter param : execution.parameters) {
            if (param.aggregateBy != 0) {
                header += param.rule + " aggregateBy: " + param.aggregateBy + newline;
            } else {
                header += param.rule + " " + newline;
            }
        }
        header += newline;
        header += newline;
        header += newline;
    }


    /**
     * @param startingModel  the starting Model in a transformation or in a
     *                       series of transformations.
     * @param resultingModel the resulting model. Can be null if the outcome
     *                       is false
     * @param ruleString     the rule or list of rules applied to the
     *                       starting model to obtain the resulting model.
     *                       Including the"aggregateBy" parameter if applicable.
     * @param outcome        true if the transformation was successful,
     *                       false if not (including cases where I had an
     *                       exception
     */
    void addOutcome (String startingModel, String resultingModel,
     String ruleString, boolean outcome) {

        String newline = System.getProperty("line.separator");
        header += newline;
        header +=
        "---------------------------------------------------------------------";
        header += newline;
        header += "Starting model: " + startingModel + newline;
        header += newline;
        header += "Rules applied: " + ruleString + newline;
        header += newline;
        header += "Successful: " + outcome + newline;
        header += newline;
        //the resultingModel gets saved only if the transformation is
        // successful:
        if (outcome) {
            header += "The application of the rule was successful (the " +
            "resulting model is different from the starting model)" + newline;
            header += "Resulting model: " + resultingModel + newline;
            header += newline;
        } else {
            header += "the application of the rule " + ruleString + " on " +
             "model " + startingModel + " was unsuccessful. The resulting " +
              "model is " + "identical to the starting model and will not be " +
               "saved";
        }

        header += newline;
    }


    void addFailedTransformation (Transformation t) {
        //TODO
    }


    void Compose () {

        text = header + resultingModels + failedTransformations;
    }


    //TODO it makes more sense to just use the transformation as a parameter,
    // in that case i need a single addOutcome method instead of having one
    // for errors etc... It would make the code cleaner on "Transformation
    // .java" too.
    void addOutcome (Transformation transf) {
//TODO
    }


    public void addTransformation (Transformation transf) {
//    TODO:
//        Nel report, aggiungere un medoto "add transformation" che può
// inserire trasformazione per trasformazione.
//
//        Così nel report avrò la sezione iniziale con i parametri dell
// execution,
//
//        i modelli finali
//
//        e le varie trasformazioni con i loro outcome e quale regola ho
// provato ad aggiungere a quale file.
//
//        Dovrei riuscire facilmente a cambiare l'ordine con il quale vengono
// presentate queste informazioni
//
    }


    public void addError (Model model, Parameter parameter, Exception e) {

        //Se creo addTransformation non ho bisogno di questo metodo, gli
        // errori saranno gestiti lì dentro.
        String newline = System.getProperty("line.separator");
        header += newline;
        header += "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! " +
         "! " + "! ! ! ! !";
        header += newline;
        header += ("There was an error while trying to apply Rule: " + parameter.rule + "*" + parameter.aggregateBy + " to model " + model.path + newline).toUpperCase();
        header += newline;
    }


    void saveToFile (String folderPath) throws IOException {
        // saving the report as a txt
        String filename = folderPath + "Report-" + this.time + ".txt";
        BufferedWriter writer = new BufferedWriter(new PrintWriter(filename));
        System.out.println("I'm saving a report in " + filename);
        writer.write(header);
        writer.close();
    }


}
