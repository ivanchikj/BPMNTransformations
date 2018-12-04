import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Reverse3Test {


    /**
     * Note that in this model there is one applicable inclusive gateway and
     * another one that is not applicable because not all his
     * outgoing flows are tautologies.
     *
     * @throws Exception
     */
    @Test
    void aclassic () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3a" +
        "/Reverse3aClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3a" +
        "/Reverse3aClassicr3a.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.a(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    /**
     * TODO usalo anche come esempio nella tesi, quando spieghi cos'è il
     * first
     * // meeting point
     * <p>
     * In this model there are more than 3 outgoing flows from the
     * inclusive
     * gateway, and also in the middle of the construct there is a
     * parallel
     * split in one instance and an exclusive split in another.
     *
     * @throws Exception
     */
    @Test
    void aThingsInTheMiddle () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3a" +
        "/ThingsInTheMiddle.bpmn.xml");

        Model expected =
         new Model("/Users/rubenfolini/BPMNTransformations" + "/tests" +
         "/TestModels/Rule3a/ThingsInTheMiddler3a.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.a(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    /**
     * This model has a reverse3c construct inside another.
     */
    @Test
    void aOneInsideTheOther () throws Exception {

        //TODO Usalo anche questo nella tesi.

        Model startingModel = new Model("./tests/TestModels/Rule3a" +
        "/InclusiveInTheMiddle.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3a" +
        "/InclusiveInTheMiddler3a.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.a(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void b () {

    }


    @Test
    void c () {

    }


    @Test
    void applyRule () {

    }


    @Test
    void isItATautology () {

        assertTrue(Reverse3.isItATautology("1 == 1"));
        assertFalse(Reverse3.isItATautology("1 == 2"));
        //assertTrue(Reverse3.isItATautology("a == a"));
        //this doesn't work with the engine that I use now.
        assertFalse(Reverse3.isItATautology("anyWord"));
        assertFalse(Reverse3.isItATautology("4"));
        assertFalse(Reverse3.isItATautology("4 > 5"));
        assertTrue(Reverse3.isItATautology("4 < 5"));
        assertTrue(Reverse3.isItATautology("TRUE"));
        assertTrue(Reverse3.isItATautology("true"));
        assertFalse(Reverse3.isItATautology("FALSE"));
        assertFalse(Reverse3.isItATautology("FaLse"));
    }


}