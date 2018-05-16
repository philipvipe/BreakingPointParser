package data;

import java.util.*;
import java.util.stream.Collectors;

public class ExecutionTracer implements IExecutionTracer {
    private Stack<MethodExecution> liveMethodExecutions;
    private List<MethodExecution> completedMethodExecutions;

    public ExecutionTracer() {
        this.completedMethodExecutions = new ArrayList<>();
        this.liveMethodExecutions = new Stack<>();

        this.liveMethodExecutions.push(new MethodExecution("dummy_main"));
    }

    @Override
    public void onCall(String callee) {
        liveMethodExecutions.push(new MethodExecution(callee));
    }

    @Override
    public void onReturn() {
        Optional.ofNullable(liveMethodExecutions.pop())
                .ifPresentOrElse(
                        p -> completedMethodExecutions.add(0, p),
                        () -> {
                            throw new RuntimeException("Attempted return on empty stack");
                        });
    }

    @Override
    public void onBranch(int branchNum, boolean taken) {
        Optional.ofNullable(liveMethodExecutions.pop())
                .ifPresentOrElse(
                        p -> liveMethodExecutions.push(p.withBranch(branchNum, taken)),
                        () -> {
                            throw new RuntimeException("Attempted branch on empty stack");
                        });
    }

    @Override
    public Set<MethodExecution> collapse() {
        onReturn(); // Return from the dummy main

        return completedMethodExecutions.stream()
                .collect(Collectors.groupingBy(MethodExecution::getMethodName))
                .values().stream()
                .map(ms -> ms.stream().reduce(MethodExecution::join).orElseThrow())
                .collect(Collectors.toSet());

    }
}
