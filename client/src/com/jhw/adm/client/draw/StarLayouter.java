package com.jhw.adm.client.draw;

/**
 * ����
 * <br>
 * 1. ������ߵ�����A,B����������ཻ��P��,��ôP���Ϊֱ��AB���ڸ����ߵļ���(pole),ֱ��AB��ΪP��ļ���(polar).
 * <br>
 * 2. �ѿ�������ϵ (Cartesian coordinates) ����ֱ������ϵ��б������ϵ��ͳ�ơ�
 * <br>
 * 3. �ѿ������꣬����ʾ�˵��ڿռ��е�λ�ã���ȴ��ֱ������������������������໥ת����
 *    �ٸ����ӣ�ĳ����ĵѿ���������493 ,454, 967��
 *    ������X���������4+9+3=16��Y��������4+5+4=13��Z��������9+6+7=22�����������ֱ��������(16, 13, 22) 
 */
public class StarLayouter {
	public StarLayouter(MultiwayTree tree) {
		this.tree = tree;
	}
	
	/**
	 * ����
	 */
	public void layout() {
		MultiwayTree.Node root = tree.getRoot();
	}
	
//	private void caculateRoot
	private MultiwayTree tree;
}