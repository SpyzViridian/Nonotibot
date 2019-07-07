package spyzviridian.nonotibot.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import spyzviridian.nonotibot.Output;

public class Config extends PropertyFile {
	
	private static Config instance;
	private boolean checked;
	
	// File file;
	
	public Config(String subdirectory, String fileName){
		super(subdirectory, fileName);
		checked = false;
	}
	
	public static enum Property {
		BOT_NAME("botName", "The bot's Twitter account, without @"), 
		ORIGINAL_USER("originalUser", "The Twitter account you want to learn tweets from, without @"), 
		TWEET_RATE("tweetRate", "The amount of time, in secound, between tweets"),
		STARTING_RULE("startingRule", "The first rule to generate text.")
		;
		
		private String name;
		private String description;
		
		private Property(String name, String description){
			this.name = name;
			this.description = description;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getConfigDescription() {
			return "# " + name + ": " + description; 
		}
		
		@Override
		public String toString(){
			return getName();
		}
	}
	
	@Override
	protected void createFile() {
		PrintWriter logWriter = null;
		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			logWriter = new PrintWriter(bw);
		} catch (IOException e) {
			// Error fatal
			Output.getInstance().printLine("Couldn't create file '"+file.getName()+"'.", Output.Type.ERROR);
			System.exit(-1);
		}
		logWriter.println("# =================================================================================");
		logWriter.println("# Nonotibot Configuration File. Deleting this file will generate a new one.");
		logWriter.println("# =================================================================================");
		logWriter.println();
		logWriter.println("# [USER CONFIGURATION]");
		printDefaultProperty(logWriter, Config.Property.BOT_NAME);
		printDefaultProperty(logWriter, Config.Property.ORIGINAL_USER);
		logWriter.println("# =================================================================================");
		logWriter.println("# [BOT CONFIGURATION]");
		printDefaultProperty(logWriter, Config.Property.TWEET_RATE);
		printDefaultProperty(logWriter, Config.Property.STARTING_RULE);
		logWriter.flush();
		logWriter.close();
	}

	@Override
	protected String getDefaultProperty(String property) {
		switch(property){
			case "botName": return "...";
			case "originalUser": return "...";
			case "tweetRate": return "3600";
			case "startingRule": return "$$$";
		}
		return null;
	}
	
	protected void addPropertyToFile(Config.Property property) {
		PrintWriter logWriter = null;
		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			logWriter = new PrintWriter(bw);
		} catch (IOException e) {
			// Error fatal
			Output.getInstance().printLine("Couldn't edit file '"+file.getName()+"'.", Output.Type.ERROR);
			System.exit(-1);
		}
		
		printDefaultProperty(logWriter, property);
		configFile.put(property.getName(), getDefaultProperty(property.getName()));
		
		logWriter.flush();
		logWriter.close();
		
		Output.getInstance().printLine("Added new option ("+property.getName()+") to '"+file.getName()+"'.", Output.Type.INFO);
	}
	
	public String getProperty(Config.Property property){
		// Si no existe, se crea
		if(!configFile.containsKey(property.getName())) {
			addPropertyToFile(property);
		}
		return this.getProperty(property.getName());
	}
	
	public void setProperty(Config.Property property, String value){
		this.setProperty(property.getName(), value);
	}
	
	protected String getDefaultPair(Config.Property property) {
		return property.getName() + "=" + getDefaultProperty(property.getName());
	}
	
	private void printDefaultProperty(PrintWriter logWriter, Config.Property property) {
		if(!checked) {
			logWriter.println();
			checked = true;
		}
		logWriter.println(property.getConfigDescription());
		logWriter.println(getDefaultPair(property));
	}
	
	public static Config getInstance(){
		if(instance == null){
			instance = new Config("config", "bot.cfg");
		}
		return instance;
	}

	@Override
	// Comprueba que existen todos en el fichero de configuraci�n
	protected void check() {
		for(Config.Property prop : Config.Property.values()) {
			getProperty(prop);
		}
	}
	
}
