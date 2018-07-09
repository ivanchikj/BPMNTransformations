package bpmn.transformation2;

public class Transformation {

    Model startingModel;
    Model resultingModel;
    Parameter parameter; //the rule it will be trying to apply
    boolean successful; //if the resulting models are different, it's a success, otherwise it's a failure.
    //TODO decide if that will be the way to decide if successful or fail.

    //TODO if the resulting file does not pass the BPMN test, then it's not a success it's a fail. Thus the program will fail
    //gracefully, but that will be written in the report differently.

    public Transformation(Model startingModel, Parameter parameter) throws Exception {

	this.startingModel = startingModel;
	this.parameter = parameter;
	boolean successful = false; //default value
	applyTheRule();

    }

    /**
     * TODO decide if there's a better way than having this long list of 'ifs'
     * @return
     * @throws Exception
     */
    private void applyTheRule() throws Exception {
	if (parameter.equals("1")) {
	    resultingModel = Rule1.applyRule(startingModel);
	}

	if (parameter.equals("1")) {
	    resultingModel = Rule2.applyRule(startingModel);
	}


	if (parameter.equals("2")) {

	}

	if (parameter.equals("3")) {

	}

	if (parameter.equals("4")) {

	}

	if (parameter.equals("R1")) {

	}

	if (parameter.equals("R2")) {

	}

	if (parameter.equals("R3")) {

	}

	if (parameter.equals("R4")) {

	}


	boolean valid = true; //TODO what if the resulting file is not valid? Insert the test here.
	//if the models are different, it means that the transformation was successful
	boolean different = Execution.modelsAreDifferent(startingModel, resultingModel);

	if (different && valid) {
	    successful = true;
	}

    }

}
