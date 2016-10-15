package com.by.automate.base;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;

import com.by.automate.base.core.UiFramework;

public abstract class WebFramework extends UiFramework {

	/** 
	 * @see base.core.InitialFramework#getAppName()
	 */
	protected String getAppName() {

		return "";
	}

	/** 
	 * @see base.core.InitialFramework#getAppType()
	 */
	protected String getAppType() {

		return "WebApp";
	}

	/**
	 * 默认构造函数
	 */
	protected WebFramework() {

		super();
	}

	/**
	 * 带参数构造函数
	 * 
	 * @param SUT 指定SUT文件
	 */
	protected WebFramework(String SUT) {

		super(SUT);
	}

	/* (non-Javadoc)
	 * @see base.core.InitialFramework#platformSupportInitiate(java.lang.String)
	 */
	@Override
	protected void platformSupportInitiate(String profileName) {

		setProfilePath(profileName);

		startSeleniumServerAndBrowser();

		prepareTestEnvironment();
	}

	/**
	 * 指定浏览器及其配置文件
	 * 
	 * @param profileName 浏览器配置文件名
	 */
	private void setProfilePath(String profileName) {

		browserName = getSutFullFileName("browser.name");
		browserSize = getSutFullFileName("browser.size").toLowerCase();
		browserProfilePath = getProfilePath(profileName);
	}

	/**
	 * 获取浏览器配置文件路径
	 * 
	 * @param profileName 浏览器配置文件名
	 * @return 浏览器配置文件路径
	 */
	private String getProfilePath(String profileName) {

		/*
		 * Define the browserProfilePath according to the browser we are going
		 * to test.
		 */
		if (browserName.matches(".*firefox.*")) {

			return getBrowserProfileProperty("profile.firefox", profileName);

		} else if (browserName.matches(".*iexplore.*")) {
			return getBrowserProfileProperty("profile.iexplore");
		} else if (browserName.matches(".*chrome.*")) {
			return getBrowserProfileProperty("profile.chrome");
		}
		throw new RuntimeException("Can't find any profile property for browser [" + browserName + "] and profile [" + profileName + "]. ");
	}

	/**
	 * 启动Selenium服务器，打开一个新的浏览器，清除浏览器cookie
	 */
	protected void startSeleniumServerAndBrowser() {

		if (browserName.matches(".*firefox.*")) {
			log("Loading Firefox Profile and open Firefox...");
			File profileFile = new File(browserProfilePath);
			if (profileFile.exists()) {
				FirefoxProfile profile = new FirefoxProfile(new File(browserProfilePath));
				profile.setEnableNativeEvents(true);
				driver = new FirefoxDriver(profile);
			} else {
				driver = new FirefoxDriver();
			}
		}
		if (browserName.matches(".*chrome.*")) {
			System.setProperty("webdriver.chrome.driver", dataFile + "common\\browserProfiles\\drivers\\chromedriver.exe");
			String userProfile = browserProfilePath.replace("/", "\\");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("user-data-dir=" + userProfile, "--disable-prerender-local-predictor", "--incognito", "--start-maximized");
			driver = new ChromeDriver(options);
		}
		if (browserName.matches(".*iexplore.*")) {
			System.setProperty("webdriver.ie.driver", dataFile + "common\\browserProfiles\\drivers\\IEDriverServer.exe");
			DesiredCapabilities dc = DesiredCapabilities.internetExplorer();
			dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			dc.setCapability("ignoreProtectedModeSettings", true);
//			String userProfile = browserProfilePath.replace("/", "\\");
//			ChromeOptions options = new ChromeOptions();
//			options.addArguments("user-data-dir=" + userProfile, "--disable-prerender-local-predictor", "--incognito", "--start-maximized");
			driver = new InternetExplorerDriver(dc);
		}

		main_window = driver.getWindowHandle();
		driver.manage().deleteAllCookies();

	}

	/**
	 * 获取指定浏览器配置文件的路径
	 * 
	 * @param profileType 指定浏览器名
	 * @return 配置文件的完整路径
	 */
	private String getBrowserProfileProperty(String profileType) {

		return getBrowserProfileProperty(profileType, "");
	}

	/**
	 * 获取指定浏览器配置文件的路径
	 * 
	 * @param profileType 指定浏览器名
	 * @param folderName 指定配置文件名
	 * @return 配置文件的完整路径
	 */
	private String getBrowserProfileProperty(String profileType, String folderName) {

		String returnStr = "";

		if (folderName.isEmpty())
			folderName = getSutFullFileName("browser." + browserName + ".profile");
//		if (folderName.isEmpty())
//			folderName = getProperty("browser." + getProperty("application.stackName") + ".profile");
		returnStr = dataFile + "common" + getInitCofigProperty(profileType) + "/" + folderName;
		File profileFolder = new File(returnStr);

		if (!profileFolder.exists() || folderName.isEmpty()) {
			returnStr = returnStr.substring(0, returnStr.length() - folderName.length()) + "default";
			File defaultProfile = new File(returnStr);
			if (defaultProfile.exists()) {
				log("Browser Profile not found for " + appUrl + ", use default profile instead.", 3);
				log("Browser Profile used: " + returnStr);
			} else {
				log("Browser Profile not found for " + appUrl + ", use local profile instead.", 3);
				log("Browser Profile used: Local profile!");
			}
		} else {
			log("Browser Profile used: " + returnStr);
		}

		return returnStr;
	}

