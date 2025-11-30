package net.altofeather.ChASM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StackObject {

    StackObject parent;
    StackObject child;

    public char[] token;
    public String operationName;

    public boolean complete = false;

    public boolean abs = false; //is abstract
    public boolean grouped = false; //is grouped
    public ArrayList<StackObject> enforcements = new ArrayList<>();

    static class GatheredToken {

        boolean self = false;

        String tokenName;
        char[] gatheredToken;

        GatheredToken(String tokenName, char[] gatheredToken) {

            this.tokenName = tokenName;
            this.gatheredToken = gatheredToken;

        }

    }

    public ArrayList<GatheredToken> gatheredTokens = new ArrayList<>();
    public int selfIndex = 0;

    public interface CB {
        boolean cb();
    }

    CB function;

    public StackObject(CB operation, char[] token, String operationName) {
        this.token = token;
        this.function = operation;
        this.operationName = operationName;
    }

    public void unfreeze() {
        this.complete = false;
        if (this.parent != null) this.parent.unfreeze();
    }

    public void pushStack(CB operation, char[] token, String operationName) {

        if (this.child != null) this.child.pushStack(operation, token, operationName);

        else {
            this.child = new StackObject(operation, token, operationName);
            this.child.parent = this;
        }
    }

    public void pushStack(StackObject stackObject) {

        if (this.child != null) this.child.pushStack(stackObject);

        else {
            this.child = stackObject;
            this.child.parent = this;
        }
    }

    public boolean extentionStatus() {
        if (this.complete && this.child != null) this.child.extentionStatus();
        return this.complete;
    }

    public StackObject getEnd() {
        if (this.child == null) return this;
        return this.child.getEnd();
    }

    public boolean runOperation() {

        if (!this.complete) {
            this.complete = this.function.cb();
            return false;
        }

        if (this.child == null) {
            unfreeze();
            return true;
        }

        return this.child.runOperation();

    }

}
