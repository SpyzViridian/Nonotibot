package spyzviridian.nonotibot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Output {
	private static final int CAPACITY = 128;
	private static final String LOGS_FOLDER = "logs";
	private static Output instance;
	
	private File log;
	private String logFileName;
	private List<Line> buffer;
	private PrintWriter logWriter;
	
	private static class Line {
		private String string;
		private Type type;
		
		public Line(String str, Type t){
			string = str;
			type = t;
		}
		
		@SuppressWarnings("unused")
		public String getString(){
			return string;
		}
		
		@SuppressWarnings("unused")
		public Type getType(){
			return type;
		}
	}
	
	public static enum Type {
		INFO(0xFFFFFF, "INFO"), ERROR(0xD42525, "ERROR"), WARNING(0xE59E51, "WARNING"), INFO_OK(0x12F90D, "INFO"), TWITTER(0x559FCE, "TWITTER"), TWEETING(0x0DF9CF, "TWEETING"), TESTING(0xC473D8, "TESTING");
		private int color;
		private String tag;
		private Type(int color, String tag){this.color = color; this.tag = tag;}
		public int getColor(){return color;}
		public String getTag(){return tag;}
	}
	
	private Output(){
		// Creamos el buffer
		buffer = new LinkedList<Line>();
		logFileName = LOGS_FOLDER + File.separator + getDay()+".txt";
		log = new File(logFileName);
		createLogFile();
	}
	
	public void printLine(String str, Output.Type t){
		str = getHourTag()+getTag(t)+" "+str;
		// ¿Está el buffer lleno?
		if(buffer.size() >= CAPACITY) {
			// Tenemos que borrar la primera línea
			buffer.remove(0);
		} 
		buffer.add(new Line(str, t));

		// Escribir en salida o error
		if((t == Type.ERROR) || (t == Type.WARNING)){
			System.err.println(str);
		} else {
			System.out.println(str);
		}
		// Escribir en el archivo de log del día
		// Puede dar un error, pero lo ignoramos
		writeToLog(str);
	}
	
	public void printToLog(String str, Output.Type t){
		str = getHourTag()+getTag(t)+" "+str;
		System.out.println(str);
		writeToLog(str);
	}
	
	private void createLogFile(){
		File dir = new File(LOGS_FOLDER);
		if(!dir.exists()){			
			dir.mkdirs();
		}
		
		if(!log.exists()){
			try {
				log.createNewFile();
			} catch (IOException e) {
				printLine("Couldn't create new log file.", Output.Type.WARNING);
				return;
			}
		}
		// Sí existe el log
		try {
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);
			logWriter = new PrintWriter(bw);
		} catch (IOException e){
			printLine("Couldn't start writing to log file.", Output.Type.WARNING);
		}
	}
	
	private void writeToLog(String str){
		if(logWriter != null) {
			logWriter.println(str);
			logWriter.flush();
		}
	}
	
	private String getTag(Output.Type t){
		return "["+t.getTag()+"]";
	}
	
	private String getDay(){
		Calendar calendar = GregorianCalendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);
		String dd = (day > 9)?""+day:"0"+day;
		String MM = (month > 9)?""+month:"0"+month;
		String yyy = ""+year;
		return dd+"-"+MM+"-"+yyy;
	}
	
	private String getHourTag(){
		Calendar calendar = GregorianCalendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String hh = (hour > 9)?""+hour:"0"+hour;
		String mm = (minute > 9)?""+minute:"0"+minute;
		String ss = (second > 9)?""+second:"0"+second;
		return "["+hh+":"+mm+":"+ss+"]";
	}
	
	public static Output getInstance(){
		if(instance == null) {
			instance = new Output();
		}
		return instance;
	}
	
}
