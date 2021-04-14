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
    // 'prevNode' is used for re-comparing the preceding non-terminal after a reduction is made.
    public static Node prevNode;

    public static int[][] pFunctions = {
            {1, 2, 2, 24, 2, 7, 22, 2, 22, 2, 7, 9, 10, 11, 12, 15, 12, 20, 2, 15, 7, 2, 24, 22, 7, 12, 7, 9, 11, 7}, // F
            {1, 2, 2, 15, 2, 3, 3, 7, 7, 2, 7, 8, 9, 10, 11, 20, 12, 3, 8, 2, 3, 16, 2, 2, 12, 7, 15, 9, 11, 7}, // G
    };

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

            arr.add(prevNode.classification.toString());
            arr.add(" < ");
            log.printProgress(arr, classification);

            prevNode = new Node(token, classification, null);
            stack.push(prevNode);
        }
        else if (f == g) { // 'prevNode' EQUALS in precedence.
            prevNode.precedence = EQUALS;

            arr.add(prevNode.classification.toString());
            arr.add(" = ");
            log.printProgress(arr, classification);

            prevNode = new Node(token, classification, null);
            stack.push(prevNode);
        }
        else { // 'prevNode' TAKES precedence. Reduce handle.

            // The below is simply for console printing;
            // --------------------------------------------------------
            arr.add(prevNode.classification.toString());
            arr.add(" > ");
            log.printHandle(arr, classification);
            // --------------------------------------------------------

            Classification c = reduceHandle(classification);
            if (c != null) {
                // The below is simply for console printing;
                // --------------------------------------------------------
                int i = arr.size() - 1;
                do {
                    arr.remove(i);
                    i = arr.size() - 1;
                } while (!arr.get(i).equals(" < "));

                i = arr.size() - 1;
                arr.remove(i);
                i = arr.size() - 1;
                arr.remove(i);
                // --------------------------------------------------------

                handleToken(c.toString(), c);
            }
            handleToken(token, classification);
        }
    }

    public static Classification reduceHandle(Classification next) {
        StringBuilder sb = new StringBuilder();
        Classification reduction = null;

        // Pop the stack until the handle is found.
        if (!stack.isEmpty()) {
            Node node;
            while(!stack.isEmpty() && stack.peek().precedence != YIELDS) {
                node = stack.pop();
                sb.insert(0, " " + node.classification);
            }
            prevNode = stack.peek();
            String handle = sb.toString().trim();

            // Reduce the handle using the productions.
            while (productions.reductionMap.containsKey(handle)) {
                reduction = productions.reductionMap.get(handle);
                handle = reduction.toString();

                // I am using the lookahead token to determine when to stop reducing.
                int f = pFunctions[0][reduction.ordinal()];
                int g = pFunctions[1][next.ordinal()];
                if (f < g) break;
            }
        }
        return reduction;
    }
}





























