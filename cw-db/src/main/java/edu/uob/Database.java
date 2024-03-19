package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
                Table table = readTable(f, tableName);
                this.tables.put(tableName, table);
            }
        }
    }

    private Table readTable(File tableFile, String tableName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(tableFile));
        int lineNum = 0;
        String currentLine = reader.readLine();
        Table table = new Table(tableName, this);
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
        Table newTable = new Table(tableName, this);
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
}
