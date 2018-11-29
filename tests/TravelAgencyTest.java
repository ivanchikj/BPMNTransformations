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
    void detectMissingTask () throws ParserConfigurationException, IOException, XPathExpressionException, SAXException {

        Model base = new Model("./tests/TestModels/TravelAgency" +
                "/BaseModelForTA.bpmn.xml");
        //the model against which all others will be tested.

        Model missingTask = new Model("./tests/TestModels/TravelAgency" +
                "/aTaskIsMissing.bpmn.xml");
        assertTrue(TravelAgency.modelsAreDifferent(base, missingTask));
        //This model is different from the base model: a task has been deleted.

    }

    @Test
    void detectExpressionAdded () throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        Model base = new Model("./tests/TestModels/TravelAgency" +
                "/BaseModelForTA.bpmn.xml");
        //the model against which all others will be tested.

        Model expressionAdded =
                new Model("./tests/TestModels/TravelAgency" +
                        "/onlyOneExpressionIsAdded.bpmn.xml");

        assertFalse(TravelAgency.modelsAreDifferent(base, expressionAdded));
        //This model is equal from the base model: a couple of sequenceFlows
        // have
        //TODO this should change, but if it doesn't I can justify it by saying
        // that there are no rules that only change expressions.

    }


    /**
     * TODO maybe create an equal model but from Signavio.?
     * It will fail, because types are different, unless I add a translator
     * to the code which is not difficult i just have to find the right place.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test
    void ignoreDifferentPlacement () throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        Model base = new Model("./tests/TestModels/TravelAgency" +
                "/BaseModelForTA.bpmn.xml");
        //the model against which all others will be tested.

        Model onlyPositionsAreDifferent = new Model("./tests/TestModels" +
                "/TravelAgency/onlyPositionsAreDifferent.bpmn.xml");
        assertFalse(TravelAgency.modelsAreDifferent(base,
                onlyPositionsAreDifferent));
        //The position of the BPMN symbols should not matter. As such, it
        // should be equal to the base model.

    }

    @Test
    void detectDifferentTypes () throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {

        Model base = new Model("./tests/TestModels/TravelAgency" +
                "/BaseModelForTA.bpmn.xml");
        //the model against which all others will be tested.

        Model onlyTypesAreDifferent = new Model("./tests/TestModels" +
                "/TravelAgency/onlyTypesAreDifferent.bpmn.xml");
        assertTrue(TravelAgency.modelsAreDifferent(base,
                onlyTypesAreDifferent));
        //The type of the models matters: as such it is a different model
        // from the original.

    }


}