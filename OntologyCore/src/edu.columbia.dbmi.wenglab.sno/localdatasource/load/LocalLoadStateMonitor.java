package edu.columbia.dbmi.wenglab.sno.localdatasource.load;

/**
 *
 */
public class LocalLoadStateMonitor {
    private String currentProcessName = "Initializing";
    
    private int overallProgress = 0;
    
    public LocalLoadStateMonitor() {
        
    }
    
    public void setCurrentProcess(String name, int overallProgress) {
        this.currentProcessName = name;
        this.overallProgress = overallProgress;
    }
    
    public void setOverallProgress(int overallProgress) {
        this.overallProgress = overallProgress;
    }
    
    public String getProcessName() {
        return currentProcessName;
    }
    
    public int getOverallProgress() {
        return overallProgress;
    }
}
