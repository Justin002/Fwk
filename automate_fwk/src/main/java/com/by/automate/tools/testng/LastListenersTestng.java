package com.by.automate.tools.testng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.by.automate.base.core.UiFramework;
import com.by.automate.tools.bean.TestMethod;
import com.by.automate.utils.CommonTools;
import com.by.automate.utils.ReadXmlUtils;

public class LastListenersTestng extends TestListenerAdapter {

	public static List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	public static Element root;
	public static Document document;

	public String getClassName(ITestResult tr) {
		String className = tr.getTestClass().getName();
		String[] strs = className.split("\\.");
		return strs[strs.length - 1];

	}

	public String getComment(ITestResult tr, String status) {
		//
		String comment = "";
		if(!status.equals("SKIP")){
			try {
				StringBuffer sb = new StringBuffer();  
				sb.append(tr.getThrowable().getLocalizedMessage().toString()+"\n");
				StackTraceElement[] stackArray  = tr.getThrowable().getStackTrace();
				for (int i = 0; i < stackArray.length; i++) {  
		            StackTraceElement element = stackArray[i];  
		            if(element.toString().contains("org.testng.internal")){
		            	break;
		            }
		            sb.append(element.toString() + "\n");  
		        }  
				comment = sb.toString();
			} catch (Exception e) {

			}
		}
		
		if (StringUtils.isEmpty(comment)) {
			if (status.equals("FAILURE")) {
				comment = formatDate(System.currentTimeMillis()) + " " + status + " - "
						+ "Warning: message is null,please using assertions function to add comment.";
			} else if (status.equals("SKIP")) {
				comment = "This Method " + tr.getMethod() + " Is Skiped.";
			} else if (status.equals("ERROR")) {
				comment = "";
			}
		}

		return comment;
	}

	public void onTestFailure(ITestResult tr) {
		TestMethod testmethod = null;
		// 定义 变量
		String className = "";
		String method = "";
		String status = "";
		String message = "";
		long time = 0;

		// get class name.
		className = getClassName(tr);
		// get method name.
		method = tr.getName();
		// get the method run time.
		time = tr.getEndMillis() - tr.getStartMillis();
		status = "FAILURE";
		// get the method run result.
		message = getComment(tr, status);

		String imageName = screenShot(className);
		testmethod = new TestMethod(method, status, time+"", imageName, className + ".txt",message,className+".log");
		putResutlToXML(CommonTools.getCustomReportPath() + "test-report.xml",className, testmethod);

	}

	public void onTestSuccess(ITestResult tr) {
		super.onTestFailure(tr);
		TestMethod testmethod = null;

		// 定义 变量
		String className = "";
		String method = "";
		String status = "";
		long time = 0;
		// get class name.
		className = getClassName(tr);
		// get method name.
		method = tr.getName();
		// get the method run time.
		time = tr.getEndMillis() - tr.getStartMillis();
		status = "SUCCESS";
		String imageName = screenShot(className);
		testmethod = new TestMethod(method, status, time+"", imageName, className + ".txt","",className+".log");
		putResutlToXML(CommonTools.getCustomReportPath() + "test-report.xml",className, testmethod);
	}

	public void onTestSkipped(ITestResult tr) {
		super.onTestFailure(tr);
		// String url = tr.getTestContext().getOutputDirectory();

		TestMethod testmethod = null;
		// 定义 变量
		String className = "", method = "", status = "", message = "";
		long time = 0;
		// get class name.
		className = getClassName(tr);
		// get method name.
		method = tr.getName();
		// get the method run time.
		status = "SKIP";
		message = getComment(tr, status);
		testmethod = new TestMethod(method, status, time+"", "", className + ".txt",message, className+".log");
		putResutlToXML(CommonTools.getCustomReportPath() + "test-report.xml",className, testmethod);

	}

	/**
	 * get screen shot file.
	 */
	public String screenShot(String className) {
		String imageName = "";
		File screenshot = null;
		try {
			screenshot = getScreenShotFile();
			imageName = getPNGName(className);
			String newPath = prepareCaseFolder(className, imageName);
			copyScreenShot(screenshot, new File(newPath));
		} catch (Exception e) {

			e.printStackTrace();
		}

		return imageName;
	}

	private static File getScreenShotFile() {
		File screenshot = null;

		if (!(UiFramework.driver instanceof TakesScreenshot)) {
			WebDriver augmentDriver = new Augmenter().augment(UiFramework.driver);
			screenshot = ((TakesScreenshot) augmentDriver).getScreenshotAs(OutputType.FILE);
		} else {
			screenshot = ((TakesScreenshot) UiFramework.driver).getScreenshotAs(OutputType.FILE);
		}

		return screenshot;
	}

