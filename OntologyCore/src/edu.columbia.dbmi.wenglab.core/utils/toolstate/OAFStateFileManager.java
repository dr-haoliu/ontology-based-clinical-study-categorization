package edu.columbia.dbmi.wenglab.core.utils.toolstate;

import edu.columbia.dbmi.wenglab.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing persistance/state files.
 *
 */
public class OAFStateFileManager {
    
    public static final String ROOT_FOLDER_DIR = "oaf-appdata";
    
    private OAFRecentlyOpenedFileManager recentlyOpenedOntologiesManager;
    
    private OAFOntologySpecificFileManager recentAbNWorkspaceManager;
    
    private OAFOntologySpecificFileManager recentNATWorkspaceManager;
    
    private OAFOntologySpecificFileManager recentAuditSetsManager;
    
    private final Map<File, OAFRecentlyOpenedFileManager> recentAuditSetFileManagers;
    private final Map<File, OAFRecentlyOpenedFileManager> recentAbNWorkspaceFileManagers;
    private final Map<File, OAFRecentlyOpenedFileManager> recentNATWorkspaceFileManagers;
    
    
    // Try to initialize once on OAF startup
    private boolean initialized = false;
    
    public OAFStateFileManager(String toolName) {

        try {
            
            this.recentlyOpenedOntologiesManager = new OAFRecentlyOpenedFileManager(toolName, "recently_opened_ontologies", 10);

            this.recentAbNWorkspaceManager = new OAFOntologySpecificFileManager(
                    String.format("%s\\recentAbNWorkspace", toolName), "recentAbNWorkspace");

            this.recentNATWorkspaceManager = new OAFOntologySpecificFileManager(
                    String.format("%s\\recentNATWorkspace", toolName), "recentNATWorkspace");

            this.recentAuditSetsManager = new OAFOntologySpecificFileManager(
                    String.format("%s\\recentAuditSets", toolName), "recentAuditSets");
            
            this.initialized = true;
            
        } catch (RecentlyOpenedFileException rofe) {
            this.initialized = false;
        }

        this.recentAuditSetFileManagers = new HashMap<>();
        this.recentAbNWorkspaceFileManagers = new HashMap<>();
        this.recentNATWorkspaceFileManagers = new HashMap<>();
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    private void ensureInitialized() throws RecentlyOpenedFileException {
        if(!this.isInitialized()) {
            throw new RecentlyOpenedFileException("State File Manager not initialized.");
        }
    }
    
    private void ensureNotNull(File ontologyFile) throws RecentlyOpenedFileException {
        if(ontologyFile == null) {
            throw new RecentlyOpenedFileException("Cannot load recent files for null ontology file.");
        }
    } 
    
    public OAFRecentlyOpenedFileManager getRecentlyOpenedOntologiesManager() throws RecentlyOpenedFileException {
        ensureInitialized();
        
        return recentlyOpenedOntologiesManager;
    }

    public OAFRecentlyOpenedFileManager getRecentlyOpenedAuditSets(File ontologyFile) throws RecentlyOpenedFileException {
        
        ensureInitialized();
        ensureNotNull(ontologyFile);

        if (!recentAuditSetFileManagers.containsKey(ontologyFile)) {
            recentAuditSetFileManagers.put(ontologyFile,
                    new OAFRecentlyOpenedFileManager(
                            this.recentAuditSetsManager.addOrGetOntologyFile(ontologyFile), 10));
        }

        return recentAuditSetFileManagers.get(ontologyFile);
    }
    

    public OAFRecentlyOpenedFileManager getRecentAbNWorkspaces(File ontologyFile) throws RecentlyOpenedFileException {
        
        ensureInitialized();
        ensureNotNull(ontologyFile);

        if (!recentAbNWorkspaceFileManagers.containsKey(ontologyFile)) {
            recentAbNWorkspaceFileManagers.put(ontologyFile,
                    new OAFRecentlyOpenedFileManager(
                            this.recentAbNWorkspaceManager.addOrGetOntologyFile(ontologyFile), Integer.MAX_VALUE));
        }

        return recentAbNWorkspaceFileManagers.get(ontologyFile);
    }
    

    public OAFRecentlyOpenedFileManager getRecentNATWorkspaces(File ontologyFile) throws RecentlyOpenedFileException {
        
        ensureInitialized();
        ensureNotNull(ontologyFile);

        if (!recentNATWorkspaceFileManagers.containsKey(ontologyFile)) {
            recentNATWorkspaceFileManagers.put(ontologyFile,
                    new OAFRecentlyOpenedFileManager(
                            this.recentNATWorkspaceManager.addOrGetOntologyFile(ontologyFile), Integer.MAX_VALUE));
        }

        return recentNATWorkspaceFileManagers.get(ontologyFile);
    }
}
