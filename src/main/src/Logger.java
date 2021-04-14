package main.src;

import main.enums.Classification;

import java.util.ArrayList;
import java.util.Stack;

public class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private static Logger logger = null;

    public static synchronized Logger getInstance() {
        if(logger == null)
            logger = new Logger();
        return logger;
    }

    public void print(Object obj) {
        System.out.println(ANSI_PURPLE + obj.toString() + ANSI_RESET);
    }

    public void printMeta(Object obj) {
        System.out.print(ANSI_CYAN + obj.toString() + ANSI_RESET);
    }

    public void printProgress(ArrayList<String> arr, Classification classification) {
        System.out.println();
        for (String s : arr)
            System.out.print(ANSI_CYAN + s + ANSI_RESET);
        System.out.print(ANSI_CYAN + classification + ANSI_RESET);
    }

    public void printHandle(ArrayList<String> arr, Classification classification) {
        ArrayList<String> al = new ArrayList<>(arr);
        Stack<String> temp = new Stack<>();
        int i = al.size() - 1;
        String s;

        do {
            s = al.remove(i);
            temp.push(s);
            i = al.size() - 1;
        } while (!al.get(i).equals(" < "));

        i = al.size() - 1;
        s = al.remove(i);
        temp.push(s);

        System.out.println();
        for (String str : al)
            System.out.print(ANSI_CYAN + str + ANSI_RESET);
        while (!temp.isEmpty())
            System.out.print(ANSI_YELLOW + temp.pop() + ANSI_RESET);
        System.out.print(ANSI_CYAN + classification + ANSI_RESET);
    }

    public void printMessage(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    public void printError(String error) {
        System.out.println(ANSI_RED + error + ANSI_RESET);
    }

    public void printWarning(String warning) {
        System.out.println(ANSI_YELLOW + warning + ANSI_RESET);
    }


    public void printException(String exception) {
        System.out.println(ANSI_BLUE + exception + ANSI_RESET);
    }

    public void newLine() {
        System.out.println("\n");
    }
}
