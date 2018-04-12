package com.guohuai.component.api.cms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

@lombok.Data
@Configuration
public class CmsSdk {

	@Value("${cms.host:localhost}")
	private String host;
	
	@Bean
	public CmsApi cmsBootSdk() {
		return Feign.builder().encoder(new GsonEncoder()).decoder(new GsonDecoder()).logger(new Logger.JavaLogger().appendToFile("cms.log")).logLevel(Logger.Level.FULL)
				.target(CmsApi.class, this.host);
	}
}
