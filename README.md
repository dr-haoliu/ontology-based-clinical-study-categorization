# Ontology-based Categorization of Clinical Studies by Their Conditions
Data, source code, and result for the Journal of Biomedical Informatics paper [Ontology-based Categorization of Clinical Studies by Their Conditions] (https://doi.org/10.1016/j.jbi.2022.104235)

The demonstration is available at http://cliver.chunhualab.org:8090/ 

Example input NCTID: NCT00000558, NCT04359095, NCT05440305

## Requirement:
* Java 8
* **[Usagi 1.1.5](https://github.com/OHDSI/Usagi)**
* **[SNOMED CT RF2 release file](https://www.nlm.nih.gov/healthit/snomedct/international.html)**
* or **[MeSH ASCII release file](https://www.nlm.nih.gov/databases/download/mesh.html)** (e.g., d2022.bin)

## Default categorization:
* SNOMED: Subhierarchy of Disease (disorder) SCTID: 64572001 (131 concepts)
* MeSH: Subhierarchy of Diseases (C01, C04-C26) + Mental Disorder F03 

## Custom categorization:
User can define their own concepts (index terms) for customized categorizations. 
**[Format template](data/Index_terms_categorization_0707_2021.xlsx)**

## Instructions:
1. Run **[TestTrialFetcher.java](OntologyCore/src/main/java/edu/TestTrialFetcher.java)** to extract clinical studies from ClinicalTrials.GOV ;
2. Run **[TestConceptSearch.java](Usagi-1.1.5/src/org/ohdsi/apis/TestConceptSearch.java)** to perform concept normalizaiton with Usagi ;
3. Run **[TestClassifyTrialConditionsWithSNOMED.java](OntologyCore/src/main/java/edu/TestClassifyTrialConditionsWithSNOMED.java)** to get classification results;
4. Run **[TestClassifyTrialConditionsWithMeSH.java](OntologyCore/src/main/java/edu/TestClassifyTrialConditionsWithMeSH.java)** to get classification results. 

## Citation
If you found this code helpful, please consider citing: 
```
@article{LIU2022104235,
title = {Ontology-based categorization of clinical studies by their conditions},
journal = {Journal of Biomedical Informatics},
volume = {135},
pages = {104235},
year = {2022},
issn = {1532-0464},
doi = {https://doi.org/10.1016/j.jbi.2022.104235},
url = {https://www.sciencedirect.com/science/article/pii/S1532046422002404},
author = {Hao Liu and Simona Carini and Zhehuan Chen and Spencer {Phillips Hey} and Ida Sim and Chunhua Weng},
keywords = {Ontology, Clinical Study, SNOMED CT, Data Visualization, Categorization},
}
```
