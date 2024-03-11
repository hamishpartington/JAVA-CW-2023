package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Database {
    private String name, folderPath;

    private File location;

    public Database(String name) {
        this.name = name;
        this.folderPath = Paths.get("databases", this.name).toAbsolutePath().toString();
    }

    public void create() throws IOException {

        Files.createDirectories(Paths.get(this.folderPath));
    }

    public void drop() throws IOException {
        Files.delete(Paths.get(this.folderPath));
    }

    public String getFolderPath() {
        return folderPath;
    }
}
