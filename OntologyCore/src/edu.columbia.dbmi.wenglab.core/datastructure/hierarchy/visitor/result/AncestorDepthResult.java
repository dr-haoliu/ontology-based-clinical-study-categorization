package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.result;

/**
 * A result class that stores the longest path hierarchical depth for the given
 * hierarchy node
 *
 */
public class AncestorDepthResult<T>  {
    private final T node;
    
    private final int depth;
    
    public AncestorDepthResult(T node, int depth) {
        this.node = node;
        this.depth = depth;
    }
    
    public T getNode() {
        return node;
    }
    
    public int getDepth() {
        return depth;
    }
}
