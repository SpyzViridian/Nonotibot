package spyzviridian.nonotibot.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import spyzviridian.nonotibot.Output;


public class Auth extends PropertyFile {
	
	private static Auth instance;
	
	public Auth(String subdirectory, String fileName) {
		super(subdirectory, fileName);
	}
	
	public static enum Property {
		CONSUMER_KEY("consumerKey"), CONSUMER_SECRET("consumerSecret"), ACCESS_TOKEN("accessToken"), ACCESS_SECRET("accessSecret");
		
		private String name;
		
		private Property(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		@Override
		public String toString(){
			return getName();
		}
	}

	@Override
	protected void createFile() {
		File file = new File(subdirectory+File.separator+fileName);
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
		logWriter.println("# Nonotibot Auth File. Deleting this file will generate a new one.");
		logWriter.println("# =================================================================================");
		logWriter.println();
		logWriter.println("# The bot needs to have access to its account so it has permission to tweet.");
		logWriter.println("# 1) Create a Twitter acount for your bot.");
		logWriter.println("# 2) While logged in, go to https://apps.twitter.com and create a new app.");
		logWriter.println("# 3) Generate access keys.");
		logWriter.println("# Please make sure there are no whitespaces when pasting the keys.");
		logWriter.println();
		logWriter.println(Auth.Property.CONSUMER_KEY+"="+getDefaultProperty(Auth.Property.CONSUMER_KEY.getName()));
		logWriter.println(Auth.Property.CONSUMER_SECRET+"="+getDefaultProperty(Auth.Property.CONSUMER_SECRET.getName()));
		logWriter.println(Auth.Property.ACCESS_TOKEN+"="+getDefaultProperty(Auth.Property.ACCESS_TOKEN.getName()));
		logWriter.println(Auth.Property.ACCESS_SECRET+"="+getDefaultProperty(Auth.Property.ACCESS_SECRET.getName()));
		logWriter.flush();
		logWriter.close();
	}

	@Override
	protected String getDefaultProperty(String property) {
		switch(property){
		case "consumerKey": return "paste_consumer_key_here";
		case "consumerSecret": return "paste_consumer_secret_here";
		case "accessToken": return "paste_access_token_here";
		case "accessSecret": return "paste_access_secret_here";
		}
		return null;
	}
	
	public String getProperty(Auth.Property property){
		return this.getProperty(property.getName());
	}
	
	public void setProperty(Auth.Property property, String value){
		this.setProperty(property.getName(), value);
	}
	
	public static Auth getInstance(){
		if(instance == null){
			instance = new Auth("config", "auth.cfg");
		}
		return instance;
	}

	@Override
	protected void check() {
		// Not neccesary in this case
	}
	
}
