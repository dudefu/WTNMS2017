package com.jhw.adm.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
	//将文件xxWord.Z压缩包转为byte字节
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
	 * 把传入的字节流转化为本地的文件
	 * 
	 * @param object
	 * @return
	 */
	private static File getFile(byte[] object) {
		String fileName = "D:\\Documents and Settings\\Workspaces\\MyEclipse 8.5\\tftp";
		String FILE_PATH = "\\xxWork.Z";
		String path = System.getProperty("user.dir"); //获取当前项目路径
		fileName = path + FILE_PATH;
		File file = new File(fileName);
		FileOutputStream fileOutStream = null;
		if (file.isFile()) {
			file.delete();
		}
		try {

			if (file.createNewFile() && file.canWrite()) {
				fileOutStream = new FileOutputStream(file); //获取输出流
				fileOutStream.write(object);  //写入
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
	 * 删除文件
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