	private static String getPNGName(String className) {

		return className + "/" + CommonTools.getDateTimeStampString() + ".png";
	}

	private static String prepareCaseFolder(String className, String pngName) {
		String caseFolderStr = CommonTools.getCustomReportPath() + "ScreenShot/" + className + "/";
		File caseFolder = new File(caseFolderStr);
		if (!caseFolder.exists() && !caseFolder.isDirectory()) {
			caseFolder.mkdirs();
		}
		return CommonTools.getCustomReportPath() + "ScreenShot/" + pngName;

	}

	/**
	 * Convenience method for copying screen shot.
	 * 
	 * @param screenShotFile
	 *            the screen shot file.
	 * @param outputFile
	 *            the file that we want to put the screen shot file.
	 * 
	 * @throws IOException
	 */
	protected static void copyScreenShot(File screenShotFile, File outputFile) throws IOException {

		FileInputStream imgIs = new FileInputStream(screenShotFile);
		FileOutputStream imageOs = new FileOutputStream(outputFile);
		FileChannel imgCin = imgIs.getChannel();
		FileChannel imgCout = imageOs.getChannel();
		imgCin.transferTo(0, imgCin.size(), imgCout);
		imgCin.close();
		imgCout.close();
		imgIs.close();
		imageOs.close();
	}

	private String formatDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		return formatter.format(date);
	}

	public static void putResutlToXML(String XmlPath, String className, TestMethod method) {

		document = ReadXmlUtils.getDocument(XmlPath);
		root = document.getRootElement();
/*		// 添加api 节点和属性.
		Element applicationElement = ReadXmlUtils.addElement(root, "application");
		Element appname = ReadXmlUtils.addElement(applicationElement, "appname");
		Element version = ReadXmlUtils.addElement(applicationElement, "appversion");
		Element network = ReadXmlUtils.addElement(applicationElement, "network");
		Element testsystem = ReadXmlUtils.addElement(applicationElement, "testsystem");
		Element browser = ReadXmlUtils.addElement(applicationElement, "browser");
		Element testdate = ReadXmlUtils.addElement(applicationElement, "testdate");
		// add test
		ReadXmlUtils.addText(appname, "GIS");
		ReadXmlUtils.addText(version, "1.0(Test)");
		ReadXmlUtils.addText(network, "Local Connection");
		ReadXmlUtils.addText(testsystem, "Windows 7");
		ReadXmlUtils.addText(browser, "Firefox");
		ReadXmlUtils.addText(testdate, "2016-06-15");*/

		// 在report 节点下添加节点 TestCases
		Element testCasesElement = (Element)root.selectNodes("//TestCases").get(0);

		// add class element
		// 判断class节点是否存在 如果存在只用添加 method 节点。
		List<Element> list = ReadXmlUtils.findElement(testCasesElement, className);
		Element classElement = null;
		if(list.size() ==0){
			classElement = ReadXmlUtils.addElement(testCasesElement, "class");
			ReadXmlUtils.addAttribute(classElement, "name", className);
		}else{
			classElement = list.get(0);
		}

		addClassElement(classElement, method);

		// save
		ReadXmlUtils.writeXml(document, XmlPath);

	}

	private static void addClassElement(Element classElement, TestMethod method) {

		// Add mthod element
		Element methodElement = ReadXmlUtils.addElement(classElement, "method");
		ReadXmlUtils.addAttribute(methodElement, "name", method.getMethodName());
		// Add result element
		Element resultElement = ReadXmlUtils.addElement(methodElement, "result");
		ReadXmlUtils.addText(resultElement, method.getResult());
		// Add run time element
		Element runtimeElement = ReadXmlUtils.addElement(methodElement, "runtime");
		ReadXmlUtils.addText(runtimeElement, method.getRuntime() + "");
		// Add screenShortName element
		Element screenShotElement = ReadXmlUtils.addElement(methodElement, "screenShortName");
		ReadXmlUtils.addText(screenShotElement, method.getScreenShortName());
		// Add step name element
		/*Element stepNameelement = ReadXmlUtils.addElement(methodElement, "stepname");
		ReadXmlUtils.addText(stepNameelement, method.getStepName());*/
		
		Element commentelement = ReadXmlUtils.addElement(methodElement, "comment");
		ReadXmlUtils.addText(commentelement, method.getComment());

	}

	public static void main(String[] args) {
		/*putResutlToXML("C:/Users/Justin/Desktop/text2.xml", "test2",
				new TestMethod("test2", "success", "1200", "asdf.png", "test2.txt","SUCCESSFULLY"));*/
	}

}