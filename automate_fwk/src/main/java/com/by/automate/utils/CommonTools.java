package com.by.automate.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.crypto.OutputLengthException;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * 
 * @author wangxun
 * @update Add Methods:
 * 
 */
public class CommonTools {

	private static String str = "abcdefghijklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWSYZ";
	private static String chares = "~!`@#$%^&*()_+|[]-=\\{};':\"<>?,./～！·＃￥％…＆＊（）—＋｜｀－＝、［］｛｝；‘：“，。／《》？";
	private static String number = "0123456789";
	public static String strEng;
	public static String strChinese;
	public static Long timeStamp;
	public static Sheet sheet;

	/**
	 * Get the current time.
	 * 
	 * @return - String indicates the current time with format "HH:mm:ss.SSS".
	 */
	public static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss.SSS");
		return f.format(date);
	}

	/**
	 * Get the current Date.
	 * 
	 * @return - String indicates the current Date with format "yyyy-MM-dd".
	 */

	public static String getDate() {

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	public static String getDateTimeStampString() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
	}
	/**
	 * Replace the illegal characters in file name.
	 * 
	 * @param fileName
	 *            The file name.
	 * 
	 * @param newChar
	 *            The replace char.
	 * 
	 * @return - the new filename without illegal character.
	 */

	public static String replaceIllegalFileName(String fileName, String newChar) {

		if (fileName != null) {

			fileName = fileName.replace("\\", newChar);
			fileName = fileName.replace("/", newChar);
			fileName = fileName.replace(":", newChar);
			fileName = fileName.replace("*", newChar);
			fileName = fileName.replace("?", newChar);
			fileName = fileName.replace("\"", newChar);
			fileName = fileName.replace("<", newChar);
			fileName = fileName.replace(">", newChar);
			fileName = fileName.replace("|", newChar);
		}

		return fileName;
	}

	/**
	 * Get project test root path.
	 * 
	 * @return project root path.
	 */
	public static String getTestRoot() {
		/*
		 * This is the root location to search for resources and tools. NOTE:
		 * getPath() returns a path beginning with "/C:/..." on Windows, so we
		 * strip the first char if needed.
		 */

		String testRoot = StringUtils.defaultString(Thread.currentThread()
				.getContextClassLoader().getResource(".").getPath());
		if (StringUtils.contains(testRoot, ":/"))
			testRoot = StringUtils.substring(testRoot, 1);

		return testRoot;
	}
	
	public static String getCustomReportPath() {

		return getTestRoot().replace("target/test-classes", "custom_report/Report");
	}


	/**
	 * Get value on the jsonNode
	 * 
	 * @param configJsonNode
	 *            Current josnNode object.
	 * @param propertieName
	 *            property name.
	 * @return retunrValue The value of the attribute name.
	 */

	public static String getConfigValue(Properties configProperties,
			String propertieName) {

		// if the specified propertieName exist as an environment variable.
		// if so , use it. otherwise , search the configJsonNode specified.
		String returnValue = StringUtils.defaultString(System
				.getenv(propertieName));
		if (StringUtils.isBlank(returnValue)) {
			try {
				returnValue = configProperties.getProperty(propertieName);
			} catch (NullPointerException e) {
				System.err.println("Cannot locate config file at "
						+ propertieName + ". Will try continue without it.");
				return "";
			}
		}
		return returnValue;
	}

	public static Properties getConfigFormatData(String configFileName) {
		try {
			Properties pro = new Properties();
			FileInputStream fis = new FileInputStream(configFileName);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader brProp = new BufferedReader(isr);
			pro.load(brProp);
			brProp.close();
			return pro;
		} catch (Exception e) {
			throw new IllegalStateException("Can't locate config file "
					+ configFileName, e);
		}
	}

	public static String getChar(int length) {
		return randomGeneratedSpecifiedChar("char", length, "");
	}

	public static String getChinese(int length, String pathFile) {
		return randomGeneratedSpecifiedChar("chinese", length, pathFile);
	}

	public static String getEnglish(int length) {
		return randomGeneratedSpecifiedChar("english", length, "");
	}

	public static String getCombineChar(int length, String pathFile) {
		return randomGeneratedSpecifiedChar("combine", length, pathFile);
	}

	public static String getNumber(int length) {
		return randomGeneratedSpecifiedChar("number", length, "");
	}

	public static String getENCH(int length, String pathFile) {
		return randomGeneratedSpecifiedChar("en_ch", length, pathFile);
	}

	private static String randomGeneratedSpecifiedChar(String type, int length,
			String chinesePath) {
		if (length == 0) {
			length = 15;
		}
		String returnValue = "";
		if (StringUtils.equals(type, "english")) {
			int strLen = str.length();
			for (int i = 0; i < length; i++) {
				int index = getRandom(strLen);
				returnValue += str.substring(index, index + 1);
			}
			return returnValue;
		}
		if (StringUtils.equals(type, "chinese")) {
			Map<Integer, String> map = readTxtFile(chinesePath);
			for (int i = 0; i < length; i++) {
				int index = getRandom(map.size());
				String value = map.get(index);
				index = getRandom(value.length());
				returnValue += value.substring(index, index + 1);

			}
			return returnValue;
		}
		if (StringUtils.equals(type, "char")) {

			int strLen = chares.length();
			for (int i = 0; i < length; i++) {
				int index = getRandom(strLen);
				returnValue += chares.substring(index, index + 1);
			}
			return returnValue;

		}
		if (StringUtils.endsWith(type, "number")) {
			int numlen = number.length();
			for (int i = 0; i < length; i++) {
				int index = getRandom(numlen);
				returnValue += number.substring(index, index + 1);
			}
			return returnValue;
		}
		if (StringUtils.equals(type, "combine")) {
			Map<Integer, String> map = readTxtFile(chinesePath);
			int strLen = str.length();
			int charLen = chares.length();
			int chLen = map.size();
			int k = length % 3;
			for (int i = 0; i < length / 3; i++) {
				int indexStr = getRandom(strLen);
				int indexChar = getRandom(charLen);

				int indexCh = getRandom(chLen);
				String value = map.get(indexCh);
				int l = value.length();
				l = getRandom(l);

				returnValue += str.substring(indexStr, indexStr + 1)
						+ chares.substring(indexChar, indexChar + 1)
						+ value.substring(l, l + 1);

			}
			if (k > 0) {
				for (int i = 0; i < k; i++) {
					int indexStr = getRandom(strLen);
					returnValue += str.substring(indexStr, indexStr + 1);
				}
			}
			return returnValue;
		}

		if (StringUtils.equals(type, "en_ch")) {
			Map<Integer, String> map = readTxtFile(chinesePath);
			int strLen = str.length();
			int chLen = map.size();
			int k = length % 2;
			for (int i = 0; i < length / 2; i++) {
				int indexStr = getRandom(strLen);

				int indexCh = getRandom(chLen);
				String value = map.get(indexCh);
				int l = value.length();
				l = getRandom(l);

				returnValue += str.substring(indexStr, indexStr + 1)
						+ value.substring(l, l + 1);

			}
			if (k > 0) {
				for (int i = 0; i < k; i++) {
					int indexStr = getRandom(strLen);
					returnValue += str.substring(indexStr, indexStr + 1);
				}
			}
			return returnValue;
		}
		return "";
	}

	public static int getRandom(int limit) {
		Random rd = new Random();
		int index = rd.nextInt(limit);
		return index == 0 ? 1 : index;
	}

	@SuppressWarnings("unchecked")
	public static Map<Integer, String> readTxtFile(String filePath) {
		Map<Integer, String> map = new HashedMap();
		try {
			File file = new File(filePath);
			InputStreamReader read = null;
			read = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(read);
			int line = 1;
			while (reader.readLine() != null) {
				map.put(line, reader.readLine());
				line++;

			}
			reader.close();
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static Workbook getWork(String path) {

		Workbook wb = null;
		try {
			String suffix = path.substring(path.lastIndexOf(".") + 1);
			if (StringUtils.equals(suffix, "xls")) {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
						path));
				wb = new HSSFWorkbook(fs);
			} else if (StringUtils.equals(suffix, "xlsx")) {
				wb = new XSSFWorkbook(new FileInputStream(path));
			}

			return wb;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void getSheet(String excelPath, String sheetName) {
		sheet = getWork(excelPath).getSheet(sheetName);

	}

	public static double getValueWithDobule(int col, int row) {
		Row obRow = sheet.getRow(row);
		return obRow.getCell(col).getNumericCellValue();

	}
	
	public static int getValueWithInt(int col, int row) {
		Row obRow = sheet.getRow(row);
		return (int)obRow.getCell(col).getNumericCellValue();

	} 
	
	public static String getValueWithString(int col, int row) {
		Row obRow = sheet.getRow(row);
		return obRow.getCell(col).getStringCellValue();

	} 

	public static int getRow() {
		return sheet.getLastRowNum();
	}

	public static Properties excelToProperties(String excelPath,
			String properteisPath) {

		FileOutputStream fos = null;
		OutputStreamWriter bufferedWirter = null;
		PrintWriter proWriter = null;

		try {
			fos = new FileOutputStream(properteisPath);

			bufferedWirter = new OutputStreamWriter(fos, "UTF-8");
			proWriter = new PrintWriter(bufferedWirter);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Sheet sheet = getWork(excelPath).getSheetAt(0);

		for (Row row : sheet) {

			Cell valueCell = row.getCell(1);
			if (valueCell == null) {
				continue;
			}

			String value = "";
			try {
				value = valueCell.toString().trim();

			} catch (Exception e) {
				continue;
			}
			String properties = "";
			String key = "";
			key = row.getCell(0).toString().trim();
			if (StringUtils.equals(key, "comment")) {
				properties = value;
				proWriter.write("\n");
			} else
				properties = row.getCell(0) + " = " + value;

			proWriter.write(properties);
			proWriter.write("\n");

		}
		proWriter.flush();
		proWriter.close();

		return getConfigFormatData(properteisPath);
	}

	public static String createFile(String propertiesPath) {

		try {
			File file = new File(propertiesPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String fileName = "groupRows.properties";

			File file2 = new File(file, fileName);

			if (!file2.exists()) {
				file2.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return propertiesPath + "groupRows.properties";
	}

	/**
	 * 写入数据到properties 文件
	 * 
	 * @param properteisPath
	 * @param pKey
	 * @param pValue
	 */
	public static void writeProperties(String properteisPath, String pKey,
			String pValue) {
		properteisPath = createFile(properteisPath);
		Properties props = new Properties();
		try {
			InputStream fis = new FileInputStream(properteisPath);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader brProp = new BufferedReader(isr);
			props.load(brProp);
			OutputStream fos = new FileOutputStream(properteisPath);
			OutputStreamWriter streamWrite = new OutputStreamWriter(fos,
					"UTF-8");
			BufferedWriter bufw = new BufferedWriter(streamWrite);
			props.setProperty(pKey, pValue);
			props.store(bufw, "Update: '" + pKey + "=" + pValue + "'.");
		} catch (IOException e) {
			System.err.println("Write the '" + pKey + "=" + pValue
					+ "' failed.");
		}
	}

	/**
	 * 写入数据到txt 文档.
	 * 
	 * @param logs
	 * @param url
	 */
	public static void getTexts(List<String> logs, String url) {
		File file = new File(url);
		OutputStream out = null;
		try {
			// 如果文件不存在需要创建它.
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			for (int i = 0; i < logs.size(); i++) {
				byte b[] = logs.get(i).getBytes();
				out.write(b);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	public static boolean writeDate(String url, String newStr) {

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		FileOutputStream fos = null;
		PrintWriter pw = null;

		boolean flag = false;
		String temp = "";
		String filein = newStr + "\r\n";
		try {
			try {
				File file = new File(url);
				if (!file.exists()) {
					file.createNewFile();
				}
				
				FileOutputStream writerStream = new FileOutputStream(file);    
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8")); 
				writer.write(newStr);
				writer.close();  
				/*fis = new FileInputStream(file);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				
				StringBuffer buf = new StringBuffer();

				// 保存该文件原来的内容.
				for (@SuppressWarnings("unused")
				int i = 0; (temp = br.readLine()) != null; i++) {
					buf = buf.append(temp);
					buf = buf.append(System.getProperty("line.separator"));
				}

				buf.append(filein);

				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				pw.write(buf.toString().toCharArray());
				pw.flush();
				flag = true;
*/
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (pw != null) {
					pw.close();
				}
				if (fis != null) {
					fis.close();
				}
				if (br != null) {
					br.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (isr != null) {
					isr.close();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return flag;
	}

	public static void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			// System.out.println(oldPath + "是文件夹--");
			File[] files = oldPath.listFiles();
			for (File file : files) {
				System.out.println(getCurrentTime() + " INFO - Delete file: "
						+ file.getPath().toString() + ".");
				deleteFile(file);
				file.delete();
			}
		} else {
			oldPath.delete();
		}
	}

	public static Properties getTestDataFormatData(String testDataFile,
			String sheetName) {
		Properties pro = new Properties();
		try {

			Sheet sheet = getWork(testDataFile).getSheet(sheetName);

			int rows = sheet.getLastRowNum();
			for (int i = 0; i <= rows; i++) {

				Cell key = sheet.getRow(i).getCell(0);
				Cell value = sheet.getRow(i).getCell(1);
				if (key == null || value == null) {
					continue;
				}

				String proKey = "", proValue = "";
				proKey = key.toString().trim();
				proValue = value.toString().trim();
				if (proKey.equals("") && proValue.equals("")) {
					continue;
				}
				if (StringUtils.equals(proKey, "comment")) {
					continue;
				} else {
					if (pro.get(proKey) == null) {
						pro.setProperty(proKey, proValue);
					} else {

						throw new OutputLengthException("(" + testDataFile
								+ ") 中sheet 為" + sheetName + "存在相同的Comment【"
								+ proKey + "】.");
					}
				}

			}
		} catch (Exception e) {
			throw new NullPointerException("(" + testDataFile
					+ ")中sheet 名字應該有appName 和appType 組成,使用‘_’ 拼接.");
		}

		return pro;

	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
		}
		return size;
	}
	
	public static String detectCharset(byte[] buf) {

		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(buf, 0, buf.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		return encoding;
	}

	public static String detectCharset(InputStream in) {

		byte[] buf = new byte[4096];

		UniversalDetector detector = new UniversalDetector(null);

		int nread;
		try {
			while ((nread = in.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		return encoding;
	}

	
	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();

	}
	
	public static String format(String jsonStr) {
	    int level = 0;
	    StringBuffer jsonForMatStr = new StringBuffer();
	    for(int i=0;i<jsonStr.length();i++){
	      char c = jsonStr.charAt(i);
	      if(level>0&&'\n'==jsonForMatStr.charAt(jsonForMatStr.length()-1)){
	        jsonForMatStr.append(getLevelStr(level));
	      }
	      switch (c) {
	      case '{': 
	      case '[':
	        jsonForMatStr.append(c+"\n");
	        level++;
	        break;
	      case ',': 
	        jsonForMatStr.append(c+"\n");
	        break;
	      case '}':
	      case ']':
	        jsonForMatStr.append("\n");
	        level--;
	        jsonForMatStr.append(getLevelStr(level));
	        jsonForMatStr.append(c);
	        break;
	      default:
	        jsonForMatStr.append(c);
	        break;
	      }
	    }
	    
	    return jsonForMatStr.toString();

	  }
	  
	  private static String getLevelStr(int level){
	    StringBuffer levelStr = new StringBuffer();
	    for(int levelI = 0;levelI<level ; levelI++){
	      levelStr.append("    ");
	    }
	    return levelStr.toString();
	  }

	
	public static void main(String[] args) {
		Properties pro = getTestDataFormatData(
				"E:/Autotest/eclipse_workspase/cms-autotest/src/test/resources/data/testData/NextPlusOfTestData.xlsx",
				"nextPlus_androidApp");
		System.out.println(pro.size());
	}
}
