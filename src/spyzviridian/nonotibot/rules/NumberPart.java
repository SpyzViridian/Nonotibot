package spyzviridian.nonotibot.rules;

public class NumberPart extends Part {
	
	protected Part singularChoice;
	protected Part pluralChoice;
	
	public NumberPart(String singularChoice, String pluralChoice) {
		this(new SimplePart(singularChoice, false), new SimplePart(pluralChoice, false));
	}
	
	public NumberPart(String plural) {
		this(new SimplePart(plural, false));
	}
	
	public NumberPart(Part singularChoice, Part pluralChoice) {
		super("(" + ((singularChoice.toString().equalsIgnoreCase("")) ? pluralChoice : (singularChoice + "|" + pluralChoice)) + ")");
		this.singularChoice = singularChoice;
		this.pluralChoice = pluralChoice;
	}
	
	public NumberPart(Part plural) {
		this(new LambdaPart(), plural);
	}
	
	@Override
	public String toString(){
		return getStr();
	}
	
	public String toString(Number number){
		return (number == Number.PLURAL) ? pluralChoice.toString() : singularChoice.toString();
	}
	

}
