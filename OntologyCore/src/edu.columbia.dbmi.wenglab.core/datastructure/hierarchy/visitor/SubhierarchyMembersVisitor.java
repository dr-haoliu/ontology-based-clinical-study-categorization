package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

import java.util.HashSet;
import java.util.Set;

/**
 * A visitor for obtaining all of the nodes in theHierarchy
 *
 */
public class SubhierarchyMembersVisitor<T> extends HierarchyVisitor<T> {
    
    private final Set<T> members = new HashSet<>();

    public SubhierarchyMembersVisitor(Hierarchy<T> theHierarchy) {
        super(theHierarchy);
    }
    
    @Override
    public void visit(T node) {
        members.add(node);
    }
    
    public Set<T> getSubhierarchyMembers() {
        return members;
    }
}
