package spyzviridian.nonotibot.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spyzviridian.nonotibot.exception.RuleParserException;
import spyzviridian.nonotibot.parser.Token.Type;
import spyzviridian.nonotibot.rules.*;

public class TextParser {
	
	private static enum State {
		NORMAL, NUMBER;
	}
	
	private int pos;
	private int column;
	private int line;
	private String text;
	private Part choice1;
	private Part choice2;
	private Part genderChoice1;
	private Part genderChoice2;
	private Token token;
	private BodyPart.Builder builder;
	private BodyPart.Builder enumBuilder;
	private BodyPart.Builder genderBuilder1;
	private BodyPart.Builder genderBuilder2;
	private List<Part> enumParts;
	private State state;
	private boolean enumState;
	private int genderState;
	
	public TextParser(String text, int line, int column, BodyPart.Builder builder){
		this.text = text;
		this.line = line;
		this.column = column;
		this.builder = builder;
		pos = 0;
	}
	
	
	public void parse() throws RuleParserException{
		state = State.NORMAL;
		enumState = false;
		neutral();
	}
	
	private void addPart(Part part){
		if(genderState == 1){
			//System.out.println("GENDER PART 1: " + part);
			genderBuilder1.addPart(part);
		} else if (genderState == 2){
			//System.out.println("GENDER PART 2: " + part);
			genderBuilder2.addPart(part);
		} else {
			if(enumState){
				// Adds a part to the current enum part
				//System.out.println("ENUM PART: " + part);
				enumBuilder.addPart(part);
			} else {
				//System.out.println("PART: " + part);
				builder.addPart(part);
			}
		}
	}
	
	private void addEnumPart(Part part){
		enumParts.add(part);
	}
	
	private void resetNumberChoices(){
		choice1 = choice2 = null;
	}
	
	private void neutral() throws RuleParserException{
		if(hasEnded()) return;
		token = getNextTextToken();
		if(token.isType(Type.LITERALS)){
			// Add a simple part to the rule body
			SimplePart part = new SimplePart(token.toString(), false);
			addPart(part);
			literals();
		} else if (token.isType(Type.IDENTIFIER)){
			// Add an identifier to the rule body
			SimplePart part = new SimplePart(token.toString(), true);
			addPart(part);
			literals();
		} else if (token.isType(Type.LEFTDIAMOND)){
			leftDiamond();
		} else if (token.isType(Type.LEFTSQUARE)){
			leftSquare();
		} else if (token.isType(Type.LEFTPAREN)){
			leftParen();
		} else if (token.isType(Type.SEPARATOR) && (enumState || (genderState == 1))){
			separator();
		} else if (token.isType(Type.RIGHTSQUARE)){
			finishEnumState();
		} else if (token.isType(Type.RIGHTDIAMOND)){
			finishGenderState();
		} else {
			throw new RuleParserException("(neutral) Expected LITERALS, IDENTIFIER, '<', '[' or '('.", line, column + pos);
		}
	}
	
	private void literals() throws RuleParserException{
		if(hasEnded()) return;
		token = getNextTextToken();
		if (token.isType(Type.IDENTIFIER)){
			// Add an identifier to the rule body
			SimplePart part = new SimplePart(token.toString(), true);
			addPart(part);
			literals();
		} else if (token.isType(Type.LITERALS)){
			SimplePart part = new SimplePart(token.toString(), false);
			addPart(part);
			literals();
		} else if (token.isType(Type.LEFTDIAMOND) && (state == State.NORMAL)){
			leftDiamond();
		} else if (token.isType(Type.LEFTSQUARE)){
			leftSquare();
		} else if (token.isType(Type.LEFTPAREN) && (state == State.NORMAL)){
			leftParen();
		} else if (token.isType(Type.SEPARATOR)){
			separator();
		} else if (token.isType(Type.RIGHTDIAMOND) && (genderState == 2)){
			if((genderChoice1 == null) || (genderChoice1 == null))
				throw new RuleParserException("Gender expression lacks a choice. <A|B>", line, column + pos);
			finishGenderState();
		} else if (token.isType(Type.RIGHTPAREN) && (state == State.NUMBER)){
			if(choice1 == null) 
				throw new RuleParserException("Expected at least one number choice (A|B) or (A)", line, column + pos);
			NumberPart part = (choice2 == null) ? new NumberPart(choice1) : new NumberPart(choice1, choice2);
			addPart(part);
			state = State.NORMAL;
			neutral();
		} else if(token.isType(Type.RIGHTSQUARE) && enumState){
			// All enum parts have been added
			finishEnumState();
		} else {
			throw new RuleParserException("(literals) Expected LITERALS, IDENTIFIER, '<', '[' or '('.", line, column + pos);
		}
	}
	
