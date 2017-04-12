package com.jhw.adm.client.swing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jhw.adm.client.util.ThreadUtils;

public class JProgressBarModel {

	private final PropertyChangeSupport propertyChangeSupport;
	private final ExecutorService executorService;
	
	public JProgressBarModel(){
		
		propertyChangeSupport = new PropertyChangeSupport(this);
		executorService = Executors.newSingleThreadExecutor(ThreadUtils
				.createThreadFactory(JProgressBarModel.class.getSimpleName()));
		
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		for(PropertyChangeListener l : propertyChangeSupport.getPropertyChangeListeners()){
			if(l == listener){
				propertyChangeSupport.removePropertyChangeListener(listener);
				break;
			}
		}
	}
	
	public void removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		for(PropertyChangeListener l : propertyChangeSupport.getPropertyChangeListeners(propertyName)){
			if(l == listener){
				propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
				break;
			}
		}
	}
	
	public PropertyChangeListener[] getPropertyChangeListeners(){
		return propertyChangeSupport.getPropertyChangeListeners();
	}
	
	private void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		Runnable notify = new Runnable() {
			@Override
			public void run() {
				propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
			}
		};
		executorService.execute(notify);
	}
	
	/*
	 * N:normal,F:failure,T:timeout and so on.For example:保存设备侧失败|F
	 */
	public static final String NORMAL = "N";
	public static final String FAILURE = "F";
	public static final String TIMEOUT = "T";
	
	
	/**
	 * 用于更改ProgressBar的滚动模式
	 * @param determine
	 */
	public static final String DETERMINE_PROPERTY_NAME = "DETERMINE";
	public void setDetermine(Boolean determine){
		firePropertyChange(DETERMINE_PROPERTY_NAME, null, determine);
	}
	
	public static final String DETAIL_PROPERTY_NAME = "DETAIL";
	private String detail = "";
	public void setDetail(String newValue){
		String oldValue = this.detail;
		this.detail = newValue;
		firePropertyChange(DETAIL_PROPERTY_NAME, oldValue, this.detail);
	}
	
	public static final String PROGRESS_PROPERTY_NAME = "PROGRESS";
	private int progress = 0;
	public void setProgress(int newValue){
		int oldValue = this.progress;
		this.progress = newValue;
		this.firePropertyChange(PROGRESS_PROPERTY_NAME, oldValue, this.progress);
	}
	
	public static final String ENABLED_PROPERTY_NAME = "ENABLED";
	private boolean enabled = false;
	public void setEnabled(boolean newValue){
		boolean oldValue = this.enabled;
		this.enabled = newValue;
		this.firePropertyChange(ENABLED_PROPERTY_NAME, oldValue, this.enabled);
	}
}
