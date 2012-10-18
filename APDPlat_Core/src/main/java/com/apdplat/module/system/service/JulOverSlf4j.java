package com.apdplat.module.system.service;

import javax.annotation.PostConstruct;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.stereotype.Service;

/**
 * 在Spring ApplicationContext中初始化Slf4对Java.util.logging的拦截.
 *
 * @author 杨尚川
 */
@Service
public class JulOverSlf4j {

	//Spring在所有属性注入后自动执行的函数.
	@PostConstruct
	public void init() {
		SLF4JBridgeHandler.install();
	}
}