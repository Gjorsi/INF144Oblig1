package compression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {

    private HashMap<String, Integer> freqs;
    private ArrayList<String> source;
    private Node root;
    
    public Huffman(ArrayList<String> input) {
        this.source = input;
    }
    
    public void code() {
        createHuffmanTree();
    }

    private void createHuffmanTree() {
        findFrequencies();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        addInitialNodes(queue);
        
        while (queue.size() > 1) {
            Node rightN = queue.poll();
            Node leftN = queue.poll();
            
            Node parent = new Node(leftN.freq + rightN.freq);
            root = parent;
            queue.add(parent);
        }
    }

    private void addInitialNodes(PriorityQueue<Node> queue) {
        for(String block : freqs.keySet()) {
            queue.add(new Node(freqs.get(block), block));
        }
    }

    private void findFrequencies() {
        for (String s : source) {
            if (freqs.containsKey(s)) {
                freqs.put(s, freqs.get(s)+1);
            } else {
                freqs.put(s, 1);
            }
        }
    }

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
