
package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.AllPathsToNodeVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.HierarchyDepthVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.AncestorHierarchyBuilderVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.HierarchyVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.LowestCommonAncestorVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.RetrieveLeavesVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.SubhierarchyBuilderVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.SubhierarchyMembersVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.SubhierarchySizeVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.TopRootVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.TopologicalListVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.TopologicalVisitor;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.visitor.result.AncestorDepthResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * A rooted Directed Acyclic Graph (DAG)
 *
 */
public class Hierarchy<T> {
    
    private final Graph<T> baseGraph;
    
    private final Set<T> roots;

    /**
     * Initializes a singly rooted hierarchy
     * @param root 
     */
    public Hierarchy(T root) {
        this(Collections.singleton(root));
    }
    
    /**
     * Initializes a multirooted hierarchy
     * @param roots 
     */
    public Hierarchy(Set<T> roots) {
        this.baseGraph = new Graph<>();
        this.roots = new HashSet<>(roots);
        
        roots.forEach((root) -> {
            baseGraph.addNode(root);
        });
    }
    
    /**
     * Deep copy constructor
     * 
     * @param otherHierarchy 
     */
    public Hierarchy(Hierarchy<T> otherHierarchy) {
        this(otherHierarchy.getRoots(), otherHierarchy);
    }
    
    /**
     * Initializes a singly rooted subhierarchy within the source hierarchy
     * @param root
     * @param sourceHierarchy 
     */
    public Hierarchy(T root, Hierarchy<T> sourceHierarchy) {
        this(Collections.singleton(root), sourceHierarchy);
    }
       
    /**
     * Creates the subhierarchy (within the source hierarchy) according to given roots
     * @param roots
     * @param sourceHierarchy 
     */
    public Hierarchy(Set<T> roots, Hierarchy<T> sourceHierarchy) {
        this(roots);
        
        SubhierarchyBuilderVisitor<T> subhierarchyBuilderVisitor = new SubhierarchyBuilderVisitor<>(sourceHierarchy, roots);
        sourceHierarchy.topologicalDownInSubhierarchy(roots, subhierarchyBuilderVisitor);
        
        subhierarchyBuilderVisitor.getResult().getEdges().forEach( (edge) -> {
            addEdge(edge);
        });
    }
    
    public boolean isSinglyRooted() {
        return roots.size() == 1;
    }

    /** 
     * Returns the set of roots of the hierarchy
     * @return 
     */
    public Set<T> getRoots() {
        return roots;
    }
    
    /**
     * Convenience method for getting the root of a hierarchy that has only one root.
     * Otherwise returns the first root in the set of roots.
     * @return 
     */
    public T getRoot() {
        return roots.iterator().next();
    }
    
    /**
     * Returns the number of nodes in the hierarchy
     * @return 
     */
    public int size() {
        return baseGraph.getNodes().size();
    }
    
    public void addNode(T node) {
        baseGraph.addNode(node);
    }
    
    /**
     * Adds an IS A relationship between from and to. e.g., from IS A to.
     * 
     * @param from The child concept
     * @param to The parent concept
     */
    public final void addEdge(T from, T to) {
        baseGraph.addEdge(from, to);
    }
    
    /**
     * Add an IS A relationship into the hierarchy
     * 
     * @param edge The edge
     */
    public final void addEdge(Edge<T> edge) {
        this.addEdge(edge.getSource(), edge.getTarget());
    }
    
    /**
     * Copies the given hierarchy into this hierarchy
     * 
     * @param hierarchy 
     */
    public void addHierarchy(Hierarchy<T> hierarchy) {
        roots.addAll(hierarchy.roots);
        
        addAllHierarchicalRelationships(hierarchy);
    }
    
    /**
     * Adds the hierarchical relationships (and nodes) from the given hierarchy
     * into this hierarchy, without copying the roots.
     * 
     * @param hierarchy 
     */
    public void addAllHierarchicalRelationships(Hierarchy<T> hierarchy) {
        
        Set<Edge<T>> otherEdges = hierarchy.getEdges();
        
        hierarchy.getNodes().forEach( (node) -> {
            addNode(node);
        });
                
        otherEdges.forEach( (edge) -> {
            addEdge(edge);
        });
    }
    
    /**
     * Returns the subhierarchy rooted at the given node.
     * 
     * @param root
     * @return 
     */
    public Hierarchy<T> getSubhierarchyRootedAt(T root) {
        return getSubhierarchyRootedAt(Collections.singleton(root));
    }
    
    /**
     * Returns the subhierarchy rooted at the given nodes.
     * 
     * @param roots
     * @return 
     */
    
    public Hierarchy<T> getSubhierarchyRootedAt(Set<T> roots) {
        return new Hierarchy<>(roots, this);
    }
    
