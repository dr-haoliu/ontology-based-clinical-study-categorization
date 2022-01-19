package edu.columbia.dbmi.wenglab.mesh.concept;

public class AttributeRelationship {

        private final MeSHConcept relationshipType;
        private final MeSHConcept target;
        private final int relationshipGroup;
        private final long characteristicType;

        public AttributeRelationship(MeSHConcept relationshipType, MeSHConcept target, int relationshipGroup, long characteristicType) {
            this.relationshipType = relationshipType;
            this.target = target;
            this.relationshipGroup = relationshipGroup;
            this.characteristicType = characteristicType;
        }

        public boolean isDefining() {
            return this.characteristicType == 900000000000011006L || this.characteristicType == 900000000000010007L;
        }

        public MeSHConcept getType() {
            return this.relationshipType;
        }

        public MeSHConcept getTarget() {
            return this.target;
        }

        public int getGroup() {
            return this.relationshipGroup;
        }

        public long getCharacteristicType() {
            return this.characteristicType;
        }

        public boolean equals(Object o) {
            if (!(o instanceof AttributeRelationship)) {
                return false;
            } else {
                AttributeRelationship other = (AttributeRelationship)o;
                return this.equalsIgnoreGroup(other) && this.getGroup() == other.getGroup();
            }
        }

        public boolean equalsIgnoreGroup(AttributeRelationship other) {
            return this.getType().equals(other.getType()) && this.getTarget() == other.getTarget();
        }

}
