package com.cn;

import java.util.List;
import java.util.ArrayList;

/**
 * ����һ��������ṹ�ĵ�
 * @author Administrator
 *
 * @param <T> T��ʾ�������ͻ��߶���
 * 
 * ���ݽṹ���������ͷ��룬ʹ�ø�����רע�Լ�����ҵ��
 */

public class TreeNode<T> {
	private T data;
	private boolean isVisited;
	private List<TreeNode<T>> childNodeList;
	
	public TreeNode(T data) {
		this.data = data;
		isVisited = false;
		childNodeList = new ArrayList<>();
	}
	
	/**
	 * ��������
	 */
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


	public List<TreeNode<T>> getChildNodeList() {
		return childNodeList;
	}


	public void setChildNodeList(List<TreeNode<T>> childNodeList) {
		this.childNodeList = childNodeList;
	}

	
	

}
