package spyzviridian.nonotibot.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spyzviridian.nonotibot.Output;
import spyzviridian.nonotibot.exception.RuleParserException;
import spyzviridian.nonotibot.parser.Token.Type;
import spyzviridian.nonotibot.rules.*;
import spyzviridian.nonotibot.rules.Number;

public final class RuleParser {
	
	private String str;
	private int lineNumber;
	private int column;
	private int startingColumn;
	
	private String identifier;
	private BodyPart.Builder bodyBuilder;
	private Token token;
	private Gender gender;
	private Number number;
	private boolean set;
	
	public RuleParser(String str, int lineNumber, int startingColumn){
		this.str = str;
		this.lineNumber = lineNumber;
		this.startingColumn = startingColumn;
		this.column = 0;
	}
	
	public Rule parse() throws RuleParserException {
		set = false;
		bodyBuilder = new BodyPart.Builder();
		start();
		BodyPart body = bodyBuilder.create();
		return new Rule(identifier, body, gender, number, set);
	}
	
	private void start() throws RuleParserException {
		token = getNextToken(); // Get the next token
		if(token.isType(Type.IDENTIFIER)){
			// Action: left side of the rule has been found
			identifier = token.toString();
			identifier();
		} else {
			// We always should get an identifier first, so this is an error
			throw new RuleParserException("Expected IDENTIFIER at the start of the rule.", lineNumber, startingColumn + column);
		}
	}
	
	private void identifier() throws RuleParserException {
		token = getNextToken(); // Get the next token
		// Next thing should be an arrow
		if(token.isType(Type.ARROW)){
			// Move to arrow
			arrow();
		} else {
			throw new RuleParserException("Expected -> after the rule identifier.", lineNumber, startingColumn + column);
		}
	}
	
	private void arrow() throws RuleParserException {
		token = getNextToken(); // Get the next token
		if(token.isType(Type.LEFTBRACKET)){
			leftBracket();
		} else {
			throw new RuleParserException("Expected '{' after '->'.", lineNumber, startingColumn + column);
		}
	}
	
	private void leftBracket() throws RuleParserException {
		int textColumn = column + startingColumn;
		token = getNextToken(); // Get the next token
		if(token.isType(Type.TEXT)){
			analyzeText(token.toString());
			text();
		} else if(token.isType(Type.LAMBDA)){
			bodyBuilder.addLambda();
			text();
		} else {
			throw new RuleParserException("Expected '{' after '->'.", lineNumber, textColumn);
		}
	}
	
	private void text() throws RuleParserException {
		token = getNextToken(); // Get the next token
		if(token.isType(Type.COMMA)){
			comma();
		} else if (token.isType(Type.RIGHTBRACKET)){
			end();
		} else {
			throw new RuleParserException("Expected ',' or '[' after TEXT.", lineNumber, startingColumn);
		}
	}
	
	private void comma() throws RuleParserException {
		token = getNextToken(); // Get the next token
		if(token.isType(Type.PROPERTY)){
			Pattern pattern = Pattern.compile(token.getType().getPattern());
			Matcher matcher = pattern.matcher(token.toString());
			matcher.find();
			String key = matcher.group(1);
			String value = matcher.group(2);
			if(key.equalsIgnoreCase("@g")){
				gender = Gender.toGender(value);
			} else if(key.equalsIgnoreCase("@n")){
				number = Number.toNumber(value);
			} else if(key.equalsIgnoreCase("@s")){
				set = value.equalsIgnoreCase("T");
			}
			property();
		} else {
			throw new RuleParserException("Expected a property after ','", lineNumber, startingColumn + column);
		}
	}
	
	private void property() throws RuleParserException{
		token = getNextToken(); // Get the next token
		if (token.isType(Type.RIGHTBRACKET)){
			end();
		} else if (token.isType(Type.COMMA)){
			comma();
		}
	}
	
	private void end() {
		if(gender == null) gender = Gender.ANY;
		if(number == null) number = Number.ANY;
	}
	
	private void analyzeText(String text){
		// Change state
		// Remove the quotes
		text = text.substring(1, text.length() - 1);
		
		TextParser textParser = new TextParser(text, lineNumber, startingColumn + column, bodyBuilder);
		try {
			textParser.parse();
		} catch (RuleParserException e) {
			Output.getInstance().printLine("(Rule " + identifier + "): " + e.getMessage(), Output.Type.ERROR);
		}
	}

	
	private Token getNextToken() {
		// If the reading reaches the end, return an ERROR token
		if (column >= str.length()) return new Token();
		// Ignore
		while(str.charAt(column) == ' '){
			column++;
			// Return an ERROR token
			if (column >= str.length()) return new Token();
		}
		
		Pattern pattern = null;
		Matcher matcher = null;
		for( Token.Type tokenType : Token.Type.values()){
			// Iterate each token type in order
			if (tokenType.getPattern() != null) {
				pattern = Pattern.compile(tokenType.getPattern());
				matcher = pattern.matcher(str);
				// matcher.find(position) will return a string that matches the pattern, starting from position
				if (matcher.find(column) && (matcher.start() == column)){
					column = matcher.end();
					return new Token(matcher.group(), tokenType);
				}
			}
		}
		
		// We couldn't return a matching token
		return new Token();
	}
	
}
