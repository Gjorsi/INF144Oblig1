package main;

import java.io.File;
import java.util.Scanner;

import markovModel.Markov;

public class Main {

    public static void main(String[] args) {
        File f = new File("Folktale.html");
        Markov m = new Markov(f);
        
        Scanner sc = new Scanner(System.in);
        
        while(true) {
            System.out.println("q = quit\n");
            String input = sc.nextLine();
            if (input == "q")
                break;
            
            m.generateOrder2(Integer.parseInt(input));
        }
        
    }

}
