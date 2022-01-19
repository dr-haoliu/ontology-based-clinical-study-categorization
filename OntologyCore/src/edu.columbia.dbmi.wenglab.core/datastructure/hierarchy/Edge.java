package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy;

/**
 * A directed edge between two graph nodes
 *
 */
public class Edge<T> {
    private final T from;
    private final T to;
    
    public Edge(T from, T to) {
        this.from = from;
        this.to = to;
    }
    
    public T getSource() {
        return from;
    }
    
    public T getTarget() {
        return to;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Edge) {
            Edge<T> other = (Edge<T>)o;
            
            return other.from.equals(from) && other.to.equals(to);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(from.hashCode() + to.hashCode());
    }
}
