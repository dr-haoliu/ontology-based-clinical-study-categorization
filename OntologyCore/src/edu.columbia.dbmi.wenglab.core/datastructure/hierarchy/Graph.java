
package edu.columbia.dbmi.wenglab.core.datastructure.hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A directed graph
 *
 */
public class Graph<T> {
    
    private final Map<T, Set<T>> outgoingEdges = new HashMap<>();
    private final Map<T, Set<T>> incomingEdges = new HashMap<>();
    
    public Graph() {
        
    }
    
    public Graph(Set<Edge<T>> edges) {
        edges.forEach( (edge) -> {
            addEdge(edge);
        });
    }
    
    public void addNode(T node) {
        
        // Initial capacities are based on general ontology hierarchy DAG metrics
        
        if(!outgoingEdges.containsKey(node)) {
            outgoingEdges.put(node, new HashSet<>(2));
        }
        
        if(!incomingEdges.containsKey(node)) {
            incomingEdges.put(node, new HashSet<>(3));
        }
    }
    
    public final void addEdge(Edge<T> edge) {
        addEdge(edge.getSource(), edge.getTarget());
    }
    
    public final void addEdge(T from, T to) {
        addNode(from);
        addNode(to);

        outgoingEdges.get(from).add(to);
        incomingEdges.get(to).add(from);
    }
    
    public Set<T> getIncomingEdges(T node) {
        return incomingEdges.getOrDefault(node, Collections.emptySet());
    }
    
    public Set<T> getOutgoingEdges(T node) {
        return outgoingEdges.getOrDefault(node, Collections.emptySet());
    }
    
    public Set<T> getNodes() {
        return new HashSet<>(outgoingEdges.keySet());
    }
    
    public Set<Edge<T>> getEdges() {
        Set<Edge<T>> edges = new HashSet<>();
        
        outgoingEdges.forEach((node, adjacentNodes) -> {
            adjacentNodes.forEach( (adjacentNode) -> {
                edges.add(new Edge(node, adjacentNode));
            });
        });

        return edges;
    }
    
    public boolean contains(T node) {
        return incomingEdges.containsKey(node);
    }
    
    public boolean contains(Edge<T> edge) {
        if(outgoingEdges.containsKey(edge.getSource())) {
            return outgoingEdges.get(edge.getSource()).contains(edge.getTarget());
        }
        
        return false;
    }
}
