package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

import java.util.Set;

/**
 * A visitor for building a subhierarchy rooted at the specified roots
 *
 */
public class SubhierarchyBuilderVisitor<T> extends TopologicalVisitor<T> {
    
    private final Hierarchy<T> subhierarchy;

    public SubhierarchyBuilderVisitor(Hierarchy<T> sourceHierarchy, Set<T> roots) {
        super(sourceHierarchy);
        
        this.subhierarchy = new Hierarchy<>(roots);
    }

    @Override
    public void visit(T node) {
        Hierarchy<T> theHierarchy = super.getHierarchy();
        
        theHierarchy.getChildren(node).forEach( (child) -> {
            subhierarchy.addEdge(child, node);
        });
    }
    
    public Hierarchy<T> getResult() {
        return subhierarchy;
    }
}
