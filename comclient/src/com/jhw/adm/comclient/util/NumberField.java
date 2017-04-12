package com.jhw.adm.comclient.util;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: NumberField ���ָ�ʽ�����</p>
 * <p>Copyright: Copyright (c)</p>
 * <p>Company: </p>
 * @author 
 * @version 1.0
 */
public class NumberField extends JTextField implements ActionListener, FocusListener, Serializable {
    public NumberField() {
        this(true);
    }

    public NumberField(boolean addAction) {
        this(16, 0, addAction);
    }

    public NumberField(int intPartLen) {
        this(intPartLen, true);
    }

    public NumberField(int intPartLen, boolean addAction) {
        this(intPartLen, 0, addAction);
    }

    public NumberField(int maxLen, int decLen) {
        this(maxLen, decLen, true);
    }

    public NumberField(int maxLen, int decLen, boolean addAction) {
    	this.setColumns(20);
//        setPreferredSize(new Dimension(150, 20));
        setDocument(new NumberDocument(maxLen, decLen));
        super.setHorizontalAlignment(JTextField.LEADING);
        if (addAction) addActionListener(this);
        addFocusListener(this);
    }

    public NumberField(int maxLen,
                        int decLen,
                        double minRange,
                        double maxRange,
                        boolean addAction) {
        setPreferredSize(new Dimension(150, 20));
        setDocument(new NumberDocument(maxLen, decLen, minRange, maxRange));
        super.setHorizontalAlignment(JTextField.LEADING);
        if (addAction) addActionListener(this);
        addFocusListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        transferFocus();
    }

    public void focusGained(FocusEvent e) {
        selectAll();
    }

    public void focusLost(FocusEvent e) {
    }
}

//class NumberDocument extends PlainDocument {
//    int maxLength = 16;
//    int decLength = 0;
//    double minRange = -Double.MAX_VALUE;
//    double maxRange = Double.MAX_VALUE;
//    public NumberDocument(int maxLen, int decLen) {
//        maxLength = maxLen;
//        decLength = decLen;
//    }
//
//    /**
//     * @param decLen int  С��λ����
//     * @param maxLen int  ��󳤶�(��С��λ)
//     * @param minRange double  ��Сֵ
//     * @param maxRange double  ���ֵ
//     */
//    public NumberDocument(int maxLen,
//                          int decLen,
//                          double minRange,
//                          double maxRange){
//        this(maxLen, decLen);
//        this.minRange = minRange;
//        this.maxRange = maxRange;
//    }
//
//    public NumberDocument(int decLen) {
//        decLength = decLen;
//    }
//
//    public NumberDocument() {}
//
//    public void insertString(int offset, String s, AttributeSet a) throws
//        BadLocationException {
//        String str = getText(0, getLength());
//
//        if (
//            //����Ϊf,F,d,D
//            s.equals("F") || s.equals("f") || s.equals("D") || s.equals("d")
//            //��һλ��0ʱ,�ڶ�λֻ��ΪС����
//            || (str.trim().equals("0") && !s.substring(0, 1).equals(".") && offset != 0)
//            //����ģʽ��������С����
//            || (s.equals(".") && decLength == 0)
//        ) {
//            Toolkit.getDefaultToolkit().beep();
//            return;
//        }
//        String strIntPart = "";
//        String strDecPart = "";
//        String strNew = str.substring(0, offset) + s + str.substring(offset, getLength());
////        strNew = strNew.replaceFirst("-",""); //���������븺��
//        int decPos = strNew.indexOf(".");
//        if(decPos > -1){
//            strIntPart = strNew.substring(0,decPos);
//            strDecPart = strNew.substring(decPos + 1);
//        }else{
//            strIntPart = strNew;
//        }
//        if(strIntPart.length() > (maxLength - decLength)
//           || strDecPart.length() > decLength
//           || (strNew.length() > 1
//               && strNew.substring(0, 1).equals("0")
//               && !strNew.substring(1, 2).equals("."))){
//            Toolkit.getDefaultToolkit().beep();
//            return;
//        }
//
//        try {
////            if ( !strNew.equals("") && !strNew.equals("-")  ) {//���������븺��
//        	if ( !strNew.equals("")) {
//                double d = Double.parseDouble(strNew);
//                if (d < minRange || d > maxRange){
//                    throw new Exception();
//                }
//            }
//        }
//        catch (Exception e) {
//            Toolkit.getDefaultToolkit().beep();
//            return;
//        }
//        super.insertString(offset, s, a);
//    }
//}

