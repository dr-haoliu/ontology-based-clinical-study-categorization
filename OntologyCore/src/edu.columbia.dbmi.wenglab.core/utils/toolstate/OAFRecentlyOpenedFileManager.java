package edu.columbia.dbmi.wenglab.core.utils.toolstate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 */
public class OAFRecentlyOpenedFileManager {
    
    public static class RecentlyOpenedFileException extends Exception {
        public RecentlyOpenedFileException(String message) {
            super(message);
        }
    }
    
    private final int recentFileLimit;
    
    private final File recentFilesFile;
    
    private final ArrayList<RecentlyOpenedFile> recentlyOpenedFiles;
    
    public OAFRecentlyOpenedFileManager(
            File recentFilesFile,
            int recentFileLimit) throws RecentlyOpenedFileException {

        this.recentFilesFile = recentFilesFile;

        this.recentFileLimit = recentFileLimit;

        ensureInitialized();

        this.recentlyOpenedFiles = new ArrayList<>();

        try {
            this.recentlyOpenedFiles.addAll(loadRecentlyOpened());
        } catch (RecentlyOpenedFileException rofe) {

        }
    }
    
    public OAFRecentlyOpenedFileManager(
            String appDataSubDir, 
            String fileType, 
            int fileLimit) throws RecentlyOpenedFileException {
        
        this(new File(String.format("%s\\%s\\%s", OAFStateFileManager.ROOT_FOLDER_DIR, appDataSubDir, fileType)), fileLimit);
    }
    
    private void ensureInitialized() throws RecentlyOpenedFileException {
        
        if(recentFilesFile == null) {
            throw new RecentlyOpenedFileException(
                    "Specified recent files config file is null: " 
                    + recentFilesFile.getAbsolutePath());
        }
        
        File parentFile = recentFilesFile.getParentFile();
        
        if(parentFile == null) {
            throw new RecentlyOpenedFileException("Parent file is null: " + recentFilesFile.getAbsolutePath());
        }
        
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new RecentlyOpenedFileException("Cannot create required appdata folders: " + recentFilesFile.getAbsolutePath());
            }
        }
        
        if (!FileUtilities.ensureFileExistsAndWritable(this.recentFilesFile)) {
            throw new RecentlyOpenedFileException("Cannot create required recently opened files configuration file.");
        }
    }
    
    public int getRecentFileLimit() {
        return recentFileLimit;
    }
    
    public ArrayList<RecentlyOpenedFile> getRecentlyOpenedFiles() {
        return new ArrayList<>(this.recentlyOpenedFiles);
    }
    
    public ArrayList<RecentlyOpenedFile> getRecentlyOpenedFiles(int maxCount) {
        int lastIndex = Math.min(maxCount, recentlyOpenedFiles.size());
        
        return new ArrayList<>(recentlyOpenedFiles.subList(0, lastIndex));
    }
    
    private ArrayList<RecentlyOpenedFile> loadRecentlyOpened() throws RecentlyOpenedFileException {
        
        String recentlyOpenedFilesStr = "";
        
        try(Scanner scanner = new Scanner(this.recentFilesFile)) {
            while(scanner.hasNext()) {
                recentlyOpenedFilesStr += scanner.nextLine();
            }
        } catch(IOException ioe) {
            throw new RecentlyOpenedFileException("Error opening recently opened files configuration file.");
        }

        if (recentlyOpenedFilesStr.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            JSONParser parser = new JSONParser();
            JSONArray recentlyOpenedFilesJSON = (JSONArray) parser.parse(recentlyOpenedFilesStr);
            
            ArrayList<RecentlyOpenedFile> result = new ArrayList<>();
            
            for(Object obj : recentlyOpenedFilesJSON) {
                JSONObject jsonObj = (JSONObject)obj;
                
                String pathStr = jsonObj.get("path").toString();
                String dateStr = jsonObj.get("date").toString();
                
                File file = new File(pathStr);
                Date date = new Date(Long.parseLong(dateStr));
                
                if(file.exists()) {
                    result.add(new RecentlyOpenedFile(file, date));
                }
            }

            result.sort((a, b) -> -a.getDate().compareTo(b.getDate()));

            return result;
            
        } catch (ParseException pe) {
            throw new RecentlyOpenedFileException("Error parsing recently opened files list.");
        }
    }
    
    public void eraseHistory() throws RecentlyOpenedFileException {        
        recentlyOpenedFiles.clear();
        
        saveRecentlyOpenedFilesList();
    }
    
    public void addOrUpdateRecentlyOpenedFile(File file) throws RecentlyOpenedFileException {
        
        ArrayList<RecentlyOpenedFile> recentFiles = this.getRecentlyOpenedFiles();

        Optional<RecentlyOpenedFile> existingFile = recentFiles.stream().filter( (recentlyOpenedFile) -> {
            return recentlyOpenedFile.getFile().equals(file);
        }).findAny();

        if(existingFile.isPresent()) {
            existingFile.get().updateLastOpened(new Date());
        } else {
            recentFiles.add(new RecentlyOpenedFile(file, new Date()));
        }
        
        recentFiles.sort((a, b) -> -a.getDate().compareTo(b.getDate()));
        
        List<RecentlyOpenedFile> sublist = recentFiles.subList(0, Math.min(recentFileLimit, recentFiles.size()));
        
        this.recentlyOpenedFiles.clear();
        this.recentlyOpenedFiles.addAll(sublist);
  
        saveRecentlyOpenedFilesList();
    }
    
    private void saveRecentlyOpenedFilesList() throws RecentlyOpenedFileException {

        if (!FileUtilities.ensureFileExistsAndWritable(this.recentFilesFile)) {
            throw new RecentlyOpenedFileException("Cannot create recently opened files configuration file.");
        }

        try (PrintWriter writer = new PrintWriter(this.recentFilesFile)) {
            writer.println(toJSON());
        } catch (FileNotFoundException fnfe) {
            throw new RecentlyOpenedFileException("Error opening recently opened files configuration file.");
        }
    }
    
    private JSONArray toJSON() {
        JSONArray result = new JSONArray();
        
        recentlyOpenedFiles.forEach( (file) -> {
            result.add(file.toJSON());
        });
        
        return result;
    }
}
