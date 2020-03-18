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
 * 注意对照关系：一个apk对应一个图，一个apk下的多棵树也只是构成一个图
 * 
 * 图是无向图
 * 
 * 具体到apk业务，这里的节点代表包，不代表类
 * 
 * @author Administrator
 * 
 * 一样的，一个图只允许一种数据类型存在
 *
 */

public class Graph<T> {
	
	private T data;
	private Map<String, GraphNode<T>> map;
	
	// 测试用数据，后期维护不用管
	private int num;
	
	// 注意这里的映射关系中String代表apk某个根包的名字
	volatile private Map<String, GraphNode<PackageOrClass>> packageMap;
	
	// 默认线程池（先试试）
	private static ExecutorService es = Executors.newFixedThreadPool(6);
	
	
	
	// 基本的初始化
	public Graph(T data) {
		this.data = data;
		map = new HashMap<>();
	}
	
	// 对一个apk文件的初始化
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
	
	
	
	
	
	/************************apk的遍历构造包图dd*************************************/
	


	/**
	 * 
	 * 由构造函数调用，组成一个图
	 * 
	 * 输入一个apk的所有树
	 * 
	 * 构造一个只有包作为节点组成的图
	 * 
	 * @param treeList
	 */
	public void buildGraphFromTree(List<Tree<PackageOrClass>> treeList) {
		if (treeList == null || treeList.size() == 0) {
			return;
		}
		for (int i=0;i<treeList.size();i++) {
			TreeNode<PackageOrClass> treeNode = treeList.get(i).getRoot();
			// 深搜建图
			dfs(treeNode);
			
		}
	}
	
	/**
	 * 深搜建图
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
		// 构建子节点，然后组成图
		GraphNode<PackageOrClass> graphNode = new GraphNode<>(porc);
		packageMap.put(porc.getName(), graphNode);
		
		List<TreeNode<PackageOrClass>> list = treeNode.getChildNodeList();
		for (int i=0;i<list.size();i++) {
			dfs(list.get(i));
		}
	}
	
/************************对每个smali文件启动一个线程来分析*******************************/
	
	
	/**
	 * 
	 * 输入这个apk的所有树，已经分析得到的类名
	 * 
	 * 创建线程来分析，并且修改图
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
			// 深搜
			analyseSmaliFile(treeNode, classList, graph);
			
		}
		
		es.shutdown();
		
		// 注意当timeout这里设置为1个小时，当有samli文件分析时间大于这个时，记得修改
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
	 *  深搜找到类和他的父类，并传入线程，进行处理
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
			// 是类,创建线程来处理
			if (list.get(i).getData().isClass()) {
				es.execute(new AnalyseThread(treeNode, list.get(i), classList, graph));
				num++;
				continue;
			}
			analyseSmaliFile(list.get(i), classList, graph);
		}
	}
	
/************************与对每个smali文件启动一个线程来分析对比，这里是单线程操作*******************************/
	

	/**
	 * 
	 * 输入这个apk的所有树，已经分析得到的类名
	 * 
	 * 创建线程来分析，并且修改图
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
			// 深搜建图
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
	 *  深搜找到类和他的父类，并传入线程，进行处理
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
			// 是类,创建线程来处理
			if (list.get(i).getData().isClass()) {
				AnalyseThread at = new AnalyseThread(treeNode, list.get(i), classList, graph);
				at.runSingle();
			}
			analyseSmaliFileSingle(list.get(i),classList, graph);
		}
	}
	
	/**
	 * 
	 * 查找类所在的包，并将调用的包的值设置进去,这里由线程调用
	 * 
	 */
	
	synchronized public void setInvokePackage(PackageOrClass fatherPackage, Map<String, Integer> mapEdgeWeight) {
		
		// 找到类的父包
		GraphNode<PackageOrClass> graphNode = packageMap.get(fatherPackage.getName());
		
		// 找到父包的所有映射map
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
