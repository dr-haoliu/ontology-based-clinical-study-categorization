package edu.columbia.dbmi.wenglab.sno.sctdatasource;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.core.ontology.Ontology;

import edu.columbia.dbmi.wenglab.core.ontology.OntologySearcher;
import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.Description;
import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.SCTConcept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A SNOMED CT release loaded from a local source
 *
 */
public class SCTRelease extends Ontology implements OntologySearcher<SCTConcept> {

    /**
     * An entry for a description, used for searching
     */
    private class DescriptionEntry {

        public Description description;
        public SCTConcept concept;

        public DescriptionEntry(Description d, SCTConcept c) {
            this.description = d;
            this.concept = c;
        }
    }

    private final Map<Long, SCTConcept> concepts = new HashMap<>();
    
    // TODO: This needs to go away and be replaced by something better.
    private final HashMap<Character, Integer> startingIndex = new HashMap<>();

    private final ArrayList<DescriptionEntry> descriptions;
    
    private final SCTReleaseInfo releaseInfo;
    
    private final Set<SCTConcept> subhierarchiesWithAttributeRels;
    
    private final Set<SCTConcept> attributeRelationshipTypes;

    public SCTRelease(
            SCTReleaseInfo releaseInfo,
            Hierarchy<SCTConcept> activeConceptHierarchy,
            Set<SCTConcept> allConcepts) {
        
        super(activeConceptHierarchy);
        
        this.releaseInfo = releaseInfo;
                
        this.descriptions = new ArrayList<>();
        
        this.subhierarchiesWithAttributeRels = new HashSet<>();
        

        activeConceptHierarchy.getChildren(activeConceptHierarchy.getRoot()).forEach( (root) -> {
            
            Hierarchy<SCTConcept> hierarchy = activeConceptHierarchy.getSubhierarchyRootedAt(root);
            
            if(hierarchy.getNodes().stream().anyMatch( (p -> !p.getAttributeRelationships().isEmpty()))) {
                subhierarchiesWithAttributeRels.add(root);
            }
        });
        
        allConcepts.forEach( (concept) -> {
            concepts.put(concept.getID(), concept);
            
            concept.getDescriptions().forEach((d) -> {
                descriptions.add(new DescriptionEntry(d, concept));
            });
        });
        
        this.attributeRelationshipTypes = activeConceptHierarchy.getSubhierarchyRootedAt(this.getConceptFromId(410662002l).get()).getNodes();

        Collections.sort(descriptions, (a, b) -> a.description.getTerm().compareToIgnoreCase(b.description.getTerm()));

        char lastChar = Character.toLowerCase(descriptions.get(0).description.getTerm().charAt(0));

        for (int c = 1; c < descriptions.size(); c++) {
            String term = descriptions.get(c).description.getTerm();

            char curChar = Character.toLowerCase(term.charAt(0));

            if (curChar != lastChar) {
                if (curChar >= 'a' && curChar <= 'z') {
                    startingIndex.put(curChar, c);
                }

                lastChar = curChar;
            }
        }
    }

    
    public SCTReleaseInfo getReleaseInfo() {
        return releaseInfo;
    }

    public Set<SCTConcept> getHierarchiesWithAttributeRelationships() {
        return subhierarchiesWithAttributeRels;
    }
    
    public Set<SCTConcept> getAvailableAttributeRelationships() {
        return attributeRelationshipTypes;
    }
    
    @Override
    public Hierarchy<SCTConcept> getConceptHierarchy() {
        return super.getConceptHierarchy();
    }

    public Optional<SCTConcept> getConceptFromId(long id) {
        return Optional.ofNullable(concepts.get(id));
    }
    
    /**
     * Returns all concepts in the release (active and retired)
     * 
     * @return 
     */
    public Set<SCTConcept> getAllConcepts() {
        return concepts.values().stream().collect(Collectors.toSet());
    }
    
    /**
     * Returns the set of active concepts
     * 
     * @return 
     */
    public Set<SCTConcept> getActiveConcepts() {
        return concepts.values().stream().filter((concept) -> {
           return concept.isActive();
        }).collect(Collectors.toSet());
    }
    
