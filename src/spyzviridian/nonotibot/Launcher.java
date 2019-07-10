package spyzviridian.nonotibot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

import spyzviridian.nonotibot.config.Auth;
import spyzviridian.nonotibot.config.Config;
import spyzviridian.nonotibot.exception.TextGeneratorException;
import spyzviridian.nonotibot.generator.TextGenerator;
import spyzviridian.nonotibot.parser.FolderParser;
import spyzviridian.nonotibot.rules.RuleList;

public class Launcher {
	
	private static final String RULES_PATH = "rules";
	private static final int MAX_LENGTH = 250;
	
	private static Timer tweetingTimer;
	private static File rulesPath;

	public static void main(String[] args) throws IOException {
		rulesPath = new File(RULES_PATH);
		// Create the directory if it doesn't exist
		if(!rulesPath.exists()) {
			rulesPath.mkdirs();
		}
		
		Config.getInstance();
		Auth.getInstance();
		
		// Get all rules and add them to the main list
		Output.getInstance().printLine("Loading rules...", Output.Type.INFO);
		FolderParser parser = new FolderParser(RULES_PATH);
		parser.parseFolder(); // Rules have been added
		Output.getInstance().printLine("Added " + RuleList.getInstance().size() + " rules.", Output.Type.INFO_OK);
		
		TextGenerator generator = new TextGenerator(MAX_LENGTH);
		
		// Start tweeting??
		if(!Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			int tweetRate = Integer.parseInt(Config.getInstance().getProperty(Config.Property.TWEET_RATE));
			tweetingTimer = new Timer("TweetingThread", true);
			tweetingTimer.schedule(new TweetingThread(generator), tweetRate * 1000, tweetRate * 1000);
		} else {
			Output.getInstance().printLine("Bot is in offline mode!", Output.Type.WARNING);
		}
		
		Output.getInstance().printLine("Bot ready!", Output.Type.INFO_OK);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(reader.readLine() != null){
			try {
				Output.getInstance().printLine(generator.generate(), Output.Type.TESTING);
			} catch (TextGeneratorException e) {
				Output.getInstance().printLine("(" + e.getString() + ", current identifier: " + e.getExpandedIdentifier() + ") " + e.getMessage(), Output.Type.ERROR);
			}
		}
		
	}
}
