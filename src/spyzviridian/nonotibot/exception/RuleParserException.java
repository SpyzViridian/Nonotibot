package spyzviridian.nonotibot.exception;

public class RuleParserException extends Exception {
	private static final long serialVersionUID = 7958953056523971688L;
	
	private int line;
	private int column;
	
	public RuleParserException(String message, int line, int column){
		super(message);
		this.line = line;
		this.column = column;
	}
	
	public int getLine(){
		return line;
	}
	
	public int getColumn(){
		return column;
	}
	
}
