package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

/**
 * A visitor for determining the number of nodes in theHierarchy
 *
 */
public class SubhierarchySizeVisitor<T> extends HierarchyVisitor<T> {
    
    private int count = 0;
    
    public SubhierarchySizeVisitor(Hierarchy<T> theHierarchy) {
        super(theHierarchy);
    }
    
    @Override
    public void visit(T node) {
        count++;
    }
    
    public int getDescandantCount() {
        return count;
    }
}
