package main.src;

import main.enums.Classification;
import main.enums.Precedence;

public class Symbol {
    public enum Segment { DS, CS }

    public String token;
    public Classification classification;
    public Precedence precedence;
    public Object value;
    public Segment segment;
    public int address;

    public Symbol() {
        this.token = "";
    }

    public Symbol(String token, Classification classification, Precedence precedence) {
        this.token = token;
        this.classification = classification;
        this.precedence = precedence;
        this.value = token;
    }

    public Symbol(String token, Classification classification, Object value, Segment segment) {
        this.token = token;
        this.classification = classification;
        this.segment = segment;
        this.value = value;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}