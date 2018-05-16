package data;

import java.util.Set;

public class ExecutionTrace {
    private final boolean passing;
    private final Set<MethodExecution> executions;

    public ExecutionTrace(boolean passing, Set<MethodExecution> executions) {
        this.passing = passing;
        this.executions = executions;
    }

    public boolean isPassing() {
        return passing;
    }

    public Set<MethodExecution> getMethodExecutions() {
        return this.executions;
    }
}
