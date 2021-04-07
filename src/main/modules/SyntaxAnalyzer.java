package main.modules;

import main.enums.Classification;
import main.Productions;
import main.src.NodeStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static main.enums.Classification.*;


public class SyntaxAnalyzer {

    public static class Node {
        String token;
        int precedence, state;
        Classification classification;

        public Node() { }

        Node(String token, int precedence, int state, Classification classification) {
            this.token = token;
            this.precedence = precedence;
            this.classification = classification;
        }
    }

    public static int currentState = 0;
    public static NodeStack<Node> stack = new NodeStack<>();
    public static Node prevNode;

    // Note that for the given table, 0 -> '', 1 -> '=', 2 -> '<', and 3 -> '>'
    // 4 = '<>' and 5 = '=>'
    public static int[][] OPG = {
            { 0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, // $
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, // CLASS
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   2,   0,   0,   0 }, // CONST
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   0,   0,   0,   0 }, // VAR
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0 }, // PROC
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0 }, // CALL
            { 0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   0,   0,   0,   2,   0,   0,   0,   2,   2,   2 }, // IF
            { 0,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // THEN
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   2,   0,   0,   0,   2,   0,   0,   0,   2,   2,   2 }, // WHILE
            { 0,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // DO
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   0,   2,   0,   0,   0,   2,   0,   2 }, // ODD
            { 0,   0,   2,   2,   2,   2,   2,   0,   2,   0,   0,   2,   1,   2,   0,   0,   0,   2,   0,   0,   0 }, // LB
            { 1,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // RB
            { 0,   0,   0,   1,   1,   2,   2,   0,   2,   0,   0,   2,   3,   0,   0,   0,   0,   2,   0,   0,   0 }, // SEMI
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   2,   1,   0,   0,   2,   0,   2 }, // LP
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   1,   3,   3,   0,   3,   0,   0,   3,   3,   3 }, // RP
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   3,   0,   0,   3,   1,   0,   0,   0 }, // COMMA
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   3,   2,   0,   3,   0,   2,   0,   2 }, // ASSIGN
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   3,   2,   3,   0,   0,   3,   3,   2 }, // ADDOP
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   0,   2,   0,   0,   0,   2,   0,   2 }, // RELOP
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   3,   2,   3,   0,   0,   3,   3,   3 }, // MOP
    };

    public static void analyze(File file) {
        stack.push(new Node("$", 2, 0, null));

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ( (line = reader.readLine()) != null) {
                String[] lineTokens = line.split(" --- ");

                String token = lineTokens[0];
                String type = lineTokens[1];
                
                if (type != null) {
                    if (type.equals("$"))
                        handleToken(0, token, null);
                    else if (type.equals(CLASS.toString()))
                        handleToken(1, token, CLASS);
                    else if (type.equals(CONST.toString()))
                        handleToken(2, token, CONST);
                    else if (type.equals(VAR.toString()))
                        handleToken(3, token, VAR);
                    else if (type.equals(PROC.toString()))
                        handleToken(4, token, PROC);
                    else if (type.equals(CALL.toString()))
                        handleToken(5, token, CALL);
                    else if (type.equals(IF.toString()))
                        handleToken(6, token, IF);
                    else if (type.equals(THEN.toString()))
                        handleToken(7, token, THEN);
                    else if (type.equals(WHILE.toString()))
                        handleToken(8, token, WHILE);
                    else if (type.equals(DO.toString()))
                        handleToken(9, token, DO);
                    else if (type.equals(ODD.toString()))
                        handleToken(10, token, ODD);
                    else if (type.equals(LB.toString()))
                        handleToken(11, token, LB);
                    else if (type.equals(RB.toString()))
                        handleToken(12, token, RB);
                    else if (type.equals(SEMI.toString()))
                        handleToken(13, token, SEMI);
                    else if (type.equals(LP.toString()))
                        handleToken(14, token, LP);
                    else if (type.equals(RP.toString()))
                        handleToken(15, token, RP);
                    else if (type.equals(COMMA.toString()))
                        handleToken(16, token, COMMA);
                    else if (type.equals(ASSIGN.toString()))
                        handleToken(17, token, ASSIGN);
                    else if (type.equals(ADDOP.toString()))
                        handleToken(18, token, ADDOP);
                    else if (type.equals(RELOP.toString()))
                        handleToken(19, token, RELOP);
                    else if (type.equals(MOP.toString()))
                        handleToken(20, token, MOP);
                    else if (type.equals(ID.toString()))
                        handleToken(-1, token, ID);
                    else if (type.equals(INT.toString()))
                        handleToken(-1, token, INT);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleToken(int nextState, String token, Classification classification) {
        if (nextState >= 0) {
            int precedence = OPG[currentState][nextState];
            currentState = nextState;
            switch (precedence) {
                case (1): // The token equals in precedence to the previous.
                case (2): // The token yields in precedence to the previous.
                    if (prevNode != null)
                        prevNode.precedence = precedence;
                    break;
                case (3):
                    // The token takes precedence over the previous. Reduce and Continue...
                    reduceHandle(token, classification);
                    break;
            }
            prevNode = new Node(token, -1, nextState, classification);
            stack.push(prevNode);
        } else {
            // An ID or INT was found. push into the stack and continue...
            stack.push(new Node(token, -1, currentState, classification));
        }
    }

    private static void reduceHandle(String token, Classification classification) {
        StringBuilder sb = new StringBuilder();
        Productions productions = new Productions();

        if (!stack.isEmpty()) {
            Node node;
            while(!stack.isEmpty() && stack.peek().precedence != 2) {
                node = stack.pop();
                sb.insert(0, " " + node.classification);
            }
            prevNode = stack.peek();
//            currentState = prevNode.state;
//            do {
//                node = stack.pop();
//                sb.insert(0, node.classification + " ");
//            } while(!stack.isEmpty() && stack.peek().precedence != 2);

            Classification c = productions.getReduction(sb.toString());
            if (c != null)
                stack.push(new Node(c.toString(), -1, currentState, c));
        }
    }
}























