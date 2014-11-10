package com.wantflying.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

public final class ShellUtil { 
	 
    /** �ڲ���ShellUtilHolder */ 
    static class ShellUtilHolder { 
        static ShellUtil instance = new ShellUtil(); 
    } 
 
    /** ����ShellUtil�ĵ��� */ 
    public static ShellUtil getInstance() { 
        return ShellUtilHolder.instance; 
    } 
 
    /** 
     * @brief ROOTȨ����ִ������ 
     * @pre ִ��\link #root()\endlink 
     *  
     * @param cmd ���� 
     * @param isOut �Ƿ�������
     * @param isSu �ǲ�����root����
     * @param dir ����ִ��Ŀ¼
     */
    public String runShell(String cmd,boolean isOut,boolean isSu,String dir) {
        System.out.println(cmd+"||"+isOut+"||"+isSu+"||"+dir);
    	StringBuffer retI = new StringBuffer("");
    	String status = "ok";
    	String ret = "";
    	ProcessBuilder pb = new ProcessBuilder("/system/bin/sh");
    	pb.redirectErrorStream(true);
    	pb.directory(new File(dir));
    	try {
    		Process proc = pb.start();
    		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
    		InputStream ins = proc.getInputStream();
    		if(isSu){
    	    	out.println("su");
    			if(out.checkError())
                    System.out.println("========WRONG===========");
    		}
    		out.println("cd "+dir);
    		out.println(cmd);
    		out.println("pwd");///////////////////////////////////////////������Ҫ�����£�����غ�·��������Ϊ��ͬ��������(����pwd����ֻ�ܷ���һ�����ݣ���ȡ��ʱ�򰴳��ȷָ���)
			out.println("exit");
			out.close();
			int retIl=0;
			int retItl=0;
	        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
	        String line;
	        while ((line = in.readLine()) != null) {
	        	retIl = retI.length();
	        	retI.append(line+"\n");
	        	retItl = retI.length();
	        }
	        in.close();
    		System.out.println(retIl);
    		if(retIl!=0)
    			ret = retI.substring(0,retIl-1);
    		dir = retI.substring(retIl,retItl-1);
    		proc.destroy();
    	} catch (Exception e) {
    		System.out.println("exception:" + e);
    		status = "fail";
    	}
        JSONObject object = new JSONObject();
        try {
			object.put("status", status);
    		if(isOut){
		        object.put("message", ret);
			}
	        object.put("directory", dir);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return object.toString();
	}
    
}
