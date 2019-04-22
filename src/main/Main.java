package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

import compression.Huffman;
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
    private static Markov m;
    
    public static void main(String[] args) throws IOException {
        sourceText = new char[9000];
        
        alphabet = new HashSet<Character>();
        for (int i=0; i<DICT_SIZE; i++) {
            alphabet.add(chars[i]);
        }
        
        readFile();
        
        Scanner sc = new Scanner(System.in);
        int inputN;
        
        while(true) {
            System.out.println("\nEnter 'Q' to quit.\n"
                    + "Enter '1' to run compression on 'Folktale.html'.\n"
                    + "Enter '2' to run compression on 100 randomly generated texts (of markov order 0-3).\n");
            String input = sc.nextLine();
            if (input.equals("Q")) {
                sc.close();
                break;
            }
            
            try {
                inputN = Integer.parseInt(input);
                if (inputN == 1) compressFile();
                else if (inputN == 2) compressGenerated();
                else System.out.println("Unable to parse input.");
            } catch (Exception e) {
                System.out.println("Unable to parse input.");
            }
        }
    }

    private static void compressFile() {
        LZW lzw = new LZW(sourceText, alphabet, DICT_SIZE);
        lzw.compress();
        int initialBitLength = lzw.printCompressionRatio();
        
        Huffman hm = new Huffman(lzw.toStringBlocks());
        hm.encode();
        System.out.println(lzw.decompress(hm.decode()));
        int finalBitLength = hm.printCompressionRatio();
        
        printTotalCompression(initialBitLength, finalBitLength);
        
    }

    private static void compressGenerated() {
        
        m = new Markov(sourceText, chars, alphabet, 30);
        int initialBitLength, finalBitLength;
        double[] compressionResults = new double[100];
        
        for (int i=0; i<4; i++) {
            for (int j=0; j<100; j++) {
                String s = m.generateOrder(8000, i);
                LZW lzw = new LZW(s, alphabet, DICT_SIZE);
                lzw.compress();
                initialBitLength = lzw.getInitialBitLength();
                Huffman hm = new Huffman(lzw.toStringBlocks());
                hm.encode();
                finalBitLength = hm.getCompressedLength();
                compressionResults[j] = getTotalCompression(initialBitLength, finalBitLength);
            }
            System.out.println("Average compression rate for order " + i + " is " + average(compressionResults));
        }
    }

    private static String average(double[] compressionResults) {
        double sum = 0.0;
        for(double d : compressionResults) {
            sum += d;
        }
        return String.format("%.2f%%", sum/100.0);
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

    private static double getTotalCompression(int initialBitLength, int finalBitLength) {
        return (1.0-(double)finalBitLength/initialBitLength)*100;
    }
    
    private static void printTotalCompression(int initialBitLength, int finalBitLength) {
        double totalCompressionRatio = (1.0-(double)finalBitLength/initialBitLength)*100;
        System.out.printf("Final compression rate from initial bit-length of %d to final bit-length of %d:\n"
                + "%.2f%%\n", initialBitLength, finalBitLength, totalCompressionRatio);
    }

}
