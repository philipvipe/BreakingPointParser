package data;

import java.util.Set;

public interface IExecutionTracer {
    public void onCall(String callee);
    public void onReturn();
    public void onBranch(int branchNum, boolean taken);
    public Set<MethodExecution> collapse();
}
