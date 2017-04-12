package com.jhw.adm.server.interfaces;

import javax.ejb.Local;

import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.CheckUserBean;

@Local
public interface LoginServiceLocal {
	public FEPEntity loginFEP(String loginName, String password);

	public CheckUserBean longinClient(String userName, String passworld);
}
