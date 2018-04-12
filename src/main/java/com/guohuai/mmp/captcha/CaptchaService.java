package com.guohuai.mmp.captcha;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.Producer;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.util.StrRedisUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CaptchaService {
	
	/** 图形验证码有效时间 */
	public static final long IMG_VERICODE_TIME = 120;
	
	/** 图形验证码生成方式 -- 字符 */
	public static final String CAPTCHA_WAY_char = "char";
	/** 图形验证码生成方式 -- 中文 */
	public static final String CAPTCHA_WAY_chinese = "chinese";
	/** 图形验证码生成方式 -- 计算 */
	public static final String CAPTCHA_WAY_calculate = "calculate";
	
	// 图形验证码生成方式 char:字符 chinese:中文 calculate:计算
	@Value("${captcha.ways:char}")
	private String captchaWays;
	
	@Autowired
	private RedisTemplate<String, String> redis;  
	@Autowired  
	private Producer captchaProducer;
    @Autowired
    private GHChineseTextProducer gHChineseTextProducer;
    @Autowired
    private CalculateProducer calculateProducer;
    
	public ModelAndView getImgVc(HttpServletRequest request,
            HttpServletResponse response,
            String sessionId) throws Exception {
		response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        
        String capKey = "";
        String capValue = "";
        
        if (CaptchaService.CAPTCHA_WAY_char.equals(this.captchaWays)) {
        	capValue = capKey = captchaProducer.createText();
        } else if (CaptchaService.CAPTCHA_WAY_chinese.equals(this.captchaWays)) {
        	capValue = capKey = gHChineseTextProducer.getText();
        } else if (CaptchaService.CAPTCHA_WAY_calculate.equals(this.captchaWays)) {
        	String[] captchas  = calculateProducer.getCalculate();
        	capKey = captchas[0];
        	capValue = captchas[1];
        }
        
        log.info("uid：{}生成的图形验证码：{}", sessionId, capKey);
        try {
            redis.opsForValue().set(StrRedisUtil.IMG_VERICODE_REDIS_KEY + sessionId, capKey, IMG_VERICODE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        BufferedImage bi = captchaProducer.createImage(capValue);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
	}
	
	/**
	 * 校验图形验证码
	 * @param req
	 * @return
	 */
	public BaseResp checkImgVc(CaptchaValidReq req) {
		this.validImgVc(req.getImgvc(), req.getSessionId());
		return new BaseResp();
	}
	
	/**
	 * 校验图形验证码
	 * @param imgVc
	 * @param sessionId
	 * @return
	 */
	public BaseResp checkImgVc(String imgVc, String sessionId) {
		this.validImgVc(imgVc, sessionId);	
		return new BaseResp();
	}
	
	/**
	 * 校验图形验证码
	 * @param imgVc
	 * @param sessionId
	 * @return
	 */
	public void validImgVc(String imgVc, String sessionId) {
		String redisimgvc =  redis.opsForValue().get(StrRedisUtil.IMG_VERICODE_REDIS_KEY + sessionId);
		if (!imgVc.equalsIgnoreCase(redisimgvc)) {
			// error.define[120004]=图形验证码不正确！(CODE:120004)
			throw GHException.getException(120004);
		}
	}

}
