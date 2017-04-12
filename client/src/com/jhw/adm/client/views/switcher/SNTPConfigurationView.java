package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.SNTPConfigViewModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(SNTPConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class SNTPConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "sntpConfigurationView";
	
	//SNTP服务器设置
	private final JPanel severPnl = new JPanel();
	private final JLabel modeLbl = new JLabel();
	private final JCheckBox modeChkBox = new JCheckBox();
	
	private final JLabel preferredLbl = new JLabel();
//	private final IpAddressField preferredFld = new IpAddressField();
	private final JTextField preferredFld = new JTextField();
	
	private final JLabel standbyLbl = new JLabel();
//	private final IpAddressField standbyFld = new IpAddressField();
	private final JTextField standbyFld = new JTextField();
	
	private final JLabel pollLbl = new JLabel();
	private final NumberField pollFld = new NumberField(5,0,0,99999,true);
//	private final JTextField pollFld = new JTextField();
	private final JLabel secondLbl = new JLabel();
	
	//时区设置
	private final JPanel timeZonePnl = new JPanel();
	private final JLabel timeZoneLbl = new JLabel();
	private final JComboBox timeZoneCombox = new JComboBox();
	
	private static final String[] TIMEZONELIST = {"GMT-12","GMT-11","GMT-10","GMT-09","GMT-08",
										"GMT-07","GMT-06","GMT-05","GMT-04","GMT-03","GMT-02","GMT-01",
										"GMT-00","GMT+01","GMT+02","GMT+03","GMT+04","GMT+05","GMT+06",
										"GMT+07","GMT+08","GMT+09","GMT+10","GMT+11","GMT+12"};
	private static final String GMT_12 = "(GMT-12:00)International Date Line West";
	private static final String GMT_11 = "(GMT-11:00)Midway Island, Samoa";
	private static final String GMT_10 = "(GMT-10:00)Hawaii";
	private static final String GMT_09 = "(GMT-09:00)Alaska";
	private static final String GMT_08 = "(GMT-08:00)Mountain Time";
	private static final String GMT_07 = "(GMT-07:00)Central America";
	private static final String GMT_06 = "(GMT-06:00)Guadalajara, Mexico City, Monterrey";
	private static final String GMT_05 = "(GMT-05:00)Eastern Time";
	private static final String GMT_04 = "(GMT-04:00)Santiago, Brasilia";
	private static final String GMT_03 = "(GMT-03:00)Buenos Aires, Georgetown";
	private static final String GMT_02 = "(GMT-02:00)Mid-Atlantic";
	private static final String GMT_01 = "(GMT-01:00)Cape Verde Is.";
	private static final String GMT_00 = "(GMT-00:00)Greenwich Mean Time: Dublin, Edinburgh, Lisbon, London";
	private static final String GMT1 = "(GMT+01:00)Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna";
	private static final String GMT2 = "(GMT+02:00)Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius";
	private static final String GMT3 = "(GMT+03:00)Moscow, St. Petersburg, Volgograd";
	private static final String GMT4 = "(GMT+04:00)Islamabad, Karachi, Tashkent";
	private static final String GMT5 = "(GMT+05:00)Chennai, Kolkata, Mumbai, New Delhi";
	private static final String GMT6 = "(GMT+06:00)Astana, Dhaka";
	private static final String GMT7 = "(GMT+07:00)Bangkok, Hanoi, Jakarta";
	private static final String GMT8 = "(GMT+08:00)Beijing, Chongqing, Hong Kong, Urumqi";
	private static final String GMT9 = "(GMT+09:00)Osaka, Sapporo, Tokyo";
	private static final String GMT10 = "(GMT+10:00)Canberra, Melbourne, Sydney";
	private static final String GMT11 = "(GMT+11:00)Solomon Is., New Caledonia";
	private static final String GMT12 = "(GMT+12:00)Auckland, Wellington";
	
	//系统时间配置
	private final JPanel timeModefyPnl = new JPanel();
	private final JLabel currentDateLbl = new JLabel();
	private final NumberField yearFld = new NumberField(4,0,true);
	private final JLabel yearLbl = new JLabel();
	private final JTextField monthFld = new JTextField();
	private final JLabel monthLbl = new JLabel();
	private final JTextField dayFld = new JTextField();
	private final JLabel dayLbl = new JLabel();
	
	private final JLabel currentTiemLbl = new JLabel();
	private final JTextField hourFld = new JTextField();
	private final JLabel colonLbl1 = new JLabel();
	private final JTextField minutesFld = new JTextField();
	private final JLabel colonLbl2 = new JLabel();
	private final JTextField secondFld = new JTextField();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private final JPanel bottomBtnPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(SNTPConfigViewModel.ID)
	private SNTPConfigViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(SNTPConfigurationView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			queryData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initSeverPnl();
		initTimeZonePnl();
		initTimeModifyPnl();
		initToolBtnPnl();
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(severPnl,new GridBagConstraints(0,0,1,2,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		
		panel.add(timeZonePnl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		panel.add(timeModefyPnl,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(13,5,0,0),0,0));
		
		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(panel);

		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomBtnPnl,BorderLayout.SOUTH);
		this.setViewSize(600,480);
		
		setResource();
	}
	
	private void initSeverPnl(){
		JPanel panel = new JPanel(new GridBagLayout());

		panel.add(modeLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(modeChkBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		
		panel.add(preferredLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,10),0,0));
		panel.add(preferredFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,0),0,0));
		panel.add(new JLabel("(0-30个字符)"),new GridBagConstraints(2,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,10,0),0,0));
		
		panel.add(standbyLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,10),0,0));
		panel.add(standbyFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,0),0,0));
		panel.add(new JLabel("(0-30个字符)"),new GridBagConstraints(2,2,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,10,0),0,0));
		
		panel.add(pollLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
	 			GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,10),0,0));
		panel.add(pollFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,5,10,0),0,0));
		panel.add(secondLbl,new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,10,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(3,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(10,0,10,10),0,0));

		severPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		severPnl.add(panel);
	}

	private void initTimeZonePnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(timeZoneLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,10),0,0));
		panel.add(timeZoneCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		
		timeZonePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		timeZonePnl.add(panel);
	}
	
	private void initTimeModifyPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(currentDateLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(yearFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(yearLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(monthFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));
		panel.add(monthLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,7,10,8),0,0));
		panel.add(dayFld,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(dayLbl,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));

		panel.add(currentTiemLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,5),0,0));
		panel.add(hourFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,5),0,0));
		panel.add(colonLbl1,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,9,10,0),0,0));	
		panel.add(minutesFld,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,6,10,5),0,0));
		panel.add(colonLbl2,new GridBagConstraints(4,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(secondFld,new GridBagConstraints(5,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		
		timeModefyPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		timeModefyPnl.add(panel);
	}
	
	private void initToolBtnPnl(){
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(saveBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(newPanel);
		
		bottomBtnPnl.setLayout(new BorderLayout());
		bottomBtnPnl.add(leftPnl,BorderLayout.WEST);
		bottomBtnPnl.add(rightPnl,BorderLayout.EAST);
	}
	
	
	private void setResource(){
		this.setTitle("SNTP配置");
		severPnl.setBorder(BorderFactory.createTitledBorder("SNTP服务器配置"));
		timeZonePnl.setBorder(BorderFactory.createTitledBorder("时区"));
		timeModefyPnl.setBorder(BorderFactory.createTitledBorder("系统时间"));
		
		modeLbl.setText("模式");
		modeChkBox.setText("Enable");
		preferredLbl.setText("首选服务器");
		standbyLbl.setText("备用服务器");
		pollLbl.setText("轮询");
		secondLbl.setText("秒");
		timeZoneLbl.setText("时区");
//		currentDateLbl.setText("设置当前日期");
		currentDateLbl.setText("当前日期");
		yearLbl.setText("年");
		monthLbl.setText("月");
		dayLbl.setText("日");
//		currentTiemLbl.setText("设置当前时间");
		currentTiemLbl.setText("当前时间");
		colonLbl1.setText(":");	
		colonLbl2.setText(":");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		statusFld.setText("设备侧");
		
		preferredFld.setColumns(20);
		standbyFld.setColumns(20);
		pollFld.setColumns(20);
		
		yearFld.setColumns(6);
		monthFld.setColumns(3);
		dayFld.setColumns(3);
		hourFld.setColumns(3);
		minutesFld.setColumns(3);
		secondFld.setColumns(3);
		yearFld.setHorizontalAlignment(JTextField.CENTER);
		monthFld.setHorizontalAlignment(JTextField.CENTER);
		dayFld.setHorizontalAlignment(JTextField.CENTER);
		hourFld.setHorizontalAlignment(JTextField.CENTER);
		minutesFld.setHorizontalAlignment(JTextField.CENTER);
		secondFld.setHorizontalAlignment(JTextField.CENTER);
		
		timeZoneCombox.setEnabled(true);
		yearFld.setEditable(true);
		monthFld.setEditable(true);
		dayFld.setEditable(true);
		hourFld.setEditable(true);
		minutesFld.setEditable(true);
		secondFld.setEditable(true);
		yearFld.setBackground(Color.WHITE);
		monthFld.setBackground(Color.WHITE);
		dayFld.setBackground(Color.WHITE);
		hourFld.setBackground(Color.WHITE);
		minutesFld.setBackground(Color.WHITE);
		secondFld.setBackground(Color.WHITE);
		
		preferredFld.setDocument(new TextFieldPlainDocument(preferredFld, 30));
		standbyFld.setDocument(new TextFieldPlainDocument(standbyFld, 30));
		
		timeZoneCombox.setPreferredSize(new Dimension(80,timeZoneCombox.getPreferredSize().height));
		for(int i = 0 ; i <TIMEZONELIST.length; i++){
			timeZoneCombox.addItem(TIMEZONELIST[i]);
		}
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 *从服务器查询设备的vlan信息
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		try{
		List<SNTPConfigEntity> sntpConfigEntityList = (List<SNTPConfigEntity>)remoteServer.getService().findAll(SNTPConfigEntity.class, where, parms);
		if (null == sntpConfigEntityList || sntpConfigEntityList.size() <1){
			statusFld.setText("");
			return;
		}

		viewModel.setSntpConfigEntity(sntpConfigEntityList.get(0));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setValue();
	}
	
	private void setValue(){
		SNTPConfigEntity sntpConfigEntity = viewModel.getSntpConfigEntity();
		modeChkBox.setSelected(sntpConfigEntity.isApplied());
//		preferredFld.setIpAddress(sntpConfigEntity.getFirstServerIP());
//		standbyFld.setIpAddress(sntpConfigEntity.getSecondServerIP());
		preferredFld.setText(sntpConfigEntity.getFirstServerIP());
		standbyFld.setText(sntpConfigEntity.getSecondServerIP());
		pollFld.setText(String.valueOf(sntpConfigEntity.getBtSeconds()));
		timeZoneCombox.setSelectedItem(cutAreaString(sntpConfigEntity.getTimeArea()));
		//timeZoneCombox.setSelectedItem(sntpConfigEntity.getTimeArea());
		String time = sntpConfigEntity.getCurrentTime();
		String format = "dd MM yyyy HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format); 
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (null != date){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			yearFld.setText(String.valueOf(cal.get(Calendar.YEAR)));
			monthFld.setText(convertDate(cal.get(Calendar.MONTH)+1));
			dayFld.setText(convertDate(cal.get(Calendar.DATE)));
			hourFld.setText(convertDate(cal.get(Calendar.HOUR_OF_DAY )));
			minutesFld.setText(convertDate(cal.get(Calendar.MINUTE)));
			secondFld.setText(convertDate(cal.get(Calendar.SECOND)));
		}
		
		statusFld.setText(dataStatus.get(sntpConfigEntity.getIssuedTag()).getKey());
//		String aaaa = fm.format(date);
	}
	
	private String convertDate(int value){
		String str = "";
		if(value < 10){
			str = "0" + value;
		}else{
			str = String.valueOf(value);
		}
		return str;
	}
	
	private SNTPConfigEntity getValue(){
		SNTPConfigEntity sntpConfigEntity = viewModel.getSntpConfigEntity();
		
		sntpConfigEntity.setApplied(modeChkBox.isSelected());
		if (preferredFld.getText() != null && !("".equals(preferredFld.getText()))){
			sntpConfigEntity.setFirstServerIP(preferredFld.getText());
		}
		if (standbyFld.getText() != null && !("".equals(standbyFld.getText()))){
			sntpConfigEntity.setSecondServerIP(standbyFld.getText());
		}
		String timeArea = timeZoneCombox.getSelectedItem().toString();
		if("GMT+08".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT8);
		}else
		if("GMT-12".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_12);
		}else
		if("GMT-11".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_11);
		}else
		if("GMT-10".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_10);
		}else
		if("GMT-09".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_09);
		}else
		if("GMT-08".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_08);
		}else
		if("GMT-07".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_07);
		}else
		if("GMT-06".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_06);
		}else
		if("GMT-05".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_05);
		}else
		if("GMT-04".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_04);
		}else
		if("GMT-03".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_03);
		}else
		if("GMT-02".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_02);
		}else
		if("GMT-01".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_01);
		}else
		if("GMT-00".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT_00);
		}else
		if("GMT+12".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT12);
		}else
		if("GMT+11".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT11);
		}else
		if("GMT+10".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT10);
		}else
		if("GMT+09".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT9);
		}else
		if("GMT+07".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT7);
		}else
		if("GMT+06".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT6);
		}else
		if("GMT+05".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT5);
		}else
		if("GMT+04".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT4);
		}else
		if("GMT+03".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT3);
		}else
		if("GMT+02".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT2);
		}else
		if("GMT+01".equals(timeArea)){
			sntpConfigEntity.setTimeArea(GMT1);
		}
		
		sntpConfigEntity.setBtSeconds(Integer.parseInt(pollFld.getText()));
		
		int year = Integer.parseInt(yearFld.getText());
		int month = Integer.parseInt(monthFld.getText());
		int day = Integer.parseInt(dayFld.getText());
		int hour = Integer.parseInt(hourFld.getText());
		int minute = Integer.parseInt(minutesFld.getText());
		int second = Integer.parseInt(secondFld.getText());
		String currentTime = day + " " + month + " " + year + " " + hour
		+ ":" + minute + ":" + second;
		sntpConfigEntity.setCurrentTime(currentTime);
		
		sntpConfigEntity.setSwitchNode(switchNodeEntity);
		sntpConfigEntity.setIssuedTag(Constants.ISSUEDADM);
		return sntpConfigEntity;
	}
	
	private String cutAreaString(String area){
		if(StringUtils.isBlank(area)){
			return area;
		}
		int begin = area.indexOf("(");
		int end = area.indexOf(")");
		if(begin < 0 || end < 0){
			return area;
		}
		area = area.substring(begin+1,end);
		
		int index = area.indexOf(":");
		area = area.substring(0,index);
		
		return area;
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载SNTP系统信息",role=Constants.MANAGERCODE)
	public void download(){
		//Amend 2010.06.03
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		} 
	
		SNTPConfigEntity sntpConfigEntity = getValue();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(sntpConfigEntity);
	
		Task task = new DownLoadRequestTask(list, macValue, result);
		showConfigureMessageDialog(task, "下载");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public DownLoadRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.SNTPSET, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载SNTP系统信息异常");
				queryData();
				LOG.error("SNTPConfigurationView.save() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载SNTP系统信息",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
	
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHSNTP);
		showUploadMessageDialog(task, "上载SNTP系统信息");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}
	
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(uploadStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				switchNodeEntity = null;
			}
		}
	};
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	private void clear(){
		modeChkBox.setSelected(true);
		preferredFld.setText("");
		standbyFld.setText("");
		pollFld.setText("");
		yearFld.setText("");
		monthFld.setText("");
		dayFld.setText("");
		hourFld.setText("");
		minutesFld.setText("");
		secondFld.setText("");
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
	
	//Amend 2010.06.03
	public boolean isValids()
	{
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == pollFld.getText() || "".equals(pollFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "轮询时间设置错误，正确时间是：30-99999","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(((NumberUtils.toInt(pollFld.getText()) < 30) || (NumberUtils.toLong(pollFld.getText()) > 99999)))
		{
			JOptionPane.showMessageDialog(this, "轮询时间设置错误，正确时间是：30-99999","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
//		if(preferredFld.getText() != null && !"".equals(preferredFld.getText().trim())){
//			if(ClientUtils.isIllegal(preferredFld.getText())){
//				JOptionPane.showMessageDialog(this, "首选服务器IP非法，请重新输入","提示",JOptionPane.NO_OPTION);
//				isValid = false;
//				return isValid;
//			}
//		}
//		
//		if(standbyFld.getText() != null && !"".equals(standbyFld.getText().trim())){
//			if(ClientUtils.isIllegal(standbyFld.getText())){
//				JOptionPane.showMessageDialog(this, "备用服务器IP非法，请重新输入","提示",JOptionPane.NO_OPTION);
//				isValid = false;
//				return isValid;
//			}
//		}
		
		if(null == hourFld.getText() || "".equals(hourFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "小时设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(((NumberUtils.toInt(hourFld.getText()) < 0) || (NumberUtils.toInt(hourFld.getText()) > 23)))
		{
			JOptionPane.showMessageDialog(this, "小时设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == minutesFld.getText() || "".equals(minutesFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "分钟设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(((NumberUtils.toInt(minutesFld.getText()) < 0) || (NumberUtils.toInt(minutesFld.getText()) > 59)))
		{
			JOptionPane.showMessageDialog(this, "分钟设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == secondFld.getText() || "".equals(secondFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "秒设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(((NumberUtils.toInt(secondFld.getText()) < 0) || (NumberUtils.toInt(secondFld.getText()) > 59)))
		{
			JOptionPane.showMessageDialog(this, "秒设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == yearFld.getText() || "".equals(yearFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "年份设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == monthFld.getText() || "".equals(monthFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "月份设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if(((NumberUtils.toInt(monthFld.getText()) < 1) || (NumberUtils.toInt(monthFld.getText()) > 12)))
		{
			JOptionPane.showMessageDialog(this, "月份设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == dayFld.getText() || "".equals(dayFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "日期设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else
		{
			int day = NumberUtils.toInt(monthFld.getText());
			if (1 == day || 3 == day || 5 == day || 7 == day || 8 == day
					|| 10 == day || 12 == day)//31
			{
				if((NumberUtils.toInt(dayFld.getText()) < 1 || (NumberUtils.toInt(dayFld.getText()) > 31)))
				{
					JOptionPane.showMessageDialog(this, "日期设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}else if(4 == day || 6 == day || 9 == day || 11 == day)//30
			{
				if((NumberUtils.toInt(dayFld.getText()) < 1 || (NumberUtils.toInt(dayFld.getText()) > 30)))
				{
					JOptionPane.showMessageDialog(this, "日期设置错误，请重新输入","提示",JOptionPane.NO_OPTION);
					isValid = false;
					return isValid;
				}
			}else if(2 == day)// 28 or 29
			{
				GregorianCalendar gc = new GregorianCalendar();
				if(gc.isLeapYear(NumberUtils.toInt(yearFld.getText())))
				{
					if((NumberUtils.toInt(dayFld.getText()) < 1 || (NumberUtils.toInt(dayFld.getText()) > 29)))
					{
						String message = "日期设置中日错误，" + NumberUtils.toInt(yearFld.getText()) + "为闰年，" + " 范围是：1-29";
						JOptionPane.showMessageDialog(this, message,"提示",JOptionPane.NO_OPTION);
						isValid = false;
						return isValid;
					}
				}else
				{
					if((NumberUtils.toInt(dayFld.getText()) < 1 || (NumberUtils.toInt(dayFld.getText()) > 28)))
					{
						String message = "日期设置中日错误，" + NumberUtils.toInt(yearFld.getText()) + "为平年，" + " 范围是：1-28";
						JOptionPane.showMessageDialog(this, message,"提示",JOptionPane.NO_OPTION);
						isValid = false;
						return isValid;
					}
				}
			}
		}
		return isValid;
	}
	
}
