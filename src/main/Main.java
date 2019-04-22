package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import compression.Huffman;
import compression.LZWold;
import compression.LZW;
import markovModel.Markov;

public class Main {

    private static char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'æ', 'ø', 'å', ' '};
//  private static char[] chars = {'#', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
//  'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static HashSet<Character> alphabet;
    private final static int DICT_SIZE = 30;
    private static char[] sourceText;
    
    public static void main(String[] args) throws IOException {
        sourceText = new char[9000];
        
        alphabet = new HashSet<Character>();
        for (int i=0; i<DICT_SIZE; i++) {
            alphabet.add(chars[i]);
        }
        
        readFile();
        
        Markov m = new Markov(sourceText, chars, alphabet, 30);
        
        String s = m.generateOrder(8000, 3);
        System.out.println(s);
        
        LZW lzw = new LZW(s, alphabet, DICT_SIZE);
        lzw.compress();
        int initialBitLength = lzw.printCompressionRatio();
//        System.out.println(lzw.decompress(lzw.toString()));
        
        Huffman hm = new Huffman(lzw.toStringBlocks());
        hm.encode();
        int finalBitLength = hm.printCompressionRatio();
        
        printTotalCompression(initialBitLength, finalBitLength);
        
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
//        
//        bw.write(sourceText, 0, sourceText.length);
//        bw.flush();
//        bw.close();
//        m.entropy0();
//        m.entropy1();
        
//        while(true) {
//            System.out.println("q = quit\n" 
//                    + "enter two space-separated integers;\nthe desired order followed by length of generated text.\n");
//            String input = sc.nextLine();
//            if (input.equals("q")) {
//                sc.close();
//                break;
//            }
//            
//            try {
//                inSplit = input.split(" ");
//                order = Integer.parseInt(inSplit[0]);
//                length = Integer.parseInt(inSplit[1]);
//                
//            } catch (Exception e) {
//                System.out.println("Unable to parse input.");
//            }
//            
//            m.generateOrder(length, order);
//        }
        
    }

    private static void readFile() {
        File f = new File("Folktale.html");
        BufferedReader br;
        String s = "";
        try {
            br = new BufferedReader(new FileReader(f));
            while(br.ready()) {
                s = br.readLine();
                if (alphabet.contains(s.charAt(s.length()-1))) {
                    sourceText = s.toLowerCase().toCharArray();
                    break;
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTotalCompression(int initialBitLength, int finalBitLength) {
        double totalCompressionRatio = (1.0-(double)finalBitLength/initialBitLength)*100;
        System.out.printf("Final compression rate from initial bit-length of %d to final bit-length of %d:\n"
                + "%.2f%%", initialBitLength, finalBitLength, totalCompressionRatio);
    }

}
