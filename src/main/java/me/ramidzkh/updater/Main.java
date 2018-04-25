/*
 * Skybot Updater, an updater application for SkyBot
 *      Copyright (C) 2017, 2018  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Sanduhr32
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

package me.ramidzkh.updater;

import com.google.gson.*;
import me.ramidzkh.updater.github.GithubRequester;
import me.ramidzkh.updater.github.objects.Release;
import me.ramidzkh.updater.github.objects.RepositoryRef;
import me.ramidzkh.updater.github.objects.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static File configFile = new File(System.getProperty("user.dir"), "updater.json");
    public static String VERSION = "";

    public static void main(String[] args) throws IOException {
        System.out.println("Skybot Updater, an updater application for SkyBot");
        System.out.println("Copyright (C) 2017, 2018  Duncan \"duncte123\" Sterken & Ramid \"ramidzkh\" Khan & Sanduhr32\n\n");

        // The config object
        Config config;

        // Load it

        if (!configFile.exists()) {
            System.out.println("Creating the updater.json meta file");

            boolean success = false;

            try {
                success = configFile.createNewFile();
            } catch (IOException io) {
                System.err.println("Fatal, error during file creation");
                io.printStackTrace();

                System.exit(1);
            } catch (SecurityException security) {
                System.err.println("Fatal, error during file creation. Java's security settings doesn't allow it");
                security.printStackTrace();

                System.exit(1);
            } finally {
                if (!success) {
                    System.err.println("Fatal, error during file creation");
                    System.err.println("We could not find any reason why");

                    System.exit(1);
                }
            }

            // The config isn't ever created properly so we will predefine it
            config = Config.builder()
                    .gson(new GsonBuilder()
                            .setPrettyPrinting()
                            .create())
                    .config(new JsonObject())
                    .build();
        } else {
            Config $config = Config.builder()
                    .gson(new GsonBuilder()
                            .setPrettyPrinting()
                            .create())
                    .config(new JsonObject())
                    .build();

            if (!$config.load(configFile))
                // The error is already logged by the method Config#load
                System.exit(1);

            config = $config;
        }

        // Github stuff
        GithubRequester github = new GithubRequester();
        github.setGson(config.getGson());

        RepositoryRef repository = new RepositoryRef("duncte123", "SkyBot");

        try {
            List<Release> releases = github.getReleases(repository);
            Release release = releases.get(0);

            github.download(release, new FileOutputStream("skybot-" + VERSION + ".jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> aaaa = Arrays.asList("java");

        JsonArray erfgdcfgtrfbg = config.getOrElse("jvm", new JsonArray()).getAsJsonArray();
        List<String> dfgjfdvgetr = config.getGson().fromJson(erfgdcfgtrfbg, ArrayList.class);

        JsonArray fethrydfhv = config.getOrElse("program", new JsonArray()).getAsJsonArray();
        List<String> grvwe = config.getGson().fromJson(fethrydfhv, ArrayList.class);

        aaaa.addAll(dfgjfdvgetr);
        aaaa.add("-jar");
        aaaa.add("skybot-" + VERSION + ".jar");
        aaaa.addAll(grvwe);

        // Loop
        Runnable ree;
        ree = () -> {
            try {
                ProcessHandler handler = new ProcessHandler(aaaa.toArray(new String[0]));
                handler.bind();

                int code = handler.returnCode();

                if(code == 0x54) {
                    try {
                        List<Release> releases = github.getReleases(repository);
                        Release release = releases.get(0);

                        github.download(release, new FileOutputStream("skybot-.jar"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (code == 0x64) {
                    String oldVersion = VERSION;
                    Files.delete(new File("skybot-" + oldVersion + ".jar").toPath());
                    int index = aaaa.indexOf("skybot-" + oldVersion + ".jar");
                    VERSION = getVersion();
                    aaaa.set(index, "skybot-" + VERSION + ".jar");
                } else System.exit(0);
            } catch (Throwable thr) {
                thr.printStackTrace();
                System.exit("no".hashCode() & 0x7F);
            }
        };

        while(true)
            ree.run();
    }
    
    private static String getVersion() {
        Process process = Runtime.getRuntime().exec(getCommand("gradlew printVersion"));
        Scanner scanner = new Scanner(process.getInputStream());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.matches("[0-9]\\.[0-9]{1,3}\\.[0-9]_.{8}")
                return line;
        }
        return "";
    }
    
    private static String getCommand(String cmd): String {
         return
             (System.getProperty("os.name").contains("Windows", false)) ? "cmd /C " + cmd : "skybotsrc/./" + cmd;
     }
}
