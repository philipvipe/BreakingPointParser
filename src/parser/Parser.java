package parser;

import data.IExecutionTracer;

public class Parser {

	private int checkForBranch = -1; // contains branch num if we are not sure if it branched or not
	private IExecutionTracer tracer;
	
	public Parser(IExecutionTracer tracer){
		this.tracer = tracer;
	}

	public void parseLines(Iterable<String> lines){
		for(String line: lines){
			parseLineHelper(line);
		}
	}

	private void parseLineHelper(String full_line){

		String line = full_line.substring(full_line.indexOf(":") + 2);
		String arrow = line.substring(0,3);

		// last line was a branch condition
		if(checkForBranch != -1){
			if(line.contains("Did not branch!")){
				tracer.onBranch(checkForBranch, false);
			}
			else{
				tracer.onBranch(checkForBranch, true);
			}
			checkForBranch = -1; // revert back to initial flag
		}
		
		// conditional
		if(arrow.contains("<") && arrow.contains(">")){
			
			line = line.substring(4);
			int index = line.indexOf("?");
			checkForBranch = Integer.parseInt(line.substring(index - 1, index)); // tell parser to check next line to see if it branches			
		}
		// a method is calling another method
		else if(arrow.contains(">")){

			line = line.substring(4);
			int space_index = line.indexOf(" ");
			String calleeMethod = line.substring(space_index + 1);
			
			tracer.onCall(calleeMethod);
		}
		else if(arrow.contains("<")){
			tracer.onReturn();
		}
	}


}
