package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A visitor that returns all of the paths from the root of theHierarchy to the 
 * specified end point
 *
 */
public class AllPathsToNodeVisitor<T> extends TopologicalVisitor<T> {
    private ArrayList<ArrayList<T>> allPaths = new ArrayList<>();
    
    private Map<T, ArrayList<ArrayList<T>>> pathMap = new HashMap<>();
    
    private final T endPoint;
    
    public AllPathsToNodeVisitor(Hierarchy<T> theHierarchy, T endPoint) {
        super(theHierarchy);
        
        this.endPoint = endPoint;
        
        theHierarchy.getRoots().forEach((e) -> {
            ArrayList<T> startingList = new ArrayList<>();
            startingList.add(e);

            ArrayList<ArrayList<T>> rootPath = new ArrayList<>();
            rootPath.add(startingList);

            pathMap.put(e, rootPath);
        });
    }
    
    @Override
    public void visit(T node) {
        Hierarchy<T> theHierarchy = super.getHierarchy();
        
        if(theHierarchy.getRoots().contains(node)) {
            return;
        }
        
        Set<T> parents = theHierarchy.getParents(node);

        ArrayList<ArrayList<T>> pathsToConcept = new ArrayList<>();
        
        parents.forEach( (parent) -> {
            ArrayList<ArrayList<T>> parentPaths = (ArrayList<ArrayList<T>>) pathMap.get(parent).clone();

            parentPaths.forEach( (parentPath) -> {
                ArrayList<T> path = (ArrayList<T>) parentPath.clone();
                path.add(node);

                pathsToConcept.add(path);
            });
        });

        if (node.equals(endPoint)) {
            this.allPaths = pathsToConcept;
        } else {
            pathMap.put(node, pathsToConcept);
        }

    }
    
    public ArrayList<ArrayList<T>> getAllPaths() {
        return allPaths;
    }
}
