/*
 * Copyright (c) 1995-2015 Beyondsoft (Wuhan) Co. Ltd.
 * All rights reserved.
 * This software is the confidential and proprietary information of 
 * Beyondsoft (Wuhan) Co. Ltd. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Beyondsoft (Wuhan) Co. Ltd.
 * History
 * Date Ver Author Change Description
 * ----------- --- ------------- ----------------------------------------
 * 09 Mar 2015 001 Justin Create UiClass.
 * 12 Mar 2015 002 Justin Add method: getDriver,getURL,getCurrentUrl,getTitle,setValue,click,log。
 * 13 Mar 2015 003 Justin Add method: getElementLocator,getByObject,getLocatorType,getLocatorStr and updated setValue,click.
 
 */

package com.by.automate.base.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.by.automate.tools.bean.TDP;
import com.by.automate.utils.CommonTools;
import com.by.automate.utils.ExcelData;
import com.by.automate.utils.ReadXmlUtils;

public abstract class UiFramework extends InitiaFramework {
    /**
     * Selenium Web Driver.
     */
    public static WebDriver driver;
    protected static String main_window;

    Long timeStamp;
    String strNum = "";
    String strEng = "";
    String strChinese = "";
    String strChar = "";
    String strCombine = "";
    String strEn_ch = "";

    protected static String browserHost = "";
    protected static String browserPort = "";
    protected static String browserName = "";
    protected static String browserSize = "";
    
    protected String browserProfilePath = "";

    /*
     * below two parameters defined whether to capture the screen for failures.
     */
    protected static boolean enableScreenCapture = false;
    protected static String screenCapturePath = "";

    public UiFramework() {
        super("", "");

    }

    public UiFramework(String coreFileName) {
        super(coreFileName, "");
    }

    public UiFramework(String coreFileName, String profile) {
        super(coreFileName, profile);
    }

    protected void getConfigurationParameters() {
        projectLevelUiConfigPath = (configFile + getAppName() + "/" + "ui/").replace("//", "/");
        projectLevelDataConfigPath = (configFile + getAppName() + "/" + "content").replace("//", "/");
        appLocale = getSutFullFileName("application.locale");
        String xmlFiles = getSutFullFileName("conf.ui." + getAppType() + ".xml");
        if (xmlFiles.contains(",")) {
            String[] uiFiles = xmlFiles.split(",");
            doc = readerTestData(projectLevelUiConfigPath + uiFiles[0]);
            for (int i = 1; i < uiFiles.length; i++) {
                uiPath = projectLevelUiConfigPath + uiFiles[i].trim();
            	Document docs = readerTestData(projectLevelUiConfigPath + uiFiles[i].trim());
                
                doc = ReadXmlUtils.merge(doc, docs, projectLevelUiConfigPath
                        + getSutFullFileName("conf.megerUi." + getAppType() + ".xml"));
            }

        } else {
        	uiPath = projectLevelUiConfigPath + xmlFiles;
            doc = readerTestData(projectLevelUiConfigPath + xmlFiles);
        }

        connectPro = CommonTools.getTestDataFormatData(projectLevelDataConfigPath + "\\"
                + getSutFullFileName("conf.ui." + getAppType() + ".content"), getAppType() + "_" + getAppName());
        
        // 获取测试用例保存路径.
        projectLevelTestDocumentPath = projectLevelDataConfigPath +"\\"+ getSutFullFileName("conf.ui." + getAppType()+".TestDocument");
        System.out.println(getSutFullFileName("conf.ui." + getAppType()+".TestDocument"));
        System.out.println("conf.ui." + getAppType()+".TestDocument");
        appUrl = getSutFullFileName("application.url");
        apiUrl = getSutFullFileName("app.api.url");

        pageTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.page"), pageTimeout);
        viewTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.view"), viewTimeout);
        elementTimeout = StringUtils.defaultIfEmpty(getSutFullFileName("test.timeout.element"), elementTimeout);

    }

    // 初始化隨機生成測試數據.
    public void loadNewTimeStamp(int length) {
        timeStamp = System.currentTimeMillis();
        strNum = CommonTools.getNumber(length);
        strEng = CommonTools.getEnglish(length);
        strChinese = CommonTools.getChinese(length, chinesePath);
        strChar = CommonTools.getChar(length);
        strCombine = CommonTools.getCombineChar(length, chinesePath);
        strEn_ch = CommonTools.getENCH(length, chinesePath);
    }

    // The default generated character length for the 600.
    public void loadNewTimeStamp() {
        loadNewTimeStamp(600);
    }

    @Override
    protected void prepareTestEnvironment() {

        super.prepareTestEnvironment();

        /*
         * Get screen capture configurations & Initialize the screenCapturePath.
         */
        enableScreenCapture = getInitCofigProperty("screen.capture.forFailures.enable").equalsIgnoreCase("true");
        screenCapturePath = (testRoot.replace("test-classes/", "") + getInitCofigProperty("screen.capture.forFailures.path"));

        File screenCaptureFolder = new File(screenCapturePath);

        if (!screenCaptureFolder.exists())
            screenCaptureFolder.mkdirs();

    }

    /**
     * 在启动的浏览器中打开测试页面，将UImap切换到对应的视图上，并将浏览器最大化
     * 
     * @return boolean 如果页面打开过程中出错，返回false；否则返回true
     * @see #openApp(String)
     */
    public boolean openApp() {

        return openApp("");
    }

    /**
     * UImap跳转到view上，并将浏览器最大化
     * 
     * @param view
     *            指定要打开的视图，即某一页面下的某一个视图"pageName:viewName"
     * @return boolean 如果页面打开过程中出错，返回false；否则返回true
     */
    public boolean openApp(String view) {

        try {
            boolean openapp = uiMapSetView(view);

            if (browserSize.equalsIgnoreCase("maximize"))
                driver.manage().window().maximize();
            return openapp;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 在当前浏览器窗口打开一个新的web页面
     * 
     * @param url
     *            要打开的页面的URL
     */
    public void openUrl(String url) {

        driver.get(url);
    }

    /**
     * 获取当前页面的title
     * 
     * @return 页面title
     */
    public String getTitle() {

        return driver.getTitle();
    }

    /**
     * 页面回退，即从当前页面返回到浏览器历史记录中的上一页面 public void back() {
     * 
     * log("Clicking 'Back' button"); driver.navigate().back();
     * waitByTimeout(Long.parseLong(pageTimeOut)); return;
     * 
     * }
     * 
     * /** 页面前进，当前有过回退操作后，页面前进到浏览器历史记录中的下一个页面
     * 
     * @return 操作成功，返回true
     */
    public boolean forward() {

        driver.navigate().forward();
        return true;
    }

    /**
     * 将焦点切回到主窗口页面上
     */
    public void returnToMain() {

        driver.switchTo().window(main_window);
    }

    /**
     * 刷新页面
     * 
     * @return 页面刷新成功，则返回true
     */
    public boolean refresh() {

        driver.navigate().refresh();
        driver.manage().timeouts().implicitlyWait(Integer.parseInt(pageTimeout), TimeUnit.SECONDS);
        return true;
    }

    /**
     * uiMap跳转到视图reqestedView
     * 
     * @param requestedView
     *            uiMap中定义的view的合法viewName
     * @return 视图更新成功，返回true
     */
    public void setViewTo(String requestedView) {

        uiMapSetView(requestedView);
    }

    /**
     * 点击页面元素elementName，uiMap中该元素如果有view属性，则更新视图
     * 
     * @param elementName
     *            要点击的元素，uiMap中定义的合法元素名字
     */
    public boolean clickOn(String elementName) {

        return clickOn(elementName, "", "", "");
    }

    /**
     * 点击列表元素，uiMap中该元素如果有view属性，则更新视图
     * 
     * <ul>
     * 此方法适用情景：
     * <ul>
     * - 页面中找到多个listName元素，通过itemMatching值去确定点击第几个元素
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容，为int型，确定列表元素中的第itemMatching个
     * @return boolean
     */
    public boolean clickOn(String listName, Object itemMatching) {

        return clickOn(listName, itemMatching, "", "");
    }

    /**
     * 点击列表中匹配的元素，并验证指定的页面文本内容，若点击并验证成功，则更新视图
     * 
     * <ul>
     * Note：
     * <ul>
     * - 若目的元素在uiMap中定义时，有activator属性，则先点击activator值所定位的元素，然后再点击列表匹配项中指定的元素
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param message
     *            页面文本内容
     * @return boolean
     */
    private boolean clickOn(String listName, Object itemMatching, String elementName, String message) {

        boolean returnValue = false;

        log("Clicking on '" + (elementName.isEmpty() ? listName : elementName) + "'.");

        String view = getTargetView("click", elementName.isEmpty() ? listName : elementName, "");
        returnValue = clickLocator(listName, itemMatching, elementName, message);

        if (returnValue) {
            returnValue = uiMapUpdated(view);
        } else {
            throw new RuntimeException("Element clicking Error.");
        }
        if (!returnValue) {
            throw new RuntimeException("uiMap Update Error.");
        }

        return returnValue;

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
                return getElementAttValue(elementName, "view");
            }

            if (type.equalsIgnoreCase("press")) {
                return getElementAttValue(elementName, "pressView");
            }
        } else {
            throw new AssertionError("The type is null");
        }
        return "";

    }

