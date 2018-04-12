package com.guohuai.mmp.platform.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfig  {  
	
	@Bean(name = "createPaymentService")
	@Conditional(value = { PaymentCondition.class })
	public Payment createAccmentService() {
		return new PaymentServiceImpl();
	}
	
	@Bean(name = "createPaymentIsolationService")
	@Conditional(PaymentIsolationCondition.class)
	public Payment createAccmentIsolationService() {
		return new PaymentIsolationService();
	}
	
	
}  