package com.jhw.adm.comclient.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.BaseOperateService;
import com.jhw.adm.comclient.gprs.hongdian.service.HongdianService;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.TrapMonitor;
import com.jhw.adm.comclient.service.DeviceLogService;
import com.jhw.adm.comclient.service.HeartbeatService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.comclient.system.OIDConfig;
import com.jhw.adm.comclient.system.OIDList;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.servic.LoginServiceRemote;

/**
 * 
 * @author xiongbo
 * 
 */
public class LoginDialog extends JDialog {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	private LoginImagePanel loginImagePanel;
	private ConsolePanel consolePanel;
	//
	private HongdianService hongdianService;
	private TrapMonitor trapMonitor;
	private BaseOperateService baseOperateService;
	private MessageSend messageSend;
	private HeartbeatService heartbeatService;
	private DeviceLogService deviceLogService;

	public void init(Image backgroundImage, boolean flag) {
		this.flag = flag;

		setSize(530, 356);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setUndecorated(true);
		setModal(true);
		setTitle("前置机登录 ");
		setLayout(new BorderLayout());

		loginImagePanel.init(backgroundImage);
		createContents(loginImagePanel);
		add(loginImagePanel, BorderLayout.CENTER);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (StringUtils.isNotBlank(usernameField.getText())) {
					usernameField.selectAll();
				}
			}
		});
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	private void createContents(JPanel parent) {
		parent.setLayout(null);
		usernameField = new JTextField(16);
		usernameField.setText("WTFEP");
		passwordField = new JPasswordField(16);
		serverField = new JTextField(16);
		usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		serverField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		usernameField.setPreferredSize(new Dimension(10, 19));
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					logon();
				}
			}
		});
		passwordField.setPreferredSize(new Dimension(10, 19));
		passwordField.requestFocusInWindow();
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					logon();
				}
			}
		});

		serverField.setPreferredSize(new Dimension(10, 19));
		serverField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					logon();
				}
			}
		});
		if (flag) {
			serverField.setText(Configuration.readJNDI());
		} else {
			serverField.setEditable(false);
		}

		okButton = new JButton();
		cancelButton = new JButton();

		JLabel userLabel = new JLabel("编号");
		JLabel passworkLabel = new JLabel("密码");
		JLabel serverLabel = new JLabel("服务器");

		okButton.setMnemonic('L');
		okButton.setBackground(new Color(61, 151, 203));
		okButton.setText("登 录");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		cancelButton.setMnemonic('C');
		cancelButton.setBackground(new Color(61, 151, 203));
		cancelButton.setText("取 消");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});

		parent.add(usernameField);
		parent.add(passwordField);
		parent.add(serverField);

		parent.add(okButton);
		parent.add(cancelButton);

		layoutIt(userLabel, 93, 128);
		layoutIt(usernameField, 279, 157);

		layoutIt(passworkLabel, 93, 162);
		layoutIt(passwordField, 279, 191);

		layoutIt(serverLabel, 93, 195);
		layoutIt(serverField, 279, 225);

		layoutIt(okButton, 315, 267);
		layoutIt(cancelButton, 395, 267);
	}

	private void layoutIt(JComponent component, int x, int y) {
		component.setBounds(x, y, component.getPreferredSize().width, component.getPreferredSize().height);
	}

	// 登录
	private void logon() {
		if (isValids()) {
			// Connect to server verify
			int loginResult = loginServer();
			if (loginResult == 1 || (loginResult == -1 && !flag)) {
				closeCurrentUI();
				if (flag) {
					String ipPort = Configuration.removeBlank(serverField.getText());
					// First login, Weather need to modify JNDI
					if (!Configuration.jndi_ipPort.equals(ipPort)) {
						Configuration.writeJNDI(ipPort);
						Configuration.jndi_ipPort = ipPort;
						log.info("Write File:jndi.properties: " + ipPort);
					}
					//
					Configuration.fepCode = usernameField.getText().trim().toUpperCase();
					Configuration.fepPassword = passwordField.getText().trim();
					// Init System
					new Thread() {
						@Override
						public void run() {
							initSYS();
						}
					}.start();

					consolePanel.show();
					passwordField.setText("");
				} else {
					exit();
				}
			}
		}
	}

	/**
	 * -1-Can not connect the Server
	 * 
	 * 0-Login fail
	 * 
	 * 1-Login Success
	 * 
	 * @return
	 */
	private int loginServer() {
		String ipPort = Configuration.removeBlank(serverField.getText());

		CounterThread ct = new CounterThread(ipPort);
		ct.start();
		try {
			ct.join(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ct.interrupt();

		int result = ct.getResult();
		if (result == 0) {
			passwordField.setText(StringUtils.EMPTY);
			JOptionPane.showMessageDialog(LoginDialog.this, "前置机编号或密码错误.", "提示", JOptionPane.ERROR_MESSAGE);
		} else if (result == -1) {
			JOptionPane.showMessageDialog(LoginDialog.this, "网络或其他原因不能连接到服务端.", "提示", JOptionPane.ERROR_MESSAGE);
		}

		return result;
	}

	private class CounterThread extends Thread {
		private int result = -1;
		private String ipPort;

		public CounterThread(String ipPort) {
			this.ipPort = ipPort;
		}

		public int getResult() {
			return result;
		}

		public void run() {
			log.info("......Login...........................");
			InitialContext initialContext = null;
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.PROVIDER_URL, ipPort);
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
			try {
				initialContext = new InitialContext(env);
				LoginServiceRemote loginService = (LoginServiceRemote) initialContext.lookup("remote/LoginService");

				FEPEntity fepEntity = loginService.loginFEP(usernameField.getText().trim().toUpperCase(),
						passwordField.getText().trim());
				if (fepEntity != null) {
					result = 1;
					return;
				}
				result = 0;
				return;
			} catch (NamingException e) {
				log.error(e);
				result = -1;
				return;
			} finally {
				if (initialContext != null) {
					try {
						initialContext.close();
						initialContext = null;
					} catch (NamingException e) {
						e.printStackTrace();
					}
				}
				env.clear();
			}

		}
	}

	private void initSYS() {
		OIDList oidList = OIDConfig.readOIDConfig();
		if (oidList == null) {
			log.error("OID config is NULL.");
			exit();
		}
		log.info("OID config read Finish......");
		this.messageSend.init();
		log.info("JMS Send service init Finish......");
		baseOperateService.start();
		log.info("PLC Service Init Finish......");
		trapMonitor.startTrap();
		log.info("Snmp Trap service init Finish......");
		hongdianService.start();
		log.info("Gprs service init Finish......");
		heartbeatService.start();
		log.info("Heartbeat Setup Finish......");

		deviceLogService.launchLogListen();
		log.info("DeviceLog Listen Setup Finish......");

	}

	private boolean isValids() {

		boolean isValid = true;

		if (null == usernameField.getText() || "".equals(usernameField.getText())) {
			JOptionPane.showMessageDialog(this, "前置机编号不能为空，请输入编号", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == passwordField.getText() || "".equals(passwordField.getText())) {
			JOptionPane.showMessageDialog(this, "密码不能为空，请输入密码", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == serverField.getText() || "".equals(serverField.getText())) {
			JOptionPane.showMessageDialog(this, "服务器地址不能为空，请输入地址和端口", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		return isValid;
	}

	private void closeCurrentUI() {
		this.setVisible(false);
		dispose();
	}

	private void exit() {
		QuitServerThread qst = new QuitServerThread();
		qst.start();
		try {
			qst.join(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		qst.interrupt();
		//
		try {
			baseOperateService.stop();
		} catch (RuntimeException e) {
			log.error(e);
		}
		try {
			trapMonitor.stopTrap();
		} catch (RuntimeException e) {
			log.error(e);
		}
		try {
			hongdianService.stop();
		} catch (RuntimeException e) {
			log.error(e);
		}
		try {
			heartbeatService.stop();
		} catch (RuntimeException e) {
			log.error(e);
		}

		deviceLogService.clear();
		//

		dispose();

		System.exit(0);
	}

	private class QuitServerThread extends Thread {
		public void run() {
			messageSend.getLoginService().logoffFep(Configuration.fepCode);
			messageSend.closeAll();
		}
	}

	void okButtonActionPerformed(ActionEvent e) {
		logon();
	}

	void cancelButtonActionPerformed(ActionEvent e) {
		if (flag) {
			exit();
		} else {
			closeCurrentUI();
		}
	}

	private JTextField usernameField;
	private JPasswordField passwordField;
	private JTextField serverField;
	private JButton okButton;
	private JButton cancelButton;

	private static final long serialVersionUID = -5895374075474080695L;

	public static final int WAIT = 0;
	public static final int CANCEL = 1;
	public static final int OK = 2;
	public static final String ID = "loginDialog";

	private boolean flag;

	public LoginImagePanel getLoginImagePanel() {
		return loginImagePanel;
	}

	public void setLoginImagePanel(LoginImagePanel loginImagePanel) {
		this.loginImagePanel = loginImagePanel;
	}

	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	public void setConsolePanel(ConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
	}

	public HongdianService getHongdianService() {
		return hongdianService;
	}

	public void setHongdianService(HongdianService hongdianService) {
		this.hongdianService = hongdianService;
	}

	public TrapMonitor getTrapMonitor() {
		return trapMonitor;
	}

	public void setTrapMonitor(TrapMonitor trapMonitor) {
		this.trapMonitor = trapMonitor;
	}

	public BaseOperateService getBaseOperateService() {
		return baseOperateService;
	}

	public void setBaseOperateService(BaseOperateService baseOperateService) {
		this.baseOperateService = baseOperateService;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public HeartbeatService getHeartbeatService() {
		return heartbeatService;
	}

	public void setHeartbeatService(HeartbeatService heartbeatService) {
		this.heartbeatService = heartbeatService;
	}

	// TODO

	public DeviceLogService getDeviceLogService() {
		return deviceLogService;
	}

	public void setDeviceLogService(DeviceLogService deviceLogService) {
		this.deviceLogService = deviceLogService;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}

}
