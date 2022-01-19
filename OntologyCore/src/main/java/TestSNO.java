package main.java;

import edu.columbia.dbmi.wenglab.core.utils.comparators.ConceptNameComparator;
import edu.columbia.dbmi.wenglab.core.utils.toolstate.OAFStateFileManager;
import edu.columbia.dbmi.wenglab.sno.localdatasource.concept.SCTConcept;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.LoadLocalRelease;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.LocalLoadStateMonitor;
import edu.columbia.dbmi.wenglab.sno.localdatasource.load.RF2ReleaseLoader;
import edu.columbia.dbmi.wenglab.sno.sctdatasource.SCTRelease;
import edu.columbia.dbmi.wenglab.sno.sctdatasource.SCTReleaseInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSNO {

    public static void main(String[] args) {

        File inputDirectory = new File("D:/Ontology/SNO");
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

                        ArrayList<SCTConcept> validRoots = new ArrayList<>(release.getHierarchiesWithAttributeRelationships());
                        validRoots.sort(new ConceptNameComparator());

                        validRoots.forEach((root) -> {

                            SCTConcept rootConcept = release.getConceptFromId(root.getID()).get();

                            System.out.println("rootConcept.getName() = " + rootConcept.getName());
                        });

                        long id = Long.parseLong("49601007");
//                        long id = Long.parseLong("404684003");

                        Optional<SCTConcept> optConcept = release.getConceptFromId(id);

                        SCTConcept rootConcept = optConcept.get();
                        System.out.println("rootConcept.getName() = " + rootConcept.getName());

                    } catch (IOException e) {
                        // TODO: write error...
                    }
                }
            }
        }
    }
}
