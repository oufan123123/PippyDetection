package com.cn;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Administrator
 
 * 这是一个用于组成图的点
 * 
 * @param <T> T可以表示多种数据类型或者对象；
 * 
 * 分割数据类型 与 数据结构可以使得各部分专注于自身业务
 */

public class GraphNode<T> {
	
	private T data;
	private boolean isVisited;
	private boolean isClustered;
	volatile private Map<GraphNode<T>, Integer> map; // 表示包与包之间的权重联系
	
	volatile private List<GraphNode<T>> childList; // 表示包的父子关系，留待第二种兄弟关系处理
	
	public GraphNode(T data) {
		this.data = data;
		isVisited = false;
		isClustered = false;
		map = new HashMap<>();
		childList = new ArrayList<>();
		
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public Map<GraphNode<T>, Integer> getMap() {
		return map;
	}

	public void setMap(Map<GraphNode<T>, Integer> map) {
		this.map = map;
	}

	public boolean isClustered() {
		return isClustered;
	}

	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}

	public List<GraphNode<T>> getChildList() {
		return childList;
	}

	public void setChildList(List<GraphNode<T>> childList) {
		this.childList = childList;
	}
	
	
	 
}
