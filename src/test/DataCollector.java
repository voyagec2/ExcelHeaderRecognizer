package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.DirReader;
import utils.ExcelHandler;


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
		for (int fileIndex = 0; fileIndex < files.size(); fileIndex ++) {
			ExcelHandler EH = new ExcelHandler(files.get(fileIndex).getAbsolutePath());
			int sheetNum = EH.getSheetNum();
			for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex ++) {
				List<String> headers = EH.getHeader(sheetIndex);
				List<String> labels = EH.getLabel(sheetIndex);
				if (headers.size()!= labels.size()) continue;   //出现异常直接跳过
				for (int i = 0; i < headers.size(); i++) {
					String key = headers.get(i);
					String value = labels.get(i);
					labelPair.put(key,value);	
					labelPairTotal ++;
				}				
			}			
		}
		labelPairDist = labelPair.size();
		return labelPair;
	}
}
