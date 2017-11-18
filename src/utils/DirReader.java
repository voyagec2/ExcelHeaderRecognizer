package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;

public class DirReader {
	private static final String EXCEL_XLS = "xls";  
    private static final String EXCEL_XLSX = "xlsx";  
    private static String dirPath = null;
    
    public DirReader(String path) {
    	dirPath = path;
    }
    
	public List<File> readAllExcelFromDir() throws IOException {
        
		List<File> allData = new ArrayList<File>();
		int fileNum = 0, folderNum = 0;
        File file = new File(dirPath);
        
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    //System.out.println("文件夹:" + file2.getAbsolutePath());
                    list.add(file2);
                    folderNum++;
                } else {
                    //System.out.println("文件:" + file2.getAbsolutePath());
                    String fileName = file2.getName();
                    if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件   
                		allData.add(file2);
                    }
                    fileNum++;
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        //System.out.println("文件夹:" + file2.getAbsolutePath());
                        list.add(file2);
                        folderNum++;
                    } else {
                        //System.out.println("文件:" + file2.getAbsolutePath());
                        String fileName = file2.getName();
                        if (fileName.endsWith(EXCEL_XLS) || fileName.endsWith(EXCEL_XLSX)) {   //确定是EXCEL文件
                        	allData.add(file2);
                        }
                        fileNum++;
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
        
        return allData;
	}
}
