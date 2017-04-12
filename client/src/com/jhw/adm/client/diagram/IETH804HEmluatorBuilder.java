package com.jhw.adm.client.diagram;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.util.Images;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.draw.LightEdit;
import com.jhw.adm.client.draw.LightFigure;
import com.jhw.adm.client.draw.PanelFigure;
import com.jhw.adm.client.draw.PartFigure;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(IETH804HEmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class IETH804HEmluatorBuilder implements EmluatorBuilder {

	public static final String ID = "IETH804HEmluatorBuilder";
	public static final String EMULATION_TYPE = "IETH804-H";

	private final Map<Integer, PartFigure> figurePortIndex;
	private final Map<Integer, LightEdit> dataSignalMap;
	private final Map<Integer, LightEdit> workSignalMap;
	private final double offset = 5;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;

	public IETH804HEmluatorBuilder() {
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSignalMap = new HashMap<Integer, LightEdit>();
		workSignalMap = new HashMap<Integer, LightEdit>();
	}

	@Override
	public void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Image backgroundImage = imageRegistry.getImageIcon(NetworkConstants.IETH804_E).getImage();

		Image fddiImage = imageRegistry.getImageIcon(NetworkConstants.IETH804FDDI).getImage();
		Image powerImage = imageRegistry.getImageIcon(NetworkConstants.IETH804POWER).getImage();
		Image uartImage = imageRegistry.getImageIcon(NetworkConstants.IETH804UART).getImage();

		AffineTransform at = new AffineTransform();
		PanelFigure emulationalFigure = new PanelFigure(Images
				.toBufferedImage(backgroundImage));
		drawing.add(emulationalFigure);

		at = new AffineTransform();
		at.translate(offset, offset);
		emulationalFigure.transform(at);

		List<SwitchPortEntity> fddiEntity = new ArrayList<SwitchPortEntity>();
		List<SwitchPortEntity> powerEntity = new ArrayList<SwitchPortEntity>();
		List<SwitchSerialPort> uartEntity = new ArrayList<SwitchSerialPort>();

		if (switchNodeEntity.getPorts() == null) {
			switchNodeEntity.setPorts(new HashSet<SwitchPortEntity>());
		}
		if (switchNodeEntity.getSerialPorts() == null) {
			switchNodeEntity.setSerialPorts(new HashSet<SwitchSerialPort>());
		}

		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			if (portEntity.getType() == switchPortCategory.POWER.getValue()) {
				powerEntity.add(portEntity);
			}
			if (portEntity.getType() == switchPortCategory.FDDI.getValue()) {
				fddiEntity.add(portEntity);
			}
		}
		for (SwitchSerialPort portEntity : switchNodeEntity.getSerialPorts()) {
			uartEntity.add(portEntity);
		}

		Map<Integer, Point> locationMap = new HashMap<Integer, Point>();

		// 光口
		locationMap.put(1, new Point(20, 115));
		locationMap.put(2, new Point(80, 115));
		locationMap.put(3, new Point(140, 115));
		locationMap.put(4, new Point(200, 115));

		// 电口
		locationMap.put(5, new Point(265, 115));
		locationMap.put(6, new Point(293, 115));
		locationMap.put(7, new Point(321, 115));
		locationMap.put(8, new Point(349, 115));

		// 串口
		Map<Integer, Point> locationSerialMap = new HashMap<Integer, Point>();
		locationSerialMap.put(1, new Point(23, 60));
		locationSerialMap.put(2, new Point(23, 15));
		locationSerialMap.put(3, new Point(112, 60));
		locationSerialMap.put(4, new Point(112, 15));

		for (SwitchPortEntity portEntity : powerEntity) {
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			partFigure.setBounds(new Rectangle2D.Double(0, 0, powerImage
					.getWidth(null), powerImage.getHeight(null)));
			partFigure.setBufferedImage(Images.toBufferedImage(powerImage));
			at = new AffineTransform();
			Point point = locationMap.get(portEntity.getPortNO());
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationalFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNO(), partFigure);
		}
		for (SwitchPortEntity portEntity : fddiEntity) {
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			partFigure.setBounds(new Rectangle2D.Double(0, 0, fddiImage
					.getWidth(null), fddiImage.getHeight(null)));
			partFigure.setBufferedImage(Images.toBufferedImage(fddiImage));
			at = new AffineTransform();
			Point point = locationMap.get(portEntity.getPortNO());
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationalFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNO(), partFigure);
		}
		for (SwitchSerialPort portEntity : uartEntity) {
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			partFigure.setBounds(new Rectangle2D.Double(0, 0, uartImage
					.getWidth(null), uartImage.getHeight(null)));
			partFigure.setBufferedImage(Images.toBufferedImage(uartImage));
			at = new AffineTransform();
			Point point = locationSerialMap.get(portEntity.getPortNo());
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationalFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNo(), partFigure);
		}
	}

	public void buildLights(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		locationDataMap.put(1, new Point(435, 114));
		locationDataMap.put(2, new Point(452, 114));
		locationDataMap.put(3, new Point(469, 114));
		locationDataMap.put(4, new Point(486, 114));
		locationDataMap.put(5, new Point(503, 114));
		locationDataMap.put(6, new Point(520, 114));
		locationDataMap.put(7, new Point(537, 114));
		locationDataMap.put(8, new Point(554, 114));

		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		locationWorkMap.put(1, new Point(435, 127));
		locationWorkMap.put(2, new Point(452, 127));
		locationWorkMap.put(3, new Point(469, 127));
		locationWorkMap.put(4, new Point(486, 127));
		locationWorkMap.put(5, new Point(503, 127));
		locationWorkMap.put(6, new Point(520, 127));
		locationWorkMap.put(7, new Point(537, 127));
		locationWorkMap.put(8, new Point(554, 127));
		
		locationWorkMap.put(Constant.POWER, new Point(416,127));//power
		locationWorkMap.put(Constant.ALAEM, new Point(416,114));//alarm

		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			AffineTransform at = new AffineTransform();
			LightEdit dataLightEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT,10d, 10d);
			LightFigure dataLightFigure = dataLightEdit.restoreFigure(switchNodeEntity);
			Point dataPoint = locationDataMap.get(portEntity.getPortNO());
			at.translate(dataPoint.x + offset, dataPoint.y + offset);
			dataLightFigure.transform(at);
			drawing.add(dataLightFigure);
			dataSignalMap.put(portEntity.getPortNO(), dataLightEdit);
