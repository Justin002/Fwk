package com.by.automate.tools;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.by.automate.tools.bean.RemoteBrowserBean;

/**
 * 
 * @author Justin
 *
 */
public class DriverFactory {
	
	public WebDriver getRemoteDriver(RemoteBrowserBean remoteBrowserBean){
		
		DesiredCapabilities capability = null;
		
		if(remoteBrowserBean.getBrowserName().contains("firefox")){
			capability = DesiredCapabilities.firefox();
		}else if(remoteBrowserBean.getBrowserName().contains("chrome")){
			capability = DesiredCapabilities.chrome();
		}
		WebDriver driver = null;
		
		try {
			driver = new RemoteWebDriver(new URL(remoteBrowserBean.getHubURL()), capability);
		} catch (Exception e) {
			e.printStackTrace();
		}
		capability.setBrowserName(remoteBrowserBean.getBrowserName());
		capability.setVersion(remoteBrowserBean.getVersion());
		capability.setCapability(remoteBrowserBean.getPlatform()[0],remoteBrowserBean.getPlatform()[1]);
		 
		driver.manage().window().maximize();
		 
		return driver;
	}
}
