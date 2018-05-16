package analyze;

import data.ExecutionTrace;
import data.MethodExecution;
import java.util.*;

public class Analyzer {
    public static Map<String, Double> analyze(Set<ExecutionTrace> executions) {
        Map<String, Double> likelihoods = new HashMap<>();

        var executionOccurences = getExecutionOccurences(executions);
        for (var entry : executionOccurences.entrySet()) {
            String methodName = entry.getKey();
            var branchMap = entry.getValue();

            for (var branchEntry : branchMap.entrySet()) {
                int branchID = branchEntry.getKey();
                var traceSet = branchEntry.getValue();

                var branchOptions = MethodExecution.TakenStatus.values();
                for (var tookBranch : branchOptions){
                    int passCount = 0, failCount = 0;
                    for (ExecutionTrace trace : traceSet.get(tookBranch)) {

                        if (trace.isPassing()) {
                            passCount++;
                        } else {
                            failCount++;
                        }
                    }

                    double likelihood = 0;
                    if (traceSet.size() > 0) {
                        likelihood = failCount / (passCount + failCount);
                    }
                    String executionName = methodName + "_" + branchID + "_" + (tookBranch.toString());
                    likelihoods.put(executionName, likelihood);
                }
            }
        }
        return likelihoods;
    }

    public static Map<String, Map<Integer, Map<MethodExecution.TakenStatus, Set<ExecutionTrace>>>> getExecutionOccurences(Set<ExecutionTrace> executions){
        Map<String, Map<Integer, Map<MethodExecution.TakenStatus, Set<ExecutionTrace>>>> executionOcurrences = new HashMap<>();

        Map<String, Set<ExecutionTrace>> methodOccurences = getMethodOccurrences(executions);
        for (Map.Entry<String, Set<ExecutionTrace>> entry : methodOccurences.entrySet()) {
            String methodName = entry.getKey();
            Set<ExecutionTrace> traceSet = entry.getValue();

            var branchOccurrences = getMethodBranchOccurrences(traceSet, methodName);
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

    public static Map<Integer, Map<MethodExecution.TakenStatus, Set<ExecutionTrace>>> getMethodBranchOccurrences(
            Set<ExecutionTrace> executions,
            String methodName) {

        Map<Integer, Map<MethodExecution.TakenStatus, Set<ExecutionTrace>>> branchOccurrences = new HashMap<>();

        for (ExecutionTrace trace : executions) {
            MethodExecution method = getMethodExecution(trace, methodName);

            for (int i = 0; i < method.numberOfBranches(); i++) {
                MethodExecution.TakenStatus status = method.branchStatus(i);
                if (branchOccurrences.containsKey(i)) {
                    Set<ExecutionTrace> branchSet = branchOccurrences.get(i).get(status);
                    branchSet.add(trace);
                } else {
                    branchOccurrences.put(i, new HashMap<>());

                    var branchOptions = MethodExecution.TakenStatus.values();
                    for (var branchStatus : branchOptions) {
                        branchOccurrences.get(i).put(branchStatus, new HashSet<>());
                    }
                    Set<ExecutionTrace> branchSet = branchOccurrences.get(i).get(status);
                    branchOccurrences.get(i).put(status, branchSet);
                }

            }

            // special case: if no branches, invent a -1 branch
            int newBranchKey = -1;
            if (method.numberOfBranches() == 0) {
                if (branchOccurrences.containsKey(newBranchKey)) {
                    Set<ExecutionTrace> branchSet = branchOccurrences.get(newBranchKey).get(MethodExecution.TakenStatus.UNSEEN);
                    branchSet.add(trace);
                } else {
                    branchOccurrences.put(newBranchKey, new HashMap<>());

                    var branchOptions = MethodExecution.TakenStatus.values();
                    for (var branchStatus : branchOptions) {
                        branchOccurrences.get(newBranchKey).put(branchStatus, new HashSet<>());
                    }

                    Set<ExecutionTrace> branchSet = branchOccurrences.get(newBranchKey).get(MethodExecution.TakenStatus.UNSEEN);
                    branchSet.add(trace);
                    branchOccurrences.get(newBranchKey).put(MethodExecution.TakenStatus.UNSEEN, branchSet);
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
