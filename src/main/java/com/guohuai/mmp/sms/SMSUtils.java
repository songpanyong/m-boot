package com.guohuai.mmp.sms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.sms.ContentSms;
import com.guohuai.basic.component.sms.SmsAware;
import com.guohuai.basic.component.sms.TemplateSms;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.NumberUtil;
import com.guohuai.component.util.StrRedisUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.sms.notify.SMSNotifyEntity;
import com.guohuai.mmp.sms.notify.SMSNotifyService;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class SMSUtils {
	
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private SMSNotifyService sMSNotifyService;
	
	/**短信验证码过期时间*/
	public static final int EXPIRE_SECONDS = 120;
	
	/** 短信开关--开 */
	public static final String SMS_SWITCH_ON = "on";
	/** 短信开发--关 */
	public static final String SMS_SWITCH_OFF = "off";
	
	/** 短信发送类型--内容 */
	public static final String SMS_SendType_Content = "content";
	/** 短信发送类型--模板 */
	public static final String SMS_SendType_Temp = "temp";
	
	
	// 短信开关：on发短信/off不发短信
	@Value("${sms.switch:on}")
	private String smsSwitch;
	
	// 短信发送类型：content内容/temp模板
	@Value("${sms.sendTypes:content}")
	private String smsSendTypes;
	
	@Value("${sms.yimei.contentTypes:#{null}}")
	private String contentTypes;
	
	@Value("${sms.ronglian.tempTypes:#{null}}")
	private String tempTypes;
	
	public static final Map<String, String > smsContentsMap = new HashMap<String, String>();
	
	public static final Map<String, String> smsTempsMap = new HashMap<String, String>();

	@PostConstruct
	public void initSMS() {
		
		if (!StringUtil.isEmpty(this.contentTypes)) {
			List<SMSTypeEntity> list = JSON.parseArray(this.contentTypes, SMSTypeEntity.class);
			if (smsContentsMap.size() == 0) {
				for (SMSTypeEntity en : list) {
					smsContentsMap.put(en.getSmsType(), en.getContent());
				}
			}
		}
		if (!StringUtil.isEmpty(tempTypes)) {
			
			List<SMSTempIDEntity> list = JSON.parseArray(this.tempTypes, SMSTempIDEntity.class);
			if (smsTempsMap.size() == 0) {
				for (SMSTempIDEntity en : list) {
					smsTempsMap.put(en.getSmsType(), en.getTempId());
				}
			}
		} 
		
	}
	
	/**
	 * 根据发送类型发送短信
	 * @param phone 手机号
	 * @param smsType 短信类型
	 * @param values 短信模板值
	 */
	@Transactional
	public void sendSMSBySendTypes(String phone, String smsType, String[] values) {
		
		this.generateVeriCode(phone, smsType, values);
		
//		BaseResp resp = new BaseResp();
		// 发送短信内容
		String notifyContent = "";
		
		
		if (SMSUtils.SMS_SendType_Content.equals(this.smsSendTypes)) {
			String content = this.replaceComStrArr(SMSUtils.smsContentsMap.get(smsType), values);
			
			if (!StringUtil.isEmpty(content)) {
				ContentSms contentSms = this.setContentSms(phone, content);
				notifyContent = JSON.toJSONString(contentSms);
//				resp = SmsAware.send(contentSms);
			} else {
				// error.define[120006]=无效的短信内容！(CODE:120006)
				throw AMPException.getException(120006);
			}
		} else if (SMSUtils.SMS_SendType_Temp.equals(this.smsSendTypes)) {
			String tempId = SMSUtils.smsTempsMap.get(smsType);
			
			if (!StringUtil.isEmpty(tempId)) {
				TemplateSms templateSms = this.setTemplateSms(phone, tempId, values);
				notifyContent = JSON.toJSONString(templateSms);
//				resp = SmsAware.send(templateSms);
			} else {
				// error.define[120006]=无效的短信内容！(CODE:120006)
				throw AMPException.getException(120006);
			}
		}
		// 短信记录日志
		this.sMSNotifyService.createLog(this.smsSendTypes, notifyContent);
		

		
		
		
//		if (0 != resp.getErrorCode()) {
//			throw AMPException.getException(resp.getErrorMessage());
//		}
	}
	
	/**
	 * 短信发送
	 * @param notify
	 */
	public void reSendSMS(SMSNotifyEntity notify) {
		BaseResp resp = new BaseResp();
		try {
			if (SMSUtils.SMS_SendType_Content.equals(notify.getSmsSendTypes())) {
				ContentSms contentSms = JSON.parseObject(notify.getNotifyContent(), ContentSms.class);
				
				resp = SmsAware.send(contentSms);
			}
			
			if (SMSUtils.SMS_SendType_Temp.equals(notify.getSmsSendTypes())) {
				TemplateSms templateSms = JSON.parseObject(notify.getNotifyContent(), TemplateSms.class);
				
				resp = SmsAware.send(templateSms);

			}
		} catch (Exception e) {
			log.error("短信发送失败。原因：{}", AMPException.getStacktrace(e));;
			resp.setErrorCode(-1);
			resp.setErrorMessage(AMPException.getStacktrace(e));
		}
		this.sMSNotifyService.updateConfirm(notify, resp);
	}
	
	public ContentSms setContentSms(String phone, String content) {
		ContentSms contentSms = new ContentSms();
		contentSms.setPhone(phone);
		contentSms.setContent(content);
		return contentSms;
	}
	
	public TemplateSms setTemplateSms(String phone, String tempId, String[] values) {
		TemplateSms templateSms = new TemplateSms();
		templateSms.setPhone(phone);
		templateSms.setTempId(tempId);
		templateSms.setValues(values);
		return templateSms;
	}
	
	/**
	 * 替换掉短信模板中{1}格式的字样
	 * @param target 目标
	 * @param strArr 替换的值
	 * @return
	 */
	public String replaceComStrArr(String target, String[] repArr){
		if (null == target) {
			return StringUtil.EMPTY;
		}
		for (int i = 1; i <= repArr.length; i++) {
			target = target.replace("{" + i +"}" , repArr[i - 1]);
		}
		return target;
	}
	
	/**
	 * 生成短信验证码
	 * @param smsType
	 * @param values
	 */
	public String[] generateVeriCode(String phone, String smsType, String[] values) {
		if (SMSTypeEnum.smstypeEnum.regist.toString().equals(smsType) || 				
				SMSTypeEnum.smstypeEnum.login.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.forgetlogin.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.forgetpaypwd.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.normal.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.bindcard.toString().equals(smsType)) {
			// 验证码
			values[0] = NumberUtil.randomNumb();
			
			this.sendVC2Redis(phone, smsType, values[0]);
		}
		return values;
	}
	
	/**
	 * 将验证码保存到redis
	 * @param phone 手机号
	 * @param smsType 短信类型
	 * @param veriCode 验证码
	 */
	public void sendVC2Redis(String phone, String smsType, String veriCode) {
		boolean result = StrRedisUtil.setSMSEx(redis, StrRedisUtil.VERI_CODE_REDIS_KEY + phone + "_" + smsType, SMSUtils.EXPIRE_SECONDS, veriCode);
		log.info("手机号：{}短信类型为：{}的验证码是：{}", phone, smsType, veriCode);
		if (!result) {
			// error.define[120000]=生成验证码失败！(CODE:120000)
			throw AMPException.getException(120000);
		} 
	}
	
	/**
	 * 获取短信验证码
	 * @param req
	 * @return
	 */
	public SMSVeriCodeRep getVeriCode(SMSReq req) {
		SMSVeriCodeRep rep = new SMSVeriCodeRep();
		String veriCode = StrRedisUtil.get(redis, StrRedisUtil.VERI_CODE_REDIS_KEY + req.getPhone() + "_" + req.getSmsType());
		if (!StringUtil.isEmpty(veriCode)) {
			rep.setVeriCode(veriCode);
		} else {
			throw new AMPException("手机号对应的短信类型不存在验证码！");
		}
		return rep;
	}
	
	/**
	 * 校验验证码
	 * @param phoneNumb 手机号
	 * @param smsType 短信类型
	 * @param veriCode 短信码
	 * @return
	 */
	public void checkVeriCode(String phone, String smsType, String veriCode) {
		if (SMSUtils.SMS_SWITCH_OFF.equals(smsSwitch)) {
			log.info("发送短信开关为：off，不进行发送短信，短信类型：{}!", smsType);
			return;
		}
		this.checkPhone(smsType, phone);
		String vericode = StrRedisUtil.get(redis, StrRedisUtil.VERI_CODE_REDIS_KEY + phone + "_" + smsType);
		log.info("smsType{}vericode{}veriCode{}",smsType,vericode,veriCode);
		boolean result = veriCode.equals(vericode);
		if (!result) {
			// error.define[120001]=无效的验证码！(CODE:120001)
			throw AMPException.getException(120001);
		}
	}
	
	/**
	 * 根据短信通道发送短信
	 * @param phone
	 * @param smsType
	 * @param values 短信模板值
	 * @return
	 */
	public BaseResp sendSMS(String phone, String smsType, String[] values) {
		
		
		BaseResp resp = new BaseResp();
		
		if (SMSUtils.SMS_SWITCH_OFF.equals(smsSwitch)) {
			
			log.info("发送短信开关为：off，不进行发送短信，短信类型：{}!", smsType);
			return resp;
		}
		
		log.info("手机号：{}，请求的短信类型：{}，请求参数：{}", phone, smsType, values);

		this.checkPhone(smsType, phone);
				
		try {
			
			this.sendSMSBySendTypes(phone, smsType, values);
		} catch (AMPException e) {
			log.error("用户：{}，短信发送失败。原因：{}", phone, AMPException.getStacktrace(e));;
			throw AMPException.getException(e.getMessage());
		} catch (GHException e) {
			log.error("用户：{}，短信发送失败。原因：{}", phone, AMPException.getStacktrace(e));;
			throw AMPException.getException(e.getMessage());
		} catch (Exception e) {
			log.error("用户：{}，短信发送失败。原因：{}", phone, AMPException.getStacktrace(e));;
			// error.define[120003]=发送短信失败！(CODE:120003)
			throw AMPException.getException(120003);
		}
		
		return resp;
	}
	
	/**
	 * 校验手机号
	 * @param smsType
	 * @param phone
	 */
	public void checkPhone(String smsType, String phone) {
		// 是否是外部接口访问
		boolean isout = SMSTypeEnum.checkSMSType(smsType);
		InvestorBaseAccountEntity account = null;
		if (isout) {
			account = this.investorBaseAccountService.checkAccount(phone);
		} 
		
		if (SMSTypeEnum.smstypeEnum.regist.toString().equals(smsType)) {
			if (null != account) {
				// error.define[80020]=该手机号已注册(CODE:80020)
				throw AMPException.getException(80020);
			}
		}
		
		if (SMSTypeEnum.smstypeEnum.login.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.forgetlogin.toString().equals(smsType) || 
				SMSTypeEnum.smstypeEnum.forgetpaypwd.toString().equals(smsType) ||
				SMSTypeEnum.smstypeEnum.normal.toString().equals(smsType)) {
			if (null == account) {
				// error.define[80021]=用户账号不存在(CODE:80021)
				throw AMPException.getException(80021);
			}
		}
	}
	/**限制发送验证码类短信的次数*/
	@Transactional
	public SendSMSRep newSendSms(String phone, String smsType, String[] values) {
		SendSMSRep resp = new SendSMSRep();

			//先判断发送的短信的类型
			if(smsType.equals("forgetlogin")||smsType.equals("forgetpaypwd")||
					smsType.equals("regist")||smsType.equals("bindcard")){
				//从redis中获取短信的发送次数
				InvestorSMSMessageRedisInfo infos = this.saveAccountSMSSendEntity(phone);
				
				//判断最后一次发送的时间是否是当天，并且发送的次数是否大于4次
				InvestorSMSMessageRedisInfo info = this.checkSendTimes(infos,phone);

				//发送短信
				 this.sendSMS(phone,smsType,values);
				
				int times = info.getSMsSendTimes();
				info.setLastestSendTime(DateUtil.getSqlDate());
				info.setSMsSendTimes(times+1);
				InvestorBaseAccountSMSRedisUtil.set(redis, phone, info);			
				if(info.getSMsSendTimes()>3){
					resp.setMessage("今日您获取短信验证码的次数还剩余"+(5-info.getSMsSendTimes())+"次");
				}
				
			
			}else{
				 this.sendSMS(phone,smsType,values);
			}
			
			return resp;		
	}

	

	private InvestorSMSMessageRedisInfo saveAccountSMSSendEntity(String phone) {
		InvestorSMSMessageRedisInfo info = InvestorBaseAccountSMSRedisUtil.get(redis,phone);
		if(info==null){
			info = new InvestorSMSMessageRedisInfo();
			info.setSMsSendTimes(0);
		}
		 info = InvestorBaseAccountSMSRedisUtil.set(redis, phone, info);
		 return info;
		
		
	}
	public InvestorSMSMessageRedisInfo checkSendTimes(InvestorSMSMessageRedisInfo info,String phone) {
		if(info.getLastestSendTime()!=null&&(!info.getLastestSendTime().equals(""))){
			if(!DateUtil.same(DateUtil.getSqlDate(), info.getLastestSendTime())){
				info.setLastestSendTime(null);
				info.setSMsSendTimes(0);
				 info = InvestorBaseAccountSMSRedisUtil.set(redis, phone, info);
			}
		}
		if(info.getSMsSendTimes()>4){
			throw new AMPException("今日您获取短信验证码的次数已达上限，请明天再试");
		}
		return info;
	}
		
}
