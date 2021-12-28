package org.ohdsi.apis;

import org.ohdsi.usagi.UsagiSearchEngine;

import java.util.*;

public class TestConceptSearch {

//    vocabulary dir: E:\CTKB\vocabulary_download_v5
//    vocabulary dir: E:\CTKB\vocabulary_download_v5_21520

    // Usagi indexing dir
    ConceptSearchAPI csapi = new ConceptSearchAPI("D:/IdeaProjects/Usagi-1.1.5/");

    public static void main(String[] args) {

        TestConceptSearch scs = new TestConceptSearch();

        String csvFileDir = "data/output/output_2021_04_Vivli_NCT_ID_studies.csv";


        List<String[]> readInput = CSVUtil.readCSV(csvFileDir);
        List<String[]> saveOutput = new ArrayList<>();
        String[] header={"nctid", "field", "term", "domain", "conceptID", "conceptCode","conceptName", "match_socre", "CTgovText", "start_date", "completion_date", "enrollment"};
        saveOutput.add(header);

        System.out.println("Processing total size of " + readInput.size());

        AcronymDict dict = new AcronymDict("data/AcronymDict_v1.txt");

        for (int i = 0; i<readInput.size(); i++) {
            System.out.println("Processing i = " + i +"\t"+ (readInput.size()-i) +" left");
            String[] strings = readInput.get(i);
            if(i==0) continue;
            if (strings[1].equalsIgnoreCase("official_title")) continue;

            saveOutput.add(scs.testSearchConceptforCSV2(strings, dict));
        }

        String outputDir = "data/mapped/after_mapped_concept_2021_04_Vivli_NCT_ID_studies.csv";
        CSVUtil.writeToCSV(outputDir, saveOutput);


    }



    public String[] testSearchConceptforCSV(String[] inputStr) {

        String[] arr2 = Arrays.copyOf(inputStr, 8);
        if (inputStr.length == 7) {

            String term = inputStr[2];
            String domain = inputStr[3];
            if (term.length() > 0 && domain.length() > 0) {

                List<UsagiSearchEngine.ScoredConcept> scoredConcepts = csapi.standarizeConcept(term, domain);
                if (scoredConcepts.size() > 0) {
                    int conceptId = scoredConcepts.get(0).concept.conceptId;
                    String conceptName = scoredConcepts.get(0).concept.conceptName;
                    double score = scoredConcepts.get(0).matchScore;

                    arr2[4] = String.valueOf(conceptId);
                    arr2[5] = conceptName;
                    arr2[7] = inputStr[6];
                    arr2[6] = String.valueOf(score);
                }
            }
        }
        return arr2;

    }


    public String[] testSearchConceptforCSV2(String[] inputStr, AcronymDict dict) {

        String[] arr2 = Arrays.copyOf(inputStr, 12);

        if (inputStr.length == 10) {

            String term = inputStr[2];
            String domain = inputStr[3];
            if (term.length() > 0 && domain.length() > 0) {

                int conceptId = -1;
                String conceptName ="";
                String conceptCode ="";
                double score = -1;

                // remove abbreviation in parentheses
                term = AbbreMapper.cleanAbbrev(term);

                if(dict.getDict().containsKey(term)){
                    System.out.println("AcronymDict process before term = " + term);
                    term = dict.getDict().get(term);
                    System.out.println("AcronymDict process after: term = " + term);
                }

                if (term.equalsIgnoreCase("COVID19") || term.equalsIgnoreCase("COVID-19") ||
                        term.equalsIgnoreCase("COVID-19 Infection")){
                    conceptId = 840539006;
                    conceptCode = "840539006";
                    conceptName = "Disease caused by Severe acute respiratory syndrome coronavirus 2";
                    score = 1;
                }
                else if (term.equalsIgnoreCase("Severe Hypertension") ){
                    conceptId = 38341003;
                    conceptCode = "38341003";
                    conceptName = "Hypertensive disorder, systemic arterial";
                    score = 1;
                }
                else if (term.toLowerCase().contains("Erectile Dysfunction".toLowerCase())){
                    conceptId = 860914002;
                    conceptCode = "860914002";
                    conceptName = "Erectile dysfunction";
                    score = 1;
                }
                else if (term.toLowerCase().contains("Metastatic non-small cell lung cancer".toLowerCase())){
                    conceptId = 254637007;
                    conceptCode = "254637007";
                    conceptName = "Non-small cell lung cancer";
                    score = 1;
                }

                else{
                    if (term.toLowerCase().contains(" vaccine")){
                        term = term.toLowerCase().replace(" vaccine", "").trim();
                        System.out.println("term = " + term);
                    }
                    else if (term.toLowerCase().contains(" vaccines")){
                        term = term.toLowerCase().replace(" vaccines", "").trim();
                        System.out.println("term = " + term);
                    }

                    List<UsagiSearchEngine.ScoredConcept> conceptsWithHyphen = searchWithHyphen(term, domain);
    //                conceptsWithHyphen = checkNegation(term, conceptsWithHyphen);
                    conceptsWithHyphen = checkNegationReduceScore(term, conceptsWithHyphen);
                    List<UsagiSearchEngine.ScoredConcept> conceptsWithoutHyphen = searchWithoutHyphen(term, domain);
                    conceptsWithoutHyphen = checkNegationReduceScore(term, conceptsWithoutHyphen);

                    List<UsagiSearchEngine.ScoredConcept> scoredConcepts = mergeTwoConceptList(conceptsWithHyphen, conceptsWithoutHyphen);


                    if(term.toLowerCase().contains("childhood") && term.length()> 9 && scoredConcepts.size() > 0){
                        // check if only mapped to childhood
//                        4286638  Childhood
                        if(scoredConcepts.get(0).concept.conceptId == 4286638){
                            String replaceStr = term.toLowerCase().replace("childhood", "");
                            scoredConcepts = searchWithoutHyphen(replaceStr, domain);
                        }

                    }

                    if (scoredConcepts.size() > 0) {

                        int index = 0;
//                        boolean stopFlag = false;
//                        while (!stopFlag) {
//                            int c_size = scoredConcepts.get(index).concept.conceptCode.length();
//                            if (c_size > 14) {
//                                System.out.println("Concept code EXCEEDs 15 digits: c_id = " + scoredConcepts.get(index).concept.conceptCode);
//                                System.out.println("Concept term = " + term);
//                                index++;
//                                stopFlag = true;
//                            } else
//                                break;
//                        }

                        conceptId = scoredConcepts.get(index).concept.conceptId;
                        conceptCode = scoredConcepts.get(index).concept.conceptCode;
                        conceptName = scoredConcepts.get(index).concept.conceptName;
                        score = scoredConcepts.get(index).matchScore;

                        System.out.println("conceptId = " + conceptId);
                        System.out.println("conceptName = " + conceptName);
                    }
                }
                arr2[4] = String.valueOf(conceptId);
                arr2[5] = conceptCode;
                arr2[6] = conceptName;
                arr2[7] = String.valueOf(score);
                arr2[8] = inputStr[6];
                arr2[9] = inputStr[7];
                arr2[10] = inputStr[8];
                arr2[11] = inputStr[9];
            }
        }
        return arr2;

    }



