package com.cn;

import java.util.List;
import java.util.Map;
import com.am.*;

public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		
		// ��һ��������apk·�����ҵ�����Ͱ������еĽڵ㣬����һ�ÿ���
		List<Tree<PackageOrClass>> list = Tree.buildTree("D:\\apktool\\AdAway.apk");
		
		/*
		for (int i=0;i<list.size();i++) {
			list.get(i).pre_order();
			
		}
		*/
		
		// �ڶ��������ѱ������������ࣩҶ�ӽڵ��ҵ�������Ҷ�ӽڵ���ɵĶ�̬����
		List<String> classList = Tree.findAllClass(list);
		
		// �������������������������еĵ㹹��һ��ͼ
		Graph<PackageOrClass> graph = new Graph<>(list);
		
		
		
		
		//long begin = System.currentTimeMillis();
		//System.out.println(begin);
		
		// ���Ĳ��������̣߳�����ÿ��smali�ļ�
		graph.analyseAllSmaliFile(list, classList, graph);
		
		//long end = System.currentTimeMillis();
		//System.out.println(end - begin);
		
		// ���岽���������������Ӻ��ֵܽڵ�Ĺ�ϵ�ҵ�
		graph.handleGraphRelation();
		
		// ��������������ͼתΪ����ͼ��ע����岽�õ��������໥֮����õ�ֵ������a->b��ֵ������a�У� b->a��ֵ������b�У��������е�ͼ�ϵĵ�Ϊ�أ���ֵΪ5
		List<Cluster> clusters = graph.getClustersFromMap();
		ClusterPairMap clusterMap = graph.getClusterPairMap();
		// ���߲������о������
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
