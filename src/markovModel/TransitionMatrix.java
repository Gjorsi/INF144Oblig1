package markovModel;

import java.util.HashMap;
import java.util.Random;

public class TransitionMatrix {
    private Random r;
    private HashMap<String, MatrixRow> matrix;
    
    public TransitionMatrix() {
        matrix = new HashMap<>();
        r = new Random();
    }
    
    public boolean exists(String prefix) {
        return matrix.containsKey(prefix);
    }
    
    public void addPrefix(String prefix, char suffix) {
        matrix.put(prefix, new MatrixRow(suffix));
    }
    
    public void addOccurence(String prefix, char suffix) {
        matrix.get(prefix).addOccurence(suffix);
    }
    
    public char getRandomized(String prefix) {
        int random = r.nextInt(matrix.get(prefix).freqTotal+1); //a random number limited by the total of occurences of given prefix
        int sum = 0;
        
        for (Character c : matrix.get(prefix).freq.keySet()) {
            sum += matrix.get(prefix).freq.get(c);
            if (sum >= random) {
                return c;
            }
        }
        
        throw new IllegalStateException("Couldnt find next char, this should not happen");
            
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
        
        for(char c : matrix.get(" ").freq.keySet())
            System.out.println("suffixes to space: " + c);
            
        
        for(char c : matrix.get("d").freq.keySet())
            System.out.println("suffixes to d: " + c);
    }
}
