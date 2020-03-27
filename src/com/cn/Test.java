package com.cn;

import java.util.List;
import java.util.Map;
import com.am.*;

public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		
		// 第一步，输入apk路径，找到（类和包）所有的节点，构成一棵棵树
		List<Tree<PackageOrClass>> list = Tree.buildTree("D:\\apktool\\AdAway.apk");
		
		/*
		for (int i=0;i<list.size();i++) {
			list.get(i).pre_order();
			
		}
		*/
		
		// 第二步，深搜遍历树，将（类）叶子节点找到，返回叶子节点组成的动态数组
		List<String> classList = Tree.findAllClass(list);
		
		// 第三步，遍历树，将所有树中的点构成一个图
		Graph<PackageOrClass> graph = new Graph<>(list);
		
		
		
		
		//long begin = System.currentTimeMillis();
		//System.out.println(begin);
		
		// 第四步，启动线程，分析每个smali文件
		graph.analyseAllSmaliFile(list, classList, graph);
		
		//long end = System.currentTimeMillis();
		//System.out.println(end - begin);
		
		// 第五步，遍历树，将父子和兄弟节点的关系找到
		graph.handleGraphRelation();
		
		// 第六步，将有向图转为无向图，注意第五步得到的是类相互之间调用的值，所以a->b的值保存在a中， b->a的值保存在b中，聚类所有的图上的点为簇，阀值为5
		List<Cluster> clusters = graph.getClustersFromMap();
		ClusterPairMap clusterMap = graph.getClusterPairMap();
		// 第七步，进行聚类操作
		ClusterAlgorithm ca = new ClusterAlgorithm(clusterMap, clusters);
		List<Cluster> result = ca.clustering();
		
		if(result == null || result.size() == 0) {
			System.out.println("null");
		}
		for (Cluster cluster:result) {
			System.out.println("tree");
			dfs(cluster);
		}
		
		
		
		
		
	}
	
	public static void dfs(Cluster node) {
		if (node == null) {
			return;
		}
		List<Cluster> childList = node.getChildList();
		if (childList == null || childList.size() == 0) {
			System.out.println(node.getPackageName());
		}
		for (Cluster c:childList) {
			dfs(c);
		}
	}

}