    /**
     * Returns the set of currently inactive concepts
     * 
     * @return 
     */
    public Set<SCTConcept> getInactiveConcepts() {
        return concepts.values().stream().filter((concept) -> {
            return !concept.isActive();
        }).collect(Collectors.toSet());
    }
    
    /**
     * Returns the set of primitive concepts
     * 
     * @return 
     */
    public Set<SCTConcept> getPrimitiveConcepts() {
        return concepts.values().stream().filter((concept) -> {
            return concept.isPrimitive();
        }).collect(Collectors.toSet());
    }
    
    /**
     * Returns the set of fully defined concepts
     * 
     * @return 
     */
    public Set<SCTConcept> getFullyDefinedConcepts() {
        return concepts.values().stream().filter((concept) -> {
            return !concept.isPrimitive();
        }).collect(Collectors.toSet());
    }
    
    @Override
    public Set<SCTConcept> searchExact(String term) {
        
        term = term.toLowerCase();
        
        if (term.length() < 3) {
            return Collections.emptySet();
        }

        char firstChar = Character.toLowerCase(term.charAt(0));

        Set<SCTConcept> results = new HashSet<>();

        int startIndex;

        if (firstChar < 'a') {
            startIndex = 0;
        } else if (firstChar > 'z') {
            startIndex = startingIndex.get('z');
        } else {
            startIndex = startingIndex.get(firstChar);
        }
                
        // TODO: Replace with binary search... Or a trie?
        
        boolean withinIndexBounds = (firstChar >= 'a' && firstChar <= 'z');

        for (int c = startIndex; c < descriptions.size(); c++) {
            DescriptionEntry entry = descriptions.get(c);
            
            char descFirstChar = Character.toLowerCase(entry.description.getTerm().charAt(0));

            if (withinIndexBounds) {
                if (descFirstChar == firstChar) {
                    if (entry.description.getTerm().equalsIgnoreCase(term)) {

                        if (entry.concept.isActive()) {
                            results.add(entry.concept);
                        }
                    }
                }
            } else {
                if (firstChar < 'a') {
                    if (descFirstChar == 'a') {
                        break;
                    }
                }
            }
        }

        return results;
    }

    @Override
    public Set<SCTConcept> searchStarting(String term) {
        //TODO: Replace with Binary Search or a Trie or something
        
        term = term.toLowerCase();
        
        if (term.length() < 3) {
            return Collections.emptySet();
        }

        char firstChar = Character.toLowerCase(term.charAt(0));

        Set<SCTConcept> results = new HashSet<>();

        int startIndex;

        if (firstChar < 'a') {
            startIndex = 0;
        } else if (firstChar > 'z') {
            startIndex = startingIndex.get('z');
        } else {
            startIndex = startingIndex.get(firstChar);
        }

        for (int c = startIndex; c < descriptions.size(); c++) {
            DescriptionEntry entry = descriptions.get(c);
            
            char descFirstChar = Character.toLowerCase(entry.description.getTerm().charAt(0));

            if (firstChar >= 'a' && firstChar <= 'z') {
                if (descFirstChar == firstChar) {
                    if (entry.description.getTerm().toLowerCase().startsWith(term)) {
                        if (entry.concept.isActive()) {
                            results.add(entry.concept);
                        }
                    }
                } else {
                    break;
                }
            } else {
                if(firstChar < 'a') {
                    if(descFirstChar == 'a') {
                        break;
                    }
                }
            }
        }
        
        return results;
    }

    
    @Override
    public Set<SCTConcept> searchAnywhere(String term) {

        term = term.toLowerCase();

        Set<SCTConcept> results = new HashSet<>();

        for (DescriptionEntry entry : descriptions) {
            if (entry.description.getTerm().toLowerCase().contains(term)) {
                if (entry.concept.isActive()) {
                    results.add(entry.concept);
                }
            }
        }

        return results;
    }

    @Override
    public Set<SCTConcept> searchID(String query) {
        Set<SCTConcept> results = new HashSet<>();
        
        try {
            long id = Long.parseLong(query);
            
            if(concepts.containsKey(id)) {
                results.add(concepts.get(id));
            }
            
        } catch(NumberFormatException nfe) {
            
        }
        
        return results;
    }

    /**
     * Indicates if the given release includes stated relationships
     * 
     * @return 
     */
    public boolean supportsStatedRelationships() {
        return false;
    }
}
