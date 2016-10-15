package com.by.automate.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.by.automate.tools.AndroidMonitor;
import com.by.automate.tools.appInfo.apkUtil.ApkUtil;
import com.by.automate.utils.CommonTools;
import com.by.automate.base.core.UiFramework;

import io.appium.java_client.NetworkConnectionSetting;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
@SuppressWarnings("rawtypes")
public abstract class AndroidApp extends UiFramework {

	protected AndroidDriver androidDriver;

    protected String getAppName() {

        return "";
    }

    protected String getAppType() {
       return appType = "AndroidApp";
    }

    protected AndroidApp() {

        super();
    }

    protected AndroidApp(String SUT) {

        super(SUT);
    }

    public boolean openApp() {

        startAppiumDriver("");
        boolean validate = uiMapUpdateView("");

        return validate;

    }

    @Override
    protected void platformSupportInitiate(String profileName) {

        prepareTestEnvironment();
    }

    public void getPageSource() {

        log(androidDriver.getPageSource());
    }

    protected void startAppiumDriver(String app_apk) {

        try {
            File app = new File(app_apk);
            if (!app.exists()) {
                File classpathRoot = new File(System.getProperty("user.dir"));
                app = new File(classpathRoot, getSutFullFileName("app.path"));
                if(!app.exists()){
                	app = new File(getSutFullFileName("app.path"));
                }
                log("Launch the application '" + app + "'.");
            } else
                log("Launch the application " + app_apk);

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.BROWSER_NAME, getSutFullFileName("app.browser.Name"));
            capabilities.setCapability("platformVersion", getSutFullFileName("app.device.version"));
            capabilities.setCapability("platform", getSutFullFileName("app.os.platform"));
            capabilities.setCapability("deviceName", getSutFullFileName("app.device.name"));
            capabilities.setCapability("platformName", getSutFullFileName("app.device.platformName"));
            capabilities.setCapability("newCommandTimeout", getSutFullFileName("app.command.timeout"));
            //capabilities.setCapability("app", app.getAbsolutePath());
            capabilities.setCapability("appPackage", getSutFullFileName("app.package"));
            capabilities.setCapability("appActivity", getSutFullFileName("app.activity"));
            capabilities.setCapability("appWaitActivity", getSutFullFileName("app.wait.activity"));

            androidDriver = new AndroidDriver(new URL("http://" + getSutFullFileName("app.appium.serverIP") + "/wd/hub"),
                    capabilities);
            driver = androidDriver;
        } catch (Exception e) {
            log("Cannot launch application!", 2);
            throw new RuntimeException(e);
        }

