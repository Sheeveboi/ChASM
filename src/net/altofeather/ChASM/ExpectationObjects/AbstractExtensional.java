package net.altofeather.ChASM.ExpectationObjects;

import net.altofeather.ChASM.StackObject;

import java.util.regex.Matcher;

public class AbstractExtensional extends Expectation {

    public AbstractExtensional(char[] name, boolean gathered) {
        super(name, gathered);
        this.extensional = ExtendableCompiler.getAbstractExtension(name);
    }

    @Override
    public boolean check(char[] programToken) throws Exception {

        boolean out = false;

        if (this.extensional.syntax == null) out = true;
        else {
            Matcher matcher = this.extensional.syntax.matcher(new String(programToken));
            out = matcher.find();
        }

        if (out && !gathered) this.extensional.runOperation();
        return out;

    }

}
