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
	
	// ���ڴ�������ʹ��
	private Map<String, Cluster> clusterMap;
	
	
	
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
	
	
	
	
	/********************************��ͼ�Ļ�������***************************************/
	
	/**
	 * 
	 * ���ַ������ֵõ�ͼ��ĳ��
	 * 
	 * @param name
	 * @return
	 */
	public GraphNode<PackageOrClass> getGraphNode(String name) {
		return packageMap.get(name);
	}
	
	
	/**
	 * 
	 * ����ͼ
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
	 * ����ͼ�ͽڵ���ӽڵ�
	 * @return
	 */
	
	public void searchGraphAndChild() {
		Map<String, ClusterPair> pairMap = new HashMap<>();
		// ����mapһ��
		
		
		
		
		
		
		
		// �����ò���
		int all = 0;
		int type1 = 0;
		int type2 = 0;
		int type3 = 0;
		
		
		
		
		int siz = 0;	
		System.out.println("searchOne:");
		for (Map.Entry<String, GraphNode<PackageOrClass>> callEntry:packageMap.entrySet()) {
			Map<GraphNode<PackageOrClass>, Integer> calledMap = callEntry.getValue().getMap();
			//System.out.println(callEntry.getKey()+"call:");
			// ���������õİ�һ��
			
			for (Map.Entry<GraphNode<PackageOrClass>, Integer> calledEntry:calledMap.entrySet()) {
				
				//System.out.println(siz+":" + calledEntry.getKey().getData().getName());
				siz++;
				if (calledEntry.getKey().getData().getName().equals(callEntry.getKey())) {
					System.out.println("dd");
				}
				
				
			}
			
		}
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
	
	synchronized public void setInvokePackage(PackageOrClass fatherPackage, Map<String, Integer> mapEdgeWeight, String fatherString) {
		
		// �ҵ���ĸ���
		GraphNode<PackageOrClass> graphNode = packageMap.get(fatherPackage.getName());
		
		
		// �ҵ���ĸ������ڵİ��������úø��ӹ�ϵ
		GraphNode<PackageOrClass> fatherNode = packageMap.get(fatherString);
		
		if (fatherNode != null) {
			//System.out.println(fatherString);
			List<GraphNode<PackageOrClass>> list = fatherNode.getChildList();
			if (!list.contains(graphNode)) {
				list.add(graphNode);
			}
			fatherNode.setChildList(list);
		}
		
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
	
	/*********************************��ͼ�а�֮���ֵܹ�ϵ����,���ô洢�ĸ��ӹ�ϵ����******************************************/
	
	/**
	 * 
	 * ����ͼ�����е�δ�����ʵ�
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
	 *  ����ͼ�����ֵܹ�ϵ���úã�����һ���ֵ��м���
	 * 
	 * @param node
	 */
	public void handleGraphRelation(GraphNode<PackageOrClass> graphNode) {
		if (graphNode == null) {
			return;
		}
		// ����Ϊ���ʹ��ĵ�
		graphNode.setVisited(true);
		// System.out.println("fatherNode:"+graphNode.getData().getName()+graphNode.getChildList().size());
		if (graphNode.getData().getName().equals("Landroidx/core/view")) {
			System.out.println("size:"+graphNode.getChildList().size());
		}
		List<GraphNode<PackageOrClass>> childList = graphNode.getChildList();
		if (childList == null || childList.size() == 0) {
			return;
		}
		// �����ֵܹ�ϵ
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
	 * ����ͬһ�������µ��ֵܹ�ϵ����ϵ��10,�������Ǽ���������ǰ����Ǵ�磬������С�ܣ���ϵ���ڴ�絽С�ܵ�ӳ����
	 * 
	 * @param childList
	 */
	
	public void handleBrotherRelation(List<GraphNode<PackageOrClass>> childList) {
		for (int i=0;i<childList.size()-1;i++) {
			// �ҵ�����Ӱ����ڵ�����
			GraphNode<PackageOrClass> childGraphNodeBig = childList.get(i);
			
			// �ҵ�����Ӱ������ӵ�
			Map<GraphNode<PackageOrClass>, Integer> mapInBig = childGraphNodeBig.getMap();
			
			for (int j=i+1;j<childList.size();j++) {
				
				// �ҵ�С���Ӱ����ڵ�ͼ��
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
	
	
/***************************�������**********************************/
	
	/**
	 *��һ����������ͼ��������ͼ��ɸѡ����ֵ����5�ıߣ�ע���������֮����Щ��ɾ��
	 *
	 */
	
	
	/**
	 * 
	 * 
	 * ����map��Ȼ��ͼ��ÿ������ɾ���
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
	 * ���clusterPair
	 * 
	 */
	public ClusterPairMap getClusterPairMap() {
		Map<String, ClusterPair> pairMap = new HashMap<>();
		// ����mapһ��
		
		
		
		
		
		
		
		// �����ò���
		int all = 0;
		int type1 = 0;
		int type2 = 0;
		int type3 = 0;
		
		
		
		
		int siz = 0;	
		for (Map.Entry<String, GraphNode<PackageOrClass>> callEntry:packageMap.entrySet()) {
			Map<GraphNode<PackageOrClass>, Integer> calledMap = callEntry.getValue().getMap();
			//System.out.println(callEntry.getKey()+"call:");
			// ���������õİ�һ��
			
			for (Map.Entry<GraphNode<PackageOrClass>, Integer> calledEntry:calledMap.entrySet()) {
				
				//System.out.println(siz+":" + calledEntry.getKey().getData().getName());
				siz++;
				// ����һ���µ�clusterpair
				ClusterPair clusterPair = new ClusterPair(clusterMap.get(callEntry.getKey()), clusterMap.get(calledEntry.getKey().getData().getName()), calledEntry.getValue());
				// ���Ƿ����ظ���
				String saveString = calledEntry.getKey().getData().getName() + "--" + callEntry.getKey();
				//System.out.println("saveString:" + saveString);
				ClusterPair clusterPairInMap = pairMap.get(saveString);
				
				if (clusterPairInMap != null) {
					// ��һ����������໥����(a->b,b->a)��ֵС��5����ɾ��ԭ�ȵ�ClusterPair
					if (clusterPairInMap.getDistance() +calledEntry.getValue() < 5) {
						//System.out.println("delete:"+clusterPairInMap.getDistance() +calledEntry.getValue());
						pairMap.remove(saveString);
						type1 += 2;
						continue;
					}
					// �ڶ���������账��
					type2++;
				// ������������Ҳ����໥���õ����ʱ������ǰclusterPair���ý�ȥ
				} else {
					pairMap.put(callEntry.getKey() + "--" + calledEntry.getKey().getData().getName(), clusterPair);
					//System.out.println(calledEntry.getValue());
					type3++;
				}
				all++;
			}
			
		}
		// ��ɸѡ�Լ�һ��,ɾ��С��5�Ľڵ�
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
