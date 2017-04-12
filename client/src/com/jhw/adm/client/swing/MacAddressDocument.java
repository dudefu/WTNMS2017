package com.jhw.adm.client.swing;

import java.awt.Toolkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MacAddressDocument extends PlainDocument{
	private static final long serialVersionUID = 1L;
	
	int maxLength = 2;
    int decLength = 0;
    
	public MacAddressDocument() {
    }
	
	public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {

		char charStr[] = s.toCharArray();
		String value = "";
    	for(int i = 0; i < charStr.length; i++){
    		value = String.valueOf(charStr[i]);
    		
    		String regex = "^([0-9a-fA-F])$";
    	    Pattern p=Pattern.compile(regex);
    	    Matcher m=p.matcher(value);
    	    if (!m.matches()){
    			return;
    	    }
    	    
    	    String str = getText(0, getLength());

            String strIntPart = "";
            String strDecPart = "";
            String strNew = str.substring(0, offset) + value + str.substring(offset, getLength());
            int decPos = strNew.indexOf(".");
            if(decPos > -1){
                strIntPart = strNew.substring(0,decPos);
                strDecPart = strNew.substring(decPos + 1);
            }else{
                strIntPart = strNew;
            }
//            if(strIntPart.length() > (maxLength - decLength)
//               || strDecPart.length() > decLength
//               || (strNew.length() > 1
//                   && strNew.substring(0, 1).equals("0")
//                   && !strNew.substring(1, 2).equals("-"))){
//                Toolkit.getDefaultToolkit().beep();
//                return;
//            }
            if(strIntPart.length() > (maxLength - decLength)
                    || strDecPart.length() > decLength){
                     Toolkit.getDefaultToolkit().beep();
                     return;
            }
    		
    	}
    	super.insertString(offset, s, a);
	}
	
//	public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {
//		String regex = "^([0-9a-fA-F])$";
//	    Pattern p=Pattern.compile(regex);
//	    Matcher m=p.matcher(s);
//	    if (!m.matches()){
//			return;
//	    }
//	    
//	    String str = getText(0, getLength());
//
//        String strIntPart = "";
//        String strDecPart = "";
//        String strNew = str.substring(0, offset) + s + str.substring(offset, getLength());
//        int decPos = strNew.indexOf(".");
//        if(decPos > -1){
//            strIntPart = strNew.substring(0,decPos);
//            strDecPart = strNew.substring(decPos + 1);
//        }else{
//            strIntPart = strNew;
//        }
//
//        if(strIntPart.length() > (maxLength - decLength)
//                || strDecPart.length() > decLength){
//                 Toolkit.getDefaultToolkit().beep();
//                 return;
//             }
//
//		super.insertString(offset, s, a);
//	}
}
