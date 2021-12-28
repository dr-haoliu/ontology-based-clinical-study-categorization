package org.ohdsi.apis;


import org.ohdsi.usagi.BerkeleyDbEngine;
import org.ohdsi.usagi.UsagiSearchEngine;
import org.ohdsi.usagi.ui.Global;
import org.ohdsi.usagi.ui.Mapping;

import java.util.List;

public class ConceptSearchAPI {
    public static void main(String[] args) {
        ConceptSearchAPI cs = new ConceptSearchAPI("D:/IdeaProjects/Usagi-1.1.5/");

        String allterms = FileHelper.Readfile("D:/IdeaProjects/Usagi-1.1.5/test/data/test.txt");
        String[] terms = allterms.split("\n");
        StringBuffer sb=new StringBuffer();
        int i=0;
        for (String t : terms) {
//            List<UsagiSearchEngine.ScoredConcept> lsc = cs.searchResults(t);
            List<UsagiSearchEngine.ScoredConcept> lsc = cs.standarizeConcept(t,"Condition");
//            List<UsagiSearchEngine.ScoredConcept> lsc = cs.standarizeConceptWithVocabulary(t,"Condition", "SNOMED");

            UsagiSearchEngine.ScoredConcept sc =lsc.get(0);
            System.out.println(i++);
            System.out.println(sc.term + "\t" + sc.matchScore + "\t" + sc.concept.conceptId + "\t" + sc.concept.conceptName);
            sb.append(t+"\t"+sc.term + "\t" + sc.matchScore + "\t" + sc.concept.conceptId + "\t" + sc.concept.conceptName+"\n");

        }
//        FileHelper.write2File("/Users/cy2465/Downloads/interventions_result.txt", sb.toString());
    }

    public List<UsagiSearchEngine.ScoredConcept> searchConcepts(String term) {
        return null;
    }

    public ConceptSearchAPI(String indexdir) {
        Global.mapping = new Mapping();
        Global.folder = indexdir;
        Global.usagiSearchEngine = new UsagiSearchEngine(Global.folder);
        Global.dbEngine = new BerkeleyDbEngine(Global.folder);
        if (Global.usagiSearchEngine.mainIndexExists()) {
            Global.usagiSearchEngine.openIndexForSearching(false);
            Global.dbEngine.openForReading();
        }
    }

    public List<UsagiSearchEngine.ScoredConcept> searchResults(String terms) {
        List<UsagiSearchEngine.ScoredConcept> searchResults = Global.usagiSearchEngine.search(terms, true, null, null, null, null, true,
                true);
        return searchResults;
    }

    public List<UsagiSearchEngine.ScoredConcept> standarizeConcept(String terms, String domain) {
        List<UsagiSearchEngine.ScoredConcept> searchResults = Global.usagiSearchEngine.search(terms, true, null, domain, null, null, true,
                true);
        return searchResults;
    }

    public List<UsagiSearchEngine.ScoredConcept> standarizeConceptNotOnlyS(String terms, String domain) {
        List<UsagiSearchEngine.ScoredConcept> searchResults = Global.usagiSearchEngine.search(terms, true, null, domain, null, null, false,
                true);
        return searchResults;
    }

    public List<UsagiSearchEngine.ScoredConcept> standarizeConceptNotOnlyS(String terms, String domain, String excludedVocabulary) {
        List<UsagiSearchEngine.ScoredConcept> searchResults = Global.usagiSearchEngine.search(terms, true, null, domain, null, null, false,
                true);
        for (int i = 0; i < searchResults.size(); i++) {
            UsagiSearchEngine.ScoredConcept sc = searchResults.get(i);
            if(sc.concept.vocabularyId.equalsIgnoreCase(excludedVocabulary)){
                searchResults.remove(i);
            }
        }
        return searchResults;
    }
}

