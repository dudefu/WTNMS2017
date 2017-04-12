package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.Action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;

@Component(RefreshDiagramAction.ID)
public class RefreshDiagramAction extends AbstractAction {
	
	@PostConstruct
	protected void initialize() {
//		putValue(Action.NAME, "Ë¢ÐÂ");
		putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(NetworkConstants.REFRESH));
		putValue(Action.SHORT_DESCRIPTION, "Ë¢ÐÂ");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		equipmentModel.updateDiagram(equipmentRepository.findDiagram(clientModel.getCurrentUser()));
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	private static final long serialVersionUID = 1L;
	public static final String ID = "refreshDiagramAction";
}