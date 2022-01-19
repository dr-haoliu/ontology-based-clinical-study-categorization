package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A visitor for getting all of the descendants of a given DAG node
 *
 */
public class DescendantsVisitor<T> extends TopologicalVisitor<T> {
    
    private final Map<T, Set<T>> descendants = new HashMap<>();
    
    public DescendantsVisitor(Hierarchy<T> theHierarchy) {
        super(theHierarchy);
    }

    @Override
    public void visit(T node) {
        Set<T> nodeDescendants = new HashSet<>();
        
        Set<T> children = super.getHierarchy().getChildren(node);
        
        nodeDescendants.addAll(children);
                
        children.forEach((child) -> {
            nodeDescendants.addAll(descendants.getOrDefault(child, Collections.emptySet()));
        });
        
        descendants.put(node, nodeDescendants);
    }
    
    public Map<T, Set<T>> getDescendants() {
        return descendants;
    }
    
    public Map<T, Integer> getDescendantCounts() {
        Map<T, Integer> descendantCounts = new HashMap<>();
        
        descendants.forEach( (node, nodeDescendants) -> {
            descendantCounts.put(node, nodeDescendants.size());
        });
        
        return descendantCounts;
    }
}
