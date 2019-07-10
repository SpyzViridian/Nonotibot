package spyzviridian.nonotibot;

import java.util.TimerTask;

import spyzviridian.nonotibot.exception.TextGeneratorException;
import spyzviridian.nonotibot.generator.TextGenerator;
import twitter4j.TwitterException;

public class TweetingThread extends TimerTask {

	private TextGenerator generator;
	
	public TweetingThread(TextGenerator generator) {
		this.generator = generator;
	}
	
	@Override
	public void run() {
		// Tweet
		try {
			Tweeting.getInstance().tweet(generator.generate());
		} catch (TwitterException e) {
			Output.getInstance().printLine("Couldn't send a tweet. Reason: " + e.getMessage(), Output.Type.ERROR);
		} catch (TextGeneratorException e) {
			Output.getInstance().printLine("(" + e.getString() + ", current identifier: " + e.getExpandedIdentifier() + ") " + e.getMessage(), Output.Type.ERROR);
		}
	}

}
