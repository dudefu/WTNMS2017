package com.jhw.adm.client.swing;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jhw.adm.client.util.ThreadUtils;

public class JAlarmButtonModel {

	private PropertyChangeSupport propertyChangeSupport;
	private ExecutorService executorService;
	
	public JAlarmButtonModel(){
		propertyChangeSupport = new PropertyChangeSupport(this);
		executorService = Executors.newSingleThreadExecutor(ThreadUtils
				.createThreadFactory(JAlarmButtonModel.class.getSimpleName()));
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
	
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		Runnable notify = new Runnable() {
			@Override
			public void run() {
				propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
			}
		};
		executorService.execute(notify);
	}
	
	public static final String LEFT_PARENTHESIS = "(";
	public static final String RIGHT_PARENTHESIS = ")";
	
	public static final String ALARM_TEXT = "ALARM_TEXT";
	public static final String ALARM_COLOR = "ALARM_COLOR";
	public static final String ALARM_TOOLTIP = "ALARM_TOOLTIP";
	
	private String newText;
	public void setText(String newValue){
		String oldText = this.newText;
		this.newText = newValue;
		firePropertyChange(ALARM_TEXT, oldText, newText);
	}
	
	private Color newColor;
	public void setColor(Color newColor){
		Color oldColor = this.newColor;
		this.newColor = newColor;
		firePropertyChange(ALARM_COLOR, oldColor, newColor);
	}
	
	private String toolTip;
	public void setToopTip(String newToolTip){
		String oldToolTip = this.toolTip;
		this.toolTip = newToolTip;
		firePropertyChange(ALARM_TOOLTIP, oldToolTip, newToolTip);
	}
	
	/*
	 * 提供一些方法用于多态
	 */
	public void setDefaultInfo(){
		//
	}
	public Color getDrawColor(){
		return Color.BLACK;
	}
	public void startFlash(){
		//
	}
	public void stopFlash(){
		//
	}
	public boolean isFlash(){
		return false;
	}
}
