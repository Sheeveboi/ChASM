package net.altofeather.ChASM;

import java.util.Arrays;

public class KeywordImplementations extends ExtendableCompiler {
    public KeywordImplementations(char[] program) {
        super(program);
    }

    public interface StackEdition {
        void cb();
    }

    protected static void _EXTEND() {

        tokenPointer++;

        currentToken = compilerTokens.get(tokenPointer);
        extendingToken = currentToken;

        extentions.put(currentToken, new StackObject(null, () -> true, currentToken));
    }

    protected static void _INSERT_NUMBER() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = extentions.get(extendingToken);

        currentStackObject.pushStack(new StackObject(currentStackObject, () -> {
            compiledBytecode.add((byte) Integer.parseInt(new String(currentToken)));
            return true;
        }, extendingToken));

    }

    protected static void _INSERT_HEX() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = extentions.get(extendingToken);

        currentStackObject.pushStack(new StackObject(currentStackObject, () -> {
            compiledBytecode.add((byte) Integer.parseInt(new String(currentToken), 16));
            return true;
        }, extendingToken));


    }

    protected static void _INSERT_UTF_8() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = extentions.get(extendingToken);

        currentStackObject.pushStack(new StackObject(currentStackObject, () -> {
            for (char c : currentToken) compiledBytecode.add((byte) c);
            return true;
        }, extendingToken));


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
