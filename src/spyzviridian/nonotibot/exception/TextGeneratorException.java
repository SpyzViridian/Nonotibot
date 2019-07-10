package spyzviridian.nonotibot.exception;

public class TextGeneratorException extends Exception {
	private static final long serialVersionUID = -8583027387999617961L;

	private String currentString;
	private String expandedIdentifier;
	
	public TextGeneratorException(String message, String currentString, String expandedIdentifier){
		super(message);
		this.currentString = currentString;
		this.expandedIdentifier = expandedIdentifier;
	}
	
	public String getString() {
		return currentString;
	}
	
	public String getExpandedIdentifier() {
		return expandedIdentifier;
	}
}
