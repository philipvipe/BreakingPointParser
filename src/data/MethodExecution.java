package data;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodExecution {
    private final String qualifiedName;
    private int[] branches;

    private MethodExecution(String qualifiedName, int[] branches) {
        this.qualifiedName = qualifiedName;
        this.branches = branches;
    }

    public MethodExecution(String qualifiedName) {
        this(qualifiedName, new int[0]);
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

        int[] newBranches = Arrays.copyOf(withMore.branches, withMore.branches.length);

        for (int i = 0; i < withLess.branches.length; i++) {
            newBranches[i] += withLess.branches[i];
        }

        return new MethodExecution(this.qualifiedName, newBranches);
    }

    public MethodExecution withBranch(int branchNum, boolean taken) {
        int[] thisBranch = new int[branchNum];

        thisBranch[branchNum - 1] = 1;

        return this.join(new MethodExecution(this.qualifiedName, thisBranch));
    }

    public boolean wasBranchTaken(int branchNum) {
        return timesBranchTaken(branchNum) > 0;
    }

    public int timesBranchTaken(int branchNum) {
        if (branchNum >= this.branches.length) {
            return 0;
        }

        return this.branches[branchNum];
    }
}
