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

import core.ConfigHandler;
import utils.FileCopy;

public class SelectTaggingFile {
	private static String inputDirPath = null;
	private static String outputDirPath = null;	 
    private static String userDir = System.getProperty("user.dir");	 
    private static double extractPercent;
    
    public static void main(String[] args) throws IOException {
    	
    	String fileName = null;
    	
    	try{
    		
    	//----------------从配置文件中获取随机抽取率-------------------------
    	ConfigHandler CH = new ConfigHandler();		
    	extractPercent = Double.valueOf(CH.getConfig("TaggingExtractPercent"));
    	inputDirPath = CH.getConfig("AllFileDir");
    	outputDirPath = CH.getConfig("ManualTaggingFileDir");    	
    	
    	System.out.println(inputDirPath);
    	//----------------获取所有文件-----------------------------------
    	File file = new File(userDir+"\\"+inputDirPath);    
        File[] fileArray = file.listFiles(); 
        
        int fileTotal = fileArray.length;
        int extractNumber = (int) (fileTotal * extractPercent); //需要抽取的文件数
        
        System.out.println("需要提取的文件数："+ extractNumber);
        
        java.util.Random random=new java.util.Random();// 定义随机类
    	
        int r = 0;
        
    	Map<String,Boolean> extractFileName = new HashMap<String,Boolean>();   //已抽取到的文件名集合    	
    		
    	for (int i = 0; i < extractNumber; i++) {    //随机抽取文件    			
    			while (true) {
    				r = random.nextInt(fileTotal);
    				fileName = fileArray[r].getName();                    				
    				if (extractFileName.containsKey(fileName)) {  					
    					continue;
    				}
    				else {
    					extractFileName.put(fileName, true);
    					
    					System.out.println("抽取 " + fileName + " 成功！");
    					String inputPath = fileArray[r].getAbsolutePath();  
    			        String outputPath= userDir+"\\"+outputDirPath+"\\"+ fileName;
    			        FileCopy fileCopy=new FileCopy(inputPath, outputPath);  
    			        fileCopy.copy1(); 
    					break;
    				}    						
    			}    			
    	}
    	
    	
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
	            
	            fileOut.write((time+ ", 抽取人工标注文件时出错: " + fileName +": \r\n").getBytes());            
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
