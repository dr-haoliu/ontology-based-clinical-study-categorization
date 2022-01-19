package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

/**
 * Base class for defining a visit operation for a traversal of a hierarchy
 *
 */
public abstract class HierarchyVisitor<T> {
    
    private final Hierarchy<T> theHierarchy;
    
    private boolean finished = false;
    
    public HierarchyVisitor(Hierarchy<T> theHierarchy) {
        this.theHierarchy = theHierarchy;
    }
    
    public Hierarchy<T> getHierarchy() {
        return theHierarchy;
    }
    
    public abstract void visit(T node);
    
    public boolean isFinished() {
        return finished;
    }
}
