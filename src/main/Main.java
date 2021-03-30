package main;

import main.modules.Lexer;
import main.modules.SyntaxAnalyzer;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File program = new File("assets/program.txt");

        Lexer lexer = new Lexer();
        lexer.scan(program);
        SyntaxAnalyzer.analyze(new File("assets/symbolTable.txt"));
    }
}