    /**
     * Returns the set of edges in the hierarchy
     * 
     * @return 
     */
    public Set<Edge<T>> getEdges() {
        return baseGraph.getEdges();
    }
    
    /**
     * Returns all of the nodes (entries) in the hierarchy
     * 
     * @return 
     */
    public Set<T> getNodes() {
        return baseGraph.getNodes();
    }
    
    /**
     * Returns the set of child nodes of the given node
     * 
     * @param node
     * @return 
     */
    public Set<T> getChildren(T node) {
        return baseGraph.getIncomingEdges(node);
    }
    
    /**
     * Returns the set of parent nodes of the given node
     * 
     * @param node
     * @return 
     */
    public Set<T> getParents(T node) {
        return baseGraph.getOutgoingEdges(node);
    }
    
    /**
     * Determines if the hierarchy contains the given node
     * @param node
     * @return 
     */
    public boolean contains(T node) {
        return baseGraph.contains(node);
    }
    
    /**
     * Performs a breadth-first search down the hierarchy starting from the given node
     * 
     * @param startingPoint
     * @param visitor 
     */
    public void BFSDown(T startingPoint, HierarchyVisitor<T> visitor) {
        BFSDown(Collections.singleton(startingPoint), visitor);
    }
    
    /**
     * Performs a breadth-first search down the hierarchy starting from the given nodes
     * 
     * @param startingPoints
     * @param visitor 
     */
    public void BFSDown(Set<T> startingPoints, HierarchyVisitor<T> visitor) {
        Queue<T> queue = new ArrayDeque<>();
        queue.addAll(startingPoints);

        Set<T> visited = new HashSet<>();
        visited.addAll(startingPoints);

        while (!queue.isEmpty() && !visitor.isFinished()) {
            T node = queue.remove();

            visitor.visit(node);

            Set<T> nodeChildren = this.getChildren(node);

            nodeChildren.forEach((T child) -> {
                if (!visited.contains(child)) {
                    queue.add(child);
                    visited.add(child);
                }
            });
        }
    }

    /**
     * Performs a breadth-first search UP the hierarchy starting from the given node
     * 
     * @param startingPoint
     * @param visitor 
     */
    public void BFSUp(T startingPoint, HierarchyVisitor<T> visitor) {
        BFSUp(Collections.singleton(startingPoint), visitor);
    }
    
    /**
     * Performs a breadth-first search UP the hierarchy starting from the given nodes
     * 
     * @param startingPoints
     * @param visitor 
     */
    public void BFSUp(Set<T> startingPoints, HierarchyVisitor<T> visitor) {
        Queue<T> queue = new ArrayDeque<>(startingPoints);

        Set<T> visited = new HashSet<>(startingPoints);

        while (!queue.isEmpty() && !visitor.isFinished()) {
            T node = queue.remove();

            visitor.visit(node);

            Set<T> nodeParents = this.getParents(node);

            nodeParents.forEach((T parent) -> {
                if (!visited.contains(parent)) {
                    queue.add(parent);
                    visited.add(parent);
                }
            });
        }
    }
    
    /**
     * Performs a topological traversal down the hierarchy
     * 
     * @param visitor 
     */
    public void topologicalDown(TopologicalVisitor<T> visitor) {
        topologicalDownInSubhierarchy(getRoots(), visitor);
    }
    
    /**
     * Performs a topological traversal down a subhierarchy rooted at the starting point
     * 
     * @param startingPoint
     * @param visitor 
     */
    public void topologicalDownInSubhierarchy(T startingPoint, TopologicalVisitor<T> visitor) {
        topologicalDownInSubhierarchy(Collections.singleton(startingPoint), visitor);
    }
    
    public void topologicalDownInSubhierarchy(Set<T> startingPoints, TopologicalVisitor<T> visitor) {
        Set<T> subhierarchy = this.getDescendants(startingPoints);
        subhierarchy.addAll(startingPoints);

        HashMap<T, Integer> parentCountInSubhierarchy = new HashMap<>();

        subhierarchy.forEach((T node) -> {
            int parentCount = 0;

            Set<T> nodeParents = this.getParents(node);

            for (T parent : nodeParents) {
                if (subhierarchy.contains(parent)) {
                    parentCount++;
                }
            }

            parentCountInSubhierarchy.put(node, parentCount);
        });

        Queue<T> queue = new ArrayDeque<>();
        queue.addAll(startingPoints);

        while (!queue.isEmpty() && !visitor.isFinished()) {
            T node = queue.remove();

            visitor.visit(node);

            Set<T> nodeChildren = this.getChildren(node);

            nodeChildren.forEach((T child) -> {
                if (parentCountInSubhierarchy.get(child) == 1) {
                    queue.add(child);
                } else {
                    parentCountInSubhierarchy.put(child, parentCountInSubhierarchy.get(child) - 1);
                }
            });
        }
    }
    
