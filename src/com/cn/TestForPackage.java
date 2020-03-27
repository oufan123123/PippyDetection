package com.cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 仅仅测试用，看一个包下的所有类分析完毕后能找到多少与其相关的包
 * 
 * @author Administrator
 *
 */

public class TestForPackage {
	
	// 注意这里一定传入类，
		private PackageOrClass fatherPackage;
		
		// 映射到对应字符串的值,最后一起清算
		private Map<String, Integer> mapEdgeWeight;
		
		// 映射路径到类的map
		private Map<String, TreeNode<PackageOrClass>> mapToClass;
		
		// 传进来一个映射,专门存储所有的叶子节点，即所在的类，证明这个类调用的另外一个类存在
		private List<String> classList;

	public TestForPackage(PackageOrClass fatherPackage, List<String> classList, Map<String, TreeNode<PackageOrClass>> mapToClass) {
		this.fatherPackage  = fatherPackage;
		this.classList = classList;
		mapEdgeWeight = new HashMap<>();
		this.mapToClass = mapToClass;
		
		File file = new File(fatherPackage.getPath());
		File[] files = file.listFiles();
		for (int i=0;i<files.length;i++) {
			//System.out.println(files[i].getAbsolutePath());
			analyseFile(files[i].getAbsolutePath(), mapToClass.get(files[i].getAbsolutePath()).getData());
			
		}
		
		if (mapEdgeWeight == null || mapEdgeWeight.size() == 0) {
			System.out.println("这个包不调用其他包");
		}
		for (Map.Entry<String, Integer> entry:mapEdgeWeight.entrySet()) {
			System.out.println("name:"+entry.getKey());
			System.out.println("value:"+entry.getValue());
		}
		
	}
	
/***************************这是线程调用用于分析类的操作******************************/
	
	/**
	 * 分析smali文件
	 * 
	 * @param path
	 */
	public void analyseFile(String path, PackageOrClass childClass) {
			String encoding="UTF-8";
			File file=new File(path);
			BufferedReader reader=null;
			List<String> line=new ArrayList();
			int i=0;
			try {
				 if(!file.exists()||!file.isFile()) {
					 System.out.println(path+":文件不存在");
				 }
				 else {
				 // InputStreamReader read=new InputStreamReader(new FileInputStream(file),encoding);
				 reader=new BufferedReader(new FileReader(path));
				 String lineRead=null; 
				 while((lineRead=reader.readLine())!=null) {
					  analyseLine(lineRead, childClass);
					  i++;
				 }
				 reader.close();
				  
				}
			 } catch(IOException e) {
				 System.out.println("文件读取错误");
				 e.printStackTrace();
			 }
	}
	
	/**
	 * 
	 * 分析每一行smali代码
	 * 
	 * @param line
	 */
	
	public void analyseLine (String line, PackageOrClass childClass) {
		// 空行
		if (line.equals("")) {
			return;
		}
		

		// 处理方法调用，判定条件：（1）'invoke-virtual','invoke-super','invoke-direct','invoke-static','invoke-interface'
		String invokeMethod = null;
		if ((invokeMethod = isInvokeMethod(line)) != null) {
			//System.out.println("invokeMethod:" + invokeMethod);
			// 。。。。处理函数
			handleInvokeClass(invokeMethod, 2, childClass);
			return;
		}
		
		// 处理父类继承，判定条件：（1）'.super'关键字，（2）关键字位置为0
		String father = null;
		if ((father = isFather(line)) != null) {
			//System.out.println("father:" + father);
			// 。。。。。处理函数
			handleInvokeClass(father, 10, childClass);
			return;
		}
		
		
		// 处理字段调用，判断条件：（1）aget；aput；iget；iput；sget；sput（2）在头部
		String fieldString = null;
		if ((fieldString = isFieldString(line)) != null) {
			//System.out.println("fieldString:" + fieldString);
			// 。。。处理函数
			handleInvokeClass(fieldString, 1, childClass);
		}
		
	}
	
/***********************************粗判断是否是哪三种调用类型之一***********************************/
	
