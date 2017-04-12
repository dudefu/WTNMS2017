package com.jhw.adm.server.servic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.jhw.adm.server.entity.emailLog.SendEmailLog;
import com.jhw.adm.server.entity.util.EmailConfigEntity;
import com.jhw.adm.server.entity.util.PersonBean;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.WarningCheckServiceLocal;

/**
 * 定时查询告警信息，然后给相应的user发送邮件提醒
 * @author Administrator
 *
 */
@Stateless(mappedName = "warningService")
public class WarningCheckService implements WarningCheckServiceLocal {

	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@EJB
	private NMSServiceLocal nmsService;
	@EJB
	private CommonServiceBeanLocal commonService;
	@Resource
	private SessionContext ctx;
	private static int timeoutTag = 0;
	@Override
	public void scheduleTimer(long milliseconds) {
		if (ctx != null) {
			if (timeoutTag == 0) {
				ctx.getTimerService().createTimer(
						new Date(new Date().getTime() + milliseconds),
						milliseconds, "***************定时检查告警信息发送Email");
				timeoutTag++;
			}
		}
	}
    int count =0;
	@Timeout
	public void ejbTimeout(Timer timer) {
		try {
//			List<String> listWarning = this.findTrap_New();
			List<String> listWarning = new ArrayList<String>();
			System.out.println("***************" + listWarning);
			System.out.println("***************" + listWarning);
			System.out.println("***************" + listWarning);
            boolean flagtag =true;
			if (listWarning!=null&&listWarning.size() > 0) {
				String fromAddress="";
				String password="";
				EmailConfigEntity emailConfigEntity =this.queryEmailConfigEntity();
				if(emailConfigEntity!=null){
					
					fromAddress =emailConfigEntity.getEmailServer();
					password =emailConfigEntity.getPassword();
				
				for (String o : listWarning) {
					String trapIp = (String) o;
					String userName ="";
					String contents = userName+" 您好！\n\r   Ip为：" + trapIp+ "的设备告警，请及时注意检查，以免带来影响!";
					String subject = "设备告警";
					List<PersonBean> list = nmsService.findPersonInfo(trapIp);
					
					if (list!=null&&list.size() > 0) {
						for (PersonBean personBean : list) {
							String email = personBean.getEmail();
							userName =personBean.getUserName();
							SendEmailLog emailLog = findSendEmailLog(trapIp,email);
							if (emailLog != null) {
								boolean flag = this.checkSendAgain(emailLog);
								if (!flag) {
									try{
//									sendMailService.SendMesg(contents, subject,email, fromAddress,password);
									}catch (Exception e) {
								    	flagtag =false;
								    	e.printStackTrace();
								    	System.out.println("***************不能发送邮件，请检查网络设置！");
									}
									if(flagtag){
									emailLog.setSendDate(new Date());
									emailLog.setNextDate(this.setNextSendDate());
									emailLog.setEmail(email);
									emailLog.setIp_value(trapIp);
									commonService.updateEntity(emailLog);
									System.out.println("***************"+ email + " 发送成功！");
									}
								} else {
									System.out.println("***************稍后发送！");
								}

							} else {
								    try{
//									sendMailService.SendMesg(contents, subject,email, "zuojunyong@jhw.com.cn","zjy123456");
								    }catch (Exception e) {
								    	flagtag =false;
								    	e.printStackTrace();
								    	System.out.println("***************不能发送邮件，请检查网络设置！");
										// TODO: handle exception
									}
								    if(flagtag){
									emailLog = new SendEmailLog();
									emailLog.setSendDate(new Date());
									emailLog.setNextDate(this.setNextSendDate());
									emailLog.setEmail(email);
									emailLog.setIp_value(trapIp);
									commonService.saveEntity(emailLog);
									System.out.println("***************发送成功！");
								    }
							}

						}

					}

				}
				}else{
					
					System.out.println("***************邮箱地址没配置！" );
				}
				      
				
			}else{
				++count;
				if(count>=5){
					timer.cancel();
				}
				System.out.println("***************无告警信息^_^" );
				System.out.println("***************无告警信息^_^" );
				System.out.println("***************无告警信息^_^" );
				System.out.println("***************无告警信息^_^" );
				System.out.println("***************无告警信息^_^" );
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***************运行失败,请检查服务器设置！");
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	public List<String> findTrap_New() {
		String sql = "SELECT DISTINCT c.IPVALUE FROM TRAPSIMPLEWARNING c Where c.currentStatus='0'";
		Query query = manager.createNativeQuery(sql);
		List<String> list = query.getResultList();
		return list;

	}
	public SendEmailLog findSendEmailLog(String ip_value, String email) {

		 String sql =
		 "select c from SendEmailLog c where c.ip_value=:ip and c.email=:email";
		 Query query = manager.createQuery(sql);
		 query.setParameter("ip", ip_value);
		 query.setParameter("email", email);
		 List<SendEmailLog> list = query.getResultList();
		 if(list!=null&&list.size()>0){
		 return list.get(0);
		 }else{
			 return null;
		 }
		
	}
	public Date setNextSendDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY), calendar
						.get(Calendar.MINUTE) + 30);
		return calendar.getTime();
	}
	public boolean checkSendAgain(SendEmailLog log) {
		boolean flag = true;
		if (new Date().after(log.getNextDate())) {
			flag = false;
		}
		return flag;
	}
    
	public EmailConfigEntity queryEmailConfigEntity(){
		
		String sql ="select c from EmailConfigEntity c";
		Query query =manager.createQuery(sql);
		EmailConfigEntity emailConfigEntity=null;
		if(query.getResultList()!=null&&query.getResultList().size()>0){
			
			emailConfigEntity=(EmailConfigEntity) query.getResultList().get(0);
		}
		return emailConfigEntity;
	}
}
