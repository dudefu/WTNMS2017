package com.jhw.adm.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.jhw.adm.client.core.JndiLookupException;
import com.jhw.adm.client.core.LazyJndiObjectLocator;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.ClientConfig;
import com.jhw.adm.client.model.ClientConfigRepository;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.LoginDialogModel;
import com.jhw.adm.client.model.ServerInfo;
import com.jhw.adm.client.swing.ImagePanel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.CheckUserBean;
import com.jhw.adm.server.entity.util.License;
import com.jhw.adm.server.servic.CommonServiceBeanRemote;
import com.jhw.adm.server.servic.LoginServiceRemote;
import com.jhw.adm.server.servic.NMSServiceRemote;

/**
 * WTNMS系统登录对话框
 * <br> 
 * 使用了背景图片，所以组件使用绝对定位
 */
//@Component(LoginDialog.ID)
public class LoginDialog extends JDialog {

	public LoginDialog(ApplicationContext applicationContext,
			Image backgroundImage) {
		this.applicationContext = applicationContext;
		setSize(530, 356); 
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setUndecorated(true);
		setModal(true);
		setTitle("WTFEP 登录 ...");
		setLayout(new BorderLayout());
		ImagePanel imageComponent = new ImagePanel(backgroundImage);
		createContents(imageComponent);
		add(imageComponent, BorderLayout.CENTER);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (StringUtils.isNotBlank(usernameField.getText())) {
					usernameField.selectAll();
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void createContents(JPanel parent) {
		parent.setLayout(null);
		usernameField = new JTextField(29);
		passwordField = new JPasswordField(29);
		usernameField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		passwordField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		passwordField.requestFocusInWindow();
		okButton = new JButton();
		cancelButton = new JButton();
		
		getRootPane().setDefaultButton(okButton);

		JLabel serverLabel = new JLabel("服务器");
		JLabel userLabel = new JLabel("管理员");
		JLabel passworkLabel = new JLabel("密码");

//		passwordField.setText("123456");
		okButton.setMnemonic(76);
		okButton.setBackground(new Color(61, 151, 203));
		okButton.setText("登 录");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		cancelButton.setMnemonic(67);
		cancelButton.setBackground(new Color(61, 151, 203));
		cancelButton.setText("取 消");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});

		clientConfigRepository = (ClientConfigRepository) getApplicationContext().getBean(
				ClientConfigRepository.ID);
		ClientConfig clientConfig = clientConfigRepository.loadConfig();
		logingDialogModel = (LoginDialogModel) getApplicationContext().getBean(
				LoginDialogModel.ID);
		logingDialogModel.setClientConfig(clientConfig);
		usernameField.setText(logingDialogModel.getLastUser());
		passwordField.requestFocus();

		serverBox = new JComboBox<Object>(logingDialogModel.getComboModel());
		serverBox.setEditable(false);
		int lastServerIndex = clientConfig.getLastServerIndex();
		if (serverBox.getModel().getSize() > lastServerIndex) {
			serverBox.setSelectedIndex(lastServerIndex);
		}

		parent.add(serverBox);
		parent.add(usernameField);
		parent.add(passwordField);

		parent.add(okButton);
		parent.add(cancelButton);
//		JButton testButton = new JButton("测试");
		JButton addButton = new JButton("配 置");
		addButton.setBackground(new Color(61, 151, 203));
//		parent.add(testButton);
		parent.add(addButton);

		serverBox
				.setPreferredSize(new Dimension(usernameField
						.getPreferredSize().width,
						serverBox.getPreferredSize().height));

		layoutIt(userLabel, 93, 115);
		layoutIt(usernameField, 279, 157);

		layoutIt(passworkLabel, 93, 150);
		layoutIt(passwordField, 279, 190);
		
		layoutIt(serverLabel, 93, 185);
		layoutIt(serverBox, 279, 223);

//		layoutIt(okButton, 67, 230);
//		layoutIt(cancelButton, 144, 230);

//		layoutIt(testButton, 221, 230);
//		layoutIt(addButton, 298, 230);

		layoutIt(addButton, 235, 267);
//		layoutIt(, 144, 230);

		layoutIt(okButton, 315, 267);
		layoutIt(cancelButton, 395, 267);

//		testButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				LazyJndiObjectLocator lazyLoginObjectLocator = (LazyJndiObjectLocator) applicationContext
//						.getBean(RemoteServer.LOGIN_OBJECT);
//				Properties jndiEnvironment = lazyLoginObjectLocator
//						.getJndiEnvironment();
//				ServerInfo selectedServer = logingDialogModel
//						.getSelectedServer();
//				String url = String.format("jnp://%s:%s", selectedServer
//						.getAddress(), selectedServer.getPort());
//				jndiEnvironment.setProperty("java.naming.provider.url", url);
//				lazyLoginObjectLocator.setJndiEnvironment(jndiEnvironment);
//				try {
//					lazyLoginObjectLocator.start();
//					String infoMessage = String.format("连接服务器[%s]成功", url);
//					JOptionPane.showMessageDialog(LoginDialog.this,
//							infoMessage, "信息", JOptionPane.INFORMATION_MESSAGE);
//				} catch (JndiLookupException ex) {
//					LOG.error("lazyLoginObjectLocator error", ex);
//					String errorMessage = String.format("连接服务器[%s]失败", url);
//					JOptionPane.showMessageDialog(LoginDialog.this,
//							errorMessage, "错误", JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		});

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManageServerDialog serverDialog = new ManageServerDialog(
						LoginDialog.this);
				serverDialog.fillContents(logingDialogModel);
				serverDialog.setVisible(true);
				serverBox.updateUI();
				if (serverBox.getModel().getSize() > 0) {
					serverBox
							.setSelectedIndex(serverBox.getModel().getSize() - 1);
				}
				clientConfigRepository.saveConfig(logingDialogModel.getClientConfig());
			}
		});
	}

	private void layoutIt(JComponent component, int x, int y) {
		component.setBounds(x, y, component.getPreferredSize().width, component
				.getPreferredSize().height);
	}
	
	private LoginServiceRemote loginService = null;
	public void loginOffClient(){
		if(null != loginService){
			loginService.loginOffClient(usernameField.getText().trim());
		}
	}
	
	public void logon(String username, String password) {

		LazyJndiObjectLocator lazyLoginObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.LOGIN_OBJECT);
		Properties jndiEnvironment = lazyLoginObjectLocator
				.getJndiEnvironment();
		ServerInfo selectedServer = logingDialogModel.getSelectedServer();
		String url = String.format("jnp://%s:%s", selectedServer.getAddress(),
				selectedServer.getPort());
		jndiEnvironment.setProperty("java.naming.provider.url", url);
		lazyLoginObjectLocator.setJndiEnvironment(jndiEnvironment);
		try {
			this.loginService = (LoginServiceRemote) lazyLoginObjectLocator
					.start();
			checkUserBean = loginService.longinClient(username, password);
			if (checkUserBean.getErrorCode() > 0) {
				String titleMessage = "登录服务器失败";
				String errorMessage = checkUserBean.getErrorMessage();
				passwordField.setText(StringUtils.EMPTY);
				JOptionPane.showMessageDialog(LoginDialog.this, errorMessage,
						titleMessage, JOptionPane.ERROR_MESSAGE);
			} else {
				userEntity = checkUserBean.getUserEntity();
				license = checkUserBean.getLicense();
				connectRemoteSever();
				logingDialogModel.setLastUser(userEntity.getUserName());
				logingDialogModel.getClientConfig().setLastServerIndex(serverBox.getSelectedIndex());
				clientConfigRepository.saveConfig(logingDialogModel.getClientConfig());
				returnValue = OK;
				dispose();
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			JOptionPane.showMessageDialog(LoginDialog.this, ex.getMessage(), "启用服务失败",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void logon() {		
		String username = usernameField.getText();
		char[] password = passwordField.getPassword();
		Border textBorder = BorderFactory.createLineBorder(Color.WHITE);
		if (StringUtils.isBlank(username)) {
			usernameField.setBorder(new ErrorBorder("名称不能为空"));
			JOptionPane.showMessageDialog(LoginDialog.this, "名称不能为空", "无法登录",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			usernameField.setBorder(textBorder);
		}

		if (password.length == 0) {
			passwordField.setBorder(new ErrorBorder("密码不能为空"));
			JOptionPane.showMessageDialog(LoginDialog.this, "密码不能为空", "无法登录",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			passwordField.setBorder(textBorder);
		}
		
		logon(username, String.valueOf(password));
	}

	private void connectRemoteSever() throws JndiLookupException {
		ClientModel clientModel = (ClientModel) applicationContext
				.getBean(ClientModel.ID);
		//
		LazyJndiObjectLocator lazyCommonObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.COMMON_OBJECT);
		LazyJndiObjectLocator lazyNmsObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.NMS_OBJECT);
		LazyJndiObjectLocator lazyAdmObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.ADM_OBJECT);
		LazyJndiObjectLocator lazyDataCacheObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.DATA_CACHE_OBJECT);
		LazyJndiObjectLocator lazyPingTimerObjectLocator = (LazyJndiObjectLocator) applicationContext
				.getBean(RemoteServer.PING_TIMER_OBJECT);

		lazyDataCacheObjectLocator.start();
		NMSServiceRemote nmsService = (NMSServiceRemote) lazyNmsObjectLocator
				.start();
		CommonServiceBeanRemote commservice = (CommonServiceBeanRemote) lazyCommonObjectLocator
				.start();
		lazyAdmObjectLocator.start();
		lazyPingTimerObjectLocator.start();

		MessageSender heartbeatMessageSender = (MessageSender) applicationContext
				.getBean("heartbeatMessageSender");

		clientModel.setLocalAddress(ClientUtils.getLocalAddress());
		clientModel.setClientConfig(logingDialogModel.getClientConfig());
		clientModel.setCurrentUser(userEntity);
		clientModel.setHeartbeatMessageSender(heartbeatMessageSender);
		clientModel.setNmsService(nmsService);
		clientModel.setCommonService(commservice);
		clientModel.setServerConfig(logingDialogModel.getSelectedServer());
		clientModel.setLicense(license);
	}

	public void cancelAndClose() {
		returnValue = CANCEL;
		dispose();
	}

	void okButtonActionPerformed(ActionEvent e) {
		logon();
	}

	void cancelButtonActionPerformed(ActionEvent e) {
		cancelAndClose();
	}

	public int getReturnValue() {
		return returnValue;
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancelAndClose();
		}
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private ClientConfigRepository clientConfigRepository;
	private UserEntity userEntity;
	private CheckUserBean checkUserBean;
	private License license;
	private LoginDialogModel logingDialogModel;
	private JTextField usernameField;
	private JComboBox serverBox;
	private JPasswordField passwordField;
	private JButton okButton;
	private JButton cancelButton;
	private int returnValue;
	private final ApplicationContext applicationContext;

	private static final Logger LOG = LoggerFactory
			.getLogger(LoginDialog.class);
	private static final long serialVersionUID = -5895374075474080695L;

	public static final int WAIT = 0;
	public static final int CANCEL = 1;
	public static final int OK = 2;
	public static final String ID = "loginDialog";
}

