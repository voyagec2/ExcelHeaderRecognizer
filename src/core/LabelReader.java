package core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import utils.ExcelHandler;

public class LabelReader {
	 private static String labelPath = null;
	 
	 /*
	  * 构造函数 
	  */
	 public LabelReader (String path) {
	    	labelPath = path;    	
	 }
	 
	 /*
	  * 从EXCEL中获取标签，存到MAP中
	  * @return
	  */
	 public HashMap<String,String> getAllLabelData() throws IOException {
		 ExcelHandler EH = new ExcelHandler(labelPath);		 
		 List<List<List<String>>> data = EH.getWorkbookData();
		 HashMap<String,String> Header2Label = new HashMap<String,String>();
 		 for (int sheetIndex = 0; sheetIndex < data.size(); sheetIndex++) {
 			 for (int rowIndex = 0; rowIndex < data.get(sheetIndex).size(); rowIndex++) {
 				 int column = 1;
 				 int lastColumn = data.get(sheetIndex).get(rowIndex).size()-1;
 				 while (column < lastColumn) {
 					 String value = data.get(sheetIndex).get(rowIndex).get(column);
 					 String key = data.get(sheetIndex).get(rowIndex).get(column + 1);
 					 Header2Label.put(key, value);
 					 column += 2;
 				 }
 			} 			 
 		 }	 
 		 
 		 Header2Label.put("序号","S");
 		 Header2Label.put("姓名","PNAME");
 		 Header2Label.put("身份证号","PCID");
 		 Header2Label.put("人员类型","PCTYPE");
 		 Header2Label.put("部门","DEPT");
		 return Header2Label;		 
	 }
}
