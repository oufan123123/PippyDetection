package com.cn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import brut.directory.DirectoryException;

/**
 * 
 * 注意对照关系：一个apk对象对应多棵树，一个apk反编译smali文件的下一级文件对应的一个文件夹对应一棵树
 * 
 * 用于对一个apk文件构建树结构，注意到这里，已经与业务挂钩，所以有些内容已经事先约定好
 * 
 * 点结构由 Java Class Package 和 Java Class File一起组成，其中Java Class File一定作叶子节点
 * 
 * 因为数据类型有两种，所以不再使用泛型
 * 
 * 可能会有空的 Java Class Package，这样其本身作为叶子节点
 * 
 * 
 * @author Administrator
 * 
 * 在这里我犯了一个错误，即对一种泛型，不能随意在一种数据结构中使用多种泛型，比如在我的这个设计中，
 *  Java Class Package 和 Java Class File各是一种泛型，导致后序查找处理无法进行，所以最好的设计是在一种数据结构中不要出现多种泛型；
 *
 */
public class Tree<T> {
	
	// 注意这里根一定是 Java Class Package 因为本业务最终建模是基于 Java Class Package ，而非Java Class File
	private TreeNode<T> root;
	private T data;
	
	public Tree(T data, TreeNode<T> root) {
		this.data = data;
		this.root = root;
	}
	
	
	
	
	public TreeNode<T> getRoot() {
		return root;
	}




	public void setRoot(TreeNode<T> root) {
		this.root = root;
	}




	public T getData() {
		return data;
	}




	public void setData(T data) {
		this.data = data;
	}
/***********************对树的基本操作(树的数据类型不同操作不同)************************************/
	
	/**
	 * 
	 * 先序遍历树结构
	 */
	public void pre_order() {
		if (root == null) {
			return;
		}
		dfs(root);
		
	}
	
	/**
	 * 深搜树
	 * 
	 */
	public void dfs(TreeNode<T> node) {
		if (node == null) {
			return;
		}
		// 注意这里针对apk处理，其他的数据类型修改处理方式即可
		handlePackageOrClass(node);
		List<TreeNode<T>> childList = node.getChildNodeList();
		if (childList == null || childList.size() == 0) {
			return;
		}
		for (int i=0;i<childList.size();i++) {
			dfs(childList.get(i));
		}
	}
	
	/**
	 *  访问apk点内容
	 * 
	 */
	public void handlePackageOrClass(TreeNode<T> tNode) {
		// 先判断是否是apk类
		if (!tNode.getData().getClass().equals(PackageOrClass.class)) {
			return;
		}
		TreeNode<PackageOrClass> node  =  (TreeNode<PackageOrClass>)tNode;
		PackageOrClass porc = node.getData();
		if (porc == null) {
			return;
		}
		System.out.println("类与否："+porc.isClass());
		System.out.println("名字："+porc.getName());
		System.out.println("路径："+porc.getPath());
	}

/*****************************apk深搜找到对应名字的根包********************************************/
	
	public static TreeNode<PackageOrClass> findPackage(List<Tree<PackageOrClass>> treeList, String name) {
		if (treeList == null || treeList.size() == 0) {
			return null;
		}
		TreeNode<PackageOrClass> treeNode = null;
		for (int i=0;i<treeList.size();i++) {
			treeNode = findPackageFromTree(treeList.get(i).getRoot(), name);
			if (treeNode != null) {
				return treeNode;
			}
		}
		return null;
		
	}
	
	public static TreeNode<PackageOrClass> findPackageFromTree(TreeNode<PackageOrClass> node, String name) {
		if (node == null) {
			return null;
		}
		List<TreeNode<PackageOrClass>> childList = node.getChildNodeList();
		if (node.getData().getName().equals(name)) {
			return node;
		}
		if (childList == null && childList.size() == 0) {
			return null;
		}
		for (int i=0;i<childList.size();i++) {
			TreeNode<PackageOrClass> childNode = findPackageFromTree(childList.get(i), name);
			if (childNode == null) {
				continue;
			}
			return childNode;
		}
		return null;
	}
	
/*******************************映射路径到类的map*******************************************/
	
	/**
	 * 
	 * 找到路径映射到类的map
	 * 
	 * @param treeList
	 * @return
	 */
	public static Map<String, TreeNode<PackageOrClass>> getPathToClass(List<Tree<PackageOrClass>> treeList) {
		Map<String, TreeNode<PackageOrClass>> map = new HashMap<>();
		if (treeList == null || treeList.size() == 0) {
			return null;
		}
		for (int i=0;i<treeList.size();i++) {
			Tree<PackageOrClass> tree = treeList.get(i);
			getPathToClassDFS(tree.getRoot(), map);
		}
		return map;
	}
	
	/**
	 * 
	 * 深搜所有的树，返回路径到类的映射关系
	 * 
	 * @param root
	 * @param map
	 */
	public static void getPathToClassDFS(TreeNode<PackageOrClass> node, Map<String, TreeNode<PackageOrClass>> map) {
		if (node == null) {
			return;
		}
		List<TreeNode<PackageOrClass>> childList = node.getChildNodeList();
		if (node.getData().isClass()) {
			map.put(node.getData().getPath(), node);
		}
		if (childList == null && childList.size() == 0) {
			return;
		}
		for (int i=0;i<childList.size();i++) {
			getPathToClassDFS(childList.get(i), map);
		}
	}
	
/**************************apk深搜找到所有的叶子节点dd*************************************/
	
