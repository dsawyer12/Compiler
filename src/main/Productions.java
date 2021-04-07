package main;

import main.enums.Classification;

import java.util.HashMap;

import static main.enums.Classification.*;

public class Productions {
    Classification s = VAR_LIST;

    public HashMap<String, Classification> reductionMap;

    public Productions() {
        reductionMap = new HashMap<>();
        // PGM reduction
        reductionMap.put("CLASS ID LB BLOCK RB", PGM);
        // BLOCK reductions
        reductionMap.put("CONST C SEMI", BLOCK);
        reductionMap.put("VAR V SEMI", BLOCK);
        reductionMap.put("PROC ID LP RP LB BLOCK RB", BLOCK);
        reductionMap.put("STMT", BLOCK);
        reductionMap.put("CONST C SEMI VAR V SEMI", BLOCK);
        reductionMap.put("CONST C SEMI PROC ID LP RP LB BLOCK RB", BLOCK);
        reductionMap.put("CONST C SEMI STMT", BLOCK);
        reductionMap.put("VAR V SEMI PROC ID LP RP LB BLOCK RB", BLOCK);
        reductionMap.put("VAR V SEMI STMT", BLOCK);
        reductionMap.put("PROC ID LP RP LB BLOCK RB STMT", BLOCK);
        reductionMap.put("CONST C SEMI VAR V SEMI PROC ID LP RP LB BLOCK RB", BLOCK);
        reductionMap.put("CONST C SEMI VAR V SEMI STMT", BLOCK);
        reductionMap.put("VAR V SEMI PROC ID LP RP LB BLOCK RB STMT", BLOCK);
        reductionMap.put("CONST C SEMI PROC ID LP RP LB BLOCK RB STMT", BLOCK);
        // Reduction for C
        reductionMap.put("CONST_LIST", C);
        // Reduction for CONST_LIST
        reductionMap.put("CONST_LIST COMMA ID ASSIGN INT", CONST_LIST);
        reductionMap.put("ID ASSIGN INT", CONST_LIST);
        // Reduction for V
        reductionMap.put("VAR_LIST", V);
        // Reduction for VAR_LIST
        reductionMap.put("VAR_LIST COMMA ID", VAR_LIST);
//        reductionMap.put("ID", VAR_LIST);
        // Reduction for STMT
        reductionMap.put("SS SEMI", STMT);
        reductionMap.put("CALL_STMT", STMT);
        reductionMap.put("CMPD_STMT", STMT);
        reductionMap.put("IF_STMT", STMT);
        reductionMap.put("WHILE_STMT", STMT);
        reductionMap.put("S", STMT);
        // Reduction for SS
        reductionMap.put("ID ASSIGN E", SS);
        // Reduction for CALL_STMT
        reductionMap.put("CALL ID LP RP", CALL_STMT);
        // Reduction for CMPD_STMT
        reductionMap.put("LB S RB", CMPD_STMT);
        // Reduction for IF_STMT
        reductionMap.put("IF B_E THEN STMT", IF_STMT);
        // Reduction for WHILE_STMT
        reductionMap.put("WHILE B_E DO STMT", WHILE_STMT);
        // Reduction for S
        reductionMap.put("STMT_LIST", S);
        // Reduction for STMT_LIST
        reductionMap.put("STMT_LIST SEMI STMT", STMT_LIST);
        // Reduction for B_E
        reductionMap.put("ODD E", B_E);
        reductionMap.put("E RELOP E", B_E);
        // Reduction for E
        reductionMap.put("EXP", E);
        // Reduction for EXP
        reductionMap.put("E ADDOP T", EXP);
        reductionMap.put("T", EXP);
        // Reduction for T
        reductionMap.put("TERM", T);
        // Reduction for TERM
        reductionMap.put("TERM MOP FAC", TERM);
        reductionMap.put("FAC", TERM);
        // Reduction for FAC
        reductionMap.put("ID", FAC);
        reductionMap.put("INT", FAC);
        reductionMap.put("LP E RP", FAC);
    }

    public Classification getReduction(String values) {
        String tokens = values.trim();
        Classification c = null;
        while (reductionMap.containsKey(tokens)) {
            c = reductionMap.get(tokens);
            tokens = c.toString();
        }
        return c;
    }
}














