package edu.columbia.dbmi.wenglab.sno.localdatasource.concept;

import java.util.HashSet;
import java.util.Set;

/**
 * A concept that also has stated relationship information.
 * @author Chris
 */
public class SCTStatedConcept extends SCTConcept {
    private final Set<AttributeRelationship> statedAttributeRelationships = new HashSet<>();
    
    public SCTStatedConcept(long id, boolean isPrimitive, boolean isActive) {
        super(id, isPrimitive, isActive);
    }
    
    public void setStatedRelationships(Set<AttributeRelationship> statedAttributeRelationships) {
        this.statedAttributeRelationships.clear();
        this.statedAttributeRelationships.addAll(statedAttributeRelationships);
    }
    
    public Set<AttributeRelationship> getStatedRelationships() {
        return statedAttributeRelationships;
    }
}
