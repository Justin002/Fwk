package com.by.automate.utils.testData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.by.automate.utils.CommonTools;

public class TestCasesDataGtter {

	private static XSSFWorkbook workbook = null;
	private static Sheet sheet = null;
	private final static int cloumn = 6;
	
	//private static String xlsxPath =  CommonTools.getCustomReportPath() + "AutomationTestCaseGis.xlsx";
	// Gis_Cases.xlsx
	@SuppressWarnings("unused")
	public static void readExcelXSLX(String excelPath, String sheetName) {

		try {
			InputStream is = new FileInputStream(excelPath);
			if (is == null) {
				try {
					throw new RuntimeException("Test data file: [" + excelPath + "] "
							+ "could not be found, please make sure you already create this file in ["
							+ TestCasesDataGtter.class.getResource("").toURI().getPath() + "]. ");
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}

			workbook = new XSSFWorkbook(is);
			sheet = workbook.getSheet(sheetName);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*public static void setCaseId(String setId, Class<?> cls, String methodName) {
		getTestCasesAndSave(setId, cls.getSimpleName());
	}*/

	public static int getRow() {

		return sheet.getPhysicalNumberOfRows();
	}

	public static int getColumn() {

		return 2;
	}

	public static String getValue(int row, int column) {
		Row hhsrow = sheet.getRow(row);
		Cell col = hhsrow.getCell(column);
		return col.getStringCellValue().trim();
	}

	/**
	 * 获取excel 所有合并单元格的个数
	 * 
	 * @return
	 */
	public static int getNumMergedRegions() {
		return sheet.getNumMergedRegions();
	}

	/**
	 * 起始行
	 * 
	 * @param args
	 */
	public static int getMergedFirstRow(int index) {
		CellRangeAddress cellRange = sheet.getMergedRegion(index);
		return cellRange.getFirstRow();
	}

	/**
	 * 结束行
	 * 
	 * @param index
	 * @return
	 */
	public static int getMergedLastRow(int index) {
		CellRangeAddress cellRange = sheet.getMergedRegion(index);
		return cellRange.getLastRow();
	}

	public static int getMergedIndex(String id) {

		int allMergedCell = getNumMergedRegions();
		boolean falg = false;
		for (int i = 0; i < allMergedCell; i++) {
			CellRangeAddress region = sheet.getMergedRegion(i); //
			int colIndex = region.getFirstColumn(); // 合并区域首列位置
			int rowNum = region.getFirstRow(); // 合并区域首行位置
			String value = sheet.getRow(rowNum).getCell(colIndex).getStringCellValue();
			if (id.equals(value)) {
				falg = true;
				return i;
			}
		}
		
		if(!falg){
			throw new Error("!Error, Not found case id '" + id + "' from sheet name " + sheet.getSheetName() + "." );
		}
		return 0;
	}


	
	public static void getTestCasesAndSave(String id,String excelPath, String sheetName,String writePath) {
		
		readExcelXSLX(excelPath, sheetName);
		int index = getMergedIndex(id);
		int row = getMergedFirstRow(index);
		int endRow = getMergedLastRow(index);
		String caseId = "Case ID:" + "\n";
		String Step = "Step:" + "\n";
		String title = "Title:" + "\n";
		String testPoint = "Test Point:" + "\n";
		for (int i = row; i <= endRow; i++) {
			String title2 = getValue(i, 1);
			String step = getValue(i, 2);
			String caseId2 = getValue(i, 0);
			if (!step.equals("")) {
				Step += "  " + step + "\n";
			}
			if (!title2.equals("")) {
				title += "  " + title2 + "\n";
			}
			if (!caseId2.equals("")) {
				caseId += "  " + caseId2 + "\n";
			}
			testPoint += "  " + getValue(i, 3) + "\n";
		}
		
		File file = new File(writePath + ".txt");
		if(!file.exists()){
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CommonTools.writeDate(writePath + ".txt",
				caseId + "\n" + title + "\n" + Step + "\n" + testPoint);
		/*CommonTools.writeDate(CommonTools.getCustomReportPath() + "gis/report/step/" + className + ".txt",
				caseId + "\n" + title + "\n" + Step + "\n" + testPoint);*/
	}
/*
	public static void main(String[] args) {
		getTestCasesAndSave("C169019","test_1");
	}*/

}
