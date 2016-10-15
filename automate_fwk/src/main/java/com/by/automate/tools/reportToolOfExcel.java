package com.by.automate.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.by.automate.utils.CommonTools;
import com.by.automate.utils.ExcelUtils;
import com.by.automate.utils.ImageUtils;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class reportToolOfExcel {

    protected static List<String> rows = new ArrayList<String>();
    protected static Workbook wb;
    protected static WritableWorkbook wbe;
    protected static WritableSheet sheet;
    private static String navigation[] = { "Summary", "Tests", "Pass", "Failed", "Skip", "Errors", "Success rate",
            "Duration(m)" };
    private static String classNavigation[] = { "Test Set", "Tests", "Pass", "Failed", "Skip", "Errors",
            "Status(Success rete)", "Duration(m)", "Error Screenshot", "Comment" };

    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy_MM_dd");
        return f.format(date);
    }

    /**
     * 创建Excel 保存log信息. 在创建时候会初始化固定模板. 如果Excel存在追加信息,如果不存在则创建新的.
     * 
     * @param excelPath
     *            Excel 保存log 的路径. 在这里是"/cms-autotest/custom_report".
     * @param sheetName
     *            创建Excel 时候需要添加一个Sheet.
     * @param index
     */
    public static void createWorkbook(String excelPath, String sheetName, int index) {

        CommonTools.getCurrentTime();
        try {
            if (!new File(excelPath).exists()) {
                WritableWorkbook wb = Workbook.createWorkbook(new File(excelPath));
                wb.createSheet(sheetName, index);
                WritableSheet homePageSheet = wb.getSheet(sheetName);
                sheet = homePageSheet;
                Label label = new Label(0, 0, "The automation test report(" + getCurrentTime() + ")", addStyle(18, true,
                        true, false, "lime"));
                homePageSheet.addCell(label);
                setCellSize(0, 0, 5, 900);
                mergerCell(0, 0, 7, 0);
                // 概况 总数量 pass fail skip error 百分百 log

                for (int i = 0; i < navigation.length; i++) {

                    String ys = "periwinkle";
                    if (i == 1) {
                        ys = "yellow";
                    } else if (i == 2) {
                        ys = "lime";
                    } else if (i == 3) {
                        ys = "red";
                    } else if (i == 4) {
                        ys = "gold";
                    } else if (i == 5) {
                        ys = "darkRed";
                    }

                    homePageSheet.addCell(new Label(i, 1, navigation[i], addStyle(12, true, true, false, ys)));
                    setCellSize(i, 1, 5, 600);
                }
                for (int i = 0; i < classNavigation.length; i++) {
                    String ys = "periwinkle";
                    if (i == 1) {
                        ys = "yellow";
                    } else if (i == 2) {
                        ys = "lime";
                    } else if (i == 3) {
                        ys = "red";
                    } else if (i == 4) {
                        ys = "gold";
                    } else if (i == 5) {
                        ys = "darkRed";
                    }
                    if (i == 9) {
                        homePageSheet.addCell(new Label(i, 3, classNavigation[i], addStyle(12, true, false, false, ys)));
                    } else
                        homePageSheet.addCell(new Label(i, 3, classNavigation[i], addStyle(12, true, true, false, ys)));

                    setCellSize(i, 3, 5, 500);

                }

                wb.write();
                wb.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ClassName 在excel 中创建对应得Sheet名字,做log 链接.
     * 
     * @param excelPath
     * @param sheetName
     *            sheetName == className.
     * @param linkSheetName
     *            sheetName == testResult
     * @param index
     */
    public static void createSheet(String excelPath, String sheetName, String linkSheetName, int index) {
        Workbook wb;
        try {
            wb = Workbook.getWorkbook(new File(excelPath));
            WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
            wbe.createSheet(sheetName, index);

            wbe.getSheet(sheetName).addCell(
                    new Label(1, 0, sheetName + "_Log", addStyleLink(14, true, true, false, "white")));

            wbe.write();
            wbe.close();
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBackHyperlink(int col, int row) {

        WritableHyperlink link = new WritableHyperlink(0, 0, "Back", wbe.getSheet("TestSummary"), col, row);
        try {
            setCellSize(0, 0, 12, 600);
            sheet.addHyperlink(link);
            sheet.addCell(new Label(0, 0, "Back", addStyleLink(14, true, true, false, "white")));
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开sheet 对sheet 进行操作.
     * 
     * @param excelPath
     * @param sheetName
     */
    public static void openSheet(String excelPath, String sheetName) {

        try {
            wb = Workbook.getWorkbook(new File(excelPath));

            wbe = Workbook.createWorkbook(new File(excelPath), wb);
            sheet = wbe.getSheet(sheetName);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 
     * @param cow
     * @param content
     */
    public static void writeLastRow(int cow, Object content) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row, (String) content);
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeDescript(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row, (String) content, wcf);
            sheet.addCell(lbl);
            setCellSize(cow, row, 10, 500);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameDescription(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content, wcf);
            sheet.addCell(lbl);
            setCellSize(cow, row - 1, 10, 650);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeLastRow(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row, (String) content, wcf);
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameRow(int cow, Object content) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content);
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameRow(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content, wcf);
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeData(int cow, int row, Object content) {
        Label lbl = null;
        try {
            if (content instanceof Integer || content instanceof Float) {
                lbl = new Label(cow, row, content + "");
            } else {

                lbl = new Label(cow, row, (String) content);
            }
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeData(int cow, int row, Object content, WritableCellFormat wcf) {

        Label lbl = null;
        try {
            if (content instanceof Integer || content instanceof Float) {
                lbl = new Label(cow, row, content + "", wcf);
            } else {

                lbl = new Label(cow, row, (String) content, wcf);
            }
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (wbe != null) {
                wbe.write();
                wbe.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static int getTestsValue(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents();
        if (value.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }

    }

    public static float getTimeValueFloat(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents().replace("m", "");
        if (value.isEmpty()) {
            return 0;
        } else {
            return Float.parseFloat(value);
        }

    }

    public static void setValueToSummary(String pass, String fail, String skip, String error, float time) {

        DecimalFormat df = new DecimalFormat("######0.00");
        // 得到当前Summary 信息 + class 统计后的信息.
        int SMPass = getTestsValue(2, 2) + Integer.parseInt(pass);
        int SMFail = getTestsValue(3, 2) + Integer.parseInt(fail);
        int SMSkip = getTestsValue(4, 2) + Integer.parseInt(skip);

        int SMError = getTestsValue(5, 2) + Integer.parseInt(error);
        float SMTime = getTimeValueFloat(7, 2) + time;

        writeData(2, 2, SMPass + "", addStyle(14, true, true, true, "white"));
        writeData(3, 2, SMFail + "", addStyle(14, true, true, true, "white"));
        writeData(4, 2, SMSkip + "", addStyle(14, true, true, true, "white"));
        writeData(5, 2, SMError + "", addStyle(14, true, true, true, "white"));
        writeData(7, 2, df.format(SMTime) + "m", addStyle(14, true, true, true, "white"));

    }

    public static int addEmptyRow() {
        int end = sheet.getRows();
        setCellSize(0, end, 5, 400);
        writeData(0, end, null, ExcelUtils.addStyle(10, false, true, false, "white"));
        return end;
    }

    public static int addEmptyMegerRow() {
        int end = sheet.getRows();
        mergerCell(0, end, 9, end);
        // setCellSize(0, end,5, 250);
        writeData(0, end, null, ExcelUtils.addStyle(10, false, true, false, "white"));
        return end;

    }

    @SuppressWarnings("unused")
    public static void writeDataToExcel(String excelPath, List<Map<String, String>> ls) {
        String text = "●";
        // String defaultColour = "iceBlue";
        DecimalFormat df = new DecimalFormat("######0.00");
        // 获取 监听后的数据.
        String className = ls.get(0).get("ClassName");
        int countTests = ls.size();
        int countClassInfo = 0;
        int row = 0;
        int cloumns = 0;
        // 统计当前class method 通过的个数。
        int countPass = 0;
        // 统计当前class method failed的个数。
        int countFailed = 0;
        // 统计当前class method skip的个数。
        int countSkip = 0;
        // 统计当前class method error的个数。
        int countError = 0;
        // / 統計當前Class 所花費時間
        float countTime = 0;
        // back row
        int backRow = 0;
        // 添加空行保存 method 統計信息， 需要在method 信息寫完後才能填寫信息,先保存結構在寫入
        countClassInfo = addEmptyRow();
        for (int i = 0; i < ls.size(); i++) {
            String passColour = "iceBlue";
            String failedColour = "iceBlue";
            String skipeColour = "iceBlue";
            String errorColour = "iceBlue";
            Map<String, String> result = new HashMap<String, String>();
            result = ls.get(i);

            String methodName = "", comment = "", image = "", imageName = "", pass = "0", fail = "0", skip = "0", error = "0";
            float time;
            // 获取当前method 运行状态, pass, failed, skip, error.
            // 统计class 运行成功method 的个数.作百分比.
            String status = result.get("status");

            if (status.equals("SUCCESS")) {
                passColour = "green";
                pass = "1";
                countPass++;
            } else if (status.equals("FAILURE")) {
                failedColour = "red";
                fail = "1";
                countFailed++;
            } else if (status.equals("SKIP")) {
                skipeColour = "yellow";
                skip = "1";
                countSkip++;
            } else if (status.isEmpty()) {
                errorColour = "darkRed";
                error = "1";
                countError++;
            }

            // 得到Method 名字.
            methodName = result.get("method");
            // 得到当前Method 运行消耗的时间,单位秒.

            time = Float.parseFloat(result.get("time")) / 60000;
            // 获取Comment.
            comment = result.get("comment");
            // 获取图片名字
            imageName = result.get("imageName");

            row = sheet.getRows();
            cloumns = sheet.getColumns();
            if (i == 0) {
                writeData(0, row, className, addStyle(10, true, false, false, "white"));
                setCellSize(0, row, 3, 550);
                backRow = row;
            }
            // 把值写入到Excel
            writeData(1, row, methodName, addStyle(10, false, false, false, "white"));
            setCellSize(1, row, 3, 320);
            writeData(2, row, text, addStyle(20, true, true, false, "white", passColour));
            writeData(3, row, text, addStyle(20, false, true, false, "white", failedColour));
            writeData(4, row, text, addStyle(20, false, true, false, "white", skipeColour));
            writeData(5, row, text, addStyle(20, false, true, false, "white", errorColour));
            writeData(6, row, pass.equals("1") ? "SUCCESS" : "FAILED",
                    addStyle(10, true, true, true, pass.equals("1") ? "lime" : "red"));
            writeData(7, row, df.format(time), addStyle(10, false, true, false, "white"));
            // 添加图片链接.
            String newPath = "";
            if (status.equals("FAILURE") || status.equals("SKIP")) {

                String imagePath = getImagePath(imageName, className, result.get("appType"));
                setLinkToErrorScrenshot(8, row, imagePath);
            } else {
                writeData(8, row, null, addStyle(10, false, true, false, "white"));
            }

            writeData(9, row, comment, addStyle(10, false, false, false, "white"));
            setCellSize(9, row, 5, 320);

            // 每写一个class 信息的时候 同时Summary 也会同步, 第一次写入class 信息时候，
            // 得到当前Summary信息相应加上新class 的信息， 最后得到的是Summary信息.
            setValueToSummary(pass, fail, skip, error, time);
            countTime += time;

        }
        // 合并 class
        mergerCell(0, row - countTests + 1, 0, row);

        // tests -- summary
        int tests = getTestsValue(1, 2);
        writeData(1, 2, (tests + countTests) + "", addStyle(14, true, true, true, "white"));
        // Success Rate -- summary
        int passed = getTestsValue(2, 2);
        String successRate = (passed * 100 / (tests + countTests)) + "%";
        writeData(6, 2, successRate, addStyle(14, true, true, true, "white"));

        // 統計 當前Class 所有的測試結果.
        String classNameLog = className;
        int countMethod = ls.size();
        String BFB = countPass * 100 / countMethod + "%";

        // 把當前Class 信息寫到 excel.
        setClassInfoToExcel(classNameLog, countMethod, countPass, countFailed, countSkip, countError, BFB,
                df.format(countTime), countClassInfo);
        // 没统计一个class 记录就清空 里面的数据.
        addEmptyMegerRow();

        close();
        openSheet(excelPath, className);
        setBackHyperlink(0, backRow);
        close();

        rows.add((row - countTests + 1) + "=" + row);

        // CommonTools.createFile(new
        // File(excelPath).getAbsoluteFile().getPath().toString().replace(new
        // File(excelPath).getName().toString(), ""));
        // CommonTools.writeProperties(excelPath, "", pValue);

        setGroupts("TestSummary", excelPath, rows);
        ls.clear();
    }

    private static void setClassInfoToExcel(String className, int countMethod, int countPass, int countFailed,
            int countSkip, int countError, String BFB, String countTime, int setRows) {
        // 添加log 連接
        setHyperLinkForSheet(0, setRows, className + "(LOG)", className, 1, 0);
        writeData(1, setRows, countMethod, addStyle(11, true, true, false, "grey25", "red"));
        writeData(2, setRows, countPass, addStyle(11, true, true, false, "grey25", "red"));
        writeData(3, setRows, countFailed, addStyle(11, true, true, false, "grey25", "red"));
        writeData(4, setRows, countSkip, addStyle(11, true, true, false, "grey25", "red"));
        writeData(5, setRows, countError, addStyle(11, true, true, false, "grey25", "red"));
        writeData(6, setRows, BFB, addStyle(11, true, true, false, "grey25", "red"));
        writeData(7, setRows, countTime, addStyle(11, true, true, false, "grey25", "red"));
        writeData(8, setRows, "NULL", addStyle(11, true, true, false, "grey25"));
        writeData(9, setRows, "NULL", addStyle(11, true, false, false, "grey25"));

    }

    private static String getImagePath(String imageName, String className, String appType) {
        String returnVal = "";
        if (appType.equals("instrumentDriver")) {
            String name = className.contains(".") ? className.split("\\.")[1] : className;
            String oldPath = CommonTools.getTestRoot().replace("test-classes/", "") + "InstrumentDriver/log/" + name
                    + "/Run 1/" + imageName + ".png";
            returnVal = CommonTools.getTestRoot().replace("target/test-classes/", "") + "custom_report/screenCaptures/"
                    + appType + "/" + imageName + ".png";
            // 复制图片到 allScreenshot文件加下面.
            ImageUtils.copyImage(oldPath, returnVal);

        }
        return "screenCaptures/" + appType + "/" + imageName + ".png";

    }

    public static void setLinkToErrorScrenshot(int col, int row, String imagePath) {

        try {
            if (imagePath != null) {
                String formu = "HYPERLINK(\"" + imagePath + "\", \"" + imagePath.split("/")[imagePath.split("/").length - 1]
                        + "\")";
                Formula formula = new Formula(col, row, formu, addStyleLink(12, false, false, false, "white"));
                sheet.addCell(formula);

            } else {
                sheet.addCell(new Label(col, row, "", addStyle(12, false, false, false, "white")));
            }
        } catch (WriteException e) {

            e.printStackTrace();
        }

    }

    public static void mergerCell(int beginCol, int beginRow, int endCol, int endRow) {
        try {
            sheet.mergeCells(beginCol, beginRow, endCol, endRow);

        } catch (WriteException e) {

        }
    }

    /**
     * 超链接 跳转Sheet
     *
     * @param col
     * @param row
     * @param desc
     * @param sheetName
     * @param destCol
     * @param destRow
     */
    public static void setHyperLinkForSheet(int col, int row, String desc, String sheetName, int destCol, int destRow) {

        try {

            WritableSheet desSheet = wbe.getSheet(sheetName);
            WritableHyperlink link = new WritableHyperlink(col, row, desc, desSheet, destCol, destRow);

            sheet.addHyperlink(link);
            sheet.addCell(new Label(col, row, desc, addStyleLink(10, false, false, false, "grey25")));
        } catch (WriteException e) {

            e.printStackTrace();
        }

    }

    // 如果sheet 存在就關閉它, 當 android & IOS 運行調用web 時候 會重新初始化excel 結構， CreateExcel
    // 有判斷如果excel 存在就跳過, 而創建sheet 如果sheet 存在就關閉輸入輸出流 然後在執行web 的寫入操作.
    public static boolean isSheetExist(String excelPath, String name) {
        boolean returnValue = false;
        try {

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
                    returnValue = true;
                    break;
                }

            }

            wbe.write();
            wbe.close();
            wb.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return returnValue;
    }

    public static void writeContent(String excelPath, String className, int cow, int row, Object content) {

        try {
            Workbook wb = Workbook.getWorkbook(new File(excelPath));
            WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
            WritableSheet sheet = wbe.getSheet(className);
            Label lbl = new Label(cow, row, (String) content);
            sheet.addCell(lbl);
            wbe.write();
            wbe.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /*
     * public static void writeLastRow(String excelPath, String className, int
     * cow, Object content) {
     * 
     * try { Workbook wb = Workbook.getWorkbook(new File(excelPath));
     * WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
     * WritableSheet sheet = wbe.getSheet(className); int row = sheet.getRows();
     * Label lbl = new Label(cow, row, (String) content); sheet.addCell(lbl);
     * wbe.write(); wbe.close(); } catch (BiffException | IOException |
     * WriteException e) {
     * 
     * e.printStackTrace(); }
     * 
     * }
     */

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap, String bgColor) {
        return addStyle(fontSize, isBold, isCenter, isWrap, bgColor, "black");
    }

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap,
            String bgColor, String textColor) {

        try {
            WritableFont headFont = null;
            // 设置字体
            if (isBold) {
                headFont = new WritableFont(WritableFont.ARIAL, fontSize, WritableFont.BOLD, false,
                        UnderlineStyle.NO_UNDERLINE, getColour(textColor));
            } else {
                headFont = new WritableFont(WritableFont.ARIAL, fontSize, WritableFont.NO_BOLD, false,
                        UnderlineStyle.NO_UNDERLINE, getColour(textColor));
            }

            WritableCellFormat cell = new WritableCellFormat(headFont);
            if (isCenter) {
                cell.setAlignment(Alignment.CENTRE);
                cell.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格内容垂直居中.
            } else {
                cell.setAlignment(Alignment.LEFT);
                cell.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格内容垂直居中.
            }

            cell.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); // 边框
            // 是否换行
            cell.setWrap(isWrap);
            WritableCellFormat wcf = new WritableCellFormat(cell);// 单元格样式.
            wcf.setBackground(getColour(bgColor));
            return wcf;
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static WritableCellFormat addStyleLink(int fontSize, boolean isBold, boolean isCenter, boolean isWrap,
            String bgColor) {

        try {
            WritableFont headFont = null;
            // 设置字体
            if (isBold) {
                headFont = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD, false, UnderlineStyle.SINGLE);

            } else {
                headFont = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.NO_BOLD, false, UnderlineStyle.SINGLE);
            }
            headFont.setColour(Colour.BLUE);

            WritableCellFormat cell = new WritableCellFormat(headFont);
            if (isCenter) {
                cell.setAlignment(Alignment.CENTRE);
                cell.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格内容垂直居中.
            } else {
                cell.setAlignment(Alignment.LEFT);
                cell.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格内容垂直居中.
            }

            cell.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK); // 边框
            // 是否换行
            cell.setWrap(isWrap);
            WritableCellFormat wcf = new WritableCellFormat(cell);// 单元格样式.
            wcf.setBackground(getColour(bgColor));
            return wcf;
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static void setCellSize(int col, int row, int x, int y) {
        try {
            String value = sheet.getCell(col, row).getContents().toString();
            sheet.setColumnView(col, new String(value).length() + x);
            sheet.setRowView(row, y, false);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        }
    }

    public static Colour getColour(String ys) {
        Map<String, Colour> colours = new HashMap<String, Colour>();

        colours.put("unknown", Colour.UNKNOWN);
        colours.put("black", Colour.BLACK);
        colours.put("white", Colour.WHITE);
        colours.put("defaultBackground", Colour.DEFAULT_BACKGROUND);
        colours.put("red", Colour.RED);
        colours.put("brightGreen", Colour.BRIGHT_GREEN);
        colours.put("brightGreen2", Colour.BLUE);
        colours.put("yellow", Colour.YELLOW);
        colours.put("pink", Colour.PINK);
        colours.put("turquoise", Colour.TURQUOISE);
        colours.put("darkRed", Colour.DARK_RED);
        colours.put("green", Colour.GREEN);
        colours.put("darkBlue", Colour.DARK_BLUE);
        colours.put("darkYellow", Colour.DARK_YELLOW);
        colours.put("violet", Colour.VIOLET);
        colours.put("teal", Colour.TEAL);
        colours.put("grey25", Colour.GREY_25_PERCENT);
        colours.put("grey50", Colour.GREY_50_PERCENT);
        colours.put("periwinkle", Colour.PERIWINKLE);
        colours.put("plum", Colour.PLUM);
        colours.put("ivory", Colour.IVORY);
        colours.put("lightTurquoise", Colour.LIGHT_TURQUOISE);
        colours.put("darkPurple", Colour.DARK_PURPLE);
        colours.put("coral", Colour.CORAL);
        colours.put("oceanBlue", Colour.OCEAN_BLUE);
        colours.put("iceBlue", Colour.ICE_BLUE);
        colours.put("darkBlue", Colour.DARK_BLUE);
        colours.put("skyBlue", Colour.SKY_BLUE);
        colours.put("paleBlue", Colour.PALE_BLUE);
        colours.put("gold", Colour.GOLD);
        colours.put("lime", Colour.LIME);

        return colours.get(ys);
    }

    public static void writeLogToExcel(List<List<String>> logData, String excelPath, String sheetName) {
        try {

            int currentRow = 1;
            List<Integer> firstCol = new ArrayList<Integer>();
            for (int i = 0; i < logData.size(); i++) {
                String title = logData.get(i).get(0);
                String content = logData.get(i).get(1);
                if (title == "") {
                    writeLastRow(0, title, addStyle(11, false, false, false, "white"));
                    writeSameRow(1, content, addStyle(11, false, false, false, "white"));

                } else {
                    currentRow = sheet.getRows();
                    firstCol.add(currentRow);
                    writeLastRow(0, title, addStyle(11, true, false, false, "skyBlue"));
                    writeSameDescription(1, content, addStyle(11, true, false, false, "skyBlue"));
                }
            }

            firstCol.add(sheet.getRows());
            if (logData.get(0).get(0) == "ClassName") {
                for (int i = 0; i < firstCol.size() - 3; i++) {
                    int endRow = firstCol.get(i + 3) - 1;
                    int startRow = firstCol.get(i + 2);
                    if (endRow > startRow) {
                        sheet.mergeCells(0, startRow, 0, endRow);
                    }
                }

            } else {
                for (int i = 0; i < firstCol.size() - 1; i++) {
                    int endRow = firstCol.get(i + 1) - 1;
                    int startRow = firstCol.get(i);
                    if (endRow > startRow) {
                        sheet.mergeCells(0, startRow, 0, endRow);
                    }
                }
            }
        } catch (Exception e) {
            close();
        }

        close();
        logData.clear();
    }

    public static void setGroupts(String sheetName, String excelPath, List<String> rows) {

        for (int i = 0; i < rows.size(); i++) {
            int beginRow = Integer.parseInt(rows.get(i).split("=")[0]);
            int endRow = Integer.parseInt(rows.get(i).split("=")[1]);
            if (i == 0) {
                usPOISetGroups(sheetName, excelPath, beginRow, endRow, false);
            } else
                usPOISetGroups(sheetName, excelPath, beginRow, endRow, true);
        }
    }

    public static void usPOISetGroups(String sheetName, String excelPath, int beginRow, int endRow, boolean isCollapsed) {
        HSSFSheet sheet = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(excelPath));
            HSSFWorkbook wb = new HSSFWorkbook(is);
            sheet = wb.getSheet(sheetName);
            sheet.groupRow(beginRow, endRow);

            sheet.setRowGroupCollapsed(beginRow + 1, isCollapsed);
            sheet.setRowSumsBelow(false);
            sheet.setRowSumsRight(true);

            FileOutputStream writeFile = new FileOutputStream(excelPath);
            wb.write(writeFile);
            writeFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws RowsExceededException, BiffException, WriteException, IOException {

        // createWorkbook("/Users/test01/Desktop/test.xls", "sheetView", 0);
        /*
         * openSheet("/Users/test01/Desktop/test.xls", "sheetView")  ; // %20,
         * setLinkToErrorScrenshot(1, 2, "FB3.jpg");
         * 
         * Hyperlink links[] = sheet.getHyperlinks(); for (int i = 0; i <
         * links.length; i++) {
         * System.out.println(links[i].getFile().getPath()); } close();
         */
        createSheet("/Users/test01/AutoTest/workspace/cms-autotest/custom_report/TestReport.xls", "TestSummary", "", 20);
        openSheet("/Users/test01/AutoTest/workspace/cms-autotest/custom_report/TestReport.xls", "TestSummary");

        setLinkToErrorScrenshot(10, 17, "screenCaptures/InstrumentDriverIOS/20151117_113606512.png");

        setLinkToErrorScrenshot(10, 23, "screenCaptures/InstrumentDriverIOS/20151117_112951802.png");

        setLinkToErrorScrenshot(10, 30, "screenCaptures/InstrumentDriverIOS/20151117_111810596.png");

        setLinkToErrorScrenshot(10, 35, "screenCaptures/InstrumentDriverIOS/20151117_114845044.png");

        setLinkToErrorScrenshot(10, 44, "screenCaptures/InstrumentDriverIOS/20151117_114818552.png");

        setLinkToErrorScrenshot(10, 48, "screenCaptures/InstrumentDriverIOS/20151117_134611373.png");

        setLinkToErrorScrenshot(10, 51, "screenCaptures/InstrumentDriverIOS/20151117_125738407.png");

        setLinkToErrorScrenshot(10, 71, "screenCaptures/InstrumentDriverIOS/20151117_121035762.png");

        setLinkToErrorScrenshot(10, 75, "screenCaptures/InstrumentDriverIOS/20151117_112471248.png");

        setLinkToErrorScrenshot(10, 100, "screenCaptures/InstrumentDriverIOS/20151117_120136107.png");

        setLinkToErrorScrenshot(10, 136, "screenCaptures/InstrumentDriverIOS/20151117_113433780.png");

        setLinkToErrorScrenshot(10, 141, "screenCaptures/InstrumentDriverIOS/20151117_113827451.png");

        wbe.write();
        wbe.close();
        System.out.println("end");

    }

}
