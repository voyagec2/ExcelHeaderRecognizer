package core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.DirReader;


public class DataCollector {
	private static String testDataDirPath = null;
	private static int labelPairTotal = 0;
	private static int labelPairDist = 0;
	
	public DataCollector(String path) {
		testDataDirPath = path;
	}
	
	public int getLabelNumTotal() {
		return labelPairTotal;
	}
	
	public int getLabelNumDist() {
		return labelPairDist;
	}
	
	public Map<String,String> getAllLabelPair() throws IOException {
		
		Map<String,String> labelPair = new HashMap<String,String>();
		DirReader DR = new DirReader(testDataDirPath);
		List<File> files = DR.readAllExcelFromDir(); 
		
		String reg = "^[0-9]+(.[0-9]+)?$-";  
		
		for (int fileIndex = 0; fileIndex < files.size(); fileIndex ++) {
			System.out.println("正在处理"+files.get(fileIndex).getName());
			ExcelHandler EH = new ExcelHandler(files.get(fileIndex).getAbsolutePath());
			int sheetNum = EH.getSheetNum();
			for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex ++) {
				
				List<String> headers = EH.getHeaders(sheetIndex);
				List<String> tags = EH.getTags(sheetIndex);
				if (headers.size()!= tags.size()) continue;   //出现异常直接跳过
				for (int i = 0; i < headers.size(); i++) {
					String key = headers.get(i);
					String value = tags.get(i);
					if (value.equals("") || value == null||value.matches(reg)) continue;  //如果标签为空或为数字则放弃
					labelPair.put(key,value);	
					labelPairTotal ++;
				}				
			}			
		}
		labelPairDist = labelPair.size();
		return labelPair;
	}
}
