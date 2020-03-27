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
 * 
 * ���ڶ�ȡ���ҷ���һ��smali�ļ������ݵĵ��߳�
 * 
 * �������в��ԣ�StringUtils�����̰߳�ȫ�����迼���ַ�����λ������
 * 
 * @author Administrator
 *
 */
public class AnalyseThread implements Runnable{
	
	// ע������һ�������࣬
	private PackageOrClass fatherPackage;
	private PackageOrClass childClass;
	
	// ӳ�䵽��Ӧ�ַ�����ֵ,���һ������
	private Map<String, Integer> mapEdgeWeight;
	
	// ����һ�����ӹ�ϵӳ�䣬��Ϊjavaֻ�ܵ��̳У�����һ��java file���һ������
	private String father;
	
	// ������һ��ӳ��,ר�Ŵ洢���е�Ҷ�ӽڵ㣬�����ڵ��֤࣬���������õ�����һ�������
	private List<String> classList;
	
	// ͼ
	Graph<PackageOrClass> graph;
	
	
	public AnalyseThread(TreeNode<PackageOrClass> fatherTreeNode, TreeNode<PackageOrClass> treeNode, List<String> classList, Graph<PackageOrClass> graph) {
		fatherPackage = fatherTreeNode.getData();
		childClass = treeNode.getData();
		this.classList = classList;
		mapEdgeWeight = new HashMap<>();
		this.graph = graph;
	}
	
	
	@Override
	public void run() {
		// System.out.println("thread:"+Thread.currentThread().getName());
		String className = childClass.getName();
		String path = childClass.getPath();
		
		// ��ʼ�����ļ�
		analyseFile(path);
		
		// ...............������ɣ���ʼ�޸�ͼ
		graph.setInvokePackage(fatherPackage, mapEdgeWeight, father);
		
	}
	
	
	
	
	
	/***************************�����̵߳������ڷ�����Ĳ���******************************/
	
	/**
	 * ����smali�ļ�
	 * 
	 * @param path
	 */
	public void analyseFile(String path) {
			String encoding="UTF-8";
			File file=new File(path);
			BufferedReader reader=null;
			List<String> line=new ArrayList();
			int i=0;
			try {
				 if(!file.exists()||!file.isFile()) {
					 System.out.println(path+":�ļ�������");
				 }
				 else {
				 // InputStreamReader read=new InputStreamReader(new FileInputStream(file),encoding);
				 reader=new BufferedReader(new FileReader(path));
				 String lineRead=null; 
				 while((lineRead=reader.readLine())!=null) {
					  analyseLine(lineRead);
					  i++;
				 }
				 reader.close();
				  
				}
			 } catch(IOException e) {
				 System.out.println("�ļ���ȡ����");
				 e.printStackTrace();
			 }
	}
	
	/**
	 * 
	 * ����ÿһ��smali����
	 * 
	 * @param line
	 */
	
	public void analyseLine (String line) {
		// ����
		if (line.equals("")) {
			return;
		}
		

		// ���������ã��ж���������1��'invoke-virtual','invoke-super','invoke-direct','invoke-static','invoke-interface'
		String invokeMethod = null;
		if ((invokeMethod = isInvokeMethod(line)) != null) {
			//System.out.println("invokeMethod:" + invokeMethod);
			// ��������������
			handleInvokeClass(invokeMethod, 2);
			return;
		}
		
		// ������̳У��ж���������1��'.super'�ؼ��֣���2���ؼ���λ��Ϊ0
		String father = null;
		if ((father = isFather(line)) != null) {
			//System.out.println("father:" + father);
			// ����������������
			handleInvokeClass(father, 10);
			return;
		}
		
		
		// �����ֶε��ã��ж���������1��aget��aput��iget��iput��sget��sput��2����ͷ��
		String fieldString = null;
		if ((fieldString = isFieldString(line)) != null) {
			//System.out.println("fieldString:" + fieldString);
			// ������������
			handleInvokeClass(fieldString, 1);
		}
		
	}
	
/***********************************���ж��Ƿ��������ֵ�������֮һ***********************************/
	
