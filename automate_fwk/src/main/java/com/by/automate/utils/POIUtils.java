package com.by.automate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class POIUtils {

   
    public static void setGroups(String sheetName, String excelPath , int beginRow, int endRow){
        HSSFSheet sheet = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(excelPath));
            HSSFWorkbook wb = new HSSFWorkbook(is);
            sheet = wb.getSheet(sheetName);
            sheet.groupRow(beginRow, endRow);
          //  sheet.setRowGroupCollapsed(beginRow+1, false);
            sheet.setRowSumsBelow(false);
            sheet.setRowSumsRight(true);
            
            FileOutputStream writeFile = new FileOutputStream(excelPath);  
            wb.write(writeFile);  
            writeFile.close();  
            
           
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
    public static void main(String[] args) {
   //CommonTools.createFile(new File("C:/Users/Wendy/Desktop/Android_nextPlus_TestReport_20151210_17_30_04.997.xls").getAbsoluteFile().getPath().toString().replace(new File("C:/Users/Wendy/Desktop/Android_nextPlus_TestReport_20151210_17_30_04.997.xls").getName().toString(), ""));
       // CommonTools.createFile();
     ExcelUtils.getSheet("C:/Users/Wendy/Desktop/Android_NextPlus_HK_TestReport_20151211_10_48_34.023.xls", "TestSummary");
        ExcelUtils.writeData(0,30, "test");
        ExcelUtils.close();
        setGroups("TestSummary", "C:/Users/Wendy/Desktop/Android_NextPlus_HK_TestReport_20151211_10_48_34.023.xls", 14, 20);
        setGroups("TestSummary", "C:/Users/Wendy/Desktop/Android_NextPlus_HK_TestReport_20151211_10_48_34.023.xls", 5, 11);
    }
}
