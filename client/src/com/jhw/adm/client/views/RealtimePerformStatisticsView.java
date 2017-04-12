package com.jhw.adm.client.views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.RTMonitorConfig;
import com.jhw.adm.server.entity.warning.RmonCount;


/**
 * 实时性能监控视图
 */
@Component(RealtimePerformStatisticsView.ID)
@Scope(Scopes.DESKTOP)
public class RealtimePerformStatisticsView extends ViewPart{
	
	@PostConstruct
	protected void initialize() {
		setViewSize(800, 600);
		isFirst = true;
		setLayout(new BorderLayout());
		buttonFactory = actionManager.getButtonFactory(this);

		JPanel toolPanel = new JPanel(new FlowLayout(
				FlowLayout.LEADING));
		
		equipmentField = new IpAddressField();
		equipmentField.setColumns(20);
		equipmentField.setEditable(true);
		
		toolPanel.add(new JLabel("监控设备"));
		toolPanel.add(equipmentField);

		toolPanel.add(new JLabel("监控端口"));
		NumberFormat integerFormat = NumberFormat.getNumberInstance();
		integerFormat.setMinimumFractionDigits(0);
		integerFormat.setParseIntegerOnly(true);
		integerFormat.setGroupingUsed(false);
		portField = new JFormattedTextField(integerFormat);
		portField.setColumns(5);		
		toolPanel.add(portField);

		toolPanel.add(new JLabel("参数"));
		parameterBox = new JComboBox(new String[] { 
				Constants.packets, Constants.octets, Constants.bcast_pkts, Constants.mcast_pkts,
				Constants.crc_align, Constants.undersize, Constants.oversize, Constants.fragments,
				Constants.jabbers, Constants.collisions, Constants.pkts_64, Constants.pkts_65_127,
				Constants.pkts_128_255, Constants.pkts_256_511, Constants.pkts_512_1023, Constants.pkts_1024_1518,
				Constants.ifInDiscards,Constants.ifOutDiscards,Constants.txPackets,Constants.txBcastPkts,
				Constants.txMcastPkts});
		toolPanel.add(parameterBox);
		
		toolPanel.add(new JLabel("间隔（秒）"));
		intervalBox = new JComboBox(new String[] {
				"5", "10", "20", "35" });
		toolPanel.add(intervalBox);
		
		toolPanel.add(new JLabel("时间（分）"));
		minuteBox = new JComboBox(new String[] {
				"1", "2", "5", "10", "20", "30", "40", "50", "60" });
		toolPanel.add(minuteBox);

		dynamicChart = new DynamicChart();
		beginButton = buttonFactory.createButton(ActionConstants.BEGIN);
		stopButton = buttonFactory.createButton(ActionConstants.STOP);
				
		stopButton.setEnabled(false);
		toolPanel.add(beginButton);
		toolPanel.add(stopButton);

		add(toolPanel, BorderLayout.NORTH);
		
		add(dynamicChart, BorderLayout.CENTER);
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.RTMONITORRES, messageProcessor);
	}
	
	@ViewAction(icon=ButtonConstants.START, role=Constants.MANAGERCODE, desc="开始性能监控")
	public void begin() {
		if (!verify()) {
			return;
		}
		
		beginning();
		dynamicChart.reset();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.RTMONITORSTART);
				RTMonitorConfig monitorConfig = new RTMonitorConfig();
				monitorConfig.setParms(new String[]{parameterBox.getSelectedItem().toString()});
				
				monitorConfig.setIpValue(equipmentField.getText());
				monitorConfig.setPortNo(Integer.parseInt(portField.getText()));
				
				monitorConfig.setTimeStep(Integer.parseInt(intervalBox.getSelectedItem().toString()));
				
				Calendar now = Calendar.getInstance();
				monitorConfig.setBeginTime(now.getTime().getTime());
				
				now.add(Calendar.MINUTE, Integer.parseInt(minuteBox.getSelectedItem().toString()));
				monitorConfig.setEndTime(now.getTime().getTime());
				
				message.setIntProperty(Constants.DEVTYPE, Constants.DEV_SWITCHER2);
				message.setObject(monitorConfig);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				
				return message;
			}
		});
	}
	
	private void beginning() {
		isFirst = true;
		beginButton.setEnabled(false);
		stopButton.setEnabled(true);
		equipmentField.setEditable(false);
		portField.setEditable(false);
	}
	
	private void stopped() {
		beginButton.setEnabled(true);
		stopButton.setEnabled(false);
		equipmentField.setEditable(true);
		portField.setEditable(true);
	}
	
	private boolean verify() {
		if (StringUtils.isBlank(equipmentField.getText())) {
			JOptionPane.showMessageDialog(this, "请输入监控设备地址", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		SwitchTopoNodeEntity topoNodeEntity = (SwitchTopoNodeEntity) remoteServer
				.getService().findSwitchTopoNodeByIp(equipmentField.getText().trim());
		if(null == topoNodeEntity){
			JOptionPane.showMessageDialog(this, "该设备不存在，请重新输入设备地址", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		FEPEntity foundFep = findFep(equipmentField.getText());
		
		if (foundFep == null || !foundFep.getStatus().isStatus()) {
			JOptionPane.showMessageDialog(this, "该设备所属前置机不在线", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (StringUtils.isBlank(portField.getText())) {
			JOptionPane.showMessageDialog(this, "请输入监控设备端口", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		int portNo = NumberUtils.toInt(portField.getText());
		
		if (portNo < 1) {
			JOptionPane.showMessageDialog(this, "监控设备端口必需大于 0", ClientUtils.getAppName(), JOptionPane.INFORMATION_MESSAGE);
			return false;
		}else{
			SwitchNodeEntity switchNodeEntity = NodeUtils.getNodeEntity(topoNodeEntity).getNodeEntity();
			Set<SwitchPortEntity> portSet = switchNodeEntity.getPorts();
			boolean isExists = false;
			if(null != portSet){
				for(SwitchPortEntity switchPortEntity : portSet){
					if(switchPortEntity.getPortNO() == portNo){
						isExists = true;
						break;
					}
				}
			}
			
			if(isExists == false){
				JOptionPane.showMessageDialog(this, "监控设备不存在该端口，请重新输入", ClientUtils.getAppName(), JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		
		return true;
	}
	
	private FEPEntity findFep(String address) {
		List<FEPEntity> listOfFep = equipmentRepository.findAllFep();
		
		FEPEntity foundFep = null;
		
		for (FEPEntity fep : listOfFep) {
			for (IPSegment seg : fep.getSegment()) {
				String beginAddress = seg.getBeginIp();
				String endAddress = seg.getEndIp();
				if (contains(equipmentField.getText(), beginAddress, endAddress)) {
					foundFep = fep;
					break;
				}
			}
			
			if (foundFep != null) {
				break;
			}
		}
		
		return foundFep;
	}
	
	private boolean contains(String address, String beginAddress, String endAddress) {
		if (StringUtils.isBlank(address) ||
			StringUtils.isBlank(beginAddress) ||
			StringUtils.isBlank(endAddress)) {
			return false;
		}
		
		boolean result = false;
		
		String[] addressArray = address.split("\\.");
		String[] beginAddressArray = beginAddress.split("\\.");
		String[] endAddressArray = endAddress.split("\\.");
		
		if (addressArray[0].equals(beginAddressArray[0]) &&
			addressArray[1].equals(beginAddressArray[1]) &&
			addressArray[2].equals(beginAddressArray[2])) {

			int a = Integer.parseInt(addressArray[3]);
			int b = Integer.parseInt(beginAddressArray[3]);
			int c = Integer.parseInt(endAddressArray[3]);
			
			if (a >= b && a <= c) {
				result = true;
			}
		}
		
		return result;
	}

	@ViewAction(icon=ButtonConstants.STOP, role=Constants.MANAGERCODE, desc="停止性能监控")
	public void stop() {
		stopped();
		dynamicChart.reset();
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.RTMONITORSTOP);
				RTMonitorConfig monitorConfig = new RTMonitorConfig();
				monitorConfig.setParms(new String[]{parameterBox.getSelectedItem().toString()});
				monitorConfig.setIpValue(equipmentField.getText());
				monitorConfig.setPortNo(Integer.parseInt(portField.getText()));
				message.setObject(monitorConfig);
				message.setIntProperty(Constants.DEVTYPE, Constants.DEV_SWITCHER2);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				
				return message;
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.RTMONITORRES, messageProcessor);
		
		if (canStop()) {
			stop();
		}
	}
	
	private boolean canStop() {
		return stopButton.isEnabled();
	}
	
	private final MessageProcessorAdapter messageProcessor = new MessageProcessorAdapter() {
		@SuppressWarnings("unchecked")
		@Override
		public void process(ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof List<?>) {
					List<RmonCount> listOfRmon = (List<RmonCount>)messageObject;
					for (RmonCount rmon : listOfRmon) {
						rmon = setRmonValue(rmon);
						dynamicChart.addRmon(rmon);
					}
				}
			} catch (JMSException e) {
				LOG.error("RealtimePerformStatisticsView.MessageProcessor.message.getObject() error", e);
			}
		}
	};
	
	private synchronized RmonCount setRmonValue(RmonCount rmon){
		if (isFirst){
			oldValue = Math.abs(rmon.getValue());
			newValue = oldValue;			
			isFirst = false;
		}
		else{
			newValue = rmon.getValue();
		}
		diffvalue = newValue - oldValue;
		oldValue = newValue;
		
		rmon.setValue(diffvalue);		
		LOG.warn(diffvalue + "..." + rmon.getSampleTime().getTime());
		remoteServer.getService().saveEntity(rmon);
		
		return rmon;
	}

	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
		
	private ButtonFactory buttonFactory;
	
	/**
	 * 判断是否是第一次接受到的消息，true：第一次    false：非第一次
	 */
	private boolean isFirst;
	private long oldValue;
	private long newValue;
	private long diffvalue;
	
	private JButton beginButton; 
	private JButton stopButton;
	private DynamicChart dynamicChart;
	private JComboBox intervalBox;
	private JComboBox minuteBox;
	private JComboBox parameterBox;
	private IpAddressField equipmentField;
	private JFormattedTextField portField;
	private MessageSender messageSender;
	private static final Logger LOG = LoggerFactory.getLogger(RealtimePerformStatisticsView.class);
	private static final long serialVersionUID = 1L;
	
	public static final String ID = "realtimePerformStatisticsView";
	
	/**
	 * 动态图表，用于显示实时监控信息
	 */
	public class DynamicChart extends JComponent {
		
		public DynamicChart() {
			minValue = 0;
			maxValue = 1000;
		}
		
		protected void setViewRenderingHints(Graphics2D g) {
	        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    }
		
		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
	        Color oldColor = g2d.getColor();
	        Stroke oldStroke = g2d.getStroke();
	        setViewRenderingHints(g2d);
			drawBackground(g2d);
			drawGrid(g2d);
			drawAxis(g2d);
	        drawLabels(g2d);
	        drawLines(g2d);
	        g2d.setColor(oldColor);
	        g2d.setStroke(oldStroke);
        }

		/**
		 * 绘画图表背景
		 */
	    protected void drawBackground(Graphics2D g2d) {
	        g2d.setColor(Color.BLACK);
	        g2d.fill(g2d.getClipBounds());
	    }

		/**
		 * 绘画图表网格
		 */
	    private void drawGrid(Graphics2D g2d) {
	        g2d.setColor(Color.GRAY);
	        Line2D graphLine = new Line2D.Float();
			int height = DynamicChart.this.getSize().height;
			int width = DynamicChart.this.getSize().width;
			
			int yHeight = height - offset * 2;
			int rowHeight = yHeight / row;
			
	        int xWidth = width - offset * 2;
	        int columnWidth = xWidth / column;
	        
	        int yEnd = (rowHeight * row) + offset;
			int xEnd = (columnWidth * column) + offset;
			
			// h
			int dy = offset;
	        for (int j = 0; j < row; j++) {
                graphLine.setLine(offset , dy, xEnd, dy);
                g2d.draw(graphLine);
                dy += rowHeight;
            }

	        // v
	        int dx = offset + columnWidth;
	        for (int j = 0; j < column; j++) {
                graphLine.setLine(dx , offset, dx, yEnd);
                g2d.draw(graphLine);
                dx += columnWidth;
            }
	    }

		/**
		 * 绘画图表x,y轴
		 */
		protected void drawAxis(Graphics2D g2d) {
			int height = DynamicChart.this.getSize().height;
			int width = DynamicChart.this.getSize().width;
	        g2d.setColor(Color.GREEN);
	        g2d.setStroke(new BasicStroke(1));

	        int yHeight = height - offset * 2;
			int rowHeight = yHeight / row;
			
			int yEnd = (rowHeight * row) + offset;
			
			int xWidth = width - offset * 2;
	        int columnWidth = xWidth / column;
	        
			int xEnd = (columnWidth * column) + offset;
			
	        // yAxis
	        g2d.drawLine(offset, offset, offset, yEnd);
	        // xAxis
	        g2d.drawLine(offset, yEnd, xEnd, yEnd);
		}

		/**
		 * 绘画图表x,y轴标签
		 */
		protected void drawLabels(Graphics2D g2d) {
	        g2d.setColor(Color.GREEN);
			int height = DynamicChart.this.getSize().height;
			int width = DynamicChart.this.getSize().width;
			
			int yHeight = height - offset * 2;
			// h
			int dy = offset;
			
			long diffValue = maxValue - minValue;
			long stepValue = diffValue / row;
			int rowHeight = yHeight / row;
			
	        int xWidth = width - offset * 2;
	        int columnWidth = xWidth / column;
	        
	        int yEnd = (rowHeight * row) + offset;
			int xEnd = (columnWidth * column) + offset;
			
			long rowNum = maxValue;
	        for (int j = 0; j <= row; j++) {
                g2d.drawString(Long.toString(rowNum), offset - 20, dy);
                dy += rowHeight;
                rowNum -= stepValue;
            }
			// 最新时间标签

			int xEndOffset = 2;
			if (listOfRmon.size() > 0) {
				// 最前时间标签
				Date sampleTime = listOfRmon.get(listOfRmon.size() - 1).getSampleTime();
				if (sampleTime != null) {
					g2d.drawString(formatter.format(sampleTime), xEnd + xEndOffset, yEnd);
				}
			}
//	        g2d.drawString(formatter.format(new Date()), xEnd + xEndOffset, yEnd);
		}

		/**
		 * 绘画图表曲线
		 */
		protected void drawLines(Graphics2D g2d) {
			g2d.setColor(Color.GREEN);
	        g2d.setStroke(new BasicStroke(2));
			int height = DynamicChart.this.getSize().height;
			int width = DynamicChart.this.getSize().width;
			int yHeight = height - offset * 2;
			int rowHeight = yHeight / row;
			
			int xWidth = width - offset * 2;
	        int columnWidth = xWidth / column;
	        int yEnd = (rowHeight * row) + offset;
			int xEnd = (columnWidth * column) + offset;
			
			Line2D graphLine = new Line2D.Float();
			
			if (listOfRmon != null && listOfRmon.size() > 0) {
		        
				double dx = xEnd;
				double lastX = 0;
				double lastY = 0;

				double diffValue = maxValue - minValue;
				double stepValue = diffValue / yHeight;
								
				// 倒序
				for (int index = listOfRmon.size() - 1; index > 0; index--) {
					RmonCount rmon = listOfRmon.get(index);
					long diff = maxValue - rmon.getValue();
					double dy = offset;
					if (rmon.getValue() == 0) {
						dy = yEnd;
					} else if (rmon.getValue() == maxValue) {
						// 如果最大值和最小值相同
						dy = offset;
					} else if (stepValue == 0) {
						dy = offset;
					} else {
						dy = diff / stepValue;
						dy = dy == 0 ? offset : dy + offset;
						// 如果超出Y轴则等于Y轴
						dy = dy > yEnd ? yEnd : dy;
					}
					if (lastX == 0 && lastY == 0) {
						graphLine.setLine(dx, dy, dx, dy);
					} else {
						graphLine.setLine(lastX, lastY, dx, dy);
					}
					
					lastX = dx;
					lastY = dy;
					g2d.draw(graphLine);
					dx -= stepX;
					if (dx <= offset) {
						dx = offset;
						break;
					}
				}				

				if (listOfRmon.size() > 15) {
					// 最前时间标签
					Date sampleTime = listOfRmon.get(0).getSampleTime();
					int yEndOffset = 12;
					if (sampleTime != null) {
						g2d.drawString(formatter.format(sampleTime), (int) dx, yEnd + yEndOffset);
					}
				}
			}
		}
		
		public void addRmon(RmonCount rmon) {
			listOfRmon.add(rmon);
			
			if (listOfRmon.size() == 1 && rmon.getValue() != 0) {
				maxValue = rmon.getValue() + 100;
				minValue = rmon.getValue() - 50;
			}
			
			if (rmon.getValue() > maxValue) {
				maxValue = rmon.getValue() + 100;
			}
			
			if (rmon.getValue() < minValue) {
				minValue = rmon.getValue() - 50;
			}
			repaint();
		}
		
		public void reset() {
			minValue = 0;
			maxValue = 1000;
			listOfRmon.clear();
			repaint();
		}

		private static final int row = 10;
		private static final int column = 20;
		private static final int offset = 50;
		private static final int stepX = 5;
		
		private long maxValue;
		private long minValue;
		private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		private final List<RmonCount> listOfRmon = new ArrayList<RmonCount>();
		private static final long serialVersionUID = -338034022288207846L;
    }
}