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
        this.data = new ArrayList<>();
        this.fields = new ArrayList<>();
    }
    public void create(ArrayList<String> attributes) throws IOException {
        this.tableFile = new File(this.parentDatabase.getFolderPath(), this.name + ".tab");
        this.tableFile.setWritable(true);
        this.tableFile.setReadable(true);
        FileWriter writer = new FileWriter(this.tableFile);
        writer.write("id\t");
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

    public Table select(ArrayList<String> fields) throws DBException.fieldDoesNotExist {
        if(fields.get(0).equals("*")){
            return this;
        }
        Table procTable = new Table(null, null);
        for(String f : fields) {
            if(!this.fields.contains(f)){
                throw new DBException.fieldDoesNotExist(f);
            }
            procTable.fields.add(f);
            int dataIndex = this.fields.indexOf(f);
            procTable.data.add(this.data.get(dataIndex));
        }
        return procTable;
    }

    public void alter(String attribute, String alterationType) throws DBException.duplicateFields, DBException.fieldDoesNotExist, IOException, DBException.cannotRemoveID {
        if(alterationType.equals("ADD")){
            if(this.fields.contains(attribute)){
                throw new DBException.duplicateFields();
            }
            this.fields.add(attribute);
            this.data.add(new ArrayList<>());
        }
        if(alterationType.equals("DROP")){
            if(attribute.equals("id")){
                throw new DBException.cannotRemoveID();
            }
            if(!this.fields.contains(attribute)){
                throw new DBException.fieldDoesNotExist(attribute);
            }
            int fieldIndex = this.fields.indexOf(attribute);
            this.fields.remove(attribute);
            this.data.remove(fieldIndex);
        }
        this.updateTableFile();
    }

    public String toString(){
        String outputString = "";
        for(String f : this.fields){
            outputString = outputString.concat(f + "\t");
        }
        outputString = outputString.concat("\n");
        for(int i = 0; i < this.data.get(0).size(); i++){
            for(List<String> a : this.data){
                if(a.size() == 0){
                    break;
                }
                outputString = outputString.concat(a.get(i) + "\t");
            }
            outputString = outputString.concat("\n");
        }
        return outputString;
    }

    public void updateTableFile() throws IOException {
        FileWriter writer = new FileWriter(this.tableFile);
        for (String f : this.fields) {
            writer.write(f + "\t");
        }
        writer.write("\n");
        for(int i = 0; i < this.data.get(0).size(); i++){
            for(List<String> a : this.data) {
                if(a.size() == 0){
                    writer.write("\t");
                    break;
                }
                writer.write(a.get(i) + "\t");
            }
            writer.write("\n");
        }
            writer.flush();
            writer.close();
    }

    public File getTableFile() {
        return tableFile;
    }
}
