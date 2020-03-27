package com.cn;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Administrator
 
 * ����һ���������ͼ�ĵ�
 * 
 * @param <T> T���Ա�ʾ�����������ͻ��߶���
 * 
 * �ָ��������� �� ���ݽṹ����ʹ�ø�����רע������ҵ��
 */

public class GraphNode<T> {
	
	private T data;
	private boolean isVisited;
	private boolean isClustered;
	volatile private Map<GraphNode<T>, Integer> map; // ��ʾ�����֮���Ȩ����ϵ
	
	volatile private List<GraphNode<T>> childList; // ��ʾ���ĸ��ӹ�ϵ�������ڶ����ֵܹ�ϵ����
	
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
