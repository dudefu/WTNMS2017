package com.jhw.adm.client.actions;

import static com.jhw.adm.client.core.ActionConstants.CLOSE;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;

import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.swing.JCloseButton;

/**
 * 按钮工厂，在新建按钮的同时设置好Action。
 */
public class ButtonFactory {

	public ButtonFactory(ActionMap actionMap) {
		this.actionMap = actionMap;
	}

	public JCloseButton createCloseButton() {
		JCloseButton closeButton = new JCloseButton(localizationManager.getString(CLOSE),
				imageRegistry.getImageIcon(ButtonConstants.CLOSE));
		return closeButton;
	}

	public JButton createSaveButton() {
		return createButton(ActionConstants.SAVE);
	}

	public JButton createQueryButton() {
		return createButton(ActionConstants.QUERY);
	}

	public JButton createButton(String actionName) {
		JButton button = new JButton();
		Action action = actionMap.get(actionName);
		button.setAction(action);
		return button;
	}

	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}
	public void setImageRegistry(ImageRegistry imageRegistry) {
		this.imageRegistry = imageRegistry;
	}
	public LocalizationManager getLocalizationManager() {
		return localizationManager;
	}
	public void setLocalizationManager(LocalizationManager localizationManager) {
		this.localizationManager = localizationManager;
	}

	private ActionMap actionMap;
	private ImageRegistry imageRegistry;
	private LocalizationManager localizationManager;
}
