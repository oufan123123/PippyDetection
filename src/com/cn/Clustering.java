package com.cn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * 输入一个图，注意假设这个图是有向图，所以这里我们需要将其处理为无向图，即有向图中两两调用的值加起来，且聚到一堆中
 * 
 * @author Administrator
 *
 */

public class Clustering {
	
	// 传入的未划分图,图不一定是连接图，所以得全部点都走一遍
	private Graph<PackageOrClass> graph;
	
	
	// 聚类好的图
	private List<Graph<PackageOrClass>> graphList;
	
	public Clustering(Graph<PackageOrClass> graph) {
		this.graph = graph;
		graphList = new ArrayList<>();
	}
	
	/**************************广搜遍历一遍图的每个边，且遍历完成后保留一个边,并且进行聚类***************************************/
	
	
	/**
	 * 
	 * 搜索有向不一定连接图中的每个点
	 * 
	 */
	public void searchEdgeFromGraph() {
		Map<String, GraphNode<PackageOrClass>> map = graph.getMap();
		if (map == null || map.size() == 0) {
			return;
		}
		for (Map.Entry<String, GraphNode<PackageOrClass>> entry:map.entrySet()) {
			GraphNode<PackageOrClass> node = entry.getValue();
			// 聚类
			Graph<PackageOrClass> newGraph = null;
			// 是否已经在某个聚类中
			if (node.isClustered()) {
				
			}
			clustering(node);
		}
		
	}
	
	/**
	 * 
	 * 对当前点进行聚类
	 * 
	 * @param node
	 */
	public void clustering(GraphNode<PackageOrClass> node) {
		Map<GraphNode<PackageOrClass>, Integer> mapInNode = node.getMap();
		if (mapInNode == null || mapInNode.size() == 0) {
			return;
		}
		Iterator<Map.Entry<GraphNode<PackageOrClass>, Integer>> iterator = mapInNode.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<GraphNode<PackageOrClass>, Integer> entry = iterator.next();
			// 找到边另一个点
			GraphNode<PackageOrClass> childGraphNode = entry.getKey();
			// 边另一个点是否指向这个点
			if (childGraphNode.getMap().get(node) != null) {
				
			}
		}
	}
	
	
	

}
