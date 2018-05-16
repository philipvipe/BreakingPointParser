How to run the analyzer:
    - Compile the program to test as a jar
    - Compile BreakingPoint (the Java agent) as a jar
    - Compile BreakingPointParser (this library) as a jar
    - Remove any previous output (the script will prompt for this if you haven't)
    - Run `ruby runner.rb <program> <agent> <parser> <test_names...>`, where each of the first three fields are the paths to those jar files and the test names are the possible arguments to main in order to run individual tests
