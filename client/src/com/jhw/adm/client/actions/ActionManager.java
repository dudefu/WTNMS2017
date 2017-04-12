package com.jhw.adm.client.actions;

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Resource;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.views.ViewPart;

@Component(ActionManager.ID)
public class ActionManager implements BeanFactoryAware {

	public ActionMap getActionMap(ViewPart viewPart) {
		Class<?> actionsClass = viewPart.getClass();
		String viewPartName = viewPart.getBeanName();
		DesktopActionMap actionMap = new DesktopActionMap();
		int userRoleCode = clientModel.getCurrentUser().getRole().getRoleCode();
		
		// Annotation Actions
		for (Method m : actionsClass.getDeclaredMethods()) {
			ViewAction action = m.getAnnotation(ViewAction.class);
			if (action != null) {
				String methodName = m.getName();
				String actionName = action.name().length() == 0 ? methodName
						: action.name();
				String icon = action.icon();
				int actionRoleCode = action.role();
				String text = action.text();
				String textKey = text + "_action_text";
				String desc = action.desc();

				DelegateAction delegateAction = (DelegateAction)beanFactory.getBean(DelegateAction.ID);//new DelegateAction(viewPart, m);
				delegateAction.setTarget(viewPart);
				delegateAction.setActionMethod(m);
				delegateAction.setDescription(desc);
				ImageIcon imageIcon = imageRegistry.getImageIcon(icon);
				
				String defKey = String.format("%s_%s_action_text", viewPartName, actionName);
				String actionText = actionName;
				
				if (localizationManager.contains(textKey)) {
					actionText = localizationManager.getString(textKey);
				} else if (localizationManager.contains(defKey)) {
					actionText = localizationManager.getString(defKey);
				} else {
					actionText = localizationManager.getString(actionName + "_action_text");
				}
				
				delegateAction.setEnabled(actionEnabled(userRoleCode, actionRoleCode));
				delegateAction.putValue(Action.NAME, actionText);
				delegateAction.putValue(Action.SMALL_ICON, imageIcon);
				actionMap.put(actionName, delegateAction);
			}
		}
		
		// Declared Actions
		String actionMapName = viewPartName + "ActionMap";
		if (beanFactory.containsBean(actionMapName)) {
			Map viewActionMap = (Map)beanFactory.getBean(actionMapName);
			
			for (Object key : viewActionMap.keySet()) {
				Action action = (Action)viewActionMap.get(key);
				if (actionMap.get(key) == null) {
					actionMap.put(key, action);
				}
			}
		}
		
		return actionMap;
	}
	
	private boolean actionEnabled(int userRoleCode, int actionRoleCode) {
		boolean enabled = userRoleCode <= actionRoleCode;
		
		return enabled;
	}
	
	public ButtonFactory getButtonFactory(ViewPart viewPart) {
		ActionMap actionMap = getActionMap(viewPart);
		ButtonFactory buttonFactory = new ButtonFactory(actionMap);
		buttonFactory.setImageRegistry(imageRegistry);
		buttonFactory.setLocalizationManager(localizationManager);
		return buttonFactory;
	}
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	private static final Logger LOG = LoggerFactory.getLogger(ActionManager.class);
	private BeanFactory beanFactory;
	public static final String ID = "actionManager";
}