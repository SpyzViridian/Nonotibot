package spyzviridian.nonotibot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import spyzviridian.nonotibot.Output;


public abstract class PropertyFile {
	
	protected String fileName;
	protected String subdirectory;
	protected Properties configFile;
	
	protected File file;
	
	protected PropertyFile(String subdirectory, String fileName){
		this.subdirectory = subdirectory;
		this.fileName = fileName;
		init();
	}
	
	protected void init(){
		File directory = new File(subdirectory);
	    file = new File(subdirectory+File.separator+fileName);
		// Si el subdirectorio no existe, tampoco existe el fichero
		if(!directory.exists()){
			directory.mkdirs();
		}
		
		if(!file.exists()){
			try {
				file.createNewFile();
				createFile();
			} catch (IOException e) {
				// Error fatal
				Output.getInstance().printLine(e.getMessage(), Output.Type.ERROR);
				System.exit(-1);
			}
		}
		loadFile(file);
	}
	
	protected void loadFile(File f){
		configFile = new Properties();
		try {
			configFile.load(new FileInputStream(f));
			check();
		} catch (FileNotFoundException e) {
			// No se encuentra el archivo (por alguna razón)
			// Error fatal
			Output.getInstance().printLine("Couldn't find file '"+f.getName()+"'.", Output.Type.ERROR);
			System.exit(-1);
		} catch (IOException e) {
			// Error fatal
			Output.getInstance().printLine(e.getMessage(), Output.Type.ERROR);
			System.exit(-1);
		}
	}
	
	protected abstract void createFile();
	protected abstract String getDefaultProperty(String property);
	protected abstract void check();
	
	public String getProperty(String property){
		return configFile.getProperty(property, getDefaultProperty(property));
	}
	
	public void setProperty(String property, String value){
		configFile.setProperty(property, value);
	}
	
	public void saveFile(){
		try {
			configFile.store(new FileOutputStream(file), null);
		} catch (IOException e) {
			Output.getInstance().printLine("Couldn't save file '"+file.getName()+"'.", Output.Type.ERROR);
		}
	}
	
}
