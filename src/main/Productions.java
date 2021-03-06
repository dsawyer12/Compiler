package main;

import java.util.HashMap;

import main.enums.Classification;
import static main.enums.Classification.*;

public class Productions {

    public final HashMap<String, Classification> reductionMap;

    // 'productions' are the reduction mappings when a handle is found.
    public Productions() {
        reductionMap = new HashMap<>();
        // Reduction for G
        reductionMap.put("$ PGM $", PGM);
        // Reduction for PGM
        reductionMap.put("CLASS ID BLOCK", PGM);
        // Reduction for BLOCK
        reductionMap.put("LB D X PS RB", BLOCK);
        reductionMap.put("LB X PS RB", BLOCK);
        reductionMap.put("LB X RB", BLOCK);
        // Reduction for D
        reductionMap.put("DP RS", D);
        // Reduction for RS
        reductionMap.put("R", RS);
        // Reduction for R
        reductionMap.put("R RI SEMI", R);
        reductionMap.put("RI SEMI", R);
        // Reduction for RI
        reductionMap.put("GET ID", RI);
        // Reduction for DP
        reductionMap.put("DP DS", DP);
        reductionMap.put("DS", DP);
        // Reduction for DS
        reductionMap.put("CD", DS);
        reductionMap.put("VD SEMI", DS);
        // Reduction for CD
        reductionMap.put("CONST SS SEMI", CD);
        // Reduction for VD
        reductionMap.put("VAR ID", VD);
        // Reduction for X
        reductionMap.put("S", X);
        // Reduction for S
        reductionMap.put("SL", S);
        reductionMap.put("S Y", S);
        reductionMap.put("Y", S);
        // Reduction for Y
        reductionMap.put("IF_S", Y);
        reductionMap.put("WHILE_S", Y);
        // Reduction for PS
        reductionMap.put("P SEMI", PS);
        // Reduction for P
        reductionMap.put("PRINT ID", P);
        // Reduction for SL
        reductionMap.put("S SS SEMI", SL);
        reductionMap.put("SS SEMI", SL);
        // Reduction for SS
        reductionMap.put("ID ASSIGN E", SS);
        // Reduction for IF_STMT
        reductionMap.put("IF B_E THEN BLOCK", IF_S);
        // Reduction for WHILE_STMT
        reductionMap.put("WHILE B_E DO BLOCK", WHILE_S);
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














