package edu.columbia.dbmi.wenglab.mesh.concept;


import edu.columbia.dbmi.wenglab.core.ontology.Concept;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MeSHConcept extends Concept<Long> {

    private static final String UNSET_FSN = "FSN_NOT_SET";
    private final Set<Description> descList = new HashSet();
    private final Set<AttributeRelationship> lateralRelationships = new HashSet();

    private String fullySpecifiedName = "FSN_NOT_SET";

    public MeSHConcept(Long id) {
        super(id);
    }

    public MeSHConcept(Long id, Set<Description> descList){
        super(id);
        this.setDescriptions(descList);
    }

    public void setLateralRelationships(Set<AttributeRelationship> rels) {
        this.lateralRelationships.clear();
        this.lateralRelationships.addAll(rels);
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descList.clear();
        this.descList.addAll(descriptions);

        // ********************************
        // set fsn ; update later
        Iterator var2 = this.descList.iterator();

        while (var2.hasNext()) {
            Description d = (Description) var2.next();
            if (d.getDescriptionType() == 0) {
                String fsn;
                if (d.getTerm().contains(" (")) {
                    String noTagFSN = d.getTerm().substring(0, d.getTerm().lastIndexOf(" ("));
                    fsn = noTagFSN;
                } else {
                    fsn = d.getTerm();
                }

                this.fullySpecifiedName = fsn;
                break;
            }
        }
        // ************************

    }

    public Set<AttributeRelationship> getAttributeRelationships() {
        return this.lateralRelationships;
    }

    public Set<Description> getDescriptions() {
        return this.descList;
    }


    public String getName() {
        return this.fullySpecifiedName;
    }

    public String getIDAsString() {
        return ((Long)this.getID()).toString();
    }


}
