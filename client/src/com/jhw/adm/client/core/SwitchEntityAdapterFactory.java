package com.jhw.adm.client.core;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(SwitchEntityAdapterFactory.ID)
public class SwitchEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(SwitchNodeEntity.class)) {
			if (adaptableObject instanceof SwitchNodeEntity) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof SwitchTopoNodeEntity) {
				SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity)adaptableObject;
				adapterObject = NodeUtils.getNodeEntity(switchTopoNodeEntity).getNodeEntity();
			}
			if (adaptableObject instanceof SwitchPortEntity) {
				SwitchPortEntity switchPortEntity = (SwitchPortEntity)adaptableObject;
				adapterObject = switchPortEntity.getSwitchNode();
			}
			if (adaptableObject instanceof SwitchSerialPort) {
				SwitchSerialPort switchSerialPort = (SwitchSerialPort)adaptableObject;
				adapterObject = switchSerialPort.getSwitchNode();
			}
		}
		return adapterObject;
	}

	public static final String ID = "switchEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchNodeEntity.class;
}
