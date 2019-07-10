package spyzviridian.nonotibot;

import spyzviridian.nonotibot.config.Auth;
import spyzviridian.nonotibot.config.Config;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Tweeting {
	
	private static Tweeting instance;
	
	private Twitter twitter;
	private Configuration configuration;
	
	private Tweeting() {
		Auth auth = Auth.getInstance();
		Config config = Config.getInstance();
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(auth.getProperty(Auth.Property.CONSUMER_KEY));
		builder.setOAuthConsumerSecret(auth.getProperty(Auth.Property.CONSUMER_SECRET));
		builder.setOAuthAccessToken(auth.getProperty(Auth.Property.ACCESS_TOKEN));
		builder.setOAuthAccessTokenSecret(auth.getProperty(Auth.Property.ACCESS_SECRET));
		builder.setUser(config.getProperty(Config.Property.BOT_NAME));
		
		configuration = builder.build();
		twitter = new TwitterFactory(configuration).getInstance();
	}
	
	public void tweet(String message) throws TwitterException {
		twitter.updateStatus(message);
		Output.getInstance().printLine(message, Output.Type.TWEETING);
	}
	
	public static Tweeting getInstance() {
		if(instance == null) {
			instance = new Tweeting();
		}
		return instance;
	}

}
