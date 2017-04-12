package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

import com.jhw.adm.server.entity.system.UserEntity;

@Local
public interface SendMsgAndEmailLocal {
    /***
     * 发送邮件和短信通知
     * @param userEntity
     * @param ip
     * @return
     */
	public void sendMsgAndEmail(UserEntity userEntity,String ip,int  warningType,int sPort,int warningLevel,String content);
}
