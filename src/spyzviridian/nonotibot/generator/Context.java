package spyzviridian.nonotibot.generator;

import spyzviridian.nonotibot.RNG;
import spyzviridian.nonotibot.rules.Gender;
import spyzviridian.nonotibot.rules.Number;

public class Context {
	private Gender gender;
	private Number number;
	
	public Context(Gender gender, Number number){
		this.gender = gender;
		this.number = number;
		setRandomValues();
	}
	
	public Gender getGender(){
		return gender;
	}
	
	public Number getNumber(){
		return number;
	}
	
	public Context getOverridenCopy(Context context){
		// When stacking a new context on the stack, we get a copy of that
		// context depending of the top context on the stack
		Gender gender = (context.gender == Gender.ANY) ? this.gender : context.gender;
		Number number = (context.number == Number.ANY) ? this.number : context.number;
		return new Context(gender, number);
	}
	
	private void setRandomValues(){
		if(gender == Gender.RANDOM) {
			gender = RNG.getInstance().getRandomValue(Gender.MALE, Gender.FEMALE);
		}
		if(number == Number.RANDOM) {
			number = RNG.getInstance().getRandomValue(Number.SINGULAR, Number.PLURAL);
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Context){
			Context context = (Context)other;
			return (this.getGender() == context.getGender()) && (this.getNumber() == context.getNumber());
		}
		return false;
	}
	
	public String toString(){
		return "(Gender = " + gender + ", Number = " + number +")";
	}
}
