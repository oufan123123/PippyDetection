package com.cn;

import java.util.List;
import java.util.ArrayList;

/**
 * 这是一个组成树结构的点
 * @author Administrator
 *
 * @param <T> T表示数据类型或者对象
 * 
 * 数据结构和数据类型分离，使得各部分专注自己内容业务
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
	 * 基本函数
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
