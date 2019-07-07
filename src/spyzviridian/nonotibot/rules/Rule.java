package spyzviridian.nonotibot.rules;

public final class Rule {
	
	private String identifier;
	private BodyPart body;
	private Gender gender;
	private Number number;
	private boolean set;
	
	//////////////////////
	//// CONSTRUCTORS ////
	//////////////////////
	
	public Rule(String identifier, BodyPart body, Gender gender, Number number){
		this(identifier, body, gender, number, false);
	}
	
	public Rule(String identifier, BodyPart body, Gender gender, Number number, boolean set){
		this.identifier = identifier;
		this.body = body;
		this.gender = gender;
		this.number = number;
		this.set = set;
	}
	
	//////////////////////
	/////// METHODS //////
	//////////////////////
	
	@Override
	public String toString() {
		return identifier + " -> " + body.toString() + " {" + gender.toString() + ", " + number.toString() + "}";
	}
	
	//////////////////////
	/////// ACCESS ///////
	//////////////////////
	
	public String getIdentifier(){
		return identifier;
	}
	
	public BodyPart getBody(){
		return body;
	}
	
	public Gender getGender(){
		return gender;
	}
	
	public Number getNumber(){
		return number;
	}
	
	public boolean isSet(){
		return set;
	}
}
