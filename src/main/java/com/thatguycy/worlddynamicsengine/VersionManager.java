package com.thatguycy.worlddynamicsengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VersionManager {
    private final WorldDynamicsEngine plugin;
    public VersionManager(WorldDynamicsEngine plugin) {
        this.plugin = plugin;
    }
    String getCurrentVersion(){
        return plugin.getDescription().getVersion();
    }
    String getLatestVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/thatguycy/WorldDynamics-Engine/v0.2.x/current.version");
            URLConnection conn = url.openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return reader.readLine();
            }
        } catch (IOException e) {
            return "Unknown";
        }
    }
}
