package com.jhw.adm.server.servic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.util.CheckUserBean;
import com.jhw.adm.server.entity.util.License;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PropertyValue;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.TimerMonitoring;
import com.jhw.adm.server.interfaces.CheckClientThreadLocal;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.FepWarningLocal;
import com.jhw.adm.server.interfaces.LoginServiceLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.PingTimerThreadLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.util.CacheDatas;
import com.jhw.adm.server.util.CryptoUtils;
import com.jhw.adm.server.util.DateFormatter;
import com.jhw.adm.server.util.Tools;

/**
 * Session Bean implementation class LoginService
 */
@Stateless
@RemoteBinding(jndiBinding = "remote/LoginService")
public class LoginService implements LoginServiceRemote, LoginServiceLocal {
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@EJB
	private SMTCServiceLocal smtcService;
	@EJB
	private DataCacheLocal datacache;
	@EJB
	private NMSServiceLocal nmsServiceLocal;
	@EJB
	private CommonServiceBeanLocal commonServiceBeanLocal;
	@EJB
	private PingTimerThreadLocal pingTimerThreadLocal;
	@EJB
	private CheckClientThreadLocal checkClientThreadLocal;
	
	@EJB
	private FepWarningLocal fepWarningLocal;

	private static int CLIENTTIMER = 0;
	private Logger logger = Logger.getLogger(LoginService.class.getName());

	public FEPEntity loginFEP(String code, String password) {
		FEPEntity fep = commonServiceBeanLocal.findFepEntity(code);
		this.startPingTimer();// ������ʱPing���
		this.changeMonitoringStatus();// ���Ķ�ʱ�������״̬Ϊδ���
		if (fep != null) {
			String oldpw = fep.getLoginPassword();
			if (password.equals(oldpw)) {
				logger.info("ǰ�û���¼�ɹ���**" + code);
				fep.getStatus().setCurrentTime(new Date().getTime());
				fep.getStatus().setStatus(true);
				manager.merge(fep.getStatus());
				datacache.addFEPEntity(code, fep);
				try {
					smtcService.sendHeartBeatObjMeg(
							MessageNoConstants.FEP_STATUSTYPE, "server", fep);
				} catch (JMSException e) {
					e.printStackTrace();
				}

				if(!Tools.isFepOnline){
					Tools.isFepOnline = true;
					fepWarningLocal.sendFepMessageToClient(fep,Tools.LOGOUT_SUCCEED);
				}
				
				return fep;
			} else {
				logger.info("ǰ�û���¼ʧ�ܣ�**" + code);
				return null;
			}
		}

		return null;
	}

	public void changeMonitoringStatus() {
		List<TimerMonitoring> results = nmsServiceLocal.queryTimerMonitoring();

		if (results != null && results.size() > 0) {
			for (TimerMonitoring timerMonitoring : results) {
				timerMonitoring.setSendTag(false);
				manager.merge(timerMonitoring);
			}
		}

	}

	public FEPEntity getFEPByCode(String code) {
		String jpql = "select entity from " + FEPEntity.class.getName()
				+ " as entity where entity.code=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, code);
		List datas = query.getResultList();
		if (datas != null && datas.size() > 0) {
			FEPEntity fep = (FEPEntity) datas.get(0);
			return fep;

		} else {
			return null;
		}
	}

	public CheckUserBean longinClient(String userName, String password) {
		CheckUserBean userBean = new CheckUserBean();
//		userBean=initDatas(userBean,userName);
//		if(userBean.getErrorCode()>0){
//			return userBean;
//		}
		String jpql = "select entity from " + UserEntity.class.getName()
				+ " as entity where entity.userName=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, userName);
		List<UserEntity> datas = query.getResultList();
		if (datas != null && datas.size() > 0) {
			datacache.cacheAllFeps();
			UserEntity user = datas.get(0);
			if (user.getPassword().equals(password)) {
				logger.info("Client��¼�ɹ���**" + userName);
				if (CacheDatas.getInstance().getUser(userName) == null) {
					CacheDatas.getInstance().addUser(user);
				}
				if (CLIENTTIMER == 0) {
					checkClientThreadLocal.startClientTimer(30000);
					CLIENTTIMER++;
				}
				userBean.setUserEntity(user);
				userBean.setErrorCode(0);
				return userBean;

			} else {
				userBean.setErrorCode(2);
				userBean.setErrorMessage("�û���[" + userName + "]����������������롣");
				logger.warn("Client��¼�������**");
				return userBean;
			}
		} else {
			userBean.setErrorMessage("�û���[" + userName + "]�����ڣ����������롣");
			userBean.setErrorCode(1);
			return userBean;
		}
	}

	public void loginOffClient(String userName) {
		if (userName != null && !userName.equals("")) {
			CacheDatas.getInstance().removeUser(userName);
			logger.info("Client�˳���**" + userName);
		} else {
			logger.error("Client�˳�ʱUserNameΪ�ա�");
		}
	}

