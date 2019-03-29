package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

import compression.LZW;
import markovModel.Markov;

public class Main {

    private static char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'æ', 'ø', 'å', ' '};
    private static HashSet<Character> alphabet;
    private final static int DICT_SIZE = 30;
    
    public static void main(String[] args) {
        File f = new File("Folktale.html");
        
        alphabet = new HashSet<Character>();
        for (int i=0; i<DICT_SIZE; i++) {
            alphabet.add(chars[i]);
        }
        
        Markov m = new Markov(f, chars, alphabet, 30);
        int order = 0, length = 0;
        String[] inSplit;
        
        Scanner sc = new Scanner(System.in);
        
        Scanner fsc;
        try {
            fsc = new Scanner(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String s = "";
            while (fsc.hasNextLine()) {
                s = fsc.nextLine();
                System.out.println(s);
                sb.append(s);
            }
            
            System.out.println(sb);
            
            new LZW(sb.toString(), chars, alphabet, DICT_SIZE);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
//        m.entropy0();
//        m.entropy1();
        
        while(true) {
            System.out.println("q = quit\n" 
                    + "enter two space-separated integers;\nthe desired order followed by length of generated text.\n");
            String input = sc.nextLine();
            if (input.equals("q")) {
                sc.close();
                break;
            }
            
            try {
                inSplit = input.split(" ");
                order = Integer.parseInt(inSplit[0]);
                length = Integer.parseInt(inSplit[1]);
                
            } catch (Exception e) {
                System.out.println("Unable to parse input.");
            }
            
            m.generateOrder(length, order);
        }
        
    }

}
