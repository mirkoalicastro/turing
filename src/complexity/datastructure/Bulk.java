package complexity.datastructure;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A Bulk is a structure which can handle n-tuple. An n-tuple is a finite ordered sequence of n elements, where n is a non-negative integer. 
 * 
 * @author Mirko Alicastro
 * @link https://github.com/mirkoalicastro/turing
 * @version 1.2
 */

public class Bulk implements Iterable<Object> {
    private final Object[] elements;

    /**
     * Creates a new bulk collection containing all the elements given to the constructor
     * @param elements to be added to the bulk
     */
    public Bulk(Object ... elements) {
        if(elements.length == 0)
            this.elements = null;
        else
            this.elements = elements;
    }
    @Override
    public String toString() {
        String ret = recursiveToString(elements);
        if(ret.substring(0,1).equals("["))
            ret = ret.substring(1, ret.length()-1);
        return "(" + ret + ")";
    }
    private static String recursiveToString(Object x) {
        if(x == null)
            return "null";
        if(!x.getClass().isArray())
            return x.toString();
        String ret = "[";
        for(int i=0; i<Array.getLength(x); i++)
            ret += recursiveToString(Array.get(x,i)) + ", ";
        return ret.substring(0, ret.length() > 1 ? ret.length()-2 : 1) + "]";
    }
    @Override
    public boolean equals(Object x) {
        return !(x == null) && (x instanceof Bulk) && Objects.deepEquals(elements, ((Bulk)x).elements);
    }
    @Override
    public int hashCode() {
        return recursiveHashCode(elements);
    }
    private static int recursiveHashCode(Object x) {
        if(x == null)
            return 0;
        if(!x.getClass().isArray())
            return x.hashCode();
        int h = 0;
        for(int i=0; i<Array.getLength(x); i++)
            h ^= recursiveHashCode(Array.get(x,i));
        return h;
    }

    /**
     * Gets the bulk size if the bulk is not empty, 0 otherwise.
     * @return the size of the bulk
     */
    public int size() {
        if(elements == null)
            return 0;
        return Array.getLength(elements);
    }

    /**
     * Checks if the bulk contains an array or just a single element
     * @return true if the bulk contains and array, false otherwise
     */
    public boolean isArray() {
        return elements != null && elements.getClass().isArray();
    }

    /**
     * Gets the i-th element of the bulk.
     * It will throws ArrayIndexOutOfBoundsException if:
     * <ul>
     * <li><i>i</i> is lower than zero;</li>
     * <li><i>i</i> is greater than bulk size;</li>
     * </ul>
     * @param i index of the element to be retrieved
     * @return i-th element of the bulk
     * @throws ArrayIndexOutOfBoundsException
     */
    public Object get(int i) throws ArrayIndexOutOfBoundsException {
        if(i < 0 || i > size())
            throw new ArrayIndexOutOfBoundsException();
        return isArray() ? Array.get(elements, i) : elements;
    }

    /**
     * Gets an iterator over the bulk
     * @return iterator over the bulk
     */
    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public Object next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                return get(index++);
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
