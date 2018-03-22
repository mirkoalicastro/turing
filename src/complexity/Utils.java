package complexity;

import java.util.List;

/**
 * A class which could contain some useful methods
 * @author Mirko Alicastro
 * @link https://github.com/mirkoalicastro/turing
 * @version 1.0
 */
public class Utils {

    /**
     * This method checks if at least one Output object has the final state setted to <i>state</i> parameter
     * @param outputs list of Output objects of an execution of Turing machine
     * @param state final state which must be searched among the final states of Output objects
     * @return true if at least one Output object has the final state setted to <i>state</i>, false otherwise
     */
    public static boolean containsAtLeast(List<Turing.Output> outputs, Turing.FINAL_STATE state) {
        return outputs.stream().anyMatch((output) -> (output.state == state));
    }
}