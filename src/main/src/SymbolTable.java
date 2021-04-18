package main.src;

import java.util.ArrayList;

public class SymbolTable {

    private static SymbolTable symbolTable = null;
    private int address;
    private ArrayList<Symbol> table;

    public static SymbolTable getInstance() {
        if (symbolTable == null)
            symbolTable = new SymbolTable();
        return symbolTable;
    }

    public SymbolTable() {
        this.table = new ArrayList<>();
        this.address = 0;
    }

    public void startTable(Symbol pgm) {
        table.add(pgm);
    }

    public void addSymbol(Symbol symbol) {
        symbol.setAddress(address);
        table.add(symbol);
        address = address + 2;
    }

    public void printTable() {
        Logger log = Logger.getInstance();
        log.newLine();
        log.printMeta("Symbol\t\tClass\t\tValue\t\tAddress\t\tSegment");
        for(Symbol s : getInstance().table) {
            log.newLine();
            log.print(s.token + "\t\t\t" + s.classification);
            if (s.value == null)
                log.print("\t\t\t ");
            else
                log.print("\t\t\t" + s.value);
            log.print("\t\t\t" + s.address
                    + "\t\t\t" + s.segment);
        }
    }
}
