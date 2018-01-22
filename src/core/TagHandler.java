package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class TagHandler {
	 private static String tagPath = "标签库.xlsx";
	 private static Workbook wb = null;
	 private static HashMap<String,String> headerTagMap = null;
	 private static HashMap<String,Double> headerWeightMap = null;
	 private static ExcelHandler EH = null;
	 private static ConfigHandler CH = null;
	 private static String userDir = System.getProperty("user.dir");
	 
	 private static Double trainTagWeight = null;
	    
	 /*
	  * 构造函数 
	  */
	 public TagHandler() throws IOException {   
		 EH = new ExcelHandler(userDir+"\\"+tagPath);
		 wb = EH.getWorkbook();
	     getAllTags();
	     
	     CH = new ConfigHandler();		
	     trainTagWeight = Double.valueOf(CH.getConfig("TrainTagWeight"));	     
	 }
	 
	 
	 public HashMap<String,String> getHeaderTagMap() {
		 return headerTagMap;
	 }
	 
	 public HashMap<String,Double> getHeaderWeightMap() {
		 return headerWeightMap;
	 }
	 
	 public boolean existKey(String key) {
		 return headerTagMap.containsKey(key);		 
	 }
	 
	 /*
	  * 从EXCEL中获取标签，存到MAP中
	  * @return
	  */
	 public void getAllTags() throws IOException {	 
		 List<List<List<String>>> data = EH.getWorkbookData();
		 HashMap<String,String> header2Tag = new HashMap<String,String>();
		 HashMap<String,Double> header2Weight = new HashMap<String,Double>();
 		 for (int sheetIndex = 0; sheetIndex < data.size(); sheetIndex++) {
 			 for (int rowIndex = 1; rowIndex < data.get(sheetIndex).size(); rowIndex++) {
 				 int column = 0;
 				 int lastColumn = data.get(sheetIndex).get(rowIndex).size()-1;
 				 while (column < lastColumn) {
 					 String key = data.get(sheetIndex).get(rowIndex).get(column);
 					 String value = data.get(sheetIndex).get(rowIndex).get(column + 1);
 					 Double weight = Double.valueOf(data.get(sheetIndex).get(rowIndex).get(column + 2));
 					 header2Tag.put(key, value);
 					 header2Weight.put(key, weight);
 					 column += 3;
 				 }
 			} 			 
 		 }	 

 		 headerTagMap = header2Tag;
 		 headerWeightMap = header2Weight;
	 }
	 
	 
	 public void addLine(String key, String value) {
		 int rowNum= wb.getSheetAt(0).getLastRowNum();	//获得总行数
		 Row row = wb.getSheetAt(0).createRow(rowNum+1);
		 Cell cell0 = row.createCell(0);
		 Cell cell1 = row.createCell(1);
		 Cell cell2 = row.createCell(2);		 
		 cell0.setCellValue(key);
		 cell1.setCellValue(value);		
		 cell2.setCellValue(trainTagWeight);
	 }
	 
	 public void updateLine(String key, String value) {
		 Sheet sheet = wb.getSheetAt(0);
		 int rowNum = sheet.getLastRowNum();	//获得总行数
		 for (int i = 0; i < rowNum; i++) {
			 if (sheet.getRow(i).getCell(1)!=null) {
				 String header = String.valueOf(sheet.getRow(i).getCell(1));
				 if (header.equals(key))  {
					 sheet.getRow(i).getCell(0).setCellValue(value);
				 }
			 }
		 }	 
	 }
	
	 
	  /** 
	   * 保存工作薄 
	   * @param wb 
	   */  
	  public void saveExcel() {  
	        FileOutputStream fileOut;  
	        try {  
	            fileOut = new FileOutputStream(tagPath);  
	            wb.write(fileOut);  
	            fileOut.close();  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  
	 }  
}
