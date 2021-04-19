package main.src;

import java.util.LinkedHashMap;
import java.util.Map;

import static main.enums.Classification.CONST;
import static main.enums.Classification.INT;

public class SymbolTable {

    private static SymbolTable symbolTable = null;
    public static Map<Object, Symbol> table = new LinkedHashMap<>();
    private int address;

    public static SymbolTable getInstance() {
        if (symbolTable == null)
            symbolTable = new SymbolTable();
        return symbolTable;
    }

    public SymbolTable() {
        table = new LinkedHashMap<>();
        this.address = 0;
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
            case SS: { // Simple Statement
                if (prevSymbol.classification.equals(CONST)) break;
                for (String s : fragments) {
                    try {
                        int a = Integer.parseInt(s);
                        String literal = "LIT" + a;
                        getInstance().addSymbol(new Symbol(literal, INT, a, Symbol.Segment.DS));
                        symbol.token = symbol.token.replace(String.valueOf(a), literal);
                    } catch (NumberFormatException e) { }
                }
                break;
            }
            case B_E: {

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

    public static Map<Object, Symbol> getTable() {
        return table;
    }

    public void printSet() {
        Logger log = Logger.getInstance();
        log.newLine();
        log.printMeta("Symbol\t\tClass\t\tValue\t\tAddress\t\tSegment");
        for (Map.Entry<Object, Symbol> item : table.entrySet()) {
            log.newLine();
            log.printMessage(item.getValue().token + "\t");
            log.printMessage(item.getValue().classification.toString() + "\t");
            if (item.getValue().value != null)
                log.printMessage(item.getValue().value + "\t");
            log.printMessage(item.getValue().address + "\t");
            log.printMessage(item.getValue().segment.toString() + "\t");
        }
    }

    public static Symbol getSymbol(String key) {
        try {
            int a = Integer.parseInt(key);
            key = "LIT" + a;
        } catch (NumberFormatException e) { }
        return table.get(key);
    }
}
