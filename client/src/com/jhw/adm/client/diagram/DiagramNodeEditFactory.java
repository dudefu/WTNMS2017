package com.jhw.adm.client.diagram;

import com.jhw.adm.client.draw.NodeEdit;
import com.jhw.adm.client.draw.NodeEditFactory;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.RingEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

public class DiagramNodeEditFactory implements NodeEditFactory {

	@Override
	public NodeEdit<? extends NodeFigure> createNodeEdit(Object model) {
		NodeEdit<? extends NodeFigure> nodeEdit = null;
		if (model instanceof SwitchTopoNodeEntity) {
			nodeEdit = new SwitcherEdit();
		}
		if (model instanceof SwitchTopoNodeLevel3) {
			nodeEdit = new Layer3SwitcherEdit();
		}
		if (model instanceof CarrierTopNodeEntity) {
			nodeEdit = new CarrierEdit();
		}
		if (model instanceof EponTopoEntity) {
			nodeEdit = new OltEdit();
		}
		if (model instanceof ONUTopoNodeEntity) {
			nodeEdit = new OnuEdit();
		}
		if (model instanceof Epon_S_TopNodeEntity) {
			nodeEdit = new BeamsplitterEdit();
		}
		if (model instanceof FEPTopoNodeEntity) {
			nodeEdit = new FrontEndEdit();
		}
		if (model instanceof GPRSTopoNodeEntity) {
			nodeEdit = new GprsEdit();
		}
		if (model instanceof LinkEntity) {
			nodeEdit = new SmartLinkEdit();
		}
		if (model instanceof RingEntity) {
			nodeEdit = new RingEdit();
		}
		if (model instanceof SubNetTopoNodeEntity) {
			nodeEdit = new SubnetEdit();
		}
		
		if (model instanceof VirtualNodeEntity){
			nodeEdit = new VirtualElementEdit();
		}

		if(model instanceof CommentTopoNodeEntity){
			nodeEdit = new CommentAreaEdit();
		}
		
		return nodeEdit;
	}
}