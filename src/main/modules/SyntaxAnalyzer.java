package main.modules;

import java.io.*;
import java.util.ArrayList;

import main.Productions;
import main.enums.Classification;
import main.src.*;
import main.src.Symbol.Segment;

import static main.enums.Precedence.*;
import static main.enums.Classification.*;


public class SyntaxAnalyzer {

    /*
        ANYTHING ENCLOSED IN

        START PRINT --------------------------------------------------------
        END PRINT --------------------------------------------------------

        IS SIMPLY FOR CONSOLE PRINTING AND FORMATTING. IT CAN BE IGNORED OR DELETED,
         AS IT HAS NO DIRECT RELATION TO THE PARSING PROCESS.
     */

    //  'arr' and 'log' are simply used for printing to the console.
    //  Run to see the handles being made as well as the reductions that happen.
    public static ArrayList<String> arr = new ArrayList<>();
    public static Logger log = Logger.getInstance();

    // A production map used for reducing a handle when found.
    public static Productions productions = new Productions();
    public static NodeStack<Symbol> stack = new NodeStack<>();
    public static Symbol prevSymbol;

    // Precedence function table that drives the parser.
    public static int[][] pFunctions = {
            {2, 1, 2, 38, 4, 7, 38, 5, 42, 4, 5, 49, 49, 5, 4, 14, 36, 36, 5, 36, 36, 3, 11, 13, 14, 15, 16, 20, 16, 31, 31, 31, 14, 31, 3, 20, 3, 20, 8, 4, 38, 49, 8, 16, 8, 10, 12, 8}, // F
            {2, 1, 2, 20, 4, 5, 7, 42, 8, 4, 5, 7, 8, 8, 4, 5, 14, 5, 14, 15, 15, 3, 8, 9, 10, 11, 12, 31, 13, 43, 5, 3, 8, 8, 15, 3, 15, 3, 4, 21, 4, 5, 13, 11, 20, 13, 15, 11}, // G
    };

