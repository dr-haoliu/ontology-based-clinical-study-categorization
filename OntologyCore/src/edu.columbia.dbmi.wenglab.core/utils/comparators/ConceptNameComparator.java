package edu.columbia.dbmi.wenglab.core.utils.comparators;

import edu.columbia.dbmi.wenglab.core.ontology.Concept;
import java.util.Comparator;

/**
 *
 */
public class ConceptNameComparator<T extends Concept> implements Comparator<T> {

    @Override
    public int compare(T a, T b) {
        return a.getName().compareToIgnoreCase(b.getName());
    }
    
}
