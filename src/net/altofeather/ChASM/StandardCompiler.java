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
    
    protected static void _EXPECT() throws Exception {
        
        ArrayList<char[]> expectationTokens = new ArrayList<>();

        for (tokenPointer++; tokenPointer < compilerTokens.size(); tokenPointer++) {

            char[] token = compilerTokens.get(tokenPointer);

            if (Arrays.equals(token, ";".toCharArray())) break;

            expectationTokens.add(token);

        }

        StackObject currentStackObject = getCurrentStackObject();

        for (char[] expectation : expectationTokens) {

            if (getExtension(expectation) != null)
                currentStackObject.expectations.add(
                    new Expectation(Expectation.ExpectationType.EXTENSIONAL, expectation));

            else if (getAbstractExtension(expectation) != null)
                currentStackObject.expectations.add(
                    new Expectation(Expectation.ExpectationType.ABSTRACT_EXTENSIONAL, expectation));

            else if (getExtensionalGroup(expectation) != null)
                currentStackObject.expectations.add(
                    new Expectation(Expectation.ExpectationType.EXTENSIONAL_GROUP, expectation));

            else throw new Exception("Expectation not the name of any Extensional, Abstract Extensional, or Group");

        }

        currentStackObject.pushStack(() -> {

            for (int expectationIndex = 0; expectationIndex < currentStackObject.expectations.size(); expectationIndex++) {

                int fakeProgramPointer = programPointer + expectationIndex + 1;
                Expectation expectation = currentStackObject.expectations.get(expectationIndex);

                if (fakeProgramPointer >= tokenizedProgram.length) throw new Exception("Syntax Error: Unexpected end of program");

                char[] programToken = tokenizedProgram[fakeProgramPointer].toCharArray();

                System.out.println(STR."checking for \{new String(programToken)}");

                switch (expectation.type) {

                    case EXTENSIONAL -> {
                        if (!Arrays.equals(programToken, expectation.name)) throw new Exception(STR."Syntax Error: Unexpected token '\{new String(programToken)}'");
                    }

                    case ABSTRACT_EXTENSIONAL -> {

                    }

                    case EXTENSIONAL_GROUP -> {

                    }

                }

                System.out.println(expectationIndex);

            }

            return true;
        }, extendingToken, "EXPECT");
        
    }

    protected static void _EXTEND() throws Exception {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        extendingToken = currentToken;
        abstractExtension = false;

        if (extensions.containsKey(currentToken)) throw new Exception(STR."Extensional with name '\{new String(currentToken)}' already exists.");

        if (abstractExtensions.containsKey(currentToken)) throw new Exception(STR."Abstract Extensional with name '\{new String(currentToken)}' already exists.");

        extensions.put(currentToken, new StackObject(() -> true, extendingToken.clone(), "EXTEND"));

    }

    protected static void _ABSTRACT_EXTEND() throws Exception {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        extendingToken = currentToken;
        abstractExtension = true;

        if (extensions.containsKey(currentToken)) throw new Exception(STR."Extensional with name '\{new String(currentToken)}' already exists.");

        if (abstractExtensions.containsKey(currentToken)) throw new Exception(STR."Abstract Extensional with name '\{new String(currentToken)}' already exists.");

        abstractExtensions.put(currentToken, new StackObject(() -> true, extendingToken.clone(), "ABSTRACT EXTEND"));

    }

    protected static void _IMPLY() throws Exception {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = getCurrentStackObject();
        StackObject abstractStackObject = null;

        for (char[] key : abstractExtensions.keySet())
            if (Arrays.equals(key, currentToken))
                abstractStackObject = abstractExtensions.get(key);

        if (abstractStackObject == null) throw new Exception("Could not find abstract extensional.");

        for (char[] implication : currentStackObject.implications)
            if (Arrays.equals(implication, currentToken))
                throw new Exception("Abstract extension may only be implied once per extension");

        currentStackObject.pushStack(abstractStackObject);
        currentStackObject.implications.add(currentToken);

    }

    protected static void _AS() throws Exception {

        if (abstractExtension) throw new Exception("Cannot group abstract extensionals to other abstract extensionals.");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        if (!abstractExtensions.containsKey(currentToken)) throw new Exception("Could not find abstract extensional.");

        if (!abstractGroups.containsKey(currentToken)) abstractGroups.put(currentToken, new ArrayList<>());
        abstractGroups.get(currentToken).add(extendingToken);

    }

    protected static void _INSERT_FLOAT() {

        System.out.println(STR."\{new String(extendingToken)} will insert a float");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = getCurrentStackObject();

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

        System.out.println(STR."\{new String(extendingToken)} will insert an integer");

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject currentStackObject = getCurrentStackObject();

        String value = new String(currentToken);

        currentStackObject.pushStack(() -> {
            compiledBytecode.add((byte) Integer.parseInt(value));
            return true;
        }, extendingToken, "INSERT_INTEGER");

        //System.out.println(STR."stack size of current: \{currentStackObject.getStackSize()}");

    }

    protected static void _INSERT_HEX() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        StackObject stackObject = getCurrentStackObject();

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

        StackObject stackObject = getCurrentStackObject();

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

    protected static void _PROGRAM_EXTENSION_NAME() {

        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);

        programExtension = new String(currentToken);

    }

    protected static void registerImplementation(String token, StackEdition edition) {
        operationMap.put(token.toCharArray(), edition);
    }
}
