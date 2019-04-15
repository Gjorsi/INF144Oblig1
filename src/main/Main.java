package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import compression.Huffman;
import compression.LZW;
import compression.LZWHuffman;
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
        File f = new File("Folktale.html");
        sourceText = new char[9000];
        
        alphabet = new HashSet<Character>();
        for (int i=0; i<DICT_SIZE; i++) {
            alphabet.add(chars[i]);
        }
        
        Markov m = new Markov(f, chars, alphabet, 30);
        
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            int x = 0;
            while(br.ready()) 
                sourceText[x++] = ((char)br.read());
            
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String s = m.generateOrder(50, 3);
        System.out.println(s);
        
//        LZW lzw = new LZW(sourceText, alphabet, DICT_SIZE);
        LZWHuffman lzw = new LZWHuffman(s, alphabet, DICT_SIZE);
//        LZW lzw = new LZW("tobeornottobeortobeornot#", alphabet, DICT_SIZE);
        lzw.compress();
//        lzw.printCompressed();
        lzw.printCompressionRatio();
        System.out.println(lzw.decompress(lzw.toString()));
        
        Huffman hm = new Huffman(lzw.toStringBlocks());
        hm.encode();
        hm.printCompressionRatio();
        
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

}
