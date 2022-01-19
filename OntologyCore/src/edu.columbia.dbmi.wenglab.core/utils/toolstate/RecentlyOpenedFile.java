package edu.columbia.dbmi.wenglab.core.utils.toolstate;

import java.io.File;
import java.util.Date;
import org.json.simple.JSONObject;

/**
 *
 */
public class RecentlyOpenedFile {
    
    private final File file;
    private Date date;
    
    public RecentlyOpenedFile(File file, Date date) {
        this.file = file;
        this.date = date;
    }
    
    public File getFile() {
        return file;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void updateLastOpened(Date date) {
        this.date = date;
    }
    
    public JSONObject toJSON() {
        
        JSONObject obj = new JSONObject();
        obj.put("path", file.getAbsolutePath());
        obj.put("date", date.getTime());
        
        return obj;
    }
    
}
