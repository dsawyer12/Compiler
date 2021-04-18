package main.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private static SymbolTable symbolTable = null;
    public static Map<Object, Symbol> table = new HashMap<>();
    private int address;

    public static SymbolTable getInstance() {
        if (symbolTable == null)
            symbolTable = new SymbolTable();
        return symbolTable;
    }

    public SymbolTable() {
        table = new HashMap<>();
        this.address = 0;
    }

    public void startTable(Symbol pgm) {
        table.put(pgm.token, pgm);
    }

    public void addSymbol(Symbol symbol) {
        symbol.setAddress(address);
        table.put(symbol.token, symbol);
        address += 2;
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
                log.printMessage(item.getValue().value.toString() + "\t");
            log.printMessage(item.getValue().address + "\t");
            log.printMessage(item.getValue().segment.toString() + "\t");
        }
    }
}