    public void topologicalUp(T startingPoint, TopologicalVisitor<T> visitor) {
        topologicalUp(Collections.singleton(startingPoint), visitor);
    }
    
    public void topologicalUp(Set<T> startingPoints, TopologicalVisitor<T> visitor) {
        
        Set<T> subhierarchyNodes = this.getAncestors(startingPoints);
        
        subhierarchyNodes.addAll(startingPoints);

        HashMap<T, Integer> childCountInSubhierarchy = new HashMap<>();

        subhierarchyNodes.forEach((node) -> {
            int childCount = 0;

            Set<T> children = this.getChildren(node);

            for (T child : children) {
                if (subhierarchyNodes.contains(child)) {
                    childCount++;
                }
            }

            childCountInSubhierarchy.put(node, childCount);
        });

        Queue<T> queue = new ArrayDeque<>();
        queue.addAll(startingPoints);

        while (!queue.isEmpty() && !visitor.isFinished()) {
            T node = queue.remove();

            visitor.visit(node);

            Set<T> nodeParents = this.getParents(node);

            nodeParents.forEach((parent) -> {
                if (childCountInSubhierarchy.get(parent) == 1) {
                    queue.add(parent);
                } else {
                    childCountInSubhierarchy.put(parent, childCountInSubhierarchy.get(parent) - 1);
                }
            });
        }
    }

    /**
     * Returns the number of descendants of the given node
     * @param node
     * @return 
     */
    public int countDescendants(T node) {
        SubhierarchySizeVisitor<T> visitor = new SubhierarchySizeVisitor<>(this);
        
        this.BFSDown(node, visitor);
        
        return visitor.getDescandantCount() - 1;
    }
    
    /**
     * Returns the set of descendants of the given node
     * @param node
     * @return 
     */
    public Set<T> getDescendants(T node) {
        return getDescendants(Collections.singleton(node));
    }
    
        /**
     * Returns the set of descendants of the given node
     * @param nodes
     * @return 
     */
    public Set<T> getDescendants(Set<T> nodes) {
        SubhierarchyMembersVisitor<T> visitor = new SubhierarchyMembersVisitor<>(this);
        
        this.BFSDown(nodes, visitor);
        
        Set<T> members = visitor.getSubhierarchyMembers();
        members.removeAll(nodes);
        
        return members;
    }
    
    /**
     * Returns the roots of the top-level subhierarchies that the given node belongs to
     * @param node
     * @return 
     */
    public Set<T> getMemberSubhierarchyRoots(T node) {
        TopRootVisitor<T> visitor = new TopRootVisitor<>(this);
                
        this.BFSUp(node, visitor);
        
        return visitor.getRoots();
    }
    
    /**
     * Returns a hierarchy of the ancestors of the given node
     * @param node
     * @return 
     */
    public Hierarchy<T> getAncestorHierarchy(T node) {
        return getAncestorHierarchy(Collections.singleton(node));
    }
    
    /**
     * Returns a hierarchy of the ancestors of the given nodes
     * @param nodes
     * @return 
     */
    public Hierarchy<T> getAncestorHierarchy(Set<T> nodes) {
        Set<T> ancestorRoots = new HashSet<>();

        nodes.forEach( (node) -> {
            
            Set<T> subhierarchies = getMemberSubhierarchyRoots(node);
            
            if(subhierarchies.isEmpty()) {
                ancestorRoots.add(node);
            } else {
                ancestorRoots.addAll(subhierarchies);
            }
        });

        AncestorHierarchyBuilderVisitor<T> ancestorHierarchy = 
                new AncestorHierarchyBuilderVisitor<>(this, 
                new Hierarchy<T>(ancestorRoots));
        
        this.BFSUp(nodes, ancestorHierarchy);
        
        return ancestorHierarchy.getAncestorHierarchy();
    }
    
    public Set<T> getAncestors(T node) {
        return getAncestors(Collections.singleton(node));
    }
    
    public Set<T> getAncestors(Set<T> nodes) {
        Set<T> ancestors = getAncestorHierarchy(nodes).getNodes();
        ancestors.removeAll(nodes);

        return ancestors;
    }
    
    
    /**
     * Returns all paths from the roots of the hierarchy to the given node
     * 
     * @param node
     * @return 
     */
    public ArrayList<ArrayList<T>> getAllPathsTo(T node) {
        Hierarchy<T> ancestorHierarchy = this.getAncestorHierarchy(node);
        
        AllPathsToNodeVisitor<T> visitor = new AllPathsToNodeVisitor<>(this, node);
        
        ancestorHierarchy.topologicalDown(visitor);
        
        return visitor.getAllPaths();
    }
    
    /**
     * Returns all of the nodes in this hierarchy in a topological order
     * 
     * @return 
     */
    public ArrayList<T> getTopologicalOrdering() {
        TopologicalListVisitor<T> visitor = new TopologicalListVisitor<>(this);
        
        this.topologicalDown(visitor);
        
        return visitor.getTopologicalList();
    }
        
