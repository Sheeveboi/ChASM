import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static net.altosheeve.magic_lantern.client.Tuba.TubaMachine.ChASM.KeywordImplementations.applyImplementation;

public class ExtendableCompiler {
    protected interface CB {
        void cb();
    }
    protected static ArrayList<char[]> compilerTokens = new ArrayList<>();
    protected static Map<String, StackElement> compilerStack = new HashMap<>();
    protected static ArrayList<String> compilerKeywords = new ArrayList<>();
    protected StackElement currentStackElement;
    protected static ArrayList<Byte> compiledBytecode = new ArrayList<>();
    protected static char[] currentToken;
    protected static int tokenPointer;
    protected static char[] extendingToken;

    public ArrayList<char[]> tokenize(ArrayList<char[]> identifiers, char[] program) {
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
    public void parseToken(char[] token) {
        String sToken = new String(token);
        if (compilerStack.containsKey(sToken)) {
            compilerStack.get(sToken).cb.CB();
        }
        else System.out.println("UNEXPECTED TOKEN");
    }
    public void parseImplementation(ArrayList<char[]> implementationTokens) {

    }
    public ArrayList<Byte> compileImplementation(char[] implementation) {

        ArrayList<char[]> separators = new ArrayList<>();
        separators.add(new char[]{' '});

        ArrayList<char[]> implementationTokens = tokenize(separators, implementation);
        parseImplementation(implementationTokens);

        return compiledBytecode;
    }
    public ExtendableCompiler(char[] program) {

        applyImplementation(" ", () -> {});
        applyImplementation(";", () -> {});

        applyImplementation("EXTEND ", KeywordImplementations::_EXTEND);
        applyImplementation("INSERT ", KeywordImplementations::_INSERT);
        applyImplementation("PRINT ", KeywordImplementations::_PRINT);

        ArrayList<char[]> realTokens = new ArrayList<>();
        for (String t: compilerStack.keySet()) {
            realTokens.add(t.toCharArray());
        }

        compilerTokens = tokenize(realTokens, program);

        for (tokenPointer = 0; tokenPointer < compilerTokens.size() - 1; tokenPointer++) {
            char[] token = compilerTokens.get(tokenPointer);
            parseToken(token);
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        File extention = new File("C:\\Users\\Alto Wisdom\\Desktop\\Magic Lantern\\src\\main\\java\\net\\altosheeve\\magic_lantern\\client\\Tuba\\TubaMachine\\extention.txt");
        Scanner extentionReader = new Scanner(extention);
        StringBuilder extentionData = new StringBuilder();
        while (extentionReader.hasNextLine()) extentionData.append(extentionReader.nextLine());
        extentionReader.close();

        ExtendableCompiler e = new ExtendableCompiler(extentionData.toString().toCharArray());

        File program = new File("C:\\Users\\Alto Wisdom\\Desktop\\Magic Lantern\\src\\main\\java\\net\\altosheeve\\magic_lantern\\client\\Tuba\\TubaMachine\\program.txt");
        Scanner programReader = new Scanner(program);
        StringBuilder programData = new StringBuilder();
        while (programReader.hasNextLine()) programData.append(programReader.nextLine());
        programReader.close();

        System.out.println(e.compileImplementation(programData.toString().toCharArray()).toString());
    }
}
