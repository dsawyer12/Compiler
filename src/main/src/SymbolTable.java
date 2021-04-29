package main.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static main.enums.Classification.CONST;
import static main.enums.Classification.INT;

public class SymbolTable {

    private static SymbolTable symbolTable = null;
    public static Map<Object, Symbol> table = new LinkedHashMap<>();
    private int address;
    private static int temps, currentTemp;

    public static SymbolTable getInstance() {
        if (symbolTable == null)
            symbolTable = new SymbolTable();
        return symbolTable;
    }

    public SymbolTable() {
        table = new LinkedHashMap<>();
        this.address = 0;
        temps = 6;
        currentTemp = 0;
    }

    public void startTable(Symbol pgm) {
        table.put(pgm.token, pgm);
    }

    public static String pushTableEntry(Symbol symbol, Symbol prevSymbol) {
        String[] fragments = symbol.token.split(" ");
        String token, value;

        switch (symbol.classification) {
            case CD: // CONST Define
                token = fragments[1];
                value = fragments[fragments.length - 2];
                getInstance().addSymbol(new Symbol(token, symbol.classification, value, Symbol.Segment.DS));
                break;
            case VD: // VAR Define
                token = fragments[1];
                Symbol sym = new Symbol();
                sym.defUndefined(token, symbol.classification, Symbol.Segment.BSS);
                getInstance().addSymbol(sym);
                break;
            case B_E: // Boolean Expression
            case SS: { // Simple Statement
                if (prevSymbol.classification.equals(CONST)) break;
                for (String s : fragments) {
                    try {
                        // Here we are looking for numeric literals to store in the symbol table.
                        int a = Integer.parseInt(s);
                        String literal = "LIT" + a;
                        getInstance().addSymbol(new Symbol(literal, INT, a, Symbol.Segment.DS));
                        symbol.token = symbol.token.replace(String.valueOf(a), literal);
                    } catch (NumberFormatException ignored) { }
                }
                break;
            }
        }
        return symbol.token;
    }

    public void addSymbol(Symbol symbol) {
        symbol.setAddress(getNextAddress());
        table.put(symbol.token, symbol);
    }

    public int getNextAddress() {
        int temp = address;
        address += 2;
        return temp;
    }

    // Add 6 Temp variables to the symbol table
    public static void addTemps() {
        for (int i = 0; i < temps; i++) {
            Symbol symbol = new Symbol();
            symbol.defUndefined(("T"+i), INT, Symbol.Segment.BSS);
            getInstance().addSymbol(symbol);
        }
    }

    // Check whether we need to use a new temp variable or not and return the appropriate one.
    public static Symbol getNextTemp(String arg1, String arg2) {
        for (int i = 0; i < temps; i++) {
            if (arg1.equals("T" + i) || arg2.equals("T" + i))
                return table.get("T" + currentTemp);
        }
        // Recycle the temps by using modulo
        currentTemp = ++currentTemp % temps;
        return table.get("T" + currentTemp);
    }

    public static Map<Object, Symbol> getTable() {
        return table;
    }

    public void writeSymbolTable() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("assets/symbolTable.txt"));
            writer.append(String.format("%10s%10s%10s%10s%10s", "Symbol", "Class", "Value", "Address", "Segment"));
            for (Map.Entry<Object, Symbol> item : table.entrySet()) {
                writer.newLine();
                writer.append(String.format("%10s", item.getValue().token));
                writer.append(String.format("%10s", item.getValue().classification.toString()));
                writer.append(String.format("%10s", Objects.requireNonNullElse(item.getValue().value, "?")));
                writer.append(String.format("%10s", item.getValue().address));
                writer.append(String.format("%10s", item.getValue().segment));
            }
            writer.close();
        } catch (IOException e) {
            Logger.getInstance().printException(e);
        }
    }

    public static Symbol getSymbol(String key) {
        try {
            int a = Integer.parseInt(key);
            key = "LIT" + a;
        } catch (NumberFormatException ignored) { }
        return table.get(key);
    }
}
