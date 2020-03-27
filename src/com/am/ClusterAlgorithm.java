package com.am;

import java.util.List;

/**
 * 
 * �����㷨ʵ����
 * 
 * ����cluster�غͶ�Ӧ��clusterPair�أ�Ȼ�󲻶ϵ�����clusterPair�أ�ֱ���½���cluster��cluster��û����ϵΪֹ
 * 
 * �����㷨ֹͣ�ı�־��clusterPair��Ϊ��
 * 
 * @author Administrator
 *
 */

public class ClusterAlgorithm {
	
	// clusterPair��
	public ClusterPairMap map;
	
	// cluster��
	public List<Cluster> clusters;
	
	public ClusterAlgorithm(ClusterPairMap map, List<Cluster> clusters) {
		this.map = map;
		this.clusters = clusters;
	}
	
	/**
	 * 
	 * ��������������Ҫ�����½�һ����������cluster��cluster
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
	 * �ҵ�cluster�������Ѿ�ɾ���Ľڵ�����ϵ��cluster�������½���cluster��֮��ϵ
	 * 
	 */
	public void buildNewPair(Cluster lCluster, Cluster rCluster, Cluster newCluster) {
		if (clusters == null || clusters.size() == 0) {
			System.out.println("cluster�غĿս���");
		}
		// ע�����������������ĳһ��cluster��ϵ����ֻ����һ��
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
