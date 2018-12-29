import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Rule2Test {


    @Test
    void classicCase () throws XPathExpressionException,
     ParserConfigurationException, SAXException, IOException {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
        "/Rule2Classic.bpmn.xml");

        Model expected =
         new Model("./tests/TestModels/Rule2/Rule2Classic2" + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule2.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void twoLayers () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayer.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayer2" + ".bpmn.xml");

        Model result = startingModel.cloneModel();
        Rule2.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void twoLayersParallel () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerParallel.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerParallel2" + ".bpmn.xml");

        Model result = startingModel.cloneModel();
        Rule2.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void twoLayersInclusive () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerInclusive.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerInclusive2" + ".bpmn.xml");

        Model result = startingModel.cloneModel();
        Rule2.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


}