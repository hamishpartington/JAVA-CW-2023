package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Database {
    private String name, folderPath;
    private Map<String, Table> tables;
    // TODO maybe check database name is valid or this may happen in parse
    public Database(String name) {
        this.name = name;
        this.folderPath = Paths.get("databases", this.name).toAbsolutePath().toString();
        this.tables = new HashMap<>();
    }

    public void create() throws IOException, DBException.DBAlreadyExists {
        if(Files.isDirectory(Paths.get(this.folderPath))){
            throw new DBException.DBAlreadyExists(this.name);
        }
        Files.createDirectories(Paths.get(this.folderPath));
    }

    public void drop() throws DBException.DBDoesNotExist {
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

    public String getFolderPath() {
        return folderPath;
    }

    public void createTable(String name, ArrayList<String> attributes) throws IOException, DBException.TableAlreadyExists, DBException.duplicateFields {
        if(this.tables.containsKey(name)){
            throw new DBException.TableAlreadyExists(name, this.name);
        }
        if(attributes != null){
            if(this.checkForDuplicates(attributes)){
                throw new DBException.duplicateFields();
            }
        }
        Table newTable = new Table(name, this);
        newTable.create(attributes);
        tables.put(name, newTable);
    }
    public boolean checkForDuplicates(ArrayList<String> attributes) {
        HashSet<String> hSet = new HashSet<>();
        for(String a : attributes){
            if(!hSet.add(a)){
                return true;
            }
        }
        return false;
    }
    public void dropTable(String tableName) throws DBException.TableDoesNotExist {
        if(!this.tables.containsKey(name)){
            throw new DBException.TableDoesNotExist(name, this.name);
        }
        this.tables.get(tableName).drop();
        this.tables.remove(tableName);
    }

    public void insertIntoTable(String tableName, ArrayList<String> values) throws DBException.TableDoesNotExist, IOException, DBException.incorrectNumberOfValues {
        if(!this.tables.containsKey(name)){
            throw new DBException.TableDoesNotExist(name, this.name);
        }
        this.tables.get(tableName).insert(values);
    }
    public Map<String, Table> getTables() {
        return tables;
    }
}
