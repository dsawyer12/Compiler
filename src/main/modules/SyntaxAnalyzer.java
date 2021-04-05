package main.modules;

import main.src.NodeStack;
import main.src.Token.Classification;
import static main.src.Token.Classification.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class SyntaxAnalyzer {

    // Note that for the given table, 0 -> '', 1 -> '=', 2 -> '<', and 3 -> '>'
    // 4 = '<>' and 5 = '=>'

//    public static enum Classification {
//        $, $CLASS, $CONST, $VAR, $PROC, $CALL, $IF, $THEN, $WHILE, $DO, $ODD, $lb, $rb, $semi, $lp, $rp, $comma, $assign, $addop, $relop, $mop
//    }

    public static class Node {
        String token;
        int precedence;
        Classification classification;

        public Node() { }

        Node(String token, int precedence, Classification classification) {
            this.token = token;
            this.precedence = precedence;
            this.classification = classification;
        }
    }

    public static int currentState = 0;
    public static NodeStack<Node> stack = new NodeStack<>();
    public static Node prevNode;

    public static int[][] OPG = {
            { 0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, // 0 $
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0 }, // 1 $CLASS
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   2,   0,   0,   0 }, // 2 $CONST
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   0,   0,   0,   0 }, // 3 $VAR
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0 }, // 4 $PROC
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0 }, // 5 $CALL
            { 0,   0,   0,   0,   0,   0,   0,   1,   0,   0,   2,   0,   0,   0,   2,   0,   0,   0,   2,   2,   2 }, // 6 $IF
            { 0,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // 7 $THEN
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   2,   0,   0,   0,   2,   0,   0,   0,   2,   2,   2 }, // 8 $WHILE
            { 0,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // 9 $DO
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   0,   2,   0,   0,   0,   2,   0,   2 }, // 10 $ODD
            { 0,   0,   2,   2,   2,   2,   2,   0,   2,   0,   0,   2,   1,   2,   0,   0,   0,   2,   0,   0,   0 }, // 11 $lb
            { 1,   0,   0,   0,   0,   2,   2,   0,   2,   0,   0,   2,   3,   2,   0,   0,   0,   2,   0,   0,   0 }, // 12 $rb
            { 0,   0,   0,   1,   1,   2,   2,   0,   2,   0,   0,   2,   3,   4,   0,   0,   0,   2,   0,   0,   0 }, // 13 $semi
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   2,   1,   2,   0,   2,   0,   2 }, // 14 $lp
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   1,   3,   3,   0,   3,   0,   0,   3,   3,   3 }, // 15 $rp
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   3,   1,   0,   0,   0 }, // 16 $comma
            { 0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   5,   2,   0,   3,   0,   2,   0,   2 }, // 17 $assign
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   3,   2,   3,   0,   0,   3,   3,   2 }, // 18 addop
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   0,   2,   0,   0,   0,   2,   0,   2 }, // 19 relop
            { 0,   0,   0,   0,   0,   0,   0,   3,   0,   3,   0,   0,   0,   3,   2,   3,   0,   0,   3,   3,   3 }, // 20 mop

    };

    public static void analyze(File file) {
        stack.push(new Node("$", 2, null));

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
                    else if (type.equals($CLASS.toString()))
                        handleToken(1, token, $CLASS);
                    else if (type.equals($CONST.toString()))
                        handleToken(2, token, $CONST);
                    else if (type.equals($VAR.toString()))
                        handleToken(3, token, $VAR);
                    else if (type.equals(Classification.$PROC.toString()))
                        handleToken(4, token, $PROC);
                    else if (type.equals(Classification.$CALL.toString()))
                        handleToken(5, token, $CALL);
                    else if (type.equals(Classification.$IF.toString()))
                        handleToken(6, token, $IF);
                    else if (type.equals(Classification.$THEN.toString()))
                        handleToken(7, token, $THEN);
                    else if (type.equals(Classification.$WHILE.toString()))
                        handleToken(8, token, $WHILE);
                    else if (type.equals(Classification.$DO.toString()))
                        handleToken(9, token, $DO);
                    else if (type.equals(Classification.$ODD.toString()))
                        handleToken(10, token, $ODD);
                    else if (type.equals(Classification.$lb.toString()))
                        handleToken(11, token, $lb);
                    else if (type.equals(Classification.$rb.toString()))
                        handleToken(12, token, $rb);
                    else if (type.equals(Classification.$semi.toString()))
                        handleToken(13, token, $semi);
                    else if (type.equals(Classification.$lp.toString()))
                        handleToken(14, token, $lp);
                    else if (type.equals(Classification.$rp.toString()))
                        handleToken(15, token, $rp);
                    else if (type.equals(Classification.$comma.toString()))
                        handleToken(16, token, $comma);
                    else if (type.equals(Classification.$assign.toString()))
                        handleToken(17, token, $assign);
                    else if (type.equals(Classification.$addop.toString()))
                        handleToken(18, token, $addop);
                    else if (type.equals(Classification.$relop.toString()))
                        handleToken(19, token, $relop);
                    else if (type.equals(Classification.$mop.toString()))
                        handleToken(20, token, $mop);
                    else if (type.equals($id.toString()) || type.equals($int.toString()))
                        handleToken(-1, token, $id);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleToken(int nextState, String token, Classification classification) {
        if (nextState >= 0) {
            int precedence = OPG[currentState][nextState];
            switch (precedence) {
                case (1): // The token equals in precedence to the previous.
                case (2): // The token yields in precedence to the previous.
                    if (prevNode != null)
                        prevNode.precedence = precedence;
                    prevNode = new Node(token, -1, classification);
                    stack.push(prevNode);
                    currentState = nextState;
                    break;
                case (3):
                    // The token takes precedence over the previous. Reduce and Continue...
                    reduceHandle(token, classification);
                    break;
            }
        } else {
            // An $id or $int was found. push into the stack and continue...
            stack.push(new Node(token, -1, classification));
        }
    }

    private static void reduceHandle(String token, Classification classification) {
        StringBuilder sb = new StringBuilder();

        if (!stack.isEmpty()) {
            Node node;
            do {
                node = stack.pop();
                sb.insert(0, node.classification + " ");
            } while(!stack.isEmpty() && stack.peek().precedence != 2);

            System.out.println(sb);
            sb = new StringBuilder();
        }
//        while(!stack.isEmpty()) {
//            Node node = stack.pop();
//
//            if (node.precedence != 2)
//                sb.insert(0, node.classification + " ");
//            else
//                break;
//        }
    }

    public static class PGM extends Node {

    }
    public static class BLOCK extends Node {

    }
    public static class C extends Node {

    }
    public static class CONST_LIST extends Node {

    }
    public static class V extends Node {

    }
    public static class VAR_LIST extends Node {

    }
    public static class I extends Node {

    }
    public static class ID_LIST extends Node {

    }
    public static class STMT extends Node {

    }
    public static class SS extends Node {

    }
    public static class CALL_STMT extends Node {

    }
    public static class CMPD_STMT extends Node {

    }
    public static class IF_STMT extends Node {

    }
    public static class WHILE_STMT extends Node {

    }
    public static class S extends Node {

    }
    public static class STMT_LIST extends Node {

    }
    public static class BOOL_EXP extends Node {

    }
    public static class E extends Node {

    }
    public static class EXP extends Node {

    }
    public static class T extends Node {

    }
    public static class TERM extends Node {

    }
    public static class FAC extends Node {

    }
    public static class ID extends Node {

    }
    public static class INT extends Node {

    }
}























