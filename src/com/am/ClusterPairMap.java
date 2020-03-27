package com.am;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于查找两个簇是否在clusterPair池中
 * 
 * @author Administrator
 *
 */
public class ClusterPairMap {
	

	private Map<String, ClusterPair> map;
	
	// 通过此简单查找对应map并进行操作
	List<String> clusterPairList;
	
	
	// 这里初始化一定要保证所以的key在list中，否则会出错
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
	 * 构建map和list
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
	 * 根据map构建list
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
	 * 两个簇对应的clusterpair是否是否在池中中,这里不分左右
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
	 * 增加clusterPair
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
	 * 根据clusterPair删除clusterPair
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
	 * 根据key对应的值删除
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
	 * 获得两个簇的hash值
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
	 * 判断自己是否为空
	 * 
	 */
	public boolean isEmpty() {
		if (clusterPairList.size() == 0) {
			return true;
		}
		return false;
	}

}


	
