import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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



}


