package com.jhw.adm.client.diagram;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
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

@Component(IETH8008HEmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class IETH8008HEmluatorBuilder implements EmluatorBuilder {

	public static final String ID = "IETH8008HEmluatorBuilder";
	public static final String EMULATION_TYPE = "IETH8008";
	private final double offset = 5;

	@Resource(name = ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	@Resource(name = SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;

	private final Map<Integer, PartFigure> figurePortIndex;
	private final Map<Integer, LightEdit> dataSignalMap;
	private final Map<Integer, LightEdit> workSignalMap;

	public IETH8008HEmluatorBuilder() {
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSignalMap = new HashMap<Integer, LightEdit>();
		workSignalMap = new HashMap<Integer, LightEdit>();
	}

	@Override
	public void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Image backgroundImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008_E).getImage();
		Image fddiImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008FDDI).getImage();
		Image powerImage = imageRegistry.getImageIcon(NetworkConstants.IETH8008POWER).getImage();

		AffineTransform at = new AffineTransform();
		PanelFigure emulationalFigure = new PanelFigure(Images
				.toBufferedImage(backgroundImage));
		drawing.add(emulationalFigure);

		at = new AffineTransform();
		at.translate(offset, offset);
		emulationalFigure.transform(at);

		List<SwitchPortEntity> fddiEntity = new ArrayList<SwitchPortEntity>();
		List<SwitchPortEntity> powerEntity = new ArrayList<SwitchPortEntity>();

		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			if (portEntity.getType() == switchPortCategory.POWER.getValue()) {
				powerEntity.add(portEntity);
			}
			if (portEntity.getType() == switchPortCategory.FDDI.getValue()) {
				fddiEntity.add(portEntity);
			}
		}

		Map<Integer, Point> locationMap = new HashMap<Integer, Point>();

		// 光口
		locationMap.put(3, new Point(201, 47));
		locationMap.put(4, new Point(83, 47));

		// 电口
		locationMap.put(1, new Point(430, 28));
		locationMap.put(2, new Point(355, 28));
		locationMap.put(5, new Point(220, 100));
		locationMap.put(6, new Point(174, 100));
		locationMap.put(7, new Point(128, 100));
		locationMap.put(8, new Point(82, 100));

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
	}

	public void buildLights(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		locationDataMap.put(3, new Point(296,50));
		locationDataMap.put(4, new Point(177,50));
		
		locationDataMap.put(1, new Point(431, 29));
		locationDataMap.put(2, new Point(356,29));
		locationDataMap.put(5, new Point(221,101));
		locationDataMap.put(6, new Point(175,101));
		locationDataMap.put(7, new Point(129,101));
		locationDataMap.put(8, new Point(83,101));

		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		locationWorkMap.put(3, new Point(296, 67));
		locationWorkMap.put(4, new Point(177, 67));
		
		locationWorkMap.put(1, new Point(460,29));
		locationWorkMap.put(2, new Point(385,29));
		locationWorkMap.put(5, new Point(250,101));
		locationWorkMap.put(6, new Point(204,101));
		locationWorkMap.put(7, new Point(158,101));
		locationWorkMap.put(8, new Point(112,101));
		
		locationWorkMap.put(Constant.POWER, new Point(354,110));//power
		locationWorkMap.put(Constant.ALAEM, new Point(402,110));//alarm

		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			AffineTransform at = new AffineTransform();
			
			int portCategory = LightEdit.RECTANGLE_LIGHT;
			double width = 15d;
			double height = 6d;
			
			if (portEntity.getType() == SwitchPortCategory.FDDI_PORT) {
				portCategory = LightEdit.ELLIPSE_LIGHT;
				width = 10d;
				height = 10d;
			}
			
			LightEdit dataLightEdit = new LightEdit(portCategory,width, height);
			LightFigure dataLightFigure = dataLightEdit.restoreFigure(switchNodeEntity);
			Point dataPoint = locationDataMap.get(portEntity.getPortNO());
			at.translate(dataPoint.x + offset, dataPoint.y + offset);
			dataLightFigure.transform(at);
			drawing.add(dataLightFigure);
			dataSignalMap.put(portEntity.getPortNO(), dataLightEdit);

			LightEdit workLightEdit = new LightEdit(portCategory,width, height);
			LightFigure workLightFigure = workLightEdit
					.restoreFigure(switchNodeEntity);
			Point workPoint = locationWorkMap.get(portEntity.getPortNO());
			at = new AffineTransform();
			at.translate(workPoint.x + offset, workPoint.y + offset);
			workLightFigure.transform(at);
			drawing.add(workLightFigure);
			workSignalMap.put(portEntity.getPortNO(), workLightEdit);
		}
		AffineTransform powerAt = new AffineTransform();
		LightEdit powerLightEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure powerFigure = powerLightEdit.restoreFigure(switchNodeEntity);
		Point powerPoint = locationWorkMap.get(Constant.POWER);
		powerAt.translate(powerPoint.x + offset, powerPoint.y + offset);
		powerFigure.transform(powerAt);
		drawing.add(powerFigure);
		workSignalMap.put(Constant.POWER, powerLightEdit);
		
		LightEdit alarmEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure alarmFigure = alarmEdit.restoreFigure(switchNodeEntity);
		Point alarmPoint = locationWorkMap.get(Constant.ALAEM);
		powerAt = new AffineTransform(); 
		powerAt.translate(alarmPoint.x + offset, alarmPoint.y + offset);
		alarmFigure.transform(powerAt);
		drawing.add(alarmFigure);
		workSignalMap.put(Constant.ALAEM, alarmEdit);
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
