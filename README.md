# Ontology-based Classification of Clinical Studies by Their Conditions
Data, source code, and result for manuscript Ontology-based Classification of Clinical Studies by Their Conditions

Requirement:
* Java 8
* Usagi 1.15
* SNOMED CT release file


Instructions:
1. Run **[TestTrialFetcher.java](SNOMEDTest/src/main/java/edu/TestTrialFetcher.java)** to extract clinical studies from ClinicalTrials.GOV ;
2. Run **[TestConceptSearch.java](Usagi-1.1.5/src/org/ohdsi/apis/TestConceptSearch.java)** to perform concept normalizaiton with Usagi ;
3. Run **[TestClassifyTrialConditionsWithSNOMED.java](SNOMEDTest/src/main/java/edu/TestClassifyTrialConditionsWithSNOMED.java)** to get classification results. 
