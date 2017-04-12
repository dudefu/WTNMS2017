package com.jhw.adm.client.ui;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.views.PartEvent;
import com.jhw.adm.client.views.PartListener;
import com.jhw.adm.client.views.ViewPart;

@Component(DesktopModel.ID)
public class DesktopModel {
	
	@PostConstruct
	protected void initialize() {
		openedViewPart = new HashMap<String, ViewPart>();
		propertySupport = new PropertyChangeSupport(this);
		messageColor = Color.BLACK;
		errorMessageColor = Color.RED;
	}

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners()) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(l);
                break;
            }
        }
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners(propertyName)) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(propertyName, l);
                break;
            }
        }
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertySupport.getPropertyChangeListeners();
    }

	public Color getMessageColor() {
		return messageColor;
	}
	public void setMessageColor(Color messageColor) {
		this.messageColor = messageColor;
	}
	public Color getErrorMessageColor() {
		return errorMessageColor;
	}
	public void setErrorMessageColor(Color errorMessageColor) {
		this.errorMessageColor = errorMessageColor;
	}
	
	public void addViewPart(ViewPart viewPart) {
		viewPart.addPartListener(new PartListener() {
			@Override
			public void partClosed(PartEvent event) {
				removeViewPart(((ViewPart)event.getSource()).getBeanName());
			}
			@Override
			public void partOpened(PartEvent event) {			
			}
		});
		openedViewPart.put(viewPart.getBeanName(), viewPart);
//		firePropertyChange(PROP_MESSAGE, StringUtils.EMPTY, viewPart.getTitle());
	}
	
	public void removeViewPart(String beanName) {
		LOG.debug(String.format("removeViewPart: %s", beanName));
		openedViewPart.remove(beanName);
	}
	
	public boolean containsViewPart(String beanName) {
		return openedViewPart.containsKey(beanName);
	}
	
	public ViewPart getViewPart(String beanName) {
		return openedViewPart.get(beanName);
	}
	
	private Map<String, ViewPart> openedViewPart;	
	private Color messageColor;
	private Color errorMessageColor;
//	private String message;
	private PropertyChangeSupport propertySupport;
//	public static final String PROP_MESSAGE = "PROP_MESSAGE";
	public static final String ID = "desktopModel";
	private static final Logger LOG = LoggerFactory.getLogger(DesktopModel.class);
}