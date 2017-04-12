package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SwitchRefreshInfoInter;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.SwitcherExplorerView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;

public class SwitchConfigView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "switchConfigView";
	
	private final JPanel centerPnl = new JPanel();
	
	private final JPanel northPnl = new JPanel();
	
	private final JPanel leftNorthPnl = new JPanel(new BorderLayout());
	private final JScrollPane leftNorthScrollPnl = new JScrollPane();
	
	private final JPanel centerNorthPnl = new JPanel();
	private final JButton addBtn = new JButton(">>"); 
	private final JButton delBtn = new JButton("<<");
	
	private final JPanel rightNorthPnl = new JPanel(new BorderLayout());
	private final JScrollPane rightNorthSrollPnl = new JScrollPane();
	private JTree tree;
	private DefaultTreeModel model ;
	private DefaultMutableTreeNode top;
	
	private final JPanel bottomPnl = new JPanel();
	private JButton okBtn;
	private JButton closeBtn;
	
	private final JDialog dialog ;
	
	private SwitchTopoNodeEntity switchTopoNodeEntity = null;
	
	private SwitchRefreshInfoInter refreshInter = null;
	
	//false:表示已经有实体，要调用update方法。true:表示没有实体，要调用save方法
	private final boolean isNew = true;
	
	private final ImageRegistry imageRegistry;

	private final EquipmentModel equipmentModel;
	
	private final SwitcherExplorerView explorerView;

	private final AdapterManager adapterManager;
	
	/**
	 * 设备浏览器监听事件
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				switchTopoNodeEntity =(SwitchTopoNodeEntity)adapterManager.getAdapter(
						equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
			}else{
				switchTopoNodeEntity = null;
			}
		}		
	};
	
	public SwitchConfigView(ViewPart viewPart,JDialog dialog,ImageRegistry imageRegistry
			,EquipmentModel equipmentModel){
		this.adapterManager = ClientUtils.getSpringBean(DesktopAdapterManager.ID);
		this.dialog = dialog;
		this.imageRegistry = imageRegistry;
		this.equipmentModel = equipmentModel;
		this.equipmentModel.addPropertyChangeListener(this.equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		this.explorerView = ClientUtils.getSpringBean(SwitcherExplorerView.ID);
		this.explorerView.setRemove(true);
		if (viewPart instanceof SwitchRefreshInfoInter){
			refreshInter = (SwitchRefreshInfoInter)viewPart;
		}
		init();
	}
	
	private void init(){
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.NORTH);
		this.add(bottomPnl,BorderLayout.CENTER);
		
		setResource();
	}
	
	private void initCenterPnl(){
		initNorthPnl();
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(northPnl,BorderLayout.NORTH);
	}
	
	private void initNorthPnl(){
		northPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(leftNorthPnl,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,0,0),0,0));
		panel.add(centerNorthPnl,new GridBagConstraints(1,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		panel.add(rightNorthPnl,new GridBagConstraints(2,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,10,0,0),0,0));
		northPnl.add(panel);
		
		//左边的树
		leftNorthScrollPnl.getViewport().add(explorerView);
		explorerView.setBorder(null);

		//右边的树
		rightNorthSrollPnl.getViewport().setBackground(Color.WHITE);
		rightNorthSrollPnl.getViewport().setLayout(new FlowLayout(FlowLayout.LEADING));
		top = new DefaultMutableTreeNode("");
		model = new DefaultTreeModel(top);
		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		rightNorthSrollPnl.getViewport().add(tree);
		tree.setCellRenderer(new TreeNodeRenderer());

		//中间的树
		centerNorthPnl.setLayout(new GridBagLayout());
		centerNorthPnl.add(addBtn,new GridBagConstraints(0,0,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		centerNorthPnl.add(delBtn,new GridBagConstraints(0,1,1,1,0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(40,0,0,0),0,0));
	}

	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okBtn = new JButton("确定",imageRegistry.getImageIcon(ButtonConstants.SAVE));
		closeBtn = new JButton("关闭", imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		bottomPnl.add(okBtn);
		bottomPnl.add(closeBtn);
	}
	
	private void setResource(){
		leftNorthScrollPnl.setBackground(Color.WHITE);
		rightNorthSrollPnl.setBackground(Color.WHITE);
		
		
		leftNorthScrollPnl.setPreferredSize(new Dimension(200,280));
		rightNorthSrollPnl.setPreferredSize(new Dimension(200,280));
		leftNorthPnl.add(leftNorthScrollPnl,BorderLayout.CENTER);
		rightNorthPnl.add(rightNorthSrollPnl,BorderLayout.CENTER);
		leftNorthPnl.setBorder(BorderFactory.createTitledBorder("源交换机"));
		rightNorthPnl.setBorder(BorderFactory.createTitledBorder("目的交换机"));
		
		addBtn.setActionCommand("addBtn");
		delBtn.setActionCommand("delBtn");
		ButtonActionListener actionListener = new ButtonActionListener();
		addBtn.addActionListener(actionListener);
		delBtn.addActionListener(actionListener);
		
		closeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		okBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				List<SwitchTopoNodeEntity> switchTopoNodeEntityList = getAllNodesData();
				if (null != refreshInter){
					refreshInter.refresh(switchTopoNodeEntityList);
					refreshInter = null;
					dispose();
				}
			}
		});
	}
	
	/**
	 * 为外部提供接口，通过传入的dataList，设置目的交换机
	 * @param dataList
	 */
	public void setAllNodesData(List<SwitchTopoNodeEntity> dataList){
		top.removeAllChildren();
		model.reload(top);
		if (dataList == null || dataList.size() < 1){
			return;
		}
		for(SwitchTopoNodeEntity switchTopoNodeEntity : dataList){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(switchTopoNodeEntity);
			model.insertNodeInto(node, top, top.getChildCount());
		}
		expandTree(tree);
		
		explorerView.setRemovedNodeEntityList(dataList);
		this.explorerView.needToRemoveSwitchTopoNode();
	}
	
	private void addNode(){
		if (null == switchTopoNodeEntity){
			return;
		}
		boolean bool = allowAddNodes(switchTopoNodeEntity.getIpValue());
		if (!bool){
			return;
		}

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(switchTopoNodeEntity);
		model.insertNodeInto(node, top, top.getChildCount());
		tree.fireTreeExpanded(new TreePath(top));
		
		//左边的树删除此节点
		explorerView.deleteNode();
		
		switchTopoNodeEntity = null;
	}
	
	private void deleteNode(){
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();  
		if (selectedNode == null || selectedNode.getParent() == null){
	        return;
	    }
		//删除指定节点   
        model.removeNodeFromParent(selectedNode);   
	    
	    List<NodeEntity> nodeEntitieList = new ArrayList<NodeEntity>();
	    nodeEntitieList.add((NodeEntity)selectedNode.getUserObject());
	    
	    //左边的树增加此节点
	    explorerView.addNode(nodeEntitieList);
//	    explorerView.deleteNode();
	    explorerView.getRemovedNodeEntityList().remove(selectedNode.getUserObject());
	}
	
	public void setDialog(JDialog dialog){

	}
	
	/**
	 * 判断是否允许增加节点，如果有重复的，则不增加
	 * @param ip
	 * @return
	 */
	private boolean allowAddNodes(String ip) {
		boolean bool = true;
		String ipAddr = "";
		Enumeration  breadthFirst = top.breadthFirstEnumeration(); 
		while(breadthFirst.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
			if (node.getUserObject() instanceof SwitchTopoNodeEntity){
				SwitchTopoNodeEntity object = (SwitchTopoNodeEntity)node.getUserObject();
				ipAddr = object.getIpValue();
			}
			else{
				ipAddr = node.getUserObject().toString();
			}
			
			if (ipAddr.equals(ip)){
				bool = false;
				break;
			}
		}
		
		return bool;
	}
	
	/**
	 * 遍历根节点下的所有节点
	 */
	private List<SwitchTopoNodeEntity> getAllNodesData() {
		List<SwitchTopoNodeEntity> treeDataList = new ArrayList<SwitchTopoNodeEntity>();
		
		Enumeration  breadthFirst = top.breadthFirstEnumeration(); 
		while(breadthFirst.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)breadthFirst.nextElement();
			if (node.getUserObject() instanceof SwitchTopoNodeEntity){
				SwitchTopoNodeEntity object = (SwitchTopoNodeEntity)node.getUserObject();
				treeDataList.add(object);
			}
		}
		
		return treeDataList;
	}
	
	@Override
	public void dispose(){
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		dialog.dispose();
	}
	
	private void expandTree(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root));
	}
	
	private void expandAll(JTree tree, TreePath parent) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
			}
		} // Expansion or collapse must be done bottom-up

		tree.expandPath(parent);
	}
	
	class ButtonActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getActionCommand().equals("addBtn")){
				addNode();
			}
			else if (e.getActionCommand().equals("delBtn")){
				deleteNode();
			}
		}
	}
	
	
	class TreeNodeRenderer extends DefaultTreeCellRenderer{
	    private static final long serialVersionUID = 8532405600839140757L;
	    
	    private final Icon firseLayerIcon = imageRegistry.getImageIcon(MainMenuConstants.SUBNET);
	    
	    private final Icon secondLayerIcon = imageRegistry.getImageIcon(MainMenuConstants.SWITCHER_TYPE);
	    
	    @Override
		public java.awt.Component getTreeCellRendererComponent(JTree tree,
	                                                  Object value,
	                                                  boolean sel,
	                                                  boolean expanded,
	                                                  boolean leaf,
	                                                  int row,
	                                                  boolean hasFocus){
	        super.getTreeCellRendererComponent(tree, value,sel,expanded,leaf,row,hasFocus);   
	        // 取得节点
	        DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
	        
	        // 取得路径
	        TreeNode[] paths = node.getPath();
	        
	        // 按路径层次赋予不同的图标和文本信息
	        if(paths.length == 1){
	            setIcon(firseLayerIcon);
	            this.setText("设备信息");
	        }
	        else if (paths.length == 2) {            
	            setIcon(secondLayerIcon);
	            SwitchTopoNodeEntity object = (SwitchTopoNodeEntity)((DefaultMutableTreeNode)value).getUserObject();
				this.setText(object.getIpValue());
	        }
	        
	        return this;        
	    }
	}
}