    /**
     * 点击页面元素，并验证message信息在页面中存在可见
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则点击的是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则点击的是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则点击的是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param message
     *            页面文本内容
     * @return 如果点击elementName后，message信息显示在页面中，则返回true，否则返回false
     */
    private boolean clickLocator(String listName, Object itemMatching, String elementName, String message) {

        boolean returnValue = false;
        if (waitForElement(listName, itemMatching, elementName)) {
            getElement(listName, itemMatching, elementName).click();
            returnValue = true;
            if (!StringUtils.isEmpty(message)) {

                returnValue = verifyIsShown(message);

                if (!returnValue) {
                    returnValue = verifyIsShown(message);
                }
            }

            return returnValue;
        }

        Assert.fail("FAIL uiMapClickOnLocatorAndSetView() - Could not find '"
                + (elementName.isEmpty() ? listName : elementName) + "'." + "[" + pageName + "," + viewName + "," + areaName
                + "]");
        return returnValue;
    }

    /**
     * 验证页面中指定的元素或文本信息存在可见
     * 
     * @param elementNameOrMessage
     *            元素或文本信息
     * @return 页面中存在可见，则返回true；否则返回false
     * 
     * @see #verifyIsShown(String, Object, String, boolean)
     */
    public boolean verifyIsShown(String elementNameOrMessage) {

        return verifyIsShown("", "", elementNameOrMessage, true);
    }

    /**
     * 验证页面列表是指定的某一项存在可见
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容，int型
     * @return 页面中存在可见，则返回true；否则返回false
     * 
     * @see #verifyIsShown(String, Object, String, boolean)
     */
    public boolean verifyIsShown(String listName, Object itemMatching) {

        return verifyIsShown(listName, itemMatching, "", true);
    }
    
    public boolean verifyIsShown(String listName, Object itemMatching,String elementName) {

        return verifyIsShown(listName, itemMatching,elementName, true);
    }

    /**
     * 验证页面中指定元素或文本信息是否存在可见。在验证文本信息时，若当前打开多个窗口，则在所有打开的窗口上去验证是否存在指定文本，
     * 找到后再将焦点切换到当前窗口上，具体请查看{@link #isTextShown(String, boolean)}
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型，表示与列表中的第几项匹配
     * @param elementNameOrMessage
     *            元素名，uiMap中定义的合法元素名字；或页面文本信息
     * @param isShown
     *            boolean值，true表验证可见，false表验证不可见；默认为true
     * @return 验证成功，则返回true；否则返回false
     */
    private boolean verifyIsShown(String listName, Object itemMatching, String elementNameOrMessage, boolean isShown) {

        String errorMessage = "";
        String elementLocator = "";
        boolean returnValue = true;
        String expectedText = "";

        if (!elementNameOrMessage.isEmpty()) {
            log("Looking for '" + elementNameOrMessage + "'.");
            elementLocator = getElementLocator(elementNameOrMessage);
        } else {
            elementLocator = getElementLocator(listName);
            log("Looking for '" + listName + "'.");
        }

        if (!StringUtils.isEmpty(elementLocator)) {

            if (isShown) {
                returnValue = waitForElement(listName, itemMatching, elementNameOrMessage);
            } else {
                returnValue = !waitForElementNotShown(listName, itemMatching, elementNameOrMessage);
            }
            expectedText = elementLocator;

        } else {
            log("The element name '" + (elementNameOrMessage.isEmpty() ? listName : elementNameOrMessage)
                    + "' is not found on [page:view] = '" + pageName + ":" + viewName
                    + "' , Trying to match the text in the current page. ");
            expectedText = getLocalizedText(elementNameOrMessage.isEmpty() ? listName : elementNameOrMessage);
            returnValue = isTextShown(expectedText, isShown);
        }

        elementNameOrMessage = elementNameOrMessage.isEmpty() ? listName : elementNameOrMessage;
        if (isShown) {

            if (elementNameOrMessage.isEmpty())
                errorMessage = "FAIL verifyIsShown() - Could not find '" + expectedText + "' ";
            else
                errorMessage = "FAIL verifyIsShown() - Could not find '" + elementNameOrMessage + "' ";
        } else {
            if (elementNameOrMessage.isEmpty())
                errorMessage = "FAIL verifyIsNotShown() - Found '" + expectedText + "' ";
            else
                errorMessage = "FAIL verifyIsNotShown() - Found '" + elementNameOrMessage + "' ";
            returnValue = !returnValue;
        }

        errorMessage = errorMessage + "in current window.";

        return verifyIsTrue(returnValue, errorMessage);
    }

    /**
     * 获取页面中元素显示的文本值
     * 
     * @param elementName
     *            uiMap中定义的合法元素名字
     * @return 获取到文本值则返回true
     */
    public String getValueOf(String elementName) {

        return getValueOf(elementName, "", "", "");
    }

    /**
     * 获取页面中元素显示的文本值，元素由列表和匹配项定位
     * 
     * @param uiMap中定义的元素
     *            ，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，为int型，确定列表元素中的第itemMatching个
     * @return 获取到文本值则返回true
     */
    public String getValueOf(String listName, int itemMatching) {

        return getValueOf(listName, itemMatching, "", "");
    }
    
    public String getValueOf(String listName, Object itemMatching, String elementName) {

        return getValueOf(listName, itemMatching, elementName, "");
    }

