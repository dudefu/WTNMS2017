package com.jhw.adm.client.core;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

@Component(SwitchNodeEntityAdapterFactory.ID)
public class SwitchNodeEntityAdapterFactory implements AdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class<?> adapterClass) {
		Object adapterObject = null;
		if (adapterClass.equals(SwitchTopoNodeEntity.class)) {
			if (adaptableObject instanceof SwitchTopoNodeEntity) {
				adapterObject = adaptableObject;
			}
			if (adaptableObject instanceof SwitchPortEntity) {
				SwitchPortEntity switchPortEntity = (SwitchPortEntity)adaptableObject;
				SwitchNodeEntity switchNodeEntity = switchPortEntity.getSwitchNode();
				adapterObject = asNode(switchNodeEntity);
			}
			if (adaptableObject instanceof SwitchSerialPort) {
				SwitchSerialPort switchSerialPort = (SwitchSerialPort)adaptableObject;
				SwitchNodeEntity switchNodeEntity = switchSerialPort.getSwitchNode();
				adapterObject = asNode(switchNodeEntity);
			}
		}
		return adapterObject;
	}
	
	private SwitchTopoNodeEntity asNode(SwitchNodeEntity switcher) {
		SwitchTopoNodeEntity switcherNode = null;
		String address = switcher.getBaseConfig().getIpValue();
		switcherNode = (SwitchTopoNodeEntity)remoteServer.getService().findSwitchTopoNodeByIp(address);
		
		return switcherNode;
	}

	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	public static final String ID = "switchNodeEntityAdapterFactory";
	public static Class<?> ADAPTER_CLASS = SwitchTopoNodeEntity.class;
}
