package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private Tokeniser tokeniser;
    private ArrayList<String> tokens;

    private Database database;

    private DBServer server;

    private static Integer currentToken;

    private Table queryResult;

    public Parser(String query) {
        this.tokeniser = new Tokeniser(query);
        this.tokeniser.setup();
        this.tokens = this.tokeniser.getTokens();
        currentToken = 0;
    }

    public void parseQuery() throws ParserException, DBException, IOException {
        this.parseCommand();
    }
    public void parseCommand() throws ParserException, DBException, IOException {
        int lastTokenIndex = this.tokens.size() - 1;
        String lastToken = this.tokens.get(lastTokenIndex);
        if(!lastToken.equals(";")){
            throw new ParserException.QueryNotTerminated();
        }
        String firstToken = this.tokens.get(0).toUpperCase();
        switch (firstToken) {
            case "USE" -> this.parseUse();
            case "CREATE" -> this.parseCreate();
            case "DROP" -> this.parseDrop();
            case "ALTER" -> this.parseAlter();
            case "INSERT" -> this.parseInsert();
            case "SELECT" -> this.parseSelect();
            case "UPDATE" -> this.parseUpdate();
            case "DELETE" -> this.parseDelete();
            case "JOIN" -> this.parseJoin();
            default -> throw new ParserException.NotACommandType(firstToken);
        }
    }
    public void parseUse() throws ParserException, DBException, IOException {
        currentToken++;
        this.parseDatabaseName();
        String databaseName = this.tokens.get(currentToken);
        currentToken++;
        this.checkValidStatementEnd("USE");
        this.database = new Database(databaseName);
        this.database.use();
        this.server.setDatabaseInUse(this.database);
    }

    public void parseDatabaseName() throws ParserException {
        this.parsePlainText(true, false);
    }

    public void parsePlainText(boolean isDatabase, boolean isAttribute) throws ParserException {

        boolean isAlphanumeric = this.tokens.get(currentToken).matches("^[a-zA-Z0-9]+$");
        if(!isAlphanumeric && isDatabase){
            throw new ParserException.InvalidDatabaseName(this.tokens.get(currentToken));
        }
        if(!isAlphanumeric && !isAttribute){
            throw new ParserException.InvalidTableName(this.tokens.get(currentToken));
        }

        if(!isAlphanumeric){
            throw new ParserException.InvalidAttributeName(this.tokens.get(currentToken));
        }
    }

    public void parseCreate() throws ParserException, DBException, IOException {
        currentToken++;
        String token = this.tokens.get(currentToken).toUpperCase();
        if(token.equals("DATABASE")){
            currentToken++;
            this.parseDatabaseName();
            String databaseName = this.tokens.get(currentToken);
            currentToken++;
            this.checkValidStatementEnd("CREATE");
            this.database = new Database(databaseName);
            this.database.create();
        } else if(token.equals("TABLE")) {
            currentToken++;
            this.parseTableName();
            String tableName = this.tokens.get(currentToken);
            currentToken++;
            ArrayList<String> attributeList = null;
            if(this.tokens.get(currentToken).equals("(")){
                currentToken++;
                attributeList = this.parseAttributeList(")");
            }
            currentToken++;
            this.checkValidStatementEnd("CREATE");
            if(this.server.getDatabaseInUse() == null) {
                throw new ParserException.NoDatabaseInUse("CREATE");
            }
            this.database.createTable(tableName, attributeList);
        } else {
            throw new ParserException.InvalidCreate(token);
        }
    }

    public void parseTableName() throws ParserException {
        this.parsePlainText(false, false);
    }

    public ArrayList<String> parseAttributeList(String terminator) throws ParserException {
        this.checkForListTerminator(terminator, "Attribute");
        ArrayList<String> attributeList = new ArrayList<>();
        while(!this.tokens.get(currentToken).equalsIgnoreCase(terminator)){
            if(this.tokens.get(currentToken).equals(",")) {
                currentToken++;
            }
            this.parseAttributeName();
            attributeList.add(this.tokens.get(currentToken));
            currentToken++;
        }
        return attributeList;
    }

    private void checkForListTerminator(String terminator, String type) throws ParserException {
        int tempToken = currentToken;
        while(!this.tokens.get(tempToken).equalsIgnoreCase(terminator)){
            if(this.tokens.get(tempToken).equals(";")){
                throw new ParserException.ListNotTerminated(terminator, type);
            }
            tempToken++;
        }
    }

    public void parseAttributeName() throws ParserException {
        this.parsePlainText(false, true);
    }

    public void parseDrop() throws ParserException, DBException {
        currentToken++;
        String token = this.tokens.get(currentToken).toUpperCase();

        if(token.equals("TABLE")){
            currentToken++;
            this.parseTableName();
            String tableName = this.tokens.get(currentToken);
            currentToken++;
            this.checkValidStatementEnd("DROP");
            if(this.server.getDatabaseInUse() == null) {
                throw new ParserException.NoDatabaseInUse("DROP");
            }
            this.database.dropTable(tableName);
        } else if(token.equals("DATABASE")){
            currentToken++;
            this.parseDatabaseName();
            String databaseName = this.tokens.get(currentToken);
            currentToken++;
            this.checkValidStatementEnd("DROP");
            this.database = new Database(databaseName);
            this.database.drop();
        } else {
            throw new ParserException.InvalidDrop(token);
        }

    }

    public void parseAlter() throws ParserException, DBException, IOException {
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("TABLE")){
            throw new ParserException.InvalidAlter();
        }
        currentToken++;
        this.parseTableName();
        String tableName = this.tokens.get(currentToken);
        currentToken++;
        String alterationType = this.parseAlterationType();
        String attributeName = this.tokens.get(currentToken);
        currentToken++;
        this.checkValidStatementEnd("ALTER");
        if(this.server.getDatabaseInUse() == null) {
            throw new ParserException.NoDatabaseInUse("ALTER");
        }
        this.database.alterTable(tableName, attributeName, alterationType);
    }

    public String parseAlterationType() throws ParserException {
        String token = this.tokens.get(currentToken).toUpperCase();
        if(!(token.equals("DROP") || token.equals("ADD"))){
            throw new ParserException.InvalidAlterationType(token);
        }
        currentToken++;
        this.parseAttributeName();
        return token;
    }

    public void parseInsert() throws ParserException, DBException, IOException {
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("INTO")){
            throw new ParserException.NoInto();
        }
        currentToken++;
        this.parseTableName();
        String tableName = this.tokens.get(currentToken);
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("VALUES")){
            throw new ParserException.NoValues();
        }
        currentToken++;
        if(!this.tokens.get(currentToken).equals("(")){
            throw new ParserException.NoValueList();
        }
        ArrayList<String> valueList = this.parseValueList(")");
        currentToken++;
        this.checkValidStatementEnd("INSERT");
        if(this.server.getDatabaseInUse() == null) {
            throw new ParserException.NoDatabaseInUse("INSERT");
        }
        this.database.insertIntoTable(tableName, valueList);
    }

    public ArrayList<String> parseValueList(String terminator) throws ParserException {
        currentToken++;
        this.checkForListTerminator(terminator, "Value");
        ArrayList<String> valueList = new ArrayList<>();
        while(!this.tokens.get(currentToken).equalsIgnoreCase(terminator)){
            if(this.tokens.get(currentToken).equals(",")) {
                currentToken++;
            }
            this.parseValue();
            valueList.add(this.tokens.get(currentToken));
            currentToken++;
        }
        return valueList;
    }

    public void parseValue() throws ParserException {
        boolean stringLiteral, booleanLiteral, floatLiteral, integerLiteral, nullChar;

        String token = this.tokens.get(currentToken);

        stringLiteral = this.isStringLiteral(token);
        booleanLiteral = this.isBooleanLiteral(token);
        floatLiteral = token.matches("^[-|+]?[0-9]+.[0-9]+$");
        integerLiteral = token.matches("^[-|+]?[0-9]+$");
        nullChar = token.equalsIgnoreCase("NULL");

        if(!(stringLiteral || booleanLiteral || floatLiteral || integerLiteral || nullChar)) {
            throw new ParserException.InvalidValue(token);
        }
    }

    private boolean isStringLiteral(String token) {
        int endIndex = token.length() - 1;
        if(token.charAt(0) != '\'' || token.charAt(endIndex) != '\'') {
            return false;
        }
        for(int i = 1; i < endIndex; i++) {
            char currentChar = token.charAt(i);
            if(!this.isCharLiteral(currentChar)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCharLiteral(char currentChar) {
        return this.isSymbol(currentChar) || Character.isLetter(currentChar) || Character.isDigit(currentChar);
    }

    private boolean isSymbol(char currentChar) {
        switch (currentChar) {
            case '!', '#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.',
                    '/', ':', ';', '>', '=', '<', '?', '@', '[', '\\', ']',
                    '^', '_', ' ', '{', '}', '~' -> { return true; }
            default -> { return false; }
        }
    }

    private boolean isBooleanLiteral(String token) {
        return token.equalsIgnoreCase("TRUE") || token.equalsIgnoreCase("FALSE");
    }

    public void parseSelect() throws ParserException, DBException {
        currentToken++;
        ArrayList<String> attributeList = this.parseWildAttributeList();
        currentToken++;
        this.parseTableName();
        String tableName = this.tokens.get(currentToken);
        currentToken++;
        String token = this.tokens.get(currentToken).toUpperCase();
        if(!(token.equals(";") || token.equals("WHERE"))) {
            throw new ParserException.InvalidSelect(token);
        }
        if(token.equals("WHERE")) {
            currentToken++;
            this.parseCondition(false);
        }
        this.checkValidStatementEnd("SELECT");
        if(this.server.getDatabaseInUse() == null) {
            throw new ParserException.NoDatabaseInUse("INSERT");
        }
        this.queryResult = this.database.selectFromTable(attributeList, tableName);
    }

    public ArrayList<String> parseWildAttributeList() throws ParserException {
        ArrayList<String> attributeList = new ArrayList<>();
        if(this.tokens.get(currentToken).equals("*")) {
            attributeList.add("*");
            currentToken++;
            return attributeList;
        }
        return this.parseAttributeList("FROM");
    }

    public void parseCondition(boolean isRecursive) throws ParserException {
        if(!isRecursive){
            if(!this.checkConditionParentheses()) {
                throw new ParserException.UnmatchedParentheses();
            }
        }
        while(this.tokens.get(currentToken).equals("(")){
            currentToken++;
        }
        this.parseAttributeName();
        currentToken++;
        this.parseComparator();
        this.parseValue();
        currentToken++;
        while(this.tokens.get(currentToken).equals(")")){
            currentToken++;
        }
        if(this.tokens.get(currentToken).equalsIgnoreCase("AND") ||
                this.tokens.get(currentToken).equalsIgnoreCase("OR")) {
            currentToken++;
            this.parseCondition(true);
        }
    }

    private boolean checkConditionParentheses() {
        int tempTokenNum = currentToken;
        int numClosing = 0;
        int numOpening = 0;
        while(!this.tokens.get(tempTokenNum).equals(";")) {
            if(this.tokens.get(tempTokenNum).equals("(")){
                numOpening++;
            }
            if(this.tokens.get(tempTokenNum).equals(")")){
                numClosing++;
            }
            tempTokenNum++;
            if(numOpening < numClosing) return false;
        }
        return numOpening == numClosing;
    }

    public void parseComparator() throws ParserException {
        switch (this.tokens.get(currentToken).toUpperCase()) {
            case "==", ">", "<", ">=", "<=", "!=", "LIKE" -> currentToken++;
            default -> throw new ParserException.InvalidComparator(this.tokens.get(currentToken));
        }
    }

    private void checkValidStatementEnd(String statementType) throws ParserException {
        if(!this.tokens.get(currentToken).equals(";")) {
            throw new ParserException.InvalidStatementSyntax(this.tokens.get(currentToken), statementType);
        }
    }

    public void parseUpdate() throws ParserException {
        currentToken++;
        this.parseTableName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("SET")){
            throw new ParserException.NoSetInUpdate(this.tokens.get(currentToken));
        }
        currentToken++;
        this.parseNameValueList();
        currentToken++;
        this.parseCondition(false);
        this.checkValidStatementEnd("UPDATE");
    }

    public void parseNameValueList() throws ParserException {
        checkForListTerminator("WHERE", "NameValue");
        this.parseNameValuePair();
    }

    public void parseNameValuePair() throws ParserException {
        this.parseAttributeName();
        currentToken++;
        if(!this.tokens.get(currentToken).equals("=")) {
            throw new ParserException.InvalidNameValuePair();
        }
        currentToken++;
        this.parseValue();
        currentToken++;
        if(this.tokens.get(currentToken).equals(",")) {
            currentToken++;
            this.parseNameValuePair();
        }
    }

    public void parseDelete() throws ParserException {
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("FROM")) {
            throw new ParserException.NoFromDelete(this.tokens.get(currentToken));
        }
        currentToken++;
        this.parseTableName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("WHERE")) {
            throw new ParserException.NoWhere(this.tokens.get(currentToken));
        }
        currentToken++;
        this.parseCondition(false);
        this.checkValidStatementEnd("DELETE");
    }

    public void parseJoin() throws ParserException {
        currentToken++;
        this.parseTableName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("AND")){
            throw new ParserException.InvalidJoin(this.tokens.get(currentToken), "AND");
        }
        currentToken++;
        this.parseTableName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("ON")){
            throw new ParserException.InvalidJoin(this.tokens.get(currentToken), "ON");
        }
        currentToken++;
        this.parseAttributeName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("AND")){
            throw new ParserException.InvalidJoin(this.tokens.get(currentToken), "AND");
        }
        currentToken++;
        this.parseAttributeName();
        currentToken++;
        this.checkValidStatementEnd("JOIN");
    }

    public void setServer(DBServer server) {
        this.server = server;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Table getQueryResult() {
        return queryResult;
    }
}
