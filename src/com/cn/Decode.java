package com.cn;


import brut.androlib.*;
import brut.directory.DirectoryException;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

/**
 * 反编译apk到文件 D：/DecodeApk/...
 * @author Administrator
 *
 */

public class Decode {
  
	public static String decodeOneApk(String path) throws DirectoryException, IOException{
		ApkDecoder apkDecoder=new ApkDecoder();
		String [] s=StringUtils.split(path,"\\\\|\\.");
		File file=new File(path);
	    String testPath="D:/DecodeApk/"+s[s.length-2];
		try {
			apkDecoder.setOutDir(new File(testPath));
		} catch (AndrolibException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		apkDecoder.setApkFile(file);
		try {
			apkDecoder.decode();
		} catch (AndrolibException e) {
			
			e.printStackTrace();
			
		}
		return testPath;
	}
	
}
	
