package edu.columbia.dbmi.wenglab.core.utils.toolstate;

import edu.columbia.dbmi.wenglab.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 */
public class OAFOntologySpecificFileManager {
    
    private final Map<File, File> ontologySpecificFiles;
    
    private final String appDataSubFolder;
    
    private final String fileType;
    
    private final File ontologyFileListFile;
    
    public OAFOntologySpecificFileManager(String appDataSubFolder, String fileType) throws RecentlyOpenedFileException {
        
        this.ontologySpecificFiles = new HashMap<>();
        this.appDataSubFolder = appDataSubFolder;
        
        this.fileType = fileType;
        
        // TODO: Make appdata location user-selectable
        this.ontologyFileListFile = new File(String.format("%s\\%s\\%s", OAFStateFileManager.ROOT_FOLDER_DIR, appDataSubFolder, fileType));
        
        ensureInitialized();
        
        try {
            this.ontologySpecificFiles.putAll(this.loadOntologyFilesList());
        } catch(RecentlyOpenedFileException rofe) {
            
        }
    }
    
    public File addOrGetOntologyFile(File ontologyFile) throws RecentlyOpenedFileException {
        
        if(!this.ontologySpecificFiles.containsKey(ontologyFile)) {
            
            String fileName = String.format("%s_%s_%s", 
                    ontologyFile.getName(),
                    Integer.toHexString(ontologyFile.getPath().hashCode()), 
                    fileType);
            
            File filesFile = new File(String.format("%s\\%s\\%s", OAFStateFileManager.ROOT_FOLDER_DIR, appDataSubFolder, fileName));
            
            if(!FileUtilities.ensureFileExistsAndWritable(filesFile)) {
                throw new RecentlyOpenedFileException("Cannot create ontology specific configuration file.");
            }
            
            saveOntologyFilesList();
            
            return filesFile;
        }
        
        return this.ontologySpecificFiles.get(ontologyFile);
    }
    
    private void ensureInitialized() throws RecentlyOpenedFileException {
        
        try {
            Files.createDirectories(Paths.get(String.format("%s\\%s", OAFStateFileManager.ROOT_FOLDER_DIR, appDataSubFolder)));
        } catch (IOException ioe) {
            throw new RecentlyOpenedFileException("Cannot create appdata folders.");
        }
        
        if(!FileUtilities.ensureFileExistsAndWritable(this.ontologyFileListFile)) {
            throw new RecentlyOpenedFileException("Cannot create ontology file configuration file.");
        }
        
    }
    
    private JSONArray toJSON() {
        JSONArray array = new JSONArray();
        
        ontologySpecificFiles.forEach( (ontologyFile, filesFile) -> {
            JSONObject obj = new JSONObject();
            
            obj.put("ontologyFile", ontologyFile.getAbsolutePath());
            obj.put("fileFile", filesFile.getAbsolutePath());
            
            array.add(obj);
        });
        
        return array;
    }
    
    private Map<File, File> loadOntologyFilesList() throws RecentlyOpenedFileException {
        
        String recentlyOpenedFilesStr = "";

        try (Scanner scanner = new Scanner(this.ontologyFileListFile)) {
            while (scanner.hasNext()) {
                recentlyOpenedFilesStr += scanner.nextLine();
            }
        } catch (IOException ioe) {
            throw new RecentlyOpenedFileException("Error opening recently opened files configuration file.");
        }

        if (recentlyOpenedFilesStr.isEmpty()) {
            return new HashMap<>();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONArray ontologyFilesJSON = (JSONArray) parser.parse(recentlyOpenedFilesStr);
            
            Map<File, File> result = new HashMap<>();

            for (Object obj : ontologyFilesJSON) {
                JSONObject jsonObj = (JSONObject) obj;

                String ontologyPathStr = jsonObj.get("ontologyFile").toString();
                String filesPathStr = jsonObj.get("fileFile").toString();

                File ontologyFile = new File(ontologyPathStr);
                File filesFile = new File(filesPathStr);
 

                if (ontologyFile.exists() && filesFile.exists()) {
                    result.put(ontologyFile, filesFile);
                }
            }

            return result;

        } catch (ParseException pe) {
            throw new RecentlyOpenedFileException("Error parsing recently opened files list.");
        }
    }
    
    private void saveOntologyFilesList() throws RecentlyOpenedFileException {

        if (!FileUtilities.ensureFileExistsAndWritable(this.ontologyFileListFile)) {
            throw new RecentlyOpenedFileException("Cannot create ontology files configuration file.");
        }

        try (PrintWriter writer = new PrintWriter(this.ontologyFileListFile)) {
            writer.println(toJSON());
        } catch (FileNotFoundException fnfe) {
            throw new RecentlyOpenedFileException("Error opening ontology files configuration file.");
        }
    }
}
