package main.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static main.src.Symbol.Segment.*;

public class CodeGenerator {
    public static BufferedWriter dataWriter, codeWriter;

    public static void generateCode(Map<Object, Symbol> table) {
        try {
            dataWriter = new BufferedWriter(new FileWriter("assets/dataSegment.txt"));
            codeWriter = new BufferedWriter(new FileWriter("assets/dataSegment.txt"));

            for (Map.Entry<Object, Symbol> item : table.entrySet()) {
                if (item.getValue().segment.equals(DS)) {
                    dataWriter.write(item.getValue().address + "\t");
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
