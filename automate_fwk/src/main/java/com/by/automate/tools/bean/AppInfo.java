package com.by.automate.tools.bean;

public class AppInfo {
	
	private String version;
	private String environment;
	private String testCategory;
	private String appName;
	private String netWork;
	private String description;
	private String appSize;
	private String deviceName;
	private String deviceVersion;
	private String testRange;
	
	
	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getEnvironment() {
		return environment;
	}


	public void setEnvironment(String environment) {
		this.environment = environment;
	}


	public String getTestCategory() {
		return testCategory;
	}


	public void setTestCategory(String testCategory) {
		this.testCategory = testCategory;
	}


	public String getAppName() {
		return appName;
	}


	public void setAppName(String appName) {
		this.appName = appName;
	}


	public String getNetWork() {
		return netWork;
	}


	public void setNetWork(String netWork) {
		this.netWork = netWork;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getAppSize() {
		return appSize;
	}


	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}


	public String getDeviceName() {
		return deviceName;
	}


	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	public String getDeviceVersion() {
		return deviceVersion;
	}


	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}


	public String getTestRange() {
		return testRange;
	}


	public void setTestRange(String testRange) {
		this.testRange = testRange;
	}


	public AppInfo(String version,
	 String environment,
	 String testCategory,
	 String appName,
	 String netWork,
	 String description,String appSize, String testRange, String deviceName,String deviceVersion){
		
	this.version = version;
	this.environment = environment;
	this.testCategory = testCategory;
	this.appName = appName;
	this.netWork = netWork;
	this.description = description;
	this.appSize = appSize;
	this.testRange = testRange;
	this.deviceName = deviceName;
	this.deviceVersion = deviceVersion;
	}
}
