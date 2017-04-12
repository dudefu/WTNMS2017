package com.jhw.adm.server.servic;

import javax.ejb.Remote;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.util.CheckUserBean;

@Remote
public interface LoginServiceRemote {
	public FEPEntity loginFEP(String loginName, String password);
	public FEPEntity getFEPByCode(String code);
	public void logoffFep(String loginName);

	public CheckUserBean longinClient(String userName, String passworld);
	/**
	 * ClientÍË³ö
	 * @param userName
	 */
	public void loginOffClient(String userName);
}
