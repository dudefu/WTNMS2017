package com.jhw.adm.client.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(ClientConfigRepository.ID)
public class ClientConfigRepository {

	public ClientConfig loadConfig() {
		File fin = new File(CONFIG_FILE);
		FileInputStream fis = null;
		XMLDecoder decoder = null;
		ClientConfig clientConfig = null;
		try {
			fis = new FileInputStream(fin);
			decoder = new XMLDecoder(fis);
			clientConfig = (ClientConfig) decoder.readObject();
		} catch (Exception ex) {
			LOG.error("load config error", ex);
		} finally {
			if (decoder != null) {
				decoder.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
					LOG.error("FileInputStream.close error", ex);
				}
			}
		}
		
		return clientConfig;
	}

	public void saveConfig(ClientConfig clientConfig) {
		File fo = new File(CONFIG_FILE);
		FileOutputStream fos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(fo);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(clientConfig);
			encoder.flush();
		} catch (IOException ex) {
			LOG.error("save config error", ex);
		} finally {
			if (encoder != null) {
				encoder.close();
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					LOG.error("FileOutputStream.close error", ex);
				}
			}
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ClientConfigRepository.class);
	public static final String ID = "clientConfigRepository";
	public static final String CONFIG_FILE = "conf/client.config.xml";
}