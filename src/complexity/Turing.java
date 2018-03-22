package complexity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A Non-Deterministic Multi-Tape Turing machine is like a Turing machine but it has several tapes and its set of rules may prescribe more than one action to be performed for any given situation
 * @author Mirko Alicastro
 * @link https://github.com/mirkoalicastro/turing
 * @version 1.0
 */

public class Turing {

    /**
     * Output class is used to handle the (potentially) multiple outputs of the Turing machine.
     * Each Output object contains:
     * <ul>
     * <li>state: the state for which the Turing machine stopped (YES, NO, HALT);</li>
     * <li>tapes: the content of all the tapes;</li>
     * <li>heads: the position of all the heads on the tapes</li>
     * </ul>
     */
    public static class Output {
        /**
         * It represents the state for which the Turing machine stopped (YES, NO, HALT)
         */
        public final FINAL_STATE state;
        /**
         * The array as length equals to the number of tapes of the Turing machine that generated this Output object.
         * It represents a snapshot of all the tapes when the Turing machine stopped
         */
        public final String[] tapes;
        /**
         * The array as length equals to the number of tapes of the Turing machine that generated this Output object.
         * It contains the position of all the heads on the tapes
         */
        public final int[] heads;
        private Output(FINAL_STATE state, String[] tapes, int[] heads) {
            this.state = state;
            this.tapes = tapes;
            this.heads = heads;
        }
        @Override
        public String toString() {
             return "{\n\tState: " + state + ",\n\t" + Arrays.toString(tapes) + "\n}";
        }
    }

    /**
     * Enumeration of the three different kinds of final states of the Turing machine: YES, NO, HALT
     */
    public static enum FINAL_STATE {
        YES, NO, HALT;
        @Override
        public String toString() {
            return name().substring(0,1);
        }
    }
    private static final char rightDirection = 'r', leftDirection = 'l', stopDirection = '-';
    private static final String initialState = "s";
    private final String input;
    private final int tapesNumber;
    private final List<Output> output = new ArrayList<>();
    private final Map<String, Map<String, Map<String,ArrayList<String>>>> relations = new HashMap<>();

    /**
     * Create a new Turing machine which implements the program specified by the content of the file <i>filePath</i>
     * @param filePath file path of the program of the Turing machine
     * @throws IOException
     * @throws TuringException
     */
    
