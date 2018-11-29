import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TravelAgencyTest {


    @Test
    void getMandatoryDeepSuccessors () {

    }


    @Test
    void printMandatoryDeepSuccessors () {

    }


    @Test
    void printNumberOfPaths () {

    }


    @Test
    void modelsAreDifferent () throws ParserConfigurationException,
     SAXException, IOException, XPathExpressionException {

        Model base =
         new Model("/Users/rubenfolini/BPMNTransformations/tests/TestModels/TravelAgency/BaseModelForTA.bpmn.xml");
        //the model against which all others will be tested.

        Model missingTask = new Model("/Users/rubenfolini/BPMNTransformations/tests/TestModels/TravelAgency/aTaskIsMissing.bpmn.xml");
        assertTrue(TravelAgency.modelsAreDifferent(base, missingTask));
        //This model is different from the base model: a task has been deleted.

        Model expressionAdded = new Model("/Users/rubenfolini/BPMNTransformations/tests/TestModels/TravelAgency/onlyOneExpressionIsAdded.bpmn.xml");

        assertFalse(TravelAgency.modelsAreDifferent(base, expressionAdded));
        //This model is equal from the base model: a couple of sequenceFlows
        // have
        //TODO this should change, but if it doesn't I can justify it by saying
        // that there are no rules that only change expressions.

        Model onlyPositionsAreDifferent =
         new Model("/Users/rubenfolini/BPMNTransformations/tests/TestModels/TravelAgency/onlyPositionsAreDifferent.bpmn.xml");
        assertFalse(TravelAgency.modelsAreDifferent(base,
         onlyPositionsAreDifferent));
        //The position of the BPMN symbols should not matter. As such, it
        // should be equal to the base model.

        Model onlyTypesAreDifferent = new Model("/Users/rubenfolini/BPMNTransformations/tests/TestModels/TravelAgency/onlyTypesAreDifferent.bpmn.xml");
        assertTrue(TravelAgency.modelsAreDifferent(base,
         onlyTypesAreDifferent));
        //The type of the models matters: as such it is a different model
        // from the original.

    }


}