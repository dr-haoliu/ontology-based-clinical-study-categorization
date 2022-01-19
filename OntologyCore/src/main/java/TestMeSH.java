package main.java;

import edu.columbia.dbmi.wenglab.core.datastructure.hierarchy.Hierarchy;
import edu.columbia.dbmi.wenglab.mesh.load.LoadMeSH;
import edu.columbia.dbmi.wenglab.mesh.load.MeSHRelease;

import java.io.File;
import java.io.FileNotFoundException;

public class TestMeSH {

    public static void main(String[] args) {

        LoadMeSH lm = new LoadMeSH();

        File file = new File("D:/workspace/BLUMeSH/src/data/d2022.bin");
        try {

            MeSHRelease release = lm.loadMesh(file);
            Hierarchy hierarchy = release.getConceptHierarchy();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
