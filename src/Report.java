import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Report {


    public String time;
    private String text;

//    //TODO This is just an experiment constructor. Delete This after using it.
//    public Report(){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy
// HH:mm:ss");
//        this.text = "";
//        this.time = dateFormat.format(new Date());
//
//    }


    Report (Execution execution) {

        this.text = "";
        this.time = execution.executionMoment;

        //let's write the header
        String newline = System.getProperty("line.separator");
        text += "Execution time: " + time + newline;
        text += newline;
        text += "Original input: " + execution.input + newline;
        text += newline;
        text += "Recursive: " + execution.recursive;
        text += newline;
        text += "Permutations: " + execution.permutations + newline;
        text += newline;
        text += "Input Models: " + newline;
        for (Model startingModel : execution.startingModels) {
            //noinspection StringConcatenationInLoop
            text += startingModel.path + newline;
        }
        text += newline;
        text += newline;
        text += newline;

        text += "List of rules: " + newline;
        for (Parameter param : execution.parameters) {
            if (param.aggregateBy != 0) {
                text += param.rule + " aggregateBy: " + param.aggregateBy + newline;
            } else {
                text += param.rule + " " + newline;
            }
        }
        text += newline;
        text += newline;
        text += newline;
    }


    /**
     * @param startingModel  the starting Model in a transformation or in a
     *                       series of trasformations.
     * @param resultingModel the resulting model. Can be null if the outcome
     *                       is false
     * @param ruleString     the rule or list of rules applied to the
     *                       starting model to obtain the resulting model.
     *                       Including the"aggregateBy" parameter if applicable.
     * @param outcome        true if the transformation was successfull,
     *                       false if not (including cases where I had an
     *                       exception
     */
    void addOutcome (String startingModel, String resultingModel,
     String ruleString, boolean outcome) {

        String newline = System.getProperty("line.separator");
        text += newline;
        text +=
        "---------------------------------------------------------------------";
        text += newline;
        text += "Starting model: " + startingModel + newline;
        text += newline;
        text += "Rules applied: " + ruleString + newline;
        text += newline;
        text += "Successful: " + outcome + newline;
        text += newline;
        //the resultingModel gets saved only if the transformation is
        // successfull:
        if (outcome) {
            text += "The application of the rule was successful (the " +
            "resulting model is different from the starting model)" + newline;
            text += "Resulting model: " + resultingModel + newline;
            text += newline;
        } else {
            text += "the application of the rule " + ruleString + " on model "
             + startingModel + " was unsuccessful. The resulting model is " + "identical to the starting model and will not be saved";
        }

        text += newline;
    }


    //TODO it makes more sense to just use the transformation as a parameter,
    // in that case i need a single addOuctome method instead of having one
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
        text += newline;
        text += "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! "
         + "! ! ! ! !";
        text += newline;
        text += ("There was an error while trying to apply Rule: " + parameter.rule + "*" + parameter.aggregateBy + " to model " + model.path + newline).toUpperCase();
        text += newline;
    }


    void saveToFile (String folderPath) throws IOException {
        // saving the report as a txt
        String filename = folderPath + "Report-" + this.time + ".txt";
        BufferedWriter writer = new BufferedWriter(new PrintWriter(filename));
        System.out.println("I'm saving a report in " + filename);
        writer.write(text);
        writer.close();
    }


}
