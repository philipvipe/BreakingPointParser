package parser;

import data.IExecutionTracer;

public class Parser {
    // Contains branch num if we are not sure if it branched or not
    private int checkForBranch = -1;

    private final IExecutionTracer tracer;

    public Parser(IExecutionTracer tracer) {
        this.tracer = tracer;
    }

    public void parseLines(Iterable<String> lines) {
        for (String line : lines) {
            parseLineHelper(line);
        }
    }

    private void parseLineHelper(final String line) {
        // last line was a branch condition
        if (checkForBranch != -1) {
            var branchNum = checkForBranch;
            checkForBranch = -1;

            if (line.contains("Did not branch!")) {
                tracer.onBranch(branchNum, false);
                return;
            } else {
                // Do not return in this case, since the line contains logic
                tracer.onBranch(branchNum, true);
            }
        }

        var arrow = line.substring(0, 3);
        var post_arrow = line.substring(4);

        if (arrow.equals("<->")) {
            int index = post_arrow.indexOf("?");
            checkForBranch = Integer.parseInt(post_arrow.substring(index - 1, index)); // tell parser to check next line to see if it branches
        } else if (arrow.equals("-->")) {
            int space_index = post_arrow.indexOf(" ");
            String calleeMethod = post_arrow.substring(space_index + 1);

            tracer.onCall(calleeMethod);
        } else if (arrow.equals("<--")) {
            tracer.onReturn();
        }
    }


}
