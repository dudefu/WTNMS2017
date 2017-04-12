package com.jhw.adm.client.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalComboBoxIcon;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StopWatch;

import sun.awt.SunToolkit;

import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.ApplicationException;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.core.MessageListenerContainer;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RegisteringRequired;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.switcher.AutoRefreshTopology;
import com.jhw.adm.server.entity.system.TimeConfig;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.warning.WarningType;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;

/**
 * 桌面应用程序，负责系统的生命周期，包括启动，关闭，加载资源。
 */
public class DesktopApplication {
	
	public DesktopApplication() {
		appName = "WT-NMS综合网管系统";
		appFont = new Font("宋体", Font.PLAIN, 12);
		splash = SplashScreen.getSplashScreen();
		if (splash != null) {
			graphics = splash.createGraphics();
			graphics.setFont(appFont);
		}
	}

	private void drawMessage(String task) {
		if (graphics != null) {
			graphics.setComposite(AlphaComposite.Clear);
			graphics.fillRect(20, 240, 200, 40);
			graphics.setPaintMode();
			graphics.setColor(Color.BLACK);
			graphics.drawString(task + "...", 20, 250);
		}

		if (splash != null && splash.isVisible()) {
			splash.update();
		}
	}

	/**
	 * 开始启动程序
	 * @param args 控制台参数
	 */
	public void start(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());
//		RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
		appArgs = args;
		
