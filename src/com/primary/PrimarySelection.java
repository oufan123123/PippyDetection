package com.primary;


import com.am.Cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * 
 * 输入聚类结果，然后筛选主簇，主要依据为，
 * 
 * （1）调用action.main的类所在包所在簇为候选主模块；（2）调用最多activity的类所在包为候选主模块；（3）两者都有择最近似appname
 * 
 * @author Administrator
 *
 */
public class PrimarySelection {
	// 聚类
	private List<Cluster> clusters;
	
	// 第一种候选
	private String typeOneCandidate;
	
	// 第二种候选
	private String typeTwoCandidate;
	
	// 用于 (2) 候选的map
	private Map<String, Integer> candidate;

	// 存储manifest的package
	String manifestString;
	
	// 存储app_name,只存一次
	String app_name;
	
	public PrimarySelection(List<Cluster> clusters) {
		this.clusters = clusters;
		candidate = new HashMap<>();
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}
	public Map<String, Integer> getCandidate() {
		return candidate;
	}

	public void setCandidate(Map<String, Integer> candidate) {
		this.candidate = candidate;
	}


	/**
	 * 
	 * 读取文件，并且得到主模块
	 * 
	 * @param animfestPath
	 * @return
	 */
	
	public Cluster getMainModule(String animfestPath) {
		Cluster mainCluster = null;
		
		try {
			File file = new File(animfestPath);
			if (!file.exists()) {
				System.out.println("animfest文件不存在错误");
			}
			String encoding="UTF-8";
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String line = null;
			List<String> lines = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			
			// 分析找到两个候选者
			analyseLines(lines);
			
			// 判断是否在一个cluster中
			System.out.println(typeOneCandidate);
			System.out.println(typeTwoCandidate);
			Cluster clusterOne = findByString(typeOneCandidate);
			Cluster clusterTwo = findByString(typeTwoCandidate);
			if (clusterOne == null && clusterTwo == null) {
				System.out.println("error, nocluster can be select");
				return null;
			}
			if (clusterOne == null) {
				return clusterTwo;
			}
			if (clusterTwo == null) {
				return clusterOne;
			}
			
			if (clusterOne.equals(clusterTwo)) {
				return clusterOne;
			}
			
			// 如果不在找最相近的
			if (similar(typeOneCandidate, app_name) < similar(typeTwoCandidate, app_name)) {
				return clusterOne;
			} else {
				return clusterTwo;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;	
	}
	
	
	/**
	 * 
	 * 找到字符串所在的簇
	 * 
	 */
	public Cluster findByString(String name) {
		if (clusters == null || clusters.size() == 0) {
			return null;
		}
		for (int i=0;i<clusters.size();i++) {
			if (isInCluster(clusters.get(i), name)) {
				return clusters.get(i);
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * 找到两个候选主模块中的包
	 * 
	 * @param line
	 */
	public void analyseLines(List<String> lines) {
		
		if (lines == null || lines.size() == 0) {
			return;
		}
		
		// 一个用于表示进入第二种activaty的标志
		boolean isTypeTwo = false;
		
		// 第二种activity存储
		String activityString = null;
		
		// 先分析每行
		for (int i=0;i<lines.size();i++) {
			String line  = lines.get(i);
			// 空字符则返回
			if (line.equals("")) {
				continue;
			}
			
			// 存储manifest的package
			if (line.contains("<manifest ")) { 
				manifestString = getPackage(line, "package=\"", 1);
				if (manifestString != null) {
					System.out.println("manifestString:" + manifestString);
				}
				continue;
			}
			
			// 存储app_name
			if (line.contains("<application ")) {
				app_name = getPackage(line, "android:name=\"", 1);
				if (app_name != null) {
					//System.out.println("app_name:" + app_name);
				}
				continue;
			}
			
			// 处理activaty
			if (line.contains("<activity ")) {
				activityString = getPackage(line, "android:name=\"", 0);	
				if (activityString != null) {
					//System.out.println("activityString:" + activityString);
				}
				isTypeTwo = handleActivity(line);
				// 第一种普通activity直接加入list中作为候选
				if (!isTypeTwo) {
					//System.out.println("two:" + activityString);
					Integer value = candidate.get(activityString);
					if (value != null) {
						candidate.put(activityString, value+1);
					} else {
						candidate.put(activityString, 1);
					}
				}
				continue;
			}
			
			// 处理activaty尾巴
			if (line.contains("</activity>")) {
				// 开始处理第二种activity中的普通情况
				if (isTypeTwo) {
					//System.out.println("one:" + activityString);
					Integer value = candidate.get(activityString);
					if (value != null) {
						candidate.put(activityString, value+1);
					} else {
						candidate.put(activityString, 1);
					}
				}
				isTypeTwo = false;
				continue;
			}
			
			// 处理activity中的action
			if (line.contains("<action ") && isTypeTwo) {
				// 第二种activity中的作为候选主模块的包
				if (line.contains("action.MAIN")) {
					System.out.println("candidateOne:" + activityString);
					typeOneCandidate = activityString;
					isTypeTwo = false;
				}
				continue;
			}
			
			
		}
		
		if (candidate == null || candidate.size() == 0) {
			return;
		}
		
		int maxNum = 0;
		String maxString = null;
		for (Map.Entry<String, Integer> entry:candidate.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			if (entry.getValue() > maxNum) {
				
				maxNum = entry.getValue();
				maxString = entry.getKey();
			}
		}
		typeTwoCandidate = maxString;
		System.out.println("candidateTwo:" + maxString);
	}
	
	/**
	 * 
	 * 处理两种activity
	 * 
	 */
	public boolean handleActivity(String line) {
		if (line.contains("/>")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * 将对应的package返回
	 * 
	 */
	public String getPackage(String line, String containString, int type) {
		if (line == null || containString == null) {
			return null;
		}
		String[] s1 = StringUtils.split(line, " ");
		if (s1 != null && s1.length != 0) {
			for (int i=0;i<s1.length;i++) {
				if (s1[i].contains(containString)) {
					String[] s2 = StringUtils.split(s1[i], "\"|/|>| ");
					if (s2  == null || s2.length == 0) {
						return null;
					}
					String replaceString = s2[s2.length -1].replace('.', '/');
					if (replaceString.indexOf("/") == 0 && manifestString != null) {
						replaceString = manifestString + "/" + replaceString.substring(1); 
					}
					if (!replaceString.contains("/") && manifestString != null) {
						replaceString = manifestString + "/" + replaceString; 
					}
					// 去掉类
					if (type == 0) {
						int index = replaceString.lastIndexOf("/");
						replaceString = replaceString.substring(0,index);
					}
					if (replaceString.charAt(0) != 'L') {
						replaceString = "L"+replaceString;
					}
					return replaceString;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * 计算两个字符串的相似度，以‘\’分割后的字符串连续相同个数为度量标准，并以此返回相似度
	 * 
	 */
	public int similar(String s1, String s2) {
		if (s1 == null || s2 == null) {
			return 0;
		}
		String[] splitS1 = StringUtils.split(s1, "/");
		
		String[] splitS2 = StringUtils.split(s2, "/");
		List<String> s2List = new ArrayList<>();
		
		int maxSimilar = 0;
		String last = "";
		for (int i=0;i<splitS1.length;i++) {
			StringBuilder sb = new StringBuilder();
			last = last + splitS1[i];
			System.out.println(last);
			if (s2.contains(last) && i + 1 > maxSimilar) {
				maxSimilar = i + 1;
			}
			sb.append(last);
			sb.append('/');
			last = sb.toString();
		}
		last = "";
		for (int i=0;i<splitS2.length;i++) {
			StringBuilder sb = new StringBuilder();
			last = last + splitS2[i];
			System.out.println(last);
			if (s1.contains(last) && i + 1 > maxSimilar) {
				maxSimilar = i + 1;
			}
			sb.append(last);
			sb.append('/');
			last = sb.toString();
		}
		return maxSimilar;
	}
	
	/**
	 * 
	 *  遍历cluster的树结构
	 * 
	 */
	public boolean isInCluster(Cluster cluster, String childString) {
		if (cluster == null) {
			return false;
		}
		if (cluster.getPackageName().equals(childString)) {
			return true;
		}
		if (cluster.getChildList() == null || cluster.getChildList().size() == 0) {
			return false;
		}
		
		for (int i=0;i<cluster.getChildList().size();i++) {
			boolean res = isInCluster(cluster.getChildList().get(i), childString);
			if (res) {
				return true;
			}
		}
		return false;
	}
	
	
	

}