// Reference: http://www.javalobby.org/java/forums/t20551.html
class AbstractValidator extends InputVerifier implements KeyListener {
	private AbstractValidator() {
        color = new Color(243, 255, 159);
    }
	
    private AbstractValidator(JComponent c, String message) {
        this();
        c.addKeyListener(this);
        messageLabel = new JLabel(message + " ");
//        image = new JLabel(new ImageIcon("exception_16x16.png"));
    }
	
    public AbstractValidator (JDialog parent, JComponent c, String message) {		
        this(c, message);
        this.parent = parent;
        popup = new JDialog(parent);
        initComponents();
    }
	
    public AbstractValidator (JFrame parent, JComponent c, String message) {
        this(c, message);
        this.parent = parent;
        popup = new JDialog(parent);
        initComponents();
    }
    
    private void initComponents() {
        popup.getContentPane().setLayout(new FlowLayout());
        popup.setUndecorated(true);
        popup.getContentPane().setBackground(color);
//        popup.getContentPane().add(image);
        popup.getContentPane().add(messageLabel);
        popup.setFocusableWindowState(false);
    }

	@Override
	public boolean verify(JComponent c) {		
		if (c instanceof JTextField) {
			JTextField textField = (JTextField)c;
			
			if (StringUtils.isBlank(textField.getText())) {
				textField.setBorder(new ErrorBorder(messageLabel.getText()));
	            c.setBackground(Color.PINK);
	            popup.setSize(0, 0);
	            popup.setLocationRelativeTo(c);
	            point = popup.getLocation();
	            cDim = c.getSize();
	            popup.setLocation(point.x-(int)cDim.getWidth()/2,
	                point.y+(int)cDim.getHeight()/2);
	            popup.pack();
	            popup.setVisible(true);
	            return false;
			} else {
		        c.setBackground(Color.WHITE);
		        textField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		        return true;
			}
		} else {
	        c.setBackground(Color.WHITE);
	        return true;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		popup.setVisible(false);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	private JDialog popup;
    private Object parent;
    private JLabel messageLabel;
    private JLabel image;
    private Point point;
    private Dimension cDim;
    private final Color color;
}

class ErrorBorder implements Border, Serializable {
    
    public ErrorBorder() {
        this("错误");
    }
    public ErrorBorder(String message) {
        this.message = message;
    }
    
    /**
     * {@inheritDoc}
     */
    public Insets getBorderInsets(java.awt.Component c) {
        return new Insets(0, 0, 0, 0);
    }
    
    /**
     * This border is not opaque.
     * 
     * @return always returns {@code false}
     */
    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width,
        int height) {        
        Color oldColor = g.getColor();
        g.setColor(Color.RED);
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawRect(x, y, width - 1, height - 1);
        int textWidth = g.getFontMetrics().stringWidth(message);
//        g2d.drawString(message, width - textWidth - 5, 13);        
        drawErrorIcon(g2d, 7, 8);
        g.setColor(oldColor);
    }
    
    public void drawErrorIcon(Graphics2D g2d, int width, int height) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2d.setColor(Color.RED);
        int x = 0;
        int y = 0;
        g2d.fillRect(x, y, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawLine(x, y, width, height);
        g2d.drawLine(x, height, width, y);
    }
    
	private static final long serialVersionUID = 4203633688219735383L;
    private final String message;
}

class BorderDecorator extends JComponent {

	public BorderDecorator(JComponent component) {
		child = component;
		this.setLayout(new BorderLayout());
		this.add(child);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Color oldColor = g.getColor();
		g.setColor(Color.RED);
		int height = this.getHeight();
		int width = this.getWidth();
		g.drawRect(0, 0, width - 1, height - 1);
		g.setColor(oldColor);
	}
	
	protected JComponent child;
	private static final long serialVersionUID = -7439504833044158915L;
}