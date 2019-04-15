package compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LZWHuffman {

    private String text;
    HashSet<Character> alphabet;
    private BiDirectionalMap dictionary; // the dictionary used for compression
    private BiDirectionalMap decodeDictionary;
    private ArrayList<boolean[]> LZWoutput;
    private int currentBlockSize;
    private int initialBlockSize;
    private int dictionaryIndex;
    
    public LZWHuffman(char[] chars, HashSet<Character> alphabet, int alph_size) {
        this.alphabet = alphabet;
        dictionary = new BiDirectionalMap();
        decodeDictionary = new BiDirectionalMap();
        LZWoutput = new ArrayList<boolean[]>();
        
        StringBuilder sb = new StringBuilder();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            decodeDictionary.put(dictionaryIndex, Character.toString(c));
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBlockSize = findInitialBitLength();
        
        for (int i=0; i<chars.length; i++) {
            if (alphabet.contains(chars[i])) sb.append(chars[i]);
        }
        
        text = sb.toString();
    }
    
    public LZWHuffman(String source, HashSet<Character> alphabet, int alph_size) {
        this.alphabet = alphabet;
        this.text = source;
        dictionary = new BiDirectionalMap();
        decodeDictionary = new BiDirectionalMap();
        LZWoutput = new ArrayList<boolean[]>();
        
        dictionaryIndex=0;
        for (char c : alphabet) {
//            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            decodeDictionary.put(dictionaryIndex, Character.toString(c));
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        currentBlockSize = findInitialBitLength();
    }
    
    public void compress() {
        Integer pos = 0;
        
        for (int i=1; i<text.length(); i++) {
            if (dictionary.contains(text.substring(pos, i+1)) == null) { //if current sequence + char at i is not in dictionary
                addBitBlock(dictionary.contains(text.substring(pos, i))); //output current sequence's index as a block of bits (block length = currentBlockSize)
                dictionary.put(dictionaryIndex++, text.substring(pos, i+1)); //add current sequence + char to dictionary
                if (dictionary.size() > Math.pow(2.0, currentBlockSize)) { // increase bitlength if necessary
                    currentBlockSize++;
                    System.out.println("Block size changed to " + currentBlockSize);
                }
                pos = i;
            }
        }
        addBitBlock(dictionary.contains(text.substring(pos, text.length())));
        if (dictionary.size() >= Math.pow(2, currentBlockSize)) {
            currentBlockSize++;
            System.out.println("Block size changed to " + currentBlockSize);
        }
    }
    
    private void addBitBlock(int code) {
        String s = Integer.toBinaryString(code);
        boolean[] binaryCode = new boolean[currentBlockSize];

        for (int i=currentBlockSize-s.length(); i<currentBlockSize ; i++) {
            if (s.charAt(i-(currentBlockSize-s.length())) == '1') binaryCode[i] = true;
        }
        LZWoutput.add(binaryCode);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String s;
        boolean[] block;
        for (int i=0; i<LZWoutput.size(); i++) {
            block = LZWoutput.get(i);
            s = "";
            for (int j=0; j<block.length; j++) {
                s += (block[j]) ? "1" : "0";
            }
            
            sb.append(s);
        }
        
        return sb.toString();
    }
    
    public ArrayList<String> toStringBlocks() {
        ArrayList<String> stringBlocks = new ArrayList<String>();
        String s;
        boolean[] block;
        for (int i=0; i<LZWoutput.size(); i++) {
            block = LZWoutput.get(i);
            s = "";
            for (int j=0; j<block.length; j++) {
                s += (block[j]) ? "1" : "0";
            }
            stringBlocks.add(s);
            
        }
        
        return stringBlocks;
    }

    public String decompress(String coded) {
        currentBlockSize = initialBlockSize;
        StringBuilder sb = new StringBuilder();
        String block = decodeDictionary.get(Integer.parseInt(coded.substring(0, currentBlockSize), 2));
        sb.append(block);
        String parse, temp;
        int dictIndex;
        int maxDictIndex = alphabet.size();
        for (int i=currentBlockSize; i<coded.length()-currentBlockSize; i+=currentBlockSize) {
            temp = coded.substring(i, i+currentBlockSize);
            dictIndex = Integer.parseInt(temp, 2);
            if (decodeDictionary.containsKey(dictIndex)) {
                parse = decodeDictionary.get(dictIndex);
            } else if (dictIndex == maxDictIndex) {
                parse = block + block.charAt(0);
            } else {
                throw new IllegalStateException("Could not decode: " + dictIndex);
            }
            
            sb.append(parse);
            decodeDictionary.put(maxDictIndex++, block + parse.charAt(0));
            if (maxDictIndex > Math.pow(2, currentBlockSize)) currentBlockSize++;
            block = parse;
        }
        
        return sb.toString();
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
        System.out.println("Dictionary size after compression: " + dictionary.size());
        int UncompressedLength = text.length()*initialBlockSize;
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
        initialBlockSize = 1;
        while (alphabet.size() > Math.pow(initialBlockSize, 2)) initialBlockSize++;
        return --initialBlockSize;
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
        
        public boolean containsKey(int i) {
            return intToString.containsKey(i);
        }
        
        public String get(int i) {
            return intToString.get(i);
        }
        
        public int size() {
            return intToString.size();
        }
    }

}
