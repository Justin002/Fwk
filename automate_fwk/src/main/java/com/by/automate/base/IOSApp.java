package com.by.automate.base;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.by.automate.base.core.UiFramework;

import io.appium.java_client.ios.IOSDriver;

public abstract class IOSApp extends UiFramework {

    protected IOSDriver iosDriver;

    public IOSApp() {
        super();
    }

    public IOSApp(String coreFile) {
        super(coreFile, "");
    }

    public IOSApp(String coreFile, String ipaPath) {
        super(coreFile, ipaPath);
    }

    protected String getAppType() {
        return appType = "IOSApp";
    }

    protected void platformSupportInitiate(String appPath) {
        startAppiumDriver(appPath);
        prepareTestEnvironment();
    }

    protected void startAppiumDriver(String app_path) {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformVersion", getSutFullFileName("app.device.version"));
            capabilities.setCapability("platform", getSutFullFileName("app.os.platform"));
            capabilities.setCapability("deviceName", getSutFullFileName("app.device.name"));
            capabilities.setCapability("platformName", getSutFullFileName("app.device.platformName"));
            capabilities.setCapability("browserName", getSutFullFileName("app.browser.name"));
            capabilities.setCapability("app", getSutFullFileName("app.name"));

            iosDriver = new IOSDriver(new URL("http://" + getSutFullFileName("app.appium.serverIP") + "/wd/hub"),
                    capabilities);
            driver = iosDriver;

        } catch (Exception e) {
            log("Cannot launch application from the mobile device", 2);
            throw new RuntimeException(e);
        }

