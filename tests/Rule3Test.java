import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
    void cClassicCase () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3c" +
        "/Rule3cClassic.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3c/Rule3cClassic3c"
         + ".bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule3.c(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }

    @Test
    void createInverseCondition(){
        String c1 = "a > 0";
        String c2 = "b > 0";
        String c3 = "c > 0 && d < 9";
        ArrayList<String> l = new ArrayList<>();
        l.add(c1);
        l.add(c2);
        l.add(c3);


        String r = Rule3.createInverseCondition(l);
        System.out.println(r);
        String correctAnswer = "!(a > 0) && !(b > 0) && !(c > 0 && d < 9)";
        assertEquals(r, correctAnswer);

    }


    /**
     * This model contains a rule3c construct with 3 ways, one which contains
     * further splits and one which contains another rule3c inside.
     *
     * @throws Exception
     */
    @Test
    void c3WayOneInsideAnother () throws Exception {

        Model startingModel = new Model("./tests/TestModels/Rule3c" +
        "/Rule3c3WayOneInsideAnother.bpmn.xml");

        Model expected = new Model("./tests/TestModels/Rule3c" +
        "/Rule3c3WayOneInsideAnother3c.bpmn.xml");

        Model result = startingModel.cloneModel();

        Rule3.c(result);

        assertFalse(TravelAgency.modelsAreDifferent(result, expected));
        assertTrue(TravelAgency.modelsAreDifferent(result, startingModel));
    }


    @Test
    void all () {

    }


}