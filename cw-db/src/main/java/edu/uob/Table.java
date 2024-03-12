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
    private static Integer currentId;

    private List<String> fields;
    //array list of column array lists
    private List<List<String>> data;

    private File tableFile;

    public Table(String name, Database parentDatabase) {
        this.name = name;
        this.parentDatabase = parentDatabase;
        this.currentId = 1;
    }
    public void create(ArrayList<String> attributes) throws IOException {
        this.tableFile = new File(this.parentDatabase.getFolderPath(), this.name + ".tab");
        this.tableFile.setWritable(true);
        this.tableFile.setReadable(true);
        FileWriter writer = new FileWriter(tableFile);
        writer.write("id\t");
        this.data = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.add("id");
        this.data.add(new ArrayList<>());
        if(attributes != null){
            for (String a : attributes) {
                this.fields.add(a);
                this.data.add(new ArrayList<>());
                writer.write(a + "\t");
            }
            writer.flush();
            writer.close();
        }
    }
    public void drop()  {
        this.tableFile.delete();
    }

    public void insert(ArrayList<String> values) throws IOException, DBException.incorrectNumberOfValues {
        if(values.size() != this.fields.size() - 1){
            throw new DBException.incorrectNumberOfValues(this.name, this.fields.size());
        }
        int i = 1;
        data.get(0).add(currentId.toString());
        FileWriter writer = new FileWriter(tableFile, true);
        writer.write("\n" + currentId.toString() + "\t");
        currentId++;
        for(String v : values){
          data.get(i).add(v);
          writer.write(v + "\t");
          i++;
        }
        writer.flush();
        writer.close();
    }

//    public String select(ArrayList<String> fields) {
//
//    }

    public File getTableFile() {
        return tableFile;
    }
}