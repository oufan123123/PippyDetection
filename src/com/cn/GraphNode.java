package com.cn;

import java.util.Map;
import java.util.HashMap;

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
	volatile private Map<GraphNode<T>, Integer> map; // ��ʾ�����֮���Ȩ����ϵ
	
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
