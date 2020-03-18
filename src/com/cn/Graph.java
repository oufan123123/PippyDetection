package com.cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * ע����չ�ϵ��һ��apk��Ӧһ��ͼ��һ��apk�µĶ����Ҳֻ�ǹ���һ��ͼ
 * 
 * ͼ������ͼ
 * 
 * ���嵽apkҵ������Ľڵ���������������
 * 
 * @author Administrator
 * 
 * һ���ģ�һ��ͼֻ����һ���������ʹ���
 *
 */

public class Graph<T> {
	
	private T data;
	private Map<String, GraphNode<T>> map;
	
	// ���������ݣ�����ά�����ù�
	private int num;
	
	// ע�������ӳ���ϵ��String����apkĳ������������
	volatile private Map<String, GraphNode<PackageOrClass>> packageMap;
	
	// Ĭ���̳߳أ������ԣ�
	private static ExecutorService es = Executors.newFixedThreadPool(6);
	
	
	
	// �����ĳ�ʼ��
	public Graph(T data) {
		this.data = data;
		map = new HashMap<>();
	}
	
	// ��һ��apk�ļ��ĳ�ʼ��
	public Graph(List<Tree<PackageOrClass>> treeList) {
		packageMap = new HashMap<>();
		buildGraphFromTree(treeList);
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Map<String, GraphNode<T>> getMap() {
		return map;
	}

	public void setMap(Map<String, GraphNode<T>> map) {
		this.map = map;
	}

	public Map<String, GraphNode<PackageOrClass>> getPackageMap() {
		return packageMap;
	}

	public void setPackageMap(Map<String, GraphNode<PackageOrClass>> packageMap) {
		this.packageMap = packageMap;
	}
	
	
	
	
	
	/************************apk�ı��������ͼdd*************************************/
	


	/**
	 * 
	 * �ɹ��캯�����ã����һ��ͼ
	 * 
	 * ����һ��apk��������
	 * 
	 * ����һ��ֻ�а���Ϊ�ڵ���ɵ�ͼ
	 * 
	 * @param treeList
	 */
	public void buildGraphFromTree(List<Tree<PackageOrClass>> treeList) {
		if (treeList == null || treeList.size() == 0) {
			return;
		}
		for (int i=0;i<treeList.size();i++) {
			TreeNode<PackageOrClass> treeNode = treeList.get(i).getRoot();
			// ���ѽ�ͼ
			dfs(treeNode);
			
		}
	}
	
	/**
	 * ���ѽ�ͼ
	 * 
	 * @param treeNode
	 */
	
	public void dfs(TreeNode<PackageOrClass> treeNode) {
		if (treeNode == null) {
			return;
		}
		PackageOrClass porc = treeNode.getData();
		if (porc.isClass()) {
			return;
		}
		// �����ӽڵ㣬Ȼ�����ͼ
		GraphNode<PackageOrClass> graphNode = new GraphNode<>(porc);
		packageMap.put(porc.getName(), graphNode);
		
		List<TreeNode<PackageOrClass>> list = treeNode.getChildNodeList();
		for (int i=0;i<list.size();i++) {
			dfs(list.get(i));
		}
	}
	
/************************��ÿ��smali�ļ�����һ���߳�������*******************************/
	
	
	/**
	 * 
	 * �������apk�����������Ѿ������õ�������
	 * 
	 * �����߳��������������޸�ͼ
	 * 
	 * @param treeList
	 * @param classList
	 * @throws InterruptedException 
	 */
	
	public void analyseAllSmaliFile(List<Tree<PackageOrClass>> treeList, List<String> classList, Graph<PackageOrClass> graph) throws InterruptedException {
		this.num = 0;
		if (treeList == null || treeList.size() == 0) {
			return;
		}
		System.out.println(treeList.size());
		for (int i=0;i<treeList.size();i++) {
			TreeNode<PackageOrClass> treeNode = treeList.get(i).getRoot();
			// ����
			analyseSmaliFile(treeNode, classList, graph);
			
		}
		
		es.shutdown();
		
		// ע�⵱timeout��������Ϊ1��Сʱ������samli�ļ�����ʱ��������ʱ���ǵ��޸�
		es.awaitTermination(2, TimeUnit.HOURS);
		
		System.out.println("Thread:"+Thread.currentThread().getName());
		
		System.out.println("num:"+num);
		GraphNode<PackageOrClass> node = packageMap.get("Landroidx/core/graphics/drawable");
		Map<GraphNode<PackageOrClass>, Integer> map = node.getMap();
		for (Map.Entry<GraphNode<PackageOrClass>, Integer> entry:map.entrySet()) {
			System.out.println("invokeClass:"+entry.getKey().getData().getName());
			System.out.println("number:"+entry.getValue());
		}
	}
	
	/**
	 *  �����ҵ�������ĸ��࣬�������̣߳����д���
	 * 
	 * @param treeNode
	 * @param classList
	 */
	
	public void analyseSmaliFile(TreeNode<PackageOrClass> treeNode, List<String> classList, Graph<PackageOrClass> graph) {
		if (treeNode == null) {
			return;
		}
		PackageOrClass porc = treeNode.getData();
		List<TreeNode<PackageOrClass>> list = treeNode.getChildNodeList();
		for (int i=0;i<list.size();i++) {
			// ����,�����߳�������
			if (list.get(i).getData().isClass()) {
				es.execute(new AnalyseThread(treeNode, list.get(i), classList, graph));
				num++;
				continue;
			}
			analyseSmaliFile(list.get(i), classList, graph);
		}
	}
	
/************************���ÿ��smali�ļ�����һ���߳��������Աȣ������ǵ��̲߳���*******************************/
	

	/**
	 * 
	 * �������apk�����������Ѿ������õ�������
	 * 
	 * �����߳��������������޸�ͼ
	 * 
	 * @param treeList
	 * @param classList
	 */
	
	public void analyseAllSmaliFileSingle(List<Tree<PackageOrClass>> treeList, List<String> classList, Graph<PackageOrClass> graph) {
		if (treeList == null || treeList.size() == 0) {
			return;
		}
		for (int i=0;i<treeList.size();i++) {
			TreeNode<PackageOrClass> treeNode = treeList.get(i).getRoot();
			// ���ѽ�ͼ
			analyseSmaliFileSingle(treeNode, classList, graph);
			
		}
		GraphNode<PackageOrClass> node = packageMap.get("Landroidx/core/graphics/drawable");
		Map<GraphNode<PackageOrClass>, Integer> map = node.getMap();
		for (Map.Entry<GraphNode<PackageOrClass>, Integer> entry:map.entrySet()) {
			System.out.println("invokeClass:"+entry.getKey().getData().getName());
			System.out.println("number:"+entry.getValue());
		}
	}
	
	/**
	 *  �����ҵ�������ĸ��࣬�������̣߳����д���
	 * 
	 * @param treeNode
	 * @param classList
	 */
	
	public void analyseSmaliFileSingle(TreeNode<PackageOrClass> treeNode, List<String> classList, Graph<PackageOrClass> graph) {
		if (treeNode == null) {
			return;
		}
		PackageOrClass porc = treeNode.getData();
		
		
		List<TreeNode<PackageOrClass>> list = treeNode.getChildNodeList();
		for (int i=0;i<list.size();i++) {
			// ����,�����߳�������
			if (list.get(i).getData().isClass()) {
				AnalyseThread at = new AnalyseThread(treeNode, list.get(i), classList, graph);
				at.runSingle();
			}
			analyseSmaliFileSingle(list.get(i),classList, graph);
		}
	}
	
	/**
	 * 
	 * ���������ڵİ����������õİ���ֵ���ý�ȥ,�������̵߳���
	 * 
	 */
	
	synchronized public void setInvokePackage(PackageOrClass fatherPackage, Map<String, Integer> mapEdgeWeight) {
		
		// �ҵ���ĸ���
		GraphNode<PackageOrClass> graphNode = packageMap.get(fatherPackage.getName());
		
		// �ҵ�����������ӳ��map
		Map<GraphNode<PackageOrClass>, Integer> mapInNode = graphNode.getMap();
		
		for (Map.Entry<String, Integer> entry:mapEdgeWeight.entrySet()) {
			GraphNode<PackageOrClass> invokePackage = packageMap.get(entry.getKey());
			if (mapInNode.get(invokePackage) != null) {
				int x = mapInNode.get(invokePackage) + entry.getValue();
				mapInNode.put(invokePackage, x);
			} else {
				mapInNode.put(invokePackage, entry.getValue());
			}
		}
	}
	
	

}