		StopWatch clock = new StopWatch(String.format("Profiling for %s",
				"launch application"));
		try {
			drawMessage("正在启动系统");			
			clock.start("launch application");
			parseArgs();
			configureLogging();
			configureSpringContext();
			loadResoureces();
			configureLookAndFeel();
			openLoginDialog();
			configureClientModel();
			openApplicationWindow();
			configureHeartbeat();
		} catch (Throwable e) {
			String causeName = 
				e.getCause() == null ? e.getClass().getCanonicalName() :
				e.getCause().getClass().getCanonicalName();
			JOptionPane.showMessageDialog(null, 
					"启动系统时发生错误，详情请查看日志文件。\r\n" + 
					StringUtils.abbreviate(
							causeName + ": " + e.getMessage(), 100), 
					"系统异常", JOptionPane.ERROR_MESSAGE);
			LOG.error("Launch application exception", e);
			logAppError(e);
			System.exit(0);
		} finally {
			SunToolkit.closeSplashScreen();
			clock.stop();
			LOG.info(clock.prettyPrint());
			if (splash != null && splash.isVisible()) {
				splash.close();
			}
		}
	}
	
	
	private void parseArgs() {
		if (appArgs != null && appArgs.length > 0) {
			if (appArgs[0].indexOf("-debug") > -1) {
				debug = true;
			}
		}
	}
	
	private void logAppError(Throwable e) {
		try {
			String fileName = Long.toString(new Date().getTime()) + ".log";
			StringBuilder messageBuilder = new StringBuilder();
			buildExceptionMessage(messageBuilder, e);
			FileUtils.writeStringToFile(new File(fileName), messageBuilder.toString());
		} catch (IOException ex) {
			LOG.error("Logging exception error", ex);
		}
	}
	
	private void buildExceptionMessage(StringBuilder messageBuilder, Throwable e) {
		if (e != null) {
			messageBuilder.append(String.format("%s: %s", e.getClass().getName(), e.getMessage()));
			buildExceptionMessage(messageBuilder, e.getCause());
		}
	}
	
	private void configureHeartbeat() {
		drawMessage("正在启动心跳检测");
		clientModel.startHeartbreak();
	}
		
	@SuppressWarnings("unchecked")
	private void configureClientModel() {		
		drawMessage("正在连接服务器");
		
		MessageListenerContainer listenerContainer =
			getSpringBean(MessageListenerContainer.ID);
		listenerContainer.start();
		
		drawMessage("正在加载系统数据");
		
		clientModel = getSpringBean(ClientModel.ID);
		ClientUtils.setLoginUser(clientModel.getCurrentUser().getUserName());
		AlarmModel alarmModel = getSpringBean(AlarmModel.ID);
		RemoteServer remoteServer = getSpringBean(RemoteServer.ID);
		equipmentRepository = getSpringBean(EquipmentRepository.ID);
		alarmModel.setWarningTypes((List<WarningType>)remoteServer.getService().findAll(WarningType.class));    	
    	List<TimeConfig> listOfTimeConfig = (List<TimeConfig>)remoteServer.getService().findAll(TimeConfig.class);
    	
    	if (listOfTimeConfig != null && listOfTimeConfig.size() > 0) {
    		clientModel.setTimeConfig(listOfTimeConfig.get(0));
    	}
		
		TopDiagramEntity topDiagram = equipmentRepository.findDiagram(clientModel.getCurrentUser());
		
		if (topDiagram == null) {
			throw new ApplicationException("拓扑图不能为空，请初始化数据。");
		}
		diagramName = topDiagram.getName();
		clientModel.getEquipmentModel().updateDiagram(topDiagram);
	}

	/**
	 * 加载系统资源
	 */
	private void loadResoureces() {
		drawMessage("正在加载系统资源");
		imageRegistry = getSpringBean(ImageRegistry.ID);
		try {
			registImageByReflect(MainMenuConstants.class);
			registImageByReflect(ApplicationConstants.class);
			registImageByReflect(ButtonConstants.class);
			registImageByReflect(NetworkConstants.class);
		} catch (IllegalArgumentException e) {
			LOG.error("getDeclaredFields error", e);
		} catch (IllegalAccessException e) {
			LOG.error("getDeclaredFields error", e);
		}
	}
	
	private void registImageByReflect(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : clazz.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) &&
				field.getType().equals(String.class) && 
				field.getAnnotation(RegisteringRequired.class) != null) {
				String value = field.get(field).toString();
				LOG.debug(String.format("Field: %s, %s", field.getName(), value));
				imageRegistry.regist(value);
			}
		}
	}

	/**
	 * 加载系统外观
	 */
	private void configureLookAndFeel() {
		drawMessage("正在加载系统外观");
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
		try {
			MetalLookAndFeel.setCurrentTheme(new OceanTheme());
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
			
			Object treeExpandedIcon = WindowsTreeUI.ExpandedIcon.createExpandedIcon();
	        Object treeCollapsedIcon = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();
	        UIManager.put("Tree.expandedIcon", treeExpandedIcon);
	        UIManager.put("Tree.collapsedIcon", treeCollapsedIcon);
	        UIManager.put("ComboBox.icon", new MetalComboBoxIcon());
		} catch (ClassNotFoundException e) {
			LOG.error("Configure LookAndFeel exception", e);
		} catch (InstantiationException e) {
			LOG.error("Configure LookAndFeel exception", e);
		} catch (IllegalAccessException e) {
			LOG.error("Configure LookAndFeel exception", e);
		} catch (UnsupportedLookAndFeelException e) {
			LOG.error("Configure LookAndFeel exception", e);
		}

		for (Enumeration<?> keys = UIManager.getDefaults().keys(); keys
				.hasMoreElements();) {

			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource rs = (FontUIResource) value;
				int fontStyle = Font.PLAIN;
				int fontSize = rs.getSize();
				UIManager.put(key, new FontUIResource(new Font(appFont.getName(), fontStyle, fontSize)));

			}
		}
	}
    
	/**
	 * 显示登陆对话框
	 */
	private void openLoginDialog() {
		drawMessage("正在显示登陆对话框");
		
		Runnable runnable = new Runnable() {
			public void run() {				
				dialog = new LoginDialog(springContext, imageRegistry.getImage(
						ApplicationConstants.LOGIN_BACKGROUND));
				dialog.setIconImage(imageRegistry.getImage(
						ApplicationConstants.LOGO));
				ClientUtils.screenCenter(dialog);

				if (ClientUI.getApplication().isDebug()) {
					dialog.logon("admin", "123456");
				} else {		
					dialog.setVisible(true);
					if (dialog.getReturnValue() == LoginDialog.CANCEL) {
						System.exit(0);
					}
				}
			}
		};
		
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (InterruptedException e) {
			LOG.error("Open LoginDialog error", e);
		} catch (InvocationTargetException e) {
			LOG.error("Open LoginDialog error", e);
		}
	}

	/**
	 * 打开系统窗口
	 */
	private void openApplicationWindow() {
		drawMessage("正在打开系统窗口");
		final MainMenuManager mainMenuManager = getSpringBean(MainMenuManager.ID);
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JMenuBar menuBar = mainMenuManager.createMenuBar();
				desktopWindow = getSpringBean(DesktopWindow.ID);
				desktopWindow.setJMenuBar(menuBar);
				ClientUtils.setRootFrame(desktopWindow);
				ClientUtils.setAppName(getName());
				showWindow();
			}
		});
	}
	
	private void showWindow() {
		desktopWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		desktopWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});
		desktopWindow.setTitle(String.format("%s - %s", getName(), diagramName));
		desktopWindow.setImageIcon(imageRegistry.getImageIcon(
				ApplicationConstants.LOGO));		
		clientModel.getEquipmentModel().addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				updateWindow();
			}
		});
		desktopWindow.setBounds(ClientUtils.getMaxBounds());
		desktopWindow.open();
		LOG.info("Open application window ok...");
	}
	
	private void updateWindow() {
		if (SwingUtilities.isEventDispatchThread()) {
			String subTitle = clientModel.getEquipmentModel().getSubnet() == null ? 
					clientModel.getEquipmentModel().getDiagramName() : clientModel.getEquipmentModel().getSubnet().getName();
			desktopWindow.setTitle(String.format("%s - %s", 
					getName(), subTitle));
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateWindow();
				}
			});
		}
	}
	/**
	 * 关闭系统
	 */
	public void shutdown() {		
		int option = JOptionPane.showConfirmDialog(
				desktopWindow,
				String.format("确定退出%s吗？", getName()),
				getName(),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null);
		if (option == JOptionPane.OK_OPTION) {
			desktopWindow.save();
			dialog.loginOffClient();
			desktopWindow.close();
			System.exit(0);
		}
	}

	/**
	 * 加载系统上下文
	 */
	private void configureSpringContext() {
		drawMessage("正在加载系统模块");
		springContext = new FileSystemXmlApplicationContext(
				SPRING_CONFIG_LOCATIONS, false);
		springContext.registerShutdownHook();
		springContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
			@Override
			public void postProcessBeanFactory(
					ConfigurableListableBeanFactory beanFactory)
					throws BeansException {
				
				if (beanFactory == null) {
					return;
				}
				
				beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
					@Override
					public Object postProcessAfterInitialization(Object bean,
							String beanName) throws BeansException {
						return bean;
					}
					@Override
					public Object postProcessBeforeInitialization(Object bean,
							String beanName) throws BeansException {
						return bean;
					}
				});
			}
		});
		springContext.refresh();
		ClientUtils.setSpringContext(springContext);
		LOG.info("Configure spring context ok...");
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getSpringBean(String name) {
		T bean = null;
		if (springContext.containsBean(name)) {
			bean = (T)springContext.getBean(name);
		} else {
			throw new ApplicationException(String.format("No bean named '%s' is defined", DesktopModel.ID));
		}
		
		return bean;
	}

	/**
	 * 加载LogBack日志模块
	 */
	private void configureLogging() {
//		drawMessage("正在加载日志模块");
//		LoggerContext loggerContext = (LoggerContext) LoggerFactory
//				.getILoggerFactory();
//		JoranConfigurator joranConfigurator = new JoranConfigurator();
//		joranConfigurator.setContext(loggerContext);
//		loggerContext.reset();
//		try {
//			joranConfigurator.doConfigure(LOGBACK_CONFIG_FILE);
//			LOG.info("Configure logging ok...");
//		} catch (JoranException e) {
//			StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
//		}
	}

	/**
	 * @see DesktopWindow
	 */
	public DesktopWindow getDesktopWindow() {
		return desktopWindow;
	}

	/**
	 * @see ApplicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return springContext;
	}
	
	public String getName() {
		return appName;
	}
	
	public String[] getArgs() {
		return appArgs;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	private boolean debug;
	private String[] appArgs;
	private String diagramName;
	private Graphics2D graphics;
	private final Font appFont;
	private final SplashScreen splash;
	private final String appName;

	private LoginDialog dialog = null;
	private DesktopWindow desktopWindow;
	private EquipmentRepository equipmentRepository;
	private ClientModel clientModel;
	private ImageRegistry imageRegistry;
	private FileSystemXmlApplicationContext springContext;
	private static final Logger LOG = LoggerFactory
			.getLogger(DesktopApplication.class);
	
	//private static final String LOGBACK_CONFIG_FILE = "conf/logback.xml";
	private static final String[] SPRING_CONFIG_LOCATIONS = new String[] { "conf/applicationContext.xml" };

	private class ApplicationUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			LOG.error("ApplicationUncaughtException", e);
		}
	}
}