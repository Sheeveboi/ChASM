package net.altofeather.ChASM;

public class StackObject {

    StackObject parent;
    StackObject child;

    public char[] token;
    public String operationName;

    public boolean complete = false;

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
