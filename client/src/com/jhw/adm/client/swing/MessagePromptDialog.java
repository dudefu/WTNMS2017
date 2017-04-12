package com.jhw.adm.client.swing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageConstant;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.MonitorParamModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.system.TimeConfig;

public class MessagePromptDialog extends JDialog{
	private JPanel centerPnl;
	private ImagePanel mainPnl = null;
	private JLabel label = new JLabel();
	private JLabel statusLbl = new JLabel();
	private JButton button = new JButton("关闭");
	
	private boolean isReceive = false;
	private String message = "";
	
	private int timeout = 0 ;
	
	private String operateInfo = "";
	
	private boolean isContinue = true;
	
	private ImageRegistry imageRegistry;
	
	private RemoteServer remoteServer;
	
	private boolean isTopo = false; 
	private boolean isSyn = false;
	
	//当isEnd为true时，表示对话框不再显示返回的消息
	private boolean isEnd = false;
	
	private MonitorParamModel monitorParamModel ;
	
	/**
	 * 针对参数配置时的情况
	 * @param viewPart
	 * @param operate
	 * @param imageRegistry
	 * @param remoteServer
	 */
	public MessagePromptDialog(ViewPart viewPart ,String operateInfo,ImageRegistry imageRegistry,RemoteServer remoteServer){
		super(ClientUtils.getRootFrame());
		this.operateInfo = operateInfo;
		this.imageRegistry = imageRegistry;
		this.remoteServer = remoteServer;
		timeout = getTimeout();
		
		init();
		
		this.setLocationRelativeTo(viewPart);
	}
	
	/**
	 * 针对参数配置时的情况，但是又需要另外定义的比一般参数配置的超时时间长的情况
	 * @param viewPart
	 * @param timeout
	 * @param operate
	 * @param imageRegistry
	 */
	public MessagePromptDialog(ViewPart viewPart ,int timeout,String operateInfo,ImageRegistry imageRegistry){
		super(ClientUtils.getRootFrame());
		this.timeout = timeout;
		this.operateInfo = operateInfo;
		this.imageRegistry = imageRegistry;
		init();
		
		this.setLocationRelativeTo(viewPart);
	}
	
	/**
	 * 针对拓扑刷新和同步操作多台设备的情况
	 * @param viewPart
	 * @param operate
	 * @param imageRegistry
	 * @param remoteServer
	 * @param isTopo
	 * @param isSyn
	 */
	public MessagePromptDialog(ViewPart viewPart ,String operateInfo,ImageRegistry imageRegistry,RemoteServer remoteServer,boolean isTopo,boolean isSyn){
		super(ClientUtils.getRootFrame());
		this.operateInfo = operateInfo;
		this.imageRegistry = imageRegistry;
		this.remoteServer =  remoteServer;
		this.isTopo = isTopo;
		this.isSyn = isSyn;
		timeout = getTimeout();
		
		init();
		
		this.setLocationRelativeTo(viewPart);
	}
	
	public MessagePromptDialog(ViewPart viewPart ,String operateInfo,ImageRegistry imageRegistry,RemoteServer remoteServer,int operate){
		super(ClientUtils.getRootFrame());
		this.operateInfo = operateInfo;
		this.imageRegistry = imageRegistry;
		timeout = getTimeout(operate);
		
		init();
		
		this.setLocationRelativeTo(viewPart);
	}
	
	/**
	 * 初始化对话框
	 */
	private void init(){
		isEnd = false;
		
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(new JLabel("提示"));
		topPnl.setOpaque(false);
		
		label.setText( "正在" + operateInfo);
		label.setHorizontalAlignment(JLabel.RIGHT);
		centerPnl = new JPanel();

		JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPnl.setOpaque(false);
		bottomPnl.add(button);
		button.setEnabled(false);
		
		Image image = imageRegistry.getImage(ApplicationConstants.MESSAGE_PROMPT);
		mainPnl = new ImagePanel(image);
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(topPnl,BorderLayout.NORTH);
		mainPnl.add(centerPnl,BorderLayout.CENTER);
		mainPnl.add(bottomPnl,BorderLayout.SOUTH);
		mainPnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		mainPnl.setOpaque(false);
		centerPnl.setOpaque(false);
		
		this.getContentPane().add(mainPnl);
		this.setModal(true);
		this.setUndecorated(true);
		this.setSize(300, 100);
		
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		new Thread(new Runnable(){
			public void run(){
				while(isContinue){
					display();
					if (!isTopo && !isSyn){
						isContinue = false;
					}
				}
			}
		}).start();
	}
	
