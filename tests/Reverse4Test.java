import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Reverse4Test {


    @Test
    void a () {

    }


    @Test
    void b () {

    }


    /**
     * Should correctly avoid one similarly formed structure whose task does
     * not respect the condition "all outgoing flows have a condition".
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    @Test
    void cTest () throws ParserConfigurationException, SAXException, IOException
    , XPathExpressionException {
        Model startingModel = new Model("./tests/TestModels/Rule4c" +
                "/Reverse4cTest.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule4c" +
                "/Reverse4cTestr4c.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse4.c(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void all () {

    }


}