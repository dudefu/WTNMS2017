package com.jhw.adm.client.core;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.resources.ResourceManager;

@Component(LocalizationManager.ID)
public class LocalizationManager {

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initialize() {
		
		URL url = resourceManager.getURL(getFileName(getLocale()));
		if (url != null) {
			try {
				SAXReader reader = new SAXReader();
				Document document = null;
				document = reader.read(url);
				textMap = new HashMap<String, String>();
				Element menus = document.getRootElement();
				List<Element> elements = menus.elements();
				for (Element e : elements) {
					textMap.put(e.attributeValue("key"), e.getText());
				}
				LOG.info(String.format("Loading resource from file [%s]", url.getFile()));
			} catch (DocumentException e) {
				LOG.error("load resource error", e);
			}
		}
	}

	protected String getFileName(Locale locale) {
		return "Resource_zh_CN.xml";
		// 目前不做国际化
		//return String.format("Resource_%s.xml", locale);
	}

	public String getString(String key) {
		if (textMap == null) {
			return key;
		} else {
			LOG.debug("The key({}) not found.", key);
			return textMap.get(key) == null ? key : textMap.get(key);
		}
	}

	public String getString(String key, Object... args) {
		return String.format(getString(key), args);
	}
	
	public boolean contains(String key) {
		return textMap.containsKey(key);
	}
	
	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale == null ? Locale.getDefault() : locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Resource(name = ResourceManager.ID)
	private ResourceManager resourceManager;
	
	private Map<String, String> textMap;
	private Locale locale;
	private static final Logger LOG = LoggerFactory.getLogger(LocalizationManager.class);
	public static final String ID = "localizationManager";
}