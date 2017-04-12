package com.jhw.adm.client.draw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ��·�������˲���ʹ��
 */
public class MultiwayTree {	
	public MultiwayTree() {
		nodes = new LinkedList<Node>();
	}

	/**
	 * ���һ���ڵ�
	 */
	public Node addNode(Node node) {
		nodes.add(node);
		return node;
	}

	/**
	 * ��ȡ���ĸ��ڵ�
	 */
	public Node getRoot() {
		Node root = null;
		for(Node node : nodes) {
			if(node.getParent() == null) {
				root = node;
			}
		}
		
		return root;
	}
	
	private List<Node> nodes;
	
	/**
	 * ��·���Ľڵ�
	 */
	public static class Node {
		public Node(String name) {
			this.name = name;
			children = new ArrayList<Node>();
		}
		public Node getParent() {
			return parent;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		}
		public List<Node> getChildren() {
			return children;
		}
		public Node addChild(Node child) {
			children.add(child);
			child.setParent(this);
			return child;
		}
		public void setChildren(List<Node> children) {
			this.children = children;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
		public double getSize() {
			return size;
		}
		public void setSize(double size) {
			this.size = size;
		}
		public String getName() {
			return name;
		}
		private String name;
		private Node parent;
		private List<Node> children;
		private double size;
		private double x;
		private double y;
	}
}
