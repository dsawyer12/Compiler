package main;

import main.enums.Classification;

import java.util.HashMap;

import static main.enums.Classification.*;

public class Productions {
    Classification s = VAR_LIST;

    public HashMap<String, Classification> reductionMap;

    public Productions() {
        reductionMap = new HashMap<>();
        // Reduction for PGM
        reductionMap.put("CLASS ID BLOCK", PGM);
        // Reduction for BLOCK
        reductionMap.put("LB X RB", BLOCK);
        // Reduction for X
        reductionMap.put("STMT", X);
        // Reduction for STMT
        reductionMap.put("STMT_LIST", STMT);
        reductionMap.put("STMT IF_STMT", STMT);
        // Reduction for STMT_LIST
        reductionMap.put("STMT SS SEMI", STMT_LIST);
        reductionMap.put("SS SEMI", STMT_LIST);
        // Reduction for SS
        reductionMap.put("ID ASSIGN E", SS);
        // Reduction for IF_STMT
        reductionMap.put("IF B_E THEN BLOCK", IF_STMT);
        // Reduction for B_E
        reductionMap.put("ODD E", B_E);
        reductionMap.put("E RELOP E", B_E);
        // Reduction for E
        reductionMap.put("EXP", E);
        // Reduction for EXP
        reductionMap.put("EXP ADDOP T", EXP);
        reductionMap.put("T", EXP);
        // Reduction for T
        reductionMap.put("TERM", T);
        // Reduction for TERM
        reductionMap.put("TERM MOP F", TERM);
        reductionMap.put("F", TERM);
        // Reduction for F
        reductionMap.put("ID", F);
        reductionMap.put("INT", F);
        reductionMap.put("LP E RP", F);
    }
}














