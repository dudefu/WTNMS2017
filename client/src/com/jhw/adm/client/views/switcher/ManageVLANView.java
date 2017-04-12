package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;

@Component(ManageVLANView.ID)
@Scope(Scopes.DESKTOP)
public class ManageVLANView extends ViewPart {
	private static final long serialVersionUID = 1L;

	public static final String ID = "manageVLANView";
			
	private JLabel portLbl = new JLabel("端口");
	private JComboBox portCombox = new JComboBox();
	
	private JLabel portMarkLbl = new JLabel("标识");
	private JComboBox portMarkCombox = new JComboBox();
	
	private JLabel priorityLbl = new JLabel("优先级");
	private JComboBox priorityCombox = new JComboBox();

	private JPanel cards;
	private CardLayout cardLayout;
	private JPanel listPanel;
	private static final String LIST_CARD = "LIST_CARD";
	private static final String DETAIL_CARD = "DETAIL_CARD";
	
	private static final String[] PORTMARK = {"","Untag","Tag"};
	private static final String[] PRIORITYLIST = {"0","1","2","3","4","5","6","7"};
	
	//端口数并不是一定的
	private String[] portList = {"1","2","3","4","5","6","7","8"};
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@PostConstruct
	protected void initialize() {
		setTitle("VLAN管理");
		setViewSize(800, 480);
		setLayout(new BorderLayout());		

		cardLayout = new CardLayout();
		cards = new JPanel(cardLayout);
		createContents(cards);
		add(cards, BorderLayout.CENTER);
	}
	
	private void createContents(JPanel cards) {	

		listPanel = new JPanel();
		createVLANList(listPanel);
		
//		addingPanel = new JPanel();
//		createAdding(addingPanel);
		JPanel detialPanel = new JPanel();
		createVLANDetail(detialPanel);
		
        cards.add(listPanel, LIST_CARD);
        cards.add(detialPanel, DETAIL_CARD);
	}
	
