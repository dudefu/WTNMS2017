package com.jhw.adm.client.util;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*******************************************************************************
 * 
 * @file: CsvUtil.java
 * @date: 
 * @brief:
 * @version: 1.0.0
 ******************************************************************************/

public class CsvUtil{
	private String filename = null;

	private BufferedReader bufferedreader = null;

	private List list = new ArrayList();

	public CsvUtil() {

	}

	public CsvUtil(String filename) throws IOException {
		this.filename = filename;
		bufferedreader = new BufferedReader(new FileReader(filename));
		String stemp;
		while ((stemp = bufferedreader.readLine()) != null){
			list.add(stemp);
		}
	}

	public List getList() throws IOException{
		return list;
	}

	public int getRowNum(){
		return list.size();
	}

	public int getColNum(){
		if (!list.toString().equals("[]")){
			if (list.get(0).toString().contains(",")){
				return list.get(0).toString().split(",").length;
			} 
			else if (list.get(0).toString().trim().length() != 0){
				return 1;
			} 
			else{
				return 0;
			}
		} 
		else{
			return 0;
		}
	}

	public String getRow(int index){
		if (this.list.size() != 0)
			return (String) list.get(index).toString().trim();
		else
			return null;
	}

	public String getCol(int index){
		if (this.getColNum() == 0){
			return null;
		}
		StringBuffer scol = new StringBuffer();
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1){
			for (Iterator it = list.iterator(); it.hasNext();){
				temp = it.next().toString().trim();

				scol = scol.append(temp.split(",")[index] + ",");
			}
		} 
		else{
			for (Iterator it = list.iterator(); it.hasNext();){
				temp = it.next().toString().trim();
				scol = scol.append(temp + ",");
			}
		}
		String str = new String(scol.toString());
		str = str.substring(0, str.length() - 1);
		return str;
	}

	public String getString(int row, int col){
		String temp = null;
		int colnum = this.getColNum();
		if (colnum > 1){
			temp = list.get(row).toString().split(",")[col].trim();
		} 
		else if (colnum == 1){
			temp = list.get(row).toString().trim();
		} 
		else{
			temp = null;
		}
		return temp;
	}

	public void close() throws IOException{
		this.bufferedreader.close();
	}

	public void test() throws IOException
	{
		CsvUtil cu = new CsvUtil("g:\\aaa.csv");
		List tt = cu.getList();
		for (Iterator itt = tt.iterator(); itt.hasNext();)
		{
			System.err.println("out:" + itt.next().toString());
		}
		System.err.println("out:" + cu.getRowNum());
		System.err.println("out:" + cu.getColNum());
		System.err.println("out:" + cu.getRow(0));
		System.err.println("out:" + cu.getCol(0));
		System.err.println("out:" + cu.getString(0, 0));
		cu.close();

	}

	public static void main(String[] args) throws IOException
	{
		CsvUtil test = new CsvUtil();
		test.test();
	}
}
