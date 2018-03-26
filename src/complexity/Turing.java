package complexity;

import complexity.datastructure.Bulk;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Non-Deterministic Multi-Tape Turing machine is like a Turing machine but it has several tapes and its set of rules may prescribe more than one action to be performed for any given situation
 * 
 * @author Mirko Alicastro
 * @link https://github.com/mirkoalicastro/turing
 * @version 1.2
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
         * It is the last state of the Turing machine (YES, NO, HALT)
         */
        public final FINAL_STATE state;
        /**
         * It is a snapshot of all the tapes when the Turing machine stopped.
         * The array has length equals to the number of tapes of the Turing machine that generated this Output object
         */
        public final String[] tapes;
        /**
         * It contains the position of all the heads on the tapes when the Turing machine stopped.
         * The array has length equals to the number of tapes of the Turing machine that generated this Output object
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
    private static final char blankSymbol = '_', initialSymbol = '>';
    private static final char rightDirection = 'R', leftDirection = 'L', stopDirection = '-';
    private static final String initialState = "s";
    private final String input;
    private final int tapesNumber;
    private final Map<String, Map<String, Map<String,List<String>>>> relations = new HashMap<>();

    /**
     * Creates a new Turing machine which implements the program specified by the content of the file <i>filePath</i>
     * @param filePath file path of the program of the Turing machine
     * @throws IOException
     * @throws TuringException
     */
    
    public Turing(String filePath) throws IOException, TuringException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line, inp = "";
        int tapesNum = -1;
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
            if(tapesNum == -1)
                tapesNum = oldConfig.length();
            else if(tapesNum != oldConfig.length())
                throw new TuringException("Malformed input: the number of tapes must be the same in all the instructions");
            Map<String, Map<String, List<String>>> rel;
            Map<String, List<String>> trans;
            if((rel=relations.get(state)) == null) {
                rel = new HashMap<>();
                trans = new HashMap<>();
            } else if((trans = rel.get(oldConfig)) == null)
                trans = new HashMap<>();
            String newConfig = split[2].trim();
            if(!newConfig.substring(0,1).equals("(") || !newConfig.substring(newConfig.length()-1).equals(")"))
                throw new TuringException("Malformed input: the following relation must be enclosed between two brackets: " + line);
            split = newConfig.substring(1, newConfig.length()-1).split(",");
            String newState = split[0];
            newConfig = "";
            for(int i=1; i<split.length; i++)
                newConfig += split[i].trim();
            if(newConfig.length() != tapesNum*2)
                throw new TuringException("Malformed input: check the relation of the following line: " + line);
            List<String> allConfig;
            if((allConfig=trans.get(newState)) == null)
                allConfig = new ArrayList<>();
            allConfig.add(newConfig);
            trans.put(newState, allConfig);
            rel.put(oldConfig, trans);
            relations.put(state, rel);
        }
        this.input = inp;
        if(tapesNum < 1)
            throw new TuringException("Every Turing machine must have at least 1 tape");
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
     * Returns the number of tapes of this Turing machine
     * @return the number of tapes of this Turing machine
     */
    
    public int getTapesNumber() {
        return tapesNumber;
    }
    
    /**
     * Generates the program of the Turing machine on-the-fly and returns it.
     * @return the program of the Turing machine
     */
    public String getProgram() {
        String[] states = new String[relations.size()];
        relations.keySet().toArray(states);
        Comparator<String> charComparator = (String a,String b) -> {
            char[] x = a.toCharArray();
            char[] y = b.toCharArray();
            for(int i=0; i<Integer.min(x.length, y.length); i++) {
                if(x[i] == initialSymbol)
                    return -1;
                if(y[i] == initialSymbol)
                    return 1;
                if(x[i] == blankSymbol)
                    return -1;
                if(y[i] == blankSymbol)
                    return 1;
                int tmp = Character.compare(x[i], y[i]);
                if(tmp != 0)
                    return tmp;
            }
            return 0;
        };
        Comparator<String> statesComparator = (a,b) -> {
            if(a.equals(initialState))
                return -1;
            if(b.equals(initialState))
                return 1;
            return a.compareTo(b);
        };
        Arrays.sort(states,statesComparator);
        String ret = "";
        for(String state: states) {
            Map<String, Map<String, List<String>>> trans = relations.get(state);
            String[] conf = new String[trans.keySet().size()];
            trans.keySet().toArray(conf);
            Arrays.sort(conf, charComparator);
            for(String configuration: conf) {
                String hConf = configuration.replaceAll("", ", ");
                hConf = hConf.substring(2, hConf.length()-2);
                Map<String, List<String>> rel = trans.get(configuration);
                String[] newStates = new String[rel.size()];
                rel.keySet().toArray(newStates);
                Arrays.sort(newStates, statesComparator);
                for(String newState: newStates) {
                    List<String> newConfigs = rel.get(newState);
                    newConfigs.sort(charComparator);
                    for(String newConfig: newConfigs) {
                        String hRel = newConfig.replaceAll("", ", ");
                        hRel = hRel.substring(2, hRel.length()-2);
                        ret += state + "; (" + hConf  + "); (" + newState + "; " + hRel + ")\n";                                        
                    }
                }
            }
        }
        return ret.substring(0, ret.length()-1);
    }
    
    private static String encodeCurrentConfiguration(String s) {
        String ret = "(";
        char[] c = s.toCharArray();
        for(int i=0; i<c.length; i++)
            ret += c[i] + ", ";
        return ret.substring(0, ret.length()-2) + ")";
    }
    
    /**
     * Runs a complete simulation
     * @return a list of all the outputs of the simulation
     * @throws TuringException
     */

    public List<Output> run() throws TuringException {
        return run(input);
    }

    /**
     * Runs a complete simulation with a customized input 
     * @param input customized input without initial symbol
     * @return a list of all the outputs of the simulation
     * @throws TuringException
     */
    
    public List<Output> run(String input) throws TuringException {
        return run(input, false);
    }
    
    /**
     * Runs an optimized or a complete simulation
     * @param optimize true if don't want to re-execute branches, false otherwise 
     * @return a list of all the outputs of the simulation (if <i>optimize</i> is true then the list will contains only the executed branches outputs)
     * @throws TuringException
     */
    
    public List<Output> run(boolean optimize) throws TuringException {
        return run(input, optimize);
    }
    
    /**
     * Runs an optimized or a complete simulation with a customized input
     * @param input customized input without initial symbol
     * @param optimize true if don't want to re-execute branches, false otherwise 
     * @return a list of all the outputs of the simulation (if <i>optimize</i> is true then the list will contains only the executed branches outputs)
     * @throws TuringException
     */
    
    public List<Output> run(String input, boolean optimize) throws TuringException {
        String[] tapes = new String[tapesNumber];
        int[] heads = new int[tapesNumber];
        for(int i=0; i<tapes.length; i++) {
            tapes[i] = initialSymbol + (i==0 ? input : "");
            heads[i] = 0;
        }
        List<Output> output;
        Set<Bulk> set;
        run(tapes, heads, initialState, (output=new ArrayList<>()), optimize ? (set=new HashSet<>()) : (set=null));
        if(set != null)
            set.clear();
        return output;
    }
    
    private void run(String[] tapes, int[] heads, String state, List<Output> output, Set<Bulk> yetExecuted) throws TuringException {
        if(yetExecuted != null) {
            Bulk b;
            if(yetExecuted.contains((b=new Bulk(state, tapes, heads))))
                return;
            yetExecuted.add(b);
        }
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
        Map<String, Map<String, List<String>>> config;
        Map<String, List<String>> go;
        if((config=relations.get(state)) == null)
            throw new TuringException("Cannot find state " + state);
        char[] curr = new char[tapes.length];
        String conf = "";
        for(int i=0; i<tapes.length; i++) {
            if(tapes[i].length() <= heads[i])
                tapes[i] += blankSymbol;
            curr[i] = tapes[i].charAt(heads[i]);
        }
        for(char c: curr)
            conf += c;
        if((go=config.get(conf)) == null)
            throw new TuringException("It is not defined what to do from state " + state + " with configuration " + encodeCurrentConfiguration(conf));
        for(String newState: go.keySet()) {
            Iterator<String> it = go.get(newState).iterator();
            while(it.hasNext()) {
                String newConfig = it.next();
                String[] newTapes = new String[tapesNumber];
                System.arraycopy(tapes, 0, newTapes, 0, tapesNumber);
                int[] newHeads = new int[tapesNumber];
                System.arraycopy(heads, 0, newHeads, 0, tapesNumber);
                char[] tmp = newConfig.toCharArray();
                for(int m=0, j=0; j<tmp.length; j+=2, m++) {
                    char dir = tmp[j+1];
                    newTapes[m] = newTapes[m].substring(0,newHeads[m])+tmp[j]+newTapes[m].substring(newHeads[m]+1);
                    if(dir == rightDirection)
                        newHeads[m]++;
                    else if(dir == leftDirection) {
                        if(newHeads[m] == 0)
                            throw new TuringException("Cannot go before the universe!");
                        newHeads[m]--;
                    } else if(dir != stopDirection)
                        throw new TuringException("Cannot understand the following direction: " + dir);
                }
                run(newTapes, newHeads, newState, output, yetExecuted);
            }
        }
    }
    
    /**
        This exception may be thrown by:
        * <ul>
        * <li>Turing constructor, if the input file contains a malformed input;</li>
        * <li>Turing runs methods, if the program has an error</li>
        * </ul>
     */
    public final class TuringException extends RuntimeException {
        public TuringException(String s) {
            super(s);
        }
    }
    
}
