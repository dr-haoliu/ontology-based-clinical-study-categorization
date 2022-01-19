package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

/**
 * A base class for all visitors that are executed during a topological 
 * traversal of theHierarchy
 *
 */
public abstract class TopologicalVisitor<T> extends HierarchyVisitor<T> {
    
    public TopologicalVisitor(Hierarchy<T> theHierarchy) {
        super(theHierarchy);
    }
    
}
