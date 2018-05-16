package data;

public interface IExecutionTracer {
    public void onCall(String callee);
    public boolean onReturn(String callee);
    public void onBranch(int branchNum, boolean taken);
}