	private void createVLANList(JPanel parent) {
		parent.setLayout(new BorderLayout());
        
		JPanel vlanListPanel = new JPanel(new BorderLayout());
		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JButton addButton = new JButton("明细",imageRegistry.getImageIcon(ButtonConstants.DETAIL));
		toolBar.add(addButton);
		toolBar.add(new JButton("删除", imageRegistry.getImageIcon(ButtonConstants.DELETE)));
		
		JButton closetButton = createCloseButton();
		toolBar.add(closetButton);
		this.setCloseButton(closetButton);

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cards, DETAIL_CARD);
			}			
		});
		parent.add(toolBar, BorderLayout.PAGE_END);
		
		String[] columnNames = { "VLAN ID", "VLAN 名称" };
		String[][] data = new String[][] {
				{ "1", "测试室" },
				{ "2", "会议室" },
				{ "3", "培训室" },
				{ "4", "人力资源部" },
				{ "5", "品牌部" },
				{ "6", "战略合作部" },
				{ "7", "通信产品部" },
				{ "8", "财务审计部" },
				{ "9", "测试部" },
				{ "10", "项目管理部" },
				{ "11", "销售管理部" },
				{ "12", "综合系统部" },
				{ "13", "客户工程部" },
				{ "14", "工控技术部" },
				{ "15", "工控市场部" },
				{ "16", "系统集成部" },
				{ "17", "通信产品部" }};
		JXTable table = new JXTable(data, columnNames);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 1));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);	
		table.setHighlighters(HighlighterFactory.createAlternateStriping(2));
				
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);
		
		vlanListPanel.add(scrollTablePanel, BorderLayout.CENTER);
		scrollTablePanel.setBorder(BorderFactory.createTitledBorder("VLAN 列表"));
		//
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(SwingConstants.TOP);
		
		
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEADING));
		wrapper.setBorder(BorderFactory.createTitledBorder("添加VLAN"));
		
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);

		JFormattedTextField idField = new JFormattedTextField(integerFormat);
		idField.setColumns(15);
		idField.setText("12");
		wrapper.add(new JLabel("VLAN ID"));
		wrapper.add(idField);

		wrapper.add(new JLabel("VLAN 名称"));
		JTextField nameField = new JTextField("会议室", 25);
		nameField.setColumns(20);
		wrapper.add(nameField);
		wrapper.add(new JButton("添加",imageRegistry.getImageIcon(ButtonConstants.APPEND)));

		
		vlanListPanel.add(wrapper, BorderLayout.PAGE_START);
		parent.add(vlanListPanel, BorderLayout.CENTER);
	}
	
	private JButton createCloseButton() {
		JButton closeButton = new JButton(localizationManager.getString(com.jhw.adm.client.core.ActionConstants.CLOSE), 
				imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		return closeButton;
	}
	
	private void createVLANDetail(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		infoPanel.setBorder(BorderFactory.createTitledBorder("当前VLAN"));
		infoPanel.add(new JLabel("ID：2"));
		infoPanel.add(new JLabel(""));
		infoPanel.add(new JLabel("名称：会议室"));
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		JButton listButton = new JButton("返回", imageRegistry.getImageIcon(ButtonConstants.RETURN));
		toolPanel.add(new JButton("保存", imageRegistry.getImageIcon(ButtonConstants.SAVE)));
		toolPanel.add(listButton);
		
		JButton closetButton = new JButton("关闭",imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		toolPanel.add(closetButton);
		this.setCloseButton(closetButton);
		
		listButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cards, LIST_CARD);
			}			
		});
				
		
		JPanel detailListPanel = new JPanel();
		detailListPanel.setBorder(BorderFactory.createTitledBorder("VLAN详细信息"));
		createVLANDetailList(detailListPanel);
		
		JPanel treePanel = new JPanel();
		createTreePanel(treePanel);
		
		parent.add(infoPanel, BorderLayout.NORTH);
		parent.add(treePanel, BorderLayout.LINE_START);
		parent.add(detailListPanel, BorderLayout.CENTER);
		parent.add(toolPanel, BorderLayout.SOUTH);
	}
	
	private void createVLANDetailList(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		String[] columnNames = {"设备","端口","标识","优先级"};
		String[][] data = new String[][] {
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" },
				{ "192.168.10.2", "1", "Tag", "0" }};
		JXTable table = new JXTable(data, columnNames);
		JPanel tabelPanel = new JPanel(new BorderLayout(1, 1));
		tabelPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		tabelPanel.add(table, BorderLayout.CENTER);	
		table.setHighlighters(HighlighterFactory.createAlternateStriping(2));
				
		JScrollPane scrollTablePanel = new JScrollPane();
		scrollTablePanel.getViewport().add(table, null);
		scrollTablePanel.getHorizontalScrollBar().setFocusable(false);
		scrollTablePanel.getVerticalScrollBar().setFocusable(false);
		scrollTablePanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollTablePanel.getVerticalScrollBar().setUnitIncrement(30);

		parent.add(scrollTablePanel, BorderLayout.CENTER);
	}
	
	private void createTreePanel(JPanel parent) {
		parent.setLayout(new BorderLayout());

		JPanel toolPanel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(toolPanel, BoxLayout.Y_AXIS); 
		toolPanel.setLayout(boxlayout);
		toolPanel.add(Box.createRigidArea(new Dimension(0, 180)));
		toolPanel.add(new JButton("添加",imageRegistry.getImageIcon(ButtonConstants.APPEND)));
		toolPanel.add(new JButton("删除",imageRegistry.getImageIcon(ButtonConstants.DELETE)));
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(BorderFactory.createTitledBorder("VLAN选项"));
		createOptionPanel(optionPanel);
		
		parent.add(toolPanel, BorderLayout.LINE_END);
		parent.add(optionPanel, BorderLayout.CENTER);
	}
	
	private void createOptionPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel optionContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel optionPanel = new JPanel(new SpringLayout());
		optionContainer.add(optionPanel);

		portCombox.addItem("");
		for(int i = 0 ; i < portList.length ; i++){
			portCombox.addItem(portList[i]);
		}
		for(int j = 0 ; j < PRIORITYLIST.length ; j++){
			priorityCombox.addItem(PRIORITYLIST[j]);
		}
		for(int k = 0 ; k < PORTMARK.length ; k++){
			portMarkCombox.addItem(PORTMARK[k]);
		}
		
		optionPanel.add(portLbl);
		optionPanel.add(portCombox);
		
		optionPanel.add(portMarkLbl);
		optionPanel.add(portMarkCombox);
		portMarkCombox.setSelectedIndex(portMarkCombox.getItemCount() - 1);
		
		optionPanel.add(priorityLbl);
		optionPanel.add(priorityCombox);
		
		SpringUtilities.makeCompactGrid(optionPanel, 6, 1, 6, 6, 6, 6);
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("金宏威");
		createNodes(top);
		JTree tree = new JTree(top);		
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scrollTreeView = new JScrollPane(tree);
		
		parent.add(optionContainer, BorderLayout.LINE_END);
		parent.add(scrollTreeView, BorderLayout.CENTER);
		
		ImageIcon leafIcon = imageRegistry
			.getImageIcon(NetworkConstants.SWITCHER_TOOL);
		ImageIcon openIcon = imageRegistry
			.getImageIcon(NetworkConstants.SUBNET_TOOL);
		ImageIcon closedIcon = imageRegistry
			.getImageIcon(NetworkConstants.SUBNET_TOOL);
		
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setOpenIcon(openIcon);
			renderer.setClosedIcon(closedIcon);
			renderer.setLeafIcon(leafIcon);
			tree.setCellRenderer(renderer);
		}
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
	
	public JButton getCloseButton(){
		return new JButton();
	}
}