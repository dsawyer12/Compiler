package main.src;

import main.enums.Classification;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

import static main.enums.Classification.CONST;
import static main.enums.Classification.INT;

public class CodeGenerator {
    public static BufferedWriter dataWriter, codeWriter, bssWriter;
    public static int nextLabel;

    public static class Label {
        String label;
        int address;

        public Label(String label, int address) {
            this.label = label;
            this.address = address;
        }
    }

    public NodeStack<Label> fixUpStack;
//    public NodeStack<Label> endStack;

    private static CodeGenerator codeGenerator = null;

    public static CodeGenerator getInstance() {
        if (codeGenerator == null)
            codeGenerator = new CodeGenerator();
        return codeGenerator;
    }

    static final String dataDef = "sys_exit\tequ\t1\n" +
            "sys_read\tequ\t3\n" +
            "sys_write\tequ\t4\n" +
            "stdin\t\tequ\t0\n" +
            "stdout\t\tequ\t1\n" +
            "stderr\t\tequ\t3\n" +
            "\n" +
            "section .data\t\n" +
            "\tuserMsg\t\t\tdb\t'Enter an integer(less than 32,765): '\n" +
            "\tlenUserMsg\t\tequ\t$-userMsg\n" +
            "\tdisplayMsg\t\tdb\t'You entered: '\n" +
            "\tlenDisplayMsg\tequ\t$-displayMsg\n" +
            "\tnewline\t\t\tdb\t0xA\n" +
            "\n" +
            "\tTen\t\t\t\tDW\t10\n" +
            "\n" +
            "\tResult\t\t\tdb\t'Ans = '\n" +
            "\tResultValue\t\tdb\t'aaaaa'\n" +
            "\t\t\t\t\tDb\t0xA\n" +
            "\tResultEnd\t\tequ\t$-Result   \n" +
            "\n" +
            "\tnum\ttimes\t6\tdb\t'ABCDEF'\n" +
            "\tnumEnd\t\t\tequ\t$-num\n";

    static final String bssDef = "section\t.bss\n" +
            "\tTempChar\tRESB\t1\t\n" +
            "\ttestchar\tRESB\t1\t\n" +
            "\tReadInt\t\tRESW\t1\n" +
            "\ttempint\t\tRESW\t1\n" +
            "\tnegflag\t\tRESB\t1\n";

    static final String txtDef = "section\t.text\t\n" +
            "\tglobal main \n" +
            "main:\tnop\n";

    public CodeGenerator() {
        nextLabel = 0;
        fixUpStack = new NodeStack<>();
//        endStack = new NodeStack<>();

        try {
            dataWriter = new BufferedWriter(new FileWriter("assets/dataSegment.txt"));
            codeWriter = new BufferedWriter(new FileWriter("assets/codeSegment.txt"));
            bssWriter = new BufferedWriter(new FileWriter("assets/bssSegment.txt"));
            dataWriter.append(dataDef + "\n");
            bssWriter.append(bssDef + "\n");
            codeWriter.append(txtDef + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getNextLabel() {
        return ("L" + ++nextLabel);
    }

    public void appendTableData(Map<Object, Symbol> table) {
        try {
            for (Map.Entry<Object, Symbol> item : table.entrySet()) {
                switch (item.getValue().segment) {
                    case CS:
                        break;
                    case DS:
                        dataWriter.write("\t" + item.getValue().token + "\t\tDW\t");
                        dataWriter.write(item.getValue().value + "\t\n");
                        break;
                    case BSS:
                        bssWriter.write("\t" + item.getValue().token + "\t\t\tRESW\t1\n");
                        break;
                }
            }
            dataWriter.close();
            bssWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assemble() {
        try {
            codeWriter.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter("assets/assembly.asm"));
            BufferedReader dataReader = new BufferedReader(new FileReader("assets/dataSegment.txt"));
            BufferedReader bssReader = new BufferedReader(new FileReader("assets/bssSegment.txt"));
            BufferedReader codeReader = new BufferedReader(new FileReader("assets/codeSegment.txt"));

            String line;
            while ( (line = dataReader.readLine()) != null ) {
                writer.write(line);
                writer.newLine();
            }
            while ( (line = bssReader.readLine()) != null ) {
                writer.write(line);
                writer.newLine();
            }
            while ( (line = codeReader.readLine()) != null ) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            dataReader.close();
            bssReader.close();
            codeReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToCodeSegment(String code) {
        try {
           codeWriter.append(code);
           codeWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateCode(Classification classification, Symbol symbol) {

        Logger.getInstance().newLine();
        Logger.getInstance().printMessage(symbol.token);

        String[] fragments = symbol.token.split(" ");
        String op, arg1, arg2;
        String code = null;

        switch (classification) {
            case ASSIGN:
                arg1 = fragments[0];
                arg2 = fragments[2];
                code = "\tmov ax, [" +SymbolTable.getSymbol(arg2).token
                        + "]\n\tmov [" + SymbolTable.getSymbol(arg1).token + "], ax\n";
                Logger.getInstance().newLine();
                Logger.getInstance().printMessage(code);
                writeToCodeSegment(code);
                return "SS";
            case ADDOP:
                op = fragments[1];
                arg1 = fragments[0];
                arg2 = fragments[2];
                if (op.equals("+")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tadd ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [T1], ax\n";
                } else if (op.equals("-")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tsub ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [T1], ax\n";
                }
                Logger.getInstance().newLine();
                Logger.getInstance().printMessage(code);
                writeToCodeSegment(code);
                return "T1";
            case MOP:
                op = fragments[1];
                arg1 = fragments[0];
                arg2 = fragments[2];
                if (op.equals("*")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tmul ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [T1], ax\n";
                } else if (op.equals("/")) {
                    code = "\tmov dx, 0"
                            + "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tmov bx, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tdiv bx"
                            + "\n\tmov [T1], ax\n";
                }
                Logger.getInstance().newLine();
                Logger.getInstance().printMessage(code);
                writeToCodeSegment(code);
                return "T1";
            case RELOP:
                String label = getNextLabel();
                if (fragments[0].equals("ODD")) {
                    op = fragments[0];
                    arg1 = fragments[1];
                    arg2 = null;
                } else {
                    op = fragments[1];
                    arg1 = fragments[0];
                    arg2 = fragments[2];
                }

                if (op.equals("<")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tjge " + label + "\n";
                } else if (op.equals(">")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tjle " + label + "\n";
                } else if (op.equals("<=")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tjg " + label + "\n";
                } else if (op.equals(">=")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tjl " + label + "\n";
                } else if (op.equals("==")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tjne " + label + "\n";
                } else if (op.equals("!=")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tcmp ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tje " + label + "\n";
                } else if (op.equals("ODD")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tand ax, 1"
                            + "\n\tjnz " + label + "\n";
                }
                Logger.getInstance().newLine();
                Logger.getInstance().printMessage(code);
                fixUpStack.push(new Label(label, SymbolTable.getInstance().getNextAddress()));
                writeToCodeSegment(code);
                return "B_E";
            case IF_S:
                Label fix = fixUpStack.pop();
                code = "\t" + fix.label + ":\tnop\n";
                Logger.getInstance().newLine();
                Logger.getInstance().printMessage(code);
                writeToCodeSegment(code);
                return "IF_S";
        }
        return null;
    }
}





























