package com.by.automate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Workbook;
import jxl.biff.drawing.ComboBox;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtils {

    public static String excelPath = "";
    public static Workbook wb;
    public static WritableWorkbook wbe;
    public static WritableSheet currentSheet;

    public static void createExcel(String path, String sheetName) {
        if (new File(path).exists()) {
            new File(path).delete();
        }

        try {
            wbe = Workbook.createWorkbook(new File(path));
            wbe.createSheet(sheetName, 0);
            wbe.write();
            wbe.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        excelPath = path;

    }

    public static void createSheet(String path, String sheetName, int index) {

        try {
            wb = Workbook.getWorkbook(new File(excelPath));
            wbe = Workbook.createWorkbook(new File(excelPath), wb);
            currentSheet = wbe.createSheet(sheetName, index);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        excelPath = path;

    }
    

    public static void openSheet(String sheetName) {

        try {
            wb = Workbook.getWorkbook(new File(excelPath));
            wbe = Workbook.createWorkbook(new File(excelPath), wb);
            currentSheet = wbe.getSheet(sheetName);

        } catch (Exception e) {
        }
    }

    public static WritableSheet getSheet(String path, String sheetName) {
        try {

            excelPath = path;
            wb = Workbook.getWorkbook(new File(excelPath));
            wbe = Workbook.createWorkbook(new File(excelPath), wb);
            currentSheet = wbe.getSheet(sheetName);
        } catch (Exception e) {

        }

        return currentSheet;
    }

    public static void close() {
        try {
            wbe.write();
            wbe.close();
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPageNoClose(String pageName) {
        if (new File(excelPath).exists()) {
            try {
                wb = Workbook.getWorkbook(new File(excelPath));
                wbe = Workbook.createWorkbook(new File(excelPath), wb);
                wbe.createSheet(pageName, 999);
                currentSheet = wbe.getSheet(pageName);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }

    /**
     * 
     * @param cow
     *            行 row 列。
     * @param content
     */
    public static void writeLastRow(int cow, Object content) {

        try {
            int row = currentSheet.getRows();
            Label lbl = new Label(cow, row, (String) content);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static int getRow() {
        return currentSheet.getRows();
    }

    public static int getCol() {

        return currentSheet.getColumns();
    }

    public static String[] getSheetNameItem() {

        return wbe.getSheetNames();

    }

    public static void writeLastCow(int row, Object content) {

        try {
            int cow = currentSheet.getColumns();
            Label lbl = new Label(cow, row, (String) content);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeLastRow(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = currentSheet.getRows();
            Label lbl = new Label(cow, row, (String) content, wcf);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameRow(int cow, Object content) {

        try {
            int row = currentSheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeSameRow(int cow, Object content, WritableCellFormat wcf) {

        try {
            int row = currentSheet.getRows();
            Label lbl = new Label(cow, row - 1, (String) content, wcf);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeData(int cow, int row, Object content) {

        try {
            Label lbl = new Label(cow, row, (String) content);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void writeData(int cow, int row, Object content, WritableCellFormat wcf) {

        try {
            String content2 = "";

            if (content instanceof Integer) {
                content2 = content + "";
            } else if (content instanceof Float) {
                content2 = (content) + "";
            } else
                content2 = (String) content;
            Label lbl = new Label(cow, row, content2, wcf);
            currentSheet.addCell(lbl);
        } catch (WriteException e) {

            e.printStackTrace();
        }
    }

    public static void mergerCell(int beginCol, int beginRow, int endCol, int endRow) {
        try {
            currentSheet.mergeCells(beginCol, beginRow, endCol, endRow);

        } catch (WriteException e) {

        }
    }

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap, String bgColor) {
        return addStyle(fontSize, isBold, isCenter, isWrap, bgColor, "black");
    }

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap,
            String bgColor, String textColor) {
        return addStyle(fontSize, isBold, isCenter, isWrap, bgColor, textColor, "black");
    }

    public static WritableCellFormat addStyle(int fontSize, boolean isBold, boolean isCenter, boolean isWrap,
            String bgColor, String textColor, String borderColor) {

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

            cell.setBorder(Border.ALL, BorderLineStyle.THIN, getColour(borderColor)); // 边框
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
        colours.put("grey80", Colour.GRAY_80);
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
        colours.put("brown", Colour.BROWN);
        colours.put("orange", Colour.ORANGE);
        colours.put("bluegrey", Colour.BLUE_GREY);
        colours.put("lavender", Colour.LAVENDER);
        colours.put("blue", Colour.BLUE);
        colours.put("lightBlue", Colour.LIGHT_BLUE);
        return colours.get(ys);
    }

    public static void setCellSize(int col, int row, int x, int y) {
        try {
            // String value = currentSheet.getCell(col,
            // row).getContents().toString();
            currentSheet.setColumnView(col, x);
            currentSheet.setRowView(row, y, false);
        } catch (RowsExceededException e) {
            e.printStackTrace();
        }
    }

    public static WritableCellFeatures addColumnList(List<String> contentArray) {
        // Create cobobox
        ComboBox cb = new ComboBox();
        // create WritableCellFeatures for the combo option.
        WritableCellFeatures cellFeatures = new WritableCellFeatures();
        cellFeatures.getDVParser();
        // bind list with WritableCellFeatures
        cellFeatures.setDataValidationList(contentArray);
        // Now bind cobobox with WritableCellFeatures
        cellFeatures.setComboBox(cb);
        return cellFeatures;
    }

    public static void setFeatures(int row, String content) {
        List<String> contentArray = new ArrayList<String>();
        contentArray.add("css");
        contentArray.add("id");
        contentArray.add("name");
        contentArray.add("xpath");
        try {
            Label label = new Label(3, row, content, addStyle(10, false, true, false, "white"));
            label.setCellFeatures(addColumnList(contentArray));
            // 設置默認選擇 的sheet
            // currentSheet.getSettings().setSelected(true);
            currentSheet.addCell(label);
        } catch (Exception e) {
        }

    }

    public static String getValue(int col, int row) {
        try {
            return currentSheet.getCell(col, row).getContents();
        } catch (Exception e) {

        }
        return "";

    }

    public static void writeImageToExcel(String iconPath, double begincol, double row, double d, double strideRow) {

        File fileImage = new File(iconPath);
        WritableImage image = new WritableImage(begincol, row, d, strideRow, fileImage);// 从A1开始

        currentSheet.addImage(image);
    }

    public static void writeImageExcel(String excePath, String sheetName, String iconPath, int col, int row)
            throws Exception {
        // 一定要流的形式创建这个对象
        wb = Workbook.getWorkbook(new File(excelPath));
        wbe = Workbook.createWorkbook(new FileOutputStream(new File(excePath)), wb);

        currentSheet = wbe.getSheet(sheetName);
        File image = new File(iconPath);

        java.awt.image.BufferedImage bi7 = null;
        try {
            bi7 = javax.imageio.ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int picWidth = bi7.getWidth(); // 图片宽, 像素 150
        int picHeight = bi7.getHeight(); // 图片高, 像素 105

        // test
        System.out.println("--1703");
        System.out.println("Width=" + picWidth);
        System.out.println("Height=" + picHeight);

        // 输入参数, 图片显示的位置
        double picBeginCol = 1.2;
        double picBeginRow = 1.2;

        // 计算参数( picCellWidth, picCellHeight ), 图片显示大小, 默认 100% 显示: begin
        // 图片cell宽度 = 图片实际跨越每个cell所占长度的相对各个cell ratio的和
        // 方法: 根据起始位置,计算图片实际跨越的区域, 然后计算相对ratio,然后累加
        //
        double picCellWidth = 0.0; // 是 cell的跨越个数, 可小数
        double picCellHeight = 0.0;
        // wc = ws.getWritableCell( picBeginCol, picBeginRow ); // 列,行
        // ws.getColumnView( picBeginCol ).getSize();
        // ws.getRowView( picBeginRow ).getSize();

        int _picWidth = picWidth * 32; // pic的宽度,循环递减, 是jxl的宽度单位, 32/15
        for (int x = 0; x < 1234; x++) {
            int bc = (int) Math.floor(picBeginCol + x); // 3.6 to 3 //
                                                        // 本次循环所在cell位置
            System.out.println("x =" + x); // test
            System.out.println("bc =" + bc); // test
            int v = currentSheet.getColumnView(bc).getSize(); // 本次cell宽,jxl单位
            double _offset0 = 0.0; // >= 0 // 离左边的偏移量, 仅 x = 0 的时候才用
            if (0 == x)
                _offset0 = (picBeginCol - bc) * v; //

            System.out.println("_offset0  =" + _offset0); // test
            System.out.println("_picWidth =" + _picWidth); // test
            System.out.println("v =" + v); // test
            System.out.println("cw 00=" + currentSheet.getColumnView(0).getSize()); // test
            System.out.println("ch 00=" + currentSheet.getRowView(0).getSize()); // test
            System.out.println("cw 22=" + currentSheet.getColumnView(2).getSize()); // test
            System.out.println("ch 22=" + currentSheet.getRowView(2).getSize()); // test

            if (0.0 + _offset0 + _picWidth > v) // _picWidth 剩余长度超过一个cell时
            {
                // 计算本次cell内, pic 所占 ratio值, 累加到 picCellWidth
                double _ratio = 1.0;
                if (0 == x)
                    _ratio = (0.0 + v - _offset0) / v;
                System.out.println("_ratio =" + _ratio); // test
                // picCellWidth += 1.0;
                picCellWidth += _ratio;
                _picWidth -= (int) (0.0 + v - _offset0); // int
            } else // _picWidth 剩余长度在一个cell内时
            {
                double _ratio = 0.0;
                if (v != 0)
                    _ratio = (0.0 + _picWidth) / v;
                picCellWidth += _ratio;
                System.out.println("for: picCellWidth =" + picCellWidth); // test
                break;
            }
            if (x >= 1233) {
            }
        } // for
          // 此时 picCellWidth 是图片实际的值了

        //
        int _picHeight = picHeight * 15; // pic的高度,循环递减, 是jxl的高度单位, 32/15
        for (int x = 0; x < 1234; x++) {
            int bc = (int) Math.floor(picBeginRow + x); // 3.6 to 3 //
                                                        // 本次循环所在cell位置
            int v = currentSheet.getRowView(bc).getSize(); // 本次cell高,jxl单位
            double _offset0 = 0.0; // >= 0 // 离顶部的偏移量, 仅 x = 0 的时候才用
            if (0 == x)
                _offset0 = (picBeginRow - bc) * v; //
            if (0.0 + _offset0 + _picHeight > v) // _picHeight 剩余长度超过一个cell时
            {
                // 计算本次cell内, pic 所占 ratio值, 累加到 picCellHeight
                double _ratio = 1.0;
                if (0 == x)
                    _ratio = (0.0 + v - _offset0) / v;
                // picCellHeight += 1.0;
                picCellHeight += _ratio;
                _picHeight -= (int) (0.0 + v - _offset0); // int
            } else // _picHeight 剩余长度在一个cell内时
            {
                double _ratio = 0.0;
                if (v != 0)
                    _ratio = (0.0 + _picHeight) / v;
                picCellHeight += _ratio;
                break;
            }
            if (x >= 1233) {
            }
        } // for
          // 此时 picCellHeight 是图片实际的值了
          // 计算参数( picCellWidth, picCellHeight ), 图片显示大小, 默认 100% 显示: end

        // test
        System.out.println("picBeginCol=" + picBeginCol);
        System.out.println("picBeginRow=" + picBeginRow);
        System.out.println("picCellWidth=" + picCellWidth);
        System.out.println("picCellHeight=" + picCellHeight);

        WritableImage wimage = new WritableImage(col, row, 79, 90, image);
        // WritableImage wimage = new WritableImage(0, 0, 3, 2.3, image);
        // WritableImage wimage1 = new WritableImage(3, 0, 3, 2.3, image);
        currentSheet.addImage(wimage);

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

    public static void setBg() {

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                writeData(i, j, "", addStyle(10, false, false, false, "white", "white", "white"));
            }
        }
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 16; j++) {
                writeData(i, j, "", addStyle(10, false, false, false, "white", "white"));
            }
        }

    }
    
   

}
