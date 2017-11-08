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
    }
    
    public Workbook getWorkbook() {  
        return wb;
    }  
    
    /**
     * 获取Excel中的所有数据
     * @return
     * @throws IOException 
     */
    public List<List<List<String>>> getAllData() throws IOException {    	
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