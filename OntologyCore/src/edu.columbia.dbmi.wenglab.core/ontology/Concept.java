package edu.columbia.dbmi.wenglab.core.ontology;

/**
 * Generic class representing a concept in an ontology
 *
 */
public abstract class Concept<ID_T> {
    private final ID_T id;
    
    public Concept(ID_T id) {
        this.id = id;
    }
    
    public abstract String getName();
   
    public abstract String getIDAsString();
    
    public String toString() {
        return String.format("%s (%s)", 
                getName(),
                getIDAsString());
    }
    
    public final ID_T getID() {
        return id;
    }
    
    public boolean equals(Object o) {
        if(o instanceof Concept) {
            Concept other = (Concept)o;
            
            return this.getID().equals(other.getID());
        }
        
        return false;
    }
    
    public int hashCode() {
        return id.hashCode();
    }
}
