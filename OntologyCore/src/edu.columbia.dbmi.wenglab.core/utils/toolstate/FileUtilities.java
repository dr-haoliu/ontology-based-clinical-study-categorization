package edu.columbia.dbmi.wenglab.core.utils.toolstate;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class FileUtilities {
    
    /**
     * Makes sure the file can be saved
     * 
     * @param file
     * @return 
     */
    public static boolean ensureFileExistsAndWritable(File file) {
        boolean error = false;
        
        try {
            if(!file.createNewFile()) {
                if(!file.canWrite()) {
                    error = true;
                }
            }
        } catch(IOException e) {
            error = true;
        }
        
        return !error;
    }

}
