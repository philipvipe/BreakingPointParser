package data;

import java.util.Arrays;

import static data.MethodExecution.TakenStatus.*;

public class MethodExecution {
    private final String qualifiedName;
    private TakenStatus[] branches;

    private MethodExecution(String qualifiedName, TakenStatus[] branches) {
        this.qualifiedName = qualifiedName;
        this.branches = branches;
    }

    public MethodExecution(String qualifiedName) {
        this(qualifiedName, new TakenStatus[0]);
    }

    public boolean isSameMethod(MethodExecution other) {
        return this.qualifiedName.equals(other.qualifiedName);
    }

    public String getMethodName() {
        return qualifiedName;
    }

    public MethodExecution join(MethodExecution other) {
        // Cannot join the results of two executions for different methods
        if (!this.isSameMethod(other)) {
            throw new RuntimeException("Cannot join methods with different names");
        }

        MethodExecution withMore, withLess;
        if (this.branches.length > other.branches.length) {
            withMore = this;
            withLess = other;
        } else {
            withMore = other;
            withLess = this;
        }

        TakenStatus[] newBranches = Arrays.copyOf(withMore.branches, withMore.branches.length);

        for (int i = 0; i < withLess.branches.length; i++) {
            if (newBranches[i] == UNSEEN) {
                newBranches[i] = withLess.branches[i];
            } else if ((newBranches[i] == NOT_TAKEN && withLess.branches[i] == TAKEN)
                    || (newBranches[i] == TAKEN && withLess.branches[i] == NOT_TAKEN)
                    || withLess.branches[i] == BOTH) {
                newBranches[i] = BOTH;
            }
        }

        return new MethodExecution(this.qualifiedName, newBranches);
    }

    public MethodExecution withBranch(int branchNum, boolean taken) {
        TakenStatus[] thisBranch = new TakenStatus[branchNum + 1];

        thisBranch[branchNum] = taken ? TAKEN : NOT_TAKEN;

        return this.join(new MethodExecution(this.qualifiedName, thisBranch));
    }

    public TakenStatus branchStatus(int branchNum) {
        if (branchNum >= branches.length) {
            return UNSEEN;
        }

        return branches[branchNum];
    }

    public String getName() {
        return this.qualifiedName;
    }

    public int numberOfBranches() {
        return this.branches.length;
    }

    public enum TakenStatus {
        UNSEEN, NOT_TAKEN, TAKEN, BOTH;
    }
}
