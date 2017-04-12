package com.jhw.adm.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;

public class MessageDisplayDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JButton closeButton = new JButton("关闭");
	private final JLabel messageLabel = new JLabel();
	private final JLabel statusLabel = new JLabel();
	
	private JPanel statusPanel;
	private ImagePanel mainPanel; 
	
	private final ImageRegistry imageRegistry;
	private final String operatorMessage;
	
	public MessageDisplayDialog(String operatorMessage){
		super(ClientUtils.getRootFrame());
		this.operatorMessage = operatorMessage;
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.class, ImageRegistry.ID);
	}
	
	//初始化提示框
	public void initializeDialog(ViewPart viewPart){
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPanel.add(new JLabel(this.operatorMessage));
		topPanel.setOpaque(false);
		
		messageLabel.setText("请求中");
		messageLabel.setPreferredSize(new Dimension(150,20));
		messageLabel.setHorizontalAlignment(JLabel.RIGHT);
		messageLabel.setForeground(Color.BLACK);
		
		statusLabel.setPreferredSize(new Dimension(150,20));
		statusLabel.setHorizontalAlignment(JLabel.LEADING);
		statusLabel.setText("");
		
		statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setOpaque(false);
		statusPanel.add(messageLabel, BorderLayout.CENTER);
		statusPanel.add(statusLabel, BorderLayout.EAST);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);
		buttonPanel.add(closeButton);
		closeButton.setEnabled(false);
		
		Image image = imageRegistry.getImage(ApplicationConstants.MESSAGE_PROMPT);
		mainPanel = new ImagePanel(image);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(statusPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		this.getContentPane().add(mainPanel);
		this.setModal(true);
		this.setUndecorated(true);
		this.setSize(300, 100);
		
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		
		progressBarThread = new Thread(new ProgressBarThread());
		progressBarThread.start();
		this.setLocationRelativeTo(viewPart);
	}
	
	public void showNormalMessage(String normalMessage){
		if(progressBarThread.isAlive()){
			this.isProgressBar = false;
		}
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setForeground(new Color(35,175,98));
		messageLabel.setText("");
		statusLabel.setText("");
		messageLabel.setText(normalMessage);
		
		statusPanel.removeAll();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(messageLabel,BorderLayout.CENTER);
		this.closeButton.setEnabled(true);
	}
	
	public void showErrorMessage(String errorMessage){
		if(progressBarThread.isAlive()){
			this.isProgressBar = false;
		}
		
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setForeground(new Color(231,88,20));
		messageLabel.setText("");
		statusLabel.setText("");
		messageLabel.setText(errorMessage);
		
		statusPanel.removeAll();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(messageLabel,BorderLayout.CENTER);
		this.closeButton.setEnabled(true);
	}
	
	public void showSingleFinishMessage(String singleFinishMessage){
		if(progressBarThread.isAlive()){
			this.isProgressBar = false;
		}
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setForeground(new Color(35,175,98));
		messageLabel.setText("");
		statusLabel.setText("");
		messageLabel.setText(singleFinishMessage);
		
		statusPanel.removeAll();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(messageLabel,BorderLayout.CENTER);
	}
	
	//modifier:wuzhongwei  Date:2010.12.10
	public void showSingleFailMessage(String signalFinishMessage){
		if(progressBarThread.isAlive()){
			this.isProgressBar = false;
		}
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setForeground(new Color(231,88,20));
		messageLabel.setText("");
		statusLabel.setText("");
		messageLabel.setText(signalFinishMessage);
		
		statusPanel.removeAll();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(messageLabel,BorderLayout.CENTER);
	}
	
	public void closeDialog(){
		clear();
		this.dispose();
	}
	
	private void clear(){
		messageLabel.setText("");
		setStatusLabel("");
	}
	
	private void setStatusLabel(final String message){
		if(SwingUtilities.isEventDispatchThread()) {
			statusLabel.setText(message);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setStatusLabel(message);
				}
			});
		}
	}
	
	private Thread progressBarThread;
	private boolean isProgressBar = true;
	private static final int SLEEP_TIME = 500;
	private class ProgressBarThread implements Runnable{
		
		public ProgressBarThread(){
			isProgressBar = true;
		}
		
		@Override
		public void run() {
			String newMessage = "";
			int time = 0;
			while(isProgressBar){
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				
				time +=1;
				newMessage += "...";
				setStatusLabel(newMessage);
				
				if(time > 3){
					time = 0;
					newMessage = "";
					setStatusLabel(newMessage);
					continue;
				}
			}
		}
	}
}
