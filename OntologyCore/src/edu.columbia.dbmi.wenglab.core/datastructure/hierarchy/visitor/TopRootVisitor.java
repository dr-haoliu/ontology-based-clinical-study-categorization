package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import java.util.HashSet;
import java.util.Set;

/**
 * For a multirooted hierarchy, returns the roots for the set of subhierarchies a given
 * DAG node belongs to
 * 
 *
 */
public class TopRootVisitor<T> extends HierarchyVisitor<T> {
    private final Set<T> roots = new HashSet<>();
    
    public TopRootVisitor(Hierarchy<T> theHierarchy) {
        super(theHierarchy);
    }
    
    @Override
    public void visit(T node) {
        Hierarchy<T> theHierarchy = super.getHierarchy();
        
        if(theHierarchy.getRoots().contains(node)) {
            roots.add(node);
        }
    }
    
    public Set<T> getRoots() {
        return roots;
    }
}
