package edu;

import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.mesh.concept.Description;
import edu.njit.cs.saboc.blu.mesh.concept.MeSHConcept;
import edu.njit.cs.saboc.blu.mesh.load.LoadMeSH;
import edu.njit.cs.saboc.blu.mesh.load.MeSHRelease;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TestClassifyTrialConditionsWithMeSH {
    public List<String> logs;

    public String DiseaseID = "1720342000000004194";

    public Hierarchy<MeSHConcept> hierarchy;

    public static void main(String[] args) {
        TestClassifyTrialConditionsWithMeSH ist = new TestClassifyTrialConditionsWithMeSH();
    }


    public TestClassifyTrialConditionsWithMeSH() {

        File file = new File("data/MeSH/d2022.bin");
        logs = new ArrayList<>();
        loadSNOOntologies(file);
    }


    private void loadSNOOntologies(File file) {

        System.out.println("Loading");
        LoadMeSH lm = new LoadMeSH();

        Boolean default_cat = true;

        try {
            MeSHRelease release = lm.loadMesh(file);
            hierarchy = release.getConceptHierarchy();

            Map<String, List<String>> bucketsListMap = new HashMap<>();


            if (default_cat) {
                bucketsListMap = readBucketsListDefault(release);
            }else {
                String fileName = "data/MeSH_Classification_01072022.csv";
                bucketsListMap = readBucketsListUpdated(fileName, release);
            }


            String TrialMeSHFile = "data/output/all_mesh_headings_for_cat_0107_2022_v1.csv";

            List<MeSHTrialRecord> topConditions = readMappedTrials(TrialMeSHFile, release);

            System.out.println("=================================================================");

            Set<MeSHTrialRecord> recordResultSet = new HashSet<>();
            Set<String> originalNCTids = new HashSet<>();
            Set<String> bucketedNCTids = new HashSet<>();

            bucketsListMap.forEach((bucketName, conceptIDlist) -> {
                conceptIDlist.forEach(conceptIDStr -> {

                    long id = Long.parseLong(conceptIDStr);
                    Optional<MeSHConcept> optConcept = release.getConceptFromId(id);

                    if (optConcept.isPresent()) {
                        MeSHConcept root = optConcept.get();
                        hierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(root);

                        topConditions.forEach((record) -> {

                            String nctid = record.getNctid();
                            originalNCTids.add(nctid);

                            String snoConceptID = record.getConceptID();
                            long conid = Long.parseLong(snoConceptID);
                            Optional<MeSHConcept> concepts = release.getConceptFromId(conid);

                            if (concepts.isPresent()) {
                                MeSHConcept sct = concepts.get();

                                // skip bucket Healthy related trial
                                if (sct.getIDAsString().equalsIgnoreCase("1720342000000064368")) {
                                    return;
                                }

                                // **** if concept equals bucket concept
                                if (sct.getIDAsString().equalsIgnoreCase(root.getIDAsString())) {
                                    System.out.println("bucket Name = " + bucketName);
                                    System.out.println("root Name = " + root.getName());
                                    System.out.println("nctid = " + nctid);
                                    System.out.println("concept name = " + sct.getName());
                                    System.out.println("allPathsTo.size() = 0");
                                    System.out.println();

                                    record.addBucket(bucketName);
                                    record.addBucketConceptName(root.getName());
                                    recordResultSet.add(record);
                                    bucketedNCTids.add(nctid);
                                } else {

                                    ArrayList<ArrayList<MeSHConcept>> allPathsTo = hierarchy.getAllPathsTo(sct);

                                    if (allPathsTo.size() > 0) {
                                        System.out.println("bucket Name = " + bucketName);
                                        System.out.println("root Name = " + root.getName());
                                        System.out.println("nctid = " + nctid);
                                        System.out.println("concept name = " + sct.getName());
                                        System.out.println("allPathsTo.size() = " + allPathsTo.size());
                                        System.out.println();

                                        record.addBucket(bucketName);
                                        record.addBucketConceptName(root.getName());
                                        recordResultSet.add(record);
                                        bucketedNCTids.add(nctid);

                                    }
                                }

                            }
                        });
                    }

                });
            });

            System.out.println("To be bucketed NCT size = " + originalNCTids.size());
            logs.add("To be bucketed NCT size = " + originalNCTids.size());
            System.out.println("bucketed NCT size = " + bucketedNCTids.size());
            logs.add("bucketed NCT size = " + bucketedNCTids.size());
            originalNCTids.removeAll(bucketedNCTids);

            System.out.println("Checking Un-bucketed nctids if it can be assigned to Healthy bucket");
            originalNCTids.forEach(s -> {

                for (MeSHTrialRecord trialRecord : topConditions) {
                    if (trialRecord.nctid.equalsIgnoreCase(s)) {

                        String ctgovText = trialRecord.getCTgovText();

                        if (ctgovText.toLowerCase().contains("healthy")
                        ) {
                            logs.add("nctids bucketed to Healthy: " + trialRecord.nctid);
                            System.out.println("bucket Name = " + "Healthy");
                            System.out.println("root Name = " + "Healthy");
                            System.out.println("nctid = " + trialRecord.nctid);
                            System.out.println();

                            trialRecord.addBucket("Healthy");
                            trialRecord.addBucketConceptName("Healthy");
                            recordResultSet.add(trialRecord);
                            bucketedNCTids.add(trialRecord.nctid);
                        }
                    }
                }
            });

            originalNCTids.removeAll(bucketedNCTids);
            System.out.println("un-bucketed NCT size = " + originalNCTids.size());
            logs.add("un-bucketed NCT size = " + originalNCTids.size());
            originalNCTids.forEach(s -> {
                System.out.println("Un-bucketed nctids due to not located in MeSH hierarchy = " + s);
                logs.add("Un-bucketed nctids due to not located in MeSH hierarchy = " + s);
                for (MeSHTrialRecord trialRecord : topConditions) {
                    if (trialRecord.nctid.equalsIgnoreCase(s)) {
                        System.out.println("trialRecord.conceptName = " + trialRecord.conceptName);
                        logs.add("\ttrialRecord.conceptName = " + trialRecord.conceptName);
                        System.out.println("trialRecord.CTgovText = " + trialRecord.CTgovText);
                        logs.add("\ttrialRecord.CTgovText = " + trialRecord.CTgovText);
                    }
                }

            });

            List<String[]> saveOutput = new ArrayList<>();
            String[] header = {"nctid", "field", "term", "domain", "conceptID", "conceptName", "CTgovText", "start_date", "completion_date", "enrollment", "bucket", "bucketConceptName"};
            saveOutput.add(header);

            System.out.println("Processing original total size of " + topConditions.size());
            System.out.println("Processing total size of " + recordResultSet.size());


            topConditions.forEach(record -> {
                saveOutput.add(record.convertToStringList());
            });

            String outputDir = "data/mesh_hier/after_bucketed_mesh_hier_all_2022_0107_v1_default_cat.csv";
            CSVUtil.writeToCSV(outputDir, saveOutput);

            String logDir = "data/mesh_hier/log_after_bucketed_mesh_hier_all_2021_0107_v1_default_cat.txt";
            logs.forEach(s -> {
                FileUtil.add2File(logDir, s + "\n");
            });


            System.out.println();
            System.out.println("Processing Finished");

        } catch (IOException e) {
            // TODO: write error...
        }
    }


    private Map<String, List<String>> readBucketsListDefault(MeSHRelease release) {


        Map<String, List<String>> resultMap = new HashMap<>();


        MeSHConcept rootConcept = release.getConceptFromId(1720342000000000000L).get();
//        Disease (disorder)  D004194

        Set<MeSHConcept> children = release.getConceptHierarchy().getChildren(rootConcept);

        for (MeSHConcept child : children) {
            String bucketName = child.getName();
            Set<Description> descriptions = child.getDescriptions();
            String tree_num = descriptions.stream().filter(t-> t.getDescriptionType()==2).findAny().get().getTerm();
            if(tree_num.startsWith("C") || tree_num.startsWith("F03")){
                System.out.println("default bucket name = " + bucketName.trim());
                System.out.println("tree_num = " + tree_num);
                List<String> idlist = Collections.singletonList(child.getIDAsString());
                resultMap.put(bucketName, idlist);
            }
            System.out.println();
        }

        return resultMap;

    }

    private Map<String, List<String>> readBucketsListUpdated(String fileName, MeSHRelease release) {


        Map<String, List<String>> resultMap = new HashMap<>();

        List<String[]> allData = Utils.readDataFromCommaSeperator(fileName);

        for (String[] row : allData) {
//            System.out.println("row = " + row);
//            if(row.length !=3) continue;
            if (row.length < 3) continue;
            String bucketName = row[0].trim();
            System.out.println("bucket name = " + bucketName);
            String conceptId = row[2].trim();

            if (resultMap.containsKey(bucketName)) {
                resultMap.get(bucketName).add(conceptId);
            } else {
                List<String> idlist = new ArrayList<>();

                idlist.add(conceptId);
                resultMap.put(bucketName, idlist);
            }
        }

        return resultMap;

    }


    private List<MeSHTrialRecord> readMappedTrials(String fileName, MeSHRelease release) {

        List<MeSHTrialRecord> resultMap = new ArrayList<>();


        List<String> nonMappedConceptNames = new ArrayList<>();

        List<String[]> allData = Utils.readDataFromCustomSeperator(fileName, 1);


        Set<String> originalNCTids = new HashSet<>();
        Set<String> mappedNCTIDs = new HashSet<>();

//        create diseases hierarchy
        long id = Long.parseLong(DiseaseID);
        Optional<MeSHConcept> optConcept = release.getConceptFromId(id);
        if (optConcept.isPresent()) {
            MeSHConcept root = optConcept.get();
            hierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(root);
        }

//      scan records
//        nctid,field,mesh_term,domain,conceptID,conceptName,mesh conditions,start_date,completion_date,enrollment

        for (String[] row : allData) {

            String nctid = row[0];
            String field = row[1];
            String term = row[2];
            String domain = row[3];
            String conceptID = row[4];
            String conceptName = row[5];
            String CTgovText = row[6];
            String start_date = row[7];
            String completion_date = row[8];
            String enrollment = row[9];

            System.out.println("nctid = " + nctid);
            originalNCTids.add(nctid);

            if (term.isEmpty()) {
                continue;
            }

            Set<MeSHConcept> optConcepts = release.searchExact(term);

            if (optConcepts.size() == 0) {
                if (term.toLowerCase().contains("metastatic")) {
                    String concetpNameUpdated = term;
                    concetpNameUpdated = concetpNameUpdated.replaceAll("Non-metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("non-metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("Metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("metastatic", "");

                    optConcepts = release.searchExact(concetpNameUpdated.trim());
                }
            }
            // if no concept returned, search term text without hyphen
            if (optConcepts.size() == 0) {
                String aterm = CTgovText;
                aterm = aterm.replaceAll("-", " ");
                optConcepts = release.searchExact(aterm);
            }


            if (optConcepts.size() == 0) {
//                System.out.println( "No concept found for name "+ conceptName);

                if (term.contains("Healthy") || term.contains("healthy")) {  // handle exception of condition including "Healthy"
                    conceptID = "1720342000000064368";  // to-do
                    optConcepts = release.searchID(conceptID);
                } else {
                    nonMappedConceptNames.add(nctid + "\t" + conceptName);
                }
            }


            if (optConcepts.size() > 1) {
                optConcepts.forEach(sctConcept -> {
                    if (checkDomain(hierarchy, sctConcept)) {
                        String meshConceptID = sctConcept.getIDAsString();
                        String meshConceptName = sctConcept.getName();
                        System.out.println("get concept id: " + sctConcept.getIDAsString());
                        System.out.println("get concept: " + sctConcept.getName());
                        if (field.equalsIgnoreCase("condition_field") || field.equalsIgnoreCase("mesh_field")) {
                            resultMap.add(new MeSHTrialRecord(nctid, field, term, domain, meshConceptID, meshConceptName, CTgovText, start_date, completion_date, enrollment));
                            mappedNCTIDs.add(nctid);
                        }
                    }
                });
            } else {
                optConcepts.forEach(sctConcept -> {
                    String meshConceptID = sctConcept.getIDAsString();
                    String meshConceptName = sctConcept.getName();
                    System.out.println("get concept id: " + sctConcept.getIDAsString());
                    System.out.println("get concept: " + sctConcept.getName());
                    if (field.equalsIgnoreCase("condition_field") || field.equalsIgnoreCase("mesh_field")) {
                        resultMap.add(new MeSHTrialRecord(nctid, field, term, domain, meshConceptID, meshConceptName, CTgovText, start_date, completion_date, enrollment));
                        mappedNCTIDs.add(nctid);
                    }
                });
            }


        }

        System.out.println("originalNCTids size = " + originalNCTids.size());
        logs.add("original NCT size = " + originalNCTids.size());
        originalNCTids.removeAll(mappedNCTIDs);
        logs.add("Size of NCTs failed to map to SNOMED concept = " + originalNCTids.size());
        originalNCTids.forEach(nctid -> {
            System.out.println("Nctids due to no mapped to SNOMED concept = " + nctid);
            logs.add("Nctids due to no mapped to SNOMED concept = " + nctid);
        });

        nonMappedConceptNames.forEach(conceptName -> {
            System.out.println("Concept names not mapped to SNOMED = " + conceptName);
            logs.add("Concept names not mapped to SNOMED = " + conceptName);
        });

        return resultMap;
    }


    private boolean checkDomain(Hierarchy<MeSHConcept> hierarchy, MeSHConcept sct) {
        ArrayList<ArrayList<MeSHConcept>> allPathsTo = hierarchy.getAllPathsTo(sct);
        if (allPathsTo.size() > 0) return true;
        else return false;
    }



}
