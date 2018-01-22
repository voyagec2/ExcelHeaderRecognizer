package process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import core.DataCollector;
import core.SimilarityHandler;

import java.util.*;

public class Testing {
	private static String inputDirPath = "历史数据";
	private static String userDir = System.getProperty("user.dir");
    
    public static void main(String[] args) throws IOException {
    	
    	DataCollector DC = new DataCollector(userDir+"\\"+inputDirPath);
    	Map<String,String> labelPair = DC.getAllLabelPair();
    	
    	Date now = new Date();   	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分ss秒");//可以方便地修改日期格式
    	String time = dateFormat.format( now );    	
    	
    	FileOutputStream report = new FileOutputStream(new File(userDir+"\\"+"测试报告"+time+".txt")); ;     
 
    	report.write(("标签对总数: "+String.valueOf(DC.getLabelNumTotal())+"\r\n").getBytes());   
    	  
    	report.write(("不同标签对数量: "+String.valueOf(DC.getLabelNumDist())+"\r\n").getBytes()); 
    	
    	int total = labelPair.size();     //标签对总数    	
    	int testTotal = (int) (total * 0.8);   //用作测试的标签对数量
    	int predictTotal = total - testTotal;  //用作预测的标签对数量
    	
    	double[] similarityThresholds = {1, 0.95, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2,0.1, 0};  
    	
    	int ran = 0;
    	java.util.Random random=new java.util.Random();// 定义随机类
    	
    	for (int i = 0; i < similarityThresholds.length; i++) {
    		Map<String,String> testPair = new HashMap<String,String>();   //测试集
    		Map<String,String> predictPair = new HashMap<String, String>();  //预测集
    		List<String> headers = new ArrayList<String>(labelPair.keySet());    
    		
    		for (int j = 0; j < predictTotal; j++) {    //随机抽取预测集    			
    			while (true) {
    				ran = random.nextInt(total);
    				String key = headers.get(ran);
    				if (predictPair.containsKey(key)) {
    					continue;
    				}
    				else {
    					predictPair.put(key, labelPair.get(key));
    					break;
    				}    						
    			}    			
    		}
    		
    		for (int j =0; j < total; j++) {
    			if (!predictPair.containsKey(headers.get(j))) {
    				testPair.put(headers.get(j), labelPair.get(headers.get(j)));
    			}
    		}
    		
    		/*SimilarityHandler SH = new SimilarityHandler();
    		int hitNum = 0;
    		int emptyNum = 0;
    		for (Map.Entry<String, String> entry : predictPair.entrySet()) { 
    			String mostSimilarityOne = SH.getTag(entry.getKey());
    			if (mostSimilarityOne.equals(entry.getValue()))
    				hitNum++;
    			else
    			if (mostSimilarityOne.equals(""))
    				emptyNum++;    			   
    		}
    		report.write(("----------------------------------------------------------------\r\n").getBytes()); 
    		report.write(("相似度阈值 :"+String.valueOf(similarityThresholds[i])+"\r\n").getBytes()); 
    		report.write(("留空率 :"+String.valueOf(emptyNum*1.0/predictTotal)+"\r\n").getBytes());
    		report.write(("精确度 :"+String.valueOf(hitNum*1.0/(predictTotal-emptyNum))+"\r\n").getBytes());*/  					
    		
    	}  	 
    	

    	report.close();  

    }
}
