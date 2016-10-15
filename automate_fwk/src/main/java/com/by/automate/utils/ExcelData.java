package com.by.automate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelData {

    private static String navigation[] = { "Summary", "Tests", "Pass", "Failed", "Skip", "Errors",
            "Success rate", "Time" };
    private static String classNavigation[] = { "Classes", "MethodName", "Pass", "Failed", "Skip",
            "Errors", "Success rate", "Time ", "log", "Error Screenshot", "Comment" };

    private static Workbook readExcel(String excelPath) {
        try {
            InputStream is = new FileInputStream(excelPath);
            return Workbook.getWorkbook(is);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private static Sheet getSheet(String className, String excelPath) {
        try {
            Workbook work = readExcel(excelPath);

            return work.getSheet(className);
        } catch (Exception e) {
            throw new NullPointerException("sheet(" + className
                    + " is not found please check TestData.properties file.)");
        }

        /*
         * Sheet[] sheets = work.getSheets(); if (sheets.length > 0) { String[]
         * sheetNames = work.getSheetNames(); for (int i = 0; i <
         * sheetNames.length; i++) { if (StringUtils.equals(sheetNames[i],
         * className)) { return work.getSheet(i); } } }
         */
        // return work.getSheet(0);

    }

    private static Map<Integer, String> getDataColToIndexMap(String className, String excelPath) {
        Map<Integer, String> rtn = new HashMap<Integer, String>();
        Sheet sheet = getSheet(className, excelPath);
        int sheetColumns = sheet.getColumns();
        int sheetRowCount = sheet.getRows();
        if (sheetRowCount == 0) {
            throw new IllegalStateException("There is now row found in excel file [" + excelPath
                    + "], can't " + "generate map from column name to column index. ");
        }
        for (int i = 0; i < sheetColumns; i++) {
            rtn.put(i, sheet.getCell(i, 0).getContents());
        }
        return rtn;
    }

    public static List<Map<String, String>> getRowsMap(String className, String excelPath) {
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        Sheet sheet = getSheet(className, excelPath);
        Map<Integer, String> rtn = new HashMap<Integer, String>();

        int sheetRowCount = sheet.getRows();
        int sheetColumns = sheet.getColumns();

        rtn = getDataColToIndexMap(className, excelPath);
        // get title on the columns

        for (int i = 1; i < sheetRowCount; i++) {
            Map<String, String> oneRow = new HashMap<String, String>();
            for (int j = 0; j < sheetColumns; j++) {

                String key = rtn.get(j);
                String value = sheet.getCell(j, i).getContents();
                oneRow.put(key, value);

            }

            data.add(oneRow);
        }
        return data;
    }

    public static void setValueToExcel(String className, String path, String no, String value) {
        Sheet sheet = getSheet(className, path);
        int row = sheet.getRows();
        for (int i = 0; i < row; i++) {
            System.out.println(sheet.getCell(0, i).getContents());
            if (sheet.getCell(0, i).getContents().equals(no)) {
                System.out.println(sheet.getCell(1, i).getContents());
                Label l = (Label) sheet.getCell(1, i);
                l.setString(value);
                break;
            }
        }

    }

    public static void createSheet(String excelPath, String sheetName, int index) throws IOException, WriteException, BiffException {
        Workbook wb = Workbook.getWorkbook(new File(excelPath));
        WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
        wbe.createSheet(sheetName, index);
        WritableSheet desSheet = wbe.getSheet("TestSummary");

        WritableHyperlink link = new WritableHyperlink(0, 0, "Back", desSheet, 0, 0);
        wbe.getSheet(sheetName).addHyperlink(link);
        wbe.write();
        wbe.close();
        wb.close();
    }

    public static void createWorkbook(String excelPath, String sheetName, int index)
            throws IOException, WriteException, BiffException {
        WritableWorkbook wb = Workbook.createWorkbook(new File(excelPath));
        wb.createSheet(sheetName, index);

        WritableSheet homePageSheet = wb.getSheet(sheetName);
        Label label = new Label(0, 0, "Description");
        homePageSheet.addCell(label);

        // 概况 总数量 pass fail skip error 百分百 log
        for (int i = 0; i < navigation.length; i++) {
           
            homePageSheet.addCell(new Label(i, 1, navigation[i]));
        }

        for (int i = 0; i < classNavigation.length; i++) {
            homePageSheet.addCell(new Label(i, 3, classNavigation[i]));
        }
        wb.write();
        wb.close();
    }

    public static void deleteSheet(String excelPath, String name) throws BiffException,
            IOException, WriteException {
        Workbook wb = Workbook.getWorkbook(new File(excelPath));
        WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
        String[] sheetNames = wbe.getSheetNames();
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < sheetNames.length; i++) {
            String j = Integer.toString(i);
            map.put(sheetNames[i], j);
        }
        for (String sheetName : sheetNames) {
            if (sheetName.contains(name)) {
                wbe.removeSheet(Integer.parseInt(map.get(sheetName)));
            }

        }
        wbe.write();
        wbe.close();
        wb.close();
    }

    public static void writeContent(String excelPath, String className, int cow, int row,
            Object content) throws BiffException, IOException, RowsExceededException,
            WriteException {
        Workbook wb = null;
        wb = Workbook.getWorkbook(new File(excelPath));
        WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
        WritableSheet sheet = wbe.getSheet(className);
        Label lbl = new Label(cow, row, (String) content);
        sheet.addCell(lbl);
        wbe.write();
        wbe.close();
    }

    public static void writeLastRow(String excelPath, String className, int cow, Object content)
            throws BiffException, IOException, RowsExceededException, WriteException {
        Workbook wb = Workbook.getWorkbook(new File(excelPath));
        WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
        WritableSheet sheet = wbe.getSheet(className);
        int row = sheet.getRows();
        Label lbl = new Label(cow, row, (String) content);
        sheet.addCell(lbl);
        wbe.write();
        wbe.close();

    }

    public static void main(String[] args) {
        setValueToExcel(
                "testSetValue",
                "E:/Userso/jiangsikai/workspace/cms-autotest/src/test/resources/data/testData/createStoryWithAllFieldsData.xls",
                "2", "123");
        /*
         * ExcelData data = new ExcelData();
         * 
         * @SuppressWarnings("static-access") List<Map<String, String>> data2 =
         * data.getRowsMap("CreateStory",
         * "C:/Users/jiangsikai01/Desktop/createStoryData.xls"); for (int i = 0;
         * i < data2.size(); i++) { String value =
         * data2.get(i).get("headValue"); System.out.println(value);
         * if(value.contains("//d")){
         * 
         * value = value.replace("//d"," ") + System.currentTimeMillis();
         * System.out.println(value); } }
         */

        String s = "bc2132d";

        System.out.println(s.matches(".*?b.*?d.*?"));

    }
}
