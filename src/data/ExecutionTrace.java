package data;

import java.util.Set;

public class ExecutionTrace {
    private final String qualifiedName;
    private final boolean passing;
    private final Set<MethodExecution> executions;

    public ExecutionTrace(String qualifiedName, boolean passing, Set<MethodExecution> executions) {
        this.qualifiedName = qualifiedName;
        this.passing = passing;
        this.executions = executions;
    }

    public String getName() {
        return this.qualifiedName;
    }

    public boolean isPassing() {
        return passing;
    }

    public Set<MethodExecution> getMethodExecutions() {
        return this.executions;
    }
}
