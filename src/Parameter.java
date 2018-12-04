import java.util.Arrays;


public class Parameter {


    public String rule;
    public int aggregateBy;
    static String[] validParameters = {//@formatter:off
                    "1",
                    "2",
                    "3",
                    "3a", "3b", "3c",
                    "4",
                    "4a", "4b", "4c",
                    "r1",
                    "r2",
                    "r3",
                    "r3a", "r3b", "r3c",
                    "r4",
                    "r4a", "r4b", "r4c"
            }; //@formatter:on


    Parameter (String parameter) {

        if (isValid(parameter)) {
            this.rule = parameter;
        }
    }


    //NOTE that
    //if the AggregateBy parameter is present in a rule that doesn't use it,
    //it will simply be ignored.
    Parameter (String parameter, int aggregateBy) {

        if (isValid(parameter)) {
            this.rule = parameter;
            this.aggregateBy = aggregateBy;
        }
    }


    private boolean isValid (String parameter) {

        if (Arrays.asList(validParameters).contains(parameter)) {
            return true;
        } else {
            System.err.println(parameter + " is not a valid parameter.");
            System.exit(0);
            return false; // never reached but mandatory
        }
    }


}
