package com.jhw.adm.client.resources;

import java.net.URL;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component(ResourceManager.ID)
public class ResourceManager implements ResourceLoaderAware {
	
	
	public URL getURL(String name) {
		return this.getClass().getResource(name);
//		Resource resource = resourceLoader.getResource("classpath:com/jhw/adm/client/resources/" + name);
//		URL url = null;
//		try {
//			System.out.println(resource.getDescription());
//			if (resource.getFile().exists()) {
//				System.out.println("exists true");
//			} else {
//				System.out.println("exists false");
//			}
//			url = resource.getURL();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return url;
	}
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	private ResourceLoader resourceLoader;
	public static final String ID = "resourceManager";
}