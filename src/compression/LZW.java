package compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

public class LZW {

    private String text;
    HashSet<Character> alphabet;
    private BiDirectionalMap<Integer, String> dictionary; // the dictionary used for compression
    private BiDirectionalMap<Integer, String> decodeDictionary;
    private ArrayList<boolean[]> LZWoutput;
    private int currentBlockSize;
    private int initialBlockSize;
    private int dictionaryIndex;
    
// -------- Constructors -----------
    
    public LZW(char[] chars, HashSet<Character> alphabet, int alph_size) {
        this(alphabet);
        
        
        StringBuilder sb = new StringBuilder();
        for (char c : alphabet) {
            decodeDictionary.put(dictionaryIndex, Character.toString(c));
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
        
        for (int i=0; i<chars.length; i++) {
            if (alphabet.contains(chars[i])) sb.append(chars[i]);
        }
        
        text = sb.toString();
    }
    
    public LZW(String source, HashSet<Character> alphabet, int alph_size) {
        this(alphabet);
        this.text = source;
        
        for (char c : alphabet) {
//            System.out.println("adding character '" + c + "' to dictionary at index " + dictionaryIndex);
            decodeDictionary.put(dictionaryIndex, Character.toString(c));
            dictionary.put(dictionaryIndex++, Character.toString(c));
        }
    }
    
    public LZW(HashSet<Character> alphabet) {
        this.alphabet = alphabet;
        dictionary = new BiDirectionalMap<>();
        decodeDictionary = new BiDirectionalMap<>();
        LZWoutput = new ArrayList<boolean[]>();
        dictionaryIndex=0;
        currentBlockSize = findInitialBitLength();
    }
    
    
 // -------- Compression -----------
    
    public void compress() {
        Integer pos = 0;
        
        for (int i=1; i<text.length(); i++) {
            if (!dictionary.containsV(text.substring(pos, i+1))) { //if current sequence + char at i is not in dictionary
                
                addBitBlock(dictionary.getE(text.substring(pos, i))); //output current sequence's index as a block of bits (block length = currentBlockSize)
                dictionary.put(dictionaryIndex++, text.substring(pos, i+1)); //add current sequence + char to dictionary
                if (dictionary.size() > Math.pow(2.0, currentBlockSize)) { // increase bitlength if necessary
                    currentBlockSize++;
//                    System.out.println("Block size changed to " + currentBlockSize);
                }
                pos = i;
            }
        }
        addBitBlock(dictionary.getE(text.substring(pos, text.length())));
        if (dictionary.size() >= Math.pow(2, currentBlockSize)) {
            currentBlockSize++;
//            System.out.println("Block size changed to " + currentBlockSize);
        }
    }
    
    
 // -------- Decompression -----------
    
    public String decompress(String coded) {
        StringBuilder sb = new StringBuilder();
        currentBlockSize = initialBlockSize;
        int curr;
        String s;
        char c = 0;
        int maxDictIndex = alphabet.size(); //
        
        //get first code, and add its' translation to output
        int prev = Integer.parseInt(coded.substring(0, currentBlockSize), 2);
        sb.append(decodeDictionary.getV(prev));
        
        int i = currentBlockSize;
        while (i <= coded.length()-currentBlockSize) {
            curr = Integer.parseInt(coded.substring(i, i+currentBlockSize), 2);
            if (!decodeDictionary.containsE(curr)) {
                s = decodeDictionary.getV(prev);
                s = s+c;
            } else {
                s = decodeDictionary.getV(curr);
            }
            
            sb.append(s);
            c = s.charAt(0);
            decodeDictionary.put(maxDictIndex++, (decodeDictionary.getV(prev)+c));
            i += currentBlockSize;
            if (maxDictIndex >= Math.pow(2, currentBlockSize)) currentBlockSize++;
            prev = curr;
        }
        
        return sb.toString();
    }
    
    
 // -------- Helper-methods -----------
   
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
    
    private int findInitialBitLength() {
        initialBlockSize = 1;
        while (alphabet.size() > Math.pow(initialBlockSize, 2)) initialBlockSize++;
        return --initialBlockSize;
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
    
    
    // -------- Printer-methods -----------
    
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
    
    public void printCodeDictionary() {
        System.out.println("LZW Encode dictionary ------------");
        for (Integer i : dictionary.getESet()) {
            System.out.println(i + " --> " + dictionary.getV(i));
        }
    }
    
    public void printDecodeDictionary() {
        System.out.println("LZW Decode dictionary ------------");
        for (Integer i : decodeDictionary.getESet()) {
            System.out.println(i + " --> " + decodeDictionary.getV(i));
        }
    }
    
}
