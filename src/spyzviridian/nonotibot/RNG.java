package spyzviridian.nonotibot;

import java.util.List;
import java.util.Random;

public final class RNG {
	
	private static RNG instance;
	
	private Random random;
	
	private RNG(){
		random = new Random(System.currentTimeMillis());
	}
	
	public int getRange(int min, int max){
		max++;
		return min + random.nextInt(max - min);
	}
	
	public int getRange(int max){
		return random.nextInt(max + 1);
	}
	
	public <T> T getRandomValue(@SuppressWarnings("unchecked") T ... args){
		if(args.length <= 0) return null;
		return args[getRange(args.length - 1)];
	}
	
	public <T, L extends List<T>> T getRandomValue(L list){
		if(list.size() <= 0) return null;
		return list.get(getRange(list.size()-1));
	}
	
	public static RNG getInstance(){
		if(instance == null)
			instance = new RNG();
		return instance;
	}
	
	
}
