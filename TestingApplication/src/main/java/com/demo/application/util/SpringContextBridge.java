package com.demo.application.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.demo.application.dao.GeoTabDao;

@Component
public class SpringContextBridge implements SpringContextBridgeService, ApplicationContextAware {
	
	@Autowired
	private GeoTabDao geoTabDao;

	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		 SpringContextBridge.applicationContext = applicationContext;
	}

	public static SpringContextBridgeService services() {
        return applicationContext.getBean(SpringContextBridgeService.class);
    }
	
	@Override
	public GeoTabDao getGeoTabDao() {
		return geoTabDao;
	}

}
