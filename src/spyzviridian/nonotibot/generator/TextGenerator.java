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
		String result = buffer.toString();
		result = firstCharacterToUpperCase(result);
		result = cutExtraSpaces(result);
		return result;
	}
	
	private void start(String startingRuleId) throws TextGeneratorException{
		Context context = new Context(Gender.ANY, Number.ANY);
		stackContext(context);
		List<Rule> rules = ruleList.getRules(startingRuleId);
		if((rules == null) || rules.size() <= 0) {
			throw new TextGeneratorException("Couldn't find starting symbol.");
		}
		Rule rule = ruleList.getRules(startingRuleId).get(0);
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
			throw new TextGeneratorException(e.getMessage());
		}
		if(rule == null) {
			throw new TextGeneratorException("There are no suitable rules for rule with identifier \"" 
					+ identifier + "\" in context " + "(" + context.getGender() + ", " + context.getNumber() +")");
		}
		//System.out.println("CHOSEN RULE = " + rule.toString());
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
		return str.replaceAll("\\s\\s+", " ");
	}
}