	/**
	 * 输入行，判断是否是调用字段，并返回调用的函数名字,并未返回调用的根包，还需继续处理
	 * 
	 * 为了不影响对其他情况的判断时间，我们只先看必要条件！！！！！！！！
	 * 
	 * @param line
	 * @return
	 */
	public String isInvokeMethod(String line) {
		if (line.contains("invoke-virtual") || line.contains("invoke-super")
				|| line.contains("invoke-direct") || line.contains("invoke-static")
				|| line.contains("invoke-interface")) {
			
			// 第一次分割把调用API找到
			String[] s1 = StringUtils.split(line, " ");
			if (s1.length < 2) {
				return null;
			}
			
			// 第二次分割把API分割的头部找到，即是调用的类，
			String[] s2 = StringUtils.split(s1[s1.length-1], ";");
			return s2[0];
		}
		return null;
	}
	
	/**
	 * 输入行，判断是否继承其他的类，还需继续处理,并未返回调用的根包，还需继续处理
	 * 
	 * 为了不影响对其他情况的判断时间，我们只先看必要条件！！！！！！！！
	 * 
	 * @param line
	 * @return
	 */
	
	public String isFather(String line) {
		
		if (line.indexOf(".super") == 0) {
			// 基本类就不考虑了
			if (line.contains("Ljava/lang/Object")) {
				return null;
			} 
			
			String[] s = StringUtils.split(line, " |;");
			if (s.length < 2) {
				return null;
			}
			return s[1];
		}
		return  null;
	}
	
	/**
	 * 输入行，判断是否为字段调用，并未返回调用的根包，还需继续处理
	 * 
	 * 为了不影响对其他情况的判断时间，我们只先看必要条件！！！！！！！！
	 * 
	 * 注意每行含有两个类，需要提取的是第一个类 例如sput-object v0, Lcom/google/ads/AdActivity;->b:Ljava/lang/Object;提取Lcom/google/ads/AdActivity
	 * 
	 * 
	 * @param line
	 * @return
	 */
	
	public String isFieldString(String line) {
		if (line.contains("aget") || line.contains("aput")
				|| line.contains("iget") || line.contains("iput")
				|| line.contains("sget") || line.contains("sput")) {
			// 第一次将一行按照空行和逗号分
			String[] s1 = StringUtils.split(line, " |,");
			//System.out.println(s1[0]);
			if (s1.length < 2) {
				return null;
			}
			
			
			// 第二次将第一次得到的结果再分
			if (s1[0].contains("aget") || s1[0].contains("aput")
					|| s1[0].contains("iget") || s1[0].contains("iput")
					|| s1[0].contains("sget") || s1[0].contains("sput")) {
				
				String[] s2 = StringUtils.split(s1[s1.length-1], ";");
				return s2[0];
			}	
		}
		return null;
	}
	
/***********************************细判断被调用类是否存在，然后将对应的值加到map中去***********************************/
	
	/**
	 * 
	 * 输入调用的类名和找到类名后的增加值
	 * 
	 * 
	 * @param invokeMethod
	 * @param addedNum
	 */
	
	public void handleInvokeClass(String invokeClass, int addedNum, PackageOrClass childClass) {
		
		// 第1步，先看是否是自身的类
		if (invokeClass.equals(childClass.getName())) {
			return;
		}
		// 第2步，看是否调用的类存在
		if (!classList.contains(invokeClass)) {
			return;
		}
					
		// 第3步，若类存在，则将其所在父类的值加上2
		int lastIndex = invokeClass.lastIndexOf("/");
		
		if (lastIndex < 0) {
			return;
		}
		String invokePackage = invokeClass.substring(0, lastIndex);
		
		if (invokePackage.equals(fatherPackage.getName())) {
			return;
		}
		System.out.println("class:"+childClass.getName());
		System.out.println("value:"+addedNum);
		System.out.println("invoke:"+invokeClass);
		if (mapEdgeWeight.get(invokePackage) != null) {
			int x = mapEdgeWeight.get(invokePackage);
			mapEdgeWeight.put(invokePackage, x + addedNum);
		} else {
			mapEdgeWeight.put(invokePackage, addedNum);
		}
	}
}
