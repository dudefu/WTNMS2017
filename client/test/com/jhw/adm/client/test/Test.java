package com.jhw.adm.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
	//���ļ�xxWord.Zѹ����תΪbyte�ֽ�
	public static byte[] fileToByte() throws IOException{
		String filePath = "D:\\apache-maven-3.3.9\\conf\\logging\\xxWork.Z";
		File file = new File(filePath);
		InputStream is = new FileInputStream(file);
		int available = is.available();
		byte[] byteBuffer = new byte[available];
		byte[] object = byteBuffer;
		return object;
	}
	
	/**
	 * �Ѵ�����ֽ���ת��Ϊ���ص��ļ�
	 * 
	 * @param object
	 * @return
	 */
	private static File getFile(byte[] object) {
		String fileName = "D:\\Documents and Settings\\Workspaces\\MyEclipse 8.5\\tftp";
		String FILE_PATH = "\\xxWork.Z";
		String path = System.getProperty("user.dir"); //��ȡ��ǰ��Ŀ·��
		fileName = path + FILE_PATH;
		File file = new File(fileName);
		FileOutputStream fileOutStream = null;
		if (file.isFile()) {
			file.delete();
		}
		try {

			if (file.createNewFile() && file.canWrite()) {
				fileOutStream = new FileOutputStream(file); //��ȡ�����
				fileOutStream.write(object);  //д��
			}
		} catch (IOException e) {
			System.out.println("IOException occur");
			e.getMessage();
		} finally {
			try {
				if (null != fileOutStream) {
					fileOutStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}
	
	/**
	 * ɾ���ļ�
	 * 
	 * @param file
	 */
	private static void deleteFile(final File file) {
		new Thread(new Runnable() {
			public void run() {
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
		}).start();
	}
}
