package main.modules;

import main.src.Token;

import java.io.*;

public class Lexer {

    enum CharType {
        DIGIT, LETTER, AST, FS, PLUS, MINUS, EQUAL, EXC,
        LT, GT, LB, RB, COMMA, SEMI, PERIOD, LP, RP, WS
    }

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
            {13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13},
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
    static File symbolTable;
    static BufferedWriter writer;

    public Lexer() {
        symbolTable = new File("assets/symbolTable.txt");
    }

    public void scan(File program) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(program));
            writer = new BufferedWriter(new FileWriter(symbolTable));
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
            System.out.println(e.getMessage());
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
                    bufferHandler(Token.Classification.$int);
                    mapState(0, value, type);
                    break;
                case(5):
                    bufferHandler(Token.Classification.$var);
                    mapState(0, value, type);
                    break;
                case(10):
                    bufferHandler(Token.Classification.$mop);
                    mapState(0, value, type);
                    break;
                case(12):
                    bufferHandler(Token.Classification.$addop);
                    mapState(0, value, type);
                    break;
                case(14):
                    bufferHandler(Token.Classification.$assign);
                    mapState(0, value, type);
                    break;
                case(17):
                    bufferHandler(Token.Classification.$negate);
                    mapState(0, value, type);
                    break;
                case(19):
                    bufferHandler(Token.Classification.$relop);
                    mapState(0, value, type);
                    break;
                case(21):
                    bufferHandler(Token.Classification.$lb);
                    mapState(0, value, type);
                    break;
                case(23):
                    bufferHandler(Token.Classification.$rb);
                    mapState(0, value, type);
                    break;
                case(25):
                    bufferHandler(Token.Classification.$comma);
                    mapState(0, value, type);
                    break;
                case(27):
                    bufferHandler(Token.Classification.$semi);
                    mapState(0, value, type);
                    break;
                case(29):
                    bufferHandler(Token.Classification.$period);
                    mapState(0, value, type);
                    break;
                case(31):
                    bufferHandler(Token.Classification.$lp);
                    mapState(0, value, type);
                    break;
                case(33):
                    bufferHandler(Token.Classification.$rp);
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
            System.out.println(e.getMessage());
        }
    }

    public static void bufferHandler(Token.Classification classification) {
        // From here, the 'classification' will need further inspection (not implemented yet) in order
        // to determine the reserved keywords. For now, every string is classified as a $var
        System.out.println(buffer + " --- " + classification);
        try {
            writer.append(buffer.toString()).append(" --- ").append(classification.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        buffer = new StringBuilder();
    }

    public static void finishWithError() {
        System.out.println("Error: Finished at state 1");
    }
}