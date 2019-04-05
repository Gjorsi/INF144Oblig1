package compression;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

public class LZWHuffman {

    private String text;
    HashSet<Character> alphabet;
    private BiDirectionalMap dictionary; // the dictionary used for compression
    private BiDirectionalMap decodeDictionary;
//    private ArrayList<Integer> compressed; //the output of the compression algorithm
    private ArrayList<boolean[]> LZWoutput;
    private int currentBitLength;
    private int initialBitLength;
    private int dictionaryIndex;
    
    public LZWHuffman(char[] chars, HashSet<Character> alphabet, int alph_size) {
        this.alphabet = alphabet;
        dictionary = new BiDirectionalMap();
//        compressed = new ArrayList<>();
        LZWoutput = new ArrayList<boolean[]>();
        
        StringBuilder sb = new StringBuilder();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBitLength = findInitialBitLength();
        
        for (int i=0; i<chars.length; i++) {
            if (alphabet.contains(chars[i])) sb.append(chars[i]);
        }
        
        text = sb.toString();
    }
    
    public LZWHuffman(String source, HashSet<Character> alphabet, int alph_size) {
        this.alphabet = alphabet;
        this.text = source;
        dictionary = new BiDirectionalMap();
//        compressed = new ArrayList<>();
        LZWoutput = new ArrayList<boolean[]>();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBitLength = findInitialBitLength();
    }
    
    public void compress() {
        Integer pos = 0;
        
        for (int i=1; i<text.length(); i++) {
            if (dictionary.contains(text.substring(pos, i+1)) == null) { //if current sequence + char at i is not in dictionary
                addBitBlock(dictionary.contains(text.substring(pos, i))); //output current sequence's index
                dictionary.put(dictionaryIndex++, text.substring(pos, i+1)); //add current sequence + char to dictionary
                if (dictionary.size() > Math.pow(2.0, currentBitLength)) { // increase bitlength if necessary
                    currentBitLength++;
                    System.out.println("bitlength changed to " + currentBitLength);
                }
                pos = i;
            }
        }
        addBitBlock(dictionary.contains(text.substring(pos, text.length())));
        if (dictionary.size() >= Math.pow(2, currentBitLength)) {
            currentBitLength++;
            System.out.println("bitlength changed to " + currentBitLength);
        }
    }
    
    private void addBitBlock(int code) {
        String s = Integer.toBinaryString(code);
        boolean[] binaryCode = new boolean[currentBitLength];

        for (int i=currentBitLength-s.length(); i<currentBitLength ; i++) {
            if (s.charAt(i-(currentBitLength-s.length())) == '1') binaryCode[i] = true;
        }
        LZWoutput.add(binaryCode);
    }

    public void decompress() {
        
    }
    
    public void printCompressed() {
        for (int i=0; i<LZWoutput.size(); i++) {
            for (int j=0; j<LZWoutput.get(i).length ; j++) {
                String s = (LZWoutput.get(i)[j]) ? "1" : "0";
                System.out.print(s);
            }
            System.out.print("  ");
            if (i%8 == 0) System.out.println();
        }
        System.out.println();
    }
    
    public void printCompressionRatio() {
//        System.out.println("initial bit length: " + initialBitLength);
//        System.out.println("current bit length: " + currentBitLength);
//        System.out.println("text.length: " + text.length());
        int UncompressedLength = text.length()*initialBitLength;
        int compressedLength = 0;
        for (boolean[] b : LZWoutput) {
            compressedLength += b.length;
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
