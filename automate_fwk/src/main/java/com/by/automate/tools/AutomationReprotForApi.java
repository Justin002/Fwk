package com.by.automate.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import jxl.write.Formula;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import com.by.automate.tools.bean.API;
import com.by.automate.tools.bean.AppInfo;
import com.by.automate.utils.CommonTools;
import com.by.automate.utils.ExcelUtils;
import com.by.automate.utils.ReadXmlUtils;
import com.ibm.icu.text.DecimalFormat;

public class AutomationReprotForApi {
    protected static List<String> rows = new ArrayList<String>();
   /* private static String title = "自动化测试报告";
    private static String module1 = "测试概况";
    private static String module2 = "测试统计";
    private static String rate = "评级";
    private static String noteMsg = "温馨提示：更多详细信息请查看Automation Test Reprot(Sheet)";*/
    
    private static String title = "The Automation Test Report";
    private static String module1 = "General Situation Of Test";
    private static String module2 = "Parameters Statistics";
    private static String rate = "Rated";
    private static String noteMsg = "Reminder : Please verify the sheet 'Test Reprot of Details' for more information.";
    
    // for android 
    private static String[] module1Str = null;
    
    private static String[] module2Str = { "TESTS", "PASS", "FAILED", "SKIP", "ERROR", "Success Rate", "Duration(m)" };

    private static String textColor = "white";

    private static WritableSheet sheet = null;
    // test data
    private static String excel;
    private static String imagepath;
    private static String thisxx;
    private static String thisx;
    
    
    private static void getModule1Str(){
 
    	//module1Str = new String[]{ "App名称", "App版本", "测试平台", "测试网络", "安装包大小", "浏览器", "测试范围", "测试日期" };
    	module1Str = new String[]{ "App Name", "App Version",  "Test Plan", "Test Network", "", "", "","Test  Date" };
    	 
    }
    
    public static void initialpath(String excelPath, String image, String xx, String x) {
        excel = excelPath;
        imagepath = image;
        thisxx = xx;
        thisx = x;
    }
    


