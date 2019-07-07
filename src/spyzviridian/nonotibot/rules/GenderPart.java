package spyzviridian.nonotibot.rules;

public class GenderPart extends Part {
	
	protected Part maleChoice;
	protected Part femaleChoice;
	
	public GenderPart(String maleChoice, String femaleChoice) {
		this(new SimplePart(maleChoice, false), new SimplePart(femaleChoice, false));
	}
	
	public GenderPart(Part maleChoice, Part femaleChoice){
		super("<" + maleChoice.toString() + "|" + femaleChoice.toString() + ">");
		this.maleChoice = maleChoice;
		this.femaleChoice = femaleChoice;
	}
	
	@Override
	public String toString(){
		return getStr();
	}
	
	public Part toPart(Gender gender){
		return (gender == Gender.FEMALE) ? femaleChoice : maleChoice;
	}
	
	public Part getMaleChoice(){
		return maleChoice;
	}
	
	public Part getFemaleChoice(){
		return femaleChoice;
	}
	
}
