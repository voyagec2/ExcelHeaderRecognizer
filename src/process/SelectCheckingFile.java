package process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import core.ConfigHandler;
import utils.FileCopy;

public class SelectCheckingFile {
	private static String inputDirPath = null;
	private static String outputDirPath1 = null;
	private static String outputDirPath2 = null;
	private static String userDir = System.getProperty("user.dir");	 
    private static double extractPercent;
    
    public static void main(String[] args) throws IOException {
    	
    	//----------------从配置文件中获取随机抽取率-------------------------
    	ConfigHandler CH = new ConfigHandler();		
    	extractPercent = Double.valueOf(CH.getConfig("CheckingExtractPercent"));
    	inputDirPath = CH.getConfig("AutoTaggedFileDir");
    	outputDirPath1 = CH.getConfig("ManualCheckingFileDir");
    	outputDirPath2 = CH.getConfig("ManualCheckingBackupFileDir");    	
    	
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
    				String fileName = fileArray[r].getName();                    				
    				if (extractFileName.containsKey(fileName)) {  					
    					continue;
    				}
    				else {
    					extractFileName.put(fileName, true);
    					
    					System.out.println("抽取 " + fileName + " 成功！");
    					String inputPath = fileArray[r].getAbsolutePath();  
    			        String outputPath1 = userDir+"\\"+outputDirPath1+"\\"+ fileName;
    			        String outputPath2 = userDir+"\\"+outputDirPath2+"\\"+ fileName;    			        
    			        
    			        FileCopy fileCopy = new FileCopy(inputPath, outputPath1);	        
    			        fileCopy.copy1();
    			        
    			        fileCopy = new FileCopy(inputPath, outputPath2);	        
    			        fileCopy.copy2(); 
    			        
    					break;
    				}    						
    			}    			
    	}
        
    	System.out.println("按回车键退出");
    	while(true){
    		   if(System.in.read() == '\n')
    		    System.exit(0);
    		  }
    }
}
