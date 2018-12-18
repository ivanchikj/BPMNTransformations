import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rule4Test {


    @Test
    void aClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule4a" +
        "/Rule4aClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule4a/Rule4aClassic4a"
         + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.a(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    /**
     * In this test, nothing should be transformed, as the parallel gateway
     * is not preceded by a task but from the start element directly.
     */
    @Test
    void aNotApplicable() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        Model startingModel = new Model(
         "./tests/TestModels/Rule4a/Rule4aNOTAPPLICABLE.bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.a(result);

        assertFalse(TravelAgency.modelsAreDifferent(result,startingModel));
    }


    @Test
    void bClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule4b" +
        "/Rule4bClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule4b/Rule4bClassic4b"
         + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.b(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    /**
     * In this test, nothing should be transformed, as the exclusive gateway
     * is not followed by a task but from the end element directly.
     */
    @Test
    void bNotApplicable() throws ParserConfigurationException, SAXException,
     IOException, XPathExpressionException {

        Model startingModel = new Model(
                "./tests/TestModels/Rule4b/Rule4bNOTAPPLICABLE.bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.b(result);

        assertFalse(TravelAgency.modelsAreDifferent(result,startingModel));
    }


    @Test
    void cClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule4c" +
                "/Rule4cClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule4c/Rule4cClassic4c"
                + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.c(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    /**
     * In this test, nothing should be transformed, as the inclusive gateway
     * is not preceded by a task but from the start element directly.
     */
    @Test
    void cNotApplicable() throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        Model startingModel = new Model(
                "./tests/TestModels/Rule4c/Rule4cNOTAPPLICABLE.bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule4.c(result);

        assertFalse(TravelAgency.modelsAreDifferent(result,startingModel));
    }




}


