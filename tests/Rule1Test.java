import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rule1Test {


    /**
     * The classic Rule1 example.
     */
    @Test
    void classicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule1" +
        "/Rule1Classic.bpmn.xml");

        Model expected =
         new Model("./tests/TestModels/Rule1/Rule1Classic1" + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule1.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));

    }


    @Test
    void twoLayers () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule1" +
        "/Rule1DoubleLayer.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule1" +
         "/Rule1DoubleLayer1" + ".bpmn.xml");

        Model result = startingModel.cloneModel();
        Rule1.apply(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


}