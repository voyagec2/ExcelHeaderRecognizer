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
	private static String inputDirPath1 = "2-人工标注文件";
	private static String inputDirPath2 = "4-人工抽检文件";
	private static String inputDirPath3 = "4-人工抽检文件";
	private static String userDir = System.getProperty("user.dir");
    
    public static void main(String[] args) throws IOException {
    	
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
    	
    	
    	System.out.println("按回车键退出");
    	while(true){
    		   if(System.in.read() == '\n')
    		    System.exit(0);
    	 }
    }
}
