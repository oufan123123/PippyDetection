package com.cn;

import java.util.List;

public class Test {
	
	public static void main(String[] args) throws InterruptedException {
		
		// ��һ��������apk·�����ҵ�����Ͱ������еĽڵ㣬����һ�ÿ���
		List<Tree<PackageOrClass>> list = Tree.buildTree("D:\\apktool\\AdAway.apk");
		
		/*
		for (int i=0;i<list.size();i++) {
			list.get(i).pre_order();
			
		}
		*/
		
		// �ڶ��������ѱ������������ࣩҶ�ӽڵ��ҵ�������Ҷ�ӽڵ���ɵĶ�̬����
		List<String> classList = Tree.findAllClass(list);
		
		// �������������������������еĵ㹹��һ��ͼ
		Graph<PackageOrClass> graph = new Graph<>(list);
		long begin = System.currentTimeMillis();
		System.out.println(begin);
		
		// ���Ĳ��������̣߳�����ÿ��smali�ļ���Ŀǰֻ����˷��������ļ��ģ�����Ӻ��ֵܹ�ϵ�Ľ�����
		graph.analyseAllSmaliFile(list, classList, graph);
		
		// �����Ȳ��Ե��Ĳ�����ȷ��,�ҵĲ���˼·�ǿ����̺߳Ͷ��̵߳Ĳ����������һ��package������Ƿ�һ��
		
		// �����ߵ������������������������еĵ㹹��һ��ͼ
		//Graph<PackageOrClass> graphSingle = new Graph<>(list);
		
		// ���µ��Ĳ�
		//graph.analyseAllSmaliFileSingle(list, classList, graphSingle);
		
		long end = System.currentTimeMillis();
		System.out.println(end - begin);
		
		
		
		
		
		
	}

}