    public Turing(String filePath) throws IOException, TuringException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line, inp = "";
        int tapesNum = 0;
        boolean first = true;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if(first) {
                inp = line;
                first = false;
                continue;
            }
            if(line.isEmpty())
                continue;
            String[] split = line.split(";");
            if(split.length != 3)
                throw new TuringException("Malformed input: check the following line: " + line);                
            String state = split[0].trim();
            String oldConfig = split[1].trim();
            if(!oldConfig.substring(0,1).equals("(") || !oldConfig.substring(oldConfig.length()-1).equals(")"))
                throw new TuringException("Malformed input: the following configuration must be enclosed between two brackets: " + line);
            oldConfig = oldConfig.substring(1, oldConfig.length()-1).replaceAll("\\.","").replaceAll(",", "").replaceAll(" ", "");
            if(tapesNum == 0)
                tapesNum = oldConfig.length();
            else if(tapesNum != oldConfig.length())
                throw new TuringException("Malformed input: the number of tapes must be the same in all the instructions");
            Map<String, Map<String, ArrayList<String>>> rel;
            Map<String, ArrayList<String>> trans;
            if((rel=relations.get(state)) == null) {
                rel = new HashMap<>();
                trans = new HashMap<>();
            } else if((trans = rel.get(oldConfig)) == null)
                trans = new HashMap<>();
            String newConfig = split[2].trim();
            if(!newConfig.substring(0,1).equals("(") || !newConfig.substring(newConfig.length()-1).equals(")"))
                throw new TuringException("Malformed input: the following relation must be enclosed between two brackets: " + line);
            String[] c = newConfig.substring(1, newConfig.length()-1).split(",");
            String newState = c[0];
            newConfig = "";
            for(int i=1; i<c.length; i++)
                newConfig += c[i].trim();
            if(newConfig.length() != tapesNum*2)
                throw new TuringException("Malformed input: check the relation of the following line: " + line);
            ArrayList<String> allConfig;
            if((allConfig=trans.get(newState)) == null)
                allConfig = new ArrayList<>();
            allConfig.add(newConfig);
            trans.put(newState, allConfig);
            rel.put(oldConfig, trans);
            relations.put(state, rel);
        }
        this.input = inp;
        this.tapesNumber = tapesNum;
    }

    /**
     * Returns the input defined by the program of the Turing machine
     * @return the input defined by the program of the Turing machine
     */

    public String getInput() {
        return input;
    }

    /**
     * Returns the number of tapes in this Turing machine
     * @return the number of tapes of this Turing machine
     */
    
    public int getTapesNumber() {
        return tapesNumber;
    }
    private static String encodeCurrentConfiguration(String s) {
        String ret = "(";
        char[] c = s.toCharArray();
        for(int i=0; i<c.length; i++)
            ret += c[i] + ", ";
        return ret.substring(0, ret.length()-2) + ")";
    }
    /**
     * Run a simulation
     * @return a list of all the outputs of the simulation
     * @throws TuringException
     */

    public List<Output> run() throws TuringException {
        return run(input);
    }

    /**
     * Run a simulation with a customized input 
     * @param input customized input without initial symbol
     * @return a list of all the outputs of the simulation
     * @throws TuringException
     */
    
    public List<Output> run(String input) throws TuringException {
        String[] tapes = new String[tapesNumber];
        int[] heads = new int[tapesNumber];
        for(int i=0; i<tapes.length; i++) {
            tapes[i] = ">" + (i==0 ? input : "");
            heads[i] = 0;
        }
        run(tapes, heads, initialState);
        List<Output> tmp = new ArrayList<>();
        tmp.addAll(output);
        output.clear();
        return tmp;
    }
    private void run(String[] tapes, int[] heads, String state) throws TuringException {
        FINAL_STATE retState = null;
        if(state.equals(FINAL_STATE.YES.toString()))
            retState = FINAL_STATE.YES;
        else if(state.equals(FINAL_STATE.NO.toString()))
            retState = FINAL_STATE.NO;
        else if(state.equals(FINAL_STATE.HALT.toString()))
            retState = FINAL_STATE.HALT;
        if(retState != null) {
            output.add(new Output(retState, tapes, heads));
            return;
        }
        Map<String, Map<String, ArrayList<String>>> config;
        Map<String, ArrayList<String>> go;
        if((config=relations.get(state)) == null)
            throw new TuringException("Cannot find state " + state);
        char[] curr = new char[tapes.length];
        String conf = "";
        for(int i=0; i<tapes.length; i++) {
            if(tapes[i].length() < heads[i])
                tapes[i] += "_";
            curr[i] = tapes[i].charAt(heads[i]);
        }
        for(char c: curr)
            conf += c + "";
        if((go=config.get(conf)) == null)
            throw new TuringException("It is not defined what to do from state " + state + " with configuration " + encodeCurrentConfiguration(conf));
        for(String newState: go.keySet()) {
            Iterator<String> it = go.get(newState).iterator();
            while(it.hasNext()) {
                String newConfig = it.next();
                String[] newTapes = tapes;
                int[] newHeads = heads;
                if(it.hasNext()) {
                    newTapes = new String[tapesNumber];
                    System.arraycopy(tapes, 0, newTapes, 0, tapesNumber);
                    newHeads = new int[tapesNumber];
                    System.arraycopy(heads, 0, newHeads, 0, tapesNumber);
                }
                char[] tmp = newConfig.toCharArray();
                for(int m=0, j=0; j<tmp.length; j+=2, m++) {
                    char dir = tmp[j+1];
                    newTapes[m] = newTapes[m].substring(0,newHeads[m])+tmp[j]+newTapes[m].substring(newHeads[m]+1);
                    if(dir == rightDirection) {
                        newHeads[m]++;
                        if(newHeads[m] == newTapes[m].length())
                            newTapes[m] += "_";
                    } else if(dir == leftDirection) {
                        newHeads[m]--;
                        if(newHeads[m] == -1)
                            throw new TuringException("Cannot go before the universe!");
                    } else if(dir != stopDirection) {
                        throw new TuringException("Cannot understand the following direction: " + dir);
                    }
                }
                run(newTapes, newHeads, newState);
            }
        }
    }
    
    /**
        This exception will be thrown by:
        * <ul>
        * <li>Turing constructor, if the input file is a malformed input;</li>
        * <li>Turing run methods, if the program has an error</li>
        * </ul>
     */
    public final class TuringException extends RuntimeException {
        public TuringException(String s) {
            super(s);
        }
    }
    
}
