package main;

import java.io.File;
import java.util.Scanner;

import markovModel.Markov;

public class Main {

    public static void main(String[] args) {
        File f = new File("Folktale.html");
        Markov m = new Markov(f);
        int order = 0, length = 0;
        String[] inSplit;
        
        Scanner sc = new Scanner(System.in);
        
        m.entropy0();
        m.entropy1();
        
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
