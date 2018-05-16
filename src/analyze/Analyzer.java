package analyze;

import data.ExecutionTrace;
import data.MethodExecution;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Analyzer {
    public static void analyze(Set<ExecutionTrace> executions) {
        Map<String, Map<Integer, Set<ExecutionTrace>>> executionOccurences = getExecutionOccurences(executions);

        for (var entry : executionOccurences.entrySet()) {
            String methodName = entry.getKey();
            var branchMap = entry.getKey();

//            for (var branchEntry : branchMap) {
//
//            }
        }
    }

    public static Map<String, Map<Integer, Set<ExecutionTrace>>> getExecutionOccurences(Set<ExecutionTrace> executions){
        Map<String, Map<Integer, Set<ExecutionTrace>>> executionOcurrences = new HashMap<>();

        Map<String, Set<ExecutionTrace>> methodOccurences = getMethodOccurrences(executions);
        for (Map.Entry<String, Set<ExecutionTrace>> entry : methodOccurences.entrySet()) {
            String methodName = entry.getKey();
            Set<ExecutionTrace> traceSet = entry.getValue();

            Map<Integer, Set<ExecutionTrace>> branchOccurrences = getMethodBranchOccurrences(traceSet, methodName);
            executionOcurrences.put(methodName, branchOccurrences);

        }
        return executionOcurrences;
    }

    public static Map<String, Set<ExecutionTrace>> getMethodOccurrences(Set<ExecutionTrace> executions) {
        Map<String, Set<ExecutionTrace>> methodOccurrences = new HashMap<>();

        for (ExecutionTrace trace : executions) {
            for (MethodExecution method : trace.getMethodExecutions()) {

                if (methodOccurrences.containsKey(method.getName())) {
                    Set<ExecutionTrace> traceSet = methodOccurrences.get(method.getName());
                    traceSet.add(trace);
                } else {
                    Set<ExecutionTrace> traceSet = new HashSet<>();
                    traceSet.add(trace);
                    methodOccurrences.put(method.getName(), traceSet);
                }

            }
        }

        return methodOccurrences;
    }

    public static Map<Integer, Set<ExecutionTrace>> getMethodBranchOccurrences(
            Set<ExecutionTrace> executions,
            String methodName) {

        Map<Integer, Set<ExecutionTrace>> branchOccurrences = new HashMap<>();

        for (ExecutionTrace trace : executions) {
            MethodExecution method = getMethodExecution(trace, methodName);

            for (int i = 0; i < method.numberOfBranches(); i++) {
                if (method.wasBranchTaken(i)) {
                    if (branchOccurrences.containsKey(i)) {
                        Set<ExecutionTrace> branchSet = branchOccurrences.get(i);
                        branchSet.add(trace);
                    } else {
                        Set<ExecutionTrace> branchSet = new HashSet<>();
                        branchSet.add(trace);
                        branchOccurrences.put(i, branchSet);
                    }
                }
            }

            // special case: if no branches, invent a -1 branch
            int newBranchKey = -1;
            if (method.numberOfBranches() == 0) {
                if (branchOccurrences.containsKey(newBranchKey)) {
                    Set<ExecutionTrace> branchSet = branchOccurrences.get(newBranchKey);
                    branchSet.add(trace);
                } else {
                    Set<ExecutionTrace> branchSet = new HashSet<>();
                    branchSet.add(trace);
                    branchOccurrences. put(newBranchKey, branchSet);
                }
            }
        }

        return branchOccurrences;
    }

    public static MethodExecution getMethodExecution(ExecutionTrace trace, String methodName) {
        for (MethodExecution method : trace.getMethodExecutions()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
