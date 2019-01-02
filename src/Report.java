import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Report {


    public String time;
    private String body = "";
    private String header = "";
    private String resultingModels = "";
    private String finalText = "";
    private String failedTransformations = "";
    private String allTransformations;
    final String newline = System.getProperty("line.separator");
    public int[] rulesCount = new int[20];
    String countText = "";
    //TODO spiega come funziona questa classe: in breve il testo viene
    // composto tutto alla fine in modo da avere i resulting models prima dei
    // singoli outcomes.


    Report (Execution execution) {

        this.time = execution.executionMoment;

        //let's write the header
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


    public void addResultingModels (ArrayList<Model> resultingModels) {

        this.resultingModels += "Resulting models: " + resultingModels.size() + newline;
        for (Model resultingModel : resultingModels) {
            this.resultingModels += resultingModel.name + newline;
        }
        this.resultingModels += newline;
        this.resultingModels += newline;
        this.resultingModels += newline;
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

        this.body += newline;
        String newline = System.getProperty("line.separator");
        body += newline;
        body +=
        "---------------------------------------------------------------------";
        body += newline;
        body += "Starting model: " + startingModel + newline;
        body += newline;
        body += "Rules applied: " + ruleString + newline;
        body += newline;
        body += "Successful: " + outcome + newline;
        body += newline;
        //the resultingModel gets saved only if the transformation is
        // successful:
        if (outcome) {
//            header += "The application of the rule was successful (the " +
//            "resulting model is different from the starting model)" + newline;
            body += "Resulting model: " + resultingModel + newline;
            body += newline;
        } else {
            body += "the application of the rule " + ruleString + " on " +
            "model " + startingModel + " was unsuccessful. The resulting " +
            "model is " + "identical to the starting model and will not be " + "saved";
        }
        body += newline;
    }


    /**
     * This method is a really complicated way to do a simple thing, when
     * addOutcome will use transformations instead it will be easier to count
     * how many times every rule has been applied.
     * @param p the rule applied
     */
    void count (Parameter p) {

        if (p.rule.equals("1")) {
            rulesCount[0]++;
        }
        if (p.rule.equals("2")) {
            rulesCount[1]++;
        }
        if (p.rule.equals("3")) {
            rulesCount[2]++;
        }
        if (p.rule.equals("3a")) {
            rulesCount[3]++;
        }
        if (p.rule.equals("3b")) {
            rulesCount[4]++;
        }
        if (p.rule.equals("3c")) {
            rulesCount[5]++;
        }
        if (p.rule.equals("4")) {
            rulesCount[6]++;
        }
        if (p.rule.equals("4a")) {
            rulesCount[7]++;
        }
        if (p.rule.equals("4b")) {
            rulesCount[8]++;
        }
        if (p.rule.equals("4c")) {
            rulesCount[9]++;
        }
        if (p.rule.equals("r1")) {
            rulesCount[10]++;
        }
        if (p.rule.equals("r2")) {
            rulesCount[11]++;
        }
        if (p.rule.equals("r3")) {
            rulesCount[12]++;
        }
        if (p.rule.equals("r3a")) {
            rulesCount[13]++;
        }
        if (p.rule.equals("r3b")) {
            rulesCount[14]++;
        }
        if (p.rule.equals("r3c")) {
            rulesCount[15]++;
        }
        if (p.rule.equals("r4")) {
            rulesCount[16]++;
        }
        if (p.rule.equals("r4a")) {
            rulesCount[17]++;
        }
        if (p.rule.equals("r4b")) {
            rulesCount[18]++;
        }
        if (p.rule.equals("r4c")) {
            rulesCount[19]++;
        }

    }


    /**
     * TODO mettere degli if "== 0" per non aggiungere roba inutile.
     */
    void createCountText(){

        countText += "Number of times rules have been applied: " + newline;
        if (rulesCount[0] != 0) {
            countText += "Rule 1: " + rulesCount[0] + newline;
        }
        if (rulesCount[1] != 0) {
            countText += "Rule 2: " + rulesCount[1] + newline;
        }
        if (rulesCount[2] != 0){
        countText += "Rule 3: " + rulesCount[2] + newline;
        }
        if (rulesCount[3] != 0) {
            countText += "Rule 3a: " + rulesCount[3] + newline;
        }
        if (rulesCount[4] != 0) {
            countText += "Rule 3b: " + rulesCount[4] + newline;
        }
        if (rulesCount[5] != 0) {
            countText += "Rule 3c: " + rulesCount[5] + newline;
        }
        if (rulesCount[6] != 0) {
            countText += "Rule 4: " + rulesCount[6] + newline;
        }
        if (rulesCount[7] != 0) {
            countText += "Rule 4a: " + rulesCount[7] + newline;
        }
        if (rulesCount[8] != 0) {
            countText += "Rule 4b: " + rulesCount[8] + newline;
        }
        if (rulesCount[9] != 0) {
            countText += "Rule 4c: " + rulesCount[9] + newline;
        }
        if (rulesCount[10] != 0) {
            countText += "Rule r1: " + rulesCount[10] + newline;
        }
        if (rulesCount[11] != 0) {
            countText += "Rule r2: " + rulesCount[11] + newline;
        }
        if (rulesCount[12] != 0) {
            countText += "Rule r3: " + rulesCount[12] + newline;
        }
        if (rulesCount[13] != 0) {
            countText += "Rule r3a: " + rulesCount[13] + newline;
        }
        if (rulesCount[14] != 0) {
            countText += "Rule r3b: " + rulesCount[14] + newline;
        }
        if (rulesCount[15] != 0) {
            countText += "Rule r3c: " + rulesCount[15] + newline;
        }
        if (rulesCount[16] != 0) {
            countText += "Rule r4: " + rulesCount[16] + newline;
        }
        if (rulesCount[17] != 0) {
            countText += "Rule r4a: " + rulesCount[17] + newline;
        }
        if (rulesCount[18] != 0) {
            countText += "Rule r4b: " + rulesCount[18] + newline;
        }
        if (rulesCount[19] != 0) {
            countText += "Rule r4c: " + rulesCount[19] + newline;
        }
    }



    void compose () {
        createCountText();
        this.finalText =
         header + resultingModels + countText + failedTransformations + body;
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
        this.failedTransformations += newline;
        this.failedTransformations += "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! " +
         "! ! ! ! ! ! ! ! ! ! " + "! ! ! ! ! !";
        this.failedTransformations += newline;
        this.failedTransformations += ("There was an error while trying to " +
         "apply Rule: " + parameter.rule + "*" + parameter.aggregateBy + " to" +
          " model " + model.path + newline).toUpperCase();
        this.failedTransformations += newline;
        this.failedTransformations += newline;
        this.failedTransformations += newline;
    }


    void saveToFile (String folderPath) throws IOException {
        // saving the report as a txt
        String filename = folderPath + "Report-" + this.time + ".txt";
        BufferedWriter writer = new BufferedWriter(new PrintWriter(filename));
        System.out.println("I'm saving a report in " + filename);
        writer.write(finalText);
        writer.close();
    }


}
