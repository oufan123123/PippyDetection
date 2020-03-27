package com.am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ���ڲ����������Ƿ���clusterPair����
 * 
 * @author Administrator
 *
 */
public class ClusterPairMap {
	

	private Map<String, ClusterPair> map;
	
	// ͨ���˼򵥲��Ҷ�Ӧmap�����в���
	List<String> clusterPairList;
	
	
	// �����ʼ��һ��Ҫ��֤���Ե�key��list�У���������
	public ClusterPairMap(List<ClusterPair> list) {
		map = new HashMap<>();
		clusterPairList = new ArrayList<>();
		buildMapAndList(list);
	}
	
	public ClusterPairMap(Map<String, ClusterPair> map) {
		this.map = map;
		clusterPairList = new ArrayList<>();
		buildListByMap();
	}
	
	
	/**
	 * 
	 * ����map��list
	 * 
	 */
	public void buildMapAndList(List<ClusterPair> list) {
		if (list == null || list.size() == 0) {
			return ;
		}
		
		for (ClusterPair clusterPair:list) {
			String pairName = getHashCode(clusterPair.getlCluster(), clusterPair.getrCluster());
			System.out.println(pairName + clusterPair.getDistance());
			map.put(pairName, clusterPair);
			clusterPairList.add(pairName);
		}
	}
	
	/**
	 * 
	 * ����map����list
	 * 
	 */
	public void buildListByMap() {
		if (map == null || map.size() == 0) {
			return;
		}
		
		for (Map.Entry<String, ClusterPair> entry:map.entrySet()) {
			clusterPairList.add(entry.getKey());
		}
	}

	
	
	
	/**
	 * 
	 * �����ض�Ӧ��clusterpair�Ƿ��Ƿ��ڳ�����,���ﲻ������
	 * 
	 * @param lCluster
	 * @param rCluster
	 * @return
	 */
	
	public boolean isInMap(Cluster c1, Cluster c2) {
		String stringOne = getHashCode(c1, c2);
		String stringTwo = getHashCode(c2, c1);
		boolean isDelete = false;
		if (stringOne != null && map.get(stringOne) != null) {
			map.remove(stringOne);
			clusterPairList.remove(stringOne);
			return true;
		}
		if (stringTwo != null && map.get(stringTwo) != null) {
			map.remove(stringTwo);
			clusterPairList.remove(stringTwo);
			return true;
		}
		return false;
	}
	
	/**
	 * ����clusterPair
	 * 
	 */
	public void add(ClusterPair clusterPair) {
		if (clusterPair == null) {
			return;
		}
		String newString = getHashCode(clusterPair.getlCluster(), clusterPair.getrCluster());
		if (newString == null) {
			return;
		}
		clusterPairList.add(newString);
		map.put(newString, clusterPair);
	}
	
	/**
	 * ����clusterPairɾ��clusterPair
	 * 
	 */
	public void delete(ClusterPair clusterPair) {
		String string = getHashCode(clusterPair.getlCluster(), clusterPair.getrCluster());
		if (string == null) {
			return;
		}
		clusterPairList.remove(string);
		map.remove(string);
	}
	
	
	/**
	 * ����key��Ӧ��ֵɾ��
	 * 
	 */
	
	public ClusterPair deleteFirst() {
		if (clusterPairList == null || clusterPairList.size() == 0) {
			return null; 
		}
		
		String string = clusterPairList.remove(0);
		ClusterPair clusterPair = map.remove(string);
		return clusterPair;
		
	}
	
	
	
	/**
	 * 
	 * ��������ص�hashֵ
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public String getHashCode(Cluster c1, Cluster c2) {
		return getHashCode(c1.getPackageName(), c2.getPackageName());
	}
	
	public String getHashCode(String s1, String s2) {
		if (s1 == null || s2 == null) {
			return null;
		}
		String newString = s1+"--"+s2;
		return newString;
	}
	
	
	/**
	 * 
	 * �ж��Լ��Ƿ�Ϊ��
	 * 
	 */
	public boolean isEmpty() {
		if (clusterPairList.size() == 0) {
			return true;
		}
		return false;
	}

}


	
