package com.jhw.adm.server.servic;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class PopupAuthenticator  extends Authenticator{
	String username;
	String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public PopupAuthenticator (String user,String pass){
		this.password=pass;
		this.username=user;
	}
	public PasswordAuthentication performCheck(String user,String pass){
		username = user;
		password = pass;
		return getPasswordAuthentication();
	}

	
	public PasswordAuthentication getPasswordAuthentication()  
    { 
//     String result = JOptionPane.showInputDialog( 
//     "Enter 'username,password'"); 
//     StringTokenizer st = new StringTokenizer(result, ","); 
//     username = st.nextToken(); 
//     password = st.nextToken(); 
	 
     return new PasswordAuthentication(username, password); 
     } 

}
