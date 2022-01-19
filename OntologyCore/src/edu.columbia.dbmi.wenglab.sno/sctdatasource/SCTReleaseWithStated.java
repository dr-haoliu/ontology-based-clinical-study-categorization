package edu.columbia.dbmi.wenglab.sno.sctdatasource;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.SCTConcept;
import java.util.Set;

/**
 * A SNOMED CT release that includes the stated relationships of 
 * each concept
 *
 */
public class SCTReleaseWithStated extends SCTRelease {
    
    /**
     * A hierarchy based on the stated Is a relationships
     * of each concept.
     */
    private final Hierarchy<SCTConcept> statedHierarchy;
    
    public SCTReleaseWithStated(
           SCTReleaseInfo releaseInfo,
           Hierarchy<SCTConcept> activeConceptHierarchy,
           Set<SCTConcept> allConcepts,
           Hierarchy<SCTConcept> statedHierarchy) {
        
        super(releaseInfo, activeConceptHierarchy, allConcepts);
        
        this.statedHierarchy = statedHierarchy;
    }
    
    public Hierarchy<SCTConcept> getStatedHierarchy() {
        return statedHierarchy;
    }
    
    /**
     * Returns a SNOMED CT release where the hierarchy is based on
     * only stated hierarchical relationships
     * 
     * @return 
     */
    public SCTRelease getStatedHierarchyRelease() {
        return new SCTRelease(
                this.getReleaseInfo(), 
                this.getStatedHierarchy(), 
                this.getAllConcepts());
    }
    
    @Override
    public boolean supportsStatedRelationships() {
        return true;
    }
}
