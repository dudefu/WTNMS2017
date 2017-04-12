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

@Component(IETH802EmluatorBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class IETH802EmluatorBuilder implements EmluatorBuilder {
	
	public IETH802EmluatorBuilder() {
		figurePortIndex = new HashMap<Integer, PartFigure>();
		dataSingalMap = new HashMap<Integer, LightEdit>();
		workSingalMap = new HashMap<Integer, LightEdit>();
	}

	public void buildPorts(Drawing drawing, SwitchNodeEntity switchNodeEntity) {

		Image fddiImage = imageRegistry.getImageIcon(NetworkConstants.FDDI).getImage();
		Image powerImage = imageRegistry.getImageIcon(NetworkConstants.POWER).getImage();
		Image powerImageDown = imageRegistry.getImageIcon(NetworkConstants.POWERdown).getImage();
		Image uartImage = imageRegistry.getImageIcon(NetworkConstants.UART).getImage();
		
		AffineTransform at = new AffineTransform();
		PanelFigure emulationlFigure = new SwitcherPanelEdit().restoreFigure(switchNodeEntity);
		drawing.add(emulationlFigure);
		
		at = new AffineTransform();
		at.translate(offset, offset);
		emulationlFigure.transform(at);
		List<SwitchPortEntity> fddiPortList = new ArrayList<SwitchPortEntity>();
		List<SwitchPortEntity> powerPortList = new ArrayList<SwitchPortEntity>();
		List<SwitchSerialPort> uartPortList = new ArrayList<SwitchSerialPort>();

		if (switchNodeEntity.getPorts() == null) {
			switchNodeEntity.setPorts(new HashSet<SwitchPortEntity>());
		}
		if(switchNodeEntity.getSerialPorts() == null) {
			switchNodeEntity.setSerialPorts(new HashSet<SwitchSerialPort>());
		}
		
		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			if (portEntity.getType() == switchPortCategory.POWER.getValue()) {
				powerPortList.add(portEntity);
			}
			if (portEntity.getType() == switchPortCategory.FDDI.getValue()) {
				fddiPortList.add(portEntity);
			}
		}
		for(SwitchSerialPort portEntity : switchNodeEntity.getSerialPorts()){
			uartPortList.add(portEntity);
		}

		// IETH802≤Âø⁄Œª÷√
		Map<Integer, Point> locationMap = new HashMap<Integer, Point>();
		locationMap.put(1, new Point(736, 26));
		locationMap.put(2, new Point(736, 85));
		locationMap.put(3, new Point(664, 26));
		locationMap.put(4, new Point(664, 85));
		
		locationMap.put(5, new Point(592, 26));
		locationMap.put(6, new Point(592, 85));
		locationMap.put(7, new Point(375, 26));
		locationMap.put(8, new Point(375, 85));
		locationMap.put(9, new Point(190, 85));
		
		Map<Integer, Point> locationSerialMap = new HashMap<Integer, Point>();
		locationSerialMap.put(1, new Point(445, 100));
		
		for (SwitchPortEntity portEntity : fddiPortList) {
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			partFigure.setBounds(new Rectangle2D.Double(0, 0, 
					fddiImage.getWidth(null), fddiImage.getHeight(null)));
			partFigure.setBufferedImage(Images.toBufferedImage(fddiImage));
			at = new AffineTransform();
			Point point = locationMap.get(portEntity.getPortNO());
			at.translate(point.x + offset, point.y + offset);
			partFigure.transform(at);
			emulationlFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNO(), partFigure);
		}
		
		for (SwitchPortEntity portEntity : powerPortList) {
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure = partEdit.restoreFigure(portEntity);
			int portNo = portEntity.getPortNO();
			partFigure.setBounds(new Rectangle2D.Double(0, 0, 
					powerImage.getWidth(null), powerImage.getHeight(null)));
			if(1==portNo || 3==portNo || 5==portNo){
				partFigure.setBufferedImage(Images.toBufferedImage(powerImage));
			}else{
				partFigure.setBufferedImage(Images.toBufferedImage(powerImageDown));
			}
			at = new AffineTransform();

			Point point = locationMap.get(portEntity.getPortNO());
			at.translate(point.x + offset, point.y + offset);
			
			partFigure.transform(at);
			emulationlFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNO(), partFigure);
		}
		for(SwitchSerialPort portEntity : uartPortList)
		{
			PartEdit partEdit = new PartEdit();
			PartFigure partFigure =  partEdit.restoreFigure(portEntity);
			partFigure.setBounds(new Rectangle2D.Double(0,0,uartImage.getWidth(null),uartImage.getHeight(null)));
			partFigure.setBufferedImage(Images.toBufferedImage(uartImage));
			at = new AffineTransform();
			
			Point point = locationSerialMap.get(portEntity.getPortNo());
			at.translate(point.x + offset, point.y + offset);
			
			partFigure.transform(at);
			emulationlFigure.add(partFigure);
			figurePortIndex.put(portEntity.getPortNo(), partFigure);
		}
	}

	public void buildLights(Drawing glassDrawing, SwitchNodeEntity switchNodeEntity) {
		
		
		//…¡µ∆Œª÷√
		Map<Integer, Point> locationDataMap = new HashMap<Integer, Point>();
		locationDataMap.put(1, new Point(766,27));
		locationDataMap.put(2, new Point(766,120));
		locationDataMap.put(3, new Point(694,27));
		locationDataMap.put(4, new Point(694,120));
		
		locationDataMap.put(5, new Point(622,27));
		locationDataMap.put(6, new Point(622,120));
		locationDataMap.put(7, new Point(405,17));
		locationDataMap.put(8, new Point(405,132));
		locationDataMap.put(9, new Point(300,132));
		
		locationDataMap.put(Constant.RUN, new Point(216, 12));//run
		
		//≤ª…¡µ∆Œª÷√
		Map<Integer, Point> locationWorkMap = new HashMap<Integer, Point>();
		locationWorkMap.put(1, new Point(737,27));
		locationWorkMap.put(2, new Point(737,120));
		locationWorkMap.put(3, new Point(665,27));
		locationWorkMap.put(4, new Point(665,120));
		
		locationWorkMap.put(5, new Point(593,27));
		locationWorkMap.put(6, new Point(593,120));
		locationWorkMap.put(7, new Point(376,18));
		locationWorkMap.put(8, new Point(376,132));
		locationWorkMap.put(9, new Point(270,132));
		
		locationWorkMap.put(Constant.POWER, new Point(95,12));//power
		locationWorkMap.put(Constant.ALAEM, new Point(155,12));//alarm
		
		
		
		for (SwitchPortEntity portEntity : switchNodeEntity.getPorts()) {
			if (portEntity.getType() == switchPortCategory.POWER.getValue()) {

				AffineTransform at = new AffineTransform();
				LightEdit lightEdit1 = new LightEdit(LightEdit.RECTANGLE_LIGHT, 15d, 6d);
				LightFigure light1 = lightEdit1.restoreFigure(switchNodeEntity);
				Point point = locationDataMap.get(portEntity.getPortNO());
				at.translate(point.x + offset, point.y + offset);
				light1.transform(at);
				glassDrawing.add(light1);
				dataSingalMap.put(portEntity.getPortNO(), lightEdit1);
//				lightEdit1.flash();
				
				LightEdit lightEdit2 = new LightEdit(LightEdit.RECTANGLE_LIGHT, 15d, 6d);
				LightFigure light2 = lightEdit2.restoreFigure(switchNodeEntity);
				at = new AffineTransform();
				Point point1 = locationWorkMap.get(portEntity.getPortNO());
				at.translate(point1.x + offset, point1.y + offset);
				light2.transform(at);
				glassDrawing.add(light2);
				workSingalMap.put(portEntity.getPortNO(), lightEdit2);
//				lightEdit2.flash();
			}
			if (portEntity.getType() == switchPortCategory.FDDI.getValue()) {
				
				AffineTransform at = new AffineTransform();
				LightEdit lightEdit1 = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
				LightFigure light1 = lightEdit1.restoreFigure(switchNodeEntity);
				Point point = locationDataMap.get(portEntity.getPortNO());
				at.translate(point.x + offset, point.y + offset);
				light1.transform(at);
				glassDrawing.add(light1);
				dataSingalMap.put(portEntity.getPortNO(), lightEdit1);
//				lightEdit1.flash();
				
				LightEdit lightEdit2 = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
				LightFigure light2 = lightEdit2.restoreFigure(switchNodeEntity);
				at = new AffineTransform();
				Point point1 = locationWorkMap.get(portEntity.getPortNO());
				at.translate(point1.x + offset, point1.y + offset);
				light2.transform(at);
				glassDrawing.add(light2);
				workSingalMap.put(portEntity.getPortNO(), lightEdit2);
//				lightEdit2.flash();
			}
			
		}
		AffineTransform at = new AffineTransform();
		LightEdit pwrEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure pwrLight = pwrEdit.restoreFigure(switchNodeEntity);
		Point pwrPoint = locationWorkMap.get(Constant.POWER);
		at.translate(pwrPoint.x + offset, pwrPoint.y + offset);
		pwrLight.transform(at);
		glassDrawing.add(pwrLight);
		workSingalMap.put(new Integer(Constant.POWER), pwrEdit);
//			pwrEdit.turnOn();
		
		LightEdit almEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure almLight = almEdit.restoreFigure(switchNodeEntity);
		at = new AffineTransform();
		Point almPoint = locationWorkMap.get(Constant.ALAEM);
		at.translate(almPoint.x + offset, almPoint.y + offset);
		almLight.transform(at);
		glassDrawing.add(almLight);
		workSingalMap.put(new Integer(Constant.ALAEM), almEdit);
//			almEdit.turnOn();
		LightEdit runEdit = new LightEdit(LightEdit.ELLIPSE_LIGHT, 10d, 10d);
		LightFigure runFigure = runEdit.restoreFigure(switchNodeEntity);
		at = new AffineTransform(); 
		Point runPoint = locationDataMap.get(Constant.RUN);
		at.translate(runPoint.x + offset, runPoint.y + offset);
		runFigure.transform(at);
		glassDrawing.add(runFigure);
		workSingalMap.put(new Integer(Constant.RUN), runEdit);
		runEdit.flash();
	}
	
	public Map<Integer, PartFigure> getPartMap() {		
		return figurePortIndex;
	}
	public Map<Integer, LightEdit> getDataSingalMap() {		
		return dataSingalMap;
	}
	public Map<Integer, LightEdit> getWorkSingalMap() {		
		return workSingalMap;
	}
	
	@Resource(name=SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	private Map<Integer,LightEdit> dataSingalMap;
	private Map<Integer,LightEdit> workSingalMap;
	private Map<Integer, PartFigure> figurePortIndex;
	private double offset = 5d;
	public static final String EMULATION_TYPE = "IETH802";
	public static final String ID = "IETH802EmluatorBuilder";
}