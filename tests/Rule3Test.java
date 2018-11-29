import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rule3Test {


    @Test
    void aClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3a" +
        "/Rule3aClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3a/Rule3aClassic3a"
         + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule3.a(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void bClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3b" +
                "/Rule3bClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3b/Rule3bClassic3b"
                + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule3.b(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    @Test
    void c () {

    }


    @Test
    void all () {

    }


}