    public List<UsagiSearchEngine.ScoredConcept> searchWithoutHyphen(String term, String domain){
        term = term.replaceAll("-", "");
        List<UsagiSearchEngine.ScoredConcept> scoredConcepts = csapi.standarizeConcept(term, domain);
        if (scoredConcepts.size() > 10) {
            List<UsagiSearchEngine.ScoredConcept> top10Concepts = scoredConcepts.subList(0,9);
            return top10Concepts;
        }
        return scoredConcepts;
    }

    public List<UsagiSearchEngine.ScoredConcept> searchWithHyphen(String term, String domain){
        List<UsagiSearchEngine.ScoredConcept> scoredConcepts = csapi.standarizeConcept(term, domain);
        if (scoredConcepts.size() > 10) {
            List<UsagiSearchEngine.ScoredConcept> top10Concepts = scoredConcepts.subList(0,9);
            return top10Concepts;
        }
        return scoredConcepts;
    }


    public List<UsagiSearchEngine.ScoredConcept> mergeTwoConceptList(List<UsagiSearchEngine.ScoredConcept> list1, List<UsagiSearchEngine.ScoredConcept> list2){


        List<UsagiSearchEngine.ScoredConcept> mergedConcepts = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            UsagiSearchEngine.ScoredConcept concept1 = list1.get(i);
            for (int j = 0; j < list2.size(); j++) {
                UsagiSearchEngine.ScoredConcept concept2 = list2.get(j);
                if(concept1.concept.conceptId == concept2.concept.conceptId){
                    if(concept1.matchScore>=concept2.matchScore){
                        list2.remove(j);
                    }else{
                        list1.remove(i);
                    }
                }
            }
        }
        mergedConcepts.addAll(list1);
        mergedConcepts.addAll(list2);

        Collections.sort(mergedConcepts, new Comparator<UsagiSearchEngine.ScoredConcept>() {
            @Override
            public int compare(UsagiSearchEngine.ScoredConcept o1, UsagiSearchEngine.ScoredConcept o2) {
                return Float.compare(o1.matchScore, o2.matchScore);
            }
        }.reversed());

        return mergedConcepts;
    }

    public List<UsagiSearchEngine.ScoredConcept> checkNegation(String term, List<UsagiSearchEngine.ScoredConcept> concepts){
        String[] negations = {"Non", "non"};
        Boolean ngflag = false;

        for (String negation : negations) {
            if(term.contains(negation)){
                ngflag = true;
                break;
            }
        }

        if(ngflag){
//             remove concept without neg
            for (int i = 0; i < concepts.size(); i++) {
                String conceptName = concepts.get(i).concept.conceptName;
                boolean conNameNeg = false;
                for (String negation : negations) {
                    if(conceptName.contains(negation)){
                        conNameNeg = true;
                        break;
                    }
                }
                if(!conNameNeg){
                    concepts.remove(i);
                }
            }
        }
        return concepts;


    }


    public List<UsagiSearchEngine.ScoredConcept> checkNegationReduceScore(String term, List<UsagiSearchEngine.ScoredConcept> concepts){
        List<String> negations = TestMatcher.matchNegation(term);

        if(negations.size()>0){
//             remove concept without neg
            for (int i = 0; i < concepts.size(); i++) {
                String conceptName = concepts.get(i).concept.conceptName;
                conceptName = conceptName.toLowerCase();

                for (String negation : negations) {
                    String neg1 = negation.toLowerCase();
                    String neg2 = negation.toLowerCase().replaceAll("-","");
                    if(!conceptName.contains(neg1) && !conceptName.contains(neg2)){
                        concepts.get(i).matchScore -= 0.15;
                    }
                }
            }
        }
        return concepts;


    }





}