    /**
     * Returns a subhierarchy containing all of the descendants within a given longest-path distance
     * @param node
     * @param maxDistance
     * @return 
     */
    public Hierarchy<T> getDescendantHierarchyWithinDistance(T node, int maxDistance) {
        Hierarchy<T> hierarchy = new Hierarchy<>(node);
        
        int levelProcessed = 0;
                
        Queue<T> levelQueue = new ArrayDeque<>();
        levelQueue.add(node);
        
        Set<T> processed = new HashSet<>();

        while(!levelQueue.isEmpty() && levelProcessed < maxDistance) {
            levelProcessed++;
            
            processed.addAll(levelQueue);
            
            Queue<T> nextLevelQueue = new ArrayDeque<>();
            
            while(!levelQueue.isEmpty()) {
                T levelNode = levelQueue.remove();
                
                Set<T> nodeChildren = this.getChildren(levelNode);
                
                nodeChildren.forEach((T child) -> {
                    if(!processed.contains(child)) {
                        nextLevelQueue.add(child);
                    }
                    
                    hierarchy.addEdge(child, levelNode);
                });
            }
            
            levelQueue = nextLevelQueue;
        }
        
        return hierarchy;
    }
    
    /**
     * Returns a topologically sorted list of nodes within the given longest-path distance
     * 
     * @param node
     * @param distance
     * @return 
     */
    public ArrayList<AncestorDepthResult<T>> getTopologicalDescendantListWithinDistance(T node, int distance) {
        Hierarchy<T> hierarchyWithinDistance = this.getDescendantHierarchyWithinDistance(node, distance);
        
        HierarchyDepthVisitor<T> visitor = new HierarchyDepthVisitor<>(hierarchyWithinDistance);
        
        hierarchyWithinDistance.topologicalDown(visitor);
        
        return visitor.getResult();
    }
    
    /**
     * Returns the siblings (nodes with at least one of the same parents) of the given node
     * @param node
     * @return 
     */
    public Set<T> getSiblings(T node) {
        Set<T> nodeParents = this.getParents(node);
        
        Set<T> siblings = new HashSet<>();
        
        nodeParents.forEach( (parent) -> {
            siblings.addAll(getChildren(parent));
        });
        
        siblings.remove(node);
        
        return siblings;
    }
    
    /**
     * Returns the strict siblings (siblings with all of the same parents) of the given node
     * 
     * @param node
     * @return 
     */
    public Set<T> getStrictSiblings(T node) {
        Set<T> nodeParents = this.getParents(node);
        
        Set<T> strictSiblings = new HashSet<>();
        
        nodeParents.forEach( (parent) -> {
            Set<T> siblings = getChildren(parent);
            
            siblings.forEach( (sibling) -> {
               if(!siblings.equals(node) && nodeParents.equals(getParents(sibling))) {
                   strictSiblings.add(sibling);
               }
            });
        });
        
        strictSiblings.remove(node);
        
        return strictSiblings;
    }
    
    /**
     * Returns the leaves (external nodes) of the hierarchy
     * 
     * @return 
     */
    public Set<T> getLeaves() {
        RetrieveLeavesVisitor<T> leavesVisitor = new RetrieveLeavesVisitor<>(this);
        
        this.BFSDown(getRoots(), leavesVisitor);
        
        return leavesVisitor.getLeaves();
    }
    
    /**
     * Returns the internal nodes of the hierarchy
     * 
     * @return 
     */
    public Set<T> getInternalNodes() {
        Set<T> leaves = getLeaves();
        
        Set<T> internalNodes = this.getNodes();
        internalNodes.removeAll(leaves);
        
        return internalNodes;
    }
    
    public Map<T, Integer> getAllLongestPathDepths() {
        HierarchyDepthVisitor<T> hierarchyDepthVisitor = new HierarchyDepthVisitor(this);
        
        this.topologicalDown(hierarchyDepthVisitor);
        
        return hierarchyDepthVisitor.getAllDepths();
    }
    
    public Set<T> lowestCommonAncestors(Set<T> nodes) {
        Hierarchy<T> ancestorHierarchy = this.getAncestorHierarchy(nodes);
        
        LowestCommonAncestorVisitor<T> visitor = new LowestCommonAncestorVisitor(ancestorHierarchy, nodes);
        ancestorHierarchy.topologicalUp(nodes, visitor);

        return visitor.getLowestCommonAncestors();
    }
    
    public boolean isAncestorOf(T potentialAncestor, T node) {
        return this.getAncestors(node).contains(potentialAncestor);
    }
    
    public boolean isDescendantOf(T potentialDescendant, T node) {
        return this.getDescendants(node).contains(potentialDescendant);
    }
}
