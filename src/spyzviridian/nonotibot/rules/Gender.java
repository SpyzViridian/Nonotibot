package spyzviridian.nonotibot.rules;

public enum Gender {
	MALE("M"), FEMALE("F"), RANDOM("?"), ANY("_");
	
	private String str;
	
	private Gender(String str){
		this.str = str;
	}
	
	public static Gender toGender(String str){
		if(str.equalsIgnoreCase(MALE.str)) return MALE;
		if(str.equalsIgnoreCase(FEMALE.str)) return FEMALE;
		if(str.equalsIgnoreCase(RANDOM.str)) return RANDOM;
		return ANY;
	}
}
