package edu.columbia.dbmi.wenglab.core.ontology;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Generic class representing an ontology
 */
public class Ontology<T extends Concept> {
    
    private final Hierarchy<T> conceptHierarchy;
    
    private final Map<Object, T> concepts = new HashMap<>();
    
    public Ontology(Hierarchy<T> conceptHierarchy) {
        this.conceptHierarchy = conceptHierarchy;
        
        conceptHierarchy.getNodes().forEach( (T concept) -> {
            concepts.put(concept.getID(), concept);
        });
    }
    
    public Hierarchy<T> getConceptHierarchy() {
        return conceptHierarchy;
    }
    
    public Optional<T> getConceptFromID(Object id) {
        return Optional.ofNullable(concepts.get(id));
    }
}
