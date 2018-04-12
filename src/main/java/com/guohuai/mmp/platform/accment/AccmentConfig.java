package com.guohuai.mmp.platform.accment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
public class AccmentConfig  {  
	
	@Bean(name = "createAccmentService")
	@Conditional(value = { AccmentCondition.class })
	public Accment createAccmentService() {
		return new AccmentService();
	}
	
	@Bean(name = "createAccmentIsolationService")
	@Conditional(AccmentIsolationCondition.class)
	public Accment createAccmentIsolationService() {
		return new AccmentIsolationService();
	}
	
	
}  