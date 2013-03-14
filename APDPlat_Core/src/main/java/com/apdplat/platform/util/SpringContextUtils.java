package com.apdplat.platform.util;

import com.apdplat.platform.log.APDPlatLogger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringContextUtils implements ApplicationContextAware {
        protected static final APDPlatLogger log = new APDPlatLogger(SpringContextUtils.class);

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
