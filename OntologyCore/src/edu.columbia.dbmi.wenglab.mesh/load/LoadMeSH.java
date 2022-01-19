package edu.columbia.dbmi.wenglab.mesh.load;


import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.mesh.concept.Description;
import edu.columbia.dbmi.wenglab.mesh.concept.MeSHConcept;
import edu.columbia.dbmi.wenglab.mesh.load.MeSHRelease;
import edu.columbia.dbmi.wenglab.mesh.utils.GenerateUUID;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static edu.columbia.dbmi.wenglab.mesh.utils.RegexMatch.*;


public class LoadMeSH {
    public static void main(String[] args) {

        LoadMeSH lm = new LoadMeSH();

        File file = new File("src/data/d2022.bin");
        try {
//            HashMap<Long, MeSHConcept> concepts = lm.loadConcept(file);
//            MeSHRelease release = lm.loadMeshUUID(file);
            MeSHRelease release = lm.loadMesh(file);
            Hierarchy hierarchy = release.getConceptHierarchy();

            System.out.println("hierarchy.size() = " + hierarchy.size());
            MeSHConcept root_concept = release.getConceptFromId(1720342000000009369L).get();  // Neoplasms
            System.out.println("root_concept.getName() = " + root_concept.getName());

            Hierarchy subhierarchy = hierarchy.getSubhierarchyRootedAt(root_concept);
            System.out.println("subhierarchy.size() = " + subhierarchy.size());


//            String conceptName = "Bacteria, Thermoduric";  //Key: 1720342000000000994, Value: Bacteria, Thermoduric
            String conceptName = "Triple Negative Breast Neoplasms";
//            String conceptName = "Nervous System Neoplasms";  // Key: 1720342000000011797, Value: Nervous System Neoplasms
            Set<MeSHConcept> optConcepts = release.searchExact(conceptName);
            System.out.println("optConcepts.size() = " + optConcepts.size());

            optConcepts.forEach(meshConcept -> {
                String meshConceptID = meshConcept.getIDAsString();
                String meshConceptName = meshConcept.getName();
                System.out.println("get concept id: " + meshConcept.getIDAsString());
                System.out.println("get concept: " + meshConcept.getName());

//                MeSHConcept mconcept = release.getConceptFromId(1720342000000004650L).get();

                ArrayList<ArrayList<MeSHConcept>> allPathsTo = hierarchy.getAllPathsTo(meshConcept);

                System.out.println("allPathsTo.size() = " + allPathsTo.size());

                allPathsTo.forEach(arr -> {
                    System.out.println("new path: ");
                    arr.forEach(sctConcept -> System.out.print(sctConcept.getName() + " -> "));
                    System.out.println();
                });

                allPathsTo = subhierarchy.getAllPathsTo(meshConcept);

                System.out.println("Sub allPathsTo.size() = " + allPathsTo.size());

                allPathsTo.forEach(arr -> {
                    System.out.println("new path: ");
                    arr.forEach(sctConcept -> System.out.print(sctConcept.getName() + " -> "));
                    System.out.println();
                });

            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public MeSHRelease loadMesh(File conceptsFile) throws FileNotFoundException {
        HashMap<Long, MeSHConcept> concepts = new HashMap();
        Hierarchy hierarchy = null;

        Long MeSH_root_id = GenerateUUID.getFixedLong();
        MeSHConcept root_concept = new MeSHConcept(MeSH_root_id);
        root_concept.setDescriptions(Collections.singleton(new Description("MeSH_ROOT", 0)));
        concepts.put(MeSH_root_id, root_concept);

        try {
            BufferedReader in = new BufferedReader(new FileReader(conceptsFile));
            Throwable var4 = null;

            try {
//                in.readLine();
                int processedConcepts = 0;
                Map<Long, String> terms = new HashMap<>();
                Map<Long, List<String>> tree_numbers = new HashMap<>();
                Map<String, Long> reverse_tree = new HashMap<>();
                Map<Long, List<String>> desc_dict = new HashMap<>();

                String line;
                Long fixed_uid = GenerateUUID.getFixedLong();
                int id_offset = 1;
                String mesh_term = "";
                List<String> tree_number_list = new ArrayList<>();
                List<String> alter_name_list = new ArrayList<>();

                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("*NEWRECORD")) {

                        mesh_term = "";
                        tree_number_list = new ArrayList<>();
                        alter_name_list = new ArrayList<>();
                    }

                    if (getMeshHeading(line).size() > 0) {
                        mesh_term = getMeshHeading(line).get(0);
                    }

                    if (getMeshUniqueID(line).size() > 0) {
                        id_offset = Integer.parseInt(getMeshUniqueID(line).get(0));

                        Long uid = fixed_uid + id_offset;

                        terms.put(uid, mesh_term);

                        for (String tree_number : tree_number_list) {
                            tree_numbers.computeIfAbsent(uid, k -> new ArrayList<>()).add(tree_number);
                            reverse_tree.put(tree_number, uid);
                        }

                        for (String alter_name : alter_name_list) {
                            desc_dict.computeIfAbsent(uid, k -> new ArrayList<>()).add(alter_name);
                        }
                    }

                    if (getMeshNumber(line).size() > 0) {
                        String tree_number = getMeshNumber(line).get(0);
                        tree_number_list.add(tree_number);

                    }
                    if (getMeshEntry(line).size() > 0) {
                        String alter_name_long = getMeshEntry(line).get(0);
                        String alter_name = alter_name_long.split("\\|")[0];
                        alter_name_list.add(alter_name);
                    }

                }
                System.out.println("Finish reading file");
                System.out.println("terms.size() = " + terms.size());
                System.out.println("tree_numbers.size() = " + tree_numbers.size());
                System.out.println("reverse_tree.size() = " + reverse_tree.size());
                System.out.println("desc_dict.size() = " + desc_dict.size());


                // male and female no mesh id
//                terms.keySet().removeAll(tree_numbers.keySet());
//
//                for(Long id : terms.keySet()){
//                    System.out.println("terms.get(id) = " + terms.get(id));
//                }
                for (Long key : terms.keySet()) {
                    String term = terms.get(key);
                    if (term.equalsIgnoreCase("Male") || term.equalsIgnoreCase("Female")) continue;
                    MeSHConcept concept = new MeSHConcept(key);
                    List<Description> descriptions = new ArrayList<>();
                    // Mesh heading type 0
                    descriptions.add(new Description(term, 0));
                    List<String> descs = desc_dict.get(key);
                    if (descs != null && descs.size() > 0) {
                        for (String desc : descs) {
                            // Synonyms type 1
                            descriptions.add(new Description(desc, 1));
                        }
                    }
                    for (String tr_num : tree_numbers.get(key)) {
                        // tree number, type 2
                        descriptions.add(new Description(tr_num, 2));
                    }

                    concept.setDescriptions(descriptions.stream().collect(Collectors.toSet()));
                    System.out.println("Key: " + key + ", Value: " + terms.get(key));
                    concepts.put(key, concept);
                    ++processedConcepts;
                }

                hierarchy = new Hierarchy(concepts.get(MeSH_root_id));

                int processedRelations = 0;
                for (Long key : tree_numbers.keySet()) {
                    List<String> numbers = tree_numbers.get(key);
                    for (String number : numbers) {
                        if (number.lastIndexOf(".") != -1) {
                            String parent_number = number.substring(0, number.lastIndexOf("."));
                            if (reverse_tree.containsKey(parent_number)) {
                                Long parent_id = reverse_tree.get(parent_number);
                                Long child_id = key;
                                MeSHConcept child = (MeSHConcept) concepts.get(child_id);
                                MeSHConcept parent = (MeSHConcept) concepts.get(parent_id);
                                hierarchy.addEdge(child, parent);
                                processedRelations ++;
                            }

                        } else {
                            //  top level mesh concepts?
                            Long parent_id = MeSH_root_id;
                            Long child_id = key;
                            MeSHConcept child = (MeSHConcept) concepts.get(child_id);
                            MeSHConcept parent = (MeSHConcept) concepts.get(parent_id);
                            hierarchy.addEdge(child, parent);
                            processedRelations ++;
                        }

                    }

                }


                System.out.println("PROCESSED CONCEPTS: " + processedConcepts);
                System.out.println("PROCESSED RELATIONSHIPS: " + processedRelations);
            } catch (Throwable var22) {
                var4 = var22;
                throw var22;
            } finally {
                if (in != null) {
                    if (var4 != null) {
                        try {
                            in.close();
                        } catch (Throwable var21) {
                            var4.addSuppressed(var21);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        } catch (IOException var24) {
            var24.printStackTrace();
        }

        MeSHRelease release = new MeSHRelease(hierarchy, concepts.values().stream().collect(Collectors.toSet()));

        return release;
    }

    private MeSHRelease loadMeshUUID(File conceptsFile) throws FileNotFoundException {
        HashMap<Long, MeSHConcept> concepts = new HashMap();
        Hierarchy hierarchy = null;

        Long MeSH_root_id = GenerateUUID.getFixedLong();
        MeSHConcept root_concept = new MeSHConcept(MeSH_root_id);
        concepts.put(MeSH_root_id, root_concept);

        try {
            BufferedReader in = new BufferedReader(new FileReader(conceptsFile));
            Throwable var4 = null;

            try {
//                in.readLine();
                int processedConcepts = 0;
                Map<Long, String> terms = new HashMap<>();
                Map<Long, List<String>> tree_numbers = new HashMap<>();
                Map<String, Long> reverse_tree = new HashMap<>();
                Map<Long, List<String>> desc_dict = new HashMap<>();

                String line;
                Long uid = GenerateUUID.getFixedLong();
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("*NEWRECORD")) {
                        uid = uid + 1;
                    }
                    if (getMeshHeading(line).size() > 0) {
                        terms.put(uid, getMeshHeading(line).get(0));
                    }

                    if (getMeshNumber(line).size() > 0) {
                        String tree_number = getMeshNumber(line).get(0);
                        tree_numbers.computeIfAbsent(uid, k -> new ArrayList<>()).add(tree_number);
                        reverse_tree.put(tree_number, uid);
                    }
                    if (getMeshEntry(line).size() > 0) {
                        String alter_name_long = getMeshEntry(line).get(0);
                        String alter_name = alter_name_long.split("\\|")[0];
                        desc_dict.computeIfAbsent(uid, k -> new ArrayList<>()).add(alter_name);
                    }


                }
                System.out.println("Finish reading file");
                System.out.println("terms.size() = " + terms.size());
                System.out.println("tree_numbers.size() = " + tree_numbers.size());
                System.out.println("reverse_tree.size() = " + reverse_tree.size());
                System.out.println("desc_dict.size() = " + desc_dict.size());


                // male and female no mesh id
//                terms.keySet().removeAll(tree_numbers.keySet());
//
//                for(Long id : terms.keySet()){
//                    System.out.println("terms.get(id) = " + terms.get(id));
//                }
                for (Long key : terms.keySet()) {
                    String term = terms.get(key);
                    if (term.equalsIgnoreCase("Male") || term.equalsIgnoreCase("Female")) continue;
                    MeSHConcept concept = new MeSHConcept(key);
                    List<Description> descriptions = new ArrayList<>();
                    // Mesh heading type 0
                    descriptions.add(new Description(term, 0));
                    List<String> descs = desc_dict.get(key);
                    if (descs != null && descs.size() > 0) {
                        for (String desc : descs) {
                            // Synonyms type 1
                            descriptions.add(new Description(desc, 1));
                        }
                    }
                    concept.setDescriptions(descriptions.stream().collect(Collectors.toSet()));
                    System.out.println("Key: " + key + ", Value: " + terms.get(key));
                    concepts.put(key, concept);
                    ++processedConcepts;
                }

                hierarchy = new Hierarchy(concepts.get(MeSH_root_id));


                for (Long key : tree_numbers.keySet()) {
                    List<String> numbers = tree_numbers.get(key);
                    for (String number : numbers) {
                        if (number.lastIndexOf(".") != -1) {
                            String parent_number = number.substring(0, number.lastIndexOf("."));
                            if (reverse_tree.containsKey(parent_number)) {
                                Long parent_id = reverse_tree.get(parent_number);
                                Long child_id = key;
                                MeSHConcept child = (MeSHConcept) concepts.get(child_id);
                                MeSHConcept parent = (MeSHConcept) concepts.get(parent_id);
                                hierarchy.addEdge(child, parent);
                            }

                        } else {
                            //  top level mesh concepts?
                            Long parent_id = MeSH_root_id;
                            Long child_id = key;
                            MeSHConcept child = (MeSHConcept) concepts.get(child_id);
                            MeSHConcept parent = (MeSHConcept) concepts.get(parent_id);
                            hierarchy.addEdge(child, parent);
                        }

                    }

                }


                System.out.println("PROCESSED CONCEPTS: " + processedConcepts);
            } catch (Throwable var22) {
                var4 = var22;
                throw var22;
            } finally {
                if (in != null) {
                    if (var4 != null) {
                        try {
                            in.close();
                        } catch (Throwable var21) {
                            var4.addSuppressed(var21);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        } catch (IOException var24) {
            var24.printStackTrace();
        }

        MeSHRelease release = new MeSHRelease(hierarchy, concepts.values().stream().collect(Collectors.toSet()));

        return release;
    }

    private HashMap<Long, MeSHConcept> loadConcept(File conceptsFile) throws FileNotFoundException {
        HashMap concepts = new HashMap();

        Long MeSH_root_id = GenerateUUID.getFixedLong();
        MeSHConcept root_concept = new MeSHConcept(MeSH_root_id);
        concepts.put(MeSH_root_id, root_concept);

        try {
            BufferedReader in = new BufferedReader(new FileReader(conceptsFile));
            Throwable var4 = null;

            try {
                in.readLine();
                int processedConcepts = 0;
                Map<Long, String> terms = new HashMap<>();
                Map<Long, List<String>> tree_numbers = new HashMap<>();
                Map<String, Long> reverse_tree = new HashMap<>();
                Map<Long, List<String>> desc_dict = new HashMap<>();

                String line;
                Long uid = GenerateUUID.getFixedLong();
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("*NEWRECORD")) {
                        uid = uid + 1;
                    }
                    if (getMeshHeading(line).size() > 0) {
                        terms.put(uid, getMeshHeading(line).get(0));
                    }

                    if (getMeshNumber(line).size() > 0) {
                        String tree_number = getMeshNumber(line).get(0);
                        tree_numbers.computeIfAbsent(uid, k -> new ArrayList<>()).add(tree_number);
                        reverse_tree.put(tree_number, uid);
                    }
                    if (getMeshEntry(line).size() > 0) {
                        String alter_name = getMeshEntry(line).get(0);
                        alter_name = alter_name.split("|")[0];
                        desc_dict.computeIfAbsent(uid, k -> new ArrayList<>()).add(alter_name);
                    }
                    ++processedConcepts;

                }
                System.out.println("Finish reading file");
                System.out.println("terms.size() = " + terms.size());
                System.out.println("tree_numbers.size() = " + tree_numbers.size());
                System.out.println("reverse_tree.size() = " + reverse_tree.size());
                System.out.println("desc_dict.size() = " + desc_dict.size());


                // male and female no mesh id
//                terms.keySet().removeAll(tree_numbers.keySet());
//
//                for(Long id : terms.keySet()){
//                    System.out.println("terms.get(id) = " + terms.get(id));
//                }
                for (Long key : terms.keySet()) {
                    String term = terms.get(key);
                    if (term.equalsIgnoreCase("Male") || term.equalsIgnoreCase("Female")) continue;
                    MeSHConcept concept = new MeSHConcept(key);
                    List<Description> descriptions = new ArrayList<>();
                    // Mesh heading type 0
                    descriptions.add(new Description(term, 0));
                    List<String> descs = desc_dict.get(key);
                    if (descs != null && descs.size() > 0) {
                        for (String desc : descs) {
                            // Synonyms type 1
                            descriptions.add(new Description(desc, 1));
                        }
                    }
                    concept.setDescriptions(descriptions.stream().collect(Collectors.toSet()));
                    System.out.println("Key: " + key + ", Value: " + terms.get(key));
                    concepts.put(key, concept);
                }


                System.out.println("PROCESSED CONCEPTS: " + processedConcepts);
            } catch (Throwable var22) {
                var4 = var22;
                throw var22;
            } finally {
                if (in != null) {
                    if (var4 != null) {
                        try {
                            in.close();
                        } catch (Throwable var21) {
                            var4.addSuppressed(var21);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        } catch (IOException var24) {
            var24.printStackTrace();
        }

        return concepts;
    }
}