        new WebDriverWait(androidDriver, 10);
    }

    public String getAndroidVersion() {

        try {
            Process process = Runtime.getRuntime().exec("adb shell getprop ro.build.version.release");
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String version = br.readLine();
            log("device version " + version);
            int points = 0;
            for (int i = 0; i < version.length(); i++) {
                if (version.substring(i, i + 1).equals(".")) {
                    points++;
                    if (points == 2) {
                        return version.substring(0, i);
                    }
                }
            }
            return version;

        } catch (Exception e) {
            log("adb shell getprop ro.build.version.release", 2);
            throw new NullPointerException("get device version is null.");
        }
    }
    
    public String getDeviceName(){
    	return	runADB("adb shell getprop ro.product.brand") +" " + runADB("adb shell getprop ro.product.model");
    	
    }
    
    public String runADB(String cmd) {

        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String value = br.readLine();
           // log("device version " + value);
           
            return value;

        } catch (Exception e) {
          //  log(cmd, 2);
            throw new NullPointerException(cmd);
        }
    }
    
  
    
    public String getSysetmVersion(){
    	return runADB("adb shell getprop ro.build.version.release");
    }

    public void uninstallApp() {

        String apkPackage = getSutFullFileName("app_package");
        try {
            Runtime.getRuntime().exec("adb uninstall " + apkPackage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void swipeToText(String text) {

        androidDriver.scrollTo(text);
        if (verifyBodyTextContainsExpectedText(text, true))
            log("'" + text + "' is found.");
        else {
            throw new RuntimeException("'" + text + "' is not found.");
        }
    }

    public void swipeUpToElement(String elementName) {

        int num = 0;
        while (!isElementShown(elementName)) {
            swipe("up");
            waitByTimeout(1000);
            num++;
            if (num > 10)
                break;
        }
        verifyIsShown(elementName);
    }

    private void swipeTo(int startX, int startY, int endX, int endY, int duration) {

        androidDriver.swipe(startX, startY, endX, endY, duration);
    }

    // 1- 10 等份
    public void swipe(int startX, int startY, int endX, int endY) {

        log("Swipe from [" + startX + ":" + startY + "] to [" + endX + ":" + endY + "].");
        int windowlenX = androidDriver.manage().window().getSize().getWidth();
        int windowlenY = androidDriver.manage().window().getSize().getHeight();
        swipeTo((int) (windowlenX * startX / 10), (int) (windowlenY * startY / 10), (int) (windowlenX * endX / 10),
                (int) (windowlenY * endY / 10), 1500);
    }

    // 按坐標
    private void swipe(double startX, double startY, double endX, double endY, int duration) {

        log("Swipe from [" + startX + ":" + startY + "] to [" + endX + ":" + endY + "].");
        swipeTo((int) startX, (int) startY, (int) endX, (int) endY, duration);
    }

    public void swipeByXY(double startX, double startY, double endX, double endY, int duration) {
        swipeTo((int) startX, (int) startY, (int) endX, (int) endY, duration);
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
        int windowlenX = androidDriver.manage().window().getSize().getWidth();
        int windowlenY = androidDriver.manage().window().getSize().getHeight();
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
                    2000);
        }

        // From the left of screen to began to slip
        if (type.equalsIgnoreCase(swipeLeftSide)) {
            log("SWIPE : From the LeftSide of screen to right .");
            swipe(1, (int) (windowlenY * 0.5), (int) (windowlenX * 0.9), (int) (windowlenY * 0.5), 2000);
        }

        // Sliding screen to the right
        if (type.equalsIgnoreCase(swipeRight)) {
            log("SWIPE : Sliding screen to the right");
            swipe((int) (windowlenX * 0.2), (int) (windowlenY * 0.5), (int) (windowlenX * 0.9), (int) (windowlenY * 0.5),
            		2000);
        }

        // From the right of screen to began to slip
        if (type.equalsIgnoreCase(swipeRightSide)) {
            log("SWIPE : From the RightSide of screen to left .");
            swipe((int) (windowlenX * 0.9), (int) (windowlenY * 0.5), (int) (windowlenX * 0.2), (int) (windowlenY * 0.5),
            		2000);
        }

        // Screen upward sliding
        if (type.equalsIgnoreCase(swipeUp)) {
            log("SWIPE : Screen upward sliding  .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5), (int) (windowlenY * 0.4),
            		2000);
        }

        // From the top of screen to began to slip
        if (type.equalsIgnoreCase(swipeTop)) {
            log("SWIPE : From the top of screen to top .");
            swipe((int) (windowlenX * 0.5), 0, (int) (windowlenX * 0.5), (int) (windowlenY * 0.8), 2000);
        }

        // Slide down the screen
        if (type.equalsIgnoreCase(swipeDown)) {
            log("SWIPE : Slide down the screen .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.4), (int) (windowlenX * 0.5), (int) (windowlenY * 0.9),
            		2000);
        }

        // From the bottom of screen to began to slip
        if (type.equalsIgnoreCase(swipeBottom)) {
            log("SWIPE :From the bottom of screen to top .");
            swipe((int) (windowlenX * 0.5), (int) (windowlenY * 0.9), (int) (windowlenX * 0.5), (int) (windowlenY * 0.1),
            		2000);
        }

    }

    private void tap(int fingers, String elementName, int duration) {

        tap(fingers, elementName, "", "", duration);

    }

    private void tap(int fingers, String itemList, Object itemMatching, int duration) {

        tap(fingers, itemList, itemMatching, "", duration);
    }

    private void tap(int fingers, String itemList, Object itemMatching, String elementName, int duration) {

        if (waitForElement(itemList, itemMatching, elementName)) {
            WebElement element = getElement(itemList, itemMatching, elementName);
            androidDriver.tap(fingers, element, duration);
            log("Tap on '" + (elementName.isEmpty() ? itemList : elementName) + "'.");
        } else {

            log("tap on element failed.");
            throw new RuntimeException("tapOn element failed.");

        }

    }

    public void doubleTap() {

        TouchAction action = new TouchAction(androidDriver);
        action.press(500, 500).release().waitAction(100).press(500, 500).release().perform();
        log("Double tap the screen.");
    }

    public void zoom(int x, int y) {

        androidDriver.zoom(x, y);
    }

    public void zoom(String elementName) {

        zoom(elementName, "", "");
    }

    public void zoom(String itemList, int itemMatching) {

        zoom(itemList, itemMatching, "");
    }

    private void zoom(String itemList, Object itemMatching, String elementName) {

        WebElement element = getElement(itemList, itemMatching, elementName);

        androidDriver.zoom(element);
    }

    public void pinch(String elementName) {

        pinch(elementName, "");

    }

    public void pinch(String itemList, String itemMatching) {

        pinch(itemList, itemMatching, "");

    }

    public void pinch(String itemList, int itemMatching) {

        pinch(itemList, itemMatching, "");

    }

    private void pinch(String itemList, Object itemMatching, String elementName) {

        WebElement element = getElement(itemList, itemMatching, elementName);

        androidDriver.pinch(element);

    }

    public void pinch(int x, int y) {

        androidDriver.pinch(x, y);
    }

    public void sendKey(int key) {

        androidDriver.longPressKeyCode(key);
    }

    public void closeKeyWord() {

        androidDriver.hideKeyboard();

    }

    public void back() {

        androidDriver.longPressKeyCode(AndroidKeyCode.BACK);

    }

    public void home() {

        androidDriver.longPressKeyCode(AndroidKeyCode.HOME);
    }

    public void rotate() {

        androidDriver.rotate(ScreenOrientation.LANDSCAPE);
    }

    public void setNetworkToAirplane() {

        androidDriver.setNetworkConnection(new NetworkConnectionSetting(true, false, false));

    }

    public void setNetworkToData() {

        androidDriver.setNetworkConnection(new NetworkConnectionSetting(false, false, true));

    }

    public void connectWifi() {

        log("连接到Wifi");
        androidDriver.setNetworkConnection(new NetworkConnectionSetting(false, true, true));
        
    }
    
    

    public void disconnectWifi() {

        log("断开Wifi");
        androidDriver.setNetworkConnection(new NetworkConnectionSetting(false, false, true));

    }
    
    public int getNetWorkStatus(){
        
        return androidDriver.getNetworkConnection().value;
    }
    
    
    private boolean tap(String elementName, int pressTime, String type) {

        return tap(elementName, "", "", pressTime, type);

    }

    private boolean tap(String itemList, Object itemMatching, int pressTime, String type) {

        return tap(itemList, itemMatching, "", pressTime, type);

    }

    public boolean tapOn(double x, double y) {

        boolean returnValue = false;
        int windowlenX = androidDriver.manage().window().getSize().getWidth();
        int windowlenY = androidDriver.manage().window().getSize().getHeight();
        if (windowlenX > 0 && windowlenY > 0)
            returnValue = true;
        androidDriver.tap(1, windowlenX * (int) (x * 10) / 100, windowlenY * (int) (y * 10) / 100, 200);

        return returnValue;
    }

    public boolean tapOnByXY(double x, double y) {
        log("Using coordinate click {x:" + x + " , y:" + y + "}.");
        boolean returnValue = false;
        androidDriver.tap(1, (int) x, (int) y, 200);
        return returnValue;
    }

    private boolean tap(String itemList, Object itemMatching, String elementName, int pressTime, String type) {

        boolean returnValue = false;
        // String locator = getElementLocator(itemList);
        String view = getTargetView(type, itemList, "");
        /*
         * if (locator.contains("-")) { String xStr = locator.split("-")[0];
         * String yStr = locator.split("-")[1]; int x = Integer.valueOf(xStr);
         * int y = Integer.valueOf(yStr); int windowlenX =
         * androidDriver.manage().window().getSize().getWidth(); int windowlenY
         * = androidDriver.manage().window().getSize().getHeight();
         * androidDriver.tap(1, windowlenX * x / 10, windowlenY * y / 10,
         * pressTime); }
         */
        boolean emptyItemMatching = true;
        if (itemMatching instanceof Integer) {
            if ((Integer) itemMatching > 0)
                emptyItemMatching = false;
        }
        if (itemMatching instanceof String) {
            if (!((String) itemMatching).isEmpty())
                emptyItemMatching = false;
        }
        if (emptyItemMatching) {
            waitForElement(itemList);
            tap(1, itemList, pressTime);
        } else {
            waitForElement(itemList, itemMatching, "");
            tap(1, itemList, itemMatching, pressTime);
        }
        returnValue = uiMapUpdated(view);
        if (!returnValue) {
            takeFullScreenShot(elementName.isEmpty() ? itemList : elementName);
            throw new RuntimeException("uiMap Update Error.");
        }

        return returnValue;

    }

    public boolean swipe(String direction) {

        return SwipeAndUpdateView(direction);
    }

    protected boolean SwipeAndUpdateView(String direction) {

        String swipeToView = getTargetView("gestures", "", direction);
        swipeOfType(direction);
        // if (!swipeToView.isEmpty())
        // log("swipe to view :" + swipeToView, 1);
        return uiMapUpdated(swipeToView);
    }

    public boolean tapOn(String elementName) {

        return tap(elementName, 500, "tap");
    }

    public boolean tapOn(String itemList, int itemMatching) {

        return tap(itemList, itemMatching, 500, "tap");
    }

    public boolean flickOn(String elementName) {

        return tap(elementName, "", "", 0, "tap");
    }

    public boolean flickOn(String itemList, int itemMatching) {

        return tap(itemList, itemMatching, "", 0, "tap");
    }

    public boolean pressOn(String elementName, int pressTime) {

        return tap(elementName, pressTime, "press");
    }

    public boolean pressOn(String itemList, int itemMatching, int pressTime) {

        return tap(itemList, itemMatching, pressTime, "press");
    }

    protected int getCoorinateX() {

        return androidDriver.manage().window().getSize().getWidth();

    }

    protected int getCoorinateY() {

        return androidDriver.manage().window().getSize().getHeight();

    }

    public void reset() {

        androidDriver.resetApp();
    }

    public void close() {

        androidDriver.closeApp();
    }

    public void removeApp(String packageName) {

        androidDriver.removeApp(packageName);
    }

    public void switchToActivity(String packageApp, String activityPage) {

        androidDriver.startActivity(packageApp, activityPage);
    }

    private boolean getAndroidText(String className, String expectedText) {

        boolean validate = false;
        List<WebElement> listText = androidDriver.findElements(By.className(className));
        int textSize = listText.size();
        for (int i = 0; i < textSize; i++) {
            String bodyText = listText.get(i).getText();
            if (bodyText.contains(expectedText) || bodyText.matches(expectedText)) {
                validate = true;
                break;
            }
        }

        return validate;
    }

    public String getValueOf(String elementName) {

        String text = "";
        waitForElement(elementName);
        WebElement element = getElement(elementName);
        text = element.getText();
        log("The content of '" + elementName + "' is '" + text + "'!");

        return text;
    }

    public String getValueOf(String elementName, int itemMatching) {

        String text = "";
        waitForElement(elementName, itemMatching, "");
        WebElement element = getElement(elementName, itemMatching);
        text = element.getText();
        log("The content of '" + elementName + "' is '" + text + "'!");

        return text;
    }

    public boolean compareText(String elementName, String expectText) {

        boolean isSame = false;
        String text = "";
        waitForElement(elementName);
        WebElement element = getElement(elementName);
        text = element.getText().replace(" ", "");
        if (text.contains(expectText.replace(" ", "")))
            isSame = true;
        else {
            log("The text of element '" + elementName + "' is not " + expectText, 2);
            throw new RuntimeException();
        }

        return isSame;
    }

    @Override
    protected boolean verifyBodyTextContainsExpectedText(String expectedText, boolean isShown) {

        boolean returnValue = false;
        Long currentTimeMillis = System.currentTimeMillis();
        try {
            while ((System.currentTimeMillis() - currentTimeMillis) < Long.parseLong(elementTimeout)) {
                returnValue = getAndroidText("android.widget.TextView", expectedText);
                if (isShown == returnValue) {
                    break;
                }
                waitByTimeout(500);
            }
        } catch (Exception e) {

        }
        return returnValue;
    }

    public double getFlow() {

        double flow = AndroidMonitor.Flow(getSutFullFileName("app.package"));

        return flow;
    }

    public double getCPU() {

        double CPU = AndroidMonitor.CPU(getSutFullFileName("app.package"));

        return CPU;
    }

    public double getMemory() {

        double Memory = AndroidMonitor.Memory(getSutFullFileName("app.package"));

        return Memory;
    }

    public void monkeyTest(int count) {

        String nStr = getSutFullFileName("test.monkey.times");
        if (nStr.isEmpty()) {
            AndroidMonitor.Monkey(getSutFullFileName("app.package"), String.valueOf(count));
            log("随机事件次数：" + count);
        } else {
            int times = Integer.parseInt(nStr);
            AndroidMonitor.Monkey(getSutFullFileName("app.package"), String.valueOf(times));
            log("随机事件次数+" + times);
        }
    }

    public int getElementX(String elementName) {
        return getElementX("", "", elementName);
    }

    public int getElementX(String listName, Object itMatch) {
        return getElementX(listName, itMatch, "");
    }

    public int getElementX(String listName, Object itMatch, String elementName) {
        return getElement(listName, itMatch, elementName).getLocation().x;
    }

    public int getElementY(String elementName) {
        return getElementY("", "", elementName);
    }

    public int getElementY(String listName, Object itMatch) {
        return getElementY(listName, itMatch, "");
    }

    public int getElementY(String listName, Object itMatch, String elementName) {
        return getElement(listName, itMatch, elementName).getLocation().y;
    }

    public void switchToApp(String packageName, String activity) {
        androidDriver.startActivity(packageName, activity);
    }

    public String getCurrentActivity() {

        return androidDriver.currentActivity();

    }

    protected int getIndex(String listName, Object itemMatching) {
        int index = 0;
        String listLocator = getElementLocator(listName);
        if (listLocator.isEmpty()) {
            throw new NullPointerException();
        }
        System.out.println();

        boolean emptyItemMatching = true;
        if (itemMatching instanceof Integer)
            if ((Integer) itemMatching > 0)
                emptyItemMatching = false;
        if (itemMatching instanceof String)
            if (!((String) itemMatching).isEmpty())
                emptyItemMatching = false;
        if (emptyItemMatching) {
            throw new NullPointerException("itemMatching is error.");
        } else {

            List<WebElement> listElements = waitForElementList(listLocator, Integer.parseInt(elementTimeout) / 1000);

            index = getMatchingIndex(listElements, itemMatching);

        }
        return index;

    }

    public boolean verifyIsDisplayed(String listName, Object itMatch, String elementName) {
        int index = getIndex(listName, itMatch);
        return verifyIsShown(elementName, index);
    }

    public String getValueOnAndroid(String listName, Object itMatch, String elementName) {
        int index = getIndex(listName, itMatch);
        return getValueOf(elementName, index);
    }

    /**
     * 
     * @param apkPath
     * @return
     */
    public String getPackageSize(String apkPath) {
        try {
            return (float) (CommonTools.getFileSize(new File(apkPath)) / (float) 1024 / (float) 1024) + "MB";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件名:app-preview-release.apk 
     * 文件大小:10947637Bit; 10.44048MB 
     * 版本号:1090
     * 版本名:2.7.0 
     * 包名:hk.com.nextmedia.magazine.nextmediaplus
     * 
     * @param apkPath
     * @return
     */
    public String getPackageName(String apkPath) {

       return ApkUtil.info.get("packageName");
    }
    
    public String getVersion(String apkPath) {

        return ApkUtil.info.get("version");
     }
    
    public String getVersionName(String apkPath) {

        return ApkUtil.info.get("versionName");
     }
    
}
