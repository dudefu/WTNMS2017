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

@Component(IETH9307EmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class IETH9307EmluatorBuilder implements EmluatorBuilder{
	
	public static final String ID = "IETH9307EmluatorBuilder";
	public static final String EMULATION_TYPE = "IETH9307";
	private final double offset = 5;

	private Map<Integer, PartFigure> figurePortIndex;
	private Map<Integer,LightEdit> dataSignalMap;
	private Map<Integer,LightEdit> workSignalMap;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;
	
	public IETH9307EmluatorBuilder(){
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSignalMap = new HashMap<Integer, LightEdit>();
		workSignalMap = new HashMap<Integer, LightEdit>();
	}
	
	
	@Override
	public void buildLights(Drawing drawing, SwitchNodeEntity switchNodeEntity) {
		
		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		
		locationDataMap.put(1, new Point(101, 91));
		locationDataMap.put(2, new Point(172, 124));
		locationDataMap.put(3, new Point(172, 45));
		locationDataMap.put(4, new Point(218, 124));
		locationDataMap.put(5, new Point(218, 45));
		locationDataMap.put(6, new Point(264, 124));
		locationDataMap.put(7, new Point(264, 45));
		locationDataMap.put(8, new Point(495, 88));//G1
		locationDataMap.put(9, new Point(515, 88));//G2
		locationDataMap.put(10, new Point(535, 88));//G3
		locationDataMap.put(Constant.RUN, new Point(515, 52));//run

		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		
		locationWorkMap.put(1, new Point(130, 91));
		locationWorkMap.put(2, new Point(201, 124));
		locationWorkMap.put(3, new Point(201, 45));
		locationWorkMap.put(4, new Point(247, 124));
		locationWorkMap.put(5, new Point(247, 45));
		locationWorkMap.put(6, new Point(293, 124));
		locationWorkMap.put(7, new Point(293, 45));
		
		locationWorkMap.put(Constant.POWER, new Point(495, 52));//power
		locationWorkMap.put(Constant.ALAEM, new Point(535, 52));//alarm

		for(SwitchPortEntity portEntity : switchNodeEntity.getPorts()){
			AffineTransform at = new AffineTransform();
			
			int portNo = portEntity.getPortNO();
			
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
			Point dataPoint = locationDataMap.get(portNo);
			at.translate(dataPoint.x + offset, dataPoint.y + offset);
			dataLightFigure.transform(at);
			drawing.add(dataLightFigure);
			dataSignalMap.put(portNo, dataLightEdit);

			if(portEntity.getType() == SwitchPortCategory.POWER_PORT){
				LightEdit workLightEdit = new LightEdit(portCategory,width, height);
				LightFigure workLightFigure = workLightEdit
				.restoreFigure(switchNodeEntity);
				Point workPoint = locationWorkMap.get(portNo);
				at = new AffineTransform();
				at.translate(workPoint.x + offset, workPoint.y + offset);
				workLightFigure.transform(at);
				drawing.add(workLightFigure);
				workSignalMap.put(portNo, workLightEdit);
			}
		}
		
		AffineTransform powerAt = new AffineTransform();
		LightEdit powerLightEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure powerFigure = powerLightEdit.restoreFigure(switchNodeEntity);
		Point powerPoint = locationWorkMap.get(Constant.POWER);
		powerAt.translate(powerPoint.x + offset, powerPoint.y + offset);
		powerFigure.transform(powerAt);
		drawing.add(powerFigure);
		workSignalMap.put(Constant.POWER, powerLightEdit);
		powerLightEdit.turnOn();
		
		LightEdit alarmEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure alarmFigure = alarmEdit.restoreFigure(switchNodeEntity);
		Point alarmPoint = locationWorkMap.get(Constant.ALAEM);
		powerAt = new AffineTransform(); 
		powerAt.translate(alarmPoint.x + offset, alarmPoint.y + offset);
		alarmFigure.transform(powerAt);
		drawing.add(alarmFigure);
		workSignalMap.put(Constant.ALAEM, alarmEdit);
		alarmEdit.turnOn();
		
		LightEdit runEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure runFigure = runEdit.restoreFigure(switchNodeEntity);
		Point runPoint = locationDataMap.get(Constant.RUN);
		powerAt = new AffineTransform(); 
		powerAt.translate(runPoint.x + offset, runPoint.y + offset);
		runFigure.transform(powerAt);
		drawing.add(runFigure);
		dataSignalMap.put(Constant.RUN, runEdit);
		runEdit.turnOn();
	}

	@Override
	public void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity) {
		
		Image backgroundImage = imageRegistry.getImageIcon(NetworkConstants.IETH9307_E).getImage();
		Image powerUpImage = imageRegistry.getImageIcon(NetworkConstants.IETH9307POWER_UP).getImage();
		Image powerDownImage = imageRegistry.getImageIcon(NetworkConstants.IETH9307POWER_DOWN).getImage();
		
		AffineTransform at = new AffineTransform();
		PanelFigure emulationalFigure = new PanelFigure(Images.toBufferedImage(backgroundImage));
		drawing.add(emulationalFigure);

		at = new AffineTransform();
		at.translate(offset, offset);
		emulationalFigure.transform(at);
		
		List<SwitchPortEntity> powerEntity = new ArrayList<SwitchPortEntity>();
		
		for(SwitchPortEntity switchPortEntity : switchNodeEntity.getPorts()){
			if(switchPortEntity.getType() == switchPortCategory.POWER.getValue()){
				powerEntity.add(switchPortEntity);
			}
		}
		
		Map<Integer, Point> locationMap = new HashMap<Integer, Point>();
		
		// µç¿Ú
		locationMap.put(1, new Point(100, 90));
		locationMap.put(2, new Point(171, 89));
		locationMap.put(3, new Point(171, 44));
		locationMap.put(4, new Point(217, 89));
		locationMap.put(5, new Point(217, 44));
		locationMap.put(6, new Point(263, 89));
		locationMap.put(7, new Point(263, 44));
		
		for(SwitchPortEntity portEntity : powerEntity){
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			int portNo = portEntity.getPortNO();
			if((portNo % 2) != 0){
				partFigure.setBounds(new Rectangle2D.Double(0, 0, powerUpImage
						.getWidth(null), powerUpImage.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(powerUpImage));
			}else{
				partFigure.setBounds(new Rectangle2D.Double(0, 0, powerDownImage
						.getWidth(null), powerDownImage.getHeight(null)));
				partFigure.setBufferedImage(Images.toBufferedImage(powerDownImage));
			}
			at = new AffineTransform();
			Point point = locationMap.get(portNo);
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationalFigure.add(partFigure);
			figurePortIndex.put(portNo, partFigure);
		}
	}

	@Override
	public Map<Integer, LightEdit> getDataSingalMap() {
		return dataSignalMap;
	}

	@Override
	public Map<Integer, PartFigure> getPartMap() {
		return figurePortIndex;
	}

	@Override
	public Map<Integer, LightEdit> getWorkSingalMap() {
		return workSignalMap;
	}
}
