/*
 * History
 * Date          Version   Author    Change               Description
 * ----------- --- ------------- ----------------------------------------
 * 2015-11-29      1       Justin    保存XML方式   开发函数, 把UIMpa保存到Map<String, Map<String List<String>>> 集合里里面去.
 *                                                第一个String=areaName, 第二个String=ElementName,第三个String=Type,Value,view.
 * 2016-1-20       1.1     Justin    修改Cusotm Reprot    android||IOS 调用Web 浏览器最后Report统计修改.
 * 
 */
package com.by.automate.base.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.by.automate.utils.CommonTools;
import com.by.automate.utils.testData.TestCasesDataGtter;
import com.by.automate.tools.bean.AppInfo;

/*Test base class for exercising an application (Nim endplay, Nextplus)
 * via the UI.
 * */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class InitiaFramework {

    /**
	 * 根节点
	 */
	protected Element rootNode = null;
	/**
	 * 被测应用详细信息. UIMap <Application> 节点.
	 */
	protected String defaultPage = "";
	protected String appVersion;
	protected String environment;
	protected String testCategory;
	protected String app;
	protected String netWork;
	protected String description;
	protected String appSize;
	protected String deviceName;
	protected String deviceVersion;
	protected String testRange;

	protected AppInfo appInfo = null;

	/**
	 * 保存当前 page 和view,area.
	 * 
	 */
	protected String pageName = "";
	protected String viewName = "";
	protected String areaName = "";

	/**
	 * 定义 每个节点.
	 */
	protected Element uiMap = null;
	protected Element application = null;
	protected Element pages = null;
	// 公共Area 节点.
	protected Element commonAreas = null;

	/*
	 * Default config filenames. Locates initialConfig file by the environment
	 * variable SeleniumConfigFile; if not set, uses the default
	 * conf/initialConfig.properties.
	 */
	private static String entryConfigFile = StringUtils.defaultString(
			System.getenv("SeleniumConfigFile"), "MainConfig.properties");
	protected static String testRoot = CommonTools.getTestRoot();

	// log4j 配置文件名.
	protected static String log4j = "log4j.properties";
	protected Logger logger = null;

	protected static String configFile;

	// 给监听类区分Android Web IOS 截图.
	protected String appType;
	protected String appName;

	// 保存elements 数据,
	/*
	 * 第一个String=areaName, 第二个String=ElementName, 第三个String=Type,Value,view.
	 */
	Map<String, Map<String, List<String>>> elements = new HashMap<String, Map<String, List<String>>>();
	// initConfigPro , 保存MainConfig 文件裡面的值. corePro, 保存defaule, 以及sut ui文件信息.
	// connectPro 保存Excel 保存的測試數據.
	protected Properties mainPro;
	protected Properties sutPro;
	protected Properties connectPro;

	protected String projectLevelSutConfigPath;
	protected static String projectLevelUiConfigPath = "";
	protected static String projectLevelDataConfigPath = "";
	protected static String projectLevelTestDocumentPath = "";
	protected String sutFileName;

	protected String dataFile;
	protected Document doc;
	protected String coreConfigPath;
	protected String coreFileName;
	protected String uiPath;
	protected String chinesePath;
	protected String uiFullFileName;
	protected String borwserFileName;

	protected String pageTimeout = "30000";
	protected String viewTimeout = "20000";
	protected String elementTimeout = "15000";

	protected String appUrl = "";
	protected String apiUrl = "";
	protected String appLocale = "";

	// using excel save logs.
	protected String classNamePath;
	public static String excelPath;
	protected String testExcelName;
	// 保存截图 log report 等文件夹.
	protected String screenCapturesFile;
	protected String logFile;

	/**
	 * The target which is under test. It could be "endplay", "Nextplus", etc
	 * This name
	 * */
	protected abstract String getAppName();

	protected abstract String getAppType();

	/**
	 * Convenience method for initiate data.
	 */
	public InitiaFramework() {
		this("", "");
	}

	public InitiaFramework(String coreFileName) {
		this(coreFileName, "");
	}

	public InitiaFramework(String coreFileName, String profile) {

		// 初始化必要的數據.
		frameworkInitiate(coreFileName);
		// 初始化 log4j 配置
		initializationLog4jLogs();
		// 根據項目類別初始化數據.
		getConfigurationParameters();
		// 初始化XML
		xmlinitiate();
		getAppINFO();
		// 重构函數,啟動指定app.
		platformSupportInitiate(profile);
	}

	protected void getAppINFO() {

		defaultPage = application.element("DefaultPage").getText().trim();
		// version = application.element("Version").getText().trim();
		environment = application.element("Environment").getText().trim();
		testCategory = application.element("TestCategory").getText().trim();
		app = application.element("AppName").getText().trim();
		netWork = application.element("NetWork").getText().trim();
		description = application.element("Description").getText().trim();

		log("Description   : " + description);
		log("Test Category : " + testCategory);
		log("App Name      : " + app);
		log("Environment   : " + environment);
		log("Version       : " + appVersion);
		log("Test Network  : " + netWork);

	}

	/**
	 * Initialize the all configuration files. 初始化配置文件.
	 */
	private void frameworkInitiate(String indicatedSutFileName) {

		configFile = testRoot.replace("target/test-classes",
				"src/test/resources/conf");
		dataFile = testRoot.replace("target/test-classes",
				"src/test/resources/data");

		mainPro = CommonTools.getConfigFormatData(configFile + entryConfigFile);

		projectLevelSutConfigPath = configFile + getAppName() + "/sut/";
		sutFileName = StringUtils.defaultIfEmpty(indicatedSutFileName,
				getDefaultSutFileName());
		sutPro = CommonTools.getConfigFormatData(projectLevelSutConfigPath
				+ getDefaultSutFileName());
		getAdditionalConf(sutPro);

	}

	private void getAdditionalConf(Properties pro) {
		// 解析Core 文件, 文件中 以conf. 开始并只有两节的key 会被自动解析到corePro中去. Ex: conf.statck.
		Enumeration en = pro.keys();
		String fieldName;
		String path;
		Properties temp;
		Properties additionalConf = new Properties();
		while (en.hasMoreElements()) {
			fieldName = en.nextElement().toString();
			// Properties Contains "conf." , use "." splitted, it length is 2.
			if (fieldName.contains("conf.")
					&& fieldName.split("\\.").length == 2) {
				path = configFile
						+ getAppName()
						+ "/"
						+ StringUtils.defaultIfEmpty(
								getInitCofigProperty("path.conf."
										+ fieldName.substring(5)),
								fieldName.substring(5)) + "/"
						+ pro.getProperty(fieldName).toString();
				temp = CommonTools.getConfigFormatData(path);
				additionalConf.putAll(temp);
			}
		}

		pro.putAll(additionalConf);
		sutPro = pro;
	}

	protected void xmlinitiate() {

		getClassName();
		if (!getAppType().equals("API")) {
			initXmlProperty();
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see farmwork.selenium.test.util.ISeleniumUtil#getDriver()
	 * 
	 *      获取当前java Class 名字.
	 */
	protected String getClassName() {

		try {
			log(">=================================================================<");
			ITestResult it = Reporter.getCurrentTestResult();
			log("Now Starting: " + it.getTestClass().getName());
			return classNamePath = it.getTestClass().getName();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	protected void platformSupportInitiate(String profile) {
	}

	protected void getConfigurationParameters() {
	}

	protected void prepareTestEnvironment() {
	}

	/**
	 * 初始化 XML 节点
	 */
	protected void initXmlProperty() {
		try {
			rootNode = doc.getRootElement();
			uiMap = rootNode.element("UiMap");
			application = uiMap.element("application");
			pages = uiMap.element("pages");
			commonAreas = pages.element("commonAreas");
		} catch (Exception e) {
			throw new NullPointerException("XML 格式不對,請檢查 (" + uiPath + "). "
					+ e.getMessage());
		}

	}

	private String getDefaultSutFileName() {

		String coreFileKey = String.format("sut.%s.%s.file", getAppName(),
				getAppType());
		String rtn = getInitCofigProperty(coreFileKey);
		if (rtn == null || StringUtils.isEmpty(rtn)) {
			System.out.println("!Warning: can't find any key " + coreFileKey
					+ " in the initial config file. ");
		}
		return rtn;
	}

	/**
	 * Get the System Under Test (AutotestConig) filename, which may be
	 * different from what is in initialConfig.properties.
	 * 
	 * @return The file path & name of the Autotestconfig that is being tested.
	 */

	public String getInitCofigProperty(String key) {

		return CommonTools.getConfigValue(mainPro, key);
	}

	/**
	 * Get the System Under Test (core) filename, which may be different from
	 * what is in initialConfig.properties.
	 * 
	 * @return The file path & name of the core that is being tested.
	 */

	public String getSutFullFileName(String key) {
		return CommonTools.getConfigValue(sutPro, key);
	}

	public String getContentPropertry(String key) {
		return CommonTools.getConfigValue(connectPro, key);
	}

	/**
	 * Log info to Console by different type.
	 * 
	 * @param content
	 *            Message that need to log to Console.
	 * @param type
	 *            1: Normal INFO 2: ERROR 3: WARNING (show in black) 4: WARNING
	 *            (show in red)
	 */
	public void log(String content, Integer type) {
		switch (type) {
		case 1: {
			logger.info(content);
			break;
		}
		case 2: {
			logger.error(content);
			break;
		}
		case 3: {
			logger.warn(content);
			break;
		}
		case 4: {
			logger.debug(content);
			break;
		}

		}
	}

	public void log(Object content) {
		if (content instanceof Integer) {
			log("" + content, 1);
		} else if (content instanceof Float) {
			log(content + "", 1);
		} else
			log((String) content, 1);
	}

	/**
	 * This test data getter try finding test data from global XML file
	 * SeleniumConfig.XML_STYLE_TEST_DATA_FILE_NAME
	 * 
	 * @throws DocumentException
	 */

	protected Document readerTestData(String XmlPath) {

		try {
			File file = new File(XmlPath);
			if (!file.exists()) {
				if (!file.exists()) {
					throw new RuntimeException(
							"Test data set file: ["
									+ file
									+ "] "
									+ "could not be found, please make sure you set property correctly.");
				}
			}
			SAXReader reader = new SAXReader();
			return reader.read(new FileInputStream(XmlPath));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			log(XmlPath
					+ " can not found , please check config of the sut file.");
		}

		return null;
	}

	/**
	 * 
	 * @param pageName
	 * @return
	 */
	protected String getDefaultView(String pageName) {
		return getPage(pageName).attributeValue("defaultView");
	}

	/**
	 * 
	 * @param pageName
	 * @return
	 */
	protected Element getPage(String pageName) {

		List<Element> allPage = pages.selectNodes("./page[@name='" + pageName
				+ "']");

		if (allPage.size() == 0) {
			throw new NullPointerException("在XML文件中找不到page '" + pageName
					+ "' (" + uiPath + ").");

		}
		if (allPage.size() > 1) {
			throw new IndexOutOfBoundsException("在XML文件中" + uiPath
					+ " 存在相同page:" + pageName + "的節點.");
		}

		this.pageName = pageName;
		return allPage.get(0);

	}

	/**
	 * 
	 * @param viewName
	 * @return
	 */
	protected String getAreas(String viewName) {
		return getView(viewName).attributeValue("areas");
	}

	protected Element getView(String viewName) {
		List<Element> views = getPage(pageName).selectNodes(
				"./view[@name='" + viewName + "']");

		if (views.size() == 0) {
			throw new NullPointerException("XML[" + uiPath + "], Page["
					+ pageName + "]節點下找不到View為" + viewName + "的節點.");

		}
		if (views.size() > 1) {
			throw new IndexOutOfBoundsException("XML[" + uiPath + "], Page["
					+ pageName + "]節點下存在多個View節點[" + viewName + "].");
		}

		this.viewName = viewName;
		return views.get(0);
	}

	protected Map<String, Map<String, List<String>>> getUiMap(String areas) {
		Map<String, Map<String, List<String>>> elements = new HashMap<String, Map<String, List<String>>>();
		areas = StringUtils.substringBetween(areas, "[", "]");

		// 如果Areas只包含一个area
		String[] aresNames = null;
		if (areas.contains(",")) {
			aresNames = areas.split(",");
		} else {
			aresNames = new String[] { areas };
		}

		for (int i = 0; i < aresNames.length; i++) {

			Map<String, Map<String, List<String>>> temp = new HashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			map = getElementsOfArea(aresNames[i].trim());
			temp.put(aresNames[i], map);
			elements.putAll(temp);
		}

		return elements;

	}

	/**
	 * 
	 * @param area
	 * @return
	 */
	protected Map<String, List<String>> getElementsOfArea(String area) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<Element> allArea = getPage(pageName)
				.selectNodes("./areas/" + area);
		if (allArea.size() == 0) {
			log("在page name '" + pageName + "' -  view　'" + viewName
					+ "' 下找不到 area '" + area + "' 節點, 嘗試到commonAreas查找.");
			allArea = getElementsOfCommonAreas(area);

		}
		if (allArea.size() > 1) {
			throw new IndexOutOfBoundsException("在xml 配置文件中 pages节点下page名字为 "
					+ pageName + " 下有相同area节点{" + area + "}.");
		}
		List<Element> elemenets = allArea.get(0).elements();
		int meagerBefore = elemenets.size();
		String name = "", type = "", value = "", view = "";
		for (int i = 0; i < meagerBefore; i++) {
			List<String> attributes = new ArrayList<String>();
			try {
				/*
				 * List<Attribute> item = elemenets.get(i).attributes();
				 * 
				 * for (Attribute attribute : item) { attribute.getName(); }
				 */
				name = elemenets.get(i).attributeValue("name");
				type = elemenets.get(i).attributeValue("type");
				value = elemenets.get(i).attributeValue("value");
				view = elemenets.get(i).attributeValue("view");
			} catch (Exception e) {
				System.out.println("View is empty");
				view = null;
			}
			attributes.add("type=" + type);
			attributes.add("value=" + value);
			attributes.add("view=" + view);
			map.put(name, attributes);

		}

		if (map.size() == meagerBefore) {
			return map;
		} else {
			String returnValue = "";
			for (int i = 0; i < elemenets.size(); i++) {
				String name1 = elemenets.get(i).attributeValue("name");
				for (int j = i + 1; j < elemenets.size(); j++) {
					String name2 = elemenets.get(j).attributeValue("name");
					if (name1.equals(name2)) {
						returnValue = name1;
					}
				}
			}
			throw new IndexOutOfBoundsException("在 pageName " + pageName
					+ ", view[" + viewName + "], area[" + area
					+ "]模块下 存在相同Element,名字是:" + returnValue + ".");
		}
	}

	/**
	 * 
	 * @param area
	 * @return
	 */
	protected List<Element> getElementsOfCommonAreas(String area) {

		List<Element> allArea = commonAreas.selectNodes("./" + area);
		if (allArea.size() == 0) {
			throw new NullPointerException("area" + area
					+ "在xml CommonArea下找不到. 请检查文件【" + uiPath + "】.");

		}
		if (allArea.size() > 1) {
			throw new IndexOutOfBoundsException("在xml CommonArea节点下有相同area节点{"
					+ area + "}.");
		}
		return allArea;
	}

	/**
	 * View the specified view, or the default if requestedView is blank.
	 */
	protected void uiMapSetView() {
		uiMapSetView("");
	}

	/**
	 * Convenience method for updating the UI Map objects with the current view.
	 * Single-arg wrapper for uiMapUpdateView() that always adds a new view and
	 * deletes any forward bread crumbs.
	 * 
	 * Parameters:
	 * 
	 * @param view
	 *            -The view which you want switch to .
	 * @return true if view was updated successfully.
	 */
	protected boolean uiMapSetView(String view) {

		String defaultView = "";

		// get page and view
		if (StringUtils.isBlank(view)) {
			pageName = defaultPage;
			defaultView = getDefaultView(defaultPage);
		} else {
			pageName = view.split(":")[0];
			defaultView = view.split(":")[1];
		}

		viewName = defaultView;
		String areas = getAreas(viewName);
		elements = getUiMap(areas);
		return true;
	}

	protected String getElementAttValue(String elemnetName, String attributeName) {
		List<String> info = getElementInfo(elemnetName);
		String val = "";
		try {
			for (int i = 0; i < info.size(); i++) {
				String value = info.get(i);
				if (value.contains(attributeName + "=")) {
					val = value.replace(attributeName + "=", "");
					break;
				}
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("在pageName[" + pageName + "],area["
					+ areaName + "],下找不到元素[" + elemnetName + "].");
		}
		if(val.equals("") || val.equals("null")){
			log("在 ["+pageName+":"+areaName+ "] 下, 元素["+elemnetName+"], 没有 " +attributeName+ "属性值.");
		}
		return val;
	}

	/**
	 * 
	 * @param attributeName
	 * @return
	 */
	protected String getViewAttributeValue(String attributeName) {
		try {
			return getView(viewName).attributeValue(attributeName);
		} catch (Exception e) {
			log("Page is not change.");
		}

		return null;
	}

	/**
	 * 
	 * @param elementName
	 * @return
	 */
	protected List<String> getElementInfo(String elementName) {

		Iterator it = elements.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String areaName = (String) entry.getKey();
			Map<String, List<String>> info = (Map) entry.getValue();

			List<String> retrunValue = info.get(elementName);
			if (retrunValue != null) {
				this.areaName = areaName;
				return retrunValue;
			}
		}

		return null;
	}

	/**
	 * 检查点状态， 成功,失败,以及原因. 添加测试步骤的连接
	 */
	protected void input_setp(String setp) {

	}

	/**
	 * input 测试数据.
	 * 
	 * @param input
	 */
	protected void input_setp(Object input) {

	}

	private String getImage() {
		String androidImage = dataFile + "icon/android.png";
		String webImage = dataFile + "icon/web.png";
		String IOSImage = dataFile + "icon/ios.png";
		if (getAppType().equals("AndroidApp")) {
			return androidImage;
		} else if (getAppType().equals("InstrumentDriver")) {
			return IOSImage;

		} else if (getAppType().equals("IOSApp")) {
			return IOSImage;

		} else if (getAppType().equals("WebApp")) {
			return webImage;
		}

		throw new NullPointerException("改应用不在测试范围内.");
	}

	/**
	 * 初始化 log4j 配置.
	 */
	private void initializationLog4jLogs() {
		ITestResult it = Reporter.getCurrentTestResult();
		String classNamePath = it.getTestClass().getName();
		String className = classNamePath.split("\\.")[classNamePath
				.split("\\.").length - 1];
		PropertyConfigurator.configure(configFile + log4j);
		logger = Logger.getLogger(className);

		Appender appender = LogManager.getLoggerRepository().getRootLogger()
				.getAppender("FILE");
		if (appender instanceof FileAppender) {
			FileAppender fileAppender = (FileAppender) appender;
			fileAppender.setFile(CommonTools.getCustomReportPath() + "Logs/"
					+ className + ".log");
			fileAppender.activateOptions();
		}
	}

	private String getName() {
		ITestResult it = Reporter.getCurrentTestResult();
		String classNamePath = it.getTestClass().getName();
		return classNamePath.split("\\.")[classNamePath.split("\\.").length - 1];
	}

	public void putStep(String id) {
		TestCasesDataGtter.getTestCasesAndSave(id, projectLevelDataConfigPath
				+ "\\" + getSutFullFileName("test.excelName"),
				getSutFullFileName("test.sheetName"),
				CommonTools.getCustomReportPath() + "Step/" + getName());
	}

}