	/**
	 * 使用Javascript点击页面元素
	 * 
	 * @param jsScript 要点击的元素的定位符，两种格式：id=XX or tagname=XX
	 */
	public void clickElement(String jsScript) {

		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		String js = null;
		if (!jsScript.contains("=")) {
			throw new AssertionError("The format of the navigate element is incorrect.");
		}
		if (jsScript.contains("id")) {

			jsScript = jsScript.split("=")[1];
			js = "document.getElementById('" + jsScript + "').click()";
			jsExecutor.executeScript(js);
		}

		else if (jsScript.contains("tagname")) {

			jsScript = jsScript.split("=")[1];
			js = "document.getElementsByTagName('" + jsScript + "').click()";
			jsExecutor.executeScript(js);
		} else {
			throw new AssertionError("The parameter jsScript should only be 'id' or 'tagname'.");
		}
	}

	/**
	 * 移动鼠标到指定元素上
	 * 
	 * @param elementName 指定元素名，其为uiMap中定义的可用元素名
	 */
	public void hover(String elementName) {

		RemoteWebElement element = (RemoteWebElement) getElement(elementName);
		log("Moving mouse to element: " + elementName + ".");

		Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();
	}

	/**
	 * 关闭当前浏览器会话，清除框架历史记录，然后再启动一个新的浏览器
	 */
	public void restartBrowser() {

		log("Restarting the browser session.");
		close();
	//	clearHistory();

		startSeleniumServerAndBrowser();
		log("Restarted.");
	}

/*	*//**
	 * uiMap数据清零； 'uiMapViewList','uiMapAreasAlreadyChecked','uiMapViewIndex'中的对象将被清除
	 *//*
	public void clearHistory() {

		uiMapViewList.clear();
		uiMapAreasAlreadyChecked.clear();
		uiMapViewIndex = -1;
	}*/

	/**
	 * 键盘输入退出键（Esc）
	 */
/*	public void pressEscKey() {

		SmartRobot robot = new SmartRobot();
		robot.pressESC();
	}*/

	/**
	 * 获取当前页面的URL
	 */
	public String getCurrentUrl() {

		return driver.getCurrentUrl();
	}

	/**
	 * 获取指定元素的attribute属性值
	 * 
	 * @param elementName 为uiMap中定义的合法元素名字
	 * @param name 属性名
	 * @return 元素属性的当前值，如果HTML中没有被设置，则返回null
	 */
	public String getAttributeFromElementLocator(String elementName, String attribute) {

		WebElement element = getElement(elementName);
		String attributeValue = element.getAttribute(attribute);
		return attributeValue;
	}

	/**
	 * 切换到指定frame上
	 * 
	 * @param frameElement 指定frame，其为uiMap中定义的合法的frame名字
	 */
	public void switchIfarme(String frameElement) {

		// String locator = getElementLocator(frameElement);
		WebElement element = getElement(frameElement);
		driver.switchTo().frame(element);
		log("switch to ifarme");
	}

	/**
	 * 点击frame中的元素
	 * 
	 * @param elementLocator 指定元素的Xpath定位符，其为uiMap中定义的合法值
	 */
	public void clickElementIframe(String elementLocator) {

		boolean verify = driver.findElement(By.xpath(elementLocator)).isDisplayed();
		log("verify element if display=" + verify);
		if (verify) {
			driver.findElement(By.xpath(elementLocator)).click();
		}
	}

	/**
	 * 获取页面中相同类型（style）的元素的个数
	 * 
	 * @param elementName 为uiMap中定义的合法元素名字
	 * @return 同类元素的个数
	 */
	public int getAllElements(String elementName) {

		waitForElement(elementName);

		return getElements(elementName).size();
	}

	/**
	 * 滚动浏览器窗口，直到在当前窗口的可见范围内显示出指定元素
	 * 
	 * @param elementLocator 需要显示在窗口可见范围内的元素，为uiMap中定义的合法名字
	 * */
	// TODO: Need to modify elementLocator to elementName.
	public void scrollTo(String elementName) {

		waitForElement(elementName);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", getElement(elementName));
	}

	/**
	 * 滚动浏览器窗口到指定高度
	 * 
	 * @param height 浏览器窗口可见视图的高度
	 */
	public void scrollTo(int height) {

		String setscroll = "document.documentElement.scrollTop=" + height;
		((JavascriptExecutor) driver).executeScript(setscroll);
	}

	/**
	 * 滚动浏览器窗口，直到在当前窗口的可见范围内显示出指定元素
	 * 
	 * @param element 需要显示在窗口可见范围内的WebElement元素
	 */
	public void scrollTo(WebElement element) {

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", element);
	}

	/**
	 * 使用JS点击页面元素
	 * 
	 * @param elementName 元素名，为uiMap中定义的合法名字
	 */
	public void clickOnUseJS(String elementName) {

		// String locator = uiMapElementLocator(elementName);
		WebElement element = getElement(elementName);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	/**
	 * 滚动浏览器窗口到页面顶部
	 */
	public void scrollToTop() {

		String Js = "var q=document.documentElement.scrollTop=0";
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript(Js);

	}
}
