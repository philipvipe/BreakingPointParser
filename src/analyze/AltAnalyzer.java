package analyze;

import data.ExecutionTrace;
import data.MethodExecution;

import java.util.Set;
import java.util.stream.Collectors;

import static data.MethodExecution.TakenStatus.*;

public class AltAnalyzer {
    public static void analyze(Set<ExecutionTrace> executions) {
        var passingTraces = executions.stream()
                .filter(ExecutionTrace::isPassing)
                .collect(Collectors.toSet());

        var failingTrace = executions.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No failing trace to analyze"));

        Set<MethodExecution> passingExecutions =
                passingTraces.stream()
                        .map(ExecutionTrace::getMethodExecutions)
                        .flatMap(Set::stream)
                        .collect(Collectors.groupingBy(MethodExecution::getMethodName))
                        .values().stream()
                        .map(ms -> ms.stream().reduce(MethodExecution::join).orElseThrow())
                        .collect(Collectors.toSet());


        for (var fme : failingTrace.getMethodExecutions()) {
            var oPassingMethodExecution = passingExecutions.stream()
                    .filter(pme -> pme.getName().equals(fme.getName()))
                    .findFirst();

            if (!oPassingMethodExecution.isPresent()) {
                System.out.println(fme.getName() + " is unique to failing"); // Handled above
            } else {
                var pme = oPassingMethodExecution.get();

                for (int i = 0; i < Math.min(pme.numberOfBranches(), fme.numberOfBranches()); i++) {
                    var fb = fme.branchStatus(i);
                    var pb = pme.branchStatus(i);

                    if (pb == UNSEEN && fb != UNSEEN) {
                        System.out.println("Only failing ever sees branch " + i + " in " + fme.getMethodName());
                    }

                    switch (fb) {
                        case UNSEEN: break;
                        case TAKEN:
                            if (pb == NOT_TAKEN) {
                                System.out.println("Failing does take, but passing does not on " + fme.getMethodName() + " branch number " + i);
                            }
                            break;
                        case NOT_TAKEN:
                            if (pb == TAKEN) {
                                System.out.println("Failing does not take, but passing does on " + fme.getMethodName() + " branch number " + i);
                            }
                            break;
                        case BOTH:
                            if (pb != BOTH) {
                                System.out.println("Failing takes both, but passing only takes one on " + fme.getMethodName() + " branch number " + i);
                            }
                            break;
                    }
                }
            }
        }
    }
}
