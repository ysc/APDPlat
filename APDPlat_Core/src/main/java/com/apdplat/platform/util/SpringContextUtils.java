package com.apdplat.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringContextUtils implements ApplicationContextAware {
        private static final Logger log = LoggerFactory.getLogger(SpringContextUtils.class);

	private static ApplicationContext applicationContext;

        @Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		if (applicationContext == null){
                    return null;
                }
		return (T)applicationContext.getBean(name); 
	}
}
