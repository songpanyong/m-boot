package com.guohuai.mmp.captcha;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class CaptchaConfig {
     
	public static String textLength = "4";
	
	// 是否有边框  默认为true 可以设置yes，no
	@Value("${captcha.isBorder:no}")
	private String isBorder;
	
	// 边框颜色   默认为Color.BLACK
	@Value("${captcha.borderColor:blue}")
	private String borderColor;
		
	// 边框粗细度  默认为1 
	@Value("${captcha.borderThickness:1}")
	private String borderThickness;
	
	// 验证码文本字符内容范围  默认为abcde2345678gfynmnpwx
	@Value("${captcha.charString:abcde2345678gfynmnpwx}")
	private String charString;
	
	// 验证码文本字符长度  默认为5
	@Value("${captcha.charLength:4}")
	private String charLength;
	
	// 验证码文本字符间距  默认为2
	@Value("${captcha.charSpace:2}")
	private String charSpace;
	
	// 验证码文本字符颜色  默认为Color.BLACK
	@Value("${captcha.fontColor:blue}")
	private String fontColor;
	
	// 验证码文本字符大小  默认为40
	@Value("${captcha.fontSize:30}")
	private String fontSize;
	
	@Value("${captcha.width:125}")
	private String width;
	
	@Value("${captcha.height:45}")
	private String height;
	
	// WaterRipple:水纹 FishEyeGimpy:鱼眼 ShadowGimpy:阴影
	@Value("${captcha.obscurificator.impl:com.google.code.kaptcha.impl.WaterRipple}")
	private String obscurificator;
	
	// 验证码噪点颜色   默认为Color.BLACK
	@Value("${captcha.noiseColor:black}")
	private String noiseColor;
	
	// 验证码背景颜色渐进   默认为Color.LIGHT_GRAY
	@Value("${captcha.backgroundClearFrom:192,192,192}")
	private String backgroundClearFrom;
	
	// 验证码背景颜色渐进   默认为Color.WHITE
	@Value("${captcha.backgroundClearTo:WHITE}")
	private String backgroundClearTo;  
	
    @Bean(name="captchaProducer")
    public DefaultKaptcha getKaptchaBean() {
    	CaptchaConfig.textLength = this.charLength;
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", this.isBorder);
        properties.setProperty("kaptcha.border.color", this.borderColor);
        properties.setProperty("kaptcha.border.thickness", this.borderThickness);
        properties.setProperty("kaptcha.textproducer.char.string", this.charString);
        properties.setProperty("kaptcha.textproducer.char.length", this.charLength);
        properties.setProperty("kaptcha.textproducer.char.space", this.charSpace);
        properties.setProperty("kaptcha.textproducer.font.color", this.fontColor);
        properties.setProperty("kaptcha.textproducer.font.size", this.fontSize);
        properties.setProperty("kaptcha.image.width", this.width);
        properties.setProperty("kaptcha.image.height", this.height);
        properties.setProperty("kaptcha.obscurificator.impl", this.obscurificator);
        properties.setProperty("kaptcha.noise.color", this.noiseColor);
        properties.setProperty("kaptcha.background.clear.from", this.backgroundClearFrom);
        properties.setProperty("kaptcha.background.clear.to", this.backgroundClearTo);
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.font.names", "Courier"); 
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}