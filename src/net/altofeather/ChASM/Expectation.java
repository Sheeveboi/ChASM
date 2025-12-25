package net.altofeather.ChASM;

public class Expectation {

    public enum ExpectationType {
        EXTENSIONAL,
        ABSTRACT_EXTENSIONAL,
        EXTENSIONAL_GROUP
    }

    ExpectationType type;
    char[] name;

    public Expectation(ExpectationType type, char[] name) {
        this.type = type;
        this.name = name;
    }

}
