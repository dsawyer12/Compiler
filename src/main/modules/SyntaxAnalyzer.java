package main.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.Productions;
import main.enums.Classification;
import main.enums.Precedence;
import main.src.Logger;
import main.src.NodeStack;

import static main.enums.Precedence.*;
import static main.enums.Classification.*;


public class SyntaxAnalyzer {

    /*
        ANYTHING ENCLOSED IN

        START --------------------------------------------------------
        END --------------------------------------------------------

        IS SIMPLY FOR CONSOLE PRINTING AND FORMATTING. IT CAN BE IGNORED, AS IT HAS NO DIRECT
        RELATION TO THE PARSING PROCESS.
     */

    public static class Node {
        String token;
        Classification classification;
        Precedence precedence;

        Node(String token, Classification classification, Precedence precedence) {
            this.token = token;
            this.classification = classification;
            this.precedence = precedence;
        }
    }

    //  'arr' is a list that is simply used for printing to the console.
    //  Run to see the handles being made as well as the reductions that happen.
    public static ArrayList<String> arr = new ArrayList<>();
    // 'log' is a simple Logging class for console print formatting.
    public static Logger log = Logger.getInstance();
    // 'productions' are the reduction mappings when a handle is found.
    public static Productions productions = new Productions();
    // 'stack' is a simple Stack implementation in which the tokens are pushed and popped in compliance with the rest of the program.
    public static NodeStack<Node> stack = new NodeStack<>();
    // 'prevNode' is used for re-comparing after a reduction is made.
    public static Node prevNode;
//    public static int state = 0;

//    public static int[][] pFunctions = {
//            {1, 2, 2, 24, 2, 7, 22, 2, 22, 2, 7, 9, 10, 11, 12, 15, 12, 20, 2, 15, 7, 2, 24, 22, 7, 12, 7, 9, 11, 7}, // F
//            {1, 2, 2, 15, 2, 3, 3, 7, 7, 2, 7, 8, 9, 10, 11, 20, 12, 3, 8, 2, 3, 16, 2, 2, 12, 7, 15, 9, 11, 7}, // G
//    };

