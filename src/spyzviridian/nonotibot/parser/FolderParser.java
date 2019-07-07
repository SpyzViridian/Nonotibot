package spyzviridian.nonotibot.parser;

import java.io.File;

import spyzviridian.nonotibot.rules.RuleList;

public final class FolderParser {

	private String mainFolder;
	
	public FolderParser(String mainFolder){
		this.mainFolder = mainFolder;
	}
	
	public void parseFolder(){
		parseFolder(new File(mainFolder));
	}
	
	private boolean parseFolder(File folder){
		// Recursively search all files
		File[] files = folder.listFiles();
		for(File f : files){
			if(f.isDirectory()) {
				if(!parseFolder(f))
					return false;
			} else findRules(f);
		}
		return true;
	}
	
	private void findRules(File file){
		RuleExtractor extractor = new RuleExtractor(file);
		RuleList.getInstance().addRules(extractor.extract());
	}
}
