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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.ramidzkh.updater.github.GithubRequester;
import me.ramidzkh.updater.github.objects.Release;
import me.ramidzkh.updater.github.objects.RepositoryRef;
import me.ramidzkh.updater.github.objects.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static File configFile = new File(System.getProperty("user.dir"), "updater.json");

    public static void main(String[] args) {
        System.out.println("\tSkybot Updater, an updater application for SkyBot");
        System.out.println("\tCopyright (C) 2017, 2018  Duncan \"duncte123\" Sterken & Ramid \"ramidzkh\" Khan & Sanduhr32\n\n");

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

        boolean check = config.getOrElse("autocheck", new JsonPrimitive(true)).getAsJsonPrimitive().getAsBoolean();

        if(!check)
            check = config.getNested("version").getConfig().equals(new JsonObject());
        
    }
}
