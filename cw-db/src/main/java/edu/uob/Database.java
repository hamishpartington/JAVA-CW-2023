package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;

public class Database {
    private String name, folderPath;
    private Map<String, Table> tables;

    public Database(String name) {
        this.name = name.toLowerCase();
        this.folderPath = Paths.get("databases", this.name).toAbsolutePath().toString();
        this.tables = new HashMap<>();
    }

    public void create() throws IOException, DBException {
        if(Files.isDirectory(Paths.get(this.folderPath))){
            throw new DBException.DBAlreadyExists(this.name);
        }
        if(isReservedKeyWord(this.name)) {
            throw new DBException.ReservedKeyWord(this.name);
        }
        Files.createDirectories(Paths.get(this.folderPath));

    }

    public void drop() throws DBException {
        if(!Files.isDirectory(Paths.get(this.folderPath))){
            throw new DBException.DBDoesNotExist(this.name);
        }
        File databaseDirectory = new File(this.folderPath);
        File[] contents = databaseDirectory.listFiles();
        if(contents != null) {
            for(File f : contents){
                f.delete();
            }
        }
        databaseDirectory.delete();
    }

    public void use() throws DBException, IOException {
        if(!Files.isDirectory(Paths.get(this.folderPath))){
            throw new DBException.DBDoesNotExist(this.name);
        }
        File databaseDirectory = new File(this.folderPath);
        File[] contents = databaseDirectory.listFiles();
        if(contents != null) {
            for(File f : contents){
                String tableName = f.getName().replaceAll(".tab", "");
                Table table = this.readTable(f, tableName);
                this.tables.put(tableName, table);
            }
        }
    }

