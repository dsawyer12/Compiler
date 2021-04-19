package main.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static main.enums.Classification.INT;
import static main.src.Symbol.Segment.*;

public class CodeGenerator {
    public static BufferedWriter dataWriter, codeWriter;
    public static final String definitions = "sys_exit\tequ\t1\n" +
            "sys_read\tequ\t3\n" +
            "sys_write\tequ\t4\n" +
            "stdin\t\tequ\t0\n" +
            "stdout\t\tequ\t1\n" +
            "stderr\t\tequ\t3\n" +
            "\n" +
            "section .data";

    public static void generateCode(Map<Object, Symbol> table) {
        try {
            dataWriter = new BufferedWriter(new FileWriter("assets/dataSegment.txt"));
            codeWriter = new BufferedWriter(new FileWriter("assets/codeSegment.txt"));

            codeWriter.append(definitions);

            for (Map.Entry<Object, Symbol> item : table.entrySet()) {
                if (item.getValue().segment.equals(CS)) {

                } else if (item.getValue().segment.equals(DS)) {
//                    dataWriter.write(item.getValue().address + "\t");
                    if (item.getValue().classification.equals(INT))
                        dataWriter.write("LIT" + item.getValue().token + "\t");
                    else
                        dataWriter.write(item.getValue().token + "\t");
                    dataWriter.write("DW" + "\t");
                    if (item.getValue().value != null)
                        dataWriter.write(item.getValue().value + "\t\n");
                }

                switch (item.getValue().classification) {
                    case CD: {
                        break;
                    }
                }
            }

            dataWriter.close();
            codeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