	/**
	 * �����У��ж��Ƿ��ǵ����ֶΣ������ص��õĺ�������,��δ���ص��õĸ����������������
	 * 
	 * Ϊ�˲�Ӱ�������������ж�ʱ�䣬����ֻ�ȿ���Ҫ��������������������
	 * 
	 * @param line
	 * @return
	 */
	public String isInvokeMethod(String line) {
		if (line.contains("invoke-virtual") || line.contains("invoke-super")
				|| line.contains("invoke-direct") || line.contains("invoke-static")
				|| line.contains("invoke-interface")) {
			
			// ��һ�ηָ�ѵ���API�ҵ�
			String[] s1 = StringUtils.split(line, " ");
			if (s1.length < 2) {
				return null;
			}
			
			// �ڶ��ηָ��API�ָ��ͷ���ҵ������ǵ��õ��࣬
			String[] s2 = StringUtils.split(s1[s1.length-1], ";");
			return s2[0];
		}
		return null;
	}
	
	/**
	 * �����У��ж��Ƿ�̳��������࣬�����������,��δ���ص��õĸ����������������
	 * 
	 * Ϊ�˲�Ӱ�������������ж�ʱ�䣬����ֻ�ȿ���Ҫ��������������������
	 * 
	 * @param line
	 * @return
	 */
	
	public String isFather(String line) {
		
		if (line.indexOf(".super") == 0) {
			// ������Ͳ�������
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
	 * �����У��ж��Ƿ�Ϊ�ֶε��ã���δ���ص��õĸ����������������
	 * 
	 * Ϊ�˲�Ӱ�������������ж�ʱ�䣬����ֻ�ȿ���Ҫ��������������������
	 * 
	 * ע��ÿ�к��������࣬��Ҫ��ȡ���ǵ�һ���� ����sput-object v0, Lcom/google/ads/AdActivity;->b:Ljava/lang/Object;��ȡLcom/google/ads/AdActivity
	 * 
	 * 
	 * @param line
	 * @return
	 */
	
	public String isFieldString(String line) {
		if (line.contains("aget") || line.contains("aput")
				|| line.contains("iget") || line.contains("iput")
				|| line.contains("sget") || line.contains("sput")) {
			// ��һ�ν�һ�а��տ��кͶ��ŷ�
			String[] s1 = StringUtils.split(line, " |,");
			//System.out.println(s1[0]);
			if (s1.length < 2) {
				return null;
			}
			
			
			// �ڶ��ν���һ�εõ��Ľ���ٷ�
			if (s1[0].contains("aget") || s1[0].contains("aput")
					|| s1[0].contains("iget") || s1[0].contains("iput")
					|| s1[0].contains("sget") || s1[0].contains("sput")) {
				
				String[] s2 = StringUtils.split(s1[s1.length-1], ";");
				return s2[0];
			}	
		}
		return null;
	}
	
/***********************************ϸ�жϱ��������Ƿ���ڣ�Ȼ�󽫶�Ӧ��ֵ�ӵ�map��ȥ***********************************/
	
	/**
	 * 
	 * ������õ��������ҵ������������ֵ
	 * 
	 * 
	 * @param invokeMethod
	 * @param addedNum
	 */
	
	public void handleInvokeClass(String invokeClass, int addedNum) {
		
		// ��1�����ȿ��Ƿ����������
		if (invokeClass.equals(childClass.getName())) {
			return;
		}
		// ��2�������Ƿ���õ������
		if (!classList.contains(invokeClass)) {
			return;
		}
					
		// ��3����������ڣ��������ڸ����ֵ����2
		int lastIndex = invokeClass.lastIndexOf("/");
		
		if (lastIndex < 0) {
			return;
		}
		String invokePackage = invokeClass.substring(0, lastIndex);
		// ��4���������ֵ��õİ����Լ�ʱ������
		if (invokePackage.equals(fatherPackage.getName())) {
			return;
		}
		// ���Ӹ��ӹ�ϵ
		if (addedNum == 10) {
			this.father = invokePackage;
			//System.out.println("class:"+ childClass.getName()+"; "+"father:"+invokePackage);
		}
		
		if (mapEdgeWeight.get(invokePackage) != null) {
			int x = mapEdgeWeight.get(invokePackage);
			mapEdgeWeight.put(invokePackage, x + addedNum);
		} else {
			mapEdgeWeight.put(invokePackage, addedNum);
		}
	}
	
/******************************���һ�����ҵ��ĵ�����������**************************************/
	
	

	
	
	
	
	
	// ���迴�ⲿ�ִ��룬ֻ�����ڵ��̲߳���
	/**
	 * ���̲߳�����
	 */
	
	public void runSingle() {
		String className = childClass.getName();
		//System.out.println("className:"+className);
		String path = childClass.getPath();
		
		// ��ʼ�����ļ�
		analyseFile(path);
		
		// ...............������ɣ���ʼ�޸�ͼ
		graph.setInvokePackage(fatherPackage, mapEdgeWeight, father);
		
	}
	
	
}
