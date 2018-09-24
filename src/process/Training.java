package process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import core.ConfigHandler;
import core.DataCollector;
import core.SimilarityHandler;
import core.TagHandler;

public class Training {
	private static String inputDirPath1 = null;
	private static String inputDirPath2 = null;
	private static String inputDirPath3 = null;
	private static String userDir = System.getProperty("user.dir");
    
    public static void main(String[] args) throws IOException {
    	
    	try {
    		
    	ConfigHandler CH = new ConfigHandler();
    	inputDirPath1 = CH.getConfig("ManualTaggingFileDir");
    	inputDirPath2 = CH.getConfig("ManualCheckingFileDir");
    	inputDirPath3 = CH.getConfig("BatchTaggedFileOutDir");    	
    	
    	DataCollector DC = new DataCollector(userDir+"\\"+inputDirPath1);
    	Map<String,String> labelPair = DC.getAllLabelPair();    	
    	
    	TagHandler TH = new TagHandler();
    	for (Map.Entry<String, String> entry:labelPair.entrySet()) {
    		String key = entry.getKey();
    		String value = entry.getValue();
    		if (TH.existKey(key)) 
    			TH.updateLine(key, value);
    		else 
    			TH.addLine(key, value);
    	}    	
    	
    	TH.saveExcel();
    	
    	DC = new DataCollector(userDir+"\\"+inputDirPath2);
    	labelPair = DC.getAllLabelPair();    	
    	
    	TH = new TagHandler();
    	for (Map.Entry<String, String> entry:labelPair.entrySet()) {
    		String key = entry.getKey();
    		String value = entry.getValue();
    		if (TH.existKey(key)) 
    			TH.updateLine(key, value);
    		else 
    			TH.addLine(key, value);
    	} 	
    	
    	TH.saveExcel();
    	
    	DC = new DataCollector(userDir+"\\"+inputDirPath3);
    	labelPair = DC.getAllLabelPair();    	
    	
    	TH = new TagHandler();
    	for (Map.Entry<String, String> entry:labelPair.entrySet()) {
    		String key = entry.getKey();
    		String value = entry.getValue();
    		if (TH.existKey(key)) 
    			TH.updateLine(key, value);
    		else 
    			TH.addLine(key, value);
    	} 	
    	
    	TH.saveExcel();
    	
    }
	catch (Exception e) {
		System.out.println("出现异常，程序中止操作，请排查后重新运行");
		
		Date now = new Date();   	
	    SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分ss秒");//可以方便地修改日期格式
		String time = dateFormat.format( now );
		FileOutputStream fileOut;  
        try {  
            fileOut = new FileOutputStream(userDir+"\\"+"异常日志"+".log");  
            fileOut.write("--------------------------------------------------------\r\n".getBytes());
            
            fileOut.write((time+ ", 训练标签库时出错: " + ": \r\n").getBytes());            
            fileOut.write(("异常信息: " + e.getMessage() +": \r\n").getBytes());	                
            fileOut.write(("异常信息: " + e.toString() +": \r\n").getBytes());
            	            
            fileOut.close();  
        } 
        catch (Exception ex) {  
			ex.printStackTrace();  
		}  
	}
    	
    	
    	System.out.println("按回车键退出");
    	while(true){
    		   if(System.in.read() == '\n')
    		    System.exit(0);
    	 }
    }
}
