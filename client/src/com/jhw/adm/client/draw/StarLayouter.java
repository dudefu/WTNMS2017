package com.jhw.adm.client.draw;

/**
 * 几何
 * <br>
 * 1. 如果曲线的切于A,B两点的切线相交于P点,那么P点称为直线AB关于该曲线的极点(pole),直线AB称为P点的极线(polar).
 * <br>
 * 2. 笛卡尔坐标系 (Cartesian coordinates) 就是直角坐标系和斜角坐标系的统称。
 * <br>
 * 3. 笛卡尔坐标，它表示了点在空间中的位置，但却和直角坐标有区别，两种坐标可以相互转换。
 *    举个例子：某个点的笛卡尔坐标是493 ,454, 967，
 *    那它的X轴坐标就是4+9+3=16，Y轴坐标是4+5+4=13，Z轴坐标是9+6+7=22，因此这个点的直角坐标是(16, 13, 22) 
 */
public class StarLayouter {
	public StarLayouter(MultiwayTree tree) {
		this.tree = tree;
	}
	
	/**
	 * 布局
	 */
	public void layout() {
		MultiwayTree.Node root = tree.getRoot();
	}
	
//	private void caculateRoot
	private MultiwayTree tree;
}