package com.cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.am.Cluster;
import com.am.ClusterPair;
import com.am.ClusterPairMap;

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
	
	// 用于创建聚类使用
	private Map<String, Cluster> clusterMap;
	
	
	
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
	
	
	
	
	/********************************对图的基本操作***************************************/
	
	/**
	 * 
	 * 由字符串名字得到图上某点
	 * 
	 * @param name
	 * @return
	 */
	public GraphNode<PackageOrClass> getGraphNode(String name) {
		return packageMap.get(name);
	}
	
	
	/**
	 * 
	 * 遍历图
	 * 
	 */
	public void searchGraph() {
		if (packageMap == null || packageMap.size() == 0) {
			return;
		}
		
		for (Map.Entry<String, GraphNode<PackageOrClass>> entry:packageMap.entrySet()) {
			System.out.println(entry.getKey());
		}
		System.out.println(packageMap.size());
	}
	
	/**
	 * 
	 * 遍历图和节点的子节点
	 * @return
	 */
	
	public void searchGraphAndChild() {
		Map<String, ClusterPair> pairMap = new HashMap<>();
		// 遍历map一遍
		
		
		
		
		
		
		
		// 测试用部分
		int all = 0;
		int type1 = 0;
		int type2 = 0;
		int type3 = 0;
		
		
		
		
		int siz = 0;	
		System.out.println("searchOne:");
		for (Map.Entry<String, GraphNode<PackageOrClass>> callEntry:packageMap.entrySet()) {
			Map<GraphNode<PackageOrClass>, Integer> calledMap = callEntry.getValue().getMap();
			//System.out.println(callEntry.getKey()+"call:");
			// 遍历被调用的包一遍
			
			for (Map.Entry<GraphNode<PackageOrClass>, Integer> calledEntry:calledMap.entrySet()) {
				
				//System.out.println(siz+":" + calledEntry.getKey().getData().getName());
				siz++;
				if (calledEntry.getKey().getData().getName().equals(callEntry.getKey())) {
					System.out.println("dd");
				}
				
				
			}
			
		}
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
	
	synchronized public void setInvokePackage(PackageOrClass fatherPackage, Map<String, Integer> mapEdgeWeight, String fatherString) {
		
		// 找到类的父包
		GraphNode<PackageOrClass> graphNode = packageMap.get(fatherPackage.getName());
		
		
		// 找到类的父类所在的包，并设置好父子关系
		GraphNode<PackageOrClass> fatherNode = packageMap.get(fatherString);
		
		if (fatherNode != null) {
			//System.out.println(fatherString);
			List<GraphNode<PackageOrClass>> list = fatherNode.getChildList();
			if (!list.contains(graphNode)) {
				list.add(graphNode);
			}
			fatherNode.setChildList(list);
		}
		
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
	
	/*********************************对图中包之间兄弟关系处理,利用存储的父子关系处理******************************************/
	
	/**
	 * 
	 * 遍历图中所有的未被访问点
	 * 
	 * @param 
	 */
	public void handleGraphRelation() {
		if (packageMap.size() == 0) {
			return;
		}
		
		for (Map.Entry<String, GraphNode<PackageOrClass>> entry:packageMap.entrySet()) {
			GraphNode<PackageOrClass> root = entry.getValue();
			if (!root.isVisited()) {
				root.setVisited(true);
				handleGraphRelation(root);
			}
		}
	}
	
	
	/**
	 * 
	 *  深搜图，将兄弟关系设置好，或者一个兄弟中即可
	 * 
	 * @param node
	 */
	public void handleGraphRelation(GraphNode<PackageOrClass> graphNode) {
		if (graphNode == null) {
			return;
		}
		// 设置为访问过的点
		graphNode.setVisited(true);
		// System.out.println("fatherNode:"+graphNode.getData().getName()+graphNode.getChildList().size());
		if (graphNode.getData().getName().equals("Landroidx/core/view")) {
			System.out.println("size:"+graphNode.getChildList().size());
		}
		List<GraphNode<PackageOrClass>> childList = graphNode.getChildList();
		if (childList == null || childList.size() == 0) {
			return;
		}
		// 处理兄弟关系
		handleBrotherRelation(childList);
		
		for (int i=0;i<childList.size();i++) {
			if (childList.get(i).isVisited()) {
				continue;
			}
			handleGraphRelation(childList.get(i));
		}
	}
	
	/**
	 * 
	 * 处理同一个父亲下的兄弟关系，联系加10,这里我们假设在数组前面的是大哥，后面是小弟，联系加在大哥到小弟的映射上
	 * 
	 * @param childList
	 */
	
	public void handleBrotherRelation(List<GraphNode<PackageOrClass>> childList) {
		for (int i=0;i<childList.size()-1;i++) {
			// 找到大哥子包所在的树点
			GraphNode<PackageOrClass> childGraphNodeBig = childList.get(i);
			
			// 找到大哥子包的连接点
			Map<GraphNode<PackageOrClass>, Integer> mapInBig = childGraphNodeBig.getMap();
			
			for (int j=i+1;j<childList.size();j++) {
				
				// 找到小弟子包所在的图点
				GraphNode<PackageOrClass> childGraphNodeLit = childList.get(j);
				//System.out.println("childNode:"+childGraphNodeLit.getData().getName());
				if (mapInBig.get(childGraphNodeLit) != null) {
					int x = mapInBig.get(childGraphNodeLit) + 10;
					mapInBig.put(childGraphNodeLit, x);
				} else {
					mapInBig.put(childGraphNodeLit, 10);
				}
				
			}
		}
	}
	
	
/***************************聚类操作**********************************/
	
	/**
	 *第一步，将有向图构成无向图，筛选连接值大于5的边，注意搜索完成之后将这些边删除
	 *
	 */
	
	
	/**
	 * 
	 * 
	 * 分析map，然后将图中每个点组成聚类
	 * 
	 * 
	 */
	public List<Cluster> getClustersFromMap() {
		clusterMap = new HashMap<>();
		List<Cluster> clusters = new ArrayList<>();
		for (Map.Entry<String, GraphNode<PackageOrClass>> entry:packageMap.entrySet()) {
			Cluster cluster = new Cluster(entry.getKey());
			clusters.add(cluster);
			clusterMap.put(entry.getKey(), cluster);
		}
		return clusters;
	}
	/**
	 * 组成clusterPair
	 * 
	 */
	public ClusterPairMap getClusterPairMap() {
		Map<String, ClusterPair> pairMap = new HashMap<>();
		// 遍历map一遍
		
		
		
		
		
		
		
		// 测试用部分
		int all = 0;
		int type1 = 0;
		int type2 = 0;
		int type3 = 0;
		
		
		
		
		int siz = 0;	
		for (Map.Entry<String, GraphNode<PackageOrClass>> callEntry:packageMap.entrySet()) {
			Map<GraphNode<PackageOrClass>, Integer> calledMap = callEntry.getValue().getMap();
			//System.out.println(callEntry.getKey()+"call:");
			// 遍历被调用的包一遍
			
			for (Map.Entry<GraphNode<PackageOrClass>, Integer> calledEntry:calledMap.entrySet()) {
				
				//System.out.println(siz+":" + calledEntry.getKey().getData().getName());
				siz++;
				// 创建一个新的clusterpair
				ClusterPair clusterPair = new ClusterPair(clusterMap.get(callEntry.getKey()), clusterMap.get(calledEntry.getKey().getData().getName()), calledEntry.getValue());
				// 看是否有重复边
				String saveString = calledEntry.getKey().getData().getName() + "--" + callEntry.getKey();
				//System.out.println("saveString:" + saveString);
				ClusterPair clusterPairInMap = pairMap.get(saveString);
				
				if (clusterPairInMap != null) {
					// 第一种情况，当相互调用(a->b,b->a)的值小于5，则删除原先的ClusterPair
					if (clusterPairInMap.getDistance() +calledEntry.getValue() < 5) {
						//System.out.println("delete:"+clusterPairInMap.getDistance() +calledEntry.getValue());
						pairMap.remove(saveString);
						type1 += 2;
						continue;
					}
					// 第二种情况无需处理
					type2++;
				// 第三种情况，找不到相互调用的情况时，将当前clusterPair设置进去
				} else {
					pairMap.put(callEntry.getKey() + "--" + calledEntry.getKey().getData().getName(), clusterPair);
					//System.out.println(calledEntry.getValue());
					type3++;
				}
				all++;
			}
			
		}
		// 再筛选自己一遍,删除小于5的节点
		if (pairMap.size() == 0) {
			return new ClusterPairMap(pairMap);		
		}
		Iterator<Map.Entry<String, ClusterPair>> iterator = pairMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ClusterPair> entry = iterator.next();
			if (entry.getValue().getDistance() < 5) {
				//type1++;
				iterator.remove();
			}
		}
		//System.out.println("type1:"+type1);
		//System.out.println("type2:"+type2);
		//System.out.println("type3:"+type3);
		//System.out.println("siz:"+siz);
		return new ClusterPairMap(pairMap);
	}
	
	

}
