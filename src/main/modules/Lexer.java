package main.modules;

import main.enums.CharType;
import main.enums.TokenType;
import main.src.Logger;

import static main.enums.TokenType.*;

import java.io.*;

public class Lexer {

    // 'log' is a simple Logging class for console print formatting.
    public static Logger log = Logger.getInstance();

    static int[][] stateTable = {
            {2, 4, 6, 7, 11, 11, 13, 15, 18, 18, 20, 22, 24, 26, 28, 30, 32, 0},
            {},
            {2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
            {},
            {4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5},
            {},
            {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            {10, 10, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            {8, 8, 9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {8, 8, 8, 34, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {},
            {12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12},
            {},
            {14, 14, 14, 14, 14, 14, 18, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14},
            {},
            {17, 17, 17, 17, 17, 17, 16, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17},
            {19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19},
            {},
            {19, 19, 19, 19, 19, 19, 17, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19},
            {},
            {21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21},
            {},
            {23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23},
            {},
            {25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25},
            {},
            {27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27},
            {},
            {29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29},
            {},
            {31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31, 31},
            {},
            {33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33},
            {},
            {},
    };
    static int currentState = 0;
    static StringBuilder buffer = new StringBuilder();
    static File tokens;
    static BufferedWriter writer;

    public Lexer() {
        tokens = new File("assets/tokens.txt");
    }

    public void scan(File program) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(program));
            writer = new BufferedWriter(new FileWriter(tokens));
            int charValue;

            while( ((charValue = reader.read()) != -1)) {
                /* Instead of converting and comparing the actual character symbol, I chose to work
                   with the ASCII value the reader reads. One less conversion instruction!
                 */
                if ( (charValue >= 9 && charValue <= 13) || charValue == 32) {
                    /* If the charValue is one of the following:
                            HORIZONTAL TAB
                            NL LINE FEED, NEW LINE
                            VERTICAL TAB
                            NP FORM FEED, NEW PAGE
                            CARRIAGE RETURN
                            SPACE
                     */
                    mapState(currentState, charValue, CharType.WS);
                } else if (charValue >= 48 && charValue <= 57) {
                    // If the charValue is a digit (0-9)...
                    mapState(currentState, charValue, CharType.DIGIT);
                } else if ( (charValue >= 65 && charValue <= 90) || (charValue >= 97 && charValue <= 122) ) {
                    // If the charValue is a Letter (A-Z, a-z)...
                    mapState(currentState, charValue, CharType.LETTER);
                } else if (charValue == 42) {
                    // If the charValue is an asterisks (*)...
                    mapState(currentState, charValue, CharType.AST);
                } else if (charValue == 47) {
                    // If the charValue is a forward slash (/)...
                    mapState(currentState, charValue, CharType.FS);
                } else if (charValue == 43) {
                    // If the charValue is an plus (+)...
                    mapState(currentState, charValue, CharType.PLUS);
                } else if (charValue == 45) {
                    // If the charValue is an minus (-)...
                    mapState(currentState, charValue, CharType.MINUS);
                } else if (charValue == 61) {
                    // If the charValue is a equal sign (=)...
                    mapState(currentState, charValue, CharType.EQUAL);
                } else if (charValue == 33) {
                    // If the charValue is an exclamation (!)...
                    mapState(currentState, charValue, CharType.EXC);
                } else if (charValue == 60) {
                    // If the charValue is a less than (<)...
                    mapState(currentState, charValue, CharType.LT);
                } else if (charValue == 62) {
                    // If the charValue is a greater than (>)...
                    mapState(currentState, charValue, CharType.GT);
                } else if (charValue == 123) {
                    // If the charValue is a left bracket ({)...
                    mapState(currentState, charValue, CharType.LB);
                } else if (charValue == 125) {
                    // If the charValue is a right bracket (})...
                    mapState(currentState, charValue, CharType.RB);
                } else if (charValue == 44) {
                    // If the charValue is a comma (,)...
                    mapState(currentState, charValue, CharType.COMMA);
                } else if (charValue == 59) {
                    // If the charValue is a semicolon (;)...
                    mapState(currentState, charValue, CharType.SEMI);
                } else if (charValue == 46) {
                    // If the charValue is a period (.)...
                    mapState(currentState, charValue, CharType.PERIOD);
                } else if (charValue == 40) {
                    // If the charValue is a left parentheses (()...
                    mapState(currentState, charValue, CharType.LP);
                } else if (charValue == 41) {
                    // If the charValue is a right parentheses ())...
                    mapState(currentState, charValue, CharType.RP);
                }
            }

            reader.close();
            writer.close();

        } catch (IOException e) {
            log.printException(e);
        }
    }

    public static void mapState(int state, int value, CharType type) {
        currentState = stateTable[state][type.ordinal()];
        try {
            switch (currentState) {
                case(1):
                    finishWithError();
                    break;
                case(3):
                    bufferHandler(INT);
                    mapState(0, value, type);
                    break;
                case(5):
                    bufferHandler(ID);
                    mapState(0, value, type);
                    break;
                case(10):
                    bufferHandler(MOP);
                    mapState(0, value, type);
                    break;
                case(12):
                    bufferHandler(ADDOP);
                    mapState(0, value, type);
                    break;
                case(14):
                    bufferHandler(ASSIGN);
                    mapState(0, value, type);
                    break;
                case(17):
                    bufferHandler(NEGATE);
                    mapState(0, value, type);
                    break;
                case(19):
                    bufferHandler(RELOP);
                    mapState(0, value, type);
                    break;
                case(21):
                    bufferHandler(LB);
                    mapState(0, value, type);
                    break;
                case(23):
                    bufferHandler(RB);
                    mapState(0, value, type);
                    break;
                case(25):
                    bufferHandler(COMMA);
                    mapState(0, value, type);
                    break;
                case(27):
                    bufferHandler(SEMI);
                    mapState(0, value, type);
                    break;
                case(29):
                    bufferHandler(PERIOD);
                    mapState(0, value, type);
                    break;
                case(31):
                    bufferHandler(LP);
                    mapState(0, value, type);
                    break;
                case(33):
                    bufferHandler(RP);
                    mapState(0, value, type);
                    break;
                case(34):
                    // End of a comment. buffer can be discarded.
                    buffer = new StringBuilder();
                    mapState(0, value, type);
                    break;
                default:
                    if (!type.equals(CharType.WS))
                    buffer.append((char)value);
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            log.printException(e);
        }
    }

    public static void bufferHandler(TokenType classification) {
        if (classification.equals(ID)) {
            if (buffer.toString().equals("CONST"))
                classification = CONST;
            else if (buffer.toString().equals("IF"))
                classification = IF;
            else if (buffer.toString().equals("VAR"))
                classification = VAR;
            else if (buffer.toString().equals("THEN"))
                classification = THEN;
            else if (buffer.toString().equals("PROCEDURE"))
                classification = PROC;
            else if (buffer.toString().equals("WHILE"))
                classification = WHILE;
            else if (buffer.toString().equals("CALL"))
                classification = CALL;
            else if (buffer.toString().equals("DO"))
                classification = DO;
            else if (buffer.toString().equals("ODD"))
                classification = ODD;
            else if (buffer.toString().equals("CLASS"))
                classification = CLASS;
        }

        log.printToken(buffer + " --- " + classification);

        try {
            writer.append(buffer.toString()).append(" --- ").append(classification.toString());
            writer.newLine();
        } catch (IOException e) {
            log.printException(e);
        }
        buffer = new StringBuilder();
    }

    public static void finishWithError() {
        log.printError("Error: Finished at state 1");
    }
}






































