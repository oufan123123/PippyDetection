package com.cn;

import java.util.Map;
import java.util.HashMap;

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
	volatile private Map<GraphNode<T>, Integer> map; // 表示包与包之间的权重联系
	
	public GraphNode(T data) {
		this.data = data;
		isVisited = false;
		map = new HashMap<>();
		
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
	
	
	 
}