    /**
     * 创建Excel 保存log信息. 在创建时候会初始化固定模板. 如果Excel存在追加信息,如果不存在则创建新的.
     * 
     * @param excelPath
     *            Excel 保存log 的路径. 在这里是"/cms-autotest/custom_report".
     * @param sheetName
     *            创建Excel 时候需要添加一个Sheet.
     * @param index
     */
    public static void createSummary(String sheetName,AppInfo appInfo) {

        try {
            // 如果excel 不存在就需要创建它 ,如果存在直接追加信息.
            if (!(new File(excel).exists())) {
            	
                ExcelUtils.createExcel(excel, sheetName);
                ExcelUtils.openSheet(sheetName);
                getModule1Str();
                //
               // ExcelUtils.setBg();
                
                
                // add 自动化测试报告
                ExcelUtils.writeData(0, 0, title, ExcelUtils.addStyle(24, false, true, true, "lightBlue", textColor));
                ExcelUtils.mergerCell(0, 0, 6, 2);

                // add 测试概括
                ExcelUtils.mergerCell(0, 3, 6, 4);
                ExcelUtils.writeData(0, 3, module1, ExcelUtils.addStyle(18, false, true, true, "periwinkle", textColor));

                // 添加android 图片到excel
                ExcelUtils.writeImageToExcel(imagepath, 0.16, 5.5, 0.7, 3.2);

                // app 名称
                ExcelUtils.writeData(1, 5, module1Str[0], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // app 版本
                ExcelUtils.writeData(1, 6, module1Str[1], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 测试平台
                ExcelUtils.writeData(1, 7, module1Str[2], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 测试网络
                ExcelUtils.writeData(1, 8, module1Str[3], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 安装包大小
                ExcelUtils.writeData(3, 5, module1Str[4], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 测试机型
                ExcelUtils.writeData(3, 6, module1Str[5], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 测试范围
                ExcelUtils.writeData(3, 7, module1Str[6], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 测试日期
                ExcelUtils.writeData(3, 8, module1Str[7], ExcelUtils.addStyle(12, false, true, true, "paleBlue", textColor));

                // 评级
                ExcelUtils.writeData(5, 5, rate, ExcelUtils.addStyle(14, false, true, false, "paleBlue", textColor));
                ExcelUtils.mergerCell(5, 5, 6, 5);
                ExcelUtils.mergerCell(5, 6, 6, 8);

                // 添加统计测试
                ExcelUtils.mergerCell(0, 9, 6, 10);
                ExcelUtils.writeData(0, 9, module2, ExcelUtils.addStyle(16, false, true, true, "periwinkle", textColor));

                for (int i = 0; i < module2Str.length; i++) {
                    String ys = "periwinkle";
                    if (i == 0) {
                        ys = "grey50";
                    } else if (i == 1) {
                        ys = "lime";
                    } else if (i == 2) {
                        ys = "red";
                    } else if (i == 3) {
                        ys = "gold";
                    } else if (i == 4) {
                        ys = "darkRed";
                    } else if (i == 5) {
                        ys = "grey25";
                    } else if (i == 6) {
                        ys = "grey25";
                    }
                    ExcelUtils.mergerCell(i, 11, i, 12);
                    ExcelUtils.writeData(i, 11, module2Str[i], ExcelUtils.addStyle(12, false, true, false, ys, textColor));
                    // value
                    ExcelUtils.mergerCell(i, 13, i, 14);
                }

                // 添加温馨提示
                ExcelUtils.mergerCell(0, 15, 6, 16);
                ExcelUtils.writeData(0, 15, noteMsg, ExcelUtils.addStyle(14, false, false, false, "lightBlue", textColor));

                // 调整格式 title
                ExcelUtils.setCellSize(0, 0, 20, 320);
                ExcelUtils.setCellSize(0, 1, 20, 320);
                ExcelUtils.setCellSize(0, 2, 20, 320);

                // 调整格式 测试概括
                ExcelUtils.setCellSize(0, 3, 20, 280);
                ExcelUtils.setCellSize(0, 4, 20, 280);

                // 调整格式， 测试概况Info
                ExcelUtils.setCellSize(0, 5, 20, 420);
                ExcelUtils.setCellSize(0, 6, 20, 420);
                ExcelUtils.setCellSize(0, 7, 20, 420);
                ExcelUtils.setCellSize(0, 8, 20, 420);
                // 圖片后的cell
                ExcelUtils
                        .writeData(0, 5, "", ExcelUtils.addStyle(10, false, false, false, textColor, textColor, textColor));
                ExcelUtils
                        .writeData(0, 6, "", ExcelUtils.addStyle(10, false, false, false, textColor, textColor, textColor));
                ExcelUtils
                        .writeData(0, 7, "", ExcelUtils.addStyle(10, false, false, false, textColor, textColor, textColor));
                ExcelUtils
                        .writeData(0, 8, "", ExcelUtils.addStyle(10, false, false, false, textColor, textColor, textColor));

                // 调整格式 测试统计
                ExcelUtils.setCellSize(0, 9, 20, 280);
                ExcelUtils.setCellSize(0, 10, 20, 280);

                // 调整格式 Tests,Pass,filed,skip,error, successRate ,Duration.
                ExcelUtils.setCellSize(0, 11, 18, 340);
                ExcelUtils.setCellSize(1, 11, 24, 340);
                ExcelUtils.setCellSize(2, 11, 24, 340);
                ExcelUtils.setCellSize(3, 11, 24, 340);
                ExcelUtils.setCellSize(4, 11, 24, 340);
                ExcelUtils.setCellSize(5, 11, 18, 340);
                ExcelUtils.setCellSize(6, 11, 18, 340);

                // 提示
                ExcelUtils.setCellSize(0, 15, 18, 280);
                ExcelUtils.setCellSize(0, 16, 18, 280);
                
                // 写入app 以及测试环境的信息到excel， 只执行一次.
                writeAppinfotoSummary(appInfo);
                ExcelUtils.close();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    
    private static void writeAppinfotoSummary(AppInfo appinfo){
    	
    	 String version = appinfo.getVersion();
    	 String environment = appinfo.getEnvironment();
    	 String testCategory = appinfo.getTestCategory();
    	 String appName = appinfo.getAppName();
    	 String netWork = appinfo.getNetWork();
    	 //String appSize = appinfo.getAppSize();
    	 //String testRange = appinfo.getTestRange();
    	 //String deviceName = appinfo.getDeviceName();
    	 //String deviceVersion = appinfo.getDeviceVersion();
    	 //String description = appinfo.getDescription();
    	 
    	 ExcelUtils.writeData(2, 5, appName,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 6, version+"("+environment+")",ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 7, testCategory,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 8, netWork,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 //ExcelUtils.writeData(4, 5, appSize,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 //ExcelUtils.writeData(4, 6, deviceName,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 //ExcelUtils.writeData(4, 7, deviceVersion,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(4, 8, CommonTools.getDate(),ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    }

  
    /**
     * 构建详细信息sheet
     * 
     * @param args
     */
    private static String[] results = { "API-NUMBER", "API-DESCRIPTION", "URL", "CHECKPOINT","METHOD" , "STATUS", "AUTO/MANUAL", "DURATION(m)", "LOGS" };

    /**
     * 创建Test Report of Details sheet.
     * 
     * @param excelPath
     * @param sheetName
     */
    public static void createDetailsSheet(String sheetName) {
        sheet = ExcelUtils.getSheet(excel, sheetName);
        if (null == sheet) {
            ExcelUtils.close();
            ExcelUtils.createSheet(excel, sheetName, 1);
            sheet = ExcelUtils.getSheet(excel, sheetName);
            for (int i = 0; i < results.length; i++) {

                ExcelUtils.mergerCell(i, 0, i, 1);
                ExcelUtils.writeData(i, 0, results[i], ExcelUtils.addStyle(12, false, true, false, "periwinkle", textColor));

            }

            ExcelUtils.setCellSize(0, 0, 18, 300);
            ExcelUtils.setCellSize(1, 0, 22, 300);
            ExcelUtils.setCellSize(2, 0, 18, 300);
            ExcelUtils.setCellSize(3, 0, 18, 300);
            ExcelUtils.setCellSize(4, 0, 15, 300);
            ExcelUtils.setCellSize(5, 0, 15, 300);
            ExcelUtils.setCellSize(6, 0, 20, 300);
            ExcelUtils.setCellSize(7, 0, 18, 300);
            ExcelUtils.setCellSize(8, 0, 15, 300);
        }

    }

    /**
     * 写入每个@Test 数据到详细sheet.
     * 
     * @param ls
     *            使用testng 监控来获取每个@Test数据, class, methid, pass, failed, skip,
     *            error, run time, errorScreenShot, checkPoint, input(setp)
     */
    @SuppressWarnings("unused")
	public static void writeData(List<API> ls) {
    	
    	// 格式化 float 数据类型.
        DecimalFormat df = new DecimalFormat("######0.00");
    	int countPass=0,countFailed=0,countSkip=0,countError=0;
    	float  countTime = 0;
    	for (int i = 0; i < ls.size(); i++) {
    		API api = ls.get(i);
        	
        	// api number 
        	String apiNumber = api.getApiNumber();
        	String apiDescription = api.getApiDescription();
        	String url = api.getUrl();
        	String method = api.getMethod();
        	String checkPoint = api.getCheckpoint();
        	String status = api.getStatus();
        	String execute = api.getExecute();
        	Float time = (Float.parseFloat(api.getTime()))/ 60000;
        	String log = api.getLog();
        	
        	String pass = "0", fail = "0", skip = "0", error = "0";
            if (status.equals("SUCCESS")) {
                pass = "1";
                countPass++;
            } else if (status.equals("FAILURE")) {
               	fail = "1";
               	countFailed++;
            } else if (status.equals("SKIP")) {
            	skip = "1";
            	countSkip++;
            } else if (status.isEmpty()) {
            	error = "1";
            	countError++;
            }
            
            int row = ExcelUtils.getRow();

            // 写入Api number
            ExcelUtils.writeData(0, row, apiNumber, ExcelUtils.addStyle(10, false, true, false, "white"));
            
            ExcelUtils.writeData(1, row, apiDescription, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(1, row, apiDescription.length() + 18, 280);

            // url
            ExcelUtils.writeData(2, row, url, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(3, row, url.length()+6 , 280);
            
            // checkpoint
            ExcelUtils.writeData(3, row, checkPoint, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(3, row, (checkPoint.length()>11)? checkPoint.length():20, 280);
  
            // Method
            ExcelUtils.writeData(4, row,method, ExcelUtils.addStyle(10, false, true, false, "white"));
            
            // 写入当前@Test 状态.
            ExcelUtils.writeData(5, row, pass.equals("1") ? "SUCCESS" : "FAILED",
            ExcelUtils.addStyle(10, false, true, true, pass.equals("1") ? "lime" : "red", "white"));
            
            // Auto/Manual
            ExcelUtils.writeData(6, row, execute, ExcelUtils.addStyle(10, false, true, false, "white"));
            
            // 写入时间
            ExcelUtils.writeData(7, row, df.format(time), ExcelUtils.addStyle(10, false, true, false, "white"));
            
            // 添加log链接.
           setHyperLinkForSheet(8, row, log);
        
           countTime+= countTime;
		}
    	   // 关闭detials sheet.
        ExcelUtils.close();

       // 打开Summary sheet
        ExcelUtils.openSheet("Summary");

        // 获取当前Class 信息 统计到Summary sheet. 每写一个class 信息的时候 同时Summary 也会同步.
        setValueToSummary(ls.size(), countPass, countFailed, countSkip, countError, countTime);

        // 保存信息
        ExcelUtils.close();

        ls.clear();
        
    }

  
    /**
     * 获取Summary 本身有的数据 和当前class 数据相加 再写入到Summary 这样可以同每一个Class 信息.
     * 
     * @param method
     *            - Tests
     * @param pass
     *            -PASS
     * @param failed
     *            -FAILED
     * @param skip
     *            -SKIP
     * @param error
     *            -ERRORS
     * @param time
     *            - Duration(m)
     */
    private static void setValueToSummary(int method, int pass, int failed, int skip, int error, float time) {
        // 先获取tests, pass, failed, skip , error , duration 信息.
        DecimalFormat df = new DecimalFormat("######0.00");
        int tests = getTestsValue(0, 13) + method;
        pass = getTestsValue(1, 13) + pass;
        failed = getTestsValue(2, 13) + failed;
        skip = getTestsValue(3, 13) + skip;
        error = getTestsValue(4, 13) + error;
        time = getTimeValueFloat(6, 13) + time;
        String successRate = (pass * 100 / (tests)) + "%";

        ExcelUtils.writeData(0, 13, tests, ExcelUtils.addStyle(12, false, true, false, "black", "black"));
        ExcelUtils.writeData(1, 13, pass, ExcelUtils.addStyle(12, false, true, false, textColor, "black"));
        ExcelUtils.writeData(2, 13, failed, ExcelUtils.addStyle(12, false, true, false, textColor, "black"));
        ExcelUtils.writeData(3, 13, skip, ExcelUtils.addStyle(12, false, true, false, textColor, "black"));
        ExcelUtils.writeData(4, 13, error, ExcelUtils.addStyle(12, false, true, false, textColor, "black"));
        ExcelUtils.writeData(5, 13, successRate, ExcelUtils.addStyle(12, false, true, false, textColor, "black"));
        ExcelUtils.writeData(6, 13, df.format(time) + "m", ExcelUtils.addStyle(12, false, true, false, textColor, "black"));

        // 添加评级
        addRate(pass, tests);
    }

    /**
     * 添加评级
     * 
     * @param pass
     * @param tests
     */
    @SuppressWarnings("unused")
    private static void addRate(int pass, int tests) {

        int pj = (int) (pass * 100 / (tests));

        int fullBXX = pj / 10;
        int fullXX = 0;

        int bkx = 0;
        if (fullBXX >= 2) {
            fullXX = fullBXX / 2;
        }
        fullBXX = fullBXX % 2;

        double jl = 0;
        double begin = 5.4;
        for (int i = 0; i < fullXX; i++) {
            if (i != 0) {
                jl = +0.2;
                begin += jl;
            } else {
                jl = 0.2;
            }
            ExcelUtils.writeImageToExcel(thisxx, begin, 7.1, 0.2, 0.9);

        }

        // 添加半颗星星的图片
        if (fullBXX == 1) {
            // 添加半颗星星图片
            ExcelUtils.writeImageToExcel(thisx, begin + 0.2, 7.1, 0.2, 0.9);
        }

    }

  

    /**
     * 添加空行并合并.
     * 
     * @return
     */
    public static int addEmptyMegerRow(String details) {
        int end = sheet.getRows();
        ExcelUtils.mergerCell(0, end, 6, end);
        // setCellSize(0, end,5, 250);
        ExcelUtils.writeData(0, end, details, ExcelUtils.addStyle(10, false, false, false, "white"));
        return end;

    }

    /**
     * 超链接 跳转Log.log
     *
     * @param col
     * @param row
     * @param desc
     * @param sheetName
     * @param destCol
     * @param destRow
     */
    private static void setHyperLinkForSheet(int col, int row, String desc) {

        String logPath = ("../Logs/" + desc + ".log");
        try {
            // desc = (logPath.split("/")[logPath.split("/").length -
            // 1]).replace(".log", "(LOG)");
            desc = "Detailed Log";
            String formu = "HYPERLINK(\"" + logPath + "\", \"" + desc + "\")";
            Formula formula = new Formula(col, row, formu, ExcelUtils.addStyle(9, false, true, false, "grey80", "white"));
            sheet.addCell(formula);

        } catch (WriteException e) {

            e.printStackTrace();

        }

    }

    /**
     * 获取时间
     * 
     * @param cel
     * @param row
     * @return
     */
    public static float getTimeValueFloat(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents().replace("m", "");
        if (value.isEmpty()) {
            return 0;
        } else {
            return Float.parseFloat(value);
        }

    }

    /**
     * 获取 test set 总数
     * 
     * @param cel
     * @param row
     * @return
     */
    public static int getTestsValue(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents().toString();
        if (value.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }

    }
    
    /**
     * 解析api xml文件 将 每个api 节点数据已API对象形式存放到list 集合中.
     * 
     * @param xmlPath
     * @return
     */
    @SuppressWarnings("unchecked")
	public static List<API> analysisXML(String xmlPath){
    	
    	String apiNumber;
    	String apiDescription;
    	String url;
    	String method;
    	String checkpoint;
    	String status;
    	String execute;
    	String time;
    	String log;
    	API api = null;
    	List<API> ls = new ArrayList<API>();
    	
    	Document docuemnt = ReadXmlUtils.getDocument(xmlPath);
    	Element root = docuemnt.getRootElement();
    	
    	List<Element> elements = root.selectNodes("/APIS/Api");
    	for (int i = 0; i < elements.size(); i++) {
    		Element currentElement = elements.get(i);
			apiNumber = currentElement.attribute("Number").getValue().toString().trim();
			apiDescription = currentElement.element("ApiDescription").getText().toString().trim();
			url = currentElement.element("URL").getText().toString().trim();
			method = currentElement.element("Method").getText().toString().trim();
			checkpoint = currentElement.element("CheckPoint").getText().toString().trim();
			status = currentElement.element("Status").getText().toString().trim();
			execute = currentElement.element("Execute").getText().toString().trim();
			time = currentElement.element("Time").getText().toString().trim();
			log = currentElement.element("Log").getText().toString().trim();
			
			api = new API(apiNumber, apiDescription, url, method, checkpoint, status, execute, time, log);
			ls.add(api);
    	}
    	
    	return ls;
    	
    }
    
    public static void main(String[] args) {
    	//ExcelUtils.createExcel("D:/localGit/repositories/automate_fwk/src/test/resources/data/custom_report/API/PGY/report/result.xls", "Summary");
		initialpath("D:/localGit/repositories/automate_fwk/src/test/resources/data/custom_report/API/PGY/report/result.xls", "D:/localGit/repositories/automate_fwk/src/test/resources/data/icon/web.png", "D:/localGit/repositories/automate_fwk/src/test/resources/data/icon/X1.png", "D:/localGit/repositories/automate_fwk/src/test/resources/data/icon/X2.png");
		AppInfo appInfo = new AppInfo("1.0","QA", "100", "test", "LocaWork", "test","12.00","12", "12","4.0");
		createSummary("Summary", appInfo);
		createDetailsSheet("Api");
		writeData(analysisXML("D:/localGit/repositories/automate_fwk/src/test/resources/data/custom_report/API/PGY/PGY_TestReport_20160309_13_47_55.055.xml"));
	}

}
