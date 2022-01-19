package main.java.edu;

import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.SCTConcept;
import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.core.utils.comparators.ConceptNameComparator;
import edu.columbia.dbmi.wenglab.core.utils.toolstate.OAFRecentlyOpenedFileManager;
import edu.columbia.dbmi.wenglab.core.utils.toolstate.OAFStateFileManager;
import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.SCTConcept;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.LoadLocalRelease;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.LocalLoadStateMonitor;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.RF2ReleaseLoader;
import edu.columbia.dbmi.wenglab.sno.sctdatasource.SCTRelease;
import edu.columbia.dbmi.wenglab.sno.sctdatasource.SCTReleaseInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClassifyTrialConditionsWithSNOMED {

    public List<String> logs;
    public String clinicalFindingID = "404684003";

    public Hierarchy<SCTConcept> hierarchy;

    public static void main(String[] args) {
        TestClassifyTrialConditionsWithSNOMED ist = new TestClassifyTrialConditionsWithSNOMED();
    }



    public TestClassifyTrialConditionsWithSNOMED(){

        File inputDir = new File("D:/Ontology/SNO");
        logs = new ArrayList<>();
        loadSNOOntologies(inputDir);
    }

    
    private void loadSNOOntologies(File inputDirectory) {

        System.out.println("Loading");
        OAFStateFileManager stateFileManager = new OAFStateFileManager("BLUSNO");
        Boolean default_cat = false;


        if (inputDirectory.isDirectory()) {

            File[] subfiles = inputDirectory.listFiles();

            for (File file : subfiles) {
                if (file.isDirectory()) {
                    System.out.println("Find file: " + file.getAbsolutePath());
                    ArrayList<File> dirList = LoadLocalRelease.findReleaseFolders(file);
                    dirList.forEach((t) -> {
                        System.out.println("dir " + t);
                    });
                    ArrayList<String> releaseNames = LoadLocalRelease.getReleaseFileNames(dirList);
                    releaseNames.forEach((t) -> {
                        System.out.println("release " + t);
                    });
                    String releaseName = releaseNames.get(0);
                    try {
                        RF2ReleaseLoader rf2Importer = new RF2ReleaseLoader(stateFileManager);
                        LocalLoadStateMonitor loadMonitor = rf2Importer.getLoadStateMonitor();

                        File dirFile = dirList.get(0);
                        SCTRelease release = rf2Importer.loadLocalSnomedRelease(dirFile,
                                new SCTReleaseInfo(dirFile, releaseName), loadMonitor);



                        String fileName = "data/Index_term_Classification_0707.csv";

                        Map<String, List<String>> bucketsListMap = readBucketsListUpdated(fileName, release);

                        if(default_cat){
                            bucketsListMap = readBucketsListDefault(release);
                        }

                        String topConditionTrialFile = "data/mapped/after_mapped_concept_2021_04_Vivli_NCT_ID_studies.csv";

                        List<TrialRecord> topConditions = readMappedTrials(topConditionTrialFile, release);

                        System.out.println("=================================================================");

                        Set<TrialRecord>  recordResultSet = new HashSet<>();
                        Set<String> originalNCTids = new HashSet<>();
                        Set<String> bucketedNCTids = new HashSet<>();

                        bucketsListMap.forEach((bucketName, conceptIDlist)->{
                            conceptIDlist.forEach( conceptIDStr->{

                                long id = Long.parseLong(conceptIDStr);
                                Optional<SCTConcept> optConcept = release.getConceptFromId(id);

                                if (optConcept.isPresent()) {
                                    SCTConcept root = optConcept.get();
                                    Hierarchy<SCTConcept> hierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(root);

                                    topConditions.forEach((record)->{

//                                        if (record.field.equalsIgnoreCase("official_title")){
//                                            return;
//                                        }
                                        String nctid = record.getNctid();
                                        originalNCTids.add(nctid);

                                        String snoConceptID = record.getSnomedConceptID();
                                        long conid = Long.parseLong(snoConceptID);
                                        Optional<SCTConcept> concepts = release.getConceptFromId(conid);

                                        if (concepts.isPresent()) {
                                            SCTConcept sct = concepts.get();

                                            // skip bucket Healthy related trial
                                            if(sct.getIDAsString().equalsIgnoreCase("102509001") || sct.getIDAsString().equalsIgnoreCase("102512003")) {
                                                return;
                                            }

                                            // **** if concept equals bucket concept
                                            if (sct.getIDAsString().equalsIgnoreCase(root.getIDAsString())){
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
                                            }
                                            else {

                                                ArrayList<ArrayList<SCTConcept>> allPathsTo = hierarchy.getAllPathsTo(sct);

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

                            } );
                        });

                        System.out.println("To be bucketed NCT size = " + originalNCTids.size());
                        logs.add("To be bucketed NCT size = " + originalNCTids.size());
                        logs.add("bucketed NCT size = " + bucketedNCTids.size());
                        originalNCTids.removeAll(bucketedNCTids);

                        System.out.println("Checking Un-bucketed nctids if it can be assigned to Healthy bucket");
                        originalNCTids.forEach(s -> {


                                    for (TrialRecord trialRecord : topConditions) {
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
                        logs.add("un-bucketed NCT size = " + originalNCTids.size());
                        originalNCTids.forEach(s -> {
                            System.out.println("Un-bucketed nctids due to not located in SNOMED hierarchy = " + s);
                            logs.add("Un-bucketed nctids due to not located in SNOMED hierarchy = " + s);
                            for (TrialRecord trialRecord:topConditions) {
                                if(trialRecord.nctid.equalsIgnoreCase(s)){
                                    System.out.println("trialRecord.conceptName = " + trialRecord.conceptName);
                                    logs.add("\ttrialRecord.conceptName = " + trialRecord.conceptName);
                                    System.out.println("trialRecord.CTgovText = " + trialRecord.CTgovText);
                                    logs.add("\ttrialRecord.CTgovText = " + trialRecord.CTgovText);
                                }
                            }

                        });

                        List<String[]> saveOutput = new ArrayList<>();
                        String[] header={"nctid", "field", "term", "domain", "omopConceptID", "snomedConceptID", "conceptName", "match_socre", "CTgovText", "start_date", "completion_date", "enrollment", "bucket", "bucketConceptName"};
                        saveOutput.add(header);

                        System.out.println("Processing original total size of " + topConditions.size());
                        System.out.println("Processing total size of " + recordResultSet.size());


                        topConditions.forEach(record -> {
                            saveOutput.add(record.convertToStringList());
                        });

                        String outputDir = "data/bucketed/after_bucketed_concept_2021_04_Vivli_NCT_ID_studies.csv";
                        CSVUtil.writeToCSV(outputDir, saveOutput);

                        String logDir = "data/bucketed/log_after_bucketed_concept_2021_04_Vivli_NCT_ID_studies.txt";
                        logs.forEach(s -> {
                            FileUtil.add2File(logDir,s +"\n");
                        });


                        

                        ArrayList<SCTConcept> validRoots = new ArrayList<>(release.getHierarchiesWithAttributeRelationships());
                        validRoots.sort(new ConceptNameComparator());



                        stateFileManager.getRecentlyOpenedOntologiesManager().eraseHistory();

                        System.out.println();
                        System.out.println("Processing Finished");

                    } catch (IOException e) {
                        // TODO: write error...
                    } catch (OAFRecentlyOpenedFileManager.RecentlyOpenedFileException ex) {
                        Logger.getLogger(TestClassifyTrialConditionsWithSNOMED.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }




    private Map<String, List<String>> readBucketsList(String fileName, SCTRelease release){


        Map<String, List<String>> resultMap = new HashMap<>();

        List<String[]> allData = Utils.readDataFromCommaSeperator(fileName);

        for (String[] row : allData) {
            String bucketName = row[0];
            System.out.println("bucket name = " + bucketName);
            List<String> idlist = new ArrayList<>();
            String conceptNames = row[1];
            String[] names = conceptNames.split("[||]");
            for (String name : names) {
                name = name.trim();
                Set<SCTConcept> optConcept = release.searchExact(name);
                optConcept.forEach(sctConcept -> {
                    System.out.println("get concept id: "+ sctConcept.getIDAsString());
                    System.out.println("get concept: "+ sctConcept.getName());
                    idlist.add(sctConcept.getIDAsString());
                });
            }
            resultMap.put(bucketName, idlist);

            System.out.println();
        }

        return resultMap;

    }

    private Map<String, List<String>> readBucketsListDefault(SCTRelease release){


        Map<String, List<String>> resultMap = new HashMap<>();

        long id = Long.parseLong("64572001");
//        Disease (disorder)  SCTID: 64572001

        Optional<SCTConcept> optConcept = release.getConceptFromId(id);

        SCTConcept rootConcept = optConcept.get();

        Set<SCTConcept> children = release.getConceptHierarchy().getChildren(rootConcept);

        for (SCTConcept child : children) {
            String bucketName = child.getName();
            System.out.println("default bucket name = " + bucketName.trim());
            List<String> idlist = Collections.singletonList(child.getIDAsString());
            resultMap.put(bucketName, idlist);
            System.out.println();
        }

        return resultMap;

    }

    private Map<String, List<String>> readBucketsListUpdated(String fileName, SCTRelease release){


        Map<String, List<String>> resultMap = new HashMap<>();

        List<String[]> allData = Utils.readDataFromCommaSeperator(fileName);

        for (String[] row : allData) {
//            System.out.println("row = " + row);
//            if(row.length !=3) continue;
            if(row.length <3) continue;
            String bucketName = row[0].trim();
            System.out.println("bucket name = " + bucketName);
            String conceptId = row[2].trim();

            if(resultMap.containsKey(bucketName)){
                resultMap.get(bucketName).add(conceptId);
            }else{
                List<String> idlist = new ArrayList<>();

                idlist.add(conceptId);
                resultMap.put(bucketName, idlist);
            }
        }

        return resultMap;

    }

    private Map<String, String> readTrialTopCondition(String fileName, SCTRelease release){

        Map<String, String> resultMap = new HashMap<>();

        List<String[]> allData = Utils.readDataFromCustomSeperator(fileName);
        for (String[] row : allData) {
            String nctid = row[0];
            System.out.println("nctid = " + nctid);
            String conceptName = row[2];
            Set<SCTConcept> optConcept = release.searchExact(conceptName);
            optConcept.forEach(sctConcept -> {
                System.out.println("get concept id: "+ sctConcept.getIDAsString());
                System.out.println("get concept: "+ sctConcept.getName());
                resultMap.put(nctid, sctConcept.getIDAsString());
            });
        }

        return resultMap;
    }

    private List<TrialRecord> readMappedTrials(String fileName, SCTRelease release){

        List<TrialRecord> resultMap = new ArrayList<>();


        List<String> nonMappedConceptNames = new ArrayList<>();

        List<String[]> allData = Utils.readDataFromCustomSeperator(fileName, 1);

        Set<String> filteredNCTids = filterNoConditionButTitle(allData);

        Set<String> originalNCTids = new HashSet<>();
        Set<String> mappedNCTIDs = new HashSet<>();

//        create clinical finding hierarchy
        long id = Long.parseLong(clinicalFindingID);
        Optional<SCTConcept> optConcept = release.getConceptFromId(id);
        if (optConcept.isPresent()) {
            SCTConcept root = optConcept.get();
            hierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(root);
        }

//      scan records

        for (String[] row : allData) {
            String nctid = row[0];
            String field = row[1];
            String term = row[2];
            String domain = row[3];
            String omopConceptID = row[4];
            String conceptCode = row[5];
            String conceptName = row[6];
            String match_socre = row[7];
            String CTgovText = row[8];
            String start_date = row[9];
            String completion_date = row[10];
            String enrollment = row[11];

            System.out.println("nctid = " + nctid);
            originalNCTids.add(nctid);

            if(conceptName.isEmpty()){
                continue;
            }

            if (omopConceptID.equalsIgnoreCase("44808122") || omopConceptID.equalsIgnoreCase("4300704")
                    || omopConceptID.equalsIgnoreCase("35610331")){
                conceptCode = "109969005";
                conceptName = "Diffuse non-Hodgkin's lymphoma, large cell";
            }

            if(omopConceptID.equalsIgnoreCase("4095285")){
                conceptCode = "1153570009";
                conceptName = "Treatment resistant depression";
            }

            Set<SCTConcept> optConcepts = release.searchExact(conceptName);

            if(optConcepts.size()==0){
                if(conceptName.toLowerCase().contains("metastatic")){
                    String concetpNameUpdated = conceptName;
                    concetpNameUpdated = concetpNameUpdated.replaceAll("Non-metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("non-metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("Metastatic", "");
                    concetpNameUpdated = concetpNameUpdated.replaceAll("metastatic", "");

                    optConcepts = release.searchExact(concetpNameUpdated.trim());
                }

            }
            // if no concept returned, search term text without hyphen
            if(optConcepts.size()==0){
                String aterm = CTgovText;
                aterm = aterm.replaceAll("-", " ");
                optConcepts = release.searchExact(aterm);
            }

            if(optConcepts.size()==0){
                optConcepts = release.searchID(conceptCode);
            }

            if(optConcepts.size()==0){
//                System.out.println( "No concept found for name "+ conceptName);

                if(term.contains("Healthy")||term.contains("healthy")){  // handle exception of condition including "Healthy"
                    conceptCode = "102509001";
                    optConcepts = release.searchID(conceptCode);
                }else{
                    nonMappedConceptNames.add(nctid + "\t" + conceptName);
                }
            }

            String finalConceptName = conceptName;
            if(optConcepts.size()>1){
                optConcepts.forEach(sctConcept -> {
                    if (checkDomain(hierarchy, sctConcept)) {
                        String snomedConceptID = sctConcept.getIDAsString();
                        String snomdeConceptName = sctConcept.getName();
                        System.out.println("get concept id: " + sctConcept.getIDAsString());
                        System.out.println("get concept: " + sctConcept.getName());
                        if (filteredNCTids.contains(nctid) || field.equalsIgnoreCase("condition_field")) {
                            resultMap.add(new TrialRecord(nctid, field, term, domain, omopConceptID, snomedConceptID, snomdeConceptName, match_socre, CTgovText, start_date, completion_date, enrollment));
                            mappedNCTIDs.add(nctid);
                        }
                    }
                });
            }else{
                optConcepts.forEach(sctConcept -> {
                        String snomedConceptID = sctConcept.getIDAsString();
                        String snomdeConceptName = sctConcept.getName();
                        System.out.println("get concept id: " + sctConcept.getIDAsString());
                        System.out.println("get concept: " + sctConcept.getName());
                        if (filteredNCTids.contains(nctid) || field.equalsIgnoreCase("condition_field")) {
                            resultMap.add(new TrialRecord(nctid, field, term, domain, omopConceptID, snomedConceptID, snomdeConceptName, match_socre, CTgovText, start_date, completion_date, enrollment));
                            mappedNCTIDs.add(nctid);
                        }
                });
            }


        }

        System.out.println("originalNCTids size = " + originalNCTids.size());
        logs.add("original NCT size = " + originalNCTids.size());
        originalNCTids.removeAll(mappedNCTIDs);
        logs.add("Size of NCTs failed to map to SNOMED concept = " + originalNCTids.size());
        originalNCTids.forEach(nctid->{
            System.out.println("Nctids due to no mapped to SNOMED concept = " + nctid);
            logs.add("Nctids due to no mapped to SNOMED concept = " + nctid);
        });

        nonMappedConceptNames.forEach(conceptName->{
            System.out.println("Concept names not mapped to SNOMED = " + conceptName);
            logs.add("Concept names not mapped to SNOMED = " + conceptName);
        });

        return resultMap;
    }


    private boolean checkDomain(Hierarchy<SCTConcept> hierarchy, SCTConcept sct) {
        ArrayList<ArrayList<SCTConcept>> allPathsTo = hierarchy.getAllPathsTo(sct);
        if (allPathsTo.size() > 0) return true;
        else return false;
    }

    private Set<String> filterNoConditionButTitle(List<String[]> allData){

        Map<String, Set<String>> nctidsNoCond = new HashMap<>();
        Map<String, Set<String>> nctidsNoTitle = new HashMap<>();
        Set<String> nctidsKept = new HashSet<>();
        Set<String> nctids = new HashSet<>();

        for (String[] row : allData) {

            String nctid = row[0];
            String field = row[1];
            String term = row[2];
            String conceptName = row[5];

            nctids.add(nctid);

            if(field.equalsIgnoreCase("official_title")){
                if(nctidsNoTitle.containsKey(nctid)){
                    nctidsNoTitle.get(nctid).add(conceptName);
                }else {
                    nctidsNoTitle.put(nctid, new HashSet<>(Arrays.asList(conceptName)));
                }
            }else{
                if(nctidsNoCond.containsKey(nctid)){
                    nctidsNoCond.get(nctid).add(conceptName);
                }else {
                    nctidsNoCond.put(nctid, new HashSet<>(Arrays.asList(conceptName)));
                }
            }

        }

        Set<String> filteredNCTids = new HashSet<>();

        nctids.forEach(nctid->{
            if(nctidsNoCond.containsKey(nctid)){

                Boolean conFlag = false;

                for (String s : nctidsNoCond.get(nctid)) {
                    if (!s.isEmpty()){
                        conFlag = true;
                        break;
                    }
                }

                if (!conFlag){
                    if (nctidsNoTitle.containsKey(nctid)){
                        Boolean titleFlag = false;

                        for (String s : nctidsNoTitle.get(nctid)) {
                            if (!s.isEmpty()){
                                titleFlag = true;
                                break;
                            }
                        }

                        if (titleFlag){
                            filteredNCTids.add(nctid);
                        }

                    }
                }else {
                    nctidsKept.add(nctid);
                }
            }
        });

        filteredNCTids.forEach(f->{
            System.out.println("No condition but title: nctid = " + f);
        });

       nctidsKept.addAll(filteredNCTids);

       nctids.removeAll(nctidsKept);
       nctids.forEach(f->{
            System.out.println("Not mapped nctid : " + f);
       });


       return filteredNCTids;

    }


    private void processSNOOntology(SCTRelease release, SCTConcept root) {

        try {
            String ns = root.getName().replaceAll(" / ", " ");
            System.out.println("Processing: " + ns);

        } catch (Exception e) {

            System.err.println("Error occured...");

            return;
        }

        Hierarchy<SCTConcept> hierarchy = release.getConceptHierarchy().getSubhierarchyRootedAt(root);

        String conceptID ="49436004";
        String conceptName = "atrial fibrillation";

        long id = Long.parseLong(conceptID);

        Optional<SCTConcept> optConcept = release.getConceptFromId(id);
        Set<SCTConcept> optConcept2 = release.searchExact(conceptName);
        optConcept2.forEach(sctConcept -> {
            System.out.println("get concept: "+ sctConcept.getName());
        });

        if (optConcept.isPresent()) {
            SCTConcept sct = optConcept.get();
            System.out.println("root is: " +root.getName());
            ArrayList<ArrayList<SCTConcept>> allPathsTo = hierarchy.getAllPathsTo(sct);

            System.out.println("allPathsTo.size() = " + allPathsTo.size());

            allPathsTo.forEach(arr->{
                System.out.println("new path: ");
                arr.forEach(sctConcept -> System.out.print(sctConcept.getName() + " -> "));
                System.out.println();
            });

        }

//
//        hierarchy.getNodes().forEach((concept) -> {
//
//
//
//        });


    }

    private String sanitizeClassLabel(SCTConcept concept) {

        String conceptName = concept.getName();

        conceptName = conceptName.replaceAll("\t", " ");

        return conceptName;
    }


}
