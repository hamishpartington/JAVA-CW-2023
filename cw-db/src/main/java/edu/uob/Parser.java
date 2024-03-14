package edu.uob;

import java.util.ArrayList;

public class Parser {
    private Tokeniser tokeniser;
    private ArrayList<String> tokens;

    private static Integer currentToken;

    public Parser(String query) {
        this.tokeniser = new Tokeniser(query);
        this.tokeniser.setup();
        this.tokens = this.tokeniser.getTokens();
        currentToken = 0;
    }

    public void parseQuery() throws ParserException {
        this.parseCommand();
    }
    public void parseCommand() throws ParserException {
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
    public void parseUse() throws ParserException {
        currentToken++;
        this.parseDatabaseName();
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

    public void parseCreate() throws ParserException {
        currentToken++;
        String token = this.tokens.get(currentToken).toUpperCase();
        if(token.equals("DATABASE")){
            currentToken++;
            this.parseDatabaseName();
        } else if(token.equals("TABLE")) {
            currentToken++;
            this.parseTableName();
            currentToken++;
            if(this.tokens.get(currentToken).equals("(")){
                currentToken++;
                this.parseAttributeList(")");
            }
        } else {
            throw new ParserException.InvalidCreate(token);
        }
        currentToken++;
        this.checkValidStatementEnd("CREATE");
    }

    public void parseTableName() throws ParserException {
        this.parsePlainText(false, false);
    }

    public void parseAttributeList(String terminator) throws ParserException {
        this.checkForListTerminator(terminator, "Attribute");
        while(!this.tokens.get(currentToken).equalsIgnoreCase(terminator)){
            if(this.tokens.get(currentToken).equals(",")) {
                currentToken++;
            }
            this.parseAttributeName();
            currentToken++;
        }
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

    public void parseDrop() throws ParserException {
        currentToken++;
        String token = this.tokens.get(currentToken).toUpperCase();

        if(token.equals("TABLE")){
            currentToken++;
            this.parseTableName();
        } else if(token.equals("DATABASE")){
            currentToken++;
            this.parseDatabaseName();
        } else {
            throw new ParserException.InvalidDrop(token);
        }
        currentToken++;
        this.checkValidStatementEnd("DROP");
    }

    public void parseAlter() throws ParserException {
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("TABLE")){
            throw new ParserException.InvalidAlter();
        }
        currentToken++;
        this.parseTableName();
        currentToken++;
        this.parseAlterationType();
        currentToken++;
        this.checkValidStatementEnd("ALTER");
    }

    public void parseAlterationType() throws ParserException {
        String token = this.tokens.get(currentToken).toUpperCase();
        if(!(token.equals("DROP") || token.equals("ADD"))){
            throw new ParserException.InvalidAlterationType(token);
        }
        currentToken++;
        this.parseAttributeName();
    }

    public void parseInsert() throws ParserException {
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("INTO")){
            throw new ParserException.NoInto();
        }
        currentToken++;
        this.parseTableName();
        currentToken++;
        if(!this.tokens.get(currentToken).equalsIgnoreCase("VALUES")){
            throw new ParserException.NoValues();
        }
        currentToken++;
        if(!this.tokens.get(currentToken).equals("(")){
            throw new ParserException.NoValueList();
        }
        this.parseValueList(")");
        currentToken++;
        this.checkValidStatementEnd("INSERT");
    }

    public void parseValueList(String terminator) throws ParserException {
        currentToken++;
        this.checkForListTerminator(terminator, "Value");
        while(!this.tokens.get(currentToken).equalsIgnoreCase(terminator)){
            if(this.tokens.get(currentToken).equals(",")) {
                currentToken++;
            }
            this.parseValue();
            currentToken++;
        }
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

    public void parseSelect() throws ParserException {
        currentToken++;
        this.parseWildAttributeList();
        currentToken++;
        this.parseTableName();
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
    }

    public void parseWildAttributeList() throws ParserException {
        if(this.tokens.get(currentToken).equals("*")) {
            currentToken++;
            return;
        }
        this.parseAttributeList("FROM");
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
}
