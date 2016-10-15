/*package com.by.automate.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.by.automate.base.core.UiFramework;
import com.by.automate.utils.ReadXmlUtils;

public class ListenersTestngOfApi extends TestListenerAdapter {

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
		try {
			comment = tr.getThrowable().getMessage();
		} catch (Exception e) {
		
		}
		if (StringUtils.isEmpty(comment)) {
			if (status.equals("FAILURE")) {
				comment = formatDate(System.currentTimeMillis())
						+ " "
						+ status
						+ " - "
						+ "Warning: message is null,please using assertions function to add comment.";
			}else if (status.equals("SKIP")) {
				comment = "This Method " + tr.getMethod() + " Is Skiped.";
			} else if (status.equals("ERROR")) {
				comment = "";
			}
		}
	
		return comment;
	}

	public void onTestFailure(ITestResult tr) {
		
		String path = apiSavePath();
		String apiDescription = getDescription();
		String url = getURl();
		String method = getMehod();
		String id = getApiNumber();
		String status = "FAILURE";
		String checkPoint = getComment(tr, status);
		String className = getClassName(tr);
		String time = (tr.getEndMillis() - tr.getStartMillis()) +"";
		String excute = "Auto";
		putResutlToXML(path, id, apiDescription, url, method, status, excute, className,checkPoint, time);

	}

	public void onTestSuccess(ITestResult tr) {
		super.onTestFailure(tr);
		String path = apiSavePath();
		System.out.println("aaaaaaaaaaaa" + path);
		String apiDescription = getDescription();
		String url = getURl();
		String method = getMehod();
		String id = getApiNumber();
		String checkPoint = getCheckPoint();
		String className = getClassName(tr);
		String status = "SUCCESS";
		String time = (tr.getEndMillis() - tr.getStartMillis()) +"";
		String excute = "Auto";
		putResutlToXML(path, id, apiDescription, url, method, status, excute, className,checkPoint, time);

	}

	public void onTestSkipped(ITestResult tr) {
		super.onTestFailure(tr);
		String path = apiSavePath();
		
		String apiDescription = getDescription();
		String url = getURl();
		String method = getMehod();
		String id = getApiNumber();
		String status = "SKIPE";
		String checkPoint = "The Current API Of Skiped.";
		String className = getClassName(tr);
		String time = (tr.getEndMillis() - tr.getStartMillis()) +"";
		String excute = "non-execution";
		putResutlToXML(path, id, apiDescription, url, method, status, excute, className,checkPoint, time);

	}

	public static void putResutlToXML(String XmlPath,String number, String apiDescription, String url, String method, String status,String execute,String log ,String checkPoing,String time){
		if(root == null){
			document = ReadXmlUtils.createXML();
			root = ReadXmlUtils.createRootElement(document, "APIS");
		}else{
			document =ReadXmlUtils.getDocument(XmlPath);
			root = document.getRootElement();
		}
		// 添加api 节点和属性.
		Element apiElement = ReadXmlUtils.addElement(root, "Api");
		apiElement = apiElement.addAttribute("Number",number );
		
		// 在api 节点下添加节点
		// Add ApiDescription
		Element apiDescriptionElement = ReadXmlUtils.addElement(apiElement, "ApiDescription");
		ReadXmlUtils.addText(apiDescriptionElement, apiDescription);
		// Add URL
		Element apiURLElement = ReadXmlUtils.addElement(apiElement, "URL");
		ReadXmlUtils.addText(apiURLElement, url);
		// Add Method
		Element apiMethodElement = ReadXmlUtils.addElement(apiElement, "Method");
		ReadXmlUtils.addText(apiMethodElement, method);
		// Add Check Point
		Element apiCheckPointElement = ReadXmlUtils.addElement(apiElement, "CheckPoint");
		ReadXmlUtils.addText(apiCheckPointElement, checkPoing);
		// Add Status
		Element apiStatusElement = ReadXmlUtils.addElement(apiElement, "Status");
		ReadXmlUtils.addText(apiStatusElement, status);
		// Add Execute
		Element apiExecuteElement = ReadXmlUtils.addElement(apiElement, "Execute");
		ReadXmlUtils.addText(apiExecuteElement,execute);
		// Add time
		Element apiTimeElement = ReadXmlUtils.addElement(apiElement, "Time");
		ReadXmlUtils.addText(apiTimeElement,time);
		// Add Log
		Element apiLogElement = ReadXmlUtils.addElement(apiElement, "Log");
		ReadXmlUtils.addText(apiLogElement, log);
		
		// save 
		ReadXmlUtils.writeXml(document , XmlPath);
		
	}
	
	private String apiSavePath(){
		return UiFramework.info.get("APIPath");
	}
	private String getCheckPoint(){
		return UiFramework.info.get("CheckPoint");
	}
	private String getApiNumber(){
		return UiFramework.info.get("APINumber");
	}
	private String getDescription(){
		return UiFramework.info.get("ApiDescription");
	}
	private String getURl(){
		return UiFramework.info.get("URL");
	}
	private String getMehod(){
		return UiFramework.info.get("Method");
	}
	
	private String formatDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		return formatter.format(date);
	}
	
	

}*/