package com.jhw.adm.client.swing;

import java.awt.Cursor;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;



public class CommonTree extends JTree{

	private QueryDataInterface dataInterface = null;
	
	public CommonTree(){
		super();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("金宏威");
		
		this.setModel(new CommonTreeModel(root));
		
		// 设置树的选择模式
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// 设置树的选择事件
		this.addTreeSelectionListener(new CommonTreeSelectionListener());
	}
	public CommonTree(QueryDataInterface dataInterface){
		super();
		this.dataInterface = dataInterface;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("金宏威");
		
		this.setModel(new CommonTreeModel(root));
		
		// 设置树的选择模式
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// 设置树的选择事件
		this.addTreeSelectionListener(new CommonTreeSelectionListener());
	}
	
	
	
	class CommonTreeModel extends DefaultTreeModel{

		public CommonTreeModel(TreeNode root) {
			super(root);
			// TODO Auto-generated constructor stub
			
			createNodes((DefaultMutableTreeNode)root);
		}

		private void createNodes(DefaultMutableTreeNode top) {
			DefaultMutableTreeNode category = null;
			DefaultMutableTreeNode book = null;

			book = new DefaultMutableTreeNode("192.168.1.1:IETH802");
			top.add(book);

			category = new DefaultMutableTreeNode("测试室");
			top.add(category);

			book = new DefaultMutableTreeNode("192.168.1.215:IETH802");
			category.add(book);

			book = new DefaultMutableTreeNode("192.168.1.71:IETH802");
			category.add(book);

			book = new DefaultMutableTreeNode("192.168.1.75:IETH802");
			category.add(book);

			category = new DefaultMutableTreeNode("研发部");
			top.add(category);

			book = new DefaultMutableTreeNode("192.168.1.216:IETH802");
			category.add(book);

			DefaultMutableTreeNode subCategory = new DefaultMutableTreeNode("软件开发部");
			category.add(subCategory);
			
			book = new DefaultMutableTreeNode("192.168.12.21:IETH802");
			subCategory.add(book);
			
			book = new DefaultMutableTreeNode("192.168.12.22:IETH802");
			subCategory.add(book);
			
			book = new DefaultMutableTreeNode("192.168.12.23:IETH802");
			subCategory.add(book);
			
			book = new DefaultMutableTreeNode("192.168.12.24:IETH802");
			subCategory.add(book);
			subCategory.add(book);

			subCategory = new DefaultMutableTreeNode("硬件开发部");
			category.add(subCategory);
			
			book = new DefaultMutableTreeNode("192.168.12.10:IETH802");
			subCategory.add(book);
			
			book = new DefaultMutableTreeNode("192.168.12.11:IETH802");
			subCategory.add(book);
			
			book = new DefaultMutableTreeNode("192.168.12.12:IETH802");
			subCategory.add(book);
		}
		private static final long serialVersionUID = 1L;
		
	}
	
	
	
	
	
	
	
	class CommonTreeSelectionListener implements TreeSelectionListener{
		JTree tree = null;
		public CommonTreeSelectionListener(){
			
		}
		public void valueChanged(TreeSelectionEvent e){
			tree = (CommonTree)e.getSource();
			DefaultMutableTreeNode selectionNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

			if (selectionNode == null){
				return;
			}
			
			tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			if (null != dataInterface){
				dataInterface.queryData(selectionNode);
			}
		}
	}
}
