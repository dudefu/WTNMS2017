package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.views.SpringUtilities;
import com.jhw.adm.client.views.ViewPart;

@Component(ConfigureSwitcherUserView.ID)
public class ConfigureSwitcherUserView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle("交换机用户设置");
		setLayout(new BorderLayout());
		
		JPanel toolPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(new JButton("添加",imageRegistry.getImageIcon(ButtonConstants.APPEND)));
		toolBar.add(new JButton("修改",imageRegistry.getImageIcon(ButtonConstants.MODIFY)));
		toolBar.add(new JButton("删除",imageRegistry.getImageIcon(ButtonConstants.DELETE)));
		
		JToolBar searchBar = new JToolBar();
		searchBar.setFloatable(false);
		JTextField nameField = new JTextField(20);
		searchBar.add(nameField);
		searchBar.add(new JButton(imageRegistry.getImageIcon(NetworkConstants.SEARCH)));		

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 0;
		toolPanel.add(searchBar, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 10;
		c.ipady = 0;
		c.gridx = 1;
		c.gridy = 0;
		toolPanel.add(toolBar, c);

		add(toolPanel, BorderLayout.NORTH);
		
		JPanel userListPanel = new JPanel();
		JScrollPane scrollListPanel = new JScrollPane();
		JList userList = new JList();
		
		userListPanel.setBorder(BorderFactory
				.createTitledBorder("用户列表"));
		userListPanel.setLayout(new BorderLayout());

		scrollListPanel.getViewport().add(userListPanel, null);
		scrollListPanel.getHorizontalScrollBar().setFocusable(false);
		scrollListPanel.getVerticalScrollBar().setFocusable(false);
		scrollListPanel.getHorizontalScrollBar().setUnitIncrement(30);
		scrollListPanel.getVerticalScrollBar().setUnitIncrement(30);

		userList.setLayoutOrientation(JList.VERTICAL);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		DefaultListModel stubListModel = new DefaultListModel();
		stubListModel.addElement("liaoyijun");
		stubListModel.addElement("wuzhongwei");
		stubListModel.addElement("xiongbo");
		stubListModel.addElement("yangxiao");
		userList.setModel(stubListModel);

		userListPanel.add(userList, BorderLayout.CENTER);
		JPanel detail = new JPanel();
		createDetail(detail);
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scrollListPanel, detail);
		splitPanel.setResizeWeight(0.18);
		
		add(splitPanel, BorderLayout.CENTER);
		
		this.setViewSize(520, 400);
	}
	
	private void createDetail(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel container = new JPanel(new SpringLayout());
		parent.add(container);
		
		container.add(new JLabel("用户"));
		container.add(new JTextField("liaoyijun", 25));
		
		container.add(new JLabel("密码"));
		container.add(new JTextField("****", 15));
		
		JComboBox roleBox = new JComboBox(new String[] { "readonly", "readwrite", "none" });
		roleBox.setEditable(false);
		
		container.add(new JLabel("角色"));
		container.add(roleBox);

		container.add(new JLabel(""));
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		toolPanel.add(new JButton("保存",imageRegistry.getImageIcon(ButtonConstants.SAVE)));
		container.add(toolPanel);
		
		SpringUtilities.makeCompactGrid(container, 4, 2, 6, 6, 30, 10);
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureSwitcherUserView";
}