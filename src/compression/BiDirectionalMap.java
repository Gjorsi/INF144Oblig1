package compression;

import java.util.HashMap;
import java.util.Set;

public class BiDirectionalMap<E extends Comparable<E>, V extends Comparable<V>> {
    private HashMap<E, V> EtoV;
    private HashMap<V, E> VtoE;
    
    public BiDirectionalMap() {
        EtoV = new HashMap<>();
        VtoE = new HashMap<>();
    }
    
    public void put(E e, V v) {
        EtoV.put(e, v);
        VtoE.put(v, e);
    }
    
    public boolean containsE(E e) {
        return EtoV.containsKey(e);
    }
    
    public boolean containsV(V v) {
        return VtoE.containsKey(v);
    }    
    
    public E getE(V v) {
        return VtoE.get(v);
    }
    
    public V getV(E e) {
        return EtoV.get(e);
    }
    
    public int size() {
        return EtoV.size();
    }
    
    public Set<E> getESet() {
        return EtoV.keySet();
    }
}
