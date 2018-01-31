package process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.ConfigHandler;
import core.ExcelHandler;
import core.TagHandler;

public class Optimizing {
	private static String beferCheckDirPath = null;
	private static String afterCheckDirPath = null;
	private static String outputPath = "优化报告.txt";
	private static String userDir = System.getProperty("user.dir");		
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx"; 
    
	public static void main(String[] args) throws IOException {
		
		
		//----------------从配置文件中获取随机抽取率-------------------------
    	ConfigHandler CH = new ConfigHandler();		
    	beferCheckDirPath = CH.getConfig("ManualCheckingBackupFileDir");
    	afterCheckDirPath = CH.getConfig("ManualCheckingFileDir");

    	
        
      //------------------读取抽检前的标签信息----------------------------
        File file2 = new File(userDir+"\\"+beferCheckDirPath);    
        File[] fileArray = file2.listFiles(); 
        Map<String,String> beforeCheck = new HashMap<String,String>();
        
        for(int fileIndex=0;fileIndex<fileArray.length;fileIndex++){      //文件层
            if(fileArray[fileIndex].isFile()){     
                String fileName = fileArray[fileIndex].getName();
            	if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件
            		ExcelHandler EH = new ExcelHandler(userDir+"\\"+beferCheckDirPath+"\\"+fileName);   //打开这个excel       		           		
            		int sheetNum = EH.getSheetNum();  //获取sheet数量            		
            		for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++){    //Sheet层 
            			List<String> headers = EH.getHeaders(sheetIndex);
            			List<String> tags = EH.getTags(sheetIndex);
            			for (int i =0 ;i < headers.size(); i++) 
            				beforeCheck.put(headers.get(i),tags.get(i));
            		}   
            	}           
            }          
        }
        
        //---------------------读取标签库信息------------------------        
        
        TagHandler TR = new TagHandler();
		HashMap<String,String> tagMap = TR.getHeaderTagMap();	
		
		//----------------------读取质检后的标签信息,并比对----------------------
		
        FileOutputStream report = new FileOutputStream(new File(userDir+"\\"+outputPath)); 
        
        int eventId = 0;
        
		File file1 = new File(userDir+"\\"+afterCheckDirPath);    
        fileArray = file1.listFiles(); 
     
        for(int fileIndex = 0; fileIndex < fileArray.length; fileIndex++){      //文件层
            if(fileArray[fileIndex].isFile()){     
                String fileName = fileArray[fileIndex].getName();
            	if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件            		
            		ExcelHandler EH = new ExcelHandler(userDir+"\\"+afterCheckDirPath+"\\"+fileName);   //打开这个excel       		           		
            		int sheetNum = EH.getSheetNum();  //获取sheet数量            		
            		for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++){    //Sheet层 
            			List<String> headers = EH.getHeaders(sheetIndex);
            			List<String> tags = EH.getTags(sheetIndex);
            			for (int i =0 ;i < headers.size(); i++)  {
            				String key = headers.get(i);
            				String value = tags.get(i);
            				if (value.equals("") || value == null) continue;   //如果是空的不更新
            				
            	        	String writeInfo = "表头: "+key+"\r\n"+ "标签: "+ value + "\r\n";
            	        	if (!tagMap.containsKey(key)) {
            	        		report.write(("-------------------------------------------------- \r\n").getBytes());
            	        		report.write(("新增事件ID = "+String.valueOf(eventId)+": \r\n").getBytes());
            	        		report.write(("文件名  = "+fileName +": \r\n").getBytes());
            	        		report.write(("页序号  = "+ sheetIndex +": \r\n").getBytes());            	        		
            	        		report.write((writeInfo + "在标签库中不存在！ \r\n").getBytes());
            	        		if (!beforeCheck.containsKey(key)) {
            	        			report.write(",在标注生成文件中不存在.\r\n".getBytes());        			
            	        		}
            	        		else if (beforeCheck.get(key).equals(value)) {
            	        			report.write(",与标注生成文件中标签一致. \r\n".getBytes());        			
            	        		}
            	        		else {
            	        			report.write((",与标注生成文件中标签不一致！ 标注生成文件中为：" + beforeCheck.get(key)+"\r\n").getBytes()); 
            	        		}
            	        		tagMap.put(key, value);
            	        		TR.addLine(key,value);   
            	        		report.write("已添加至标签库！\r\n".getBytes()); 
            	        		eventId ++;
            	        	}
            	        	else 
            	        	if (!tagMap.get(key).equals(value))      	{
            	        		report.write(("-------------------------------------------------- \r\n").getBytes());
            	        		report.write(("更新事件ID = "+String.valueOf(eventId)+": \r\n").getBytes());
            	        		report.write(("文件名  = "+fileName +": \r\n").getBytes());
            	        		report.write(("页序号  = "+ sheetIndex +": \r\n").getBytes());  
            	        		report.write((writeInfo + "在标签库中已存在！ 标签库中为： " + tagMap.get(key) +"\r\n").getBytes());
            	        		if (!beforeCheck.containsKey(key)) {
            	        			report.write("在标注生成文件中不存在.\r\n".getBytes());        			
            	        		}
            	        		else if (beforeCheck.get(key).equals(value)) {
            	        			report.write("与标注生成文件中标签一致.\r\n".getBytes());        			
            	        		}
            	        		else {
            	        			report.write(("与标注生成文件中标签不一致！标注生成文件中为： " + beforeCheck.get(key)+"\r\n").getBytes()); 
            	        		}
            	        		tagMap.put(key, value);
            	        		TR.updateLine(key,value);   
            	        		report.write("已更新标签库！\r\n".getBytes()); 
            	        		eventId ++;
            	        	}
            				
            				
            			}
            		}   
            	}           
            }          
        }

        report.close();  	
        //-----------------重写回标签库------------------------------
        TR.saveExcel();
	}	
}
