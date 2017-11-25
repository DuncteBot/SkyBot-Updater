package me.ramidzkh.skybot_updater;

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
        
        if(ev.length == 0
                && pv.length == 0)
            l = new String[] {Main.java, "-jar", "skybot.jar"};
        else if (ev.length == 0) {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java, "-jar", "skybot.jar"));
            ll.addAll(Arrays.asList(pv));
            l = ll.toArray(new String[0]);
        } else if(pv.length == 0) {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java));
            ll.addAll(Arrays.asList(ev));
            ll.addAll(Arrays.asList("-jar", "skybot.jar"));
            l = ll.toArray(new String[0]);
        } else {
            List<String> ll = new ArrayList<>(Arrays.asList(Main.java));
            ll.addAll(Arrays.asList(ev));
            ll.addAll(Arrays.asList("-jar", "skybot.jar"));
            ll.addAll(Arrays.asList(pv));
            l = ll.toArray(new String[0]);
        }
        
        System.out.println(Arrays.toString(l));
        
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
