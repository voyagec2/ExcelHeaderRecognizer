import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import core.LabelReader;
import core.SimilarityHandler;
import utils.ExcelHandler;

public class Recognizer {
	private static String labelPath = "标签.xlsx";
	private static String inputDirPath = "待标注文件";
	private static String outputDirPath = "标注文件输出";	
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
    private static String userDir = System.getProperty("user.dir");
    
	public static void main(String[] args) throws IOException {
		//System.out.println(userDir);
		
		LabelReader LR = new LabelReader(labelPath);
		HashMap<String,String> labelMap = LR.getAllLabelData();
		
		SimilarityHandler SH = new SimilarityHandler(labelMap);
		//System.out.println(labelMap.size());
		File file = new File(userDir+"\\"+inputDirPath);    
        File[] fileArray = file.listFiles(); 
        
        for(int fileIndex=0;fileIndex<fileArray.length;fileIndex++){      //文件层
            if(fileArray[fileIndex].isFile()){     
                String fileName = fileArray[fileIndex].getName();
                System.out.println("准备处理"+fileName+":");
            	if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件
            		ExcelHandler EH = new ExcelHandler(userDir+"\\"+inputDirPath+"\\"+fileName);   //打开这个excel       		
            		Workbook wb = EH.getWorkbook();            		
            		List<List<List<String>>> wbData = EH.getWorkbookData();  //获取所有sheet的数据
            		
            		for (int sheetIndex = 0; sheetIndex < wbData.size(); sheetIndex++){    //Sheet层  
            			Sheet sheet = wb.getSheetAt(sheetIndex);
            			List<List<String>> sheetData = wbData.get(sheetIndex);
            			List<String> headers = new ArrayList<String>();	     //存储表头
            			int rowNum = sheetData.size();             
            			int columnNum = 0;
            			int lastHeaderRow = 0;    //记录表头所在的最后一行位置            			
            			
            			//System.out.println("sheetData.size = " + rowNum);
            			
            			//以所有行的最大列数为整个Sheet的列数，因为存在单元格合并的情况
            			for(int i=0; i<rowNum; i++) {
            				if (sheetData.get(i).size()>columnNum)
            					columnNum = sheetData.get(i).size();    
            			}		
            			 
            			for(int i=0; i<rowNum; i++) {      	
            				List<String> rowData = sheetData.get(i);
            				int sum = 1; //计算一行内 将合并单元格视作一格后的列数，以此判断是否为表头行
            				String lastValue = "";
            				for(int j=0; j< rowData.size(); j++) {					
            					if (rowData.get(j).equals(lastValue) || rowData.get(j) == null || rowData.get(j).equals("")) 
            						continue;
            					lastValue = rowData.get(j);
            					sum ++;
            				}  
            				//System.out.println(i+" " + sum+ " " + row.getLastCellNum());
            				if (sum <= columnNum / 3)   //暂时以不同单元格数与总列数的1/3的大小关系来判别该行是否为表头行
            					continue;
            				if (headers.isEmpty()) {  //如果表头行没有存值，直接将当前行的所有值赋予表头行
            					headers = rowData;
            				}
            				else {
            					boolean flag = false;
            					for(int j=0;j<headers.size();j++) {
            						flag = flag || headers.get(j).equals(rowData.get(j));  //判断是否有当前行元素与表头元素相同					
            					}
            					if (!flag) { 
            						lastHeaderRow = i-1;
            						break;   // 如果没有任意一格相同 说明表头继承关系结束， 跳出循环
            					}
            					for(int j=0;j<headers.size();j++) {
            						if (headers.get(j).equals(rowData.get(j))) continue;
            						headers.set(j, headers.get(j) + "." + rowData.get(j));   //对表头进行组合 
            					}
            				}	    
            			}
            			

            			//for(int i=0;i<headers.size();i++) {
            			//	System.out.print(headers.get(i)+ " ");
            			//}
            			//System.out.println();
            			//System.out.println(lastHeaderRow+" "+rowNum);
            			
            			sheet.shiftRows(lastHeaderRow+1, rowNum, 1,true,true);  
            			
            			Row row = sheet.createRow(lastHeaderRow+1);
            	        
            			CellStyle style =  wb.createCellStyle();
            	        //设置左右边框宽度
            	        style.setBorderLeft(BorderStyle.THIN);
            	        style.setBorderRight(BorderStyle.THIN);
            	        style.setAlignment(HorizontalAlignment.CENTER);
            	        style.setFillForegroundColor(IndexedColors.BROWN.getIndex());  
            	        
            			for (int i=0;i<headers.size();i++) {
            				//System.out.print(SH.getLabel(headers.get(i))+" ");
            				Cell cell = row.createCell(i);
            				cell.setCellValue(SH.getLabel(headers.get(i), 0.667));   //插入标签
            				cell.setCellStyle(style);
            			} 
            			//System.out.println();
            		}
            		EH.saveExcel(wb, userDir+"\\"+outputDirPath+"\\"+fileName);			//保存到输出文件夹
            		System.out.println(fileName+"处理完毕，保存成功");
            	}                
            }            
        }  	 
	}	
}
