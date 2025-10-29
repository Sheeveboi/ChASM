package net.altofeather.ChASM;

import java.util.ArrayList;
import java.util.Arrays;

public class StandardCompiler extends ExtendableCompiler {

    public StandardCompiler(char[] program) throws Exception {
        super(program);
    }

    public interface StackEdition {
        void cb() throws Exception;
    }

    protected static void _EXTEND() {

        tokenPointer++;

        currentToken = compilerTokens.get(tokenPointer);
        extendingToken = currentToken;
        abstractExtension = false;

        extensions.put(currentToken, new StackObject(() -> true, extendingToken.clone(), "EXTEND"));

    }

    protected static void _ABSTRACT_EXTEND() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        extendingToken = currentToken;
        abstractExtension = true;

        abstractExtensions.put(currentToken, new StackObject(() -> true, extendingToken.clone(), "ABSTRACT EXTEND"));

    }

    protected static void _INSERT_FLOAT() {

        System.out.println(extendingToken + " will insert a float");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = extentions.get(extendingToken);

        String value = new String(currentToken);

        currentStackObject.pushStack(() -> {

            ArrayList<Byte> out = new ArrayList<>();

            //encode value
            int intBits = Float.floatToIntBits(Float.parseFloat(value));

            out.add((byte) (intBits >> 24));
            out.add((byte) (intBits >> 16));
            out.add((byte) (intBits >> 8));
            out.add((byte) (intBits));

            compiledBytecode.addAll(out);

            return true;
        }, extendingToken, "INSERT_NUMBER");

    }

    protected static void _INSERT_INTEGER() {

        System.out.println(extendingToken + " will insert an integer");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = extentions.get(extendingToken);

        String value = new String(currentToken);

        currentStackObject.pushStack(() -> {
            compiledBytecode.add((byte) Integer.parseInt(value));
            return true;
        }, extendingToken, "INSERT_NUMBER");

    }

    protected static void _INSERT_HEX() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject stackObject = extentions.get(extendingToken);

        String value = new String(currentToken);

        stackObject.pushStack(() -> {
            compiledBytecode.add((byte) Integer.parseInt(value, 16));
            return true;
        }, extendingToken, "INSERT_HEX");

    }

    protected static void _INSERT_UTF_8() {

        System.out.println("inserting utf8");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject stackObject = extentions.get(extendingToken);

        String value = new String(currentToken);

        stackObject.pushStack(() -> {
            for (char c : value.toCharArray()) compiledBytecode.add((byte) c);
            return true;
        }, extendingToken, "INSERT_UTF_8");


    }

    protected static void _PRINT() { //should only be used for debugging. does not actually compile anything
        String sToken = "";
        StringBuilder out = new StringBuilder();
        while (tokenPointer < compilerTokens.size() - 1) {
            tokenPointer++;
            currentToken = compilerTokens.get(tokenPointer);
            sToken = new String(currentToken);
            if (Arrays.equals(currentToken, ";".toCharArray())) break;
            out.append(sToken);
            out.append(" ");
        }
        System.out.println(out);
    }

    protected static void registerImplementation(String token, StackEdition edition) {
        operationMap.put(token.toCharArray(), edition);
    }
}
