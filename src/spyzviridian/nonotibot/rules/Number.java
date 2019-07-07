package spyzviridian.nonotibot.rules;

public enum Number {
	SINGULAR("S"), PLURAL("P"), RANDOM("?"), ANY("_");
	
	private String str;
	
	private Number(String str){
		this.str = str;
	}
	
	public static Number toNumber(String str){
		if(str.equalsIgnoreCase(PLURAL.str)) return PLURAL;
		if(str.equalsIgnoreCase(SINGULAR.str)) return SINGULAR;
		if(str.equalsIgnoreCase(RANDOM.str)) return RANDOM;
		return ANY;
	}
}
