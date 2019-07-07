package spyzviridian.nonotibot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import spyzviridian.nonotibot.exception.TextGeneratorException;
import spyzviridian.nonotibot.generator.TextGenerator;
import spyzviridian.nonotibot.parser.FolderParser;
import spyzviridian.nonotibot.rules.RuleList;

public class Launcher {
	
	private static final String RULES_PATH = "rules";

	public static void main(String[] args) throws IOException {
		// Get all rules and add them to the main list
		FolderParser parser = new FolderParser(RULES_PATH);
		parser.parseFolder(); // Rules have been added
		Output.getInstance().printLine("Added " + RuleList.getInstance().size() + " rules.", Output.Type.INFO);
		
		TextGenerator generator = new TextGenerator(240);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(reader.readLine() != null){
			try {
				System.out.print(generator.generate());
			} catch (TextGeneratorException e) {
				Output.getInstance().printLine(e.getMessage(), Output.Type.ERROR);
			}
		}
		
	}
}
