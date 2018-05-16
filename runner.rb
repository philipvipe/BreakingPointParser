require 'Open3'

$OUTPUT_DIR = 'output'

if Dir.exist? $OUTPUT_DIR
  puts 'The output dir already exists - delete it'
  exit 1
end

if ARGV.size < 3
  puts 'Please provide the jar to test, the agent jar, and the analyzer jar'
  exit 1
end

Dir.mkdir $OUTPUT_DIR
jar, agent, analyzer, *tests = ARGV

tests.each do |test|
  command = "java -javaagent:#{agent} -jar #{jar} #{test}"
  output, status = Open3.capture2e(command)

  sanitized_output = output.lines.flat_map do |line|
    if line.chomp =~ /^<<BREAKING_POINT>> \[THREAD \d+\]:\s+(.*)/
      [$1]
    else
      []
    end
  end.join("\n")

  output_file = File.join($OUTPUT_DIR, "#{test}_#{status.success? ? "passed" : "failed"}")
  File.open(output_file, 'w') do |file|
    file.puts sanitized_output
  end
end

`java -jar #{analyzer} #{$OUTPUT_DIR}`
