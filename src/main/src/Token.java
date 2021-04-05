package main.src;

public class Token {

    public enum Classification {
        $mop, $int, $id, $addop, $assign, $relop, $negate, $lb,
        $rb, $comma, $semi, $period, $lp, $rp, $CONST, $IF, $VAR,
        $THEN, $PROC, $WHILE, $CALL, $DO, $ODD, $CLASS
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
