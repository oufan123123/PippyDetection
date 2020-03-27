package com.am;

import java.util.List;

/**
 * 
 * 聚类算法实现类
 * 
 * 输入cluster池和对应的clusterPair池，然后不断的缩减clusterPair池，直到新建的cluster与cluster池没有联系为止
 * 
 * 所以算法停止的标志是clusterPair池为空
 * 
 * @author Administrator
 *
 */

public class ClusterAlgorithm {
	
	// clusterPair池
	public ClusterPairMap map;
	
	// cluster池
	public List<Cluster> clusters;
	
	public ClusterAlgorithm(ClusterPairMap map, List<Cluster> clusters) {
		this.map = map;
		this.clusters = clusters;
	}
	
	/**
	 * 
	 * 主操作函数，主要用于新建一个包含左右cluster的cluster
	 * 
	 */
	public List<Cluster> clustering() {
		while (!map.isEmpty() && clusters.size() != 1) {
			ClusterPair clusterPair = map.deleteFirst();
			Cluster lCluster = clusterPair.getlCluster();
			Cluster rCluster = clusterPair.getrCluster();
			clusters.remove(lCluster);
			clusters.remove(rCluster);
			Cluster newCluster = new Cluster(lCluster.getPackageName() + rCluster.getPackageName());
			newCluster.addChild(rCluster);
			newCluster.addChild(lCluster);
			buildNewPair(lCluster, rCluster, newCluster);
			clusters.add(newCluster);
		}
		return clusters;
	}
	
	/**
	 * 
	 * 找到cluster池中与已经删除的节点有联系的cluster，并将新建的cluster与之联系
	 * 
	 */
	public void buildNewPair(Cluster lCluster, Cluster rCluster, Cluster newCluster) {
		if (clusters == null || clusters.size() == 0) {
			System.out.println("cluster池耗空结束");
		}
		// 注意这里左右如果均与某一个cluster联系，则只保存一个
		for (int i=0;i<clusters.size();i++) {
			boolean lJu = map.isInMap(clusters.get(i), lCluster);
			//System.out.println("l:"+clusters.get(i).getPackageName());
			boolean rJu = map.isInMap(clusters.get(i), rCluster);
			if (lJu || rJu) {
				ClusterPair newClusterPair = new ClusterPair(clusters.get(i), newCluster, 10);
				map.add(newClusterPair);
			}	
		}
	}

}
