import analyze.AltAnalyzer;
import analyze.Analyzer;
import data.ExecutionTrace;
import data.ExecutionTracer;
import parser.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new RuntimeException("Please provide the path to the outputs");
        }

        // Process the ExecutionTrace for each test result
        var traces = new HashSet<ExecutionTrace>();
        var folder = new File(args[0]);
        for (var file : folder.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }

            // Get the status and name of the test
            boolean passed;
            if (file.getName().endsWith("_passed")) {
                passed = true;
            } else if (file.getName().endsWith("_failed")) {
                passed = false;
            } else {
                throw new RuntimeException(String.format("Unexpected file '%s'", file.getName()));
            }
            var testName = file.getName().substring(0, file.getName().length() - 7);

            // Run the results through the parser and store for later
            var lines = getFileLineIterator(file);
            var tracer = new ExecutionTracer();
            new Parser(tracer).parseLines(lines);
            traces.add(new ExecutionTrace(testName, passed, tracer.collapse()));
        }

        //printLikelihoods(Analyzer.analyze(traces));
        AltAnalyzer.analyze(traces);
    }

    private static void printLikelihoods(Map<String, Double> likelihoods) {
        System.out.println("Likelihoods of containing bugs:");

        for (var entry : likelihoods.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
    }

    private static Iterable<String> getFileLineIterator(File f) throws FileNotFoundException {
        var s = new Scanner(new BufferedReader(new FileReader(f)));
        var lines = new ArrayList<String>();

        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }

        return lines;
    }
}
