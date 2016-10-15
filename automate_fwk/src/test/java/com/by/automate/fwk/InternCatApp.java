package com.by.automate.fwk;

import java.io.File;

import com.by.automate.base.AndroidApp;

public class InternCatApp extends AndroidApp{

	 public InternCatApp() {

	        super();
	    }

	    public InternCatApp(String core) {
	        super(core);
	    }

	    protected String getAppName() {

	        return "InternCatApp";
	    }

		
		public void login(String phoneNumber, String pwd){
			
			verifyIsShown("手机号码");
			verifyIsShown("密码");
			verifyIsShown("登录");
			verifyIsShown("实习猫图标");
			setValue("手机号码", "13037131950");
			setValue("密码", "qwe123");
			clickOn("实习猫图标");
			clickOn("登录");
			
		}
		

}