    public static void pass1(File file) {
        // push the program-delimiter into the stack.
        prevSymbol = new Symbol("$",  $, YIELDS);
        stack.push(prevSymbol);

        // Read the tokens created by the Lexical analyzer.
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ( (line = reader.readLine()) != null) {
                // Split the line by the format used by the lexical analyzer to separate the token and it's corresponding classification.
                String[] lineTokens = line.trim().split("[ ]+");

                String token = lineTokens[0];
                String type = lineTokens[1];

                Classification classification = Classification.valueOf(type);

                if (prevSymbol.classification.equals(CLASS) && classification.equals(ID))
                    SymbolTable.getInstance().startTable(new Symbol(token, PGM, null, Segment.CS));

                handleP1Token(token, classification);
            }
            // When no more tokens are found, push the closing program-delimiter into the stack and continue.
            handleP1Token("$", $);

            SymbolTable.addTemps();
            SymbolTable.getInstance().writeSymbolTable();
            CodeGenerator.getInstance().appendTableData(SymbolTable.getTable());
            pass2(file);

        } catch (IOException e) {
            log.printException(e);
        }
    }

    private static void handleP1Token(String token, Classification classification) {
        // Get the precedence function values based on 'prevNode' and the next token. Then compare them.
        int f = pFunctions[0][prevSymbol.classification.ordinal()];
        int g = pFunctions[1][classification.ordinal()];

        if (f < g) { // 'prevNode' YIELDS in precedence.
            prevSymbol.precedence = YIELDS;
            // START PRINT --------------------------------------------------------
            arr.add(prevSymbol.classification.toString());
            arr.add(" < ");
            log.printProgress(arr, classification);
            // END PRINT --------------------------------------------------------

            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else if (f == g) { // 'prevNode' EQUALS in precedence.
            prevSymbol.precedence = EQUALS;
            // START PRINT --------------------------------------------------------
            arr.add(prevSymbol.classification.toString());
            arr.add(" = ");
            log.printProgress(arr, classification);
            // END PRINT --------------------------------------------------------

            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else { // 'prevNode' TAKES precedence. Reduce handle.
            // START PRINT --------------------------------------------------------
            arr.add(prevSymbol.classification.toString());
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
            // END PRINT --------------------------------------------------------

            Symbol reduction = reduceP1Handle();
            // After a reduction, compare precedence between prevSymbol and the new reduced symbol...
            handleP1Token(reduction.token, reduction.classification);
            // ...Then compare the new reduced symbol with following one.
            handleP1Token(token, classification);
        }
    }

    public static Symbol reduceP1Handle() {
        StringBuilder cb = new StringBuilder(); // Classification builder
        StringBuilder tb = new StringBuilder(); // Token builder
        // stash is used to maintain the previous token's value while continuous reductions happen.
        Symbol stash = new Symbol();
        // Pop the stack until the handle is found.
        Symbol symbol;
        while(!stack.isEmpty() && stack.peek().precedence != YIELDS) {
            symbol = stack.pop();
            cb.insert(0, " " + symbol.classification);
            tb.insert(0, " " + symbol.token);
        }
        String handle = cb.toString().trim();

        prevSymbol.token = tb.toString().trim();
        prevSymbol = stack.peek();

        stash.token = tb.toString().trim();
        stash.classification = productions.reductionMap.get(handle);

        stash.token = SymbolTable.pushTableEntry(stash, prevSymbol);

        return stash;
    }

    private static void pass2(File file) {
        prevSymbol = new Symbol("$",  $, YIELDS);
        stack.push(prevSymbol);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ( (line = reader.readLine()) != null) {
                String[] lineTokens = line.trim().split("[ ]+");

                String token = lineTokens[0];
                String type = lineTokens[1];

                Classification classification = Classification.valueOf(type);

                handleP2Token(token, classification);
            }
            handleP2Token("$", $);
            CodeGenerator.getInstance().assemble();

        } catch (IOException e) {
            log.printException(e);
        }
    }

    private static void handleP2Token(String token, Classification classification) {
        int f = pFunctions[0][prevSymbol.classification.ordinal()];
        int g = pFunctions[1][classification.ordinal()];

        if (f < g) {
            prevSymbol.precedence = YIELDS;
            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else if (f == g) {
            prevSymbol.precedence = EQUALS;
            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else {
            Symbol reduction = reduceP2Handle();
            handleP2Token(reduction.token, reduction.classification);
            handleP2Token(token, classification);
        }
    }

    public static Symbol reduceP2Handle() {
        StringBuilder cb = new StringBuilder();
        StringBuilder tb = new StringBuilder();
        Symbol stash = new Symbol();
        Symbol symbol;
        while(!stack.isEmpty() && stack.peek().precedence != YIELDS) {
            symbol = stack.pop();
            cb.insert(0, " " + symbol.classification);
            tb.insert(0, " " + symbol.token);
        }
        String handle = cb.toString().trim();

        prevSymbol.token = tb.toString().trim();
        prevSymbol = stack.peek();

        stash.token = tb.toString().trim();
        stash.classification = productions.reductionMap.get(handle);

        // Call the Code Generator to generate code based on the reduction that was made.
        if (handle.equals("LP E RP"))
            stash.token = stash.token.replace("(", "").replace(")", "").trim();
        if (handle.equals("ID ASSIGN E")) {
            if (!prevSymbol.classification.equals(CONST))
                stash.token = CodeGenerator.getInstance().generateCode(ASSIGN, stash, prevSymbol);
        } else if (handle.equals("TERM MOP F")) {
            stash.token = CodeGenerator.getInstance().generateCode(MOP, stash, prevSymbol);
        } else if (handle.equals("EXP ADDOP T")) {
            stash.token = CodeGenerator.getInstance().generateCode(ADDOP, stash, prevSymbol);
        } else if (handle.equals("E RELOP E")) {
            stash.token = CodeGenerator.getInstance().generateCode(RELOP, stash, prevSymbol);
        } else if (handle.equals("IF B_E THEN BLOCK")) {
            stash.token = CodeGenerator.getInstance().generateCode(IF_S, stash, prevSymbol);
        } else if (handle.equals("WHILE B_E DO BLOCK")) {
            stash.token = CodeGenerator.getInstance().generateCode(WHILE_S, stash, prevSymbol);
        } else if (handle.equals("GET ID")) {
            stash.token = CodeGenerator.getInstance().generateCode(RI, stash, prevSymbol);
        } else if (handle.equals("PRINT ID")) {
            stash.token = CodeGenerator.getInstance().generateCode(P, stash, prevSymbol);
        }

        return stash;
    }
}





























