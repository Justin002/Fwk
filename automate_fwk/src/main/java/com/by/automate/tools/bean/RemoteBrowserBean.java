package com.by.automate.tools.bean;

/**
 * for remote browser bean.
 * @author Justin
 *
 */
public class RemoteBrowserBean {
		
	private String browserName;
	private String version;
	private String[] platform;
	private String hubURL;
	public String getBrowserName() {
		return browserName;
	}
	public String getVersion() {
		return version;
	}
	public String[] getPlatform() {
		return platform;
	}
	public String getHubURL() {
		return hubURL;
	}
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setPlatform(String[] platform) {
		this.platform = platform;
	}
	public void setHubURL(String hubURL) {
		this.hubURL = hubURL;
	}
	
	public RemoteBrowserBean(){
		this.browserName = "firefox";
		this.version = "30";
		this.platform= new String[]{"VISTA", "windows 7"};
		this.hubURL = "http://localhost:4444/wd/hub";
	}
	
	public RemoteBrowserBean(String browser){
		this.browserName = browser;
		this.version = "42";
		this.platform= new String[]{"VISTA", "windows 7"};
		this.hubURL = "http://localhost:4444/wd/hub";
	}
	
	
	
	
}
