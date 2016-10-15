package com.by.automate.base;

import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;

import com.by.automate.base.core.UiFramework;

public abstract class WebApp extends UiFramework {

    public WebApp() {
        super();
    }

    public WebApp(String coreFileName) {
        super(coreFileName);
    }

    public WebApp(String coreFileName, String profile) {
        super(coreFileName, profile);

    }

    protected void platformSupportInitiate(String profile) {
    	
        setProfilePath(profile);
        startBrowser();
        openUrl(appUrl);
        prepareTestEnvironment();
    }

    @Override
    protected String getAppType() {
        return appType = "WebApp";
    }

    @Override
    protected abstract String getAppName();

    private void startBrowser() {
        browserName = getSutFullFileName("browser.name");
        File file = new File(browserProfilePath);
        if (browserName.matches(".*firefox.*")) {
            if (file.exists()) {
                FirefoxProfile profile = new FirefoxProfile(file);
                profile.setEnableNativeEvents(true);
                driver = new FirefoxDriver(profile);
            } else {
                driver = new FirefoxDriver();
                driver.manage().window().maximize();

            }
        }
        if (browserName.matches(".*chrome.*")) {
            log("Loading open Chrome...");
            System.setProperty("webdriver.chrome.driver", dataFile + "drivers\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            // options.addArguments("user-data-dir=, --disable-prerender-local-predictor",
            // "--incognito");
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);
        }

        main_window = driver.getWindowHandle();
        driver.manage().deleteAllCookies();
        
    }

    private void setProfilePath(String profileName) {

        browserName = getSutFullFileName("browser.name");
        browserSize = getSutFullFileName("browser.size").toLowerCase();
        browserProfilePath = getProfilePath(profileName);
    }

    private String getProfilePath(String profileName) {

        if (browserName.matches(".*firefox.*")) {
            return getBrowserProfileProperty("profile.firefox", profileName);
        } else if (browserName.matches(".*iexplore.*")) {
            return getBrowserProfileProperty("profile.iexplore");
        } else if (browserName.matches(".*chrome.*")) {
            return getBrowserProfileProperty("profile.chrome");
        }
        throw new RuntimeException("Can't find any profile property for browser[" + browserName + "] and profile ["
                + profileName + "]");
    }

    private String getBrowserProfileProperty(String profileType) {
        return getBrowserProfileProperty(profileType, "");
    }

    private String getBrowserProfileProperty(String profileType, String folderName) {
        String returnStr = "";

        if (folderName.isEmpty())
            returnStr = dataFile + "common" + getInitCofigProperty(profileType) + "/default";
        else
            returnStr = dataFile + "common" + getInitCofigProperty(profileType) + "/" + folderName;
        if (new File(returnStr).exists()) {
            log("Browser Profile used: " + returnStr);
        }

        return returnStr;
    }

    /*
     * public boolean openApp() { try {
     * 
     * log("<===============COMMENCING THE TEST================>");
     * updatedUiMap(); // getElementAttribute("signInButton", "page-view"); //
     * open a URL. getURL(getCorePropetry("application .url")); // Current
     * Windows Maximize. return true; } catch (Exception e) { e.getStackTrace();
     * log
     * ("launch Browser failed , Please check your configuration file information!"
     * , 2);
     * 
     * }
     * 
     * return false; }
     */
    /**
     * Convenience method for move mouse On specified element. NOTE: Situation
     * of this method using:Element will be displayed when the mouse moves up
     * Parameters:
     * 
     * @param elementName
     *            - the name of valid Selenium locator string in uiMap .
     */
    public void moveMouseOn(String elementName) {

        WebElement element = getElement(elementName);
        log("Moving mouse to element: " + elementName + ".");

        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }

    public void mouseDownAndUp(String elementName) {
        Locatable hoverItem = (Locatable) getElement(elementName);
        Mouse mouse = ((HasInputDevices) driver).getMouse();
        mouse.mouseDown(hoverItem.getCoordinates());
        mouse.mouseUp(hoverItem.getCoordinates());
    }

    /**
     * Convenience method for clicking on element use JS
     * 
     * @param elementName
     */
    public void clickElementByJS(String elementName) {

        // String locator = uiMapElementLocator(elementName);
        WebElement element = getElement(elementName);
        executeJavaScript("arguments[0].click();", element);
    }

    /**
     * 
     * @param nameOrHandle
     */
    public void switchToWindow(String nameOrHandle) {
        driver.switchTo().window(nameOrHandle);
    }

    public void switchToMainWindow() {
        Set<String> getWindows = driver.getWindowHandles();

        for (String win : getWindows) {
            if (!win.equals(main_window)) {
                switchToWindow(win);
                driver.close();
                isAlertPresent();
            }
        }
        switchToWindow(main_window);
        // driver.close();
        // switchToWindow(main_window);
        // updatedUiMap("dashboardPage:createStory");
    }

    /**
	  * 
	  */
    public void switchToNewWindow() {
        for (String window : getWindowHandles()) {
            if (!window.equalsIgnoreCase(getCurrentWindow())) {
                switchToWindow(window);
            }
        }
    }

    /**
	 * 
	 */
    public void switchToPromptWindow(Set<String> before, Set<String> after) {
        List<String> whs = new ArrayList<String>(after);
        whs.removeAll(before);
        if (whs.size() > 0) {
            switchToWindow(whs.get(0));
        } else {
            throw new WebDriverException("No new window prompted out.");
        }
    }

    public void switchDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public void setValueToCKEditor(String elementName, String value) {
        String elementLocator = getElementLocator(elementName).split("==")[1];
        executeJavaScript("CKEDITOR.instances['" + elementLocator + "'].setData('" + value + "');", getElement(elementName));
    }

    public String getValueToCKEditor(String elementName) {
        String elementLocator = getElementLocator(elementName).split("==")[1];
        return (String) ((JavascriptExecutor) driver).executeScript("var value = CKEDITOR.instances['" + elementLocator
                + "'].getData(); return value.toString();", getElement(elementName));
    }

    public void KeyPress(int key) {
        Robot robot;
        try {
            robot = new Robot();
            robot.keyPress(key);
            robot.keyRelease(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAlertPresent() {
        waitByTimeOut(500);
        try {
            driver.switchTo().alert().accept();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getAlertMsg() {
        return driver.switchTo().alert().getText().trim();
    }

    public void rollToElement(String elementName) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", getElement(elementName));
    }

    public void rollToButtom() {
        String js = "var q=document.documentElement.scrollTop=10000";
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript(js);
    }

    public void switchToIframe(String elementName) {
        driver.switchTo().frame(getElement(elementName));
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }
}
