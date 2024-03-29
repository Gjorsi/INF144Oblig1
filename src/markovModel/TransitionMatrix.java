package markovModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class TransitionMatrix {
    private Random r;
    private HashMap<String, MatrixRow> matrix;
    
    public TransitionMatrix() {
        matrix = new HashMap<>();
        r = new Random();
    }
    
    /**
     * Check to see if the transition matrix has a row for the given prefix
     * 
     * @param prefix
     * @return true if the row exists
     */
    public boolean exists(String prefix) {
        return matrix.containsKey(prefix);
    }
    
    /**
     * Add a new row for the given prefix and the suffix as a possible transition
     * 
     * @param prefix
     * @param suffix
     */
    public void addPrefix(String prefix, char suffix) {
        matrix.put(prefix, new MatrixRow(suffix));
    }
    
    /**
     * Add an occurrence of the given suffix to the row of the prefix in the transition matrix
     * 
     * @param prefix
     * @param suffix
     */
    public void addOccurrence(String prefix, char suffix) {
        matrix.get(prefix).addOccurence(suffix);
    }
    
    /**
     * Get a random character based on the probability statistics of the given prefix
     * 
     * @param prefix
     * @return the generated character
     */
    public char getRandomized(String prefix) {
        try {
            int temp = matrix.get(prefix).freqTotal;
            int random = r.nextInt(temp); //a random number limited by the total of occurrences of given prefix
            int sum = 0;
            
            for (Character c : matrix.get(prefix).freq.keySet()) {
                sum += matrix.get(prefix).freq.get(c);
                if (sum > random) {
                    return c;
                }
            }
            
            throw new IllegalStateException("Couldnt find next char, this should not happen");
        } catch (NullPointerException e) {
            System.out.println("Could not find frequency total of prefix '" + prefix + "'" );
            e.printStackTrace();
        }
        
        return 0;    
    }
    
    public HashSet<Character> getSuffixes (String prefix) {
        HashSet<Character> t = new HashSet<>();
        t.addAll(matrix.get(prefix).freq.keySet());
        return t;
    }
    
    public boolean containsSuffix (String prefix, char c) {
        return matrix.get(prefix).freq.containsKey(c);
    }
    
    public double getProb (String suffix, char c) {
        return (double)matrix.get(suffix).freq.get(c) / matrix.get(suffix).freqTotal;
    }

    private class MatrixRow {
        public int freqTotal;
        public HashMap<Character, Integer> freq;
        
        public MatrixRow(char symbol) {
            freqTotal = 1;
            freq = new HashMap<Character, Integer>();
            freq.put(symbol, 1);
        }
        
        public void addOccurence(char symbol) {
            if (freq.containsKey(symbol))
                freq.put(symbol, freq.get(symbol)+1);
            else   
                freq.put(symbol, 1);
            
            freqTotal++;
        }
    }

    //for testing purposes
    public void print() {
        System.out.println("all stored prefixes:");
        for(String s : matrix.keySet())
            System.out.println("prefix: " + s);
    }
}
