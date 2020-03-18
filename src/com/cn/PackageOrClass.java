package com.cn;

/**
 * 表示包或者类文件
 * 
 */
public class PackageOrClass {
	
	private String name;
	private String path;

	private boolean isClass; // 表示这个对象是否是类或是包
	
	
	public PackageOrClass(String name, String path, boolean isClass) {
		this.name = name;
		this.path = path;
		this.isClass = isClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isClass() {
		return isClass;
	}

	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}
	
	
	
	

}