	private void leftDiamond() throws RuleParserException{
		// <, gender choice
		genderChoice1 = null;
		genderChoice2 = null;
		genderBuilder1 = new BodyPart.Builder();
		genderBuilder2 = new BodyPart.Builder();
		genderState = 1;
		token = getNextTextToken();
		if (token.isType(Type.LITERALS)){
			genderBuilder1.addSimple(token.toString());
			literals();
		} else if(token.isType(Type.LEFTPAREN)){
			leftParen();
		} else {
			throw new RuleParserException("Expected LITERALS after '<'.", line, column + pos);
		}
	}
	
	
	private void leftSquare() throws RuleParserException{
		// [, enumerator
		enumState = true;
		token = getNextTextToken();
		enumParts = new ArrayList<Part>();
		enumBuilder = new BodyPart.Builder();
		if(token.isType(Type.LITERALS)){
			Part simplePart = new SimplePart(token.toString(), false);
			addPart(simplePart); // Adds to the current enum part
			literals();
		} else if (token.isType(Type.IDENTIFIER)){
			Part simplePart = new SimplePart(token.toString(), true);
			addPart(simplePart); // Adds to the current enum part
			literals();
		} else if(token.isType(Type.LEFTDIAMOND)){
			leftDiamond();
		} else if(token.isType(Type.LEFTPAREN)){
			leftParen();
		} else {
			throw new RuleParserException("Expected LITERALS, IDENTIFIER, '<' or '(' after '['.", line, column + pos);
		}
	}
	
	private void leftParen() throws RuleParserException{
		// (, number choice
		resetNumberChoices();
		state = State.NUMBER;
		token = getNextTextToken();
		if (token.isType(Type.LITERALS)){
			choice1 = new SimplePart(token.toString(), false);
			literals();
		} else {
			throw new RuleParserException("Expected LITERALS after '('.", line, column + pos);
		}
	}
	
	private void separator() throws RuleParserException{
		if(enumState && (state == State.NORMAL) && (genderState == 0)){
			BodyPart body = enumBuilder.create();
			addEnumPart(body);
			enumBuilder = new BodyPart.Builder();
		} else if (genderState == 1 && (state == State.NORMAL)){
			genderChoice1 = genderBuilder1.create();
			genderState = 2;
		}
		token = getNextTextToken();
		if(token.isType(Type.LITERALS)) {
			if((genderState > 0) && (state == State.NORMAL)) {
				Part simplePart = new SimplePart(token.toString(), false);
				//genderBuilder2.addSimple(token.toString());
				addPart(simplePart);
				literals();
			} else if (state == State.NUMBER) {
				choice2 = new SimplePart(token.toString(), false);
				literals();
			} else if (enumState){
				Part simplePart = new SimplePart(token.toString(), false);
				addPart(simplePart); // Adds to the current enum part
				literals();
			} else {
				throw new RuleParserException("Expected LITERALS after '|'.", line, column + pos);
			}
		} else if(token.isType(Type.IDENTIFIER) && (enumState || (genderState > 0))) {
			Part simplePart = new SimplePart(token.toString(), true);
			addPart(simplePart); // Adds to the current enum part
			literals();
		} else if(token.isType(Type.LEFTDIAMOND)){
			leftDiamond();
		} else if(token.isType(Type.LEFTPAREN)){
			leftParen();
		} else {
			throw new RuleParserException("Expected IDENTIFIER, '<' or '(' after '|'.", line, column + pos);
		}
	}
	
	private void finishEnumState() throws RuleParserException{
		BodyPart ruleBody = enumBuilder.create();
		addEnumPart(ruleBody);
		EnumeratorPart part = new EnumeratorPart(enumParts);
		enumState = false;
		addPart(part);
		neutral();
	}
	
	private void finishGenderState() throws RuleParserException{
		genderChoice2 = genderBuilder2.create();
		GenderPart genderPart = new GenderPart(genderChoice1, genderChoice2);
		genderState = 0;
		addPart(genderPart);
		neutral();
	}
	
	private boolean hasEnded(){
		return pos >= text.length();
	}
	
	private Token getNextTextToken(){
		if (hasEnded()) return new Token();
		Pattern pattern = null;
		Matcher matcher = null;
		for( Token.Type tokenType : Token.Type.values()){
			// Iterate each token type in order
			if (tokenType.getPattern() != null) {
				if(tokenType == Token.Type.COMMA)
					tokenType = Token.Type.LITERALS; // Token type correction
				pattern = Pattern.compile(tokenType.getPattern());
				matcher = pattern.matcher(text);
				// matcher.find(position) will return a string that matches the pattern, starting from position
				if (matcher.find(pos)){
					if(matcher.start() == pos){
						pos = matcher.end();
						return new Token(matcher.group(), tokenType);
					}
				}
			}
		}
		
		return new Token();
	}
}
