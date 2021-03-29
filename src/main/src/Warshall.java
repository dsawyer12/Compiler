package main.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Warshall {

    enum operators {$WHILE, $IF, $THEN, $ASSIGN, $PLUS, $EQUAL, $DO, $DELIM}

    /* The following matrix is the result of,
          B = [ {(0), (>=)}, {(<=)T, (0)} ]
    */
    static int[][] matrix = {
         //  1   2   3   4   5   6   7   8     9   10  11  12  13  14  15  16
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  1,  0},   // 1
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  1,  0,  0,  0,  0,  0},   // 2
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  1},   // 3
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  1,  0,  0,  0,  1,  0},   // 4
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  1,  1,  1,  0,  1,  1},   // 5
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  1},   // 6
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  1},   // 7
            {0,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  1},   // 8

            {0,  0,  0,  0,  0,  0,  0,  1,    0,  0,  0,  0,  0,  0,  0,  0},   // 9
            {0,  0,  1,  0,  0,  0,  1,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 10
            {0,  1,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 11
            {1,  1,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 12
            {1,  1,  0,  0,  0,  1,  0,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 13
            {0,  0,  1,  0,  0,  0,  1,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 14
            {1,  0,  0,  0,  0,  0,  0,  0,    0,  0,  0,  0,  0,  0,  0,  0},   // 15
            {0,  0,  0,  0,  0,  0,  0,  1,    0,  0,  0,  0,  0,  0,  0,  0}    // 16
    };

    public static void runClosure() {
        // Begin Warshall's Algorithm
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 1) {
                    for (int k = 0; k < matrix.length; k++) {
                        matrix[j][k] = (matrix[j][k] | matrix[i][k]);
                    }
                }
            }
        }
        // End Warshall's Algorithm

        // Find FIRST*
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == j)
                    matrix[i][j] = 1;
            }
        }
        // End FIRST*

        // Create resulting precedence function.
        String[][] pFunction = new String[3][9];

        pFunction[1][0] = "f";
        pFunction[2][0] = "g";

        // Add operator headers
        for (int i = 1; i < pFunction[0].length; i++)
            pFunction[0][i] = operators.values()[i-1].toString();

        // Add row 'f'
        int sum = 0, limit = matrix.length / 2;
        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                sum += matrix[i][j];
            pFunction[1][i + 1] = String.valueOf(sum);
            sum = 0;
        }

        // Add row 'g'
        int index = 1;
        for (int i = limit; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++)
                sum += matrix[i][j];
            pFunction[2][index++] = String.valueOf(sum);
            sum = 0;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("assets/pFunction-output.txt"));

            writer.write("B = \n");
            for (int[] ints : matrix) writer.write(Arrays.toString(ints) + "\n");

            writer.write("\n" + "(Precedence function for matrix B) = \n");
            for (String[] function : pFunction) writer.write(Arrays.toString(function) + "\n");

            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}





















