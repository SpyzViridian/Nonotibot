package spyzviridian.nonotibot.rules;

import java.util.List;

import spyzviridian.nonotibot.RNG;

public class EnumeratorPart extends Part {
	
	private List<Part> parts;
	
	public EnumeratorPart(List<Part> parts) {
		super("");
		this.parts = parts;
		generateString();
	}
	
	public Part getRandomPart(){
		return RNG.getInstance().getRandomValue(parts);
	}
	
	private void generateString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(int i = 0; i < parts.size(); i++){
			buffer.append(parts.get(i).toString());
			if(i < parts.size() - 1) {
				buffer.append("|");
			}
		}
		buffer.append("]");
		str = buffer.toString();
	}
	
	public List<Part> getParts(){
		return parts;
	}
	
	public String toString(int index){
		return parts.get(index).toString();
	}
	
	@Override
	public String toString(){
		return str;
	}

}
