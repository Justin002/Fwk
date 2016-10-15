package com.by.automate.tools.bean;

public class API {
	
	private String apiNumber;
	private String apiDescription;
	private String url;
	private String method;
	private String checkpoint;
	private String status;
	private String execute;
	private String time;
	private String log;
	public String getApiNumber() {
		return apiNumber;
	}
	public String getApiDescription() {
		return apiDescription;
	}
	public String getUrl() {
		return url;
	}
	public String getMethod() {
		return method;
	}
	public String getCheckpoint() {
		return checkpoint;
	}
	public String getStatus() {
		return status;
	}
	public String getExecute() {
		return execute;
	}
	public String getTime() {
		return time;
	}
	public String getLog() {
		return log;
	}
	public void setApiNumber(String apiNumber) {
		this.apiNumber = apiNumber;
	}
	public void setApiDescription(String apiDescription) {
		this.apiDescription = apiDescription;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public void setCheckpoint(String checkpoint) {
		this.checkpoint = checkpoint;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setExecute(String execute) {
		this.execute = execute;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setLog(String log) {
		this.log = log;
	}
	
	public API(String apiNumber, String apiDescription, String url,
			String method, String checkpoint, String status, String execute,
			String time, String log) {
		super();
		this.apiNumber = apiNumber;
		this.apiDescription = apiDescription;
		this.url = url;
		this.method = method;
		this.checkpoint = checkpoint;
		this.status = status;
		this.execute = execute;
		this.time = time;
		this.log = log;
	}
	
	
	
}
