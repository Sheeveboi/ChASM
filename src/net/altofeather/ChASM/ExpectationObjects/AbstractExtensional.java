package net.altofeather.ChASM.ExpectationObjects;

import net.altofeather.ChASM.StackObject;

public class AbstractExtensional extends Expectation {

    public AbstractExtensional(char[] name, boolean gathered) {
        super(name, gathered);
    }

    @Override
    public boolean check(char[] programToken) throws Exception {
        return false;
    }

}
