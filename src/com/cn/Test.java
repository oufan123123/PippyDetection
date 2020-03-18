package com.cn;

import java.util.List;

public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		
		// 第一步，输入apk路径，找到（类和包）所有的节点，构成一棵棵树
		List<Tree<PackageOrClass>> list = Tree.buildTree("D:\\apktool\\AdAway.apk");
		
		/*
		for (int i=0;i<list.size();i++) {
			list.get(i).pre_order();
			
		}
		*/
		
		// 第二步，深搜遍历树，将（类）叶子节点找到，返回叶子节点组成的动态数组
		List<String> classList = Tree.findAllClass(list);
		
		// 第三步，遍历树，将所有树中的点构成一个图
		Graph<PackageOrClass> graph = new Graph<>(list);
		long begin = System.currentTimeMillis();
		System.out.println(begin);
		
		// 第四步，启动线程，分析每个smali文件（目前只完成了分析三种文件的，还差父子和兄弟关系的建立）
		graph.analyseAllSmaliFile(list, classList, graph);
		
		// 这里先测试第四步的正确性,我的测试思路是看单线程和多线程的操作后，随机找一个package看结果是否一样
		
		// 重新走第三步，遍历树，将所有树中的点构成一个图
		//Graph<PackageOrClass> graphSingle = new Graph<>(list);
		
		// 重新第四步
		//graph.analyseAllSmaliFileSingle(list, classList, graphSingle);
		
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
		
		
		
		
		
		
	}

}
