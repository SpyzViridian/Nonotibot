package spyzviridian.nonotibot.rules;

public abstract class Part {
	
	protected String str;
	
	protected Part(String str){
		this.str = str;
	}
	
	@Override
	public String toString(){
		return getStr();
	}
	
	public String getStr(){
		return str;
	}
}
