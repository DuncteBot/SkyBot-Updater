package me.ramidzkh.skybot_updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

public final class UpdateInfo {
    
    public static final File file = new File("updater.json");
    
    private final String ver, dl;
    private final long id, len;
    
    public UpdateInfo(String ver, long id, String dl, long len) {
        this.ver = ver;
        this.id = id;
        this.dl = dl;
        this.len = len;
    }
    
    public UpdateInfo(JsonObject release, JsonObject asset) {
        this(release.get("name").getAsString(),
                release.get("id").getAsLong(),
                asset.get("browser_download_url").getAsString(),
                asset.get("size").getAsLong());
    }
    
    public static UpdateInfo load() {
        try {
            // If only one of them exists
            if(!Main.file.exists() ^ !file.exists()) {
                Main.file.delete();
                file.delete();
                return null;
            }
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            UpdateInfo info = new Gson().fromJson(br, UpdateInfo.class);
            br.close();
            return info;
        } catch (Throwable thr) {
            return null;
        }
    }
    
    public void save() throws IOException {
        if (file.exists())
            file.delete();
        
        // Save
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        
        // Write it
        new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .create().toJson(this, UpdateInfo.class, bw);
        
        // Close it, flush all resources
        bw.close();
    }
    
    public String getVersion() {
        return ver;
    }
    
    public String getDownloadLink() {
        return dl;
    }
    
    public long getId() {
        return id;
    }
    
    public long getSize() {
        return len;
    }
    
    public boolean equals(Object o) {
        if(o == null) return false;
        if(this == o) return true;
        
        if(o.getClass() != UpdateInfo.class) return false;
        
        UpdateInfo ui = (UpdateInfo) o;
        
        try {
            return ui.id == id && ui.ver.equals(ver) && ui.dl.equals(dl) && ui.len == len;
        } catch (NullPointerException e) {return false;}
    }
}
