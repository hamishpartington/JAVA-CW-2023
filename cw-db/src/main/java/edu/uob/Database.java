package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public void drop() throws IOException, DBException.DBDoesNotExist {
        if(!Files.isDirectory(Paths.get(this.folderPath))){
            throw new DBException.DBDoesNotExist(this.name);
        }
        Files.delete(Paths.get(this.folderPath));
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void createTable(String name, Set<String> attributes) throws IOException, DBException.TableAlreadyExists {
        if(this.tables.containsKey(name)){
            throw new DBException.TableAlreadyExists(name, this.name);
        }
        Table newTable = new Table(name, this);
        newTable.create(attributes);
        tables.put(name, newTable);
    }
    public void dropTable(String tableName) throws DBException.TableDoesNotExist {
        if(!this.tables.containsKey(name)){
            throw new DBException.TableDoesNotExist(name, this.name);
        }
        this.tables.get(tableName).drop();
        this.tables.remove(tableName);
    }

    public Map<String, Table> getTables() {
        return tables;
    }
}
