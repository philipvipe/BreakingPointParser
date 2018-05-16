package parser;

import java.io.*;
import java.util.HashMap;
import java.util.Stack;

public class Parser {

	private Stack<String> methods = new Stack<String>(); // all the methods visited
	private HashMap<String, Stack<String>> methodToMethods = new HashMap<String, Stack<String>>(); // maps a method to methods that called it
	private Stack<String> methodsAndConditionals = new Stack<String>();

	public static void main(String[] args) {
		String filename = args[0];	// filename of breaking point output we want to parse
		Parser parser = new Parser();
		parser.readFile(filename);
	}

	public void readFile(String filename){
		System.out.println("Parsing through " + filename);
		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				parseLine(line);
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String m: methodsAndConditionals){
			System.out.println(m);
		}
	}

	public void parseLine(String full_line){

		//System.out.println("Parsing..." + full_line);

		String line = full_line.substring(full_line.indexOf(":") + 2);
		String arrow = line.substring(0,3);

		// conditional
		if(arrow.contains("<") && arrow.contains(">")){

			line = line.substring(4);
			methodsAndConditionals.add(line);
		}
		// a method is calling another method
		else if(arrow.contains(">")){

			line = line.substring(4);
			int space_index = line.indexOf(" ");
			String callerMethod = line.substring(0, space_index);
			String calleeMethod = line.substring(space_index + 1);
			
			Stack<String> q1;
			if(methodToMethods.containsKey(calleeMethod)){
				q1 = methodToMethods.get(calleeMethod);
			}
			else{
				q1 = new Stack<String>();
			}
			q1.push(callerMethod);
			methodToMethods.put(calleeMethod, q1);
			if(methods.isEmpty()){
				methods.add(callerMethod);
				methodsAndConditionals.add(callerMethod);
			}
			if(!methods.peek().equals(calleeMethod)){
				methods.add(calleeMethod);
				methodsAndConditionals.add(calleeMethod);
			}
		}
		else if(arrow.contains("<")){
			line = line.substring(4);
			
			if(methodToMethods.containsKey(line)){
				String callerMethod = methodToMethods.get(line).pop();
				if(!methods.peek().equals(callerMethod)){
					methods.add(callerMethod);
					methodsAndConditionals.add(callerMethod);
				}
			}
		}
		else if(line.contains("Did not branch!")){
			methodsAndConditionals.pop();
		}
	}

}
