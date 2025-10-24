package net.altofeather.ChASM;

import java.util.*;

import static net.altofeather.ChASM.KeywordImplementations.registerImplementation;

public class ExtendableCompiler {

    protected static ArrayList<char[]> compilerTokens = new ArrayList<>();
    protected static Map<char[], StackObject> extentions = new HashMap<>();
    protected static Map<char[], KeywordImplementations.StackEdition> operationMap = new HashMap<>();
    protected static ArrayList<Byte> compiledBytecode = new ArrayList<>();
    protected static char[] currentToken;
    protected static int tokenPointer;
    protected static char[] extendingToken;

    public ArrayList<char[]> gatherIdentifiers(ArrayList<char[]> identifiers, char[] program) {
        ArrayList<char[]> newTokens = new ArrayList<>();
        char[] newToken = new char[]{};

        for (int pIndex = 0; pIndex < program.length; pIndex++) {
            boolean matched = false;
            identifiers.sort((o1, o2) -> o2.length - o1.length); //sort by length in descending order. compilerTokens that are less likely to appear are the longest and should be checked first
            for (char[] token: identifiers) { //check every token
                int matches = 0;
                if (token.length - 1 + pIndex < program.length) { //check to see if token match is even within bounds of program before matching
                    int matchIndex = pIndex;
                    for (int tIndex = 0; tIndex < token.length; tIndex++) {
                        matchIndex = pIndex + tIndex;
                        if (token[tIndex] == program[matchIndex]) matches++;
                    }
                    if (matches == token.length) {
                        if (newToken.length != 0) newTokens.add(newToken);
                        newTokens.add(token);
                        matched = true;
                        pIndex = matchIndex;
                        newToken = new char[]{};
                        break;
                    }
                }
            }
            if (!matched) {
                newToken = Arrays.copyOf(newToken, newToken.length + 1);
                newToken[newToken.length - 1] = program[pIndex];
            }
        }
        newTokens.add(newToken);
        return newTokens;
    }

    public ExtendableCompiler(char[] program) {
        registerImplementation(";", () -> {});

        registerImplementation("EXTEND ", KeywordImplementations::_EXTEND);
        registerImplementation("INSERT NUMBER ", KeywordImplementations::_INSERT_NUMBER);
        registerImplementation("INSERT HEX ", KeywordImplementations::_INSERT_HEX);
        registerImplementation("INSERT UTF8 ", KeywordImplementations::_INSERT_UTF_8);
        registerImplementation("PRINT ", KeywordImplementations::_PRINT);

        ArrayList<char[]> realTokens = new ArrayList<>(operationMap.keySet());

        compilerTokens = gatherIdentifiers(realTokens, program);

        for (tokenPointer = 0; tokenPointer < compilerTokens.size() - 1; tokenPointer++) {
            char[] token = compilerTokens.get(tokenPointer);
            operationMap.get(token).cb();
        }

    }

    public ArrayList<Byte> runCompiler(String program) {

        String[] tokenizedProgram = program.split(" ");

        for (String token : tokenizedProgram) {
            StackObject copiedStackProgram = extentions.get(token.toCharArray());
            while (copiedStackProgram.runOperation());
        }

        return compiledBytecode;

    }

}
