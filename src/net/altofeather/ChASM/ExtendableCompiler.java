package net.altofeather.ChASM;

import java.util.*;

public class ExtendableCompiler {

    protected static ArrayList<char[]> compilerTokens = new ArrayList<>();

    protected static Map<char[], StackObject> extensions = new HashMap<>(); //for front end extensionals
    protected static Map<char[], StackObject> abstractExtensions = new HashMap<>(); //for back end extensionals
    protected static Map<char[], ArrayList<char[]>> abstractGroups = new HashMap<>(); //groups back end extensionals to multiple front end extensionals

    protected static Map<char[], StandardCompiler.StackEdition> operationMap = new HashMap<>(); //maps front end extensionals to back end operations

    protected static ArrayList<Byte> compiledBytecode = new ArrayList<>(); //stores final result

    protected static String[] tokenizedProgram; //stores tokenized compile target

    //control variables
    protected static char[] currentToken;
    protected static int tokenPointer;
    protected static int programPointer;
    protected static char[] extendingToken;
    protected static boolean abstractExtension = false;

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

    public static StackObject getCurrentStackObject() {

        if (!abstractExtension) return extensions.get(extendingToken);
        return abstractExtensions.get(extendingToken);

    }

    public ExtendableCompiler(char[] program) throws Exception {

        StandardCompiler.registerImplementation(";", () -> {});
        StandardCompiler.registerImplementation(" ", () -> {});

        StandardCompiler.registerImplementation("ABSTRACT EXTEND ", StandardCompiler::_ABSTRACT_EXTEND);
        StandardCompiler.registerImplementation("EXTEND ", StandardCompiler::_EXTEND);
        StandardCompiler.registerImplementation("IMPLY ", StandardCompiler::_IMPLY);
        StandardCompiler.registerImplementation("INSERT FLOAT ", StandardCompiler::_INSERT_FLOAT);
        StandardCompiler.registerImplementation("INSERT INTEGER ", StandardCompiler::_INSERT_INTEGER);
        StandardCompiler.registerImplementation("INSERT HEX ", StandardCompiler::_INSERT_HEX);
        StandardCompiler.registerImplementation("PRINT ", StandardCompiler::_PRINT);

        ArrayList<char[]> realTokens = new ArrayList<>(operationMap.keySet());

        compilerTokens = gatherIdentifiers(realTokens, program);

        for (tokenPointer = 0; tokenPointer < compilerTokens.size() - 1; tokenPointer++) {
            char[] token = compilerTokens.get(tokenPointer);
            if (operationMap.containsKey(token)) operationMap.get(token).cb();
        }

    }

    public ArrayList<Byte> runCompiler(String program) {

        tokenizedProgram = program.split(" ");

        for (programPointer = 0; programPointer < tokenizedProgram.length; programPointer++) {

            String token = tokenizedProgram[programPointer];

            for (char[] key : extensions.keySet()) {

                if (Arrays.equals(key, token.toCharArray())){
                    StackObject copiedStackProgram = extensions.get(key);
                    while (!copiedStackProgram.runOperation());
                    break;
                }

            }

        }

        return compiledBytecode;

    }

}
