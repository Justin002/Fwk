package com.by.automate.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.by.automate.base.core.InitiaFramework;
import com.by.automate.utils.CommonTools;
import com.by.automate.utils.DriverUtil;
import com.by.automate.utils.ReadXmlUtils;

import ios.instrumentdriver.MySocket;
import ios.instrumentdriver.RunType;
import ios.instrumentdriver.UIAApplication;
import ios.instrumentdriver.UIAButton;
import ios.instrumentdriver.UIACollectionCell;
import ios.instrumentdriver.UIACollectionView;
import ios.instrumentdriver.UIAElement;
import ios.instrumentdriver.UIAImage;
import ios.instrumentdriver.UIALink;
import ios.instrumentdriver.UIANavigationBar;
import ios.instrumentdriver.UIAScrollView;
import ios.instrumentdriver.UIASecureTextField;
import ios.instrumentdriver.UIASegmentedControl;
import ios.instrumentdriver.UIASlider;
import ios.instrumentdriver.UIAStaticText;
import ios.instrumentdriver.UIASwitch;
import ios.instrumentdriver.UIATableCell;
import ios.instrumentdriver.UIATableView;
import ios.instrumentdriver.UIATarget;
import ios.instrumentdriver.UIATextField;
import ios.instrumentdriver.UIAWebView;
import ios.instrumentdriver.UIAWindow;
import ios.instrumentdriver.config.ResourceManager;

/**
 * @author Justin
 */
public abstract class InstrumentDriver extends InitiaFramework {

    private static final String instrumentTraceFileFolder = "./instrumentsDriver.trace";
    public UIATarget target;
    public UIAApplication app;
    public UIAWindow win;

    public Boolean isDebug = false;
    protected Boolean isRunSimulator = false;
    protected String udid = "";
    protected String appPath = "";
    private String[] cmd = {};
    protected static Process proc = null;
    public static int windowlenX = 0;
    public static int windowlenY = 0;

    public InstrumentDriver() {
        this("");
    }

    public InstrumentDriver(String configPath) {
        this(configPath, "");
    }

    public InstrumentDriver(String configPath, String appPath) {
        super(configPath, appPath);
    }

    protected String getAppType() {
        return appType = "instrumentDriver";
    }

    /**
     * @author Justin. Initialize App default profile.
     * 
     */
    protected void getConfigurationParameters() {

        projectLevelUiConfigPath = (configFile + getAppName() + "/" + "ui/").replace("//", "/");
        projectLevelDataConfigPath = (configFile + getAppName() + "/" + "data/").replace("//", "/");
        appLocale = getSutFullFileName("application.locale");
        String xmlFiles = getSutFullFileName("conf.ui." + getAppType() + ".xml");
        if (xmlFiles.contains(",")) {
            String[] uiFiles = xmlFiles.split(",");
            doc = readerTestData(uiFiles[0]);
            for (int i = 1; i < uiFiles.length; i++) {
                Document docs = readerTestData(projectLevelUiConfigPath + uiFiles[i].trim());

                doc = ReadXmlUtils.merge(doc, docs, uiPath + "merget.xml");
            }

        }

        connectPro = CommonTools.getTestDataFormatData(projectLevelDataConfigPath
                + getSutFullFileName("conf.ui." + getAppType() + ".content"), getAppType() + "_" + getAppName());

        appUrl = getSutFullFileName("application.url");
        apiUrl = getSutFullFileName("app.api.url");

        pageTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.page"), pageTimeout);
        viewTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.view"), viewTimeout);
        elementTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.element"), elementTimeout);

    }

    /**
     * 
     */
    protected void platformSupportInitiate(String appPath) {
        prepareResource();
        luanchApp();
    }

    /**
     * prepare resource files. <runShell.sh, tcpShell.sh, CSRunner.js,
     * common.js>
     * 
     */
    private void prepareResource() {
        try {

            ResourceManager.updateResource();

            String[] cmdrun = { "chmod", "777", String.format("%s", ResourceManager.getRunShell()) };
            Runtime.getRuntime().exec(cmdrun);
            String[] cmdtcp = { "chmod", "777", String.format("%s", ResourceManager.getTcpShell()) };
            Runtime.getRuntime().exec(cmdtcp);

            // 关闭已有的instruments进程，防止instruments造成内存泄露

        } catch (Exception e) {

            Assert.assertFalse(true, "update resources failed.");
        }
    }

    /**
     * Get class name of running class.
     * 
     * @return class name.
     */
    protected String getRunClassName() {

        try {
            ITestResult it = Reporter.getCurrentTestResult();

            String classNamePath = it.getTestClass().getName();
            String[] strs = classNamePath.split("\\.");
            return strs[strs.length - 1];
        } catch (Exception e) {
            log("None testNG executor detected, test may continue, but highly recommended to migrate your test to testNG.",
                    3);
        }
        return "";
    }