	/**
	 * 显示操作的进度信息
	 */
	private void display(){
		statusLbl.setPreferredSize(new Dimension(150,20));
		
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(label,BorderLayout.CENTER);
		centerPnl.add(statusLbl,BorderLayout.EAST);
		
		
		label.setText( "正在" + operateInfo);
		statusLbl.setForeground(Color.BLACK);
		statusLbl.setHorizontalAlignment(JLabel.LEADING);
		statusLbl.setText("");
		int time = 0;
		int times = 0 ;
		String str = "";
		isReceive = false;
		while(time < timeout){
			if (isReceive){
				setStatusLbl();
//				button.setEnabled(true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			str = str + "...";
			statusLbl.setText(str);
				
			time = time + 500;
			
			times = times + 1;
			if (times > 3){
				times = 0;
				str = "";
				statusLbl.setText(str);
				continue;
			}
		}
		
		statusLbl.setForeground(new Color(231,88,20));
		statusLbl.setHorizontalAlignment(JLabel.CENTER);
		
		label.setText("");
		statusLbl.setText(operateInfo + "超时");
		centerPnl.removeAll();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(statusLbl,BorderLayout.CENTER);
		statusLbl.setHorizontalAlignment(JLabel.CENTER);
		
		isContinue = false;
		button.setEnabled(true);
		
		isEnd = true;
		
		if (isTopo){
			//向服务端发消息,拓扑发现的时候，如果客户端超时，发送一个消息给服务器端，用于把REFRESHING还原成false
			remoteServer.getDataCacheService().resettingTopo();
		}
		if (isSyn){
			//向服务端发消息,同步的时候，如果客户端超时，发送一个消息给服务器端，用于把SYNING还原成false
			remoteServer.getDataCacheService().resettingSyn();
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				mainPnl.revalidate();
			}
		});
	}
	
	/**
	 * 对状态标签的现实
	 */
	private void setStatusLbl(){
		statusLbl.setForeground(new Color(35,175,98));
		statusLbl.setHorizontalAlignment(JLabel.CENTER);
		label.setText("");
		statusLbl.setText("");
		statusLbl.setText(message);
		
		centerPnl.removeAll();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(statusLbl,BorderLayout.CENTER);
	}
	
	/**
	 * 得到操作结束的消息
	 * @param message
	 */
	public synchronized void setMessage(String message){
		if (isEnd){
			return;
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.message = message;
		isReceive = true;
		isContinue = false;
		setStatusLbl();
		
		isEnd = true;
		
//		button.setEnabled(true);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				button.setEnabled(true);
				mainPnl.revalidate();
			}
		});
	}
	
	/**
	 * 得到某个设备节点发现时返回的消息
	 * 针对拓扑发现或同步多个设备时。
	 * @param message
	 */
	public synchronized void setNodeMessage(String message){
		this.message = message;
		isReceive = true;
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				mainPnl.revalidate();
			}
		});
	}
	
	public synchronized void setStopMessage(String message){
		if (isEnd){
			return;
		}
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.message = message;
		isReceive = true;
		isContinue = false;
		setStatusLbl();
		button.setEnabled(true);
		
		isEnd = true;
		
		statusLbl.setForeground(new Color(231,88,20));
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				mainPnl.revalidate();
			}
		});
	}
	
	private int getTimeout(int operate){
		int maxTime = 0;
		switch(operate){
			case MessageConstant.SYN_SWITCH:
				this.isSyn = true;
				maxTime = monitorParamModel.getTimeConfig().getSynchoizeMaxTime();
				if (0 <= maxTime){
					maxTime = MessageConstant.TIMEOUT_SYN;
				}
				break;
			case MessageConstant.FINISH_TOPO:
				this.isTopo = true;
				maxTime = monitorParamModel.getTimeConfig().getTuopoMaxTime();
				if (0 <= maxTime){
					maxTime = MessageConstant.TIMEOUT_TOPOREFRESH;
				}
				break;
			case MessageConstant.SYN_CARRIER:
				maxTime = MessageConstant.TIMEOUT;
				break;
			case MessageConstant.UPGRADE_SWITCH:
				maxTime = MessageConstant.TIMEOUT_UPGRADE;
				break;
			case MessageConstant.UPGRADE_CARRIER:
				maxTime = MessageConstant.TIMEOUT_UPGRADE;
				break;
		}
		
		return maxTime;
	}
	
	/**
	 * 得到超时时间，当没有从数据库中得到值或得到的值为0时，取文件MessageConstant中相应的值
	 * @return
	 */
	private int getTimeout(){
		int time = 0;
		List<TimeConfig> list = null;
		try{
			list = (List<TimeConfig>)remoteServer.getService().findAll(TimeConfig.class);
		}
		catch(Exception e){
			list = null;
		}
		
		if (null == list || list.size() < 1){
			if (isTopo == false && isSyn == false){
				time = MessageConstant.TIMEOUT;
			}
			else{
				if (isTopo){
					time = MessageConstant.TIMEOUT_TOPOREFRESH;
				}
				if (isSyn){
					time = MessageConstant.TIMEOUT_SYN;
				}
			}
			
			return time;
		}
		if (isTopo == false && isSyn == false){
			time = list.get(0).getParmConfigMaxTime() * 1000;
			if (0 == time){
				time = MessageConstant.TIMEOUT;
			}
		}
		else{
			if (isTopo){
				time = list.get(0).getTuopoMaxTime()* 1000;
				if (0 == time){
					time = MessageConstant.TIMEOUT_TOPOREFRESH;
				}
			}
			if (isSyn){
				time = list.get(0).getSynchoizeMaxTime()* 1000;
				if (0 == time){
					time = MessageConstant.TIMEOUT_SYN;
				}
			}
		}
		return time;
	}
	
	private void close(){
		this.dispose();
	}
}