package com.jhw.adm.server.servic;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.WarningCategoryBean;
import com.jhw.adm.server.entity.util.WarningLevelBean;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.FepWarningLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.util.Tools;

@Stateless
@Local(FepWarningLocal.class)
public class FepWarning implements FepWarningLocal{

	@EJB
	private SMTCServiceLocal smtcService;
	
	@EJB
	private CommonServiceBeanLocal commonServiceBeanLocal;
	
	
	private Logger logger = Logger.getLogger(LoginService.class.getName());
	
	private FEPEntity fepEntity = null;
	
	private int fepOperate ;  //登录成功,正常关闭,异常退出
	
	@Override
	public void sendFepMessageToClient(FEPEntity fepEntity,int fepOperate) {
		// TODO Auto-generated method stub

		this.fepEntity = fepEntity;
		this.fepOperate = fepOperate;
		
		WarningEntity warningEntity = getWarningEntity();
		
		//保存数据库
		commonServiceBeanLocal.saveEntity(warningEntity);
		
		//向客户端发送告警
		try {
			smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEntity.getWarningEvent(),
					warningEntity.getIpValue(), "", -1, warningEntity);
		} catch (JMSException e) {
			logger.info("",e);
		}
		
		if (Tools.isFepOnline){
			logger.info("发送了前置机与服务端连接的消息");
		}
		else{
			logger.info("发送了前置机与服务端断开的消息");
		}
	}
	
	/**
	 * 组合WarningEntity
	 * @return
	 */
	private WarningEntity getWarningEntity(){
		WarningEntity warningEntity = new WarningEntity();
		
		String ip = "";
		if (fepEntity != null){
			ip = fepEntity.getIpValue();
		}
		
		int warningEvent = -1;
		if (Tools.isFepOnline){
			warningEvent = Constants.FEP_CONNECT;
		}
		else{
			warningEvent = Constants.FEP_DISCONNECT;
		}
		int currentStatus = Constants.UNCONFIRM;

		Date createDate = new Date();
		
		int warningLevel = WarningLevelBean.getInstance().getWarningLevel(warningEvent);
		int warningCategory = WarningCategoryBean.getInstance().getWarningCategory(warningEvent);
		
		String descs = "";
		if (warningEvent == Constants.FEP_DISCONNECT){
			if (fepOperate == Tools.SHUTDOWN_NORMAL){
				descs = "前置机正常关闭";
				warningLevel = Constants.GENERAL;
			}
			else{
				descs = "前置机异常退出";
				warningLevel = Constants.SERIOUS;
			}
//			descs = WarningEventDescription.getInstance().getWarningEventDescription(warningEvent) + "与服务端的连接";
		}
		else{
			descs = "前置机登录成功";
//			descs = WarningEventDescription.getInstance().getWarningEventDescription(warningEvent) + "服务端";
		}
		
		//来源
		String source = "";   
		
		
		//子网ID
		Long subnetId = null;
		//子网名称
		String subnetName = "";
		
		if (fepEntity != null){
			FEPTopoNodeEntity topoNodeEntity = null;
			List<FEPTopoNodeEntity> fepTopoNodeEntities = (List<FEPTopoNodeEntity>) commonServiceBeanLocal.findAll(FEPTopoNodeEntity.class);
			if ((fepTopoNodeEntities != null) && fepTopoNodeEntities.size() > 0){
				for (FEPTopoNodeEntity topoentity : fepTopoNodeEntities){
					String code = topoentity.getCode().trim();
					String fepcode = fepEntity.getCode().trim();
					if (code.equals(fepcode)){
						topoNodeEntity = topoentity;
						break;
					}
				}
			}
			
			if (topoNodeEntity != null){
				if ((topoNodeEntity.getName() == null) || "".equals(topoNodeEntity.getName())){
					if (null != fepEntity){
						source = fepEntity.getIpValue();
					}
				}
				else{
					source = topoNodeEntity.getName();
				}
				
				String guid = topoNodeEntity.getParentNode();
				if (guid != null){
					NodeEntity subnetEntity = commonServiceBeanLocal.querySubNetTopoNodeEntity(guid);
					if (subnetEntity != null){
						subnetName = subnetEntity.getName();
						subnetId = subnetEntity.getId();
					}
				}
				else{
					subnetName = topoNodeEntity.getTopDiagramEntity().getName();
				}
				warningEntity.setNodeId(topoNodeEntity.getId());
			}
		}
		
		warningEntity.setNodeName(source);
		warningEntity.setSubnetName(subnetName);
		warningEntity.setSubnetId(subnetId);
		
		warningEntity.setIpValue(ip);
		warningEntity.setWarningEvent(warningEvent);
		warningEntity.setCurrentStatus(currentStatus);
		warningEntity.setCreateDate(createDate);
		warningEntity.setWarningLevel(warningLevel);
		warningEntity.setWarningCategory(warningCategory);
		warningEntity.setDescs(descs);
		
		return warningEntity;
	
	}

	

}
