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
 * �����������Ȼ��ɸѡ���أ���Ҫ����Ϊ��
 * 
 * ��1������action.main�������ڰ����ڴ�Ϊ��ѡ��ģ�飻��2���������activity�������ڰ�Ϊ��ѡ��ģ�飻��3�����߶����������appname
 * 
 * @author Administrator
 *
 */
public class PrimarySelection {
	// ����
	private List<Cluster> clusters;
	
	// ��һ�ֺ�ѡ
	private String typeOneCandidate;
	
	// �ڶ��ֺ�ѡ
	private String typeTwoCandidate;
	
	// ���� (2) ��ѡ��map
	private Map<String, Integer> candidate;

	// �洢manifest��package
	String manifestString;
	
	// �洢app_name,ֻ��һ��
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
	 * ��ȡ�ļ������ҵõ���ģ��
	 * 
	 * @param animfestPath
	 * @return
	 */
	
	public Cluster getMainModule(String animfestPath) {
		Cluster mainCluster = null;
		
		try {
			File file = new File(animfestPath);
			if (!file.exists()) {
				System.out.println("animfest�ļ������ڴ���");
			}
			String encoding="UTF-8";
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String line = null;
			List<String> lines = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			
			// �����ҵ�������ѡ��
			analyseLines(lines);
			
			// �ж��Ƿ���һ��cluster��
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
			
			// ����������������
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
	 * �ҵ��ַ������ڵĴ�
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
	 * �ҵ�������ѡ��ģ���еİ�
	 * 
	 * @param line
	 */
	public void analyseLines(List<String> lines) {
		
		if (lines == null || lines.size() == 0) {
			return;
		}
		
		// һ�����ڱ�ʾ����ڶ���activaty�ı�־
		boolean isTypeTwo = false;
		
		// �ڶ���activity�洢
		String activityString = null;
		
		// �ȷ���ÿ��
		for (int i=0;i<lines.size();i++) {
			String line  = lines.get(i);
			// ���ַ��򷵻�
			if (line.equals("")) {
				continue;
			}
			
			// �洢manifest��package
			if (line.contains("<manifest ")) { 
				manifestString = getPackage(line, "package=\"", 1);
				if (manifestString != null) {
					System.out.println("manifestString:" + manifestString);
				}
				continue;
			}
			
			// �洢app_name
			if (line.contains("<application ")) {
				app_name = getPackage(line, "android:name=\"", 1);
				if (app_name != null) {
					//System.out.println("app_name:" + app_name);
				}
				continue;
			}
			
			// ����activaty
			if (line.contains("<activity ")) {
				activityString = getPackage(line, "android:name=\"", 0);	
				if (activityString != null) {
					//System.out.println("activityString:" + activityString);
				}
				isTypeTwo = handleActivity(line);
				// ��һ����ͨactivityֱ�Ӽ���list����Ϊ��ѡ
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
			
			// ����activatyβ��
			if (line.contains("</activity>")) {
				// ��ʼ����ڶ���activity�е���ͨ���
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
			
			// ����activity�е�action
			if (line.contains("<action ") && isTypeTwo) {
				// �ڶ���activity�е���Ϊ��ѡ��ģ��İ�
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
	 * ��������activity
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
	 * ����Ӧ��package����
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
					// ȥ����
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
	 * ���������ַ��������ƶȣ��ԡ�\���ָ����ַ���������ͬ����Ϊ������׼�����Դ˷������ƶ�
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
	 *  ����cluster�����ṹ
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
