package com.am;

/**
 * 
 * 两个簇的联系
 * 
 * @author Administrator
 *
 */

public class ClusterPair {
	
	// 左右簇
	private final Cluster lCluster;
	private final Cluster rCluster;
	
	// 两个簇之间的距离
	private int distance;
	
	public ClusterPair(Cluster lCluster, Cluster rCluster, int distance) {
		try {
			if (lCluster == null || rCluster == null) {
				throw new NullPointerException();
				
			} 
		} catch(NullPointerException e) {
			System.out.println("左右簇为空error");
			e.printStackTrace();
		}
		this.lCluster = lCluster;
		this.rCluster = rCluster;
		this.distance = distance;
	}

	public Cluster getlCluster() {
		return lCluster;
	}


	public Cluster getrCluster() {
		return rCluster;
	}


	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * 根据左右簇建一个新的父簇
	 * 
	 * @return
	 */
	public Cluster getNewCluster() {
		String newName = this.lCluster.getPackageName() + this.rCluster.getPackageName();
		Cluster newCluster = new Cluster(newName);
		newCluster.getChildList().add(this.lCluster);
		newCluster.getChildList().add(this.rCluster);
		return newCluster;
	}
	

}
