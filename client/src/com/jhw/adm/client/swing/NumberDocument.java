package com.jhw.adm.client.swing;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberDocument extends PlainDocument {
	
	private static final long serialVersionUID = 1L;
	int maxLength = 16;
    int decLength = 0;
    double minRange = -Double.MAX_VALUE;
    double maxRange = Double.MAX_VALUE;
    public NumberDocument(int maxLen, int decLen) {
        maxLength = maxLen;
        decLength = decLen;
    }

    /**
     * @param decLen int  С��λ����
     * @param maxLen int  ��󳤶�(��С��λ)
     * @param minRange double  ��Сֵ
     * @param maxRange double  ���ֵ
     */
    public NumberDocument(int maxLen,
                          int decLen,
                          double minRange,
                          double maxRange){
        this(maxLen, decLen);
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public NumberDocument(int decLen) {
        decLength = decLen;
    }

    public NumberDocument() {
    	this(3, 0, 0, 255);
    }

    public void insertString(int offset, String s, AttributeSet a) throws
        BadLocationException {
        String str = getText(0, getLength());

        if (
            //����Ϊf,F,d,D
            s.equals("F") || s.equals("f") || s.equals("D") || s.equals("d")
            //��һλ��0ʱ,�ڶ�λֻ��ΪС����
            || (str.trim().equals("0") && !s.substring(0, 1).equals(".") && offset != 0)
            //����ģʽ��������С����
            || (s.equals(".") && decLength == 0)
        ) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        String strIntPart = "";
        String strDecPart = "";
        String strNew = str.substring(0, offset) + s + str.substring(offset, getLength());
        int decPos = strNew.indexOf(".");
        if(decPos > -1){
            strIntPart = strNew.substring(0,decPos);
            strDecPart = strNew.substring(decPos + 1);
        }else{
            strIntPart = strNew;
        }
        if(strIntPart.length() > (maxLength - decLength)
           || strDecPart.length() > decLength
           || (strNew.length() > 1
               && strNew.substring(0, 1).equals("0")
               && !strNew.substring(1, 2).equals("."))){
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        try {
        	if ( !strNew.equals("")) {
                double d = Double.parseDouble(strNew);
                if (d < minRange || d > maxRange){
                    throw new Exception();
                }
            }
        }
        catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        super.insertString(offset, s, a);
    }
}