	public static List<String> findAllClass(List<Tree<PackageOrClass>> treeList) {
		List<String> classList = new ArrayList<>();
		if (treeList == null || treeList.size() == 0) {
			return null;
		}
		for (int i=0;i<treeList.size();i++) {
			findAllClassFromTree(treeList.get(i).getRoot(), classList);
		}
		return classList;
		
	}
	
	public static void findAllClassFromTree(TreeNode<PackageOrClass> node, List<String> classList) {
		if (node == null) {
			return;
		}
		List<TreeNode<PackageOrClass>> childList = node.getChildNodeList();
		if (node.getData().isClass()) {
			classList.add(node.getData().getName());
		}
		if (childList == null && childList.size() == 0) {
			return;
		}
		for (int i=0;i<childList.size();i++) {
			findAllClassFromTree(childList.get(i), classList);
		}
	}
	
	
/**************************apk建树操作dd*************************************/


	/**
	 * 输入一个正确的apkpath，
	 * 
	 * 返回一个反编译之后apk树结构
	 * 
	 * @param apkPath apk路径
	 * @return
	 * @throws IOException 
	 * @throws DirectoryException 
	 */
	public static List<Tree<PackageOrClass>> buildTree(String apkPath) {
		// 获取所有树的根路径
		List<String> rootPathList = new ArrayList<>();
		try {
			// 反编译apk文件
			String decodePath = Decode.decodeOneApk(apkPath);
			//String decodePath = "D:\\DecodeApk\\AcrylicPaint";
			
			// 将文件目录提取构成一个树
			File apkFile = new File(decodePath);
			if (!apkFile.exists()) {
				System.out.println("反编译文件不存在，请检查路径：" + decodePath);
				return null;
			}
			File[] filesInApk = apkFile.listFiles();
			if (filesInApk == null || filesInApk.length == 0) {
				System.out.println("反编译文件夹为空，请检查：" + decodePath);
			}
			for (int i=0;i<filesInApk.length;i++) {
				String[] filesInApkSplit = StringUtils.split(filesInApk[i].getAbsolutePath(),"\\\\");
				if (filesInApkSplit[filesInApkSplit.length-1].contains("AndroidManifest.xml")) {
					//.....在这里要写如何操作 AndroidManifest.xml
					continue;
				}
				if (filesInApkSplit[filesInApkSplit.length-1].contains("smali")) {
					File[] filesInSmali = filesInApk[i].listFiles();
					
					// 将smali文件夹中所有的根目录加入rootPathList
					if (filesInSmali == null || filesInSmali.length == 0) {
						continue;
					}
					for (int j=0;j<filesInSmali.length;j++) {
						if (filesInSmali[j].isDirectory()) {
							rootPathList.add(filesInSmali[j].getAbsolutePath());
						}
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buildTreeNode(rootPathList);
		
	}
	
	/**
	 * 输入一个smali文件夹的路径
	 * 
	 * 构建树结构
	 * 
	 * 
	 * @param smaliPath
	 * @return
	 */
	
	public static List<Tree<PackageOrClass>> buildTreeNode (List<String> rootPathList) {
		List<Tree<PackageOrClass>> result = new ArrayList<>();
		try {
			if (rootPathList == null || rootPathList.size() == 0) {
				return null;
			}
			for (int i=0;i<rootPathList.size();i++) {
				String rootPath = rootPathList.get(i);
				String[] s = rootPath.split("\\\\");
				// 得到当前节点的名字在路径中的起始位置
				int beginIndex = rootPath.length() - s[s.length - 1].length();
				
				// 新建一个根包
				String replaceString = rootPath.substring(beginIndex).replace("\\", "/");
				PackageOrClass porc = new PackageOrClass("L" + replaceString, rootPath, false);
				
				TreeNode<PackageOrClass> node = new TreeNode<>(porc);
				// 根据根包组建树结构
				buildTreeFromRoot(node, beginIndex);
				Tree<PackageOrClass> tree = new Tree<>(porc, node);
				result.add(tree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 输入根包，然后dfs建立树结构
	 * 
	 * @param node
	 */
	public static void buildTreeFromRoot(TreeNode<PackageOrClass> node ,int beginIndex) {
		try {
			List<TreeNode<PackageOrClass>> childList = node.getChildNodeList();
			File file = new File(node.getData().getPath());
			File[] files = file.listFiles();
			if (files == null || files.length == 0) {
				return;
			}
			for (int i=0;i<files.length;i++) {
				// 对根包处理
				if (files[i].isDirectory()) {
					String replaceString = files[i].getAbsolutePath().substring(beginIndex).replace("\\", "/");
					PackageOrClass porc = new PackageOrClass("L" + replaceString, files[i].getAbsolutePath(), false);
					TreeNode<PackageOrClass> childNode = new TreeNode<>(porc);
					childList.add(childNode);
					//System.out.println(childNode.getData().getName());
					// 递归组建子树
					buildTreeFromRoot(childNode, beginIndex);
				// 对类文件处理
				} else {
					String smaliString = files[i].getAbsolutePath().replace(".smali", "");
					String replaceString = smaliString.substring(beginIndex).replace("\\", "/");
					PackageOrClass porc = new PackageOrClass("L" + replaceString, files[i].getAbsolutePath(), true);
					TreeNode<PackageOrClass> childNode = new TreeNode<>(porc);
					childList.add(childNode);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
