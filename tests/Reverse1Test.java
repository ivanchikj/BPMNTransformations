import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Reverse1Test {


    /**
     * The classic Reverse1 example.
     * Here the user doesn't provide an AggregateBy parameter so aggregateBy
     * becomes 1.
     */
    @Test
    void twoByTwo () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule1" +
        "/Rule1DoubleLayer1.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule1" +
         "/Rule1DoubleLayer1r1*2" + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse1.applyRule(result, 0);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void threeByThree () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule1" +
        "/Rule1DoubleLayer1.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule1" +
        "/Rule1DoubleLayer1r1*3.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse1.applyRule(result, 3);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


}