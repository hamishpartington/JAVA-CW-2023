package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Table implements Cloneable {
    private String name;
    private Database parentDatabase;
    private static Integer currentId;

    private List<String> fields;
    //array list of column array lists
    private List<List<String>> data;

    private File tableFile;

    public Table(String name, Database parentDatabase, int idNum) {
        this.name = name.toLowerCase();
        this.parentDatabase = parentDatabase;
        currentId = idNum;
        this.data = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public Table(String name, Database parentDatabase, int idNum, File tableFile) {
        this.name = name.toLowerCase();
        this.parentDatabase = parentDatabase;
        currentId = idNum;
        this.data = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.tableFile = tableFile;
    }
    public void create(ArrayList<String> attributes) throws IOException {
        this.tableFile = new File(this.parentDatabase.getFolderPath(), this.name + ".tab");
        this.tableFile.setWritable(true);
        this.tableFile.setReadable(true);
        FileWriter writer = new FileWriter(this.tableFile);
        writer.write(currentId + "\n");
        writer.write("id\t");
        this.fields.add("id");
        this.data.add(new ArrayList<>());
        if(attributes != null){
            for (String a : attributes) {
                this.fields.add(a);
                this.data.add(new ArrayList<>());
                writer.write(a + "\t");
            }
        }
        writer.flush();
        writer.close();
    }
    public void drop()  {
        this.tableFile.delete();
    }

    public void insert(ArrayList<String> values) throws IOException, DBException {
        if(values.size() != this.fields.size() - 1){
            throw new DBException.IncorrectNumberOfValues(this.name, this.fields.size() - 1);
        }
        int i = 1;
        data.get(0).add(currentId.toString());
        currentId++;
        for(String v : values){
          data.get(i).add(v);
          i++;
        }
        this.updateTableFile();
    }

    public Table select(ArrayList<String> fields) throws DBException {
        if(fields.get(0).equals("*")){
            return this;
        }
        Table procTable = new Table("temp", null, 0);
        for(String f : fields) {
            if(this.fields.stream().noneMatch(f::equalsIgnoreCase)){
                throw new DBException.FieldDoesNotExist(f, "SELECT");
            }
            procTable.fields.add(f);
            int dataIndex = this.findFieldIndex(f);
            procTable.data.add(new ArrayList<>(this.data.get(dataIndex)));
        }
        return procTable;
    }

    public int findFieldIndex(String field) {
        for(int i = 0; i < this.fields.size(); i++) {
            if(this.fields.get(i).equalsIgnoreCase(field)) {
                return i;
            }
        }
        return -1;
    }

   public Table selectWithConditions(HashSet<String> trueIds) {
        Table filteredTable = new Table("temp", null, 0);

       filteredTable.fields.addAll(this.fields);

       int j = 0;
       for(List<String> a : this.data) {
           filteredTable.data.add(new ArrayList<>());
           for(int i = 0; i < a.size(); i++) {
               String dataEntry = a.get(i);
               String associatedId = this.data.get(0).get(i);
               if(trueIds.contains(associatedId)) {
                   filteredTable.data.get(j).add(dataEntry);
               }
           }
           j++;
       }
       return filteredTable;
   }

    public void alter(String attribute, String alterationType) throws DBException, IOException {
        if(alterationType.equalsIgnoreCase("ADD")){
            if(this.fields.stream().anyMatch(attribute::equalsIgnoreCase)){
                throw new DBException.DuplicateFields();
            }
            this.fields.add(attribute);
            this.data.add(new ArrayList<>());
            int tableLength = this.data.get(0).size();
            int colNumber = this.fields.size() - 1;
            for(int i = 0; i < tableLength; i++) {
                this.data.get(colNumber).add("");
            }
        }
        if(alterationType.equalsIgnoreCase("DROP")){
            if(attribute.equals("id")){
                throw new DBException.CannotRemoveID();
            }
            if(this.fields.stream().noneMatch(attribute::equalsIgnoreCase)){
                throw new DBException.FieldDoesNotExist(attribute, "DROP");
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
                if(a.isEmpty()){
                    break;
                }
                outputString = outputString.concat(a.get(i) + "\t");
            }
            outputString = outputString.concat("\n");
        }
        return outputString;
    }

    public void update(ArrayList<Update> updates, HashSet<String> trueIds) throws IOException, DBException {
        for(Update u : updates) {
            String field = u.getAttributeName();
            if(this.fields.stream().noneMatch(field::equalsIgnoreCase)) {
                throw new DBException.FieldDoesNotExist(field, "UPDATE");
            }
            if(field.equalsIgnoreCase("id")) {
                throw new DBException.CannotUpdateID();
            }
            int fieldIndex = this.findFieldIndex(field);
            String newValue = u.getNewValue();
            for(String id : trueIds) {
                int dataIndex = this.data.get(0).indexOf(id);
                this.data.get(fieldIndex).set(dataIndex, newValue);
            }
        }
        this.updateTableFile();
    }

    public void delete(HashSet<String> trueIds) throws IOException {
        for(String id : trueIds) {
            int dataIndex = this.data.get(0).indexOf(id);
            for(List<String> d : this.data) {
                d.remove(dataIndex);
            }
        }
        this.updateTableFile();
    }

    public void updateTableFile() throws IOException {
        FileWriter writer = new FileWriter(this.tableFile);
        writer.write(currentId + "\n");
        for (String f : this.fields) {
            writer.write(f + "\t");
        }
        writer.write("\n");
        for(int i = 0; i < this.data.get(0).size(); i++){
            for(List<String> a : this.data) {
                if(a.isEmpty()){
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

    public List<String> getFields() {
        return fields;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void filterTableByForeignKeys (List<String> foreignKeys, String joinAttribute) {

        int keyIndex = this.findFieldIndex(joinAttribute);
        ArrayList<Integer> dataIndexesToRemove = new ArrayList<>();

        for(String dataEntry : this.data.get(keyIndex)) {
            if(!foreignKeys.contains(dataEntry)){
                int dataIndex = this.data.get(keyIndex).indexOf(dataEntry);
                dataIndexesToRemove.add(dataIndex);
            }
        }

        for(Integer i: dataIndexesToRemove) {
            this.removeDataAtIndex((int)i);
        }
    }

    private void removeDataAtIndex(int dataIndex) {
        for(List<String> column : this.data) {
            column.remove(dataIndex);
        }
    }

    public void renameColsForJoin() {
        this.fields.replaceAll(s -> this.name + "." + s);
    }

    public void generateNewIdAfterJoin() {
        this.fields.set(0, "id");
        for(Integer i = 0; i < this.data.get(0).size(); i++) {
            Integer id = i+1;
            this.data.get(0).set(i, id.toString());
        }
    }

    public void removePreviousIdAndJoiningColumns(String table1Name, String table2Name, String table1Attribute, String table2Attribute) {
        ArrayList<String> colNamesToRemove = new ArrayList<>();
        colNamesToRemove.add(table1Name + ".id");
        colNamesToRemove.add(table2Name + ".id");
        colNamesToRemove.add(table1Name + "." + table1Attribute);
        colNamesToRemove.add(table2Name + "." + table2Attribute);

        ArrayList<String> fieldToRemove = new ArrayList<>();
        ArrayList<Integer> dataIndexToRemove = new ArrayList<>();

        for(int i = 0; i < this.fields.size(); i++){
            String field = this.fields.get(i);
            if(colNamesToRemove.stream().anyMatch(field::equalsIgnoreCase)) {
                dataIndexToRemove.add(i);
                fieldToRemove.add(field);
            }
        }
        this.fields.removeAll(fieldToRemove);
        for(int i = dataIndexToRemove.size() - 1; i >= 0; i--) {
            this.data.remove((int)dataIndexToRemove.get(i));
        }
    }

    @Override
    public Table clone() throws CloneNotSupportedException {
        return (Table)super.clone();
    }
}
