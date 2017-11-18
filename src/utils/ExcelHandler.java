package utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;  
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.ss.usermodel.Sheet;  
import org.apache.poi.ss.usermodel.Workbook;  
import org.apache.poi.ss.usermodel.WorkbookFactory;  
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 
/**  
*   
* @author Kai Chen  
*  
* @date   2017-10-28  
*/  
public class ExcelHandler {  
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
    
    private static String excelPath = null;  
    private static Workbook wb = null;
    private static List<List<List<String>>> wbData = null;
    private static int sheetNum = 0;
    
    /**
     * 构造函数 
     * @param path
     * @throws IOException 
     */
    public ExcelHandler(String path) throws IOException {
    	excelPath = path;  
        File file = new File(excelPath); 
        FileInputStream in = new FileInputStream(file);  
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003  
            wb = new HSSFWorkbook(in);  
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010  
            wb = new XSSFWorkbook(in);  
        }   
        wbData = getAllData();  
        sheetNum = wbData.size();
    }
    
    public Workbook getWorkbook() {  
        return wb;
    }  
    
    public List<List<List<String>>> getWorkbookData() {
    	return wbData;
    }
    
    public int getSheetNum() {
    	return sheetNum;
    }
    
    
    /**
     * 获取Excel中的所有数据
     * @return
     * @throws IOException 
     */
    private List<List<List<String>>> getAllData() throws IOException {    	
    	List<List<List<String>>> wbData = new ArrayList<List<List<String>>>();    	
    	for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
    		Sheet sheet = wb.getSheetAt(sheetIndex);  		
    		List<List<String>> sheetData = new ArrayList<List<String>>();	
    		Row row = null;		
    		for(int i=0; i <= sheet.getLastRowNum(); i++) {
    			row = sheet.getRow(i);
    			List<String> rowData = new ArrayList<String>();
    			if (row!=null) { 
    				for(int j=0; j< row.getLastCellNum(); j++) {  
    					// 依据该单元是否为合并单元格，分别以两种方式获取值
    					String cellData = isMergedRegion(sheet, i, j) ? getMergedRegionValue(sheet, i, j):getCellValue(row.getCell(j));
    					cellData = cellData.replaceAll(" |\n" , "");  //删除字段中的空格和换行符
    					rowData.add(cellData);
    				}  
    			}
    			sheetData.add(rowData);
    		}
    		wbData.add(sheetData);
		}		
		return wbData;
    }
    
    /**
     * 获取Excel中的表头所在结尾行
     * @return
     * @throws IOException 
     */
    public int getLastHeaderRow(int sheetIndex) {
    	List<List<String>> sheetData = wbData.get(sheetIndex);
		List<String> headers = new ArrayList<String>();	     //存储表头
		int rowNum = sheetData.size();             
		int columnNum = 0;
		int lastHeaderRow = 0;    //记录表头所在的最后一行位置            			
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
		
		return lastHeaderRow;    	
    	
    }
    
    /**
     * 获取Excel中的标签
     * @return
     * @throws IOException 
     */
    public List<String> getLabel(int sheetIndex) {
    	List<List<String>> sheetData = wbData.get(sheetIndex);
		List<String> labels = new ArrayList<String>();	     //存储标签
		int lastHeaderRow = getLastHeaderRow(sheetIndex);
		return sheetData.get(lastHeaderRow+1);	    	
    }
    
    /**
     * 获取Excel中的表头
     * @return
     * @throws IOException 
     */
    public List<String> getHeader(int sheetIndex) {
    	List<List<String>> sheetData = wbData.get(sheetIndex);
		List<String> headers = new ArrayList<String>();	     //存储表头
		int rowNum = sheetData.size();             
		int columnNum = 0;         			
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
					break;   // 如果没有任意一格相同 说明表头继承关系结束， 跳出循环
				}
				for(int j=0;j<headers.size();j++) {
					if (headers.get(j).equals(rowData.get(j))) continue;
					headers.set(j, headers.get(j) + "." + rowData.get(j));   //对表头进行组合 
				}
			}	    
		}
		
		return headers;   	
    }
    
    
    
    /** 
     * 保存工作薄 
     * @param wb 
     */  
    public void saveExcel(Workbook wb, String savePath) {  
        FileOutputStream fileOut;  
        try {  
            fileOut = new FileOutputStream(savePath);  
            wb.write(fileOut);  
            fileOut.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
    }  
    
  
	/**   
	 * 获取合并单元格的值   
	 * @param sheet   
	 * @param row   
	 * @param column   
	 * @return   
	 */    
	public String getMergedRegionValue(Sheet sheet ,int row , int column){    
		int sheetMergeCount = sheet.getNumMergedRegions();  
		for(int i = 0 ; i < sheetMergeCount ; i++){    
			CellRangeAddress ca = sheet.getMergedRegion(i);    
			int firstColumn = ca.getFirstColumn();    
			int lastColumn = ca.getLastColumn();    
			int firstRow = ca.getFirstRow();    
			int lastRow = ca.getLastRow();    
			if(row >= firstRow && row <= lastRow){    
				if(column >= firstColumn && column <= lastColumn){    
					Row fRow = sheet.getRow(firstRow);    
					Cell fCell = fRow.getCell(firstColumn);    
					return getCellValue(fCell) ;    
				}    
			}    
		}    
		return null;    
	}    
  
	/**  
	 * 判断指定的单元格是否是合并单元格  
	 * @param sheet   
	 * @param row 行下标  
	 * @param column 列下标  
	 * @return  
	 */  
	private boolean isMergedRegion(Sheet sheet,int row ,int column) {  
		int sheetMergeCount = sheet.getNumMergedRegions();  
		for (int i = 0; i < sheetMergeCount; i++) {  
			CellRangeAddress range = sheet.getMergedRegion(i);  
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			if(row >= firstRow && row <= lastRow){  
				if(column >= firstColumn && column <= lastColumn){  
					return true;  
				}  
			}  
		}  
		return false;  
	}  
  
	/**   
	 * 获取单元格的值   
	 * @param cell   
	 * @return   
	 */    
	public String getCellValue(Cell cell){  
		String cellValue = "";
		if (cell==null)
			return cellValue;
		CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case BLANK:
                cellValue = "";
                break;
            default:
                break;
        }
        return cellValue;  
	}
	
}  