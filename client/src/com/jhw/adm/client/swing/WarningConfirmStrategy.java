package com.jhw.adm.client.swing;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 该strategy针对只与确认告警有关的操作
 * @author Administrator
 *
 */
public class WarningConfirmStrategy implements
		MessageProcessorStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(WarningConfirmStrategy.class);
	private JProgressBarModel model;
	public String operatorMessage = "";
	private static final int ONE = 1;

	public WarningConfirmStrategy(String operatorMessage,JProgressBarModel model){
		this.model = model;
		showInitializeDialog(operatorMessage);
	}
	
	private void showInitializeDialog(String operatorMessage) {
		this.operatorMessage = operatorMessage;
	}
	
	public void showNormalMessage(final int confirmNumber, final int unConfirmNumber){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				model.setDetail(operatorMessage + "共计:"
						+ (confirmNumber + unConfirmNumber) + ";新确认:" + (unConfirmNumber) + ";历史确认:"
						+ (confirmNumber) + "|" + model.NORMAL);
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
