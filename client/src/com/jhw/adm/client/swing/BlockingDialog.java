package com.jhw.adm.client.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import com.jhw.adm.client.util.ClientUtils;

public class BlockingDialog extends JDialog {
	
	public BlockingDialog() {
		super(ClientUtils.getRootFrame());
		setModal(true);
		setSize(300, 40);
		setUndecorated(true);
		setResizable(false);
		setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		createContents(contentPanel);
		add(contentPanel, BorderLayout.CENTER);
	}
	
	private void createContents(JPanel parent) {
		parent.setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		createContentPanel(contentPanel);

		parent.add(contentPanel, BorderLayout.CENTER);
	}
	
	private void createContentPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1));
//		System.out.println(UIManager.get("ProgressBar.repaintInterval"));//50
//		System.out.println(UIManager.get("ProgressBar.cycleTime"));//3000
		UIManager.put("ProgressBar.repaintInterval", new Integer(10));
		UIManager.put("ProgressBar.cycleTime", new Integer(1000));
		progressBar = new JProgressBar(1, 100);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		parent.add(progressBar);
	}
	
	public void describe(String text) {
		progressBar.setString(text);
	}
	
	private JProgressBar progressBar;
	private static final long serialVersionUID = -4433859874182308345L;
}