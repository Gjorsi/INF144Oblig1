package markovModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Markov {

    private final int DICT_SIZE = 30;
    private HashMap<Character, Integer> charPos = new HashMap<>();
    private char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'æ', 'ø', 'å', ' '};
    private HashSet<Character> alphabet;
    private Character[] sourceText = new Character[10000];
    private Double[] matrix0 = new Double[DICT_SIZE];
    private Random r;
    private TransitionMatrix trMatrix;
    
    public Markov(File source) {
        
        //read file into character array sourceText
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(source));
            int x = 0;
            while(br.ready()) 
                sourceText[x++] = ((char)br.read());
            
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Initialise
        r = new Random();
        alphabet = new HashSet<Character>();
        for (int i=0; i<DICT_SIZE; i++) {
            charPos.put(chars[i], i); 
            alphabet.add(chars[i]);
        }
        for (int i=0; i<matrix0.length; i++) {
            matrix0[i] = 0.0;
        }
        
        trMatrix = new TransitionMatrix();
        
    }

    public void entropy1() {
        analyzeOrder(1);
        
        int[][] piMatrix1 = new int[DICT_SIZE][DICT_SIZE];
        
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                piMatrix1[i][j] = trMatrix.
            }
        }
    }
    
    private void analyzeOrder0 () {
        int sum = 0;
        for(int i=0; i<sourceText.length; i++) {
            if (alphabet.contains(sourceText[i])) {
                matrix0[charPos.get(sourceText[i])]++;
                sum++;
            }
        }

        for (int i=0; i<DICT_SIZE; i++) {
            matrix0[i] /= sum;
        }
    }
    
    public void generateOrder0(int length) {
        analyzeOrder0();
        Double random, sum = 0.0;
        int x;
        StringBuilder sb = new StringBuilder(length);
        
        for (int i=0; i<length; i++) {
            random = r.nextDouble();
            sum = 0.0;
            x = 0;
            
            while(random>sum) {
                sum+=matrix0[x++];
            }
            
            sb.append(chars[x-1]);
        }
        
        System.out.println(sb.toString());
    }
    
    private void analyzeOrder(int order) {
        String pfx;
        
        nextPfx: for (int i=0+order; i<sourceText.length; i++) {
            if (!alphabet.contains(sourceText[i]))
                continue;
            pfx = "";
            //look at current prefix, cancel if any of the characters in this one is not in the alphabet
            for (int j=order; j>=1; j--) {
                if (!alphabet.contains(sourceText[i-j]))
                    continue nextPfx;
                pfx += sourceText[i-j];
            }
            if (trMatrix.exists(pfx))
                trMatrix.addOccurrence(pfx, sourceText[i]);
            else
                trMatrix.addPrefix(pfx, sourceText[i]);
        }
    }

    
    
    public void generateOrder(int length, int order) {
        if (order == 0) {
            generateOrder0(length);
            return;
        }
        
        analyzeOrder0();
        for (int i=1; i<=order; i++) {
            analyzeOrder(i);
        }
        Double random, sum = 0.0;
        int x;
        StringBuilder sb = new StringBuilder(length);
        
        //first char
        x = 0;
        random = r.nextDouble();
        while(random>sum) {
            sum+=matrix0[x++];
        }
        sb.append(chars[x-1]);
        
        //2nd, 3rd etc characters for higher orders
        for (int i=1; i<order; i++) {
            sb.append(trMatrix.getRandomized(sb.substring(sb.length()-i)));
        }
        
        //rest of text
        for (int i=order; i<length; i++) {
            sb.append(trMatrix.getRandomized(sb.substring(sb.length()-order)));
        }
        
        System.out.println(sb.toString());
    }
}