    private Table readTable(File tableFile, String tableName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tableFile));
        String idNum = reader.readLine().replaceAll("\n", "");
        int lineNum = 0;
        String currentLine = reader.readLine();
        Table table = new Table(tableName, this, Integer.parseInt(idNum), tableFile);
        do {
            String procLine = currentLine.replaceAll("\n", "");
            String[] data = procLine.split("\t");
            for(int i = 0; i < data.length; i++) {
                if(lineNum == 0){
                    table.getFields().add(data[i]);
                    table.getData().add(new ArrayList<>());
                }else {
                    table.getData().get(i).add(data[i]);
                }
            }
            lineNum++;
            currentLine = reader.readLine();
        } while (currentLine != null);

        return table;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void createTable(String tableName, ArrayList<String> attributes) throws IOException, DBException {
        if(this.tables.containsKey(tableName.toLowerCase())){
            throw new DBException.TableAlreadyExists(tableName, this.name);
        }
        if(isReservedKeyWord(tableName)) {
            throw new DBException.ReservedKeyWord(tableName);
        }
        if(attributes != null){
            if(this.checkForDuplicates(attributes)){
                throw new DBException.DuplicateFields();
            }
            this.checkForKeywords(attributes);
        }
        Table newTable = new Table(tableName, this, 1);
        newTable.create(attributes);
        tables.put(tableName, newTable);
    }
    private boolean checkForDuplicates(ArrayList<String> attributes) {
        HashSet<String> hSet = new HashSet<>();
        for(String a : attributes){
            if(!hSet.add(a.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    public void dropTable(String tableName) throws DBException {
        if(!this.tables.containsKey(tableName.toLowerCase())){
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        this.tables.get(tableName).drop();
        this.tables.remove(tableName);
    }

    public void insertIntoTable(String tableName, ArrayList<String> values) throws DBException, IOException {
        if(!this.tables.containsKey(tableName.toLowerCase())){
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        this.tables.get(tableName).insert(values);
    }

    public Table selectFromTable(ArrayList<String> fields, String tableName, boolean isConditional, HashSet<String> trueIds) throws DBException {
        if(!this.tables.containsKey(tableName)){
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        if(isConditional) {
            Table filteredTable = this.tables.get(tableName).selectWithConditions(trueIds);
            return filteredTable.select(fields);
        }
        return this.tables.get(tableName).select(fields);
    }

    public void alterTable(String tableName, String tableAttribute, String alterationType) throws DBException, IOException {
        if(!this.tables.containsKey(tableName)) {
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        if(isReservedKeyWord(tableAttribute)) {
            throw new DBException.ReservedKeyWord(tableAttribute);
        }
        this.tables.get(tableName).alter(tableAttribute, alterationType);
    }

    public void updateTable(String tableName, ArrayList<Update> updates, HashSet<String> trueIds) throws DBException, IOException {
        if(!this.tables.containsKey(tableName)) {
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        this.tables.get(tableName).update(updates, trueIds);
    }

    public void deleteTable(String tableName, HashSet<String> trueIds) throws DBException, IOException {
        if(!this.tables.containsKey(tableName)) {
            throw new DBException.TableDoesNotExist(tableName, this.name);
        }
        this.tables.get(tableName).delete(trueIds);
    }

    public Table joinTables(String table1Name, String table2Name, String table1Attribute, String table2Attribute) throws DBException, CloneNotSupportedException {
        if(!this.tables.containsKey(table1Name)) {
            throw new DBException.TableDoesNotExist(table1Name, this.name);
        }
        if(!this.tables.containsKey(table2Name)) {
            throw new DBException.TableDoesNotExist(table2Name, this.name);
        }
        if(this.tables.get(table1Name).getFields().stream().noneMatch(table1Attribute::equalsIgnoreCase)) {
            throw new DBException.FieldDoesNotExist(table1Attribute, "JOIN ON");
        }
        if(this.tables.get(table2Name).getFields().stream().noneMatch(table2Attribute::equalsIgnoreCase)) {
            throw new DBException.FieldDoesNotExist(table2Attribute, "JOIN ON");
        }
        Table table1 = this.tables.get(table1Name).clone();
        Table table2 = this.tables.get(table2Name).clone();

        int table2Ids = table2.findFieldIndex(table2Attribute);
        int table1Ids = table1.findFieldIndex(table1Attribute);

        table1.filterTableByForeignKeys(table2.getData().get(table2Ids), table1Attribute);
        table1.renameColsForJoin();
        table2.renameColsForJoin();

        int table1Size = table1.getData().get(0).size();

        for(String id: table1.getData().get(table1Ids)) {
            int table2DataIndex = table2.getData().get(table2Ids).indexOf(id);
            for(int i = 0; i < table2.getData().size(); i++) {
                String fieldName = table2.getFields().get(i);
                if(table1.getFields().stream().noneMatch(fieldName::equalsIgnoreCase)) {
                    table1.getFields().add(fieldName);
                    table1.getData().add(new ArrayList<>(Arrays.asList(new String[table1Size])));
                }
                int table1Index = table1.getData().get(table1Ids).indexOf(id);
                int table1ColIndex = table1.findFieldIndex(fieldName);
                String dataToAdd = table2.getData().get(i).get(table2DataIndex);
                table1.getData().get(table1ColIndex).set(table1Index, dataToAdd);
            }
        }
        table1.generateNewIdAfterJoin();
        table1.removePreviousIdAndJoiningColumns(table1Name, table2Name, table1Attribute, table2Attribute);

        return table1;
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    private boolean isReservedKeyWord(String name) {
        switch (name.toUpperCase()) {
            case "SELECT", "JOIN", "USE", "CREATE", "DATABASE", "LIKE", "TABLE", "DROP", "ALTER",
                    "INSERT", "INTO", "VALUES", "FROM", "WHERE", "SET", "UPDATE", "DELETE",
                    "ON", "AND", "OR", "ADD", "TRUE", "FALSE", "NULL" -> {return true;}
            default -> {return false;}
        }
    }

    private void checkForKeywords(ArrayList<String> attributes) throws DBException {
        for(String a : attributes){
            if(isReservedKeyWord(a)){
                throw new DBException.ReservedKeyWord(a);
            }
        }
    }

    public String getName() {
        return name;
    }
}