//			dataLightEdit.flash();

			LightEdit workLightEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT,
					10d, 10d);
			LightFigure workLightFigure = workLightEdit
					.restoreFigure(switchNodeEntity);
			Point workPoint = locationWorkMap.get(portEntity.getPortNO());
			at = new AffineTransform();
			at.translate(workPoint.x + offset, workPoint.y + offset);
			workLightFigure.transform(at);
			drawing.add(workLightFigure);
			workSignalMap.put(portEntity.getPortNO(), workLightEdit);
//			workLightEdit.flash();
		}
		AffineTransform powerAt = new AffineTransform();
		LightEdit powerLightEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure powerFigure = powerLightEdit.restoreFigure(switchNodeEntity);
		Point powerPoint = locationWorkMap.get(Constant.POWER);
		powerAt.translate(powerPoint.x + offset, powerPoint.y + offset);
		powerFigure.transform(powerAt);
		drawing.add(powerFigure);
		workSignalMap.put(Constant.POWER, powerLightEdit);
//		powerLightEdit.changeColor(Color.RED);
//		powerLightEdit.flash();
		
		LightEdit alarmEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure alarmFigure = alarmEdit.restoreFigure(switchNodeEntity);
		Point alarmPoint = locationWorkMap.get(Constant.ALAEM);
		powerAt = new AffineTransform(); 
		powerAt.translate(alarmPoint.x + offset, alarmPoint.y + offset);
		alarmFigure.transform(powerAt);
		drawing.add(alarmFigure);
		workSignalMap.put(Constant.ALAEM, alarmEdit);
//		alarmEdit.flash();
	}

	@Override
	public Map<Integer, PartFigure> getPartMap() {
		return figurePortIndex;
	}

	@Override
	public Map<Integer, LightEdit> getDataSingalMap() {
		return dataSignalMap;
	}

	@Override
	public Map<Integer, LightEdit> getWorkSingalMap() {
		return workSignalMap;
	}

}
