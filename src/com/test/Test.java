package com.test;

import java.util.*;

import com.am.*;

public class Test {
	
	
			
			public static void main(String[] args) {
				// 开始测试聚类算法
				Cluster a = new Cluster("A");
				Cluster b = new Cluster("B");
				Cluster c = new Cluster("C");
				Cluster d = new Cluster("D");
				Cluster e = new Cluster("E");
				Cluster f = new Cluster("F");
				List<Cluster> clusters = new ArrayList<>();
				clusters.add(a);
				clusters.add(b);
				clusters.add(c);
				clusters.add(d);
				clusters.add(e);
				clusters.add(f);
				
				ClusterPair c1 = new ClusterPair(a,b,10);
				ClusterPair c4 = new ClusterPair(b,c,10);
				ClusterPair c2 = new ClusterPair(b,d,10);
				ClusterPair c3 = new ClusterPair(f,e,10);
				
				
				List<ClusterPair> list = new ArrayList<>();
				list.add(c1);
				list.add(c2);
				list.add(c3);
				list.add(c4);
				
				List<ClusterPair> listo = new ArrayList<>();
				listo.add(c1);
				listo.add(c2);
				listo.add(c3);
				listo.add(c4);
				
				ClusterPairMap map = new ClusterPairMap(list);
				ClusterAlgorithm ca = new ClusterAlgorithm(map, clusters);
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
