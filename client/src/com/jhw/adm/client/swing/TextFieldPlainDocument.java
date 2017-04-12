package com.jhw.adm.client.swing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextFieldPlainDocument extends PlainDocument{
	private JTextField textField = null;
	private int length = 0;
	private final static int SIZE = 36; 
	private boolean isChinese = false;
	
	public TextFieldPlainDocument(JTextField textField){
		super();
		this.textField = textField;
		this.length = SIZE;
	}
	
	public TextFieldPlainDocument(JTextField textField,int length){
		super();
		this.textField = textField;
		this.length = length;
	}
	
	public TextFieldPlainDocument(JTextField textField,boolean isChinese){
		super();
		this.textField = textField;
		this.isChinese = isChinese;
		this.length = SIZE;
	}
	
	public TextFieldPlainDocument(JTextField textField,int length,boolean isChinese){
		super();
		this.textField = textField;
		this.length = length;
		this.isChinese = isChinese;
		this.length = length;
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (textField.getText().trim().length() >= this.length){
			return;
		}
		
		if (!this.isChinese){
			String regex = "^[^\u4e00-\u9fff]+$";
			Pattern p=Pattern.compile(regex);
			Matcher m=p.matcher(str);
			if (!m.matches()){
				return;
			}
		}
		
		super.insertString(offs, str, a);
	}
}
