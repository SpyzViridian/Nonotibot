package spyzviridian.nonotibot.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import spyzviridian.nonotibot.Output;
import spyzviridian.nonotibot.exception.RuleParserException;
import spyzviridian.nonotibot.rules.Rule;

public class RuleExtractor {
	
	private static final char COMMENT_CHARACTER = '#';
	private static final char FIRST_RULE_CHARACTER = '$';
	private static final char LAST_RULE_CHARACTER = '}';
	
	private File file;
	
	public RuleExtractor(File file){
		this.file = file;
	}
	
	public List<Rule> extract(){
		BufferedReader reader = null;
		List<Rule> rules = new ArrayList<Rule>();
		
		try {
			reader = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file.getAbsolutePath()), "UTF8"));
		} catch (FileNotFoundException e) {
			Output.getInstance().printLine("File "+file.getAbsolutePath()+ "not found.", Output.Type.ERROR);
		} catch (UnsupportedEncodingException e) {
			Output.getInstance().printLine("File "+file.getAbsolutePath()+ "cannot be opened with UTF8 encoding.", Output.Type.ERROR);
		}
		
		// If we could create the reader, continue
		if(reader != null) {
			String line = null;
			boolean newRule = true;
			StringBuffer buffer = new StringBuffer(); // We'll hold the characters here
			RuleParser ruleParser = null;
			int lineCount = 1;
			int columnCount = 0;
			int startingColumn = 0;
			
			while (true){
				// For each line in the source file ...
				try { line = reader.readLine(); }
				catch (IOException e){
					Output.getInstance().printLine("File "+file.getAbsolutePath()+ "cannot be read.", Output.Type.ERROR);
					line = null;
				}
				
				if(line == null) break; // Exit point
				
				// Line is not null, we may continue
				// First of all, check if the line is a comment, so we can ignore it
				if((line.length() <= 0) || (line.trim().charAt(0) == COMMENT_CHARACTER)) {
					lineCount++;
					continue;
				}
				// For each character...
				for(columnCount = 0; columnCount < line.length(); columnCount++){
					char character = line.charAt(columnCount);
					if(newRule && (character == FIRST_RULE_CHARACTER)){
						// This is a new rule and we find the first character
						newRule = false;
						buffer.append(character);
						startingColumn = columnCount;
					} else if(!newRule && (character == LAST_RULE_CHARACTER)){
						// This is not a new rule and we find the last character
						newRule = true;
						buffer.append(character);
						//System.out.println("REGLA: " + buffer.toString().trim());
						// Now we call the RuleParser
						ruleParser = new RuleParser(buffer.toString().trim(), lineCount, startingColumn);
						try {
							Rule rule = ruleParser.parse();
							rules.add(rule);
						} catch (RuleParserException e) {
							Output.getInstance().printLine("(File \"" + file.getAbsolutePath() + "\", line " 
									+ e.getLine() + ", column " + e.getColumn() + "): " 
									+ e.getMessage(), Output.Type.ERROR);
						}
						// Clear the buffer
						buffer = new StringBuffer();
					} else if(!isIgnorableCharacter(character)){
						buffer.append(character);
					}
				}
				
				lineCount++;
			}
			
			try {
				reader.close();
			} catch (IOException e) {
				Output.getInstance().printLine("File "+file.getAbsolutePath()+ "cannot be closed.", Output.Type.ERROR);
			}
		}
		
		return rules;
	}
	
	private boolean isIgnorableCharacter(char c){
		return (c == '\r') || (c == '\n') || (c == '\t');
	}

}
