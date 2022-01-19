package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A visitor for finding the lowest common ancestor(s) for a set of starting points
 *
 */
public class LowestCommonAncestorVisitor<T> extends TopologicalVisitor<T> {
    
    private final Map<T, Integer> subhierarchyStartingPoints = new HashMap<>();
    
    private final Set<T> startingPoints;
    
    private final Set<T> lowestCommonAncestors = new HashSet<>();
    
    public LowestCommonAncestorVisitor(Hierarchy<T> theHierarchy, Set<T> startingPoints) {
        super(theHierarchy);
        
        this.startingPoints = startingPoints;
    }

    @Override
    public void visit(T node) {
        Hierarchy<T> theHierarchy = super.getHierarchy();
        
        int nodeCount = 0;
        
        if(startingPoints.contains(node)) {
            nodeCount = 1;
        }

        Set<T> children = theHierarchy.getChildren(node);
        
        int max = 0;

        for (T child : children) {
            int subhierarchyCount = subhierarchyStartingPoints.get(child);;
            
            nodeCount += subhierarchyCount;
            
            if(subhierarchyCount > max) {
                max = subhierarchyCount;
            }
        }
        
        subhierarchyStartingPoints.put(node, nodeCount);
        
        if(nodeCount == startingPoints.size() && max != startingPoints.size()) {
            lowestCommonAncestors.add(node);
        }
    }
    
    public Set<T> getLowestCommonAncestors() {
        return lowestCommonAncestors;
    }
}
