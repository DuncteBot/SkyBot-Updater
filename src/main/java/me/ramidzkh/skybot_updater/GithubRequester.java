/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Sanduhr32
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.ramidzkh.skybot_updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class GithubRequester {
    
    public static JsonObject getLatestRelease(OkHttpClient client)
            throws IOException {
        Request request = new Request.Builder()
                              // The URL of the releases API
                              .url("https://api.github.com/repos/duncte123/SkyBot/releases")
                              // All programs using the GitHub API needs a User-Agent
                              .header("User-Agent", "Skybot auto updater")
                              .build();
        Response response = client.newCall(request).execute();
        
        JsonArray releases = new JsonParser().parse(response.body().source().readUtf8()).getAsJsonArray();
        
        for (JsonElement release : releases)
            return release.getAsJsonObject();
        
        new IllegalStateException("Unable to find a release").printStackTrace();
        System.exit(1);
        
        return null;
    }
    
    public static JsonObject getAsset(JsonObject release) {
        for (JsonElement asset : release.get("assets").getAsJsonArray()) {
            JsonObject jo = asset.getAsJsonObject();
            // Matches skybot-x((.x*).x).jar
            if (jo.get("name").getAsString().matches("((?i)skybot)-((\\d*\\.)*\\d*)_([A-z0-9]{8})\\.jar")) {
                try {
                    new UpdateInfo(release, jo).save();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return jo;
            }
        }
        
        new IllegalStateException("Unable to find an asset in " + release.toString()).printStackTrace();
        System.exit(1);
        
        return null;
    }
    
    public static void download(JsonObject release, OutputStream out)
            throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) new URL(
                                    release.get("browser_download_url").getAsString()).openConnection();
        
        con.addRequestProperty("Connection", "keep-alive");
        con.addRequestProperty("Content-Type", "application/octet-stream");
        
        con.connect();
        
        InputStream in = con.getInputStream();
        
        System.out.printf("%s bytes to be downloaded from %s%n", release.get("size").getAsLong(),
                release.get("browser_download_url").getAsString());
        
        byte[] buf = new byte[1024];
        int len;
        
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        
        long size = Main.file.length();
        boolean correctSize = size == release.get("size").getAsLong();
        
        assert correctSize : "Invalid size. Corrupt file? (Size should be " + release.get("size").getAsLong() + " bytes)";
        
        System.out.println("Successfully downloaded the latest JAR file for SkyBot!");
    }
    
    public static void downloadLatest(OkHttpClient client, OutputStream out)
            throws IOException {
        download(getAsset(getLatestRelease(client)), out);
    }
}
