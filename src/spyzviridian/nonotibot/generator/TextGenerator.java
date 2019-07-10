package spyzviridian.nonotibot.generator;

import java.util.LinkedList;
import java.util.List;

import spyzviridian.nonotibot.config.Config;
import spyzviridian.nonotibot.exception.RuleListException;
import spyzviridian.nonotibot.exception.TextGeneratorException;
import spyzviridian.nonotibot.rules.*;
import spyzviridian.nonotibot.rules.Number;

public class TextGenerator {
	
	private int maxLength;
	private List<Context> stack;
	private StringBuffer buffer;
	private String currentExpandedIdentifier;
	
	private RuleList ruleList;
	
	public TextGenerator(int maxLength){
		this.maxLength = maxLength;
	}
	
	public String generate() throws TextGeneratorException{
		do {
			stack = new LinkedList<Context>();
			buffer = new StringBuffer();
			ruleList = RuleList.getInstance();
			start(Config.getInstance().getProperty(Config.Property.STARTING_RULE));
		} while (buffer.length() > maxLength);
		String result = buffer.toString().trim();
		if(Config.getInstance().getProperty(Config.Property.SPANISH_CONTRACTIONS).equalsIgnoreCase("true"))
			result = spanishContraction(result);
		if(Config.getInstance().getProperty(Config.Property.SPANISH_Y_TO_E).equalsIgnoreCase("true"))
			result = spanishYtoE(result);
		result = firstCharacterToUpperCase(result);
		result = replaceQuotes(result);
		result = fixQuotes(result);
		result = cutExtraSpaces(result);
		return result;
	}
	
	private void start(String startingRuleId) throws TextGeneratorException{
		Context context = new Context(Gender.ANY, Number.ANY);
		stackContext(context);
		List<Rule> rules = ruleList.getRules(startingRuleId);
		if((rules == null) || rules.size() <= 0) {
			throw new TextGeneratorException("Couldn't find starting symbol.", "", "(none)");
		}
		Rule rule = ruleList.getRules(startingRuleId).get(0);
		currentExpandedIdentifier = "(none)";
		expand(rule.getIdentifier());
		unstack();
	}
	
	private void expand(String identifier) throws TextGeneratorException{
		Context context = getContext();
		//System.out.println("CURRENT CONTEXT = " + context.toString());
		Rule rule;
		try {
			rule = ruleList.getRandomRuleByContext(identifier, context);
		} catch (RuleListException e) {
			throw new TextGeneratorException(e.getMessage(), buffer.toString(), currentExpandedIdentifier);
		}
		if(rule == null) {
			throw new TextGeneratorException("There are no suitable rules for rule with identifier \"" 
					+ identifier + "\" in context " + "(" + context.getGender() + ", " + context.getNumber() +")", buffer.toString(), currentExpandedIdentifier);
		}
		//System.out.println("CHOSEN RULE = " + rule.toString());
		currentExpandedIdentifier = identifier;
		stackContext(new Context(rule.getGender(), rule.getNumber()));
		for(Part part : rule.getBody().getBodyParts()){
			// For each part in the body
			expandPart(part, context);
		}
		unstack();
	}
	
	private void expandPart(Part part, Context context) throws TextGeneratorException{
		if (part instanceof SimplePart){
			simplePart((SimplePart)(part));
		} else if(part instanceof GenderPart){
			genderPart((GenderPart)part, context);
		} else if (part instanceof NumberPart){
			numberPart((NumberPart)part, context);
		} else if (part instanceof EnumeratorPart){
			enumeratorPart((EnumeratorPart)part, context);
		} else if (part instanceof LambdaPart){
			lambdaPart((LambdaPart)part);
		} else if (part instanceof BodyPart) {
			bodyPart((BodyPart)part, context);
		}
	}
	
	private void simplePart(SimplePart part) throws TextGeneratorException{
		if(part.isIdentifier()){
			expand(part.getStr());
		} else {
			buffer.append(part.getStr());
		}
	}
	
	private void genderPart(GenderPart part, Context context) throws TextGeneratorException{
		Part partChoice = part.toPart(context.getGender());
		expandPart(partChoice, context);
	}
	
	private void numberPart(NumberPart part, Context context){
		buffer.append(part.toString(context.getNumber()));
	}
	
	private void enumeratorPart(EnumeratorPart part, Context context) throws TextGeneratorException{
		expandPart(part.getRandomPart(), context);
	}
	
	private void lambdaPart(LambdaPart part){
		buffer.append("");
	}
	
	private void bodyPart(BodyPart body, Context context) throws TextGeneratorException{
		for(Part part : body.getBodyParts()){
			expandPart(part, context);
		}
	}
	
	private void stackContext(Context context){
		if(stack.size() <= 0){
			stack.add(context);
		} else {
			stack.add(getContext().getOverridenCopy(context));
		}
	}
	
	private void unstack(){
		if(stack.size() > 0){
			stack.remove(stack.size() - 1);
		}
	}
	
	private Context getContext(){
		return stack.get(stack.size() - 1);
	}
	
	private String firstCharacterToUpperCase(String str){
		if(str.length() < 2) return str;
		return str.substring(0,1).toUpperCase() + str.substring(1);
	}
	
	private String cutExtraSpaces(String str){
		return str.replaceAll("\\s\\s+", " ").replaceAll(" : ", ": ");
	}
	
	private String spanishContraction(String str) {
		return str.replaceAll(" a el ", " al ").replaceAll(" de el ", " del ");
	}
	
	private String spanishYtoE(String str) {
		return str.replaceAll(" y i", " e i").replaceAll(" y I", " e I");
	}
	
	private String replaceQuotes(String str) {
		return str.replaceAll("''", "\"");
	}
	
	private String fixQuotes(String str) {
		char[] chars = str.toCharArray();
		if(chars.length < 2) return str;
		
		boolean start = false;
		for(int i = 0; i < chars.length - 1; i++) {
			if(chars[i] == '"') {
				if(!start) {
					start = true;
					if(chars[i+1] == ' ') {
						chars[i+1] = '·';
					}
				} else {
					start = false;
				}
			} else if(chars[i] == ' ') { 
				if(start && chars[i+1] == '"') {
					chars[i] = '·';
				}
			}
		}
		return new String(chars).replaceAll("·", "");
	}
}
