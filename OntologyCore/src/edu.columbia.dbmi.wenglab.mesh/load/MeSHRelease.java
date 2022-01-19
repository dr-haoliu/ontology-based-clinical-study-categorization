package edu.columbia.dbmi.wenglab.mesh.load;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.core.ontology.Ontology;
import edu.columbia.dbmi.wenglab.mesh.concept.Description;
import edu.columbia.dbmi.wenglab.mesh.concept.MeSHConcept;
import edu.columbia.dbmi.wenglab.core.ontology.OntologySearcher;


import java.util.*;
import java.util.stream.Collectors;

public class MeSHRelease extends Ontology implements OntologySearcher<MeSHConcept> {

        private final Map<Long, MeSHConcept> concepts = new HashMap();
        private final HashMap<Character, Integer> startingIndex = new HashMap();
        private final ArrayList<MeSHRelease.DescriptionEntry> descriptions;



        public MeSHRelease(Hierarchy<MeSHConcept> activeConceptHierarchy, Set<MeSHConcept> allConcepts) {
            super(activeConceptHierarchy);
            this.descriptions = new ArrayList();

            activeConceptHierarchy.getChildren(activeConceptHierarchy.getRoot()).forEach((root) -> {
                Hierarchy<MeSHConcept> hierarchy = activeConceptHierarchy.getSubhierarchyRootedAt(root);
            });

            allConcepts.forEach((concept) -> {
                this.concepts.put(concept.getID(), concept);
                concept.getDescriptions().forEach((d) -> {
                    this.descriptions.add(new MeSHRelease.DescriptionEntry(d, concept));
                });
            });

            Collections.sort(this.descriptions, (a, b) -> {
                return a.description.getTerm().compareToIgnoreCase(b.description.getTerm());
            });

            char lastChar = Character.toLowerCase(((MeSHRelease.DescriptionEntry)this.descriptions.get(0)).description.getTerm().charAt(0));

            for(int c = 1; c < this.descriptions.size(); ++c) {
                String term = ((MeSHRelease.DescriptionEntry)this.descriptions.get(c)).description.getTerm();
                char curChar = Character.toLowerCase(term.charAt(0));
                if (curChar != lastChar) {
                    if (curChar >= 'a' && curChar <= 'z') {
                        this.startingIndex.put(curChar, c);
                    }

                    lastChar = curChar;
                }
            }


        }


        public Hierarchy<MeSHConcept> getConceptHierarchy() {
            return super.getConceptHierarchy();
        }

        public Optional<MeSHConcept> getConceptFromId(Long id) {
            return Optional.ofNullable(this.concepts.get(id));
        }

        public Set<MeSHConcept> getAllConcepts() {
            return (Set)this.concepts.values().stream().collect(Collectors.toSet());
        }


        public Set<MeSHConcept> searchExact(String term) {
            term = term.toLowerCase();
            if (term.length() < 3) {
                return Collections.emptySet();
            } else {
                char firstChar = Character.toLowerCase(term.charAt(0));
                Set<MeSHConcept> results = new HashSet();
                int startIndex;
                if (firstChar < 'a') {
                    startIndex = 0;
                } else if (firstChar > 'z') {
                    startIndex = (Integer)this.startingIndex.get('z');
                } else {
                    startIndex = (Integer)this.startingIndex.get(firstChar);
                }

                boolean withinIndexBounds = firstChar >= 'a' && firstChar <= 'z';

                for(int c = startIndex; c < this.descriptions.size(); ++c) {
                    MeSHRelease.DescriptionEntry entry = (MeSHRelease.DescriptionEntry)this.descriptions.get(c);
                    char descFirstChar = Character.toLowerCase(entry.description.getTerm().charAt(0));
                    if (withinIndexBounds) {
                        if (descFirstChar == firstChar && entry.description.getTerm().equalsIgnoreCase(term) ) {
                            results.add(entry.concept);
                        }
                    } else if (firstChar < 'a' && descFirstChar == 'a') {
                        break;
                    }
                }

                return results;
            }
        }

        public Set<MeSHConcept> searchStarting(String term) {
            term = term.toLowerCase();
            if (term.length() < 3) {
                return Collections.emptySet();
            } else {
                char firstChar = Character.toLowerCase(term.charAt(0));
                Set<MeSHConcept> results = new HashSet();
                int startIndex;
                if (firstChar < 'a') {
                    startIndex = 0;
                } else if (firstChar > 'z') {
                    startIndex = (Integer)this.startingIndex.get('z');
                } else {
                    startIndex = (Integer)this.startingIndex.get(firstChar);
                }

                for(int c = startIndex; c < this.descriptions.size(); ++c) {
                    MeSHRelease.DescriptionEntry entry = (MeSHRelease.DescriptionEntry)this.descriptions.get(c);
                    char descFirstChar = Character.toLowerCase(entry.description.getTerm().charAt(0));
                    if (firstChar >= 'a' && firstChar <= 'z') {
                        if (descFirstChar != firstChar) {
                            break;
                        }

                        if (entry.description.getTerm().toLowerCase().startsWith(term) ) {
                            results.add(entry.concept);
                        }
                    } else if (firstChar < 'a' && descFirstChar == 'a') {
                        break;
                    }
                }

                return results;
            }
        }

    public Set<MeSHConcept> searchAnywhere(String term) {
        term = term.toLowerCase();
        Set<MeSHConcept> results = new HashSet();
        Iterator var3 = this.descriptions.iterator();

        while(var3.hasNext()) {
            MeSHRelease.DescriptionEntry entry = (MeSHRelease.DescriptionEntry)var3.next();
            if (entry.description.getTerm().toLowerCase().contains(term)) {
                results.add(entry.concept);
            }
        }

        return results;
    }



        public Set<MeSHConcept> searchID(String query) {
            HashSet results = new HashSet();

            try {
                long id = Long.parseLong(query);
                if (this.concepts.containsKey(id)) {
                    results.add(this.concepts.get(id));
                }
            } catch (NumberFormatException var5) {
            }

            return results;
        }

        public boolean supportsStatedRelationships() {
            return false;
        }

    private class DescriptionEntry {
        public Description description;
        public MeSHConcept concept;

        public DescriptionEntry(Description d, MeSHConcept c) {
            this.description = d;
            this.concept = c;
        }
    }



}
