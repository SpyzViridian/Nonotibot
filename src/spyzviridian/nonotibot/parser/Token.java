package spyzviridian.nonotibot.parser;

public class Token {
	
	public static enum Type {
		IDENTIFIER("\\$([A-Z0-9$_ñÑ?]?)+"),
		LEFTBRACKET("\\{"),
		RIGHTBRACKET("\\}"),
		ARROW("->"),
		LEFTDIAMOND("<"),
		RIGHTDIAMOND(">"),
		SEPARATOR("\\|"),
		LEFTPAREN("\\("),	
		RIGHTPAREN("\\)"),
		LEFTSQUARE("\\["),
		RIGHTSQUARE("\\]"),
		LAMBDA("_"),
		TEXT("\".+?\""),
		COMMA(","),
		LITERALS("[0-9a-zA-Z\u00C0-\u024F\u1E00-\u1EFF,.:&%'\\-€+ _¿?]+"),
		PROPERTY("(@[a-zA-Z])=([a-zA-Z]|\\?)"),
		EQUALS("="),
		SEMICOLON(";"),
		ERROR(null)
		;
		
		private String pattern;
		
		private Type(String pattern){
			this.pattern = pattern;
		}
		
		public String getPattern(){
			return pattern;
		}
	}
	
	private Token.Type type;
	private String str;
	
	public Token(String str, Token.Type type){
		this.str = str;
		this.type = type;
	}
	
	public Token(){
		this.str = null;
		this.type = Token.Type.ERROR;
	}
	
	@Override
	public String toString(){
		return str;
	}
	
	public boolean isType(Token.Type type){
		return this.type == type;
	}
	
	public Token.Type getType(){
		return type;
	}

}
