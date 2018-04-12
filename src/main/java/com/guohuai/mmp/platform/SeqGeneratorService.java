package com.guohuai.mmp.platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.guohuai.basic.common.SeqGenerator;

@Service
public class SeqGeneratorService {
	
	@Autowired
	private RedisTemplate<String, String> redis;
	
	public String getSeqNo(String prefix) {
		return SeqGenerator.getSeqNo(redis, prefix);
	}
	
}
