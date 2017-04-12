package com.jhw.adm.client.draw;

/**
 * 树形布局器
 */
public class TreeLayouter {
	public TreeLayouter(MultiwayTree tree) {
		this.tree = tree;
	}
	
	/**
	 * 布局
	 */
	public void layout() {
		currentY = initY;
		MultiwayTree.Node root = tree.getRoot();
		calculateDimensionX(root);
		double x = root.getSize()/2 + initX;
		calculateCoordinate(root, x);
	}

    /**
     * 计算每个节点的坐标
     */
    protected void calculateCoordinate(MultiwayTree.Node root, double x) {
        double sizeXofCurrent = root.getSize();

        root.setX(x);
        root.setY(currentY);
        currentY += distY;
        double lastX = x - sizeXofCurrent / 2;

        double sizeXofChild;
        double startXofChild;

        for (MultiwayTree.Node child : root.getChildren()) {
            sizeXofChild = child.getSize();
            startXofChild = lastX + sizeXofChild / 2;
            calculateCoordinate(child, startXofChild);
            lastX = lastX + sizeXofChild + distX;
        }

        currentY -= distY;
    }
    
    /**
     * 计算每个节点的尺寸
     */
    protected int calculateDimensionX(MultiwayTree.Node root) {
		int size = 0;
		int childrenNum = root.getChildren().size();
		if (childrenNum != 0) {
			for (MultiwayTree.Node child : root.getChildren()) {
				size += calculateDimensionX(child) + distX;
			}
		}
		size = Math.max(0, size - distX);
		root.setSize(size);
		
		return size;
	}

    /**
     * 同一层每个节点的横向距离
     */
	public int getDistX() {
		return distX;
	}

	public void setDistX(int distX) {
		this.distX = distX;
	}

    /**
     * 层与层之间的纵向距离
     */
	public int getDistY() {
		return distY;
	}

	public void setDistY(int distY) {
		this.distY = distY;
	}

    /**
     * 整个树的X
     */
	public int getInitX() {
		return initX;
	}

	public void setInitX(int initX) {
		this.initX = initX;
	}

    /**
     * 整个树的Y
     */
	public int getInitY() {
		return initY;
	}

	public void setInitY(int initY) {
		this.initY = initY;
	}

	private int distX = 60;
	private int distY = 100;
	private int initX;
	private int initY;
	private int currentY;
	private MultiwayTree tree;
}