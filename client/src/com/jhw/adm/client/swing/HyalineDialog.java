package com.jhw.adm.client.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;


/**
 * 查询进度对话框
 * <p>
 * 使用方法:
 * <pre>
 *    HyalineDialog hyalineDialog = new HyalineDialog(viewPart);
 *    Runnable runnable = new Runnable() {
 *        public void run() {
 *            ....
 *        }
 *    }
 *    hyalineDialog.run(runnable);
 *    
 *    ViewPart...
 *    
 *    public void dispose() {
 *        super.dispose();
 *        hyalineDialog.dispose();
 *    }
 * </pre>
 * 
 * </p>
 */
public class HyalineDialog extends JDialog {

	private static final long serialVersionUID = -2184975387868965501L;
	private JProgressBar progressBar;
	private final boolean determine = true;
	private static final Logger LOG = LoggerFactory.getLogger(HyalineDialog.class);	
	private static final long MIN_MILLIS = 180;
	
	public HyalineDialog(ViewPart viewPart) {
		super(ClientUtils.getRootFrame());
		this.setModal(true);
		this.setResizable(false);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setSize(300, 40);
		this.createProcessor(this);
		this.setLocationRelativeTo(viewPart);
	}
	
	private void createProcessor(JDialog parent){
		parent.setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(new BorderLayout());
		
		parent.add(contentPanel, BorderLayout.CENTER);

		JPanel progressBarPanel = new JPanel();
		contentPanel.add(progressBarPanel, BorderLayout.CENTER);
		createProgressBarPanel(progressBarPanel);
		setString("正在查询...");
	}
	
	private void createProgressBarPanel(JPanel parent){
		parent.setLayout(new GridLayout(1, 1));
		UIManager.put("ProgressBar.repaintInterval", new Integer(10));
		UIManager.put("ProgressBar.cycleTime", new Integer(1000));
		progressBar = new JProgressBar(1, 100);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(this.determine);
		parent.add(progressBar);
	}
	
	public void run(final Runnable runnable) {
		open();
		Runnable reallyRun = new Runnable() {
			public void run() {
				try {
					long begin = System.currentTimeMillis();
					runnable.run();
					long end = System.currentTimeMillis();
					long runMillis = end - begin;
					LOG.info("后台任务运行时间: " + runMillis);
					if (runMillis < MIN_MILLIS) {
						Thread.sleep(MIN_MILLIS);
					}
				} catch(Throwable e) {
					LOG.error("后台任务运行出错", e);
				} finally {
					close();
				}
			}
		};
		Thread thread = new Thread(reallyRun);
		thread.start();
	}
	
	protected void open() {
		// 强制进入EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HyalineDialog.this.setVisible(true);
			}
		});
	}
	
	public void setString(String description){
		this.progressBar.setString(description);
	}
	
	protected void close() {
		// 强制进入EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HyalineDialog.this.setVisible(false);
			}
		});
	}
}