	public void logoffFep(String code) {
		try {
			FEPEntity fepEntity = commonServiceBeanLocal.findFepEntity(code);
			if (fepEntity != null) {
				fepEntity.getStatus().setStatus(false);
				smtcService.sendHeartBeatObjMeg(
						MessageNoConstants.FEP_STATUSTYPE, "server", fepEntity);
				fepEntity = manager.merge(fepEntity);
				logger.info("ǰ�û��˳���**" + code);
				
				if(Tools.isFepOnline){
					Tools.isFepOnline = false;
					fepWarningLocal.sendFepMessageToClient(fepEntity,Tools.SHUTDOWN_NORMAL);
				}
			}
			datacache.removeFEP(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startPingTimer() {
		try {
			List<FaultDetection> pingResults = nmsServiceLocal
					.queryPingResult();
			if (pingResults != null && pingResults.size() > 0) {
				if (pingResults.get(0).isStarted()) {
					pingTimerThreadLocal.startPingTimer(pingResults.get(0)
							.getPinglv(), pingResults.get(0).isStarted());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkClientLoginCount(Integer count) {
		Map<String, UserEntity> userMap = CacheDatas.getInstance().getUserMap();
		boolean flag = false;
		if (userMap.size() < count) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public boolean checkClientExist(String userName) {
		Map<String, UserEntity> userMap = CacheDatas.getInstance().getUserMap();
		boolean flag = false;
		if (userMap.get(userName) == null) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public CheckUserBean initDatas(CheckUserBean userBean,String userName) {
		String path = System.getProperty("user.dir");
		ObjectInputStream objectInput = null;
		if (path != null && !path.equals("")) {
//			File keyfile = new File(path + "/license.key");
			File licensefile = new File(path + "/adm2000.license");

			try {
				if (licensefile.exists()) {
//					byte[] readKeyByte = FileUtils.readFileToByteArray(keyfile);
					try{
						InetAddress ia = InetAddress.getLocalHost();
						byte[] macAddress = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

						byte[] readByte = FileUtils.readFileToByteArray(licensefile);
						byte[] decryptedValue = CryptoUtils.decrypt(macAddress,
								readByte);
						ByteArrayInputStream byteInput = new ByteArrayInputStream(
								decryptedValue);
						objectInput = new ObjectInputStream(byteInput);
					}catch(Exception eee){
						userBean.setErrorCode(5);
						userBean.setErrorMessage("��Ȩ�ļ���Ч,���顣");
						this.logger.error("��Ȩ�ļ���Ч,���顣",eee);
					}
					
					License c = (License) (objectInput.readObject());
					userBean.setLicense(c);
					if(c.getSwitcherCount().isRestriction()){
						CacheDatas.setSWITCHCOUNT(c.getSwitcherCount().getValue());
					}else{
						CacheDatas.setSWITCHCOUNT(-1);
					}
					if(c.getLayer3SwitcherCount().isRestriction()){
						CacheDatas.setSWITCH3COUNT(c.getLayer3SwitcherCount().getValue());
					}else{
						
						CacheDatas.setSWITCH3COUNT(-1);
					}
					if(c.getOltCount().isRestriction()){
						CacheDatas.setOLTCOUNT(c.getOltCount().getValue());
						
					}else{
						CacheDatas.setOLTCOUNT(-1);
					}
					
					if(c.getOnuCount().isRestriction()){
						
						CacheDatas.setONUCOUNT(c.getOnuCount().getValue());
					}else{
						CacheDatas.setONUCOUNT(-1);
					}
					
					
					if(c.getPlcCount().isRestriction()){
						
						CacheDatas.setPLCCOUNT(c.getPlcCount().getValue());
					}else{
						
						CacheDatas.setPLCCOUNT(-1);
					}
					
					if(c.getGprsCount().isRestriction()){
						
						CacheDatas.setGPRSCOUNT(c.getGprsCount().getValue());
					}else{
						
						CacheDatas.setGPRSCOUNT(-1);
					}
					PropertyValue<Date> date = c.getExpirationDate();
					Date expirationDate = date.getValue();
					Date nowDate = new Date();
					
					if(c.getExpirationDate().isRestriction()){
					if(nowDate.after(expirationDate)){
						logger.warn("�汾����.");
						userBean.setErrorCode(6);
						userBean.setErrorMessage("��Ȩ�ļ��ѹ���["+DateFormatter.format(expirationDate)+"]��");
						logger.error("��Ȩ�ļ��ѹ��ڡ�");
						return userBean;
					}
					if (c.getClientCount().isRestriction()) {
							if (!checkClientLoginCount(c.getClientCount().getValue())) {
								userBean.setErrorCode(3);
								userBean.setErrorMessage("��������¼�û�����["+c.getClientCount().getValue()+"]��");
								logger.error("��������¼�û�����["+c.getClientCount().getValue()+"]��");
								return userBean;
							}
					}
					if(c.getVersion()==License.DEVELOPMENT_VERSION){
					}else{
						if(!checkClientExist(userName)){
							userBean.setErrorCode(4);
							userBean.setErrorMessage("�û�["+userName+"]�Ѿ���¼��");
							logger.error("�û�["+userName+"]�Ѿ���¼��");
							return userBean;
					}
					}
					}
//					System.out.println("��ͬ��:" + c.getContractNumber());
//					System.out.println("�ͻ��˵�����:"+ c.getClientCount().getValue());
//					System.out.println("�����л�:"+ c.getClientCount().isRestriction());
//					System.out.println("��Ч����:"+ c.getExpirationDate().getValue());
				}else{
					userBean.setErrorCode(5);
					userBean.setErrorMessage("û����Ȩ��������Ȩ�ļ���");
					logger.warn("adm2000.license�ļ������ڡ�û����Ȩ�ļ���������Ȩ�ļ���");
					return userBean;	
				}
			} catch (Exception e) {
				this.logger.error("û����Ȩ��������Ȩ�ļ�",e);
			} finally {
				IOUtils.closeQuietly(objectInput);
			}

		}
		return userBean;
	}
}
