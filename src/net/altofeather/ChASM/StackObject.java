package net.altofeather.ChASM;

public class StackObject {

    StackObject parent;
    StackObject child;

    char[] token;

    public interface CB {
        boolean cb();
    }

    CB function;

    public StackObject(StackObject parent, CB operation, char[] token) {
        this.parent = parent;
        this.parent.child = this;
        this.token = token;
    }

    public void breakStack() {
        this.parent.child = null;
    }

    public void pushStack(StackObject stackObject) {
        if (this.child != null) this.child.pushStack(stackObject);
        else this.child = stackObject;
    }

    public StackObject getEnd() {
        if (this.child == null) return this;
        return this.child.getEnd();
    }

    public boolean runOperation() {

        if (this.parent != null) this.parent.runOperation();

        boolean out = this.function.cb();
        if (out) breakStack();
        return out;

    }

}
