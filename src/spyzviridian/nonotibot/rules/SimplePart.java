package spyzviridian.nonotibot.rules;

public class SimplePart extends Part {

	private boolean isId;
	
	public SimplePart(String str, boolean isId) {
		super(str);
		this.isId = isId;
	}
	
	public boolean isIdentifier(){
		return isId;
	}

}
