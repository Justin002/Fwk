package com.by.automate.tests.ep2p.android;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.by.automate.fwk.Ep2pApp;

public class Test_FCDemo {
	
	private Ep2pApp ep = null;
	
	
	@BeforeClass
	public void setUp(){
		
		ep = new Ep2pApp();
		ep.openApp();
		ep.putStep("EP2P001");
		
	}
	
	@Test
	public void Test010VerifyHomePage(){
		
		ep.verifyIsShown("首页");
		ep.verifyIsShown("发现");
		ep.verifyIsShown("我的");
		
	}
	
	@Test
	public void Test020GotoFaxian(){
	
		ep.clickOn("发现");
		ep.verifyIsShown("领取红包");
		ep.verifyIsShown("领取加息券");
		ep.verifyIsShown("小e社区");
		ep.verifyIsShown("投资排行");
		ep.verifyIsShown("收益计算");
		ep.verifyIsShown("新手指引");
	}
	
	@Test
	public void Test030GotoWode(){
	
		ep.clickOn("我的");
		ep.verifyIsShown("立即登陆");
		ep.verifyIsShown("已投项目");
		ep.verifyIsShown("代收查询");
		ep.verifyIsShown("还款");
		ep.verifyIsShown("交易记录");
		ep.verifyIsShown("自动投标");
		ep.verifyIsShown("安全中心");
		ep.verifyIsShown("邀请有奖");
		ep.verifyIsShown("更多");
	}
	
	@AfterClass
	public void tearDown(){
		
		ep.close();
		//ic.writeExcelData();
	}
	
}
