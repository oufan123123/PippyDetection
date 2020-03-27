package com.am;

/**
 * 
 * �����ص���ϵ
 * 
 * @author Administrator
 *
 */

public class ClusterPair {
	
	// ���Ҵ�
	private final Cluster lCluster;
	private final Cluster rCluster;
	
	// ������֮��ľ���
	private int distance;
	
	public ClusterPair(Cluster lCluster, Cluster rCluster, int distance) {
		try {
			if (lCluster == null || rCluster == null) {
				throw new NullPointerException();
				
			} 
		} catch(NullPointerException e) {
			System.out.println("���Ҵ�Ϊ��error");
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
	 * �������Ҵؽ�һ���µĸ���
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