        new WebDriverWait(iosDriver, 10);
    }

    /*
     * public boolean openApp() { updatedUiMap();
     * 
     * return true; }
     */

    /**
     * Convenience method for swiping across the screen
     * 
     * @param startx
     *            starting x coordinate
     * @param starty
     *            starting y coordinate
     * @param endx
     *            ending x coordinate
     * @param endy
     *            ending y coordinate
     * @param duration
     *            amount of time in milliseconds for the entire swipe action to
     *            take
     */
    public void swipe(int startX, int startY, int endX, int endY, int duration) {
        iosDriver.swipe(startX, startY, endX, endY, duration);

    }

    /**
     * Convenience method for swiping screen and updating uiMap.
     * 
     * @param direction
     *            - left Sliding screen to the left - leftSide From the left of
     *            screen to began to slip - right Sliding screen to the right -
     *            rightSide From the right of screen to began to slip - up
     *            Screen upward sliding - top From the top of screen to began to
     *            slip - down Slide down the screen - bottom From the bottom of
     *            screen to began to slip
     */
    private void swipeOfType(String type) {
        waitByTimeOut(500);
        log("Swiping " + type + ".");
        int windowlenX = iosDriver.manage().window().getSize().getWidth();
        int windowlenY = iosDriver.manage().window().getSize().getHeight();
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
            swipe((int) (windowlenX * 0.9), (int) (windowlenY * 0.5), (int) (windowlenX * 0.2), (int) (windowlenY * 0.5),
                    3000);
        }

        // From the left of screen to began to slip
        if (type.equalsIgnoreCase(swipeLeftSide)) {
            log("SWIPE : From the LeftSide of screen to right .");
            swipe(1, (int) (windowlenY * 0.5), (int) (windowlenX * 0.9), (int) (windowlenY * 0.5), 3000);
        }

        // Sliding screen to the right
        if (type.equalsIgnoreCase(swipeRight)) {
            log("SWIPE : Sliding screen to the right");
            swipe((int) (windowlenX * 0.2), (int) (windowlenY * 0.5), (int) (windowlenX * 0.9), (int) (windowlenY * 0.5),
                    3000);
        }

        // From the right of screen to began to slip
        if (type.equalsIgnoreCase(swipeRightSide)) {
            log("SWIPE : From the RightSide of screen to left .");
            swipe((int) (windowlenX * 0.9), (int) (windowlenY * 0.5), (int) (windowlenX * 0.2), (int) (windowlenY * 0.5),
                    3000);
        }

        // Screen upward sliding
        if (type.equalsIgnoreCase(swipeUp)) {
            log("SWIPE : Screen upward sliding  .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5), (int) (windowlenY * 0.4),
                    3000);
        }

        // From the top of screen to began to slip
        if (type.equalsIgnoreCase(swipeTop)) {
            log("SWIPE : From the top of screen to top .");
            swipe((int) (windowlenX * 0.5), 0, (int) (windowlenX * 0.5), (int) (windowlenY * 0.8), 3000);
        }

        // Slide down the screen
        if (type.equalsIgnoreCase(swipeDown)) {
            log("SWIPE : Slide down the screen .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.4), (int) (windowlenX * 0.5), (int) (windowlenY * 0.9),
                    3000);
        }

        // From the bottom of screen to began to slip
        if (type.equalsIgnoreCase(swipeBottom)) {
            log("SWIPE :From the bottom of screen to top .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5), (int) (windowlenY * 0.1),
                    3000);
        }

    }

    public void swipe(String direction) {
        String swipeToView = getTargetView("swipe", "", direction);
        swipeOfType(direction);
        uiMapSetView(swipeToView);
    }

    private void tap(String listName, String iteMatching, String elementName, int pressTime, String type) {
        String pageView = getElementAttribute((elementName.isEmpty() ? listName : elementName), type);
        String x = "";
        String y = "";
        if (!elementName.isEmpty()) {
            x = getElementAttribute(elementName, "x");
            y = getElementAttribute(elementName, "y");
        }

        if (!StringUtils.isEmpty(x) && !StringUtils.isEmpty(y)) {
            float getPercentX = Float.parseFloat(x);
            float getPercentY = Float.parseFloat(y);
            int width = (int) (getCoorinateX() * getPercentX);
            int height = (int) (getCoorinateY() * getPercentY);
            iosDriver.tap(1, width, height, pressTime);
        } else {
            tap(1, listName, iteMatching, elementName, pressTime);
            uiMapSetView(pageView);
        }

    }

    /**
     * Convenience method for tapping the center of an element on the screen
     * 
     * @param fingers
     *            - number of fingers/appendages to tap with
     * @param element
     *            - elementName tao tap
     * @param duration
     *            - how long between pressing down, and lifting
     *            fingers/appendages
     */
    private void tap(int fingers, String listName, String iteMatching, String elementName, int duration) {
        if (waitForElementReadyOnTimeOut(listName, iteMatching, elementName, 5)) {
            WebElement element = getElement(listName, iteMatching, elementName);
            iosDriver.tap(fingers, element, duration);
            log("Tap on '" + (elementName.isEmpty() ? listName : elementName) + "'.");
        } else {
            log("Tap on element failed.");
            throw new RuntimeException("tapOn element failed.");
        }
    }

    private void tap(String listName, String iteMatching, int pressTime, String type) {
        tap(listName, iteMatching, "", pressTime, type);
    }

    private void tap(String elementName, int pressTime, String type) {
        tap("", "", elementName, pressTime, type);
    }

    /**
     * Convenience method for taping an element 500ms.
     * 
     * @param elementName
     *            - Valid selector name in uiMap
     */
    public void tapOn(String elementName) {
        tap(elementName, 500, "page-view");
    }

    public void tapOn(String listName, String itemMatching) {
        tap(listName, itemMatching, 500, "page-view");
    }

    public void tapOn(String listName, String itemMatching, String elementName) {
        tap(listName, itemMatching, elementName, 500, "page-view");
    }

    public int getCoorinateX() {
        return iosDriver.manage().window().getSize().getWidth();
    }

    public int getCoorinateY() {
        return iosDriver.manage().window().getSize().getHeight();
    }

    public void back() {
    	iosDriver.getKeyboard().sendKeys(Keys.BACK_SPACE);
        //iosDriver.getKeyboard().sendKeys(4);
    }

    public void goHome() {
    	iosDriver.getKeyboard().sendKeys(Keys.HOME);
    }

    /**
     * This method was used for the cursor location The KeyCode "20" was android
     * key code
     */
    public void down() {

      //  iosDriver.sendKeyEvent(20);
    	iosDriver.getKeyboard().sendKeys(Keys.DOWN);
    }

    public void sendKeyEvent() {
    //    iosDriver.sendKeyEvent(66);
    }

    /**
     * The android.widget.EditText in WebView component sometimes can't be send
     * keys by driver.sendKeys, it randomly lose characters. So we make this
     * special version method to handle this defect.
     * */
    public void webViewEditSendKeys(String webView, String text) {

        String[] lettersStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@.+0123456789".split("");
        String[] letters = Arrays.copyOfRange(lettersStr, 1, lettersStr.length);
        int[] androidCodes = { 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
                52, 53, 54, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
                53, 54, 77, 56, 81, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        HashMap<String, Integer> letterToCodeHashMap = new HashMap<String, Integer>();
        for (int i = 0; i < letters.length; i++) {
            letterToCodeHashMap.put(letters[i], androidCodes[i]);
        }

        clickOn(webView);
        for (int j = 0; j < text.length(); j++) {
            String charStr = Character.valueOf(text.charAt(j)).toString();
           // iosDriver.sendKeyEvent(letterToCodeHashMap.get(charStr));
          //  iosDriver.getKeyboard().sendKeys(Keys.);
            // iosDriver.findElementByIosUIAutomation(using)
            // iosDriver.executeScript(script, args)
            // wait a while, send Key Event sometimes lose command
            int sendKeyEventInteval = Integer.parseInt(getSutFullFileName("_ios.sendKeyEventIntevalMilliseconds"));
            waitByTimeOut(sendKeyEventInteval);

        }
    }
}
