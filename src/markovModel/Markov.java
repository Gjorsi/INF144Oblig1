package markovModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Markov {

    private final int DICT_SIZE = 30;
    private HashMap<Character, Integer> charPos = new HashMap<>();
    private char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'æ', 'ø', 'å', ' '};
    private Double[] matrix0 = new Double[DICT_SIZE];
    private Double[][] trMatrix1 = new Double[DICT_SIZE][DICT_SIZE];
    private Double[][][] trMatrix2 = new Double[DICT_SIZE][DICT_SIZE][DICT_SIZE];
    private Double[][][][] trMatrix3 = new Double[DICT_SIZE][DICT_SIZE][DICT_SIZE][DICT_SIZE];
    private Character[] sourceText = new Character[10000];
    private Random r;
    
    public Markov(File source) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(source));
            int x = 0;
            while(br.ready()) 
                sourceText[x++] = ((char)br.read());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //initialize
        r = new Random();
        
        for (int i=0; i<DICT_SIZE; i++) {
           charPos.put(chars[i], i); 
        }
        for (int i=0; i<matrix0.length; i++) {
            matrix0[i] = 0.0;
        }
        for (int i=0; i<trMatrix1.length; i++) {
            for (int j=0; j<trMatrix1[0].length; j++) {
                trMatrix1[i][j] = 0.0;
            }
        }
        for (int i=0; i<trMatrix2.length; i++) {
            for (int j=0; j<trMatrix2[0].length; j++) {
                for (int k=0; k<trMatrix2[0][0].length; k++) {
                    trMatrix2[i][j][k] = 0.0;
                }
            }
        }
        for (int i=0; i<trMatrix3.length; i++) {
            for (int j=0; j<trMatrix3[0].length; j++) {
                for (int k=0; k<trMatrix3[0][0].length ; k++) {
                    for (int l=0; l<trMatrix3[0][0][0].length; l++) {
                        trMatrix3[i][j][k][l] = 0.0;
                    }
                }
            }
        }
    }
    
    private void analyzeOrder1() {
        int order = 1;
        Double[][] currentOM = trMatrix1;
        
        int cChar, pChar, sum, iMax = (int) Math.pow(DICT_SIZE, order);
        
        for (int i=0+order; i<sourceText.length; i++) {
            if (charPos.get(sourceText[i]) != null && charPos.get(sourceText[i-order]) != null) {
                cChar = charPos.get(sourceText[i]);
                pChar = charPos.get(sourceText[i-order]);
                currentOM[pChar][cChar]++;
            }
        }
        
        for (int i=0; i<iMax; i++) {
            sum = 0;
            for (int j=0; j<DICT_SIZE; j++) {
                sum += currentOM[i][j];
            }
            
            if (sum != 0) {
                for (int j=0; j<DICT_SIZE; j++) {
                    currentOM[i][j] /= sum;
                }
            }
        }
        
        //print test
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                System.out.println("P(" + chars[j] + " | " + chars[i] + ") = " + currentOM[i][j]);
            }
        }
    }
    
    private void analyzeOrder2() {
        
        Double[][][] currentOM = trMatrix2;
        Double[][] prevOM = trMatrix1;
        int order = 2;
        
        int cChar, p1Char, p2Char, sum;
        
        for (int i=0+order; i<sourceText.length; i++) {
            if (charPos.get(sourceText[i]) != null && charPos.get(sourceText[i-1]) != null && charPos.get(sourceText[i-order]) != null) {
                cChar = charPos.get(sourceText[i]);
                p1Char = charPos.get(sourceText[i-1]);
                p2Char = charPos.get(sourceText[i-order]);
                currentOM[p2Char][p1Char][cChar]++;
            }
        }
        
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                sum = 0;
                for (int k=0; k<DICT_SIZE ; k++) {
                    sum += currentOM[i][j][k];
                }
                
                if (sum != 0) {
                    for (int k=0; k<DICT_SIZE; k++) {
                        currentOM[i][j][k] /= sum;
                    }
                }
            }
            
        }
        
        //print test
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                for (int k=0; k<DICT_SIZE ; k++) {
                    System.out.println("P(" + chars[k] + " | " + chars[i] + "" + chars[j] + ") = " + currentOM[i][j][k]);
                }
            }
        }
    }
    
    private void analyzeOrder3() {
        
        Double[][][][] currentOM = trMatrix3;
        int order = 3;
        
        int cChar, p1Char, p2Char, p3Char, sum;
        
        for (int i=0+order; i<sourceText.length; i++) {
            if (charPos.get(sourceText[i]) != null && charPos.get(sourceText[i-1]) != null && charPos.get(sourceText[i-order]) != null) {
                cChar = charPos.get(sourceText[i]);
                p1Char = charPos.get(sourceText[i-1]);
                p2Char = charPos.get(sourceText[i-order]);
                currentOM[p2Char][p1Char][cChar][1]++;
            }
        }
        
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                sum = 0;
                for (int k=0; k<DICT_SIZE ; k++) {
                    sum += currentOM[i][j][k][1];
                }
                
                if (sum != 0) {
                    for (int k=0; k<DICT_SIZE; k++) {
                        currentOM[i][j][k][1] /= sum;
                    }
                }
            }
            
        }
        
        //print test
        for (int i=0; i<DICT_SIZE; i++) {
            for (int j=0; j<DICT_SIZE; j++) {
                for (int k=0; k<DICT_SIZE ; k++) {
                    System.out.println("P(" + chars[k] + " | " + chars[i] + "" + chars[j] + ") = " + currentOM[i][j][k]);
                }
            }
        }
    }

    private void analyzeOrder0 () {
        for(int i=0; i<sourceText.length; i++) {
            if (charPos.get(sourceText[i]) != null) {
                matrix0[charPos.get(sourceText[i])]++;
            }
        }
        
        for (int i=0; i<30; i++) {
            matrix0[i] /= sourceText.length;
        }
        
        //print test
//        for (int i=0; i<30; i++) {
//            System.out.println(chars[i] + ": " + matrix0[i]);
//        }
        
    }
    
    public void generateOrder0(int length) {
        analyzeOrder0();
        Double random, sum = 0.0;
        int x;
        StringBuilder sb = new StringBuilder(length);
        
        for (int i=0; i<30; i++) {
            sum += matrix0[i];
        }
        System.out.println("sum" + sum); //sum should be ~1
        
        for (int i=0; i<length; i++) {
            random = r.nextDouble();
            sum = 0.0;
            x = 0;
            
            System.out.println("random" + random);
            
            
            
            while(random>sum) {
                sum+=matrix0[x++];
            }
            
            sb.append(chars[x-1]);
        }
    }
    
}
