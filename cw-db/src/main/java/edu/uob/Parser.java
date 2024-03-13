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
        this.currentToken = 0;
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
            case "USE":
                this.parseUse();
                break;
            case "CREATE":
                this.parseCreate();
                break;
            case "DROP":
                break;
            case "ALTER":
                break;
            case "INSERT":
                break;
            case "SELECT":
                break;
            case "UPDATE":
                break;
            case "DELETE":
                break;
            case "JOIN":
                break;
            default:
                throw new ParserException.NotACommandType(firstToken);
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
        if(!isAlphanumeric && !isDatabase && !isAttribute){
            throw new ParserException.InvalidTableName(this.tokens.get(currentToken));
        }

        if(!isAlphanumeric && !isDatabase && isAttribute){
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
                this.parseAttributeList(")");
            }
        } else {
            throw new ParserException.InvalidCreate(token);
        }
    }

    public void parseTableName() throws ParserException {
        this.parsePlainText(false, false);
    }

    public void parseAttributeList(String terminator) throws ParserException {
        currentToken++;
        this.checkForAttributeListTerminator(terminator);
        while(!this.tokens.get(currentToken).toUpperCase().equals(terminator)){
            if(this.tokens.get(currentToken).equals(",")) {
                currentToken++;
            }
            this.parseAttributeName();
            currentToken++;
        }
    }

    private void checkForAttributeListTerminator(String terminator) throws ParserException {
        int tempToken = currentToken;
        while(!this.tokens.get(tempToken).toUpperCase().equals(terminator)){
            if(this.tokens.get(tempToken).equals(";")){
                throw new ParserException.AttributeListNotTerminated(terminator);
            }
            tempToken++;
        }
    }

    public void parseAttributeName() throws ParserException {
        this.parsePlainText(false, true);
    }
}
