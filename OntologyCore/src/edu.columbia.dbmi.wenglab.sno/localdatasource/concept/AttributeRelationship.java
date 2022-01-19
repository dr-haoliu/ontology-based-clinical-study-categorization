package edu.columbia.dbmi.wenglab.sno.localdatasource.concept;

/**
 *
 */
public class AttributeRelationship {

    private final SCTConcept relationshipType;
    
    private final SCTConcept target;
    
    private final int relationshipGroup;
    
    private final long characteristicType;

    public AttributeRelationship(SCTConcept relationshipType, SCTConcept target, int relationshipGroup, long characteristicType) {
        this.relationshipType = relationshipType;
        this.target = target;
        this.relationshipGroup = relationshipGroup;
        this.characteristicType = characteristicType;
    }
    
    public boolean isDefining() {
        return characteristicType == 900000000000011006l || characteristicType == 900000000000010007l;
    }
    
    public SCTConcept getType() {
        return relationshipType;
    }
    
    public SCTConcept getTarget() {
        return target;
    }
    
    public int getGroup() {
        return relationshipGroup;
    }

    public long getCharacteristicType() {
        return characteristicType;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof AttributeRelationship) {
            AttributeRelationship other = (AttributeRelationship)o;
            
            return equalsIgnoreGroup(other) && (this.getGroup() == other.getGroup());
        }
        
        return false;
    }
    
    public boolean equalsIgnoreGroup(AttributeRelationship other) {
        return this.getType().equals(other.getType()) && this.getTarget() == other.getTarget();
    }
}
