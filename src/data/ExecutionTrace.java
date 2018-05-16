package data;

import java.util.Set;

public class ExecutionTrace {
    private final String testName;
    private final boolean passing;
    private final Set<MethodExecution> executions;

    public ExecutionTrace(String testName, boolean passing, Set<MethodExecution> executions) {
        this.testName = testName;
        this.passing = passing;
        this.executions = executions;
    }

    public String getName() {
        return this.testName;
    }

    public boolean isPassing() {
        return passing;
    }

    public Set<MethodExecution> getMethodExecutions() {
        return this.executions;
    }
}
