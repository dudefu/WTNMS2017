package com.jhw.adm.client.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

/**
 * 视图部件
 */
public abstract class ViewPart extends JPanel implements BeanNameAware {
	
	public ViewPart() {
		closeButtons = new ArrayList<JButton>();
		partListeners = new CopyOnWriteArrayList<PartListener>();
		propertySupport = new PropertyChangeSupport(this);
	}


    @Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    @Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners()) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(l);
                break;
            }
        }
    }

    @Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners(propertyName)) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(propertyName, l);
                break;
            }
        }
    }
    
	public void addPartListener(PartListener listener) {
		partListeners.add(listener);
	}
	
	public void removePartListener(PartListener listener) {
		partListeners.remove(listener);
	}

	public void notifyPartOpened(PartEvent event) {
		for (PartListener listener : partListeners) {
			listener.partOpened(event);
		}
	}
	
	public void notifyPartClosed(PartEvent event) {
		for (PartListener listener : partListeners) {
			listener.partClosed(event);
		}
	}
	


	/**
	 * 关闭视图
	 */
	public final void close() {
		notifyPartClosed(new PartEvent(this));
		dispose();
		LOG.info("ViewPart({}) close", getBeanName());
	}

	/**
	 * 释放视图占用的资源
	 */
	public void dispose() {
	}
	
	public List<JButton> getCloseButtons() {
		return closeButtons;
	}

	public void setCloseButton(JButton closeButton) {
		closeButtons.add(closeButton);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(String name) {
		beanName = name;
	}
	
	public void setViewSize(int width, int height) {
		this.viewWidth = width;
		this.viewHeight = height;
	}

	/**
	 * 视图的宽度
	 */
	public int getViewWidth() {
		return viewWidth;
	}

	/**
	 * @param viewWidth
	 *            the viewWidth to set
	 */
	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	/**
	 * 视图的高度
	 */
	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	/**
	 * 视图的标题
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String oldTitle = this.title;
		this.title = title;
		propertySupport.firePropertyChange(PROP_TITLE, oldTitle, this.title);
	}

	/**
	 * 视图在Spring容器里面的ID
	 */
	public String getBeanName() {
		return beanName;
	}

	public JFrame getRootFrame() {
		return rootFrame;
	}

	public void setRootFrame(JFrame rootFrame) {
		this.rootFrame = rootFrame;
	}

    private PropertyChangeSupport propertySupport;
	private JFrame rootFrame;
	private List<JButton> closeButtons;
	private List<PartListener> partListeners;
	private int viewWidth;
	private int viewHeight;
	private String title;
	private String beanName;
	private static final Logger LOG = LoggerFactory.getLogger(ViewPart.class);
	private static final long serialVersionUID = 1L;
	
	public static final String PROP_TITLE = "PROP_TITLE";
}