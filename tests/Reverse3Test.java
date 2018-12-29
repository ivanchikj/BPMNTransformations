import org.junit.jupiter.api.Test;

import java.io.IOException;

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
    void aClassic () throws Exception {

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
         new Model("./tests" +
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
    void bClassic () throws Exception {
        Model startingModel = new Model("./tests/TestModels/Rule3b" +
                "/Reverse3bClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3b" +
                "/Reverse3bClassicr3b.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.b(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    @Test
    void bThingsInTheMiddle() throws Exception {
        Model startingModel = new Model("./tests/TestModels/Rule3b" +
                "/ThingsInTheMiddle.bpmn.xml");

        Model expected =
                new Model("./tests" +
                        "/TestModels/Rule3b/ThingsInTheMiddler3b.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.b(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    /**
     * This model has a reverse3b construct inside another.
     */
    @Test
    void bOneInsideTheOther () throws Exception {

        //TODO Usalo anche questo nella tesi.

        Model startingModel = new Model("./tests/TestModels/Rule3b" +
                "/InclusiveInTheMiddle.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3b" +
                "/InclusiveInTheMiddler3b.bpmn.xml");

        Model result = startingModel.cloneModel();

        Reverse3.b(result);
        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    @Test
    void c () {

    }


    @Test
    void applyRule () {

    }


    @Test
    void areAlwaysTrue () {

        assertTrue(Reverse3.areAlwaysTrue("1 == 1"));
        assertFalse(Reverse3.areAlwaysTrue("1 == 2"));
        assertTrue(Reverse3.areAlwaysTrue("'a' == 'a'"));
        //this works if words are surrounded by 'quotation marks'
        assertFalse(Reverse3.areAlwaysTrue("a == a"));
        assertFalse(Reverse3.areAlwaysTrue("'a' == 'b'"));
        assertFalse(Reverse3.areAlwaysTrue("anyWord"));
        assertFalse(Reverse3.areAlwaysTrue("4"));
        assertFalse(Reverse3.areAlwaysTrue("4 > 5"));
        assertTrue(Reverse3.areAlwaysTrue("4 < 5"));
        assertTrue(Reverse3.areAlwaysTrue("TRUE"));
        assertTrue(Reverse3.areAlwaysTrue("true"));
        assertFalse(Reverse3.areAlwaysTrue("FALSE"));
        assertFalse(Reverse3.areAlwaysTrue("FaLse"));
    }


    /**
     * The WAappid.txt needs to be present inside the BPMNTransformation
     * folder for this test to pass, and the PC needs to be connected to the
     * internet.
     */
    @Test
    void areMutuallyExclusive() throws IOException {

        assertTrue(Reverse3.areMutuallyExclusive("a > 0","a < 0"));
        assertTrue(Reverse3.areMutuallyExclusive("A >= 100", "A < 50"));
//      assertTrue(Reverse3.areMutuallyExclusive("Value >= 100",
// "Value < 50"));
//Unfortunately words don't work as well as variables.
        assertTrue(Reverse3.areMutuallyExclusive("a == 2", "a == 3"));
        assertFalse(Reverse3.areMutuallyExclusive("1 == 2","2 == 3"));
        assertFalse(Reverse3.areMutuallyExclusive("1 == 2","4 > 0"));
        assertFalse(Reverse3.areMutuallyExclusive("",""));
        assertFalse(Reverse3.areMutuallyExclusive("a > 0",""));
        assertFalse(Reverse3.areMutuallyExclusive("a > 0","b < 0"));
        assertFalse(Reverse3.areMutuallyExclusive("a == 1",""));



    }


}