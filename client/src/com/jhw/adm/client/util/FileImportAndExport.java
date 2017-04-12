package com.jhw.adm.client.util;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileImportAndExport {

	private static final Logger LOG = LoggerFactory.getLogger(FileImportAndExport.class);
	
	public static final int FAILURE_RESULT = 0;
	public static final int SUCCESS_RESULT = 1;
	public static final int CANCLE_RESULT = 2;
	
	/**
	 * 导出
	 * @param dataList
	 * @param propertiesName
	 * @return result 
	 */
	public static int export(Component component ,List<HashMap> dataList,String[] propertiesName){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new CSVFileFilter());
		int result = fileChooser.showSaveDialog(component);

		if (result == JFileChooser.APPROVE_OPTION){
			File csvFile = fileChooser.getSelectedFile();
			
			int results = -1;
			if (csvFile.exists()){
				results = JOptionPane.showConfirmDialog(null, "文件" + csvFile
						+ "要被覆盖，你确定吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
				if (JOptionPane.YES_OPTION == results){
					try{
						FileOutputStream os = new FileOutputStream(csvFile);
						// 导出数据到csv文件
						return writeCsvBo(dataList, propertiesName, os);
						
					} catch (Exception ex){
						LOG.error("Writing data occur error", ex);
						return FAILURE_RESULT;
					}
				}
			}else {
				if (!(csvFile.toString()).endsWith(".csv")){
					csvFile = new File(csvFile + ".csv");
				}else {
					try {
						csvFile.createNewFile();
					} catch (IOException e) {
						LOG.error("Creating csv File occur error", e);
						return FAILURE_RESULT;
					}
				}
				
				try{
					FileOutputStream os = new FileOutputStream(csvFile);
					// 导出数据到csv文件
					return writeCsvBo(dataList, propertiesName, os);
					
				} catch (Exception ex){
					LOG.error("Writing data occur error", ex);
					return FAILURE_RESULT;
				}
			}
		}
		return CANCLE_RESULT;
	}
	
	private static int writeCsvBo(List list, String propertiesName[],FileOutputStream fos) {
		StringBuffer sb = new StringBuffer();
		OutputStreamWriter osw = null;
		BufferedWriter out = null;
		osw = new OutputStreamWriter(fos); 
		out = new BufferedWriter(osw);
		
		for (int i = 0; i < propertiesName.length; i++) {
			if (i == propertiesName.length - 1) {
				sb.append(propertiesName[i]);
			} else {
				sb.append(propertiesName[i] + ",");
			}
		}

		try {
			out.write(sb.toString());
			out.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			return FAILURE_RESULT;
		}

		HashMap map = null;
		for (int j = 0; j < list.size(); j++) {
			sb = new StringBuffer();
			map = (HashMap) list.get(j);
			for (int m = 0; m < propertiesName.length; m++) {
				String value = "";
				value = (String) map.get(propertiesName[m]);
				if (m == propertiesName.length - 1) {
					sb.append("\"" + value + "\"");
				} else {
					sb.append("\"" + value + "\",");
				}
			}
			try {
				out.write(sb.toString());
				out.newLine();
			} catch (IOException e) {
				e.printStackTrace();
				return FAILURE_RESULT;
			}
		}

		try {
			out.close();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return FAILURE_RESULT;
		}
		
		return SUCCESS_RESULT;
	}
	
	/**
	 * 导入文件
	 */
	public static List imports(Component component){
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showSaveDialog(component);

		if (result != 0){
			return null;
		}
		File csvFile = fileChooser.getSelectedFile();
		if (!csvFile.exists()){
			return null;
		}
		
		List list = null;
		try {
			CsvUtil cu = new CsvUtil(csvFile.getPath());
			list = cu.getList();
			cu.close();
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}

class CSVFileFilter extends FileFilter{
	 @Override 
    public boolean accept(File f) {
		 if (f.getName().toLowerCase().endsWith("csv") || f.isDirectory()) 
			 return true; 
		 return false; 
    } 
    @Override 
    public String getDescription() { 
   	 // TODO Auto-generated method stub 
   	 return "(*.csv)"; 
    } 
}
