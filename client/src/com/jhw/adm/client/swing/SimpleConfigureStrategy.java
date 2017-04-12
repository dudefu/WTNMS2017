package com.jhw.adm.client.swing;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该strategy针对只与数据库有联系，与前置机无联系的操作
 * @author Administrator
 *
 */
public class SimpleConfigureStrategy implements
		MessageProcessorStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleConfigureStrategy.class);
	private JProgressBarModel model;
	public String operatorMessage = "";
	private static final int ONE = 1;

	public SimpleConfigureStrategy(String operatorMessage,JProgressBarModel model){
		this.model = model;
	}
	
	public void showNormalMessage(final String message){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setDetail(message + "|" + model.NORMAL);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));
			}
		});
	}
	
	public void showErrorMessage(final String errorMessage){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setDetail(errorMessage + "|" + model.FAILURE);
				model.setProgress(ONE);
				model.setEnabled(true);
				model.setDetermine(new Boolean(false));
			}
		});
	}
	
	@Override
	public void processorMessage() {
		//
	}

	@Override
	public void removeProcessor() {
		//
	}

	@Override
	public void dealTimeOut() {
		// do something
		
	}

}
