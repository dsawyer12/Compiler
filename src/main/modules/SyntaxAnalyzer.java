package main.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SyntaxAnalyzer {

    public SyntaxAnalyzer() { }

    public static void analyze(File inFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}



















