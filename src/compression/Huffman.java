package compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    private HashMap<String, Integer> freqs;
    private ArrayList<String> source;
    private Node root;
    private ArrayList<String> compressed;
    private BiDirectionalMap<String, String> codeMap;
    
// --------- Constructors ---------
    
    public Huffman(ArrayList<String> input) {
        this.source = input;
    }
    
 // --------- Encoding ---------
    
    public void encode() {
        createHuffmanTree();
        createCodeMap();
//        printCodeMap();
        compressed = new ArrayList<String>();
        code();
    }
    
    private void code() {
        for(String s : source) {
            compressed.add(codeMap.getV(s)); //add codeword for s to the compressed output
        }
    }

    
 // --------- Decoding ---------
    
    public String decode() {
        StringBuilder sb = new StringBuilder();
        for(String s : compressed) {
            sb.append(codeMap.getE(s));
        }
        return sb.toString();
    }
    
 // --------- Helper-methods ---------
    
    private void createHuffmanTree() {
        findFrequencies();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        addInitialNodes(queue);
        
        while (queue.size() > 1) {
            Node rightN = queue.poll();
            Node leftN = queue.poll();
            
            Node parent = new Node(leftN.freq + rightN.freq);
            parent.right = rightN;
            parent.left = leftN;
//            System.out.println("Created new node of freq " + parent.freq + ", from nodes " + rightN.block + " and " + leftN.block);
            root = parent;
            queue.add(parent);
        }
    }
    
    private void findFrequencies() {
        freqs = new HashMap<String, Integer>();
        for (String s : source) {
            if (freqs.containsKey(s)) {
                freqs.put(s, freqs.get(s)+1);
            } else {
                freqs.put(s, 1);
            }
        }
    }
    
    private void addInitialNodes(PriorityQueue<Node> queue) {
        for(String block : freqs.keySet()) {
            queue.add(new Node(freqs.get(block), block));
        }
    }
    
    private void createCodeMap() {
        codeMap = new BiDirectionalMap<>();
        searchTree(root, "");
    }

    private void searchTree(Node n, String code) {
        if (n.left == null && n.right == null) {
            codeMap.put(n.block, code);
        } else {
            searchTree(n.left, code + "0");
            searchTree(n.right, code + "1");
        }
    }
    
    
// --------- Printer-methods ---------
    
    public void printCodeMap() {
        for(String s : codeMap.getESet()) {
            System.out.println(s + " codes to: " + codeMap.getV(s));
        }
    }
    
    public void printFreqs() {
        System.out.println("Frequency of blocks:");
        for(String s : freqs.keySet()) {
            System.out.println(s + ": " + freqs.get(s));
        }
    }
    
    public int printCompressionRatio() {
        int sourceLength = 0, compressedLength = 0;
        for(String s : source) {
            sourceLength += s.length();
        }
        for(String s : compressed) {
            compressedLength += s.length();
        }
        
        System.out.println("Bit-length before huffman-coding: " + sourceLength);
        System.out.println("Bit-length after huffman-coding: " + compressedLength);
        System.out.printf("Huffman-coding compression ratio: %.2f%%\n", (1.0-(double)compressedLength/sourceLength)*100);
        return compressedLength;
    }
    
    
 // --------- Node class ---------
    
    private class Node implements Comparable<Node> {

        int freq;
        String block;
        Node left;
        Node right;
        
        public Node(int freq, String bitBlock) {
            this.freq = freq;
            this.block = bitBlock;
        }
        
        public Node(int freq) {
            this.freq = freq;
        }
        
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.freq, other.freq);
        }
        
    }
}
