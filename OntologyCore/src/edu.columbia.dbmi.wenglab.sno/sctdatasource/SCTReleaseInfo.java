package edu.columbia.dbmi.wenglab.sno.sctdatasource;

import java.io.File;

/**
 * Class for storing various properties of a SNOMED CT release 
 *
 */
public class SCTReleaseInfo {
    public static enum ReleaseType {
        International,
        USExtension,
        Unknown
    }
    
    public static enum ReleaseFormat {
        RF1,
        RF2
    }
    
    private final ReleaseType releaseType;
    private final ReleaseFormat releaseFormat;
    
    private final int releaseMonth;
    private final int releaseYear;
    
    private final String releaseName;
    
    private final File releaseDirectory;
    
    public SCTReleaseInfo(
            File releaseDirectory, 
            String releaseName) {
        
        this.releaseDirectory = releaseDirectory;
        
        this.releaseName = releaseName;
        
        if(releaseName.startsWith("INT")) {
            this.releaseType = ReleaseType.International;
        } else if(releaseName.startsWith("US")) {
            this.releaseType = ReleaseType.USExtension;
        } else {
            this.releaseType = ReleaseType.Unknown;
        }
        
        if(releaseName.endsWith("(RF2)")) {
            this.releaseFormat = ReleaseFormat.RF2;
        } else {
            this.releaseFormat = ReleaseFormat.RF1;
        }
        
        if(releaseName.contains(" ")) {
            String releaseNumberStr = releaseName.split(" ")[1].trim();

            int releaseNum = Integer.parseInt(releaseNumberStr);
            
            this.releaseYear = releaseNum / 10000;
            
            this.releaseMonth = (releaseNum % 10000) / 100;
        } else {
            this.releaseYear = 0;
            this.releaseMonth = 0;
        }
    }
    
    public File getReleaseDirectory() {
        return releaseDirectory;
    }

    public ReleaseType getReleaseType() {
        return releaseType;
    }

    public ReleaseFormat getReleaseFormat() {
        return releaseFormat;
    }

    public int getReleaseMonth() {
        return releaseMonth;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getReleaseName() {
        return releaseName;
    }
    
}
