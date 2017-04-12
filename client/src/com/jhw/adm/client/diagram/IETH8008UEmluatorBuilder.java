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

import org.jfree.util.Log;
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

@Component(IETH8008UEmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class IETH8008UEmluatorBuilder implements EmluatorBuilder {

	public static final String ID = "IETH8008UEmluatorBuilder";
	public static final String EMULATION_TYPE = "IETH8008-U";

	private final Map<Integer, PartFigure> figurePortIndex;
	private final Map<Integer, LightEdit> dataSignalMap;
	private final Map<Integer, LightEdit> workSignalMap;
	private final double offset = 5;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;

	public IETH8008UEmluatorBuilder() {
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSignalMap = new HashMap<Integer, LightEdit>();
		workSignalMap = new HashMap<Integer, LightEdit>();
	}

	@Override
	public void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Image backgroundImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008U_E).getImage();

		Image fddiImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008UFDDI).getImage();
		Image powerImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008UPOWER).getImage();

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
		locationMap.put(1, new Point(85,60));
		locationMap.put(2, new Point(124,60));
		locationMap.put(3, new Point(163,60));
		locationMap.put(4, new Point(202,60));

		// 电口
		locationMap.put(5, new Point(240,60));
		locationMap.put(6, new Point(260,60));
		locationMap.put(7, new Point(280,60));
		locationMap.put(8, new Point(300,60));

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
			if (point == null) {
				Log.error(String.format("Port[%s] location is null", portEntity.getPortNO()));
			} else {
				at.translate(point.x + offset, point.y + offset);
				partFigure.transform(at);
				emulationalFigure.add(partFigure);
				figurePortIndex.put(portEntity.getPortNO(), partFigure);
			}
		}
	}

	public void buildLights(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		locationDataMap.put(1, new Point(350,59));
		locationDataMap.put(2, new Point(360,59));
		locationDataMap.put(3, new Point(370,59));
		locationDataMap.put(4, new Point(380,59));
		locationDataMap.put(5, new Point(390,59));
		locationDataMap.put(6, new Point(400,59));
		locationDataMap.put(7, new Point(410,59));
		locationDataMap.put(8, new Point(420,59));

		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		locationWorkMap.put(1, new Point(350,74));
		locationWorkMap.put(2, new Point(360,74));
		locationWorkMap.put(3, new Point(370,74));
		locationWorkMap.put(4, new Point(380,74));
		locationWorkMap.put(5, new Point(390,74));
		locationWorkMap.put(6, new Point(400,74));
		locationWorkMap.put(7, new Point(410,74));
		locationWorkMap.put(8, new Point(420,74));
		
		locationWorkMap.put(Constant.POWER, new Point(339,74));//power
		locationWorkMap.put(Constant.ALAEM, new Point(339,59));//alarm

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
