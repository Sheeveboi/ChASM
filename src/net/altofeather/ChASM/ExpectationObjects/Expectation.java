package net.altofeather.ChASM.ExpectationObjects;

import net.altofeather.ChASM.StackObject;

import static net.altofeather.ChASM.ExtendableCompiler.*;

public abstract class Expectation {

    public char[] name;
    StackObject extensional;
    public boolean gathered;

    public Expectation(char[] name,  boolean gathered) {
        this.name = name;
        this.gathered = gathered;
    }

    public static Expectation generateExpectation(char[] token, boolean gathered) {

        if      (getExtension(token)         != null ) return new Extensional(token, gathered);
        else if (getAbstractExtension(token) != null ) return new AbstractExtensional(token,  gathered);
        else if (getExtensionalGroup(token)  != null ) return new Grouping(token, gathered);

        return null;

    }

    public abstract boolean check(char[] programToken) throws Exception;

}
