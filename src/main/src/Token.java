package main.src;

public class Token {

    public enum Classification {
        MOP, INT, ID, ADDOP, ASSIGN, RELOP, NEGATE, LB,
        RB, COMMA, SEMI, PERIOD, LP, RP, CONST, IF, VAR,
        THEN, PROC, WHILE, CALL, DO, ODD, CLASS
    }

    private String value;
    private Classification classification;

    public Token(String value, Classification classification) {
        this.value = value;
        this.classification = classification;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }
}