    // Function table that drives the parser
    public static int[][] pFunctions = {
            {2, 1, 2, 31, 4, 32, 35, 35, 2, 10, 29, 29, 2, 29, 29, 3, 8, 10, 11, 12, 13, 17, 13, 24, 10, 3, 17, 3, 17, 8, 4, 31, 35, 8, 13, 8, 10, 12, 8}, // F
            {2, 1, 2, 17, 4, 5, 32, 33, 4, 5, 10, 5, 10, 11, 11, 3, 8, 9, 10, 11, 12, 24, 13, 3, 33, 11, 3, 11, 3, 4, 18, 2, 2, 13, 8, 17, 10, 12, 8}, // G
    };

//    public enum stateDrivers {
//        CLASS, ID ,INT ,CONST, VAR, IF, THEN, WHILE, DO, ODD, LB, RB, SEMI, LP, RP, COMMA, ASSIGN, ADDOP, RELOP, MOP, $
//    }
//
//    // CLASS	ID	INT	CONST	VAR	IF	THEN	WHILE	DO	ODD	LB	RB	SEMI	LP	RP	COMMA	ASSIGN	ADDOP	RELOP	MOP	$
//    public static int[][] states = {
//            {1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  12,  0,  4,  9,  19,  0,  19,  0,  0,  0,  28,  0,  0,  27,  0,  0,  0,  0,  0,  0},
//            {0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  6,  0,  0,  0,  0},
//            {0,  0,  7,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  8,  0,  0,  0,  0,  0,  0,  0,  0},
//            {},  // CONST DEFINE
//            {0,  10,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  11,  0,  0,  9,  0,  0,  0,  0,  0},
//            {},  //  VAR DEFINE
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  13,  0,  0,  0,  0},
//            {0,  14,  14,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  16,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  15,  0,  0,  0,  0,  13,  0,  13,  0},
//            {},  //  SIMPLE STATEMENT
//            {0,  17,  17,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  18,  0,  0,  16,  0,  16,  0},
//            {},  //  EXPRESSION
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  20,  0,  0,  0,  22,  0,  0,  0,  0,  0,  0,  0},
//            {0,  21,  21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {},  //  BOOLEAN EXPRESSION
//            {0,  23,  23,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  24,  0,  0},
//            {0,  25,  25,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  21,  0,  0,  0,  0,  0,  0},
//            {0,  0,  0,  0,  0,  0,  2,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
//            {0,  12,  0,  0,  0,  19,  0,  19,  0,  0,  0,  0,  0,  0,  28,  0,  0,  0,  0,  0,  29},
//            {},  //  END OF A BLOCK STATEMENT
//            {},  //  END OF PROGRAM
//    };

    public static void analyze(File file) {
        // push the program-delimiter into the stack.
        prevNode = new Node("$",  $, YIELDS);
        stack.push(prevNode);

        // Read the tokens created by the Lexical analyzer.
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ( (line = reader.readLine()) != null) {
                // The Lexical Analyzer creates tokens in the form  "tokenName --- Classification"
                // Therefore, we need to split each token by the Lex format.
                String[] lineTokens = line.split(" --- ");

                String token = lineTokens[0];
                String type = lineTokens[1];

                Classification classification = Classification.valueOf(type);
//                log.newLine();
//                log.print(state);
//                stateDrivers driver = stateDrivers.valueOf(type);
//                state = states[state][driver.ordinal()];
                handleToken(token, classification);
            }
            // When no more tokens are found, we still need to push the program-delimiter into the stack and continue.
            handleToken("$", $);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleToken(String token, Classification classification) {
        // Get the precedence function values based on 'prevNode' and the next token. Then compare them.
        int f = pFunctions[0][prevNode.classification.ordinal()];
        int g = pFunctions[1][classification.ordinal()];

        if (f < g) { // 'prevNode' YIELDS in precedence.
            prevNode.precedence = YIELDS;
            // START --------------------------------------------------------
            arr.add(prevNode.classification.toString());
            arr.add(" < ");
            log.printProgress(arr, classification);
            // END --------------------------------------------------------

            prevNode = new Node(token, classification, null);
            stack.push(prevNode);
        }
        else if (f == g) { // 'prevNode' EQUALS in precedence.
            prevNode.precedence = EQUALS;
            // START --------------------------------------------------------
            arr.add(prevNode.classification.toString());
            arr.add(" = ");
            log.printProgress(arr, classification);
            // END --------------------------------------------------------

            prevNode = new Node(token, classification, null);
            stack.push(prevNode);
        }
        else { // 'prevNode' TAKES precedence. Reduce handle.
            // START --------------------------------------------------------
            arr.add(prevNode.classification.toString());
            arr.add(" > ");
            log.printHandle(arr, classification);

            int i = arr.size() - 1;
            do {
                arr.remove(i);
                i = arr.size() - 1;
            } while (!arr.get(i).equals(" < "));

            i = arr.size() - 1;
            arr.remove(i);
            i = arr.size() - 1;
            arr.remove(i);
            // END --------------------------------------------------------

            Node reduction = reduceHandle(token, classification);
            handleToken(reduction.token, reduction.classification);
            handleToken(token, classification);
        }
    }

    public static Node reduceHandle(String token, Classification next) {
        StringBuilder sb = new StringBuilder();
        // stash is used to maintain the previous token's value in case a quad needs to be generated.
        Node stash = new Node(prevNode.token, null, null);
        // 'entries' is used to generate quads when a valid handle is found.
        ArrayList<Node> entries = new ArrayList<>();

        // Pop the stack until the handle is found.
        if (!stack.isEmpty()) {
            Node node;
            while(!stack.isEmpty() && stack.peek().precedence != YIELDS) {
                node = stack.pop();
                entries.add(node);
                sb.insert(0, " " + node.classification);
            }
            prevNode = stack.peek();
            String handle = sb.toString().trim();

            // Reduce the handle using the productions.
            while (productions.reductionMap.containsKey(handle)) {
//                if (checkState())
                if (productions.isValidTableEntry(handle))
                    generateQuad(entries);

                stash.classification = productions.reductionMap.get(handle);
                handle = stash.classification.toString();

                // Compare the precedence relation with the previous and next token;
                int f = pFunctions[0][stash.classification.ordinal()];
                int g = pFunctions[1][next.ordinal()];
                int h = pFunctions[0][prevNode.classification.ordinal()];
                int j = pFunctions[1][stash.classification.ordinal()];
                if (f <= g || h <= j) break;
            }
        }
        return stash;
    }

//    public static boolean checkState() {
//        switch (state) {
//            case (8): // CONST DEFINE
//                log.newLine();
//                log.print(state);
//                log.printError(" CONST DEFINE");
//                state = 3;
//                return true;
//            case (11): // VAR DEFINE
//                log.newLine();
//                log.print(state);
//                log.printError(" VAR DEFINE");
//                state = 3;
//                return true;
//            case (15): // SIMPLE STATEMENT
//                log.newLine();
//                log.print(state);
//                log.printError(" SIMPLE STATEMENT");
//                state = 27;
//                return true;
//            case (18): // EXPRESSION
//                log.newLine();
//                log.print(state);
//                log.printError(" EXPRESSION");
//                state = 14;
//                return true;
//            case (21): // BOOLEAN EXPRESSION
//                log.newLine();
//                log.print(state);
//                log.printError(" BOOLEAN EXPRESSION");
//                state = 26;
//                return true;
//            case (28): // END OF A BLOCK STATEMENT
//                log.newLine();
//                log.print(state);
//                log.printError(" END OF A BLOCK STATEMENT");
//                state = 27;
//                return true;
//            case (29): // END OF PROGRAM
//                log.newLine();
//                log.print(state);
//                log.printError(" END OF PROGRAM");
//                return true;
//            default: return false;
//        }
//    }

    private static void generateQuad(ArrayList<Node> entries) {
        log.newLine();
        log.printException(prevNode.token);
        for (Node n : entries) {
            System.out.println();
            log.printMessage(n.token);
            log.print(" --- ");
            log.printMeta(n.classification);
        }
    }
}





























