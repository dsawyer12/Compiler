package main.src;

import main.enums.Classification;

import java.io.*;
import java.util.Map;

import static main.enums.Classification.WHILE;

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
    public NodeStack<Label> endStack;

    private static CodeGenerator codeGenerator = null;

    public static CodeGenerator getInstance() {
        if (codeGenerator == null)
            codeGenerator = new CodeGenerator();
        return codeGenerator;
    }

    // The below String definitions are Dr. Burris's code to concatenate
    static final String dataDef = "sys_exit\tequ\t1\n" +
            "sys_read\tequ\t3\n" +
            "sys_write\tequ\t4\n" +
            "stdin\t\tequ\t0\n" +
            "stdout\t\tequ\t1\n" +
            "stderr\t\tequ\t3\n" +
            "\n" +
            "section .data\n" +
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
            "\tResultEnd\t\tequ\t$-Result\n" +
            "\n" +
            "\tnum\ttimes\t6\tdb\t'ABCDEF'\n" +
            "\tnumEnd\t\t\tequ\t$-num\n";

    static final String bssDef = "section\t.bss\n" +
            "\tTempChar\tRESB\t1\t\n" +
            "\ttestchar\tRESB\t1\t\n" +
            "\tReadInt\t\tRESW\t1\n" +
            "\ttempint\t\tRESW\t1\n" +
            "\tnegflag\t\tRESB\t1\n";

    static final String txtDef = "section\t.text\n" +
            "\tglobal main\n" +
            "main:\n";

    static final String IORoutines = "\tmov eax, 4\n" +
            "\tmov ebx, 1\n" +
            "\tmov ecx, Result\n" +
            "\tmov edx, ResultEnd\n" +
            "\tint 80h\n" +
            "\n" +
            "fini:\n" +
            "\tmov eax, sys_exit\n" +
            "\txor ebx, ebx\n" +
            "\tint 80h\n" +
            "\n" +
            "PrintString:\n" +
            "\tpush    ax\n" +
            "\tpush    dx\n" +
            "\n" +
            "\tmov eax, 4\n" +
            "\tmov ebx, 1\n" +
            "\tmov ecx, userMsg\n" +
            "\tmov edx, lenUserMsg\n" +
            "\tint\t80h\n" +
            "\tpop     dx\n" +
            "\tpop     ax\n" +
            "\tret\n" +
            "\n" +
            "GetAnInteger:\n" +
            "\tmov eax,3\n" +
            "\tmov ebx,2\n" +
            "\tmov ecx,num\n" +
            "\tmov edx,6\n" +
            "\tint 0x80\n" +
            "\n" +
            "\tmov edx,eax\n" +
            "\tmov eax, 4\n" +
            "\tmov ebx, 1\n" +
            "\tmov ecx, num\n" +
            "\tint 80h\n" +
            "\n" +
            "ConvertStringToInteger:\n" +
            "\tmov ax, 0\n" +
            "\tmov [ReadInt], ax\n" +
            "\tmov ecx, num\n" +
            "\n" +
            "\tmov bx, 0\n" +
            "\tmov bl, byte [ecx]\n" +
            "\n" +
            "Next:\n" +
            "    sub bl, '0'\n" +
            "\tmov ax, [ReadInt]\n" +
            "\tmov dx, 10\n" +
            "\tmul dx\n" +
            "\tadd ax, bx\n" +
            "\tmov [ReadInt],  ax\n" +
            "\n" +
            "\tmov bx, 0\n" +
            "\tadd ecx, 1\n" +
            "\tmov bl, byte[ecx]\n" +
            "\n" +
            "\tcmp bl, 0xA\n" +
            "\tjne Next\n" +
            "\tret\n" +
            "\n" +
            "ConvertIntegerToString:\n" +
            "\tmov ebx, ResultValue + 4\n" +
            "\n" +
            "ConvertLoop:\n" +
            "\tsub dx, dx\n" +
            "\tmov cx, 10\n" +
            "\tdiv cx\n" +
            "\tadd dl, '0'\n" +
            "\tmov [ebx], dl\n" +
            "\tdec ebx\n" +
            "\tcmp ebx, ResultValue\n" +
            "\tjge ConvertLoop\n" +
            "\tret";

    public CodeGenerator() {
        nextLabel = 0;
        fixUpStack = new NodeStack<>();
        endStack = new NodeStack<>();

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
            writer.append(IORoutines);

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

    public String generateCode(Classification classification, Symbol symbol, Symbol prevSymbol) {
        // Separate the token into individual fragments for code generation
        String[] fragments = symbol.token.split(" ");
        String op, arg1, arg2;
        String code = null;

        switch (classification) {
            case ASSIGN:
                arg1 = fragments[0];
                arg2 = fragments[2];
                code = "\tmov ax, [" +SymbolTable.getSymbol(arg2).token
                        + "]\n\tmov [" + SymbolTable.getSymbol(arg1).token + "], ax\n";
                writeToCodeSegment(code);
                return "SS";
            case ADDOP:
                op = fragments[1];
                arg1 = fragments[0];
                arg2 = fragments[2];
                String temp = SymbolTable.getNextTemp(arg1, arg2).token;

                if (op.equals("+")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tadd ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [" + temp + "], ax\n";
                } else if (op.equals("-")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tsub ax, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [" + temp + "], ax\n";
                }
                writeToCodeSegment(code);
                return temp;
            case MOP:
                op = fragments[1];
                arg1 = fragments[0];
                arg2 = fragments[2];
                String tempp = SymbolTable.getNextTemp(arg1, arg2).token;

                if (op.equals("*")) {
                    code = "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tmul WORD ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tmov [" + tempp + "], ax\n";
                } else if (op.equals("/")) {
                    code = "\tmov dx, 0\n"
                            + "\tmov ax, ["
                            + SymbolTable.getSymbol(arg1).token
                            + "]\n\tmov bx, ["
                            + SymbolTable.getSymbol(arg2).token
                            + "]\n\tdiv bx"
                            + "\n\tmov [" + tempp + "], ax\n";
                }
                writeToCodeSegment(code);
                return tempp;
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
                if (prevSymbol.classification.equals(WHILE)) { // While loop code generation
                    StringBuilder codeBuilder = new StringBuilder();
                    Label wLabel = new Label(getNextLabel(), SymbolTable.getInstance().getNextAddress());
                    fixUpStack.push(wLabel);
                    codeBuilder.append("\t").append(wLabel.label).append(":\tnop\n").append(code);
                    writeToCodeSegment(codeBuilder.toString());
                    endStack.push(new Label(label, SymbolTable.getInstance().getNextAddress()));
                } else {
                    fixUpStack.push(new Label(label, SymbolTable.getInstance().getNextAddress()));
                    writeToCodeSegment(code);
                }
                return "B_E";
            case IF_S:
                Label iFix = fixUpStack.pop();
                code = "\t" + iFix.label + ":\tnop\n";
                writeToCodeSegment(code);
                return "IF_S";
            case WHILE_S:
                Label wFix = fixUpStack.pop();
                Label wEnd = endStack.pop();
                code = "\tjmp\t" + wFix.label + "\n"
                        + "\t" + wEnd.label + ":\tnop\n";
                writeToCodeSegment(code);
                return "WHILE_S";
            case RI:
                arg1 = fragments[1];
                code = "\tcall PrintString\n" +
                        "\tcall GetAnInteger\n" +
                        "\n\tmov ax, [ReadInt]\n" + "\tmov [" + SymbolTable.getSymbol(arg1).token + "], ax\n";
                writeToCodeSegment(code);
                return "RI";
            case P:
                arg1 = fragments[1];
                code = "\tmov ax, [" + SymbolTable.getSymbol(arg1).token
                        +  "]\n\tcall ConvertIntegerToString\n";
                writeToCodeSegment(code);
                return "P";
        }
        return null;
    }
}





























