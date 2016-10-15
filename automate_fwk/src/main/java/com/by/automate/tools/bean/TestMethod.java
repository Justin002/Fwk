package com.by.automate.tools.bean;

public class TestMethod {
	
	private String methodName;
	private String result;
	private String runtime;
	private String screenShortName;
	private String stepName;
	private String log;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	private String comment;
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getScreenShortName() {
		return screenShortName;
	}
	public void setScreenShortName(String screenShortName) {
		this.screenShortName = screenShortName;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	
	public String getLog(){
		return log;
	}
	
	public void setLog(String log){
		this.log = log;
	}
	
	public TestMethod(){}
	
	public TestMethod(String methodName, String result, String runtime, String screenShortName, String stepName,String comment,String log) {
		super();
		this.methodName = methodName;
		this.result = result;
		this.runtime = runtime;
		this.screenShortName = screenShortName;
		this.stepName = stepName;
		this.comment = comment;
		this.log = log;
	}
	
	
}
