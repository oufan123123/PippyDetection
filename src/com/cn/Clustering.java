package com.cn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * ����һ��ͼ��ע��������ͼ������ͼ����������������Ҫ���䴦��Ϊ����ͼ��������ͼ���������õ�ֵ���������Ҿ۵�һ����
 * 
 * @author Administrator
 *
 */

public class Clustering {
	
	// �����δ����ͼ,ͼ��һ��������ͼ�����Ե�ȫ���㶼��һ��
	private Graph<PackageOrClass> graph;
	
	
	// ����õ�ͼ
	private List<Graph<PackageOrClass>> graphList;
	
	public Clustering(Graph<PackageOrClass> graph) {
		this.graph = graph;
		graphList = new ArrayList<>();
	}
	
	/**************************���ѱ���һ��ͼ��ÿ���ߣ��ұ�����ɺ���һ����,���ҽ��о���***************************************/
	
	
	/**
	 * 
	 * ��������һ������ͼ�е�ÿ����
	 * 
	 */
	public void searchEdgeFromGraph() {
		Map<String, GraphNode<PackageOrClass>> map = graph.getMap();
		if (map == null || map.size() == 0) {
			return;
		}
		for (Map.Entry<String, GraphNode<PackageOrClass>> entry:map.entrySet()) {
			GraphNode<PackageOrClass> node = entry.getValue();
			// ����
			Graph<PackageOrClass> newGraph = null;
			// �Ƿ��Ѿ���ĳ��������
			if (node.isClustered()) {
				
			}
			clustering(node);
		}
		
	}
	
	/**
	 * 
	 * �Ե�ǰ����о���
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
			// �ҵ�����һ����
			GraphNode<PackageOrClass> childGraphNode = entry.getKey();
			// ����һ�����Ƿ�ָ�������
			if (childGraphNode.getMap().get(node) != null) {
				
			}
		}
	}
	
	
	

}