    /**
     * 获取列表项中的某一元素的属性值，若属性attribute为空，则获取页面元素的文本值，这里的页面元素不分类型
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param attribute
     *            元素的属性名
     * @return 元素属性值
     */
    private String getValueOf(String listName, Object itemMatching, String elementName, String attribute) {

        String actualValue = "";

        String elementLocator = "";
        if (waitForElement(listName, itemMatching, elementName)) {

            if (attribute == "") {
                String elementType = getElementType(listName, itemMatching, elementName);
                if (StringUtils.contains(elementType, "input")) {
                    actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute(
                            "value"));
                }

                if (StringUtils.contains(elementType, "text")) {
                    actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getText());
                }

                if (StringUtils.contains(elementType, "checkbox")) {

                    String checkboxClass = getElement(listName, itemMatching, elementName).getAttribute("class");

                    actualValue = StringUtils.contains(checkboxClass, "checked") ? "checked" : "unchecked";
                }

                if (StringUtils.contains(elementType, "select")) {

                    WebElement element = getElement(listName, itemMatching, elementName);
                    for (int i = 0; i < getElementsSize(elementName.isEmpty() ? listName : elementName); i++) {
                        WebElement selectItem = element.findElements(By.tagName("option")).get(i);
                        if (selectItem.isSelected())
                            actualValue = StringUtils.defaultString(selectItem.getText());
                    }

                }

                if (StringUtils.isBlank(actualValue)) {
                    elementLocator = getElement(listName, itemMatching, elementName).getText();
                    actualValue = StringUtils.defaultString(elementLocator);
                }
                if (!actualValue.isEmpty())
                    log("get '" + (elementName.isEmpty() ? listName : elementName) + "' tagName is '" + actualValue + "'.");
                else
                    log("getValueOf() '" + (elementName.isEmpty() ? listName : elementName) + "' is Empty!");
            } else {

                actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute(
                        attribute));
                if (!actualValue.isEmpty())
                    log("get '" + (elementName.isEmpty() ? listName : elementName) + "' tagName is '" + actualValue + "'.");
                else
                    log("getValueOf() '" + (elementName.isEmpty() ? listName : elementName) + "' is Empty!");
            }

            actualValue = actualValue.replaceAll("\n", "");

            return actualValue;
        }

        if (StringUtils.isEmpty(actualValue))
            log("getValueOf() - Unable to locate element '" + (elementName.isEmpty() ? listName : elementName) + "' "
                    + elementLocator + "in current view " + viewName + ".", 2);
        return actualValue;

    }

    /**
     * 验证页面中的某一元素存在并显示
     * 
     * @param elementName
     *            指定element
     * @return true of false
     */
    public boolean isElementShown(String elementName) {

        boolean returnValue = false;

        try {
            returnValue = getElement(elementName).isDisplayed();
        } catch (Exception e) {
        }
        return returnValue;
    }

    /**
     * 清空输入框，并填入新的value值
     * 
     * <ul>
     * Note：
     * <ul>
     * - 这里的元素为可编辑的输入框 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param value
     *            要填入的值
     */
    private void clearAndSendkeys(String listName, Object itemMatching, String elementName, String value) {

        WebElement element = getElement(listName, itemMatching, elementName);
        element.clear();
        element.sendKeys(value);
    }

    /**
     * 等待元素在页面中显示后，再点击元素elementName，如果超时，输出warning信息，并截取页面保存
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     */
    private void click(String listName, Object itemMatching, String elementName) {

        waitForElementShown(listName, itemMatching, elementName);
        getElement(listName, itemMatching, elementName).click();
    }

    private boolean waitForElementShown(String listName, Object itemMatching, String elementName) {

        return waitForElement(listName, itemMatching, elementName);
    }

    /**
     * 获取指定元素的类型：select,checkbox,radio,text,file,input
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型或字符串。整型，表示与列表中的第几项匹配；字符串，表示列表项中的文本进行匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @return 页面元素的标签值：select, checkbox, radio, text, file, input
     */
    private String getElementType(String listName, Object itemMatching, String elementName) {

        WebElement element = getElement(listName, itemMatching, elementName);

        if (!element.isDisplayed()) {
            Assert.fail("Element is not found '" + elementName + "'.");
            throw new NullPointerException();
        }

        String elementType = element.getTagName();
        try {
            elementType += " " + element.getAttribute("type");
        } catch (Exception e) {
        }
        if (StringUtils.contains(elementType, "select")) {
            return elementType = "select";
        } else if (StringUtils.contains(elementType, "checkbox")) {
            return elementType = "checkbox";
        } else if (StringUtils.contains(elementType, "radio")) {
            return elementType = "radio";
        } else if (StringUtils.contains(elementType, "text")) {
            return elementType = "text";
        } else if (StringUtils.contains(elementType, "file")) {
            return elementType = "file";
        } else {
            // Default type, in case we can't tell.
            return elementType = "input";
        }
    }

    /**
     * 等待页面元素可见后，获取该页面元素所指定的属性值，attribute不为空
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型，表示与列表中的第几项匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param attribute
     *            属性名
     * @return 指定的页面元素的属性值，如果该元素的此属性没被设置则返回null
     */
    public String getElementAttribute(String listName, int itemMatching, String elementName, String attribute) {

        waitForElement(listName, itemMatching, elementName);
        String returnValue = getElement(listName, itemMatching, elementName).getAttribute(attribute);
        log("The attribute '" + attribute + "' of the element '" + (elementName.isEmpty() ? listName : elementName)
                + "' is '" + (returnValue.isEmpty() ? "null" : returnValue) + "'.");
        return returnValue;
    }

    /**
     * 获取列表项所指定的属性值
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型，表示与列表中的第几项匹配
     * @param attribute
     *            属性名
     * @return 列表项所指定的属性值，如果该项的此属性没被设置则返回null
     * 
     * @see #getElementAttribute(String, int, String, String)
     */
    public String getElementAttribute(String listName, int itemMatching, String attribute) {

        return getElementAttribute(listName, itemMatching, "", attribute);
    }

    /**
     * 获取页面元素所指定的属性值
     * 
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param attribute
     *            属性名
     * @return 指定的页面元素的属性值，如果该元素的此属性没被设置则返回null
     * 
     * @see #getElementAttribute(String, int, String, String)
     */
    public String getElementAttribute(String elementName, String attribute) {

        return getElementAttribute(elementName, 0, "", attribute);
    }

    /**
     * 验证指定表达式是否为真，如果不为真，则截取页面保存，并抛出异常信息
     * 
     * @param expression
     *            验证表达式
     * @param errorMessage
     *            异常信息
     * @return 表达式结果为真，返回true
     */
    private boolean verifyIsTrue(boolean expression, String errorMessage) {

        return verifyIsTrue(expression, errorMessage, true);
    }

    /**
     * 验证指定表达式是否为真，若failOnError为真，截图抛出异常信息；为假则只输出错误信息
     * 
     * @param expression
     *            验证表达式
     * @param errorMessage
     *            输出信息
     * @param failOnError
     *            是否因为错误面验证失败
     * @return 表达式结果为真，返回true
     */
    private boolean verifyIsTrue(boolean expression, String errorMessage, boolean failOnError) {

        if (!expression)
            if (failOnError) {
                // s String shotName = errorMessage.split("'", 3)[1];
                // takeFullScreenShot(shotName);
                throw new RuntimeException(errorMessage);

            } else
                log(errorMessage, 2);
        return expression;
    }

    /**
     * 验证页面中是否存在期望文本信息。 如果当前打开多个页面，则遍历所有打开的页面去验证期望文本是否存在。
     * 
     * @param elementNameOrMessage
     * @param isShown
     * @return
     */
    private boolean isTextShown(String elementNameOrMessage, boolean isShown) {

        boolean returnValue = false;
        boolean singleWindow = true;

        try {
            singleWindow = (driver.getWindowHandles().size() == 1);
        } catch (Exception e) {
        }

        if (singleWindow) {
            returnValue = verifyBodyTextContainsExpectedText(elementNameOrMessage, isShown);
        } else {
            String currentWindowName = driver.getWindowHandle();
            Set<String> allWindowNames = driver.getWindowHandles();
            Set<String> currentWindowNamesList = getCurrentValidWindowNamesListInAll(allWindowNames);

            for (String windowName : currentWindowNamesList) {
                driver.switchTo().window(windowName);
                waitForCondition("(selenium.browserbot.getCurrentWindow().document.readyState=='interactive') || "
                        + "(selenium.browserbot.getCurrentWindow().document.readyState=='complete');", pageTimeout);
                returnValue = verifyBodyTextContainsExpectedText(elementNameOrMessage, isShown);

                if (returnValue)
                    break;
            }
            driver.switchTo().window(currentWindowName);
        }

        return returnValue;
    }

    /**
     * 验证页面中是否存在期望文本信息
     * 
     * @param expectedText
     *            验证文本信息
     * @param isShown
     *            是否显示，true表示验证页面中存在并显示，false表示验证页面中不存在
     * @return true or false
     */
    protected boolean verifyBodyTextContainsExpectedText(String expectedText, boolean isShown) {

        boolean returnValue = false;
        String bodyText = "";
        Long currentTimeMillis = System.currentTimeMillis();
        while ((System.currentTimeMillis() - currentTimeMillis) < Long.parseLong(elementTimeout)) {
            bodyText = driver.findElement(By.tagName("body")).getText();
            returnValue = bodyText.contains(expectedText) || bodyText.matches(expectedText);
            if (isShown == returnValue) {
                break;
            }
            waitByTimeout(500);
        }

        return returnValue;
    }

    /**
     * 在当前frame或窗口中执行一段javascript。
     * 
     * @param js
     *            要执行的javascript
     * @param timeout
     */
    private void waitForCondition(String js, String timeout) {

        ((JavascriptExecutor) driver).executeScript("try {" + js + "} catch(err){false}", timeout);
    }

    /**
     * 当前线程等待waitFor秒后继续执行
     * 
     * @param waitFor
     * @see Thread#sleep(long)
     */
    public void waitByTimeout(long waitFor) {

        try {
            Thread.sleep(waitFor);
        } catch (Exception e) {
        }
    }

    /**
     * 获取本地content文件中存储指定key对应的文本信息
     * 
     * <br>
     * 注：
     * <ul>
     * 该方法会自动去除所取内容句首和句尾的空格
     * 
     * @param textString
     *            content文件中的key
     * @return content文件中指定key对应的文本值
     */
    private String getLocalizedText(String textString) {

        if (StringUtils.isBlank(textString) || StringUtils.isNumeric(textString))
            return textString;
        String returnValue = getContentPropertry(textString);

        if (StringUtils.isBlank(returnValue)) {

            returnValue = textString;
        }

        returnValue = StringUtils.replace(returnValue, "%s", ".*?");
        return returnValue.trim();
    }

    /**
     * 对不同类型的元素进行相应的可行性操作；对可编辑元素(input, text, password, email)输入指定的值value；
     * 对checkbox类型，勾选或取消勾选；对select类型，选中指定选项value；对file类型，输入指定值value
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则指定元素是一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则指定元素是一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则指定元素是一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            匹配项，可为整型，表示与列表中的第几项匹配
     * @param elementName
     *            元素名，uiMap中定义的合法元素名字
     * @param elementType
     *            元素类型： input, text, password, email or checkbox or select or
     *            file
     * @param value
     *            要设置的值；对checkbox类型，其值"y", "yes", "true", "on",
     *            "checked"表示勾选；"n", "no", "false", "off", "unchecked"表示取消勾选
     * @return 元素值设置成功返回true
     */
    private boolean operateOnEveryElementType(String listName, Object itemMatching, String elementName, String elementType,
            String value) {

        boolean returnValue = false;
        if (StringUtils.equals(elementType, "input") || StringUtils.equals(elementType, "text")
                || StringUtils.equals(elementType, "password") || StringUtils.equals(elementType, "email")) {

            value = StringUtils.defaultString(getLocalizedText(value), value);
            clearAndSendkeys(listName, itemMatching, elementName, value);
            returnValue = true;
        }

        if (StringUtils.equals(elementType, "checkbox")) {

            ArrayList<String> positiveValues = new ArrayList<String>();
            ArrayList<String> negativeValues = new ArrayList<String>();

            positiveValues.addAll(Arrays.asList("y", "yes", "true", "on", "checked"));
            negativeValues.addAll(Arrays.asList("n", "no", "false", "off", "unchecked"));

            if (positiveValues.contains(value.toLowerCase())) {
                returnValue = true;
                if (!StringUtils.containsIgnoreCase(
                        StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute("class")),
                        "checked"))
                    click(listName, itemMatching, elementName);
            } else {
                if (negativeValues.contains(value.toLowerCase())) {
                    returnValue = true;
                    if (!StringUtils
                            .containsIgnoreCase(
                                    StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute(
                                            "class")), "checked"))
                        click(listName, itemMatching, elementName);
                }
            }
        }

        if (StringUtils.equals(elementType, "select")) {

            value = StringUtils.defaultString(getLocalizedText(value), value);
            Select select = new Select(getElement(listName, itemMatching, elementName));
            select.selectByVisibleText(value);
            returnValue = true;
        }

        if (StringUtils.equals(elementType, "file")) {

            value = StringUtils.defaultString(getLocalizedText(value), value);
            WebElement element = getElement(listName, itemMatching, elementName);
            element.sendKeys(value);
            returnValue = true;
        }

        return returnValue;
    }

    /**
     * 等待元素在页面中显示出来，然后对其进行设值，如果元素始终未显示或设值不成功，则抛出异常
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容
     * @param elementName
     *            uiMap中定义的元素
     * @param value
     *            设置的值
     * @return 对元素设置成功返回true
     * @throws 设值不成功
     *             ，招聘异常
     * 
     * @see #operateOnEveryElementType(String, Object, String, String, String)
     */
    private boolean setValueTo(String listName, Object itemMatching, String elementName, String value) {

        boolean returnValue = false;
        if (waitForElement(listName, itemMatching, elementName)) {
            String elementType = getElementType(listName, itemMatching, elementName);
            returnValue = operateOnEveryElementType(listName, itemMatching, elementName, elementType, value);
        }

        if (!returnValue) {
            log("setValueTo() - Could not set '" + (elementName.isEmpty() ? listName : elementName) + "' " + value
                    + "' in current view '" + viewName + "'.", 2);
            throw new RuntimeException("setValueTo() - Could not set '" + (elementName.isEmpty() ? listName : elementName)
                    + "' to '" + value + "' in current view " + viewName + ".");
        } else
            log("Value '" + value + "' is set for '" + (elementName.isEmpty() ? listName : elementName) + "'.");
        return returnValue;
    }

    /**
     * 等待指定元素显示后，对其进行设值
     * 
     * @param elementName
     *            元素名
     * @param value
     *            设置的值
     * @return 对元素设置成功返回true
     * 
     * @see #setValueTo(String, Object, String, String)
     */
    public boolean setValueTo(String elementName, String value) {

        return setValueTo(elementName, "", "", value);
    }

    /**
     * 等待页面列表显示后，对列表中匹配的项进行设值
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容，int型，表示listName中的第itemMatching+1个
     * @param value
     *            设置的值
     * @return 对元素设置成功返回true
     * 
     * @see #setValueTo(String, Object, String, String)
     */
    public boolean setValueTo(String listName, int itemMatching, String value) {

        return setValueTo(listName, itemMatching, "", value);
    }

    /**
     * 验证页面中元素不可见
     * 
     * @param elementName
     *            指定的页面元素
     * @return 页面中不可见，则返回true；否则返回false
     * 
     * @see #verifyIsShown(String, Object, String, boolean)
     */
    public boolean verifyIsNotShown(String elementName) {

        return verifyIsShown(elementName, "", "", false);
    }

    /**
     * 验证页面列表中指定的某一项不可见
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容，int型
     * @return 页面中不可见，则返回true；否则返回false
     * 
     * @see #verifyIsShown(String, Object, String, boolean)
     */
    public boolean verifyIsNotShown(String listName, int itemMatching) {

        return verifyIsShown(listName, itemMatching, "", false);
    }

    /**
     * 返回浏览器窗口句柄的集合，用于迭代通过WebDriver打开的所有浏览器。
     * 
     * @param allWindowNames
     * @return
     */
    private Set<String> getCurrentValidWindowNamesListInAll(Set<String> allWindowNames) {

        Set<String> currentWindowNamesList = new HashSet<String>();

        for (String window : allWindowNames) {

            if (!(window.equalsIgnoreCase("null") || window.equalsIgnoreCase("undefined") || window.isEmpty()))
                currentWindowNamesList.add(window);
        }
        return currentWindowNamesList;
    }

    private boolean waitForElementNotShown(String listName, Object itemMatching, String elementName) {

        return waitForElementNotShown(listName, itemMatching, elementName, (long) 0);
    }

    private boolean waitForElementNotShown(final String listName, final Object itemMatching, final String elementName,
            Long timeInSec) {

        long timeOut = timeInSec;
        if (timeInSec == 0) {
            timeOut = Long.parseLong(elementTimeout) / 1000;
        }
        try {
            WebDriverWait driverWait = (WebDriverWait) new WebDriverWait(driver, timeOut, 500).ignoring(
                    StaleElementReferenceException.class).withMessage("Refersh the element failure to time out " + timeOut);
            return driverWait.until(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver driver) {

                    try {
                        if (getElement(listName, itemMatching, elementName, 1).isDisplayed()) {
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e) {
                    } catch (NoSuchElementException e) {
                    }

                    log("Element '" + (elementName.isEmpty() ? listName : elementName) + "' is not present on this page.");
                    return true;
                }
            });
        } catch (Exception e) {
            log("Element '" + (elementName.isEmpty() ? listName : elementName) + "' still present in " + timeOut + " secs.");
            // takeFullScreenShot(elementName.isEmpty() ? listName :
            // elementName);
            return false;
        }

    }

    protected boolean waitForElement(String elementName) {

        return waitForElement(elementName, "", "");
    }

    /**
     * 等待元素在页面中显示可见。30s内，等待直到元素在页面中显示，等待时间达到30s仍无对应元素显示，则抛出异常
     */
    protected boolean waitForElement(String listName, Object itemMatching, String elementName) {

        return waitForElement(listName, itemMatching, elementName, Long.parseLong(elementTimeout) / 1000);

    }

    private boolean waitForElement(final String listName, final Object itemMatching, final String elementName, Long timeInSec) {

        long timeOut = timeInSec;
        try {

            WebDriverWait driverWait = (WebDriverWait) new WebDriverWait(driver, timeOut).ignoring(
                    StaleElementReferenceException.class).withMessage("Refresh the element failure to time out " + timeOut);
            return driverWait.until(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver driver) {

                    boolean returnValue = false;
                    try {

                        returnValue = getElement(listName, itemMatching, elementName).isDisplayed();
                    } catch (IndexOutOfBoundsException e) {

                    }catch(NullPointerException e){
                        throw new RuntimeException("");
                    }
                  /*  if (!returnValue) {
                        isDisplayed(closeButtonForAndroid);
                    }*/
                    return returnValue;
                }
            });
        } catch (TimeoutException e) {
            log("The Element is not found elementName '" + (elementName.isEmpty() ? listName : elementName) + "' in "
                    + timeOut + " seconds.", 3);
            // takeFullScreenShot(elementName.isEmpty() ? listName :
            // elementName);
            return false;
        }
    }

    /**
     * 更新UiMap对象到当前view。一个参数则表明总是添加一个新的view并删除向前的层级关系
     * 
     * @param view
     * @return 如果view更新成功返回true
     */
    public boolean uiMapUpdateView(String view) {

        return uiMapSetView(view);
    }

    /**
     * 更新视图，如果view中包含多个视图，则将视图切换到在页面可见的视图上
     * 
     * @param view
     *            目的视图，uiMap中定义的view的合法viewName值
     * @return 如果视图切换成果，则返回true，否则返回false，并对当前页面进行截图并保存
     */
    protected boolean uiMapUpdated(String view) {

        boolean returnValue = false;
        if (view.equals("null") || view.isEmpty() || view == null) {
            returnValue = true;
        } else {
            log("Setting view to '" + view + "'.");
            returnValue = uiMapUpdateView(view);

        }
        if (!returnValue) {
            // takeFullScreenShot(view);
            throw new RuntimeException("uiMap Update Error.");
        }

        return returnValue;
    }

    /**
     * If this element is a text entry element, this will clear the value. Has
     * no effect on other elements. Text entry elements are INPUT and TEXTAREA
     * elements.
     * 
     * @param elemetnName
     */
    public void clear(String elemetnName) {
        log("Clear value for elment name " + elemetnName + ".");
        getElement(elemetnName).clear();
    }

    public void sendKeys(String elementName, String value) {
        getElement(elementName).sendKeys(value);
    }

    public void switchIframe(String elementName) {

        driver.switchTo().frame(getElement(elementName));
    }

    /**
     * 
     * @return
     */
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    /**
     * 
     * @return
     */
    public String getCurrentWindow() {
        return driver.getWindowHandle();
    }

    protected void executeJavaScript(String js, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(js, element);
    }

    public String getElementTextByJs(String elementName) {
        WebElement element = getElement(elementName);
        return (String) ((JavascriptExecutor) driver).executeScript(
                "var value = arguments[0].value;return value.toString();", element);
    }

    public void updatedElementAttribute(String elementName) {
        WebElement element = getElement(elementName);
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('type' ,'display');", element);
    }

    /**
	 * 
	 */
    public void close() {
    	
        driver.quit();
        if(driver!=null){
        	driver = null;
        }
        log("<===================TEST TERMINATED================>");

    }

    public boolean isSelect(String elementName) {
        return getElement(elementName).isSelected();
    }

    public boolean isEnabled(String elementName) {
        return getElement(elementName).isEnabled();
    }

    /**
     * 获取定位页面元素的locator的类型，默认为css.
     * 
     * @param locator
     *            页面元素locator
     * @return String
     */
    protected String getLocatorType(String locator) {

        if (locator.isEmpty())
            return "";

        if (locator.matches("<\\S*>=.*") && locator.startsWith("<"))
            return locator.split("=")[0];

        return "css";
    }

    protected String getLocatorStr(String locator) {

        if (locator.isEmpty())
            return "";

        String locatorType = getLocatorType(locator);

        if (locator.startsWith(locatorType))
            return locator.substring(locatorType.length() + 1);
        else
            return locator;
    }

    protected By getByObjectByType(String locatorStr, String locatorType) {

        if (locatorType.equals("<id>")) {
            return By.id(locatorStr);
        }

        if (locatorType.equals("<class>")) {
            return By.className(locatorStr);
        }

        if (locatorType.equals("<name>")) {
            return By.name(locatorStr);
        }

        if (locatorType.equals("<xpath>")) {
            return By.xpath(locatorStr);
        }

        if (locatorType.equals("<tagName>")) {
            return By.tagName(locatorStr);
        }

        if (locatorType.equals("<linkText>")) {
            return By.linkText(locatorStr);
        }

        if (locatorType.equals("<partialLinkText>")) {
            return By.partialLinkText(locatorStr);
        }

        return By.cssSelector(locatorStr.replace("css=", "").replace("<css>=", ""));
    }

    /**
     * 获取页面元素，webElement对象，代表一个HTML元素
     * 
     * @param elementName
     *            uiMap中定义的元素
     * @return WebElement对象，代表一个HTML元素
     */
    protected WebElement getElement(String elementName) {

        return getElement(elementName, "", "");
    }

    /**
     * 获取页面中一组具有相同样式的元素列表中与itemMatching匹配的一项，这里是取itemName中找到的第itemMathing+1个
     * 
     * @param itemName
     *            uiMap中定义的元素，在HTML中定义时可能找到多个匹配的元素
     * @param itemMatching
     *            匹配的索引号
     * @return WebElement对象，代表一个HTML元素
     */
    protected WebElement getElement(String itemName, int itemMatching) {

        return getElement(itemName, itemMatching, "");

    }

    /**
     * 获取一组有相同样式的元素列表中与itemMatching匹配的一项中的某一个元素elementName
     * 
     * @param itemName
     *            uiMap中定义的元素，在HTML中定义时可能找到多个匹配的元素
     * @param itemMatching
     *            匹配对象，int型或String类型
     * @param elementName
     *            uiMap中定义的元素
     * @return WebElement对象，代表一个HTML元素
     */
    protected WebElement getElement(String itemName, Object itemMatching, String elementName) {

        return getElement(itemName, itemMatching, elementName, 0);
    }

    /**
     * 获取页面元素，返回WebElement对象
     * 
     * <ul>
     * Note:
     * <ul>
     * - 若listName和itemMatching都为空，elementName不为空，则返回一个普通元素 <br>
     * - 若listName和itemMatching都不为空，elementName为空，则返回一个列表中的某一项 <br>
     * - 若listName和itemMatching，elementName都不为空，则返回一个列表中某一项的指定元素 <br>
     * <br>
     * 
     * @param itemName
     *            uiMap中定义的元素，在HTML中定义时可能找到多个匹配的元素
     * @param itemMatching
     *            对比内容
     * @param elementName
     *            uiMap中定义的元素
     * @param timeout
     * @return WebElement对象，代表一个HTML元素
     */
    private WebElement getElement(String itemName, Object itemMatching, String elementName, int timeout) {

        String elementLocator = getElementLocator(elementName);
        String listLocator = getElementLocator(itemName);
        WebElement returnValue = null;
        String errorMsg = "";

        List<WebElement> elements;
        boolean emptyItemMatching = true;
        if (itemMatching instanceof Integer)
            if ((Integer) itemMatching > 0)
                emptyItemMatching = false;
        if (itemMatching instanceof String)
            if (!((String) itemMatching).isEmpty())
                emptyItemMatching = false;
        if (emptyItemMatching) {
            if (listLocator.isEmpty()) {
                elements = getElements(elementLocator, "", "");
                if (elements.size() > 0)
                    returnValue = elements.get(0);
                else
                    errorMsg = itemName;
            } else {
                elements = getElements(listLocator, "", "");
                if (elements.size() > 0)
                    returnValue = elements.get(0);
                else
                    errorMsg = itemName;

            }

        } else {
            if (elementLocator.isEmpty()) {
                List<WebElement> listElements = waitForElementList(listLocator, timeout);

                int matchingIndex = getMatchingIndex(listElements, itemMatching);
                if (matchingIndex >= 0)
                    returnValue = listElements.get(matchingIndex);

            } else {
                elements = getElements(listLocator, itemMatching, elementLocator, timeout);
                if (elements.size() > 0)
                    returnValue = elements.get(0);
                else
                    errorMsg = elementName + " in " + itemName;
            }
        }

        if (returnValue == null) {
            throw new IndexOutOfBoundsException("Element '" + errorMsg + "' Not Found!");
        } else
            return returnValue;
    }

    /**
     * 获取具有相同样式的所有元素
     * 
     * @param elementName
     *            uiMap中定义的元素
     * @return 一组WebElement的集合
     */
    protected List<WebElement> getElements(String elementName) {

        String elementLocator = getElementLocator(elementName);
        return getElements(elementLocator, "", "");

    }

    private List<WebElement> getElements(String itemLocator, Object itemMatching, String elementLocator) {

        return getElements(itemLocator, itemMatching, elementLocator, 0);
    }

    private List<WebElement> getElements(String itemLocator, Object itemMatching, String elementLocator, int timeout) {
        //
        if (itemLocator.isEmpty() && elementLocator.isEmpty()) {
            throw new NullPointerException();
        }

        String locator = "";
        WebElement parentElement = null;
        boolean emptyItemMatching = true;
        if (itemMatching instanceof Integer)
            if ((Integer) itemMatching > 0)
                emptyItemMatching = false;
        if (itemMatching instanceof String)
            if (!((String) itemMatching).isEmpty())
                emptyItemMatching = false;
        if (emptyItemMatching) {
            locator = itemLocator;
        } else {
            if (elementLocator.isEmpty())
                throw new NullPointerException("Please use getElement(item,itemMatching) instead!");

            List<WebElement> listElements = waitForElementList(itemLocator, timeout);

            int index = getMatchingIndex(listElements, itemMatching);

            if (index >= 0)
                parentElement = listElements.get(index);
            locator = elementLocator;
        }

        String elementLocatorType = getLocatorType(locator);
        String elementLocatorStr = getLocatorStr(locator);

        if (parentElement == null)
            return driver.findElements(getByObjectByType(elementLocatorStr, elementLocatorType));
        else {
            return parentElement.findElements(getByObjectByType(elementLocatorStr, elementLocatorType));
        }
    }

    /**
     * 从一组具有相同样式的元素中挑选出与给出信息匹配的元素，返回其索引
     * 
     * @param listElements
     *            uiMap中定义的元素，在HTML中可能找到多个匹配的元素
     * @param itemMatching
     *            对比内容
     * @return 匹配的元素的索引
     */
    protected int getMatchingIndex(List<WebElement> listElements, Object itemMatching) {

        int index = -1;

        if (itemMatching instanceof Integer)
            index = (Integer) itemMatching - 1;
        if (itemMatching instanceof String) {
            String matchingString = (String) itemMatching;
            if (StringUtils.isNumeric(matchingString)) {
                index = Integer.valueOf(matchingString) - 1;
            } else {
                itemMatching = getLocalizedText(matchingString);
                String getAttText = "";

                for (int i = 0; i < listElements.size(); i++) {
                    getAttText = listElements.get(i).getText();
                    if (StringUtils.containsIgnoreCase(getAttText, matchingString)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    protected List<WebElement> waitForElementList(final String listLocator, final int timeInSec) {

        WebDriverWait driverWait = (WebDriverWait) new WebDriverWait(driver, timeInSec, 500).ignoring(
                StaleElementReferenceException.class).withMessage("Refersh the element failure to time out " + timeInSec);
        try {

            driverWait.until(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver driver) {

                    return (getElements(listLocator, "", "").size() > 0);
                }
            });
        } catch (TimeoutException e) {
            // takeFullScreenShot(listLocator);
           // log("Element List is not loaded in " + timeInSec + " seconds.");
        }
        return getElements(listLocator, "", "");

    }

    public int getElemetsCount(String elements) {
        return getElements(elements).size();
    }

    /**
     * 输入键盘上的key键
     * 
     * @param key
     *            指定键盘上的一个按键
     */
    public void pressKey(Keys key) {

        Actions action = new Actions(driver);
        action.sendKeys(key).perform();
    }

    /**
     * 获取当前窗口的url；在获取单出窗口及新标签页面后，会将新窗口或新标签页面关闭。
     * 
     * @return
     */
    public String getCurrentUrl() {

        String actualPageUrl = "";
        Set<String> allWindowNames = driver.getWindowHandles();

        Set<String> currentWindowNamesList = new HashSet<String>();

        if (allWindowNames.size() > 1) {

            for (String window : allWindowNames) {

                if (!(window.equalsIgnoreCase("null") || window.equalsIgnoreCase("undefined") || window.isEmpty()))
                    currentWindowNamesList.add(window);
            }

            for (String windowName : currentWindowNamesList) {

                boolean mainApplicationWindow = StringUtils.equalsIgnoreCase(windowName, main_window);

                if (!mainApplicationWindow) {

                    driver.switchTo().window(windowName);
                    waitForCondition("(selenium.browserbot.getCurrentWindow().document.readyState=='interactive') || "
                            + "(selenium.browserbot.getCurrentWindow().document.readyState=='complete');", pageTimeout);

                    actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());

                    driver.close();
                    returnToMain();
                }
            }
        } else {
            actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());
        }

        return actualPageUrl;
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
     * 
     * @param elementName
     * @param value
     */
    public void setValue(String elementName, String propertieName) {
        setValue("", "", elementName, propertieName);
    }

    public void setValue(String listName, String match, String propertieName) {
        setValue(listName, match, "", propertieName);
    }

    public void setValue(String listName, String match, String elementName, String propertieName) {
        try {
            String value = getSutFullFileName(propertieName);
            String elementLocator = getElementLocator(elementName);
            if (value == null) {
                value = propertieName;
            }
            WebElement element = getElement(listName, match, elementName);
            String elementType = getElementTagName(element);
            /*
             * Text and password inputs - set to value using type(). Also checks
             * resource files to get appropriate localized string, if available.
             */
            if (StringUtils.equals(elementType, "input") || StringUtils.equals(elementType, "text")
                    || StringUtils.equals(elementType, "password") || StringUtils.equals(elementType, "email")) {

                element.clear();
                element.sendKeys(value);
            }

            /*
             * Selects - Uses the default "label=" optionLocator to set the
             * value. Also checks resource files to get appropriate localized
             * string, if available.
             */
            if (StringUtils.equals(elementType, "select")) {
                Select select = new Select(element);
                select.selectByVisibleText(value);
            }

            /*
             * Checkboxes - click() to toggle state, depending upon value
             * ("true" or "false") and whether or not the checkbox is already
             * set to the desired value. If we toggled the value or if the
             * current value matched what we wanted, set returnValue = true.
             */

            if (StringUtils.equals(elementType, "checkbox")) {

                ArrayList<String> positiveValues = new ArrayList<String>();
                ArrayList<String> negativeValues = new ArrayList<String>();

                positiveValues.addAll(Arrays.asList("y", "yes", "true", "on", "checked"));
                negativeValues.addAll(Arrays.asList("n", "no", "false", "off", "unchecked"));

                if (positiveValues.contains(value.toLowerCase())) {
                    if (!element.isSelected())
                        element.click();
                }
                if (negativeValues.contains(value.toLowerCase())) {
                    if (element.isSelected())
                        element.click();
                }
            }
            log("Value \"" + value + "\" is set for element " + elementName + "(" + elementLocator + ")");
        } catch (NoSuchElementException e) {
            log("Element is not find, pelase check element " + elementName + " locator on the page-view(" + pageName + ":"
                    + areaName + ")", 2);
            throw new NoSuchElementException(elementName);
        }

    }

    private String getElementTagName(WebElement element) {
        String elementType = "";
        try {
            elementType = element.getTagName();
            elementType += " " + element.getAttribute("type");
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (StringUtils.contains(elementType, "select")) {
            return elementType = "select";
        } else if (StringUtils.contains(elementType, "checkbox")) {
            return elementType = "checkbox";
        } else if (StringUtils.contains(elementType, "input")) {
            return elementType = "input";
        } else {
            // default type
            return elementType = "text";
        }

    }

    public String getElementValueForAttribute(String elementName, String attribute) {
        return getElementValueForAttribute("", "", elementName, attribute);
    }

    public String getElementValueForAttribute(String listName, String match, String attribute) {
        return getElementValueForAttribute(listName, match, "", attribute);
    }

    public String getElementValueForAttribute(String listName, String match, String elementName, String attribute) {
        return getValueOf(listName, match, elementName, attribute);
    }

    /**
     * Gets the value of the specified element (text box, select list, checkbox,
     * etc.).
     * <p>
     * <p>
     * Gets the value of inputs (text boxes), the label of selects, the "on" or
     * "off" value for checkboxes, and the text of other elements.
     * 
     * @param elementName
     *            The name of the element you want to get the value of.
     * 
     * @return Returns the value of the specified element or returns an empty
     *         string ("") if the element was not found.
     */
    public String getValueOf(String listName, String match, String elementName, String attribute) {
        String returnValue = "";
        WebElement element = getElement(listName, match, elementName);
        String elementType = getElementTagName(element);

        if (StringUtils.isEmpty(attribute)) {

            if (StringUtils.contains(elementType, "text")) {
                returnValue = element.getText();
            }
            if (StringUtils.contains(elementType, "input")) {
                returnValue = StringUtils.defaultString(element.getAttribute("value"));
            }

            if (StringUtils.contains(elementType, "select")) {
                List<WebElement> options = getElement(listName, match, elementName).findElements(By.xpath(".//option"));
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).isSelected()) {
                        returnValue = options.get(i).getText();
                        break;
                    }
                }
            }

            if (StringUtils.contains(elementType, "checkbox")) {

                String checkboxClass = element.getAttribute("class");

                returnValue = StringUtils.contains(checkboxClass, "checked") ? "checked" : "unchecked";
            }

            if (StringUtils.isBlank(returnValue)) {
                returnValue = element.getText();
                returnValue = StringUtils.defaultString(returnValue);
            }
        } else {
            returnValue = element.getAttribute(attribute).toString();
        }

        returnValue = returnValue.replaceAll("\n", "");

        return returnValue;

    }

    /**
     * Wait for the fixed time(Millis)
     * 
     * @param Millis
     */
    public void waitByTimeOut(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭当前的窗口或标签页，并半焦点切回到主窗口上
     */
    public void closeCurrentWindow() {

        if (!driver.getWindowHandle().equals(main_window)) {
            driver.close();
            returnToMain();
        }
    }

    /**
     * 关闭除主窗口或主页面之外的看有窗口或标签页面
     */
    public void closeOtherWindows() {

        for (String windows : driver.getWindowHandles())
            if (!windows.equals(main_window)) {
                driver.switchTo().window(windows);
                driver.close();
            }
        returnToMain();
    }

    /**
     * 将焦点切到当前活动的警告对话框上，如果切换成功，返回true，否则返回false
     * 
     * @return true or false
     */
    public boolean isAlertPresent() {

        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        }
    }

    /**
     * 确认警告提示，返回警告上信息
     * 
     * @return 警告会话框上的信息内容
     */
    public String acceptAlert() {

        String alertText = "";
        try {
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText();
            alert.accept();
        } catch (NoAlertPresentException e) {

        }
        if (alertText.isEmpty())
            log("There is no alert in current page");
        else
            log("The alert is '" + alertText + "'.");

        return alertText;

    }

    /**
     * 取消警告提示，返回警告会话框上提示的内容
     * 
     * @return 警告会话框上的信息内容
     */
    public String dismissAlert() {

        String alertText = "";
        try {
            Alert alert = driver.switchTo().alert();
            alertText = alert.getText();
            alert.dismiss();
        } catch (NoAlertPresentException e) {

        }
        if (alertText.isEmpty())
            log("There is no alert in current page");
        else
            log("The alert is '" + alertText + "'.");

        return alertText;
    }

    /**
     * 获取元素文本值
     * 
     * @param elementName
     *            uiMap中定义的元素
     * @return 元素文本值
     */
    public String getElementText(String elementName) {

        waitForElement(elementName);
        String elementText = getElement(elementName).getText();
        log("The text of the element '" + elementName + "' is '" + elementText + "'.");
        return elementText;

    }

    /**
     * 获取有相同样式的所有元素的文本值
     * 
     * @param elementName
     *            uiMap中定义的元素名
     * @return 元素文本值
     */
    protected String getElementAllText(String elementName) {

        String allText = "";
        List<WebElement> allElement = getElements(elementName);
        for (WebElement element : allElement) {
            allText += element.getText();
        }
        return allText;
    }

    /**
     * @param elementName
     * @return
     */
    public int getElementsSize(String elementName) {

        List<WebElement> elements = getElements(elementName);
        return elements.size();
    }

    /**
     * 将焦点切换到新弹出的窗口或子标签页上
     */
    public void switchPopupNewWindow() {

        String currentHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        Iterator<String> it = handles.iterator();
        while (it.hasNext()) {
            if (currentHandle == it.next())
                continue;
            driver.switchTo().window(it.next());
        }
    }

    /**
     * 将焦点切换到新弹出的提示窗口上
     */
    public void switchToPromptWindow(Set<String> before, Set<String> after) {

        List<String> whs = new ArrayList<String>(after);
        whs.removeAll(before);
        whs.remove("");
        if (whs.size() > 0) {
            driver.switchTo().window(whs.get(0));
        } else {
            throw new WebDriverException("No new window prompted out.");
        }
    }

    public boolean isElementNotEnabled(String listName, Object itemMatching) {

        return !isElementEnabled(listName, itemMatching);
    }

    public boolean isElementNotEnabled(String elementName) {

        return !isElementEnabled(elementName);
    }

    private void waitForElementEnabled(String listName, Object itemMatching, String elementName) {

        boolean returnValue = false;
        for (int i = 0; i < Long.parseLong(elementTimeout) / 1000; i++) {
            returnValue = isElementEnabled(listName, itemMatching, elementName);
            waitByTimeout(1000);
            if (returnValue)
                break;
        }
        if (!returnValue) {
            throw new RuntimeException("Wait for <" + (elementName.isEmpty() ? listName : elementName) + "> enable timeout "
                    + Long.parseLong(elementTimeout) / 1000);
        }

    }

    public void waitForElementEnabled(String listName, Object itemMatching) {

        waitForElementEnabled(listName, itemMatching, "");
    }

    /**
     * 等待指定元素变为enable状态，若超出{@value #elementTimeout}仍不为enable，则抛出异常
     * 
     * @param elementName
     *            指定元素
     */
    public void waitForElementEnabled(String elementName) {

        waitForElementEnabled(elementName, "", "");
    }

    /**
     * 判断列表匹配项中的元素是否可操作，除了不可编辑的输入框，对其他元素都返回true
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容
     * @param elementName
     *            uiMap中定义的元素
     * @return 如果列表匹配项中的元素的状态是enabled，返回true；否则返回false
     */
    private boolean isElementEnabled(String listName, Object itemMatching, String elementName) {

        return getElement(listName, itemMatching, elementName).isEnabled();
    }

    /**
     * 判断列表中匹配的项是否可操作，除了不可编辑的输入框，对其他元素都返回true
     * 
     * @param listName
     *            uiMap中定义的元素，在HTML中可能找到多个
     * @param itemMatching
     *            对比内容
     * @return 如果列表中匹配的项的状态是enabled，返回true；否则返回false
     */
    public boolean isElementEnabled(String listName, Object itemMatching) {

        return getElement(listName, itemMatching, "").isEnabled();
    }

    /**
     * 判断元素是否可操作，除了不可编辑的输入框，对其他元素都返回true
     * 
     * @param elementName
     *            uiMap中定义的元素
     * @return 如果元素状态是enabled，返回true；否则返回false
     */
    public boolean isElementEnabled(String elementName) {

        return getElement(elementName, "", "").isEnabled();
    }

    protected void closeAdvertisement() {

        if (isElementShown("closeButtonForAndroid")) {
            log("click close button.");
            getElement("closeButtonForAndroid").click();
            waitByTimeOut(100);
        }
    }

    /**
     * Convenience method for Waiting for element disappeared.
     * 
     * Parameters:
     * 
     * @param elementName
     *            -The name of element in json file.
     * @return - if the element disappear return true , otherwise return false.
     * 
     */
    public boolean waitForElementToDisappear(final String listName, final Object itemMatching, final String elementName,
            int i) {

        log("loading for " + elementName + "...");
        long timeOut = i;
        if (i == 0) {
            timeOut = Long.parseLong(elementTimeout) / 1000;
        }
        try {
            WebDriverWait driverWait = (WebDriverWait) new WebDriverWait(driver, timeOut, 500).ignoring(
                    StaleElementReferenceException.class).withMessage("Refersh the element failure to time out " + timeOut);
            return driverWait.until(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver driver) {

                    try {
                        if (getElement(listName, itemMatching, elementName).isDisplayed()) {
                            return false;
                        }
                    } catch (IndexOutOfBoundsException e) {
                    } catch (NoSuchElementException e) {
                    } catch (NullPointerException e) {
                        throw new RuntimeException("Element Locator Error <The element '"+(elementName.equals("")? listName : elementName)+"' is empty on the pageView["+pageName+" : "+viewName+"]>!");
                    }
                    log("Element " + elementName + " is not present on this page.");
                    return true;
                }
            });
        } catch (Exception e) {
            // e.printStackTrace();
            // log();
            throw new TimeoutException("Element " + elementName + " still present in " + timeOut + " secs.");
            // return false;
        }

    }

    public boolean waitForElementToDisappear(String elementName) {

        return waitForElementToDisappear("", "", elementName, 0);
    }

    public boolean waitForElementToDisappear(String listName, Object itemMatching) {

        return waitForElementToDisappear(listName, itemMatching, "", 0);
    }

    public boolean waitForElementToDisappear(String listName, Object itemMatching, String elementName) {

        return waitForElementToDisappear(listName, itemMatching, elementName, 0);
    }

    public void waitForElementToDisappear(String elementName, int timeOutInsec) {

        if (!waitForElementToDisappear("", "", elementName, timeOutInsec)) {
            throw new TimeoutException("Loading for element disappear , time out "
                    + (timeOutInsec == 0 ? Integer.parseInt(elementTimeout) : timeOutInsec));
        }
    }

    public void waitForElementToDisappear(String listName, Object itemMatching, int timeOutInsec) {

        if (!waitForElementToDisappear(listName, itemMatching, "", timeOutInsec)) {
            throw new TimeoutException("Loading for element disappear , time out " + timeOutInsec);
        }
    }

    public boolean waitForElementReadyOnTimeOut(String elementName, int timeOut) {
        return waitForElementReadyOnTimeOut("", "", elementName, timeOut);
    }

    public boolean waitForElementReadyOnTimeOut(String listName, Object match, int timeOut) {
        return waitForElementReadyOnTimeOut(listName, match, "", timeOut);
    }
  
    public boolean waitForElementReadyOnTimeOut(final String listName, final Object itemMatch, final String elementName,
            int timeOut) {
        if (timeOut == 0) {
            timeOut = Integer.parseInt(elementTimeout) / 1000;
        }
        try {
            WebDriverWait driverWait = (WebDriverWait) new WebDriverWait(driver, timeOut).ignoring(
                    StaleElementReferenceException.class).withMessage(
                    "The Element is not found elementName: '" + elementName + "' in " + timeOut + " seconds.");
            return driverWait.until(new ExpectedCondition<Boolean>() {

                public Boolean apply(WebDriver input) {
                    boolean returnValue = false;
                    try {
                        returnValue = getElement(listName, itemMatch, elementName).isDisplayed();
                    }/* catch (Exception e) {
                    }*/catch (IndexOutOfBoundsException e) {
                    }catch(NullPointerException e){
                       throw new RuntimeException("Element Locator Error <The element '"+(elementName.equals("")? listName : elementName)+"' is empty on the pageView["+pageName+" : "+viewName+"]>!");
                       
                    }
                    if (!returnValue) {
                        closeAdvertisement();
                    }
                    return returnValue;
                }

            });

        } catch (TimeoutException e) {
            // log("The Element is not found elementName: '" + elementName +
            // "' in " + timeOut + " seconds.");
          

        }
        return false;
    }

    /**
     * Asserts that two Strings are equal.
     */
    public void assertEquest(Object expected, Object actual) {
        if (expected instanceof String && actual instanceof String) {
            expected = ((String) expected).trim();
            actual = ((String) actual).trim();
        }
        log("Asserts that two Strings are equal." + " [(" + expected + ") equals (" + actual + ")]");
        Assert.assertEquals(expected, actual);
    }

    public void assertNotEquest(String expected, String actual) {
        Assert.assertTrue(!StringUtils.equalsIgnoreCase(expected, actual),"The expected(" + expected + ") and actual(" + actual + ") value is equality.");
    }

    public void assertTrue(boolean condition, String message) {
        /*
         * if(!condition){ takeFullScreenShot("ScreentShot"); }
         */
        Assert.assertTrue(condition,message);
    }

    public void assertContains(String expected, String actual) {
        log("Checks if String contains a search String ." + " [(" + expected + ") contains (" + actual + ")]");
        boolean b = StringUtils.contains(expected, actual);
        Assert.assertTrue(b);
    }

    public void assertContainsIgnoreCase(String expected, String actual) {
        log("Checks if String contains a search String ." + " [(" + expected + ") contains (" + actual + ")]");
        boolean b = StringUtils.containsIgnoreCase(expected, actual);
        Assert.assertTrue(b);
    }

    /*
     * public void assertNotEquest(String expected , String actual){
     * Assert.assertn }
     */

    /********************* read excel *****************************/

    private String replaceStr(String value, String type, String str) {
        String returnValue = "";
        int length;
        if (StringUtils.contains(value, type)) {
            if (value.split("\\{\\{" + type + "\\}\\}")[1].substring(0, 2).equals("[[")) {
                // if (StringUtils.contains(value, "[[") &&
                // StringUtils.contains(value, "]]")) {
                if (StringUtils.isNumeric(StringUtils.substringBetween(value, "[[", "]]"))) {
                    String[] strings = value.split("\\{\\{" + type + "\\}\\}");
                    length = Integer.parseInt(StringUtils.substringBetween(strings[1], "[[", "]]"));
                    returnValue = StringUtils.substring(str, 0, length);
                    return strings[0] + StringUtils.replaceOnce(strings[1], ("[[" + length + "]]"), returnValue);
                } else {
                    log("The [[" + StringUtils.substringBetween(value, "[[", "]]") + "]] value only numbers.");
                }
            }
            returnValue = StringUtils.substring(str, 0, 10);
            return value.replace("{{" + type + "}}", returnValue);

        }
        return value.trim();
    }

    // The Value include
    // "{{number}} , {{english}} , {{chinese}} , {{en_ch}} , {{char}} , {{en_ch_char}}"
    // ,Replace it.
    public String getValue(TDP tdp, String key) {
        Map<String, String> data = tdp.getTestData();
        String returnValue = data.get(key);
        // String returnValue = valule;
        if (StringUtils.contains(returnValue, "{{number}}")) {
            returnValue = replaceStr(returnValue, "number", strNum);

        }
        if (StringUtils.contains(returnValue, "{{english}}")) {
            returnValue = replaceStr(returnValue, "english", strEng);
        }
        if (StringUtils.contains(returnValue, "{{chinese}}")) {
            returnValue = replaceStr(returnValue, "chinese", strChinese);
        }

        if (StringUtils.contains(returnValue, "{{en_ch}}")) {
            returnValue = replaceStr(returnValue, "en_ch", strEn_ch);
        }
        if (StringUtils.contains(returnValue, "{{char}}")) {
            String randomChar = replaceStr(returnValue, "char", strChar);
            int length = randomChar.length();
            int index = randomNum(length);
            if (length == index) {
                index = index - 1;
            }
            if (index % 2 == 0)
                returnValue = randomChar.replace(randomChar.substring(index, index + 1), "'");
            else
                returnValue = randomChar.replace(randomChar.substring(index, index + 1), "\"");
        }
        if (StringUtils.contains(returnValue, "{{en_ch_char}}")) {
            returnValue = replaceStr(returnValue, "en_ch_char", strCombine);
        }

        if (returnValue.contains("{{blank}}") || returnValue.contains("{{enter}}")) {
            int length;
            String type = StringUtils.contains(returnValue, "{{blank}}") ? "blank" : "enter";
            String[] strings = returnValue.split("\\{\\{" + type + "\\}\\}");
            length = Integer.parseInt(StringUtils.substringBetween(strings[1], "[[", "]]"));
            return type + "_" + length;
        }
        return returnValue;
    }

    public List<Map<String, String>> getTestData() {
        return getTestData("");
    }

    public List<Map<String, String>> getTestData(String filePath) {
        String excelName = StringUtils.defaultString(filePath, getSutFullFileName("conf.test.excel"));
        // String excelName = getCorePropetry(filePath);
        filePath = dataFile + "testData/" + excelName;

        String[] strs = classNamePath.split("\\.");
        String className = strs[strs.length - 1];
        String sheetName = sutPro.getProperty(className);
        return ExcelData.getRowsMap(sheetName, filePath);
    }

    public String randomSelectElement(String allName) {
        String returnValue = "";
        List<WebElement> list = getElements(allName);
        int index = randomNum(list.size());
        WebElement element = list.get(index);

        String elementType = element.getTagName();
        if (StringUtils.contains(elementType, "text")) {
            returnValue = element.getText();
        }
        if (StringUtils.contains(elementType, "input")) {
            returnValue = StringUtils.defaultString(element.getAttribute("value"));
        }

        if (StringUtils.contains(elementType, "select")) {
            if (element.isSelected()) {
                returnValue = element.getText();
            }
        }
        if (StringUtils.isBlank(returnValue)) {
            returnValue = element.getText();
            returnValue = StringUtils.defaultString(returnValue);
        }
        element.click();
        return returnValue;
    }

    protected int randomNum(int scope) {
        Random rd = new Random();
        return rd.nextInt(scope);
    }

    public void getScreen(String fileName) {

        takeFullScreenShot(fileName);

    }

    public void getScreen() {

        getScreen("");
    }

    /**
     * Capture the entire page screen.
     * 
     * @param failure
     *            - String messages to describe the failure and it will be a
     *            part of the png file name.
     * 
     * @return - return true if the capture completes, otherwise return false.
     * 
     */
    public boolean takeFullScreenShot(String failure) {

        boolean returnValue = false;
        String timeStamp = CommonTools.getDate().replace("-", "") + "_"
                + CommonTools.getCurrentTime().replace(":", "").replace(".", "");
        failure = timeStamp + "_" + CommonTools.replaceIllegalFileName(failure, "_");
        if (StringUtils.endsWith(failure, "_"))
            failure = timeStamp;

        if (enableScreenCapture) {
            String fileName = screenCapturePath + "/" + failure + ".png";

            getScreenShot(fileName);

            returnValue = true;
        }

        return returnValue;
    }

    /**
     * get screen shot.
     */
    private void getScreenShot(String fileName) {

        File screenshot = getScreenShotFile();
        if (screenshot == null)
            return;
        try {
            copyScreenShot(screenshot, new File(fileName));
        } catch (IOException e) {
            log("Exception happen when getting screen shot, detail is : '" + e.getMessage() + "'. "
                    + "The screen shot operatioin was ignored. ", 3);
        }
    }

    /**
     * get screen shot file.
     */
    public File getScreenShotFile() {

        File screenshot = null;
        try {
            if (!(driver instanceof TakesScreenshot)) {
                WebDriver augmentDriver = new Augmenter().augment(driver);
                screenshot = ((TakesScreenshot) augmentDriver).getScreenshotAs(OutputType.FILE);
            } else
                screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        } catch (WebDriverException e) {
            log("Failed to take screenshot, please let us know following trace! Test may continue without screenshot.", 2);
            enableScreenCapture = false;
            e.printStackTrace();
        }
        return screenshot;
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
    
    
}
