package com.jhw.adm.client.core;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.swing.ImageIcon;

import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.resources.ResourceManager;

@Component(ImageRegistry.ID)
public class ImageRegistry {

	/**
	 * 注册图标
	 */
	public void regist(String imgName) {
		try {
			URL url = resourceManager.getURL(imgName);
			
			ImageIcon imageIcon = new ImageIcon(url);
			if (imageIcon != null) {
				imageMap.put(imgName, imageIcon);
				BufferedImage bufferedImage = Images.toBufferedImage(imageIcon.getImage());
				bufferedImageMap.put(imgName, bufferedImage);
			}
		} catch (Exception e) {
			LOG.error("加载图片错误", e);
		}
	}

	/**
	 * 获取指定的图标
	 */
	public ImageIcon getImageIcon(String imgName) {
		Object ob = imageMap.get(imgName);
		if (ob != null) {
			return (ImageIcon) ob;
		} else  {
			return null;
		}
	}

	public Image getImage(String imgName) {
		ImageIcon imageIcon = getImageIcon(imgName);
		return imageIcon == null ? null : imageIcon.getImage();
	}

	public BufferedImage getBufferedImage(String imgName) {
		if (bufferedImageMap.containsKey(imgName)) {
			return bufferedImageMap.get(imgName);
		} else {
			return null;
		}
	}

	/**
	 * 判断是否已经包含该图标
	 */
	public boolean hasImage(String imgName) {
		Object ob = imageMap.get(imgName);
		if (ob != null) {
			return true;
		}

		return false;
	}

	@Resource(name = ResourceManager.ID)
	private ResourceManager resourceManager;

	public static final String ID = "imageRegistry";
	private static final Logger LOG = LoggerFactory.getLogger(ImageRegistry.class);

	/**
	 * ImageIcon缓存
	 */
	private final Map<String, ImageIcon> imageMap = new HashMap<String, ImageIcon>();

	/**
	 * BufferedImage缓存
	 */
	private final Map<String, BufferedImage> bufferedImageMap = new HashMap<String, BufferedImage>();
}