    /**
     * Create out put file. if file existence we need to delete it. if it is not
     * existence, we need to create it.
     * 
     * @return out put file.
     */
    private String createOutPutFile() {
        String outPut = "";
        try {

            outPut = ResourceManager.getInstrumentRoot() + "/log/" + getRunClassName();
            java.io.File file = new java.io.File(outPut);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            } else {
                // 如果存在删除旧的log
                System.out.println(CommonTools.getCurrentTime() + " INFO - Clean log.");
                CommonTools.deleteFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outPut;
    }

    /**
     * Start the execution of test cases and test cases executed cleanup of
     * instrument after start socket server.
     */
    private void killInstrumnetAndStartSocket() {
        try {
            // close instruments.
            killInstruments();
            // delete instrumnetTrace file folder.
            DriverUtil.delFolder(instrumentTraceFileFolder);
            // Start socket and set default time out.
            // MySocket scoket = new MySocket();
            MySocket.startSocket(Integer.parseInt(getSutFullFileName("socket.timeout")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Start socket server failed.");
        }
    }

    /**
     * Initialization App default profile and start App.
     */
    private void InitializationAppAndStartApp() {
        try {

            this.target = UIATarget.localTarget();
            this.app = target.frontMostApp();
            this.win = app.mainWindow();

            // this.elementTimeOut =
            // StringUtils.defaultIfEmpty(getSutFullFileName("socket.timeout"),
            // elementTimeOut);
            this.isRunSimulator = getSutFullFileName("app.isRunSimulator").equals("true") ? true : false;
            this.udid = getSutFullFileName("app.udid");
            this.appPath = dataFile + "IOS_App/" + getSutFullFileName("app.version") + "/" + getSutFullFileName("app.name");

            RunType.DEBUG = getSutFullFileName("app.isDebug").equals("true") ? true : false;
            System.out.println(CommonTools.getCurrentTime() + " INFO - " + "Launch the application froma the path ["
                    + appPath + "].");
            String shellCmd = String.format("%s %s %s %s %s %s %s", ResourceManager.getRunShell(), appPath,
                    ResourceManager.getInstrumentRoot(), isRunSimulator, udid, getSutFullFileName("xcode.path"),
                    createOutPutFile());

            this.cmd = new String[] { "/bin/sh", "-c", shellCmd };

            System.out.println(CommonTools.getCurrentTime() + " INFO - shellCmd: " + shellCmd);
            proc = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("start app failed.");
        }
    }

    /**
     * Verify the app is start.
     */
    private void iSStartApp() {

        for (int i = 0; i < 3; i++) {
            try {

                String appName = app.getBundelID(10000);
                boolean off = false;
                long currentTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - currentTime) / 1000 < 20) {
                    if (appName.equals(getSutFullFileName("app.bundleID"))) {
                        off = true;
                        break;
                    }
                }
                if (off) {
                    System.out.println(CommonTools.getCurrentTime() + " INFO - " + "Start the app successfully.");
                    break;
                }
                throw new Exception();

            } catch (Exception e) {
                if (i < 2) {
                    try {
                        log("Close the server listen to port: 5566, try restarted app. ");
                        MySocket.tearDownSocket();
                        MySocket.startSocket(Integer.parseInt(getSutFullFileName("socket.timeout")));
                        String shellCmd = String.format("%s %s %s %s %s %s %s", ResourceManager.getRunShell(), appPath,
                                ResourceManager.getInstrumentRoot(), isRunSimulator, udid, getSutFullFileName("xcode.path"),
                                createOutPutFile());

                        this.cmd = new String[] { "/bin/sh", "-c", shellCmd };

                        log("shellCmd: " + shellCmd);
                        proc = Runtime.getRuntime().exec(cmd);

                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }
                } else {
                    throw new Error("luanch app failed, Please disconnect the phone and try again.");
                }

            }
        }

    }

    protected void luanchApp() {
        try {
            killInstrumnetAndStartSocket();
            InitializationAppAndStartApp();

            iSStartApp();

            windowlenX = getWinWidth();
            windowlenY = getWinHeight();
            new Thread(new ReadInstrumentsOutPut(), "instruments").start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close App and all server.
     */
    public void tearDown() {
        try {

            MySocket.sendExit();
            MySocket.tearDownSocket();
            proc.destroy();
            // 关闭已有的instruments进程，防止instruments造成内存泄露
            killInstruments();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class ReadInstrumentsOutPut implements Runnable {

        public void run() {

            try {
                InputStream input = proc.getInputStream();
                InputStreamReader inputReader;

                inputReader = new InputStreamReader(input, "UTF-8");

                BufferedReader bufferR = new BufferedReader(inputReader);

                String str = null;
                boolean off = getSutFullFileName("app.isLog").equals("true") ? true : false;
                String URL = testRoot.replace("/target/test-classes/", "") + "/target/InstrumentDriver/log/"
                        + getRunClassName() + "/Print_Log";
                CommonTools.writeDate(URL, "**********************************************" + getRunClassName()
                        + "**********************************************");
                while ((str = bufferR.readLine()) != null) {
                    // log(str, 5);
                    if (str.contains("The target application appears to have died")) {

                        ITestResult it = Reporter.getCurrentTestResult();

                        it.setStatus(3);
                        System.exit(0);

                    }
                    if (off) {

                        CommonTools.writeDate(URL,
                                CommonTools.getCurrentTime() + " Log - Instruments:\t" + str.replace(" +0000", ""));
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class DefauleSendRequest implements Runnable {

        public void run() {
            while (true) {
                log("默认发送请求 支持服务端与客户端的支持.");
                try {
                    win.isVisible();
                    waitByTimeOut(1000);

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * 开始执行测试用例和测试用例执行完清理instruments进程
     * <p>
     * 测试用例批量执行时发现有instruments睡死现象
     * 
     * @throws Exception
     */
    private void killInstruments() throws Exception {
        System.out.println(CommonTools.getCurrentTime() + " INFO - Kill Instruments: run {killall -9 'instruments'}.");

        Runtime.getRuntime().exec("killall -9 'instruments'");
        Runtime.getRuntime().exec("killall -9 'Instruments'");
        Runtime.getRuntime().exec("killall -9 instruments");
        Runtime.getRuntime().exec("killall -9 Instruments");
    }

    /****************************************************************
     * 
     * @author : Justin.
     * @Date : 2015/09/21.
     * @Details: Add Methods: getXMLName, getResourcesRoot, fullPath, openApp.
     * 
     */

    public void openApp(String pageView) {

        uiMapSetView(pageView);
    }

    public void openApp() {
        uiMapSetView();
    }

    public void installApp() {
        installApp("");
    }

    public void installApp(String appName) {
        if (StringUtils.isEmpty(appName)) {
            appName = getSutFullFileName("app.name");
        }
        Process process = null;
        Runtime run = Runtime.getRuntime();
        try {
            String cmd = "//Applications/Appium3.app/Contents/Resources/node_modules/appium/build/fruitstrap/fruitstrap install --bundle "
                    + getResourcesRoot() + "data/IOS_App/" + getSutFullFileName("app.version") + "/" + appName;
            log("Install app : " + cmd);
            process = run.exec(cmd);
            InputStream input = process.getInputStream();
            InputStreamReader inputReader;

            inputReader = new InputStreamReader(input, "UTF-8");
            BufferedReader bufferR = new BufferedReader(inputReader);
            String str = null;

            while ((str = bufferR.readLine()) != null) {
                if (str.contains("100%") || str.contains("[....]") || str.contains("[  0%]")) {

                    if (str.contains("[  0%]")) {

                    }
                    log(str.split("]")[1].trim() + "  " + StringUtils.substringBetween(str, "[", "]").trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unInstallApp(String bundleID) {
        Runtime run = Runtime.getRuntime();
        try {
            run.exec("//Applications/Appium3.app/Contents/Resources/node_modules/appium/build/fruitstrap/fruitstrap uninstall --bundle "
                    + bundleID);
        } catch (IOException e) {

        }
    }

    public void unInstallApp() {
        Runtime run = Runtime.getRuntime();
        try {
            String cmd = "//Applications/Appium3.app/Contents/Resources/node_modules/appium/build/fruitstrap/fruitstrap uninstall --bundle "
                    + getSutFullFileName("app.bundleID");
            log("Uninstall App: " + cmd + ".");
            run.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get test resources path.
     * 
     * @return
     */
    private String getResourcesRoot() {

        return CommonTools.getTestRoot().replace("target/test-classes", "src/test/resources");
    }

    public void assertEquest(Object expected, Object actual) {
        assertEquest(expected, actual, "");
    }

    /********************************
     * Asserts that two Strings are equal.
     */

    public void assertEquest(Object expected, Object actual, String message) {
        if (expected instanceof String && actual instanceof String) {
            expected = ((String) expected).trim();
            actual = ((String) actual).trim();
        }
        log("Asserts that two Strings are equal." + " [(" + expected + ") equals (" + actual + ")]");
        Assert.assertEquals(expected, actual, message);
    }

    public void assertNotEquest(String expected, String actual) {

        Assert.assertTrue(!StringUtils.equals(expected, actual), "The expected(" + expected + ") and actual(" + actual
                + ") value is equality.");
    }

    public void assertTrue(boolean condition, String message) {
        /*
         * if(!condition){ takeFullScreenShot("ScreentShot"); }
         */
        Assert.assertTrue(condition, message);

    }

    public void assertContains(String expected, String actual) {
        log("Checks if String contains a search String ." + " [(" + expected + ") contains (" + actual + ")]");
        boolean b = StringUtils.contains(expected, actual);
        Assert.assertTrue(b);
    }

    public void assertContains(String expected, String actual, String message) {
        log("Checks if String contains a search String ." + " [(" + expected + ") contains (" + actual + ")]");
        boolean b = StringUtils.contains(expected, actual);
        Assert.assertTrue(b, message);
    }

    public void assertContainsIgnoreCase(String expected, String actual) {
        log("Checks if String contains a search String ." + " [(" + expected + ") contains (" + actual + ")]");
        boolean b = StringUtils.containsIgnoreCase(expected, actual);
        Assert.assertTrue(b);
    }

    /*****************************************************************************************************
     * 
     * @param key
     * @return
     */

    /**
     * Get element type.
     * 
     * @param locator
     * @return
     */
    protected String getLocatorType(String locator) {
        if (locator == null) {
            fail("Element locator is empty on " + pageName + ": " + viewName + ".");
        }
        if (locator.matches("\\S*==.*")) {
            return locator.split("==")[0];
        }
        return "";
    }

    /**
     * Get the locator String from locater specified.
     * 
     * @param locator
     * @return locatorString
     */
    protected String getLocatorStr(String locator) {

        if (locator.isEmpty())
            return "";

        String locatorType = getLocatorType(locator);

        if (locator.startsWith(locatorType))
            return locator.substring(locatorType.length() + 2);
        else
            return locator;
    }

    /**
     * 执行javaScript 脚本.
     * 
     * @param elementName
     * @param operate
     */
    protected void runScript(String elementName, String operate) {
        String script = getLocatorConvertToStr(elementName);
        try {
            MySocket.getVoid(script + operate);
        } catch (Exception e) {

            e.printStackTrace();
            fail("Run script " + script + operate + " is failed.");
        }
    }

    protected UIAElement findElement(String elementName) throws Exception {

        return findElement(elementName, null);
    }

    /**
     * Get element locator.
     * 
     * @param elementName
     * @param value
     */
    protected String getElementLocator(String elementName) {
        List<String> info = getElementInfo(elementName);
        if (info == null) {
            return "";
        }
        String returnValue = "";
        for (int i = 0; i < info.size(); i++) {
            String value = info.get(i);
            if (value.contains("type=")) {
                value = "<" + value.replace("type=", "") + ">=";

                returnValue += value;
            }
            if (value.contains("value=")) {
                value = value.replace("value=", "");

                returnValue += value;
            }

        }
        return returnValue;

    }

    /**
     * Example : locator - //UIAWindow/Element[3]/Element[0]
     * 
     * @param locator
     */

    protected UIAElement findElement(String elementName, Object itMatch) throws Exception {
        // xpath
        String locator = getElementLocator(elementName);
        String elementType = getLocatorType(locator);
        String elementLocator = getLocatorStr(locator);

        UIAElement currentElement = win;
        if (StringUtils.equals(elementType, "xpath")) {
            findElementByXpath(elementLocator);

        } else if (StringUtils.equals(elementType, "name")) { // name,

            currentElement = currentElement.findElementByText(elementLocator);

        } else if (StringUtils.equals(elementType, "uiautomation")) {

            currentElement = getElementsByUiAutomation(elementLocator, itMatch);

        } else {
            throw new Exception("定位符类型不对.");
        }
        return currentElement;

    }

    protected UIAElement findElementByXpath(String elementLocator) {

        UIAElement currentElement = win;
        try {
            String[] elements = elementLocator.replace("//UIAWindow/", "").split("/");
            String currentLocator = "";
            int index = 0;
            // win.elements()[0].element
            for (int i = 0; i < elements.length; i++) {
                currentLocator = elements[i];
                if (StringUtils.contains(currentLocator, "Elements")) {
                    index = Integer.parseInt(StringUtils.substringsBetween(currentLocator, "[", "]")[0]);
                    currentElement = currentElement.elements()[index];
                    continue;
                }
            }
        } catch (Exception e) {
            throw new Error("find element failed by xpath whit element locator " + elementLocator + ".");
        }

        return currentElement;

    }

    private String getKeyWord(String locator) {
        return StringUtils.substringBetween(locator, "[", "]");
    }

    public boolean isNum(String keyCode) {

        if (!StringUtils.isEmpty(keyCode)) {
            // key Code is number.
            if (StringUtils.isNumeric(keyCode)) {
                return true;
            } else { // key code is String.
                return false;
            }

        } else {
            fail("uiautomation 格式不对. 缺失索引值.");
        }

        return false;

    }

    /*
     * public <T> Class<T> returnObject(String name){
     * if(name.equals("webView")){ return (Class<T>) UIAWebView.class; } return
     * null; }
     */

    // UIAWindow/UIACollectionView[1]/UIACollectionCell[0]/UIACollectionView[0]/UIACollectionCell[1]/UIAStaticText[0]
    protected UIAElement getElementsByUiAutomation(String elementLocator, Object itMatch) throws Exception {

        UIAElement currentElement = null;
        if (elementLocator.contains("UIAWindow") && !elementLocator.contains("Elements")) {
            String currentLocator = "";
            currentElement = win;
            String[] elements = elementLocator.replace("//UIAWindow", "").split("/UIA");
            for (int i = 0; i < elements.length - 1; i++) {
                currentLocator = elements[i + 1];
                String keyWord = getKeyWord(currentLocator);
                if (currentLocator.contains("CollectionView")) {
                    if (currentLocator.equals("CollectionView")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.collectionViews();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.collectionViews()[index];
                    } else {
                        // index = getIndex(currentLocator);
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.collectionViews()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIACollectionView.class, "collectionViews()",
                                    keyWord);
                        }
                    }
                    continue;

                } else if (currentLocator.contains("CollectionCell")) {
                    if (currentLocator.equals("CollectionCell")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.cells();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.cells()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.cells()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIACollectionCell.class, "cells()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("StaticText")) {
                    if (currentLocator.equals("StaticText")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.staticTexts();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.staticTexts()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.staticTexts()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIAStaticText.class, "staticTexts()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("NavigationBar")) {
                    if (currentLocator.equals("NavigationBar")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.navigationBars();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.navigationBars()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.navigationBars()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIANavigationBar.class, "navigationBars()",
                                    keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Button")) {
                    if (currentLocator.equals("Button")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.buttons();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.buttons()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.buttons()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIAButton.class, "buttons()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("ScrollView")) {
                    if (currentLocator.equals("ScrollView")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.scrollViews();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.scrollViews()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.scrollViews()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIAScrollView.class, "scrollViews()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("WebView")) {
                    if (currentLocator.equals("WebView")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.webViews();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.webViews()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.webViews()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIAWebView.class, "webViews()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("TextField") && !currentLocator.contains("SecureTextField")) {
                    if (currentLocator.equals("TextField")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.textFields();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.textFields()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.textFields()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIATextField.class, "textFields()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("SecureTextField")) {
                    if (currentLocator.equals("SecureTextField")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.secureTextFields();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.secureTextFields()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.secureTextFields()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIASecureTextField.class, "secureTextFields()",
                                    keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Switch")) {
                    if (currentLocator.equals("Switch")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.switches();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.switches()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.switches()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIASwitch.class, "switches()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Image")) {
                    if (currentLocator.equals("Image")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.images();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.images()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.images()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIAImage.class, "images()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Slider")) {
                    if (currentLocator.equals("Slider")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.sliders();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.sliders()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.sliders()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIASlider.class, "sliders()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Link")) {
                    if (currentLocator.equals("UIALink")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.links();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.links()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.links()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIALink.class, "links()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("TableView")) {
                    if (currentLocator.equals("TableView")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.tableViews();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.tableViews()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.tableViews()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIATableView.class, "tableViews()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("TableCell")) {
                    if (currentLocator.equals("TableCell")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.cells();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.cells()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.cells()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIATableCell.class, "cells()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("SegmentedControl")) {
                    if (currentLocator.equals("SegmentedControl")) {
                        int index = 0;
                        UIAElement[] findElements = null;
                        if (itMatch instanceof Integer) {
                            index = (Integer) itMatch;
                        } else { // 当是文本时候需要判断 // 排序。
                            findElements = currentElement.segmentedControls();
                            for (int j = 0; j < elements.length; j++) {
                                String text = findElements[j].name();
                                if (text.equals(itMatch)) {
                                    index = j;
                                }
                            }
                        }
                        currentElement = currentElement.segmentedControls()[index];
                    } else {
                        if (isNum(keyWord)) {
                            int index = Integer.parseInt(keyWord);
                            currentElement = currentElement.segmentedControls()[index];
                        } else {
                            currentElement = currentElement.findParentByText(UIASegmentedControl.class,
                                    "segmentedControls()", keyWord);
                        }
                    }

                    continue;

                } else if (currentLocator.contains("Alert")) {
                    currentElement = app.alert();
                    continue;

                } else if (currentLocator.contains("ActionSheet")) {
                    currentElement = app.actionSheet();
                    continue;

                } else if (currentLocator.contains("ActivityView")) {
                    if (currentLocator.equals("ActivityView")) {
                        /*
                         * int index = 0; UIAElement[] findElements = null; if
                         * (itMatch instanceof Integer) { index = (Integer)
                         * itMatch; } else { // 当是文本时候需要判断 // 排序。 findElements =
                         * currentElement.activityViews(); for (int j = 0; j <
                         * elements.length; j++) { String text =
                         * findElements[j].name(); if (text.equals(itMatch)) {
                         * index = j; } } } currentElement =
                         * currentElement.activityViews()[index]; } else { if
                         * (isNum(keyWord)) { int index =
                         * Integer.parseInt(keyWord); currentElement =
                         * currentElement.activityViews()[index]; } else {
                         * currentElement =
                         * currentElement.findParentByText(UIAActivityView
                         * .class, "activityView()", keyWord); }
                         */
                        currentElement = currentElement.activityViews()[0];
                    }

                    continue;

                } else {
                    assertTrue(false, "控件名字不对.");
                }
            }

        }

        return currentElement;

    }

    protected String getLocatorConvertToStr(String elementName) {

        String elementLocator = getElementLocator(elementName);
        String locatorStr = getLocatorStr(elementLocator);
        String script = "";
        if (locatorStr.contains("UIAWindow")) {
            script = "win.";
        }
        if (locatorStr.contains("UIAApplication")) {
            script = "app.";
        }

        String elements = locatorStr.replace("//UIAWindow/", "");
        elements = elements.replace("/", ".");
        elements = elements.replace("UIACollectionView", "collectionViews()");
        elements = elements.replace("UIACollectionCell", "cells()");
        elements = elements.replace("UIAStaticText", "staticTexts()");
        elements = elements.replace("UIANavigationBar", "navigationBars()");
        elements = elements.replace("UIAButton", "buttons()");
        elements = elements.replace("UIAScrollView", "scrollViews()");
        elements = elements.replace("UIAWebView", "webViews()");
        elements = elements.replace("UIAActivityView", "activetyView()");
        elements = elements.replace("UIAAlert", "alert()");
        elements = elements.replace("UIAActionSheet", "actionSheet()");
        elements = elements.replace("UIATableView", "tableViews()");
        elements = elements.replace("UIATableCell", "cells()");

        // script = script + elements;
        String[] elementStr = elements.split("\\.");
        for (int i = 0; i < elementStr.length; i++) {
            String currentElement = elementStr[i];
            if (currentElement.contains("[") || currentElement.contains("]")) {
                String keyCode = StringUtils.substringBetween(currentElement, "[", "]");
                if (!isNum(keyCode)) {
                    currentElement = StringUtils.replace(currentElement, "[" + keyCode + "]", "[" + "\"" + keyCode + "\""
                            + "]");
                }
            }

            script += currentElement + ".";

        }

        if (script.contains("alert") || script.contains("actionSheet")) {

            script = script.replace("win.", "app.");
        }

        return StringUtils.substring(script, 0, script.length() - 1);

    }

    @SuppressWarnings("rawtypes")
    protected <T> Class getObject(String node) throws Exception {

        if (StringUtils.equals(node, "staticTexts")) {
            return UIAStaticText.class;

        }
        if (StringUtils.equals(node, "buttons")) {
            return UIAButton.class;

        }
        if (StringUtils.equals(node, "scrollViews")) {
            return UIAScrollView.class;

        }

        return null;
    }

    public String getElementsJSON(String elementName, String childNode) {
        try {
            String script = getLocatorConvertToStr(elementName);
            if (StringUtils.isEmpty(childNode)) {
                return MySocket.getJSONArray(script);

            } else
                return MySocket.getJSONArray(script + "." + childNode + "()");
        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException(e.getMessage());
        }

    }

    public String getElementsJSON(String elementName) {
        return getElementsJSON(elementName, "");
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public List<UIAElement> findElements(String elementName, String childNode) {
        try {

            String elementsJSON = getElementsJSON(elementName, childNode);
            JSONArray jsonElementArray = JSONArray.fromObject(elementsJSON);
            return JSONArray.toList(jsonElementArray, getObject(childNode));

        } catch (Exception e) {
            e.printStackTrace();

            fail("Find element is failed, please check element " + elementName + " locator.");
        }

        return null;

    }

    public boolean verifyElementValid(String elementName) {
        return verifyElementValid(elementName, null);
    }

    public boolean verifyElementValid(String elementName, Object match) {
        boolean returnValue = false;
        log("Looking for " + elementName + ".");
        returnValue = waitForElementValid(elementName, match, (Integer.parseInt(elementTimeout) / 1000));
        if (!returnValue) {
            getScreenShot(elementName);
            log("FAIL verifyDisplay() - Could not find  (" + elementName
                    + "), May be element locator is wrong or Network timeout to element loading failures. ", 2);
            throw new NullPointerException("FAIL verifyDisplay() - Could not find  (" + elementName
                    + "), May be element locator is wrong or Network timeout to element loading failures. ");
        }

        return returnValue;
    }

    public boolean waitForElementValid(String elementName, int elementTimeOut) {
        return waitForElementValid(elementName, null, elementTimeOut);
    }

    public boolean waitForElementValid(String elementName, Object match, int elementTimeOut) {
        UIAElement closeButton = null;
        long getTime = System.currentTimeMillis();
        while (true) {
            try {
                closeButton = findElement("closeButtonForIOS");
                if (findElement(elementName, match).isValid()) {
                    return true;
                } else {
                    if ((System.currentTimeMillis() - getTime) / 1000 > elementTimeOut) {

                        return false;

                    } else {
                        closeBigForIOS(closeButton);
                    }
                }
            } catch (Exception e) {
                if ((System.currentTimeMillis() - getTime) / 1000 > elementTimeOut) {
                    e.printStackTrace();
                    log("Find element name " + elementName + " is Nill, on the " + elementTimeOut + " seconds.");
                    return false;
                } else {
                    closeBigForIOS(closeButton);
                }
            }
            waitByTimeOut(500);
        }

    }

    public boolean waitForElementReadyOnTimeOut(String elementName, int elementTimeOut) {
        return waitForElementReadyOnTimeOut(elementName, null, elementTimeOut);
    }

    public boolean waitForElementReadyOnTimeOut(String elementName, Object itMatch, int elementTimeOut) {
        UIAElement closeButton = null;
        long beginTime = System.currentTimeMillis();
        while (true) {
            try {
                closeButton = findElement("closeButtonForIOS");
                if (findElement(elementName, itMatch).isVisible()) {
                    return true;
                } else {
                    if (!waitForTime(beginTime, elementTimeOut)) {
                        log("wait for element " + elementName + " is not found on the " + elementTimeOut + " seconds.");
                        return false;
                    } else {
                        closeBigForIOS(closeButton);
                    }
                }
            } catch (Exception e) {
                if (!waitForTime(beginTime, elementTimeOut)) {
                    e.printStackTrace();
                    log("Find element name " + elementName + " is Nill, on the " + elementTimeOut + " seconds.");
                    return false;
                } else {
                    closeBigForIOS(closeButton);
                }
            }
            waitByTimeOut(500);
        }
    }

    public boolean verifyIsShown(String elementName) {

        return verifyIsShown(elementName, null);

    }

    public boolean verifyIsShown(String elementName, String message) {

        return verifyIsShown(elementName, null, message);

    }

    public boolean verifyIsShown(String elementName, Object itMatch) {
        return verifyIsShown(elementName, itMatch, "");
    }

    public boolean verifyIsShown(String elementName, Object itMatch, String message) {
        boolean returnValue = false;
        if (message == null) {
            log("Looking for" + elementName);
        } else
            log(message + ".");
        returnValue = waitForElementReadyOnTimeOut(elementName, itMatch, (Integer.parseInt(elementTimeout) / 1000));
        if (!returnValue) {
            getScreenShot(elementName);
            log("FAIL verifyDisplay() - Could not find  (" + elementName
                    + "), May be because element locator is wrong or Network timeout to element loading failures. ", 2);
            if (message == null) {
                message = "FAIL verifyDisplay() - Could not find  (" + elementName
                        + "), May be because element locator is wrong or Network timeout to element loading failures. ";
            }
            throw new NullPointerException(message + ", can not find in the 60 second.");
        }

        return returnValue;

    }

    protected boolean waitForTime(long beginTime, int timeOut) {
        long missTime = System.currentTimeMillis();
        if ((missTime - beginTime) / 1000 > timeOut) {
            return false;
        }
        return true;

    }

    protected void closeBigForIOS(UIAElement closeButton) {
        try {
            if (closeButton.isVisible()) {
                log("Tap on close big button.");
                closeButton.tap();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void fail(String message) {

        Assert.fail(message);
    }

    public void printLogElementTree() {
        try {
            win.logElementTree();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void tapOn(String elementName) {
        tapOn(elementName, "");
    }

    public void tapOn(String elementName, String message) {

        tapOn(elementName, null, message);

    }

    public void tapOfElementXY(String elementName) {

        tapOfElementXY(elementName, null);
    }

    public void tapOfElementXY(String elementName, Object match) {

        int x = getElementX(elementName, match) + 8;
        int y = getElementY(elementName, match) + 8;
        tapOn(x + 5, y + 5);
    }

    public void tapOn(int x, int y) {
        // target.frontMostApp().mainWindow().collectionViews()[0].tapWithOptions({tapOffset:{x:0.74,
        // y:0.47}});
        // target.frontMostApp().mainWindow() tapWithOptions({tapOffset:{x:"75",
        // y:"366"}})
        // MySocket.getVoid(this.guid + ".tapWithOptions(" + tapOffset + ")");
        try {
            log("tap on {x: " + x + " , y: " + y + "}.");
            target.tap("{x: " + x + ", y: " + y + "}");
        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("tap on {x: " + x + " , y: " + y + "} failed.");
        }
    }

    public void tapOn(String elementName, Object itMatch) {
        tapOn(elementName, itMatch, "");
    }

    protected String getTargetView(String type, String elementName, String direction) {

        String targetView = "";
        if (!StringUtils.isEmpty(type)) {
            if (type.equalsIgnoreCase("click")) {
                return getElementAttValue(elementName, "view");
            }

            if (type.equalsIgnoreCase("gestures")) {
                if (!StringUtils.isEmpty(direction)) {
                    try {
                        targetView = getViewAttributeValue(direction);

                        if (targetView.contains(":")) {
                            return StringUtils.substringBetween(targetView, "{", "}");
                        } else {
                            throw new NumberFormatException("The format is error");
                        }
                    } catch (Exception e) {
                    }

                } else {
                    throw new NullPointerException("The " + type + " is null.");
                }
            }

            if (type.equalsIgnoreCase("tap")) {
                return getElementAttValue("view", elementName);
            }

            if (type.equalsIgnoreCase("press")) {
                return getElementAttValue("pressView", elementName);
            }
        } else {
            throw new AssertionError("The type is null");
        }
        return "";

    }

    public void tapOn(String elementName, Object itMatch, String message) {
        String view = getTargetView("tap", elementName, "");
        try {
            log("Tap on " + elementName + ".");

            if (waitForElementReadyOnTimeOut(elementName, 5)) {
                findElement(elementName, itMatch).tap();
            } else {
                throw new NullPointerException();
            }

            if (!StringUtils.isEmpty(view)) {
                log("Setting page-view : " + view + ".");
                uiMapSetView(view);
            }
        } catch (Exception e) {
            getScreenShot();
            if (message == null) {
                message = "tap on element " + elementName + " failed, May be element is invalid or element is not enable.";
            }
            fail(message);
        }

    }

    public String getValue(String elementName) {
        return getValue(elementName, null);
    }

    public String getValue(String elementName, Object itMatch) {
        try {
            String value = findElement(elementName, itMatch).value();

            return value;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Get element " + elementName + " value failed.");
        }
        return "";

    }

    public String getName(String elementName) {
        return getName(elementName, null);
    }

    public String getName(String elementName, Object itMatch) {
        try {
            String value = findElement(elementName, itMatch).name();

            return value;

        } catch (Exception e) {
            e.printStackTrace();
            fail("Get element " + elementName + " name failed.");
        }
        return "";

    }

    public String getValueOf(String elementName) {

        return getValueOf(elementName, null);

    }

    public String getValueOf(String elementName, Object itMatch) {

        try {
            String value = findElement(elementName, itMatch).name();
            if (value.isEmpty()) {
                return findElement(elementName, itMatch).value();
            } else {
                return value;
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Get element " + elementName + " value of failed.");
        }
        return "";
    }

    public void waitByTimeOut(int timeOut) {

        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void swipeToElement(String elementName) {
        swipeToElement(elementName, null, 0);
    }

    public void swipeToElement(String elementName, int i) {
        swipeToElement(elementName, null, i);
    }

    public void swipeToElement(String elementName, Object itMatch) {
        swipeToElement(elementName, itMatch, 0);
    }

    public void swipeToElement(String elementName, Object itMatch, int i) {
        boolean off = false;
        if (i == 0) {
            off = true;
        }
        while (off) {
            if (waitForElementReadyOnTimeOut(elementName, itMatch, 2)) {
                break;
            } else {

                swipe("up");
                // target.scrollUp();
            }

            waitByTimeOut(1000);
        }

        for (int j = 0; j < i; j++) {

            if (waitForElementReadyOnTimeOut(elementName, itMatch, 2)) {
                break;
            } else {

                swipe("up");
                // target.scrollUp();
            }

            waitByTimeOut(1000);

        }

    }

    public void swipeToElementWithDriection(String elementName, Object itMatch, String direction) {
        while (true) {
            if (waitForElementReadyOnTimeOut(elementName, itMatch, 2)) {
                break;
            } else {

                swipe(direction);
                // target.scrollUp();
            }

            waitByTimeOut(1000);
        }
    }

    public void swipeToElementWithDriection(String elementName, String direction) {
        swipeToElementWithDriection(elementName, null, direction);
    }

    public void swipeToElementIsValid(String elementName) {
        swipeToElementIsValid(elementName, "up");
    }

    public void swipeToElementIsValid(String elementName, String direction) {
        for (int i = 0; i < 8; i++) {
            if (waitForElementValid(elementName, 2)) {
                break;
            } else {

                swipe(direction);
                // target.scrollUp();
            }
            waitByTimeOut(2000);
        }

    }

    public void swipe(int beginx, int beginY, int endX, int endY, int time) {
        try {
            log("Swipe: {{x : " + beginx + ", y :" + beginY + "}, {endX : " + endX + ", endY :" + endY + "})");
            target.swipe(beginx, beginY, endX, endY, time);
        } catch (Exception e) {
            // TODO: handle exception
            fail("Swipe failed.");
        }
    }

    public void swipe(String type) {
        try {
            /*
             * int windowlenX = win.getWidth(); int windowlenY =
             * win.getHeight();
             */
            String swipeLeft = "left";
            String swipeLeftSide = "leftSide";
            String swipeRight = "right";
            String swipeRightSide = "rightSide";
            String swipeUp = "up";
            String swipeTop = "top";
            String swipeDown = "down";
            String swipeBottom = "bottom";

            // Sliding screen to the left
            if (type.equalsIgnoreCase(swipeLeft)) {
                log("SWIPE : Sliding screen to the left");
                swipe((int) (windowlenX * 0.9), (int) (windowlenY * 0.5), (int) (windowlenX * 0.2),
                        (int) (windowlenY * 0.5), 1);
            }

            // From the left of screen to began to slip
            if (type.equalsIgnoreCase(swipeLeftSide)) {
                log("SWIPE : From the LeftSide of screen to right.");
                swipe(1, (int) (windowlenY * 0.5), (int) (windowlenX * 0.9), (int) (windowlenY * 0.5), 1);
            }

            // Sliding screen to the right
            if (type.equalsIgnoreCase(swipeRight)) {
                log("SWIPE : Sliding screen to the right");
                swipe((int) (windowlenX * 0.2), (int) (windowlenY * 0.5), (int) (windowlenX * 0.9),
                        (int) (windowlenY * 0.5), 1);
            }

            // From the right of screen to began to slip
            if (type.equalsIgnoreCase(swipeRightSide)) {
                log("SWIPE : From the RightSide of screen to left.");
                swipe((int) (windowlenX * 0.9), (int) (windowlenY * 0.5), (int) (windowlenX * 0.2),
                        (int) (windowlenY * 0.5), 1);
            }

            // Screen upward sliding
            if (type.equalsIgnoreCase(swipeUp)) {
                log("SWIPE : Screen upward sliding.");
                swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5),
                        (int) (windowlenY * 0.4), 1);
            }

            // From the top of screen to began to slip
            if (type.equalsIgnoreCase(swipeTop)) {
                log("SWIPE : From the top of screen to top.");
                swipe((int) (windowlenX * 0.5), 0, (int) (windowlenX * 0.5), (int) (windowlenY * 0.8), 1);
            }

            // Slide down the screen
            if (type.equalsIgnoreCase(swipeDown)) {
                log("SWIPE : Slide down the screen.");
                swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.3), (int) (windowlenX * 0.5),
                        (int) (windowlenY * 0.9), 1);
            }

            // From the bottom of screen to began to slip
            if (type.equalsIgnoreCase(swipeBottom)) {
                log("SWIPE :From the bottom of screen to top .");
                swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5),
                        (int) (windowlenY * 0.1), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Swipe failed.");
        }

    }

    public void getScreenShot() {

        getScreenShot("");
    }

    public void getScreenShot(String failure) {
        try {

            String timeStamp = CommonTools.getDate().replace("-", "") + "_"
                    + CommonTools.getCurrentTime().replace(":", "").replace(".", "");
            failure = timeStamp + "_" + CommonTools.replaceIllegalFileName(failure, "_");
            if (StringUtils.endsWith(failure, "_"))
                failure = timeStamp;
            target.captureScreenWithName(failure);

            waitByTimeOut(1500);
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    public int getElementWidth(String elementName) {

        try {
            return findElement(elementName).getWidth();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new NullPointerException(e.getMessage());
        }

    }

    public int getElementHeight(String elementName) {

        try {
            return findElement(elementName).getHeight();
        } catch (Exception e) {
            // TODO Auto-generated catch block

            throw new NullPointerException(e.getMessage());
        }

    }

    public int getElementX(String elementName) {
        return getElementX(elementName, null);

    }

    public int getElementX(String elementName, Object match) {
        try {
            if (waitForElementValid(elementName, match, 5)) {
                return (int) findElement(elementName, match).getX();
            } else {
                fail("wait for element is not found..");
            }
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }

        return 0;

    }

    public int getElementY(String elementName) {
        return getElementY(elementName, null);

    }

    public int getElementY(String elementName, Object match) {
        try {
            if (waitForElementValid(elementName, match, 5)) {
                return (int) findElement(elementName, match).getY();
            } else {
                fail("wait for element is not found..");
            }
        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }

        return 0;

    }

    public int getWinWidth() {
        try {
            return win.getWidth();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Get window width failed.");
        }
        return 0;
    }

    public int getWinHeight() {
        try {
            return win.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Get window height failed.");
        }
        return 0;
    }

    public void setValue(String elementName, String value) {

        if (waitForElementValid(elementName, 5)) {
            try {
                findElement(elementName).setVal(value);

            } catch (Exception e) {
                e.printStackTrace();
                fail("set value [" + value + "] failed.");
            }
        }
    }

    public void setValueByKeyBoard(String value) {
        try {
            app.keyboard().typeString(value);
        } catch (Exception e) {
            fail("set value [" + value + "] failed by key board.");
        }
    }

    public void clickKeyFinish() {
        try {
            MySocket.getVoid("app" + ".windows()[1].toolbar().buttons()['完成'].tap()");
        } catch (Exception e) {

            e.printStackTrace();
            fail("Running : app" + ".windows()[1].toolbar().buttons()['完成'].tap(); failed.");
        }

    }

    public void clickKeyFinishOnSharePaly() {
        try {
            MySocket.getVoid("app" + ".windows()[2].toolbar().buttons()['完成'].tap()");
        } catch (Exception e) {

            e.printStackTrace();
            fail("Running : app" + ".windows()[2].toolbar().buttons()['完成'].tap(); failed.");
        }

    }

    public String getNameOfAlert() {

        try {
            return MySocket.getText("app" + ".alert().name()");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Running : app.alert().name(); failed.");
        }

        return "";
    }

    public String getBundleID() {

        try {

            return MySocket.getText("app.bundleID()");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Running : app.bundleID(); failed.");
        }

        return "";
    }

    public void setValueOfSwitch(String elementName, boolean value) {
        try {
            findElement(elementName).setValueOfSwitch(value);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void dragToValue(String elementName, double d) {
        try {
            findElement(elementName).dragToValue(d);
        } catch (Exception e) {

        }
    }

}
