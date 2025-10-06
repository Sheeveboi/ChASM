import java.util.Arrays;

public class KeywordImplementations extends ExtendableCompiler {
    public KeywordImplementations(char[] program) {
        super(program);
    }

    protected static void _EXTEND() {
        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);
        extendingToken = currentToken;
    }

    protected static void _INSERT() {
        tokenPointer++;
        currentToken = compilerTokens.get(tokenPointer);
        String sToken = new String(currentToken);
        int intValue = Integer.parseInt(sToken);
        byte b = (byte) intValue;
        compiledBytecode.add(b);
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

    protected static void applyImplementation(String token, StackElement.NodeCallback cb) {
        compilerStack.put(token,new StackElement(cb));
        compilerKeywords.add(token);
    }
}
