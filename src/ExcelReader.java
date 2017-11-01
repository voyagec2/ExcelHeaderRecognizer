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
public class ExcelReader {  
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
    
    private static String excelPath = "F:\\2.xls";  
    
    public static void main(String args[]) {
    	ExcelReader ER = new ExcelReader();
    	ER.readExcelToObj(excelPath); 
    	
    	
    } 
    
    /** 
     * 保存工作薄 
     * @param wb 
     */  
    private void saveExcel(Workbook wb) {  
        FileOutputStream fileOut;  
        try {  
            fileOut = new FileOutputStream(excelPath);  
            wb.write(fileOut);  
            fileOut.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
    }  
    
    /** 
     * 判断Excel的版本,获取Workbook 
     * @param in 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static Workbook getWorkbok(File file) throws IOException{  
        Workbook wb = null;  
        FileInputStream in = new FileInputStream(file);  
        if(file.getName().endsWith(EXCEL_XLS)){  //Excel 2003  
            wb = new HSSFWorkbook(in);  
        }else if(file.getName().endsWith(EXCEL_XLSX)){  // Excel 2007/2010  
            wb = new XSSFWorkbook(in);  
        }  
        return wb;  
    }  
	/**  
	 * 读取excel表头 
	 * @param path  
	 */  
	private void readExcelToObj(String path) {          
		try {
			File finalXlsxFile = new File(path); 
			Workbook wb = getWorkbok(finalXlsxFile); 
			for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++)
				readExcelHeader(wb, sheetIndex);
			saveExcel(wb);  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}  
  
	/**  
	 * 读取excel文件的表头  
	 * @param wb   
	 * @param sheetIndex sheet页下标：从0开始
	 * return: List<String> header  
	 */  
	private List<String> readExcelHeader(Workbook wb,int sheetIndex) {  
		Sheet sheet = wb.getSheetAt(sheetIndex);  		
		List<String> headers = new ArrayList<String>();	
		int rowNum = sheet.getLastRowNum();
		int columnNum = 0;
		int lastHeaderRow = 0;
		//System.out.println(sheet.getLastRowNum());
		Row row = null; 
		
		for(int i=0; i<rowNum; i++) {
			row = sheet.getRow(i);
			if (row==null) continue; //如果为空行直接跳过
			if (row.getLastCellNum()>columnNum)
				columnNum = row.getLastCellNum();    //以所有行的最大列数为整个Sheet的列数，因为存在单元格合并的情况
		}		
		 
		for(int i=0; i<rowNum; i++) {  
			row = sheet.getRow(i);
			if (row==null) continue; //如果为空行直接跳过
			List<String> cellValue = new ArrayList<String>();
			int sum = 1; //计算一行内 将合并单元格视作一格后的列数，以此判断是否为表头行
			String lastValue = "";
			for(int j=0; j< row.getLastCellNum(); j++) {  
				// 依据该单元是否为合并单元格，分别以两种方式获取值
				String candidate = isMergedRegion(sheet, i, j) ? getMergedRegionValue(sheet, i, j):getCellValue(row.getCell(j));
				candidate = candidate.replaceAll(" |\n" , "");  //删除字段中的空格和换行符
				cellValue.add(candidate);
				//System.out.print(candidate+ " ");
				if (candidate.equals(lastValue) || candidate == null || candidate.equals("")) 
					continue;
				lastValue = candidate;
				sum ++;
			}  
			//System.out.println(i+" " + sum+ " " + row.getLastCellNum());
			if (sum <= columnNum / 3)   //暂时以不同单元格数与总列数的1/3的大小关系来判别该行是否为表头行
				continue;
			if (headers.isEmpty()) {  //如果表头行没有存值，直接将当前行的所有值赋予表头行
				headers = cellValue;
			}
			else {
				boolean flag = false;
				for(int j=0;j<headers.size();j++) {
					flag = flag || headers.get(j).equals(cellValue.get(j));  //判断是否有当前行元素与表头元素相同					
				}
				if (!flag) { 
					lastHeaderRow = i;
					break;   // 如果没有任意一格相同 说明表头继承关系结束， 跳出循环
				}
				for(int j=0;j<headers.size();j++) {
					if (headers.get(j).equals(cellValue.get(j))) continue;
					headers.set(j, headers.get(j) + "." + cellValue.get(j));   //对表头进行组合 
				}
			}	    
		}
		System.out.println(headers.size());
		for(int i=0;i<headers.size();i++) {
			System.out.print(headers.get(i)+ " ");
		}
		System.out.println();
		System.out.println(lastHeaderRow+" "+rowNum);
		sheet.shiftRows(lastHeaderRow+1, rowNum+1, 1,true,true);  

		return headers;  
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