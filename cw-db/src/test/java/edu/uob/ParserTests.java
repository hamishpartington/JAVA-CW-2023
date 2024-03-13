package edu.uob;

import org.junit.jupiter.api.Test;

public class ParserTests {

    @Test
    public void testTokeniser() {
        String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ";
        Tokeniser tokeniser = new Tokeniser(query);
        tokeniser.setup();
    }
}
