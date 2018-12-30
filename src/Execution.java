import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Execution {


    public String input;
    String executionMoment;
    //private String singleFilepath;
    private String folderPath;
    private String outputPath;
    private boolean isAFolder;
    ArrayList<Model> startingModels;
    ArrayList<Parameter> parameters;
    boolean recursive;
    boolean permutations;
    //public ArrayList<Transformation> activities;
    ArrayList<Model> resultingModels;

    //    private ArrayList<Combo> oldCombos; //This remembers the
    // combinations of rules that have already been tried.
    private Report report;


    /**
     * @param originalInput  the original input provided by the user
     * @param isAFolder      true when the user provides a folder, false when
     *                       the user provides a single file
     * @param startingModels the list of models to be modified. Should
     *                       probably be bigger than 0.
     * @param folderPath     the path of the folder in which the starting
     *                       file(s) are located
     * @param permutations   true when permutations are active
     * @param recursive      true when recursive behavior is active
     * @param parameters     the list of rules that can be applied. If
     *                       permutations are turned on, the order doesn't
     *                       matter.
     */
    public Execution (String originalInput, boolean isAFolder,
     ArrayList<Model> startingModels, String folderPath, boolean permutations
     , boolean recursive, ArrayList<Parameter> parameters) throws Exception {
        //From input, let's get parameters and an array of models
        this.input = originalInput;
        this.isAFolder = isAFolder;
        this.parameters = parameters;
        checkThatParamIsNotEmpty();
        this.startingModels = startingModels;
        this.resultingModels = new ArrayList<>();
        //this.singleFilepath
        getDate();
        this.folderPath = folderPath;
        this.outputPath = folderPath + "output " + executionMoment + "/";
        //noinspection ResultOfMethodCallIgnored
        new File(this.outputPath).mkdirs();
        //TODO:
//        Quando fai due esecuzioni nello stesso minuto,  i file della seconda esecuzione vengono aggiunti nella cartella della prima.
//
//        Non voglio aggiungere i secondi al nome della cartella perché diventa brutto,
//
//> Aggiungere un controllo, se la cartella esiste già crearne una con (1) tra parentesi nel nome. Fare in modo che automaticamente se esiste già la cartella 1 viene creata la due e così via.
        this.recursive = recursive;
        this.permutations = permutations;

        //analyzeInput();

        printExecutionStatus();
        if (this.startingModels.size() == 0){
            System.err.println("There are no starting Models to transform");
            System.exit(0);
        }
        this.report = new Report(this);
        decideWhatToDo(); //and do it
        saveResultingModels();
        this.report.addResultingModels(this.resultingModels);
        saveTheReport();

        System.out.println("I've created " + resultingModels.size() + " new " + "Models and saved them in " + outputPath);
        System.exit(0); //the program is terminated
    }


    /**
     * This method checks that the user selected at least one param, and if
     * not then it uses the standard set {1, 2, 3, 4}
     */
    private void checkThatParamIsNotEmpty () {

        if (this.parameters.isEmpty()) {
            this.parameters.add(new Parameter("1"));
            this.parameters.add(new Parameter("2"));
            this.parameters.add(new Parameter("3"));
            this.parameters.add(new Parameter("4"));
        }
    }


    private void getDate () {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmm");
        this.executionMoment = dateFormat.format(new Date());
    }


    private void saveTheReport () throws IOException {
        report.compose();
        report.saveToFile(outputPath);
    }


    private void saveResultingModels () throws TransformerException {

        if (resultingModels.size() == 0) {
            System.out.println("There are no resulting models");
        } else {
            for (Model m : resultingModels) {
                System.out.println();
                // todo see if this extension is ok in all occasions
                m.saveToFile(this.outputPath, ".bpmn.xml");
                System.out.println("Saving the Model: " + m.name + " in " + this.outputPath);
            }
        }
    }


    private void printParams () {

        System.out.println("I found those parameters: ");
        for (Parameter parameter : parameters) {
            if (parameter.aggregateBy == 0) {
                System.out.println("      Param " + parameter.rule);
            }
            if (parameter.aggregateBy != 0) {
                System.out.println("      Param " + parameter.rule + ", " +
                "aggregateBy: " + parameter.aggregateBy);
            }
        }
        System.out.println();
    }


    private void printModels () {

        System.out.println();
        System.out.println("I found those starting models: ");
        for (Model model : startingModels) {
            System.out.println("        " + model.path);
        }
        System.out.println();
    }


    private void printExecutionStatus () {

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("EXECUTION SETTINGS: ");

        if (isAFolder) {
            System.out.println("The user provided a folder: " + folderPath);
        } else {
            System.out.println("The user provided a single file");
        }
        printModels();
        printParams();
        System.out.println("Recursive behavior: " + recursive);
        System.out.println("Permutations: " + permutations);
        System.out.println();
        System.out.println("Output Location: " + outputPath);
        System.out.println();
        System.out.println("-------------------------------");
    }


    /**
     * Decides what to do and does it.
     * What will be done depends on the type of execution
     * (recursive or not, permutations or not).
     */
    private void decideWhatToDo () {

        if (! permutations && ! recursive) {
            //we have a simple order of rules to be applied to the various
            // startingModels in the order decided by the user.
            System.out.println();
            System.out.println("Simple order of rules");
            System.out.println();
            applyNoPermNoRec();
        } else if (permutations && recursive) {
            System.out.println();
            System.out.println("Permutations and Recursive");
            System.out.println();

            for (Model m : startingModels) {
                applyPermRec(m);
            }
        } else //noinspection ConstantConditions
            if (! permutations && recursive) {
                System.out.println();
                System.out.println("NO permutations and Recursive");
                System.out.println();

                for (Model m : startingModels) {
                    applyRecNoPerm(m);
                }
            } else //noinspection ConstantConditions
                if (permutations && ! recursive) {
                    System.out.println();
                    System.out.println("Permutations and NOT recursive");
                    System.out.println();
                    for (Model m : startingModels) {
                        applyPermNoRec(m, this.parameters);
                    }
                }
    }


    //TODO spiegare che salvi i modelli intermedi.
    private void applyRecNoPerm (Model m) {

        String startingName = m.name;
        StringBuilder ruleString = new StringBuilder();
        //boolean out; //initialized to false because if one transf is
        // successful, then the whole sequence is successful.
        //Model end = m;
        for (int i = 0 ; i < parameters.size() ; ) {
            Parameter param = parameters.get(0); //I always take the first one

            Transformation transf = new Transformation(m, param);

            m = transf.resultingModel; //now the model gets updated.
            if (transf.successful) {
                //out = true;
                ruleString.append(param.rule).append(" ");
                this.report.addOutcome(startingName, m.name,
                 ruleString.toString(), true);
                resultingModels.add(m);
            } else {
                parameters.remove(param); //if I couldn't apply the rule this
                // time, I will not try to apply it again.
            }
        }
//        if (out) {
//            resultingModels.add(m); // finally we can add the model to the
// resulting models (the ones that will be saved)
//        }
    }


    //TODO leggi questa cosa qui sotto, è vecchia, probabilmente non la pensi
    // più così:
//    Ripensare al rapporto tra execution, transformation e report.
//
//
//    La execution controlla le trasnformation, ma chi aggiorna il report?
//
//    Se lo faccio alla fine di ogni trasformazione, non so dov'è il
// resulting file, perché quello viene controllato dalla execution.
//
//
//
//    Lo faccio nella execution? Il problema è che la execution gestisce più
// trasformazioni, e io quindi ho più outcome per ogni saved model perché ho
// più trasformazioni.
//    Invece se ho una combo composta di transf, posso salvare un report per
// ogni combo e a ogni combo corrisponde un saved file. Ed è la combo ad
// aggiornare il report, in quel caso posso anche dire dettagliatamente per
// ogni transf se era successfull oppure no.
//    E poi posso dire se la combo in toto era successfull oppure no. (come
// decidere se una combo è successful? Magari se tutte le transf erano
// succesful allora anche la combo è successful).
// TODO forse non c'è bisogno. Decidi.
//
//
//    Ma questa cosa come funziona con le permutations? Lì ogni
// trasformazione dev'essere individuale, in quel caso non ho bisogno di
// combo. Ogni trasformazione dev'essere aggiornata, eventualmente salvata e
// usata per la prossima.
//
    private void applyNoPermNoRec () {

        boolean out = false; //true as long as one transformation is true.
        for (Model m : startingModels) {
            StringBuilder ruleString = new StringBuilder();
            //boolean out = false; //initialized to false because if one
            // transf is successful, then the whole sequence is successful.
            Model end = m; //TODO togli questa cosa che non serve a nulla. In
            // tutti e 4 i metodi "apply"
            for (Parameter param : parameters) {
//                 Transformation transf = new Transformation(model, param,
// this);
                Transformation transf = new Transformation(end, param);
//                if (transf.outcome.equals("successfull")) {

                end = transf.resultingModel; //now the model gets updated.
                if (transf.successful) {
                    ruleString.append(param.rule).append(" "); //we add the
                    // rule in the report only if it was successfull.
                    //TODO magari fare in modo di far vedere la lista
                    // completa di regole che ho provato ad applicare (ovvero
                    // la sequenza originale) e quelle che sono risucito ad
                    // applicare veramente (ovvero questa lista che c'è ora).
                    out = true;
                }

//            if (out) {
//                resultingModels.add(end); // finally we can add the model
// to the resulting models (the ones that will be saved)
//            }

                //Todo spiega questa cosa nella tesi:
                // NOTE that it will be added regardless of the fact that the
                // models is different or not,
                //because in this model the user provides the exact order of
                // rules himself.

            }

            if (out) {
                //we save is as long as at least one transf was successfull
                this.resultingModels.add(end);
            }
            this.report.addOutcome(m.name, end.name, ruleString.toString(),
             out);
        }
    }


    /**
     * TODO TRADURRE IN INGLESE
     * Per ogni modello del mio pool (input: Model, rulePool):
     * Ho pool di strade (regole)
     * Le prendo tutte
     * Genero 4 modelli.
     * Per ogni modello-outcome:
     * se è diverso, lo salvo negli outcome (e nel report) e poi lo do come
     * input al metodo e così via.
     * se è uguale, lo salvo nel report e poi non faccio nulla.
     */
    private void applyPermRec (Model m) {

        String ruleString = "";
        for (Parameter p : parameters) {
            Model end = m;
            ruleString += p.rule;
            Transformation t = new Transformation(end, p);
            end = t.resultingModel;
            if (t.successful) {
                this.resultingModels.add(end); // we save it.

                applyPermRec(end); // let's go further applying the rules on
                // the successful model
            }
            report.addOutcome(m.name, end.name, ruleString, t.successful);
        }
    }




    /** //TODO tradurre in inglese
     * //            Permutations but no recursion:
     * //
     * //            Per ogni modello del mio pool (input: Model, rulePool):
     * //
     * //            Ho pool di strade (regole)
     * //                    Le prendo tutte
     * //            Genero 4 modelli.
     * //                    Per ogni modello-outcome:
     * //            se è diverso, lo salvo negli outcome (e nel report) e
     * poi lo do come input al metodo RIMUOVENDO LA REGOLA CHE L'HA GENERATO
     * DAL POOL CHE DARÒ COME INPUT e così via.
     * //            se è uguale, lo salvo nel report e poi non faccio nulla.
     */
    private void applyPermNoRec (Model m, ArrayList<Parameter> parameters) {

        String ruleString = "";
        for (Parameter p : parameters) {
            Model end = m;
            ruleString += p.rule;
            Transformation t = new Transformation(end, p);
            end = t.resultingModel;
            if (t.successful) {
                this.resultingModels.add(end); // we save it.
                ArrayList<Parameter> newParamList =
                 (ArrayList<Parameter>) parameters.clone();
                newParamList.remove(p);
                applyPermNoRec(end, newParamList); // let's go further
                // applying the rules on the successful models.
            }
            report.addOutcome(m.name, end.name, ruleString, t.successful);
        }
    }

}
