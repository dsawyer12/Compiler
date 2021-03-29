package main.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Exam2 {

    public static HashMap<Character, ArrayList<Character>> map = new HashMap<>();
    public static class Head {
        int row, column;

        public Head(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
    public static NodeStack<Head> last = new NodeStack<>();
    public static char[] nodes = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J'};
    public static int[][] mtx = {
            //   A   B   C   D   E   F   G   H   J
            {1,  1,  1,  2,  0,  0,  0,  0,  0}, // A
            {1,  1,  2,  1,  0,  0,  0,  0,  0}, // B
            {0,  0,  1,  1,  0,  0,  0,  0,  0}, // C
            {0,  0,  0,  1,  0,  0,  0,  0,  0}, // D
            {0,  0,  0,  0,  1,  0,  0,  0,  0}, // E
            {0,  0,  2,  2,  0,  1,  1,  1,  0}, // F
            {0,  0,  1,  2,  1,  1,  1,  2,  2}, // G
            {0,  0,  0,  0,  0,  1,  0,  1,  1}, // H
            {0,  0,  0,  0,  0,  0,  0,  0,  1}, // J
    };
    public static void q2() {
        int current;
        for (int i = 0; i < mtx.length; i++) {
            for (int j = 0; j < mtx.length; j++) {
                current = mtx[i][j];
                if (current == 1)
                    addNode(i, j);
                else if(current == 2) {
                    addNode(i, j);
                    last.push(new Exam2.Head(i, j));
                    int temp;

                    while (!last.isEmpty()) {
                        for (int k = 0; k < mtx[j].length; k++) {
                            temp = mtx[j][k];
                            if (temp == 1)
                                addNode(i, j);
                            else if(temp == 2) {
                                addNode(i, j);
                                last.push(new Exam2.Head(i, j));
                            }
                        }
                        last.pop();
                    }
                }
            }
        }
        print();
    }

    public static void addNode(int i, int j) {
        if (map.get(nodes[i]) != null) {
            if (!map.get(nodes[i]).contains(nodes[j]))
                map.get(nodes[i]).add(nodes[j]);
        } else {
            map.put(nodes[i], new ArrayList<>());
            map.get(nodes[i]).add(nodes[j]);
        }
    }

    public static void print() {
        for (Map.Entry<Character, ArrayList<Character>> entry : map.entrySet()) {
            System.out.print(entry.getKey() + " ---> ");
            List<Character> list = entry.getValue();
            for (int i = 0; i < list.size(); i++)
                System.out.print(list.get(i) + " | ");
            System.out.println();
        }
    }

    public static void q3() {
        int[][] FIRST = {
        //   E   a   :=  A   %   B   #   (   )   **
            {0,  1,  0,  0,  0,  0,  0,  0,  0,  0}, // E
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // a
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // :=
            {0,  1,  0,  1,  0,  0,  0,  0,  0,  0}, // A
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // %
            {0,  1,  0,  0,  0,  0,  0,  1,  0,  0}, // B
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // #
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // (
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // )
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // **
        };
        int[][] LAST = {
        //   E   a   :=  A   %   B   #   (   )   **
            {0,  0,  0,  1,  0,  0,  0,  0,  0,  0}, // E
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // a
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // :=
            {0,  1,  0,  0,  0,  1,  0,  0,  0,  0}, // A
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // %
            {0,  1,  0,  0,  0,  1,  0,  0,  1,  0}, // B
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // #
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // (
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // )
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // **
        };
        int[][] EQUAL = {    // (=)
        //   E   a   :=  A   %   B   #   (   )   **
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // E
            {0,  0,  1,  0,  0,  0,  0,  0,  0,  1}, // a
            {0,  0,  0,  1,  0,  0,  0,  0,  0,  0}, // :=
            {0,  0,  0,  0,  1,  0,  1,  0,  1,  0}, // A
            {0,  0,  0,  0,  0,  1,  0,  0,  0,  0}, // %
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // B
            {0,  0,  0,  0,  0,  1,  0,  0,  0,  0}, // #
            {0,  0,  0,  1,  0,  0,  0,  0,  0,  0}, // (
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, // )
            {0,  0,  0,  0,  0,  1,  0,  0,  0,  0}, // **
        };

        // Find FIRST+
        int[][] FIRST_PLUS = new int[10][10];

        for (int i = 0; i < FIRST.length; i++) {
            for (int j = 0; j < FIRST.length; j++) {
                if (FIRST[j][i] == 1) {
                    for (int k = 0; k < FIRST.length; k++)
                        FIRST_PLUS[j][k] = (FIRST[j][k] | FIRST[i][k]);
                }
            }
        }
        // End FIRST+

        // Find FIRST*
        int[][] FIRST_STAR = FIRST_PLUS;
        for (int i = 0; i < FIRST_STAR.length; i++) {
            for (int j = 0; j < FIRST_STAR.length; j++) {
                if (i == j)
                    FIRST_STAR[i][j] = 1;
            }
        }
        // End FIRST*

        // Find LAST+
        int[][] LAST_PLUS = new int[10][10];
        for (int i = 0; i < LAST.length; i++) {
            for (int j = 0; j < LAST.length; j++) {
                if (LAST[j][i] == 1) {
                    for (int k = 0; k < LAST.length; k++) {
                        LAST_PLUS[j][k] = (LAST[j][k] | LAST[i][k]);
                    }
                }
            }
        }
        // End LAST+

        // Find LAST+ Transpose
        int[][] LAST_PLUS_TRANSPOSE = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++)
                LAST_PLUS_TRANSPOSE[i][j] = LAST[j][i];
        }
        // End LAST+ Transpose

        // BMR for (<)
        int[][] YIELDS = mtxMultiply(EQUAL, FIRST_PLUS);
        // BMR for (>)
        int[][] temp = mtxMultiply(LAST_PLUS_TRANSPOSE, EQUAL);
        int[][] TAKES = mtxMultiply(temp, FIRST_STAR);

        // Resulting complete precedence matrix
        String[][] results = new String[10][10];
        for (String[] s : results) Arrays.fill(s, "");

        // Add EQUALS to results
        appendToResults(EQUAL, results, "=");
        // Add YIELDS to results
        appendToResults(YIELDS, results, "<");
        // Add TAKES to results
        appendToResults(TAKES, results, ">");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("assets/precendenceMatrix.txt"));
            for (String[] s : results) {
                writer.write(Arrays.toString(s));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int[][] mtxMultiply(int[][] first, int[][] second) {
        int m = 10, p = 10, q = 10, sum = 0;
        int[][] result;
        result = new int[10][10];

        for ( int c = 0; c < m; c++) {
            for (int d = 0; d < q; d++) {
                for (int k = 0; k < p; k++)
                    sum = sum + first[c][k] * second[k][d];

                result[c][d] = sum;
                sum = 0;
            }
        }
        return result;
    }

    public static void appendToResults(int[][] nextMtx, String[][] resultMtx, String value) {
        for (int i = 0; i < nextMtx.length; i++) {
            for (int j = 0; j < nextMtx[i].length; j++) {
                if (nextMtx[i][j] == 1)
                    resultMtx[i][j] += value;
                else
                    resultMtx[i][j] += " ";
            }
        }
    }
}















