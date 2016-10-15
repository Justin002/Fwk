/*package com.by.automate.tools;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.by.automate.base.core.InitiaFramework;
import com.by.automate.utils.CommonTools;
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

public class reportToolOfExcel_lod {

    protected static Workbook wb;
    protected static WritableWorkbook wbe;
    protected static WritableSheet sheet;
    private static String navigation[] = { "Summary", "Tests", "Pass", "Failed", "Skip", "Errors", "Success rate", "Duration(m)" };
    private static String classNavigation[] = { "Test Set", "Test", "Pass", "Failed", "Skip", "Error", "Status", "Success rate", "Duration(m)",
            "Tests Log", "Error Screenshot", "Comment" };

    *//**
     * 创建Excel 保存log信息. 在创建时候会初始化固定模板. 如果Excel存在追加信息,如果不存在则创建新的.
     * 
     * @param excelPath
     *            Excel 保存log 的路径. 在这里是"/cms-autotest/custom_report".
     * @param sheetName
     *            创建Excel 时候需要添加一个Sheet.
     * @param index
     *//*
    public static void createWorkbook(String excelPath, String sheetName, int index) {

        try {
            if (!new File(excelPath).exists()) {
                WritableWorkbook wb = Workbook.createWorkbook(new File(excelPath));
                wb.createSheet(sheetName, index);
                WritableSheet homePageSheet = wb.getSheet(sheetName);

                Label label = new Label(0, 0, "Description", addStyle(15, true, true, false, "periwinkle"));
                homePageSheet.addCell(label);
                setCellSize(homePageSheet, 0, 0, 14, 600);
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

                    homePageSheet.addCell(new Label(i, 1, navigation[i], addStyle(14, true, true, false, ys)));

                    setCellSize(homePageSheet, i, 1, 8, 600);
                }
                for (int i = 0; i < classNavigation.length; i++) {
                    String ys = "periwinkle";
                    
                     * if (i == 2) { ys = "green"; } else if (i == 3) { ys =
                     * "red"; } else if (i == 4) { ys = "gold"; } else if (i ==
                     * 5) { ys = "darkRed"; }
                     
                    homePageSheet.addCell(new Label(i, 3, classNavigation[i], addStyle(12, true, true, false, ys)));

                    setCellSize(homePageSheet, i, 3, 8, 600);

                }
                wb.write();
                wb.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    *//**
     * 根据ClassName 在excel 中创建对应得Sheet名字,做log 链接.
     * 
     * @param excelPath
     * @param sheetName
     *            sheetName == className.
     * @param linkSheetName
     *            sheetName == testResult
     * @param index
     *//*
    public static void createSheet(String excelPath, String sheetName, String linkSheetName, int index) {
        Workbook wb;
        try {
            wb = Workbook.getWorkbook(new File(excelPath));
            WritableWorkbook wbe = Workbook.createWorkbook(new File(excelPath), wb);
            wbe.createSheet(sheetName, index);
            WritableSheet desSheet = wbe.getSheet(linkSheetName);

            WritableHyperlink link = new WritableHyperlink(0, 0, "Back", desSheet, 0, 0);
            wbe.getSheet(sheetName).addHyperlink(link);
            wbe.getSheet(sheetName).addCell(new Label(0, 0, "Back", addStyleLink(14, true, true, false, "white")));
            wbe.getSheet(sheetName).addCell(new Label(1, 0, sheetName + "_Log", addStyleLink(14, true, true, false, "white")));
            setCellSize(wbe.getSheet(sheetName), 0, 0, 12, 600);

            wbe.write();
            wbe.close();
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    *//**
     * 打开sheet 对sheet 进行操作.
     * 
     * @param excelPath
     * @param sheetName
     *//*
    public static void openSheet(String excelPath, String sheetName) {

        try {
            wb = Workbook.getWorkbook(new File(excelPath));

            wbe = Workbook.createWorkbook(new File(excelPath), wb);
            sheet = wbe.getSheet(sheetName);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    *//**
     * 
     * @param cow
     * @param content
     *//*
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
            setCellSize(sheet, cow, row, 10, 500);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameDescription(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = sheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content, wcf);
            sheet.addCell(lbl);
            setCellSize(sheet, cow, row - 1, 10, 650);
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

        try {
            Label lbl = new Label(cow, row, (String) content);
            sheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeData(int cow, int row, Object content, WritableCellFormat wcf) {

        try {
            Label lbl = new Label(cow, row, (String) content, wcf);
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

    @SuppressWarnings("unused")
    public static void writeDataToExcel(List<Map<String, String>> ls) {
        // 获取 监听后的数据.
        String className = ls.get(0).get("ClassName");
        int countTests = ls.size();
        int row = 0;
        int cloumns = 0;
        // 统计当前class method 通过的个数。
        int rateMethod = 0;
        for (int i = 0; i < ls.size(); i++) {

            Map<String, String> result = new HashMap<String, String>();
            result = ls.get(i);

            String methodName = "", comment = "", image = "", imageName = "", pass = "0", fail = "0", skip = "0", error = "0";
            float time;
            // 获取当前method 运行状态, pass, failed, skip, error.
            String status = result.get("status");

            if (status.equals("SUCCESS")) {
                pass = "1";
            } else if (status.equals("FAILURE")) {
                fail = "1";
            } else if (status.equals("SKIP")) {
                skip = "1";
            } else if (status.isEmpty()) {
                error = "1";
            }

            // 得到Method 名字.
            methodName = result.get("method");
            // 得到当前Method 运行消耗的时间,单位秒.

            DecimalFormat df = new DecimalFormat("######0.00");
            time = Float.parseFloat(result.get("time")) / 60000;
            // 获取Comment.
            comment = result.get("comment");
            // 获取图片名字
            imageName = result.get("imageName");
            // 统计class 运行成功method 的个数.作百分比.
            if (pass.equals("1")) {
                rateMethod++;
            }

            row = sheet.getRows() +1;
            cloumns = sheet.getColumns();
            if (i == 0) {
                writeData(0, row, className, addStyle(12, true, false, false, "white"));
                setCellSize(sheet, 0, row, 1, 950);
            }
            // 把值写入到Excel
            writeData(1, row, methodName, addStyle(12, false, false, false, "white"));
            setCellSize(sheet, 1, row, 1, 500);
            writeData(2, row, pass, addStyle(12, false, true, false, "white"));
            writeData(3, row, fail, addStyle(12, false, true, false, "white"));
            writeData(4, row, skip, addStyle(12, false, true, false, "white"));
            writeData(5, row, error, addStyle(12, false, true, false, "white"));
            writeData(6, row, pass.equals("1") ? "SUCCESS" : "FAILED", addStyle(10, true, true, true, pass.equals("1") ? "lime" : "red"));
            writeData(8, row, df.format(time), addStyle(12, false, true, false, "white"));
            // 添加图片链接.
            String newPath = "";
            if (status.equals("FAILURE") || status.equals("SKIP")) {

                String imagePath = getImagePath(imageName, className, result.get("appType"));
                setLinkToErrorScrenshot(10, row, imagePath);
            }

            writeData(11, row, comment, addStyle(12, false, false, false, "white"));
            setCellSize(sheet, 11, row, 4, 500);

            // 每写一个class 信息的时候 同时Summary 也会同步, 第一次写入class 信息时候，
            // 得到当前Summary信息相应加上新class 的信息， 最后得到的是Summary信息.
            setValueToSummary(pass, fail, skip, error, time);

        }
        // 合并 class
        mergerCell(0, row - countTests + 1, 0, row);
        // success Rate -- current class
        writeData(7, row - countTests + 1, (rateMethod * 100 / countTests) + "%", addStyle(12, true, true, false, "white"));
        mergerCell(7, row - countTests + 1, 7, row);

        // 添加log链接并合并.
        setHyperLinkForSheet(9, row - countTests + 1, className + "_Log", className, 1, 0);
        mergerCell(9, row - countTests + 1, 9, row);
        setCellSize(sheet, 9, row - countTests + 1, 0, 500);

        // tests -- summary
        int tests = getTestsValue(1, 2);
        writeData(1, 2, (tests + countTests) + "", addStyle(14, true, true, true, "white"));
        // Success Rate -- summary
        int passed = getTestsValue(2, 2);
        String successRate = (passed * 100 / (tests + countTests)) + "%";
        writeData(6, 2, successRate, addStyle(14, true, true, true, "white"));

        // 没统计一个class 记录就清空 里面的数据.
        ls.clear();
    }

    private static String getImagePath(String imageName, String className, String appType) {
        String returnVal = "";
        if (appType.equals("InstrumentDriverIOS")) {
            String name = className.contains(".") ? className.split("\\.")[1] : className;
            String oldPath = CommonTools.getTestRoot().replace("test-classes/", "") + "InstrumentDriver/log/" + name + "/Run 1/" + imageName + ".png";
            returnVal = CommonTools.getTestRoot().replace("target/test-classes/", "") + "custom_report/"+ InitiaFramework.appType + "/" + InitiaFramework.appName +"/screenCaptures/"
                    + imageName + ".png";
            // 复制图片到 allScreenshot文件加下面.
            ImageUtils.copyImage(oldPath, returnVal);

        }
        return appType + "../screenCaptures/" + imageName + ".png";

    }

    public static void setLinkToErrorScrenshot(int col, int row, String imagePath) {

        try {
            if (imagePath != null) {
                String formu = "HYPERLINK(\"" + imagePath + "\", \"" + imagePath.split("/")[imagePath.split("/").length - 1] + "\")";
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

    *//**
     * 超链接 跳转Sheet
     *
     * @param col
     * @param row
     * @param desc
     * @param sheetName
     * @param destCol
     * @param destRow
     *//*
    public static void setHyperLinkForSheet(int col, int row, String desc, String sheetName, int destCol, int destRow) {

        try {

            WritableSheet desSheet = wbe.getSheet(sheetName);
            WritableHyperlink link = new WritableHyperlink(col, row, desc, desSheet, destCol, destRow);

            sheet.addHyperlink(link);
            sheet.addCell(new Label(col, row, desc, addStyleLink(10, false, false, false, "white")));
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
     

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap, String bgColor) {

        try {
            WritableFont headFont = null;
            // 设置字体
            if (isBold) {
                headFont = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.BOLD);
            } else {
                headFont = new WritableFont(WritableFont.TIMES, fontSize, WritableFont.NO_BOLD);
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

    public static WritableCellFormat addStyleLink(int fontSize, boolean isBold, boolean isCenter, boolean isWrap, String bgColor) {

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

    public static void setCellSize(WritableSheet sheet, int col, int row, int x, int y) {
        try {
            String value = sheet.getCell(col, row).getContents().toString();
            sheet.setColumnView(col, new String(value).length() + x);

            sheet.setRowView(col, row + y, false);
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

    public static void main(String[] args) throws RowsExceededException, BiffException, WriteException, IOException {

        // createWorkbook("/Users/test01/Desktop/test.xls", "sheetView", 0);
        
         * openSheet("/Users/test01/Desktop/test.xls", "sheetView")  ; // %20,
         * setLinkToErrorScrenshot(1, 2, "FB3.jpg");
         * 
         * Hyperlink links[] = sheet.getHyperlinks(); for (int i = 0; i <
         * links.length; i++) {
         * System.out.println(links[i].getFile().getPath()); } close();
         
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
*/