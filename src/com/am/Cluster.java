package com.am;

import java.util.ArrayList;
import java.util.List;

import com.cn.GraphNode;

/**
 * 
 * ����ľ���Ĵ��Ǹ���
 * 
 * ע�����һ����
 * 
 * @author Administrator
 *
 */
public class Cluster {
	
	// �����������,��������Ψһ��ʶһ����,��������ֲ��ɱ�
	private final String packageName;
	
	// �Ӵ�
	private List<Cluster> childList;
	
	public Cluster(String packageName) {
		try {
			if (packageName == null) {
				throw new NullPointerException();
			} 
		} catch (NullPointerException e) {
			System.out.println("�ص����ֲ���Ϊ�գ���������");
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
	 * ���Һ��ӽڵ�
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
	 * ���Ӻ��ӽڵ�
	 */
	public void addChild(Cluster child) {
		if (child == null) {
			return;
		}
		childList.add(child);
	}
	
	
	
	
	

}
