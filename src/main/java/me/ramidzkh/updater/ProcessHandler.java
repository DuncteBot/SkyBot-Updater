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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessHandler {

    Process process;

    public ProcessHandler(String[] ev, String[] pv)
            throws IOException {
        String[] l;

        if (ev.length == 0
                && pv.length == 0)
            l = new String[]{Main.java, "-Dupdater", "-jar", "skybot.jar"};
        else if (ev.length == 0) {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java, "-Dupdater", "-jar", "skybot.jar", "use-updater"));
            ll.addAll(Arrays.asList(pv));
            l = ll.toArray(new String[0]);
        } else if (pv.length == 0) {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java));
            ll.addAll(Arrays.asList(ev));
            ll.addAll(Arrays.asList("-Dupdater", "-jar", "skybot.jar", "use-updater"));
            l = ll.toArray(new String[0]);
        } else {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java));
            ll.addAll(Arrays.asList(ev));
            ll.addAll(Arrays.asList("-Dupdater", "-jar", "skybot.jar", "use-updater"));
            ll.addAll(Arrays.asList(pv));
            l = ll.toArray(new String[0]);
        }

        process = new ProcessBuilder()
                .command(l)
                .directory(new File(System.getProperty("user.dir")))
                .inheritIO()
                .start();
    }

    public void bind() {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

    public int returnCode() {
        return process.exitValue();
    }
}
