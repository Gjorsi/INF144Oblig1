package compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LZWold {

    private int ALPH_SIZE;
    private String text;
    HashSet<Character> alphabet;
    private BiDirectionalMap dictionary; // the dictionary used for compression
    private BiDirectionalMap decodeDictionary;
    private ArrayList<Integer> compressed; //the output of the compression algorithm
    private HashMap<Integer, Integer> bitCount; //stores the number of outputs with x as bitlength
    private int currentBitLength;
    private int initialBitLength;
    private int dictionaryIndex;
    
    public LZWold(char[] chars, HashSet<Character> alphabet, int alph_size) {
        this.ALPH_SIZE = alph_size;
        this.alphabet = alphabet;
        dictionary = new BiDirectionalMap();
        compressed = new ArrayList<>();
        bitCount = new HashMap<>();
        
        StringBuilder sb = new StringBuilder();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBitLength = findInitialBitLength();
        bitCount.put(currentBitLength, 0);
        
        for (int i=0; i<chars.length; i++) {
            if (alphabet.contains(chars[i])) sb.append(chars[i]);
        }
        
        text = sb.toString();
    }
    
    public LZWold(String source, HashSet<Character> alphabet, int alph_size) {
        this.ALPH_SIZE = alph_size;
        this.alphabet = alphabet;
        this.text = source;
        dictionary = new BiDirectionalMap();
        compressed = new ArrayList<>();
        bitCount = new HashMap<>();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBitLength = findInitialBitLength();
        bitCount.put(currentBitLength, 0);
    }
    
    public void compress() {
        Integer pos = 0;
        
        for (int i=1; i<text.length(); i++) {
            if (dictionary.contains(text.substring(pos, i+1)) == null) { //if current sequence + char at i is not in dictionary
                compressed.add(dictionary.contains(text.substring(pos, i))); //output current sequence's index
                dictionary.put(dictionaryIndex++, text.substring(pos, i+1)); //add current sequence + char to dictionary
                bitCount.put(currentBitLength, bitCount.get(currentBitLength)+1); // log that an index was added to output of current bitlength
                if (dictionary.size() > Math.pow(2.0, currentBitLength)) { // increase bitlength if necessary
                    currentBitLength++;
                    System.out.println("bitlength changed to " + currentBitLength);
                    bitCount.put(currentBitLength, 0);
                }
                pos = i;
            }
        }
        compressed.add(dictionary.contains(text.substring(pos, text.length())));
        if (dictionary.size() >= Math.pow(2, currentBitLength)) {
            currentBitLength++;
            System.out.println("bitlength changed to " + currentBitLength);
            bitCount.put(currentBitLength, 0);
        }
        bitCount.put(currentBitLength, bitCount.get(currentBitLength)+1); // log that an index was added to output of current bitlength
    }
    
    public void decompress() {
        
    }
    
    public void printCompressed() {
        for (int i=0; i<compressed.size(); i++) {
            System.out.print(compressed.get(i) + " ");
            if (i%10 == 0) System.out.println();
        }
        System.out.println();
    }
    
    public void printCompressionRatio() {
//        System.out.println("initial bit length: " + initialBitLength);
//        System.out.println("current bit length: " + currentBitLength);
//        System.out.println("text.length: " + text.length());
        int UncompressedLength = text.length()*initialBitLength;
        int compressedLength = 0;
//        System.out.println("bitcount size: " + bitCount.size());
        for (int i : bitCount.keySet()) {
            compressedLength += i*bitCount.get(i);
//            System.out.println("Compressed output contains " + bitCount.get(i) + " codes of bit length " + i);
        }
        
        System.out.println("Uncompressed bit-length: " + UncompressedLength);
        System.out.println("Compressed bit-length: " + compressedLength);
        
        double rate = (double)compressedLength / UncompressedLength;
        rate = 1-rate;
        System.out.printf("Compression rate: %.2f%%\n", rate*100);
    }
    
    private int findInitialBitLength() {
        initialBitLength = 1;
        while (alphabet.size() > Math.pow(initialBitLength, 2)) initialBitLength++;
        return --initialBitLength;
    }
    
    private class BiDirectionalMap {
        private HashMap<Integer, String> intToString;
        private HashMap<String, Integer> stringToInt;
        
        public BiDirectionalMap() {
            intToString = new HashMap<>();
            stringToInt = new HashMap<>();
        }
        
        public void put(int i, String s) {
            intToString.put(i, s);
            stringToInt.put(s, i);
        }
        
        public Integer contains(String s) {
            return stringToInt.get(s);
        }
        
        public String get(int i) {
            return intToString.get(i);
        }
        
        public int size() {
            return intToString.size();
        }
    }

}
