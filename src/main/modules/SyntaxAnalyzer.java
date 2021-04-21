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

        START --------------------------------------------------------
        END --------------------------------------------------------

        IS SIMPLY FOR CONSOLE PRINTING AND FORMATTING. IT CAN BE IGNORED OR DELETED,
         AS IT HAS NO DIRECT RELATION TO THE PARSING PROCESS.
     */

    //  'arr' is a list that is simply used for printing to the console.
    //  Run to see the handles being made as well as the reductions that happen.
    public static ArrayList<String> arr = new ArrayList<>();
    public static Logger log = Logger.getInstance();
    public static Productions productions = new Productions();
    public static NodeStack<Symbol> stack = new NodeStack<>();
    public static Symbol prevSymbol;

    // Function table that drives the parser
    public static int[][] pFunctions = {
            {2, 1, 2, 32, 4, 33, 38, 38, 3, 2, 10, 30, 30, 3, 30, 30, 3, 9, 11, 12, 13, 14, 18, 14, 25, 10, 25, 3, 18, 3, 18, 8, 4, 32, 38, 8, 14, 8, 10, 12, 8}, // F
            {2, 1, 2, 18, 4, 5, 33, 34, 34, 4, 5, 10, 5, 10, 11, 11, 3, 8, 9, 10, 11, 12, 25, 13, 3, 34, 34, 11, 3, 11, 3, 4, 19, 2, 3, 13, 9, 18, 11, 13, 9}, // G
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
                // The Lexical Analyzer creates tokens in the form  "tokenName --- Classification"
                // Therefore, we need to split each token by the Lex format.
                String[] lineTokens = line.split(" --- ");

                String token = lineTokens[0];
                String type = lineTokens[1];

                Classification classification = Classification.valueOf(type);

                if (prevSymbol.classification.equals(CLASS) && classification.equals(ID))
                    SymbolTable.getInstance().startTable(new Symbol(token, PGM, null, Segment.CS));

                handleP1Token(token, classification);
            }
            // When no more tokens are found, we still need to push the program-delimiter into the stack and continue.
            handleP1Token("$", $);

            // Add 3 Temp symbols to the symbol table.
            for (int i = 1; i < 4; i++) {
                Symbol symbol = new Symbol();
                symbol.defUndefined(("T"+i), INT, Segment.BSS);
                SymbolTable.getInstance().addSymbol(symbol);
            }

            SymbolTable.getInstance().printSet();
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
            // START --------------------------------------------------------
            arr.add(prevSymbol.classification.toString());
            arr.add(" < ");
            log.printProgress(arr, classification);
            // END --------------------------------------------------------

            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else if (f == g) { // 'prevNode' EQUALS in precedence.
            prevSymbol.precedence = EQUALS;
            // START --------------------------------------------------------
            arr.add(prevSymbol.classification.toString());
            arr.add(" = ");
            log.printProgress(arr, classification);
            // END --------------------------------------------------------

            prevSymbol = new Symbol(token, classification, null);
            stack.push(prevSymbol);
        }
        else { // 'prevNode' TAKES precedence. Reduce handle.
            // START --------------------------------------------------------
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
            // END --------------------------------------------------------

            Symbol reduction = reduceP1Handle();
            handleP1Token(reduction.token, reduction.classification);
            handleP1Token(token, classification);
        }
    }

    public static Symbol reduceP1Handle() {
        StringBuilder cb = new StringBuilder();
        StringBuilder tb = new StringBuilder();
//         stash is used to maintain the previous token's value in case a quad needs to be generated.
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
                String[] lineTokens = line.split(" --- ");

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

        if (handle.equals("ID ASSIGN E")) {
            if (!prevSymbol.classification.equals(CONST))
                stash.token = CodeGenerator.getInstance().generateCode(ASSIGN, stash);
        } else if (handle.equals("TERM MOP F")) {
            stash.token = CodeGenerator.getInstance().generateCode(MOP, stash);
        } else if (handle.equals("EXP ADDOP T")) {
            stash.token = CodeGenerator.getInstance().generateCode(ADDOP, stash);
        } else if (handle.equals("E RELOP E")) {
            stash.token = CodeGenerator.getInstance().generateCode(RELOP, stash);
        } else if (handle.equals("IF B_E THEN BLOCK")) {
            stash.token = CodeGenerator.getInstance().generateCode(IF_S, stash);
        }

        return stash;
    }
}





























