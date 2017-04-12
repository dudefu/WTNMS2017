package com.jhw.adm.client.diagram;

import java.util.Map;

import org.jhotdraw.draw.Drawing;

import com.jhw.adm.client.draw.LightEdit;
import com.jhw.adm.client.draw.PartFigure;
import com.jhw.adm.server.entity.epon.OLTEntity;

public interface EponEmluatorBuilder {
	void buildPorts(Drawing drawing, OLTEntity eponEntity);
	void buildLights(Drawing drawing, OLTEntity eponEntity);
	Map<Integer, PartFigure> getPartMap();
	Map<Integer, LightEdit> getDataSingalMap();
	Map<Integer, LightEdit> getUpDataSingalMap();
	Map<Integer, LightEdit> getDownDataSingalMap();
	Map<Integer, LightEdit> getWorkSingalMap();
}
