package main.src;

public class Token {

    public enum Classification {
        $mop, $int, $var, $addop, $assign, $relop, $negate,
        $lb, $rb, $comma, $semi, $period, $lp, $rp
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
