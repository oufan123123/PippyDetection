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
 * ע����չ�ϵ��һ��apk�����Ӧ�������һ��apk������smali�ļ�����һ���ļ���Ӧ��һ���ļ��ж�Ӧһ����
 * 
 * ���ڶ�һ��apk�ļ��������ṹ��ע�⵽����Ѿ���ҵ��ҹ���������Щ�����Ѿ�����Լ����
 * 
 * ��ṹ�� Java Class Package �� Java Class Fileһ����ɣ�����Java Class Fileһ����Ҷ�ӽڵ�
 * 
 * ��Ϊ�������������֣����Բ���ʹ�÷���
 * 
 * ���ܻ��пյ� Java Class Package�������䱾����ΪҶ�ӽڵ�
 * 
 * 
 * @author Administrator
 * 
 * �������ҷ���һ�����󣬼���һ�ַ��ͣ�����������һ�����ݽṹ��ʹ�ö��ַ��ͣ��������ҵ��������У�
 *  Java Class Package �� Java Class File����һ�ַ��ͣ����º�����Ҵ����޷����У�������õ��������һ�����ݽṹ�в�Ҫ���ֶ��ַ��ͣ�
 *
 */
public class Tree<T> {
	
	// ע�������һ���� Java Class Package ��Ϊ��ҵ�����ս�ģ�ǻ��� Java Class Package ������Java Class File
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
/***********************�����Ļ�������(�����������Ͳ�ͬ������ͬ)************************************/
	
	/**
	 * 
	 * ����������ṹ
	 */
	public void pre_order() {
		if (root == null) {
			return;
		}
		dfs(root);
		
	}
	
	/**
	 * ������
	 * 
	 */
	public void dfs(TreeNode<T> node) {
		if (node == null) {
			return;
		}
		// ע���������apk�������������������޸Ĵ���ʽ����
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
	 *  ����apk������
	 * 
	 */
	public void handlePackageOrClass(TreeNode<T> tNode) {
		// ���ж��Ƿ���apk��
		if (!tNode.getData().getClass().equals(PackageOrClass.class)) {
			return;
		}
		TreeNode<PackageOrClass> node  =  (TreeNode<PackageOrClass>)tNode;
		PackageOrClass porc = node.getData();
		if (porc == null) {
			return;
		}
		System.out.println("�����"+porc.isClass());
		System.out.println("���֣�"+porc.getName());
		System.out.println("·����"+porc.getPath());
	}

/*****************************apk�����ҵ���Ӧ���ֵĸ���********************************************/
	
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
	
/*******************************ӳ��·�������map*******************************************/
	
	/**
	 * 
	 * �ҵ�·��ӳ�䵽���map
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
	 * �������е���������·�������ӳ���ϵ
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
	
/**************************apk�����ҵ����е�Ҷ�ӽڵ�dd*************************************/
	
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
	
	
/**************************apk��������dd*************************************/


	/**
	 * ����һ����ȷ��apkpath��
	 * 
	 * ����һ��������֮��apk���ṹ
	 * 
	 * @param apkPath apk·��
	 * @return
	 * @throws IOException 
	 * @throws DirectoryException 
	 */
	public static List<Tree<PackageOrClass>> buildTree(String apkPath) {
		// ��ȡ�������ĸ�·��
		List<String> rootPathList = new ArrayList<>();
		try {
			// ������apk�ļ�
			String decodePath = Decode.decodeOneApk(apkPath);
			//String decodePath = "D:\\DecodeApk\\AcrylicPaint";
			
			// ���ļ�Ŀ¼��ȡ����һ����
			File apkFile = new File(decodePath);
			if (!apkFile.exists()) {
				System.out.println("�������ļ������ڣ�����·����" + decodePath);
				return null;
			}
			File[] filesInApk = apkFile.listFiles();
			if (filesInApk == null || filesInApk.length == 0) {
				System.out.println("�������ļ���Ϊ�գ����飺" + decodePath);
			}
			for (int i=0;i<filesInApk.length;i++) {
				String[] filesInApkSplit = StringUtils.split(filesInApk[i].getAbsolutePath(),"\\\\");
				if (filesInApkSplit[filesInApkSplit.length-1].contains("AndroidManifest.xml")) {
					//.....������Ҫд��β��� AndroidManifest.xml
					continue;
				}
				if (filesInApkSplit[filesInApkSplit.length-1].contains("smali")) {
					File[] filesInSmali = filesInApk[i].listFiles();
					
					// ��smali�ļ��������еĸ�Ŀ¼����rootPathList
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
	 * ����һ��smali�ļ��е�·��
	 * 
	 * �������ṹ
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
				// �õ���ǰ�ڵ��������·���е���ʼλ��
				int beginIndex = rootPath.length() - s[s.length - 1].length();
				
				// �½�һ������
				String replaceString = rootPath.substring(beginIndex).replace("\\", "/");
				PackageOrClass porc = new PackageOrClass("L" + replaceString, rootPath, false);
				
				TreeNode<PackageOrClass> node = new TreeNode<>(porc);
				// ���ݸ����齨���ṹ
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
	 * ���������Ȼ��dfs�������ṹ
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
				// �Ը�������
				if (files[i].isDirectory()) {
					String replaceString = files[i].getAbsolutePath().substring(beginIndex).replace("\\", "/");
					PackageOrClass porc = new PackageOrClass("L" + replaceString, files[i].getAbsolutePath(), false);
					TreeNode<PackageOrClass> childNode = new TreeNode<>(porc);
					childList.add(childNode);
					//System.out.println(childNode.getData().getName());
					// �ݹ��齨����
					buildTreeFromRoot(childNode, beginIndex);
				// �����ļ�����
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
