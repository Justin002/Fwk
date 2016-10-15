package com.by.automate.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Justin 获取系统的唯一标识
 */
public class PhoneInfo {

    public static String getPhoneInfo(String adb) {

        Process process;
        try {
            process = Runtime.getRuntime().exec(adb);
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            return br.readLine();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "";

    }

    /**
     * 獲取設備電量
     * 
     * @return
     * @throws IOException
     */
    public static double battery() throws IOException {
        double batt = 0;
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("adb2 shell dumpsys battery");
        String str3;
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line + " ");

            }
            String str1 = stringBuffer.toString();
            String str2 = str1.substring(str1.indexOf("level"), str1.indexOf("level") + 10);
            str3 = str2.substring(6, 10);
            str3.trim();
            batt = Double.parseDouble(str3);
            System.out.println("batt : " + batt);
        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
        System.out.println("batt : " + batt);
        return batt;

    }

    public static String getDeviceName() {
        return getPhoneInfo("adb shell getprop ro.product.model");
    }

    public static String getDeviceVersion() {
        return getPhoneInfo("adb shell getprop ro.build.version.release");
    }
    
 

}
