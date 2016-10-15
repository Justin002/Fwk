/*package com.by.automate.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import com.by.automate.base.core.InitiaFramework;
import com.by.automate.base.core.UiFramework;
import com.by.automate.tools.bean.AppInfo;
import com.by.automate.utils.CommonTools;
import com.by.automate.utils.ExcelUtils;
import com.by.automate.utils.ImageUtils;
import com.ibm.icu.text.DecimalFormat;

public class AutomationReprot {
    protected static List<String> rows = new ArrayList<String>();
    private static String title = "自动化测试报告";
    private static String module1 = "测试概况";
    private static String module2 = "测试统计";
    private static String rate = "评级";
    private static String noteMsg = "温馨提示：更多详细信息请查看Automation Test Reprot(Sheet)";
    
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
    	
    	String appType = InitiaFramework.appType;
    	// for android
    	if(appType.equals("AndroidApp")){
    		
    		//module1Str = new String[]{ "App名称", "App版本", "测试平台", "测试网络", "安装包大小", "测试机型", "测试范围", "测试日期" };
    		module1Str = new String[]{ "App Name", "App Version",  "Test Platform", "Test Network", "App Size", "Device Models", "Version","Test  Date" };
    	}
    	 
    	// for web
    	if(appType.equals("WebApp")){
    		
    		//module1Str = new String[]{ "App名称", "App版本", "测试平台", "测试网络", "安装包大小", "浏览器", "测试范围", "测试日期" };
    		module1Str = new String[]{ "App Name", "App Version",  "Test Platform", "Test Network", "App Size", "Browser", "Version","Test  Date" };
    	}
    	 
        // for IOS
    	if(appType.equals("instrumentDriver") || appType.equals("IOSApp")){
	
    		//module1Str = new String[]{ "App名称", "App版本", "测试平台", "测试网络", "安装包大小", "测试机型", "测试范围", "测试日期" };
    		module1Str = new String[]{ "App Name", "App Version",  "Test Platform", "Test Network", "App Size", "Device Models", "Version","Test  Date" };
    	}
    	
    }
    public static void initialpath(String excelPath, String image, String xx, String x) {
        excel = excelPath;
        imagepath = image;
        thisxx = xx;
        thisx = x;
    }
    


    *//**
     * 创建Excel 保存log信息. 在创建时候会初始化固定模板. 如果Excel存在追加信息,如果不存在则创建新的.
     * 
     * @param excelPath
     *            Excel 保存log 的路径. 在这里是"/cms-autotest/custom_report".
     * @param sheetName
     *            创建Excel 时候需要添加一个Sheet.
     * @param index
     *//*
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
    	 String appSize = appinfo.getAppSize();
    	 //String testRange = appinfo.getTestRange();
    	 String deviceName = appinfo.getDeviceName();
    	 String deviceVersion = appinfo.getDeviceVersion();
    	 //String description = appinfo.getDescription();
    	 
    	 ExcelUtils.writeData(2, 5, appName,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 6, version+"("+environment+")",ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 7, testCategory,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(2, 8, netWork,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(4, 5, appSize,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(4, 6, deviceName,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(4, 7, deviceVersion,ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    	 ExcelUtils.writeData(4, 8, CommonTools.getDate(),ExcelUtils.addStyle(12, false, true, false, textColor, "black") );
    }

    *//**
     * 获取apk 和测试环境信息.
     * 
     * @param info
     *//*
    public static void setTestProfile(Map<String, String> info) {
        Map<String, String> values = info;

        ExcelUtils.writeData(2, 5, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(2, 6, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(2, 7, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(2, 8, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));

        ExcelUtils.writeData(4, 5, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(4, 6, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(4, 7, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(4, 8, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));

    }

    *//**
     * 
     * @param result
     *//*
    public static void setTestResule(Map<String, String> result) {

        Map<String, String> values = result;

        ExcelUtils.writeData(0, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(1, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(2, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(3, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(4, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(5, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
        ExcelUtils.writeData(6, 13, values.get(""), ExcelUtils.addStyle(12, false, true, false, "black"));
    }

    *//**
     * 构建详细信息sheet
     * 
     * @param args
     *//*
    private static String[] results = { "TEST CLASS", "TESTS", "STATUS(Success Rete)", "INPUT(STEP)", "CHECKPOINT",
            "DURATION(m)", "SCREENSHOT" };

    *//**
     * 创建Test Report of Details sheet.
     * 
     * @param excelPath
     * @param sheetName
     *//*
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
            ExcelUtils.setCellSize(1, 0, 18, 300);
            ExcelUtils.setCellSize(2, 0, 26, 300);
            ExcelUtils.setCellSize(3, 0, 18, 300);
            ExcelUtils.setCellSize(4, 0, 18, 300);
            ExcelUtils.setCellSize(5, 0, 17, 300);
            ExcelUtils.setCellSize(6, 0, 26, 300);
        }

    }

    *//**
     * 写入每个@Test 数据到详细sheet.
     * 
     * @param ls
     *            使用testng 监控来获取每个@Test数据, class, methid, pass, failed, skip,
     *            error, run time, errorScreenShot, checkPoint, input(setp)
     *//*
    @SuppressWarnings("unused")
	public static void writeData(List<Map<String, String>> ls) {

        // 获取 @Test 个数
        int countTests = 0;
        // 统计当前class 通过的个数
        int countPass = 0;
        // 统计当前class 失败的个数
        int countFailed = 0;
        // 统计当前class 跳过的个数
        int countSkip = 0;
        // 统计当前class error的个数
        int countError = 0;
        // / 統計當前Class 所花費時間
        float countTime = 0;
        // 获取测试流程.
        String steps = ls.get(0).get("testProcess");
        String summary = ls.get(0).get("summary");
        String version = ls.get(0).get("version");
        String expectedResults  = ls.get(0).get("expectedResults");
        String actualResult = "";

        // 格式化 float 数据类型.
        DecimalFormat df = new DecimalFormat("######0.00");

        // 获取 监听后的数据. 获取当前calss 名字
        String className = ls.get(0).get("ClassName");
        // 获取总过 @Test个数
        countTests = ls.size();

        int row = 0;

        // 添加空行保存 method 統計信息， 需要在method 信息寫完後才能填寫信息,先保存結構在寫入
        int countClassInfo = addEmptyRow();

        // 开始写入Class @Test 等数据
        for (int i = 0; i < ls.size(); i++) {

            Map<String, String> result = new HashMap<String, String>();
            result = ls.get(i);

            String methodName = "", inputStep = "",index="", comment = "", imageName = "", pass = "0", fail = "0", skip = "0", error = "0";
            float time;
            // 统计class 运行成功method 的个数.作百分比.
            String status = result.get("status");
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

            // 得到Method 名字.
            methodName = result.get("method");
            // 得到当前Method 运行消耗的时间,单位秒.

            time = Float.parseFloat(result.get("time")) / 60000;
            // 获取Comment.
            comment = result.get("comment");
            // 获取input step
            inputStep = result.get("verify");
            index = result.get("index");
            inputStep = getInputStep(inputStep,index);
            // 获取图片名字
            imageName = result.get("imageName");

            row = ExcelUtils.getRow();

            // 写入ClassName,因为一个Class 存在多个@Test这里为了做合并只需要在第一次时候写入class 名字 后面直接合并
            if (i == 0) {
                ExcelUtils.writeData(0, row, className, ExcelUtils.addStyle(10, false, false, false, "iceBlue", "black"));
                int length = 0;
                if(className.length() < 12){
                 length = 14;   
                }else{
                    length =className.length() +1;
                }
                ExcelUtils.setCellSize(0, row, length, 280);
            }

            // 写入Method
            ExcelUtils.writeData(1, row, methodName, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(1, row, methodName.length() + 1, 280);

            // 写入当前@Test 状态.
            ExcelUtils.writeData(2, row, pass.equals("1") ? "SUCCESS" : "FAILED",
            ExcelUtils.addStyle(10, false, true, true, pass.equals("1") ? "lime" : "red", "white"));
            
            // input step
            ExcelUtils.writeData(3, row, inputStep, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(3, row, (inputStep.length()>11)? inputStep.length():12 , 280);
            
            // checkpoint
            ExcelUtils.writeData(4, row, comment, ExcelUtils.addStyle(10, false, false, false, "white"));
            ExcelUtils.setCellSize(4, row, (comment.length()>11)? comment.length():17, 280);

            // 写入时间
            ExcelUtils.writeData(5, row, df.format(time), ExcelUtils.addStyle(10, false, true, false, "white"));

            // 添加图片链接.(成功失败都会进行截图)
            String imagePath = getImagePath(imageName, className, result.get("appType"));
            setLinkToErrorScrenshot(6, row, imagePath);

            countTime += time;
            
            actualResult+= comment+"\n";

        }
        // 合并 class
        ExcelUtils.mergerCell(0, row - countTests + 1, 0, row);

        // 統計 當前Class 所有的測試結果.
        String classNameLog = className;
        int countMethod = ls.size();
        String BFB = countPass * 100 / countMethod + "%";

        // 把當前Class 信息寫到 excel.
        setClassInfoToExcel(classNameLog, countMethod, BFB, df.format(countTime), countClassInfo);

        // 添加空格行来分割Class并合并.
        steps = formatSteps(summary, steps, expectedResults , actualResult , version);
        addEmptyMegerRow(steps);

        // 关闭detials sheet.
        ExcelUtils.close();

        // 打开Summary sheet
        ExcelUtils.openSheet("Summary");

        // 每次到Summary sheet 里面需要清除评级的星星, 所有的测试脚本跑完后才能得到准确的评级.
        deleteImage();

        // 获取当前Class 信息 统计到Summary sheet. 每写一个class 信息的时候 同时Summary 也会同步.
        setValueToSummary(countMethod, countPass, countFailed, countSkip, countError, countTime);

        // 保存信息
        ExcelUtils.close();

        *//**
         * 使用 poi 添加group 实现折叠.
         *//*
        // rows.add((row - countTests + 1) + "=" + row);
        // CommonTools.createFile(new
        // File(excelPath).getAbsoluteFile().getPath().toString().replace(new
        // File(excelPath).getName().toString(), ""));
        // CommonTools.writeProperties(excelPath, "", pValue);

        // setGroupts("Summary", excelPath, rows);
        *//**
         * 每次写入一条class 信息到excel 都需要将它清空. 因为这里ls 是静态,如果不清空那么会出现数据叠加的情况.
         *//*
        ls.clear();
        LastListenersTestng.data.clear();
        UiFramework.info.clear();
    }
    
    private static String formatSteps(String summary, String steps, String expectedResults , String actualResult ,String version ){
    	
	    return "Test Case : ["+version+"]-" + summary + "\n\n" + "Steps : " + "\n" + steps.replace("\n", " ") + "\n\n" + 
	    "Expected Results : " + "\n" + expectedResults.replace("\n", " ") + "\n\n" +"Actual Result : " +"\n" + actualResult; 
	    
    }
    
    private static String getInputStep(String inputStep, String index){
    	String[] verifys = inputStep.split("\\n");
    	for (int i = 0; i < verifys.length; i++) {
			String value = verifys[i].trim();
    		if(Integer.parseInt(value.substring(0, 1)) == Integer.parseInt(index)){
    			return value;
    		}
		}
    	
    	return "";
    }

    *//**
     * 删除评级的信息.
     * 
     * 原因： 写入数据方式是与class 同步,当我们执行一个class 时候需要将信息写入到excel
     * 第二次写入的时候需要将已有的数据保存然后在相加当前class 信息,同样评级数据也是一样，需要每次在set值之前将它清空.
     * 
     * 小于5, 评级最高级别是5星, getImage(1)除了 android 图片外就是第一个星星的图片.
     * 直到出现IndexOutOfBoundsException表示评级里面的图片别删除完.
     *//*
    private static void deleteImage() {
        // open summary sheet.
        WritableImage image = null;
        sheet = ExcelUtils.getSheet(excel, "Summary");
        for (int i = 0; i < 5; i++) {
            try {
                image = sheet.getImage(1);
                sheet.removeImage(image);
            } catch (Exception e) {
                break;
            }
        }
    }

    *//**
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
     *//*
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

    *//**
     * 添加评级
     * 
     * @param pass
     * @param tests
     *//*
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

    *//**
     * 添加折叠
     * 
     * @param sheetName
     * @param excelPath
     * @param rows
     *//*
    public static void setGroupts(String sheetName, String excelPath, List<String> rows) {

        for (int i = 0; i < rows.size(); i++) {
            int beginRow = Integer.parseInt(rows.get(i).split("=")[0]);
            int endRow = Integer.parseInt(rows.get(i).split("=")[1]);
            if (i == 0) {
                // 默認展開第一個class 信息
                usPOISetGroups(sheetName, excelPath, beginRow, endRow, false);
            } else
                usPOISetGroups(sheetName, excelPath, beginRow, endRow, true);
        }
    }

    *//**
     * 
     * @param sheetName
     * @param excelPath
     * @param beginRow
     * @param endRow
     * @param isCollapsed
     *//*
    public static void usPOISetGroups(String sheetName, String excelPath, int beginRow, int endRow, boolean isCollapsed) {
        HSSFSheet sheet = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(excelPath));
            HSSFWorkbook wb = new HSSFWorkbook(is);
            sheet = wb.getSheet(sheetName);
            sheet.groupRow(beginRow, endRow);

            sheet.setRowGroupCollapsed(beginRow + 1, isCollapsed);
            sheet.setRowSumsBelow(false);
            sheet.setRowSumsRight(true);

            FileOutputStream writeFile = new FileOutputStream(excelPath);
            wb.write(writeFile);
            writeFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    *//**
     * 添加空行并合并.
     * 
     * @return
     *//*
    public static int addEmptyMegerRow(String details) {
        int end = sheet.getRows();
        ExcelUtils.mergerCell(0, end, 6, end);
        // setCellSize(0, end,5, 250);
        ExcelUtils.writeData(0, end, details, ExcelUtils.addStyle(10, false, false, false, "white"));
        return end;

    }

    *//**
     * 添加每个Class 的统计行.
     * 
     * @return
     *//*
    public static int addEmptyRow() {
        int end = ExcelUtils.getRow();
        ExcelUtils.setCellSize(0, end, 10, 300);
        ExcelUtils.writeData(0, end, null, ExcelUtils.addStyle(10, false, true, false, "grey80", textColor));
        return end;
    }

    *//**
     * 添加 class summary
     * 
     * @param className
     * @param countMethod
     * @param BFB
     * @param countTime
     * @param setRows
     *//*
    private static void setClassInfoToExcel(String className, int countMethod, String BFB, String countTime, int setRows) {
        // 添加log 連接
        setHyperLinkForSheet(0, setRows, className);
        ExcelUtils.writeData(1, setRows, countMethod, ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));
        ExcelUtils.writeData(2, setRows, BFB, ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));
        ExcelUtils.writeData(3, setRows, "NULL", ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));
        ExcelUtils.writeData(4, setRows, "NULL", ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));
        ExcelUtils.writeData(5, setRows, countTime, ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));
        ExcelUtils.writeData(6, setRows, "NULL", ExcelUtils.addStyle(10, false, true, false, "grey80", "white"));

    }

    *//**
     * 超链接 跳转Log.log
     *
     * @param col
     * @param row
     * @param desc
     * @param sheetName
     * @param destCol
     * @param destRow
     *//*
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

    *//**
     * 获取图片路径.
     * 
     * @param imageName
     * @param className
     * @param appType
     * @return
     *//*
    private static String getImagePath(String imageName, String className, String appType) {
        String returnVal = "";
        if (appType.equals("InstrumentDriver")) {
            String name = className.contains(".") ? className.split("\\.")[1] : className;
            String oldPath = CommonTools.getTestRoot().replace("test-classes/", "") + "InstrumentDriver/log/" + name
                    + "/Run 1/" + imageName + ".png";
            returnVal = CommonTools.getTestRoot().replace("target/test-classes/", "")
                    + "custom_report/screenCaptures/InstrumentDriverIOS/" + imageName + ".png";
            // 复制图片到 allScreenshot文件加下面.
            ImageUtils.copyImage(oldPath, returnVal);

        }
        return "screenCaptures/" + imageName + ".png";

    }

    *//**
     * 添加图片超链接
     * 
     * @param col
     * @param row
     * @param imagePath
     *//*
    public static void setLinkToErrorScrenshot(int col, int row, String imagePath) {

        try {
            String image = "../" + imagePath;
            if (imagePath != null) {

                String formu = "HYPERLINK(\"" + image + "\", \"" + imagePath.split("/")[imagePath.split("/").length - 1]
                        + "\")";
                Formula formula = new Formula(col, row, formu, ExcelUtils.addStyleLink(10, false, false, false, "white"));
                sheet.addCell(formula);

            } else {
                sheet.addCell(new Label(col, row, "", ExcelUtils.addStyleLink(10, false, false, false, "white")));
            }
        } catch (WriteException e) {

            e.printStackTrace();
        }

    }

    *//**
     * 获取时间
     * 
     * @param cel
     * @param row
     * @return
     *//*
    public static float getTimeValueFloat(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents().replace("m", "");
        if (value.isEmpty()) {
            return 0;
        } else {
            return Float.parseFloat(value);
        }

    }

    *//**
     * 获取 test set 总数
     * 
     * @param cel
     * @param row
     * @return
     *//*
    public static int getTestsValue(int cel, int row) {

        String value = sheet.getCell(cel, row).getContents().toString();
        if (value.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(value);
        }

    }

}
*/