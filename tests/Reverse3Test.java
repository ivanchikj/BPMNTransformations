import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Reverse3Test {


    @Test
    void aclassic () {
    //TODO fare in modo che in questo modello ci sia sia un caso applicabile,
    // che uno dove le condizioni uscenti non sono tutte tautologie.
    }

    void abigger(){
    //TODO fare un modello dove c'è roba dentro il construct
    //usalo anche come esempio nella tesi, quando spieghi cos'è il first
    // meeting point
    }

    void aOneInsideTheOther(){
    //TODO fare un modello dove c'è un construct dentro il construct.
    //Usalo anche questo nella tesi.
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

    }


}