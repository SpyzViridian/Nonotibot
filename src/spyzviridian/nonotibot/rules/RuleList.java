package spyzviridian.nonotibot.rules;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import spyzviridian.nonotibot.RNG;
import spyzviridian.nonotibot.exception.RuleListException;
import spyzviridian.nonotibot.generator.Context;

public class RuleList {
	
	private static RuleList instance;
	private Dictionary<String, List<Rule>> rules;
	private int ruleCount;
	
	private RuleList(){
		rules = new Hashtable<String, List<Rule>>();
		ruleCount = 0;
	}
	
	public void addRules(List<Rule> rules){
		for(Rule rule : rules){
			addRule(rule);
		}
	}
	
	public void addRule(Rule rule){
		List<Rule> ruleList = rules.get(rule.getIdentifier());
		if(ruleList == null) {
			ruleList = new ArrayList<Rule>();
			rules.put(rule.getIdentifier(), ruleList);
		} 
		ruleList.add(rule);
		ruleCount++;
	}
	
	public List<Rule> getRules(String identifier){
		return rules.get(identifier);
	}
	
	public Rule getRandomRule(String identifier){
		return RNG.getInstance().getRandomValue(rules.get(identifier));
	}
	
	public Rule getRandomRuleByContext(String identifier, Context context) throws RuleListException{
		List<Rule> suitableRules = new ArrayList<Rule>();
		List<Rule> rules = getRules(identifier);
		if((rules == null) || (rules.size() <= 0))
			throw new RuleListException("Couldn't find a rule with identifier " +identifier);
		for(Rule rule : rules){
			if(isSuitable(rule, context)){
				suitableRules.add(rule);
			}
		}
		return RNG.getInstance().getRandomValue(suitableRules);
	}
	
	public int size(){
		return ruleCount;
	}
	
	private boolean isSuitable(Gender ruleGender, Gender contextGender){
		return (contextGender == Gender.ANY) || (ruleGender == contextGender) || (ruleGender == Gender.ANY) || (ruleGender == Gender.RANDOM);
	}
	
	private boolean isSuitable(Number ruleNumber, Number contextNumber){
		return (contextNumber == Number.ANY) || (ruleNumber == contextNumber) || (ruleNumber == Number.ANY) || (ruleNumber == Number.RANDOM);
	}
	
	private boolean isSuitable(Rule rule, Context context){
		return (isSuitable(rule.getGender(), context.getGender()) && isSuitable(rule.getNumber(), context.getNumber())) || rule.isSet();
	}
	
	public static RuleList getInstance(){
		if(instance == null)
			instance = new RuleList();
		return instance;
	}

}
