package com.jhw.adm.client.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.model.ClientModel;

@Component(MainMenuManager.ID)
public class MainMenuManager implements ApplicationContextAware {

	@PostConstruct
	public void initialize() {
		Resource resource = applicationContext.getResource(FIEL_NAME);
		if (resource != null) {
			try {
				menuDefinition = resource.getFile();
				canonicalPath = menuDefinition.getCanonicalPath();
				LOG.info(String.format("Loading resource from file [%s]", canonicalPath));
			} catch (IOException e) {
				LOG.error(String.format("load resource[%s] error", FIEL_NAME), e);
			}
		}
	}

	/**
	 * 生成客户端主菜单
	 */
	@SuppressWarnings("unchecked")
	public JMenuBar createMenuBar() {
		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			document = reader.read(menuDefinition);
		} catch (DocumentException e) {
			LOG.error(String.format("load XML Document[%s] error", canonicalPath), e);
		}
		Element menus = document.getRootElement();
		List<Element> level1Elements = menus.elements();
		menuBar = new JMenuBar();
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		JMenu menu = null;
		JMenuItem item = null;
		for (Element element : level1Elements) {
			menu = buildMenu(element);
			menuBar.add(menu);
			List<Element> level2Elements = element.elements();
			for (Element itemElement : level2Elements) {
				String elename = itemElement.getName();
				if (elename.equals("seperator")) {
					menu.addSeparator();
				} else if (elename.equals("menu")) {			
					buildLevel2Menu(menu, itemElement);
				} else {
					item = buildMenuItem(itemElement);
					menu.add(item);
				}
			}
		}

		return menuBar;
	}
	
	@SuppressWarnings("unchecked")
	private void buildLevel2Menu(JMenu menu, Element element) {
		JMenu level2menu = buildMenu(element);
		menu.add(level2menu);
		
		List<Element> level2Elements = element.elements();
		for (Element itemElement : level2Elements) {
			String elename = itemElement.getName();
			if (elename.equals("seperator")) {
				level2menu.addSeparator();
			} else {
				JMenuItem menuItem = buildMenuItem(itemElement);
				level2menu.add(menuItem);
			}
		}
	}

	/**
	 * 生成菜单子项
	 */
	private JMenuItem buildMenuItem(Element element) {
		String iname = element.elementText("name");
		String iicon = element.elementText("icon");
		String viewId = element.elementText("view");
		String actionId = element.elementText("action");
		String groupId = element.elementText("group");
		String role = element.elementText("role");
		
		int userRoleCode = clientModel.getCurrentUser().getRole().getRoleCode();
		int menuRoleCode = NumberUtils.toInt(role);
		
		boolean actionEnabled = actionEnabled(userRoleCode, menuRoleCode);
		
		String menuName = localization.getString(iname);
		JMenuItem menuItem = new JMenuItem(menuName);
		menuItem.setText(iname);
		ImageIcon icon = imageRegistry.getImageIcon(iicon);
		menuItem.setIcon(icon);
		
		if (actionId != null && actionId.length() > 0) {
			Action action = null;
			if (applicationContext.containsBean(actionId)) {
				action = (Action) applicationContext
						.getBean(actionId);
			} else {
				action = new DefaultAction();
			}
			action.putValue(Action.NAME, menuName);
			action.putValue(Action.SMALL_ICON, icon);
			action.setEnabled(actionEnabled);
			menuItem.setAction(action);
		} else if (viewId != null) {
			Action action = new DefaultAction();
			if (applicationContext.containsBean(ShowViewAction.ID)) {
				ShowViewAction showViewaction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
				showViewaction.setViewId(viewId);
				showViewaction.setGroupId(groupId);
				action = showViewaction;
			} else {
				action = new DefaultAction();
			}
			action.putValue(Action.NAME, menuName);
			action.putValue(Action.SMALL_ICON, icon);
			action.setEnabled(actionEnabled);
			menuItem.setAction(action);
		} else {
			menuItem.setEnabled(false);
		}
		
		return menuItem;
	}

	/**
	 * 生成菜单
	 */
	private JMenu buildMenu(Element element) {
		String name = element.attributeValue("name");
		String role = element.attributeValue("role");
		int userRoleCode = clientModel.getCurrentUser().getRole().getRoleCode();
		int menuRoleCode = NumberUtils.toInt(role);
		String menuName = localization.getString(name);
		JMenu menu = null;
		if (menuName == null || menuName.isEmpty()) {
			menu = new JMenu(name);
		} else {
			menu = new JMenu(menuName);
		}
		menu.setEnabled(actionEnabled(userRoleCode, menuRoleCode));
		return menu;
	}
	
	private boolean actionEnabled(int userRoleCode, int menuRoleCode) {
		boolean enabled = userRoleCode <= menuRoleCode;
		
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@javax.annotation.Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@javax.annotation.Resource(name=LocalizationManager.ID)
	private LocalizationManager localization;
	
	@javax.annotation.Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private File menuDefinition;

	private JMenuBar menuBar;
	private String canonicalPath;
	private ApplicationContext applicationContext;
	private static final Logger LOG = LoggerFactory
			.getLogger(MainMenuManager.class);
	private static final String FIEL_NAME = "conf/mainMenu.xml";
	public static final String ID = "mainMenuManager";

	private class DefaultAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null,
					"DefaultAction.actionPerformed()");
		}

		private static final long serialVersionUID = -1L;
	}
}