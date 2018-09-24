package process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import core.ConfigHandler;
import core.ExcelHandler;
import core.SimilarityHandler;
import core.TagHandler;
import core.TaggingInfo;

public class TaggingAllFile {
	private static String inputDirPath = null;
	private static String outputDirPath = null;	
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
    private static String userDir = System.getProperty("user.dir");
    private static double similarityThreshold;
	public static void main(String[] args) throws IOException {
		//System.out.println(userDir);
		String fileName = null;
		try {
		//----------------从配置文件中获取配置信息-------------------------
		ConfigHandler CH = new ConfigHandler();	
		similarityThreshold = Double.valueOf(CH.getConfig("﻿SimilarityThreshold"));
		
		inputDirPath = CH.getConfig("AllFileDir");
		outputDirPath = CH.getConfig("AutoTaggedFileDir");		

		//----------------------------------------------------------------------
		SimilarityHandler SH = new SimilarityHandler();		
		
		List<TaggingInfo> report = new ArrayList<TaggingInfo>(); 
		
		File file = new File(userDir+"\\"+inputDirPath); 
		
		System.out.println(inputDirPath);
        File[] fileArray = file.listFiles(); 
        
        for(int fileIndex=0;fileIndex<fileArray.length;fileIndex++){      //文件层
            if(fileArray[fileIndex].isFile()){     
                fileName = fileArray[fileIndex].getName();
                System.out.println("准备处理"+fileName+":");
            	if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件
            		ExcelHandler EH = new ExcelHandler(userDir+"\\"+inputDirPath+"\\"+fileName);   //打开这个excel       		
            		Workbook wb = EH.getWorkbook();            		
            		List<List<List<String>>> wbData = EH.getWorkbookData();  //获取所有sheet的数据
            		for (int sheetIndex = 0; sheetIndex < wbData.size(); sheetIndex++){    //Sheet层  
            			Sheet sheet = wb.getSheetAt(sheetIndex);
            			List<List<String>> sheetData = wbData.get(sheetIndex);
            			List<String> header = EH.getHeaders(sheetIndex);     //获取表头
            			int rowNum = sheetData.size();             
            			int lastHeaderRow = EH.getLastHeaderRow(sheetIndex);    //获取表头所在的最后一行位置              
            			sheet.shiftRows(lastHeaderRow+1, rowNum - 1, 1);  //后移一行，以便插入标签
            			Row row = sheet.createRow(lastHeaderRow+1);  //创建标签行
            	        
            			CellStyle style =  wb.createCellStyle();   //设置左右边框宽度
            	        style.setBorderLeft(BorderStyle.THIN);
            	        style.setBorderRight(BorderStyle.THIN);
            	        style.setAlignment(HorizontalAlignment.CENTER);
            	        style.setFillForegroundColor(IndexedColors.BROWN.getIndex());  
            	        
            			for (int i=0;i<header.size();i++) {
            				System.out.println("正在为“"+header.get(i)+ "”寻找相似表头");
            				List<String> data = SH.getMostLikelyHeader(header.get(i));
            				
            				String mostlikelyHeader = data.get(0);
            				String tag = data.get(1);
            				double similarity = Double.valueOf(data.get(2));
            				System.out.println("最相似表头为   “"+mostlikelyHeader+ "”");
            				
            				if (similarity<similarityThreshold ) tag = "";   //如果最大相似度小于阈值，采取留空
            				Cell cell = row.createCell(i);
            				cell.setCellValue(tag);   //插入标签
            				cell.setCellStyle(style);
            				
            				TaggingInfo info = new TaggingInfo(fileName, sheetIndex, header.get(i), mostlikelyHeader, tag , similarity, tag==""); 
            				report.add(info);
            			} 
            			//System.out.println();
            		}
            		EH.saveExcel(wb, userDir+"\\"+outputDirPath+"\\"+fileName);			//保存到输出文件夹
            		System.out.println(fileName+"处理完毕，保存成功");
            	}                
            }            
        }  	 
        
        for (int i = 0; i< report.size()-1; i++)
        	for (int j = i+1; j< report.size(); j++) {
        		if (report.get(i).getSimilarty() > report.get(j).getSimilarty()) {
        			TaggingInfo info = report.get(i);
        			report.set(i,report.get(j));
        			report.set(j, info);        			
        		}
        	}
        		
        Date now = new Date();   	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分ss秒");//可以方便地修改日期格式
    	String time = dateFormat.format( now );    	
    	
    	@SuppressWarnings("resource")
		Workbook wb = new XSSFWorkbook();
    	
    	Sheet sheet = wb.createSheet();	    	
    	
    	Row header = sheet.createRow(0);
    	
    	Cell cell1 = header.createCell(0);
    	cell1.setCellValue("表头");
    	
    	Cell cell2 = header.createCell(1);
    	cell2.setCellValue("最相似表头");
    	
    	Cell cell3 = header.createCell(2);
    	cell3.setCellValue("标签");
    	
    	Cell cell4 = header.createCell(3);
    	cell4.setCellValue("是否留空");
    	
    	Cell cell5 = header.createCell(4);
    	cell5.setCellValue("相似度");
    	
    	Cell cell6 = header.createCell(5);
    	cell6.setCellValue("所在文件");
    	
    	Cell cell7 = header.createCell(6);
    	cell7.setCellValue("所在SheetIndex");
    	
    	for (int i = 0; i< report.size()-1; i++) { 
    		Row row = sheet.createRow(i+1);
    		TaggingInfo info = report.get(i);
    		cell1 = row.createCell(0);
        	cell1.setCellValue(info.getHeader());
        	
        	cell2 = row.createCell(1);
        	cell2.setCellValue(info.getMostLikelyHeader());
        	
        	cell3 = row.createCell(2);
        	cell3.setCellValue(info.getTag());
        	
        	cell4 = row.createCell(3);
        	cell4.setCellValue(info.getIsStayEmpty());
        	
        	cell5 = row.createCell(4);
        	cell5.setCellValue(info.getSimilarty());
        	
        	cell6 = row.createCell(5);
        	cell6.setCellValue(info.getBelongExcel());
        	
        	cell7 = row.createCell(6);        	
        	cell7.setCellValue(info.getBelongSheet());
        	  		
    	}
    	
    	FileOutputStream fileOut;  
        try {  
            fileOut = new FileOutputStream(userDir+"\\"+"标注报告"+time+".xls");  
            wb.write(fileOut);  
            fileOut.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
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
	            
	            fileOut.write((time+ ", 标注所有文件时出错: " + fileName +": \r\n").getBytes());            
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
