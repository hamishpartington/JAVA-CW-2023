package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table {
    private String name;
    private Database parentDatabase;

    private Set<String> fields;
    private List<List<String>> data;

    private File tableFile;

    public Table(String name, Database parentDatabase) {
        this.name = name;
        this.parentDatabase = parentDatabase;
    }
    public void create(Set<String> attributes) throws IOException {
        tableFile = new File(this.parentDatabase.getFolderPath(), this.name + ".tab");
        tableFile.setWritable(true);
        tableFile.setReadable(true);
        FileWriter writer = new FileWriter(tableFile);
        writer.write("id\t");
        fields = new HashSet<>();
        fields.add("id");
        if(attributes != null){
            for (String a : attributes) {
                fields.add(a);
                writer.write(a + "\t");
            }
            writer.flush();
            writer.close();
        }
    }
}
