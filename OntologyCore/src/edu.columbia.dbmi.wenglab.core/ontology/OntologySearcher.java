package edu.columbia.dbmi.wenglab.core.ontology;

import edu.columbia.dbmi.wenglab.core.ontology.Concept;
import java.util.Set;

public interface OntologySearcher<T extends Concept> {
    Set<T> searchStarting(String var1);

    Set<T> searchExact(String var1);

    Set<T> searchAnywhere(String var1);

    Set<T> searchID(String var1);
}
