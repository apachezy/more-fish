package me.elsiff.morefish.protocol;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final String key = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
    private final String id = "22926";
    private String oldVersion;
    private String newVersion;

    public UpdateChecker(JavaPlugin plugin) {
        this.oldVersion = plugin.getDescription().getVersion();
        this.newVersion = loadNewVersion();
    }

    public boolean isUpToDate() {
        return oldVersion.equals(newVersion);
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    private String loadNewVersion() {
        try {
            URL url = new URL("");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=" + key + "&resource=" + id).getBytes("UTF-8"));

            return new BufferedReader(new InputStreamReader(con.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
        } catch (Exception ex) {
            ex.printStackTrace();

            return this.oldVersion;
        }
    }
}
