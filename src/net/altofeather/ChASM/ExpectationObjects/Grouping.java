package net.altofeather.ChASM.ExpectationObjects;

public class Grouping extends Expectation {

    //we don't imply any extensionals here because a grouping could imply multiple extensionals
    //our target extensional is defined in check()
    public Grouping(char[] name, boolean gathered) {
        super(name, gathered);
    }

    @Override
    public boolean check(char[] programToken) throws Exception {
        return false;
    }

}
