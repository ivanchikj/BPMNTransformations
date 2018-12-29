import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Reverse2Test {


    /**
     * The classic Reverse2 example.
     * Here the user doesn't provide an AggregateBy parameter so aggregateBy
     * becomes 2.
     */
    @Test
    void twoByTwo () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
        "/Rule2DoubleLayer2.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
        "/Rule2DoubleLayer2r2.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse2.apply(result, 0);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void twoByTwoParallel () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerParallel2.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerParallel2r2.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse2.apply(result, 0);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void twoByTwoInclusive () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerInclusive2.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
                "/Rule2DoubleLayerInclusive2r2.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse2.apply(result, 0);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void threeByThree () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule2" +
        "/Rule2DoubleLayer2.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule2" +
        "/Rule2DoubleLayer2r2*3.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse2.apply(result, 3);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


}