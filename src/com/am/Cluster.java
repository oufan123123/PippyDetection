package com.am;

import java.util.ArrayList;
import java.util.List;

import com.cn.GraphNode;

/**
 * 
 * 这里的聚类的簇是根包
 * 
 * 注意包名一定是
 * 
 * @author Administrator
 *
 */
public class Cluster {
	
	// 存根包的名字,假设这能唯一标识一个簇,且这个名字不可变
	private final String packageName;
	
	// 子簇
	private List<Cluster> childList;
	
	public Cluster(String packageName) {
		try {
			if (packageName == null) {
				throw new NullPointerException();
			} 
		} catch (NullPointerException e) {
			System.out.println("簇的名字不能为空，请检查输入");
			e.printStackTrace();
		}
		this.packageName = packageName;
		childList = new ArrayList<>();
	}

	public String getPackageName() {
		return packageName;
	}

	public List<Cluster> getChildList() {
		return childList;
	}

	public void setChildList(List<Cluster> childList) {
		this.childList = childList;
	}
	
	
	
	@Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Cluster other = (Cluster) obj;
        if (packageName == null)
        {
            if (other.packageName != null)
            {
                return false;
            }
        } else if (!packageName.equals(other.packageName))
        {
            return false;
        }
        return true;
    }
	
	
	
	/**
	 * 
	 * 查找孩子节点
	 * @param packageName
	 * @return
	 */
	public Cluster findClusterFromChild(String packageName) {
		if (childList == null || childList.size() == 0) {
			return null;
		}
		for (int i=0;i<childList.size();i++) {
			if (childList.get(i).getPackageName().equals(packageName)) {
				return childList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * 增加孩子节点
	 */
	public void addChild(Cluster child) {
		if (child == null) {
			return;
		}
		childList.add(child);
	}
	
	
	
	
	

}
