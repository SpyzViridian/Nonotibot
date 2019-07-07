package spyzviridian.nonotibot.rules;

import java.util.ArrayList;
import java.util.List;

public final class BodyPart extends Part{
	
	public final static class Builder {
		
		private List<Part> bodyParts;
		
		public Builder(){
			bodyParts = new ArrayList<Part>();
		}
		
		public Builder addParts(List<Part> parts){
			bodyParts.addAll(parts);
			return this;
		}
		
		public Builder addPart(Part part){
			bodyParts.add(part);
			return this;
		}
		
		public Builder addSimple(String text){
			bodyParts.add(new SimplePart(text, false));
			return this;
		}
		
		public Builder addIdentifier(String identifier){
			bodyParts.add(new SimplePart(identifier, true));
			return this;
		}
		
		public Builder addLambda(){
			bodyParts.add(new LambdaPart());
			return this;
		}
		
		public Builder addEnumerator(List<Part> parts){
			bodyParts.add(new EnumeratorPart(parts));
			return this;
		}
		
		public Builder addGenderChoice(String maleChoice, String femaleChoice){
			bodyParts.add(new GenderPart(maleChoice, femaleChoice));
			return this;
		}
		
		public Builder addNumberChoice(String singularChoice, String pluralChoice){
			bodyParts.add(new NumberPart(singularChoice, pluralChoice));
			return this;
		}
		
		public Builder addNumberChoice(String plural){
			bodyParts.add(new NumberPart(plural));
			return this;
		}
		
		public BodyPart create(){
			return new BodyPart(bodyParts);
		}
		
	}
	
	private List<Part> bodyParts;
	
	//////////////////////
	//// CONSTRUCTORS ////
	//////////////////////
	
	private BodyPart(List<Part> bodyParts){
		super("");
		this.bodyParts = bodyParts;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(Part part: bodyParts){
			buffer.append(part.toString());
		}
		return buffer.toString();
	}
	
	public List<Part> getBodyParts(){
		return bodyParts;
	}
	
}
