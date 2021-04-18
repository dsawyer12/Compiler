package main.modules;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.Productions;
import main.enums.Classification;
import main.src.Symbol;
import main.src.Symbol.Segment;
import main.src.Logger;
import main.src.NodeStack;
import main.src.SymbolTable;

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

    //  'arr' is a list that is simply used for printing to the console.
    //  Run to see the handles being made as well as the reductions that happen.
    public static ArrayList<String> arr = new ArrayList<>();
    // 'log' is a simple Logging class for console print formatting.
    public static Logger log = Logger.getInstance();
    // 'productions' are the reduction mappings when a handle is found.
    public static Productions productions = new Productions();
    // 'stack' is a simple Stack implementation in which the tokens are pushed and popped in compliance with the rest of the program.
    public static NodeStack<Symbol> stack = new NodeStack<>();
    // 'prevNode' is used for re-comparing after a reduction is made.
    public static Symbol prevSymbol;
    public static Map<Object, Symbol> table = new HashMap<>();
    public static BufferedWriter dataWriter, codeWriter;

    // Function table that drives the parser
    public static int[][] pFunctions = {
            {2, 1, 2, 31, 4, 32, 35, 35, 2, 10, 29, 29, 2, 29, 29, 3, 8, 10, 11, 12, 13, 17, 13, 24, 10, 3, 17, 3, 17, 8, 4, 31, 35, 8, 13, 8, 10, 12, 8}, // F
            {2, 1, 2, 17, 4, 5, 32, 33, 4, 5, 10, 5, 10, 11, 11, 3, 8, 9, 10, 11, 12, 24, 13, 3, 33, 11, 3, 11, 3, 4, 18, 2, 2, 13, 8, 17, 10, 12, 8}, // G
    };

    public static void analyze(File file) {
        // push the program-delimiter into the stack.
        prevSymbol = new Symbol("$",  $, YIELDS);
        stack.push(prevSymbol);

        // Read the tokens created by the Lexical analyzer.
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            dataWriter = new BufferedWriter(new FileWriter("assets/dataSegment.txt"));
            codeWriter = new BufferedWriter(new FileWriter("assets/codeSegment.txt"));

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

                handleToken(token, classification);
            }
            // When no more tokens are found, we still need to push the program-delimiter into the stack and continue.
            handleToken("$", $);

            SymbolTable.getInstance().printTable();

        } catch (IOException e) {
            log.printException(e);
        }
    }

    private static void handleToken(String token, Classification classification) {
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

            Symbol reduction = reduceHandle();
            handleToken(reduction.token, reduction.classification);
            handleToken(token, classification);
        }
    }

    public static Symbol reduceHandle() {
        StringBuilder cb = new StringBuilder();
        StringBuilder tb = new StringBuilder();
//         stash is used to maintain the previous token's value in case a quad needs to be generated.
        Symbol stash = new Symbol();
        // Pop the stack until the handle is found.
        if (!stack.isEmpty()) {
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

            checkValidTableEntry(stash);

        }
        return stash;
    }

    private static void checkValidTableEntry(Symbol symbol) {
        log.newLine();
        log.print(symbol.token);
        String[] arr = symbol.token.split(" ");
        String token, value;

        switch (symbol.classification) {
            case CD:
                token = arr[1];
                value = arr[arr.length - 2];
                table.put(token, new Symbol(token, symbol.classification, value, Segment.DS));
                SymbolTable.getInstance().addSymbol(new Symbol(token, symbol.classification, value, Segment.DS));
                break;
//            case VD:
//                break;
            case SS: {
                if (prevSymbol.classification.equals(CONST)) break;

                for (int i = 0; i < arr.length; i++) {
                    try {
                        int a = Integer.parseInt(arr[i]);
                        table.put(a, new Symbol(arr[i], INT, a, Segment.DS));
                        SymbolTable.getInstance().addSymbol(new Symbol(arr[i], INT, a, Segment.DS));
                    } catch (NumberFormatException e) {
                        log.newLine();
                        log.printException(e);
                    }
                }
                break;
            }
            case B_E: {

                break;
            }
        }
        createCode(symbol);
    }

    private static void createCode(Symbol stash) {
        switch (stash.classification) {
            case ASSIGN: {
                log.newLine();
                log.printMessage(stash.toString());
            }
            case ADDOP: {

            }
            case MOP: {

            }
            case RELOP: {

            }
        }
    }

}





























