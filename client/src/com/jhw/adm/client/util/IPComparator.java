package com.jhw.adm.client.util;

import java.util.Comparator;

import org.apache.commons.lang.BooleanUtils;

import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;

@SuppressWarnings("hiding")
public class IPComparator<NodeEntity> implements Comparator<NodeEntity> {

    /* @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return zero, or one as the
     * 	       first argument is less than & equal to, or greater than the
     *	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this comparator.
     */
	@Override
	public int compare(NodeEntity o1, NodeEntity o2) {
		
		String ipa = "";
		if(o1 instanceof SwitchTopoNodeEntity){
			ipa = ((SwitchTopoNodeEntity)o1).getIpValue();
		}else if(o1 instanceof SwitchTopoNodeLevel3){
			ipa = ((SwitchTopoNodeLevel3)o1).getIpValue();
		}else if(o1 instanceof VirtualNodeEntity){
			ipa = ((VirtualNodeEntity)o1).getIpValue();
		}
		String ipb = "";
		if(o2 instanceof SwitchTopoNodeEntity){
			ipb = ((SwitchTopoNodeEntity)o2).getIpValue();
		}else if(o2 instanceof SwitchTopoNodeLevel3){
			ipb = ((SwitchTopoNodeLevel3)o2).getIpValue();
		}else if(o1 instanceof VirtualNodeEntity){
			ipb = ((VirtualNodeEntity)o2).getIpValue();
		}
		
		return BooleanUtils.toInteger(InternetUtils.greaterThan(ipa, ipb));
	}
}
