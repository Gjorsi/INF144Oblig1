package compression;

import java.util.HashMap;
import java.util.HashSet;

public class LZW {

    private char[] chars;
    private HashSet<Character> alphabet;
    private int ALPH_SIZE;
    private HashMap<Integer, String> dictionary;
    private String text;
    
    public LZW(String source, char[] chars, HashSet<Character> alphabet, int alph_size) {
        this.text = source;
        this.chars = chars;
        this.alphabet = alphabet;
        this.ALPH_SIZE = alph_size;
        dictionary = new HashMap<>();
        
        int x = 0;
        for(Character c : alphabet) {
            dictionary.put(x++, c.toString());
        }
        System.out.println("initial text: " + source);
        text.replaceAll("[^abcdefghijklmnopqrstuvwxyzæøå]", "");
        
        System.out.println("cleaned test: " + text);
    }

}
