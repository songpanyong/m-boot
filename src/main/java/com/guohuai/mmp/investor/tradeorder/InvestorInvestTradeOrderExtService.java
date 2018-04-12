package com.guohuai.mmp.investor.tradeorder;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.guohuai.ams.product.Product;
import com.guohuai.ams.product.ProductDao;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.proactive.ProActive.Execution;
import com.guohuai.basic.component.proactive.ProActiveAware;
import com.guohuai.cache.service.CacheChannelService;
import com.guohuai.cache.service.CacheProductService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.ext.investor.InvestorAfterInvest;
import com.guohuai.file.legal.file.LegalFileDao;
import com.guohuai.file.legal.file.LegalFileEntity;
import com.guohuai.file.legal.file.LegalFileResp;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.orderlog.OrderLogEntity;
import com.guohuai.mmp.investor.orderlog.OrderLogService;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.investor.tradeorder.check.CheckOrderReq;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecord;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.ebaoquan.Html2PdfService;
import com.guohuai.mmp.jiajiacai.wishplan.plan.dao.PlanInvestDao;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanInvestEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.entity.PlanProductEntity;
import com.guohuai.mmp.jiajiacai.wishplan.plan.service.PlanBaseService;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.redis.RedisSyncService;
import com.guohuai.mmp.platform.tulip.TulipService;
import com.guohuai.mmp.serialtask.SerialTaskRequireNewService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvestorInvestTradeOrderExtService {

	@Autowired
	private InvestorInvestTradeOrderService investorInvestTradeOrderService;
	@Autowired
	private InvestorRedeemTradeOrderService investorRedeemTradeOrderService;
	@Autowired
	private InvestorTradeOrderService investorTradeOrderService;
	@Autowired
	private OrderLogService orderLogService;
	@Autowired
	private TulipService tulipService;
	@Autowired
	private SerialTaskRequireNewService serialTaskRequireNewService;
	@Autowired
	private CacheProductService cacheProductService;
	@Autowired
	private CacheChannelService cacheChannelService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private RedisSyncService redisSyncService;
	@Autowired
	private ProActiveAware proActiveAware;
	@Autowired
	private LegalFileDao legalFileDao;
	@Autowired
	private SonAccountService sonAccountService; 
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao; 
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	@Autowired
	private PlanBaseService planBaseService;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private PlanInvestDao planInvestDao;
	
	@Autowired
	private EbaoquanRecordDao recordDao;
	
	
	@Transactional
	public TradeOrderRep expGoldInvest(TradeOrderReq tradeOrderReq) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		/** 校验卡券信息 */
		tulipService.validateCouponForInvest(tradeOrderReq);
		
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = investorInvestTradeOrderService.createExpGoldInvestTradeOrder(tradeOrderReq);
		
		try {
			tradeOrderRep = investorInvestTradeOrderService.invest(orderEntity.getOrderCode(), tradeOrderReq);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(e.getMessage());

			
			this.tulipService.sendInvestFail(orderEntity);
			
		}
		
		investorInvestTradeOrderService.investThen(tradeOrderRep, orderEntity.getOrderCode());
		return tradeOrderRep;
	}
	
	
	@Transactional
	public TradeOrderRep normalInvest(TradeOrderReq tradeOrderReq) {
		
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		/** 校验产品交易时间 */
		cacheProductService.isInDealTime(tradeOrderReq.getProductOid());
		
		/** 校验卡券信息 */
		tulipService.validateCouponForInvest(tradeOrderReq);
		
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = investorInvestTradeOrderService.createNormalInvestTradeOrder(tradeOrderReq);
		
		/** 校验渠道 */
		this.cacheChannelService.checkChannel(orderEntity);
		
		try {
			
			tradeOrderRep = investorInvestTradeOrderService.invest(orderEntity.getOrderCode(), tradeOrderReq);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.info("购买异常 uid={}, poid={}, tradeOrder={}", tradeOrderReq.getUid(),tradeOrderReq.getProductOid(), tradeOrderReq);
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(e.getMessage());
			//发送申购事件
			this.tulipService.sendInvestFail(orderEntity);
		}
		
		investorInvestTradeOrderService.investThen(tradeOrderRep, orderEntity.getOrderCode());

		// 定义投资人下单后扩展业务调用
		if(this.proActiveAware.achieved(InvestorAfterInvest.class)){
			this.proActiveAware.invoke(new Execution<InvestorAfterInvest, Void>() {

				@Override
				public Void execute(InvestorAfterInvest arg0) {
					arg0.normalInvestExt(orderEntity);
					return null;
				}
			}, InvestorAfterInvest.class);
		}
			
		return tradeOrderRep;
	}

	@Transactional
	public TradeOrderRep writerOffOrder(TradeOrderReq tradeOrderReq) {
		
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		InvestorTradeOrderEntity orderEntity = investorInvestTradeOrderService.createWriteOffTradeOrder(tradeOrderReq);
		
		try {

			tradeOrderRep = investorInvestTradeOrderService.invest(orderEntity.getOrderCode(), tradeOrderReq);
		} catch (Exception e) {
			e.printStackTrace();
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(e.getMessage());
		
			this.tulipService.sendInvestFail(orderEntity);
		}
		
		investorInvestTradeOrderService.investThen(tradeOrderRep, orderEntity.getOrderCode());

		return tradeOrderRep;
	}
	
	@Transactional
	public TradeOrderRep cashFailOrder(RedeemTradeOrderReq redeemTradeOrderReq) {
		TradeOrderRep rep = new TradeOrderRep();
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService.createCashFailTradeOrder(redeemTradeOrderReq);
		
		rep = investorRedeemTradeOrderService.redeem(orderEntity);
		orderEntity.setOrderStatus(orderEntity.getOrderStatus());
		// 赎回日志记录
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);
		
		return rep;
	}
	
	
	@Transactional
	public TradeOrderRep cashOrder(RedeemTradeOrderReq redeemTradeOrderReq) {
		TradeOrderRep rep = new TradeOrderRep();
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService
				.createCashTradeOrder(redeemTradeOrderReq);
		

		rep = investorRedeemTradeOrderService.redeem(orderEntity);
		orderEntity.setOrderStatus(orderEntity.getOrderStatus());
		// 赎回日志记录
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);

		return rep;
	}
	
	/**
	 * 清盘赎回
	 */
	@Transactional
	public TradeOrderRep clearRedeem(RedeemTradeOrderReq redeemTradeOrderReq) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService.createClearRedeemTradeOrder(redeemTradeOrderReq);
		
		try {
			
			tradeOrderRep = investorRedeemTradeOrderService.redeemRequiresNew(orderEntity.getOrderCode());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(e.getMessage());
			
		}
		// 赎回日志记录
		investorRedeemTradeOrderService.redeemThen(tradeOrderRep, orderEntity.getOrderCode());

		return tradeOrderRep;
	}
	
	@Transactional
	public TradeOrderRep expGoldRedeem(RedeemTradeOrderReq redeemTradeOrderReq) {
		TradeOrderRep rep = new TradeOrderRep();
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService.createExpRedeemTradeOrder(redeemTradeOrderReq);
		
		rep = investorRedeemTradeOrderService.redeem(orderEntity);
		orderEntity.setOrderStatus(orderEntity.getOrderStatus());
		// 赎回日志记录
		OrderLogEntity orderLog = new OrderLogEntity();
		orderLog.setErrorCode(rep.getErrorCode());
		orderLog.setErrorMessage(rep.getErrorMessage());
		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);
		
		return rep;
	}
	
	
	

	@Transactional
	public TradeOrderRep redeem(RedeemTradeOrderReq redeemTradeOrderReq) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();

		/** 校验产品交易时间 */
		if (redeemTradeOrderReq.getPlanRedeemOid() == null) {
			cacheProductService.isInDealTime(redeemTradeOrderReq.getProductOid());
		}
		/** 创建订单 */
		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService.createNormalRedeemTradeOrder(redeemTradeOrderReq);
		
		try {
			
			tradeOrderRep = investorRedeemTradeOrderService.redeemRequiresNew(orderEntity.getOrderCode());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(e.getMessage());
			
			/** 校验产品交易时间 */
			if (redeemTradeOrderReq.getPlanRedeemOid() == null) {
				redisSyncService.saveEntityRefInvestorHoldRequireNew(orderEntity.getInvestorBaseAccount().getOid(), 
					orderEntity.getProduct().getOid());
			}
			
		}
		// 赎回日志记录
		investorRedeemTradeOrderService.redeemThen(tradeOrderRep, orderEntity.getOrderCode());

		return tradeOrderRep;
	}
	
	@Transactional
	public void redeemDo(String orderCode, String taskOid) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();
		
		try {
			
			tradeOrderRep = investorRedeemTradeOrderService.redeemDo(orderCode, taskOid);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			tradeOrderRep.setErrorCode(-1);
			tradeOrderRep.setErrorMessage(AMPException.getStacktrace(e));

		}
		// 赎回日志记录
		investorRedeemTradeOrderService.redeemThen(tradeOrderRep, orderCode);

		
	}
	
	
	@Transactional
	public void investCallBackDo(String orderCode, String returnCode, String taskOid) {
		OrderLogEntity orderLog = new OrderLogEntity();
		
		InvestorTradeOrderEntity orderEntity = this.investorTradeOrderService.findByOrderCode(orderCode);

		if (PayParam.ReturnCode.RC0000.toString().equals(returnCode)) {
			try {

				investorInvestTradeOrderService.investCallBack(orderCode);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				orderLog.setErrorCode(-1);
				orderLog.setErrorMessage(e.getMessage());
			}
		}

		orderLog.setOrderType(orderEntity.getOrderType());
		orderLog.setTradeOrderOid(orderEntity.getOrderCode());
		orderLog.setOrderStatus(orderEntity.getOrderStatus());
		this.orderLogService.create(orderLog);

		serialTaskRequireNewService.updateTime(taskOid);
	}

	

	
	@Transactional
	public BaseResp resumitInvestOrder(CheckOrderReq checkOrderReq) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();
//		/** 创建订单 */
//		TradeOrderReq req = new TradeOrderReq();
//		req.setUid(investorBaseAccountService.findByMemberId(checkOrderReq.getMemberId()).getOid());
//		req.setMoneyVolume(checkOrderReq.getMoneyVolume());
//		req.setProductOid(checkOrderReq.getProductOid());
//		req.setOrderCode(checkOrderReq.getOrderCode());
//		req.setOrderTime(checkOrderReq.getOrderTime());
//		InvestorTradeOrderEntity orderEntity = investorInvestTradeOrderService.createReInvestTradeOrder(req);
//		
//		try {
//			tradeOrderRep = investorInvestTradeOrderService.invest(orderEntity.getOrderCode(), req);
//			this.abandonLogService.create(checkOrderReq.getOrderCode(), orderEntity.getOrderCode());
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//			tradeOrderRep.setErrorCode(-1);
//			tradeOrderRep.setErrorMessage(e.getMessage());
//			this.tulipService.sendInvestFail(orderEntity);
//			
//		}
//		
//		investorInvestTradeOrderService.investThen(tradeOrderRep, orderEntity.getOrderCode());

		return tradeOrderRep;
	}

	
	@Transactional
	public BaseResp resumitRedeemOrder(CheckOrderReq checkOrderReq) {
		TradeOrderRep tradeOrderRep = new TradeOrderRep();
		
		RedeemTradeOrderReq req = new RedeemTradeOrderReq();
		req.setUid(investorBaseAccountService.findByMemberId(checkOrderReq.getMemberId()).getOid());
		req.setOrderAmount(checkOrderReq.getMoneyVolume());
		req.setProductOid(checkOrderReq.getProductOid());
		req.setOrderCode(checkOrderReq.getOrderCode());
		req.setOrderTime(checkOrderReq.getOrderTime());
//		/** 创建订单 */
//		InvestorTradeOrderEntity orderEntity = this.investorRedeemTradeOrderService.createReRedeemTradeOrder(req);
//		
//		try {
//			
//			tradeOrderRep = investorRedeemTradeOrderService.redeemRequiresNew(orderEntity.getOrderCode());
//			this.abandonLogService.create(checkOrderReq.getOrderCode(), orderEntity.getOrderCode());
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getMessage(), e);
//			tradeOrderRep.setErrorCode(-1);
//			tradeOrderRep.setErrorMessage(AMPException.getStacktrace(e));
//
//		}
//		// 赎回日志记录
//		investorRedeemTradeOrderService.redeemThen(tradeOrderRep, orderEntity.getOrderCode());

		return tradeOrderRep;
	}


	public legalFileResp queryInvestorFile(String orderCode, String typeCode,String investorOid) {
		
		legalFileResp resp = new legalFileResp();		
		//获取协议文件
		if(!StringUtil.isEmpty(typeCode)){
			List<LegalFileEntity> legalFileEntity = this.legalFileDao.findByTypeCode(typeCode);
			if(legalFileEntity != null && legalFileEntity.size() > 0){
				for(LegalFileEntity entity : legalFileEntity){
					LegalFileResp rep = new LegalFileResp(entity);	
					resp.getFile().add(rep);
				} 
			}
		}else{
			throw new AMPException("协议类型不能为 空");
		}
		
		/** 获取协议中相应的参数类型  */
		Object obj = this.queryFileParam(orderCode, typeCode,investorOid);
		resp.setData(obj);
		return resp;
	}
	
	private Object queryFileParam(String orderCode, String typeCode, String investorOid) {
		if (!StringUtil.isEmpty(typeCode)) {
			// 注册协议
			if (typeCode.equals("10000")) {
				RegistFile registFile = new RegistFile();

				if (!StringUtil.isEmpty(investorOid)) {
					InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByMemberId(investorOid);
					if (baseAccount != null) {
						List<Integer> typeList = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_REGIST);
						EbaoquanRecord eBaoQuan = this.recordDao.findByRelateKey(investorOid, typeList);
						if (eBaoQuan != null) {
							RegistFile regist = JSON.parseObject(eBaoQuan.getFileParam(), RegistFile.class);
							// BeanUtils.copyProperties(regist,registFile);
							if(regist != null){
								registFile.setConstractNum(regist.getConstractNum());
								registFile.setFirstParty(StringUtil.kickstarOnPhoneNum(regist.getFirstParty()));
								registFile.setSecondParty(regist.getSecondParty());
								registFile.setTradeTime(regist.getTradeTime());
							}
						}else{
							registFile.setTradeTime(DateUtil.getTimestampFormated(baseAccount.getCreateTime()));// 订单时间
							registFile.setFirstParty(StringUtil.kickstarOnPhoneNum(baseAccount.getPhoneNum()));// 甲方手机号
							registFile.setConstractNum(this.getConstractNum("GENREG", null,baseAccount));
							registFile.setSecondParty(Html2PdfService.COMPANY_NAME);
						}
					}
				}
				return registFile;
			}
			// 活、定期转入协议
			if (typeCode.equals("10001") || typeCode.equals("10002")) {
				FileParam productFile = new FileParam();
				if (!StringUtil.isEmpty(orderCode)) {
					InvestorTradeOrderEntity tradeEntity = this.investorTradeOrderDao.findByOrderCode(orderCode);
					if (tradeEntity != null) {
						List<Integer> typeList = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_INVEST_OPEN,
								EbaoquanRecord.EBAOQUAN_TYPE_INVEST_CLOSE);
						EbaoquanRecord eBaoQuan = this.recordDao.findByRelateKey(tradeEntity.getOrderCode(), typeList);
						if (eBaoQuan != null) {
							FileParam fileParam = JSON.parseObject(eBaoQuan.getFileParam(), FileParam.class);
							if(fileParam != null){
								productFile.setConstractNum(fileParam.getConstractNum());
								productFile.setFirstParty(StringUtil.kickstarOnRealname(fileParam.getFirstParty()));
								productFile.setFirstPartyIdNum(
										this.sonAccountService.newKickstarOnIdNum(fileParam.getFirstPartyIdNum()));
								productFile.setSecondParty(fileParam.getSecondParty());
								productFile.setTradeTime(fileParam.getTradeTime());
							}
						}else{
							productFile.setTradeTime(DateUtil.getTimestampFormated(tradeEntity.getOrderTime()));// 合同签署时间
							productFile.setFirstParty(
									StringUtil.kickstarOnRealname(tradeEntity.getInvestorBaseAccount().getRealName()));// 甲方姓名
							//productFile.setSecondParty(tradeEntity.getProduct().getPublisherBaseAccount().getRealName());// 乙方
							productFile.setSecondParty(Html2PdfService.COMPANY_NAME);//乙方
							productFile.setFirstPartyIdNum(this.sonAccountService
									.newKickstarOnIdNum(tradeEntity.getInvestorBaseAccount().getIdNum()));// 甲方身份证号
							if(typeCode.equals("10001")){
								productFile.setConstractNum(this.getConstractNum("GENHQBUY", tradeEntity,null));
							}else if(typeCode.equals("10002")){
								productFile.setConstractNum(this.getConstractNum("GENDQBUY", tradeEntity,null));
							}
							
						}
					}
				}
				return productFile;
			}

			// 定向委托协议
			if (typeCode.equals("10003")) {
				FileParam directFile = new FileParam();
				if (!StringUtil.isEmpty(orderCode)) {
					InvestorTradeOrderEntity tradeEntity = this.investorTradeOrderDao.findByOrderCode(orderCode);
					if (tradeEntity != null) {
						List<Integer> typeList = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_OPEN,
								EbaoquanRecord.EBAOQUAN_TYPE_AGREE_CLOSE);
						EbaoquanRecord eBaoQuan = this.recordDao.findByRelateKey(tradeEntity.getOrderCode(), typeList);
						if (eBaoQuan != null) {
							FileParam fileParam = JSON.parseObject(eBaoQuan.getFileParam(), FileParam.class);
							if(fileParam != null){
							directFile.setConstractNum(fileParam.getConstractNum());
							directFile.setFirstParty(StringUtil.kickstarOnRealname(fileParam.getFirstParty()));
							directFile.setFirstPartyIdNum(
									this.sonAccountService.newKickstarOnIdNum(fileParam.getFirstPartyIdNum()));
							directFile.setSecondParty(fileParam.getSecondParty());
							directFile.setTradeTime(fileParam.getTradeTime());
							}
						}else{
							directFile.setTradeTime(DateUtil.getTimestampFormated(tradeEntity.getOrderTime()));// 合同签署时间
							directFile.setFirstParty(
									StringUtil.kickstarOnRealname(tradeEntity.getInvestorBaseAccount().getRealName()));// 甲方姓名
							//directFile.setSecondParty(tradeEntity.getProduct().getPublisherBaseAccount().getRealName());// 乙方
							directFile.setSecondParty(Html2PdfService.COMPANY_NAME);//乙方
							directFile.setFirstPartyIdNum(this.sonAccountService
									.newKickstarOnIdNum(tradeEntity.getInvestorBaseAccount().getIdNum()));// 甲方身份证号
							directFile.setConstractNum(this.getConstractNum("GENDXWT",tradeEntity,null));
						}
					}
				}
				return directFile;
			}

			// 心愿计划产品转入协议（一次性定投）
			if (typeCode.equals("10004")) {
				WishPlanOnceFile onceFile = new WishPlanOnceFile();
				if (!StringUtil.isEmpty(orderCode)) {
					String planOid = this.investorTradeOrderDao.findPlanOidByOrdeCode(orderCode);
					List<Integer> typeList = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_ONCE);
					EbaoquanRecord eBaoQuan = this.recordDao.findByRelateKey(planOid, typeList);
					if (eBaoQuan != null) {
						// 心愿计划产品转入合同编号
						FileParam fileParam = JSON.parseObject(eBaoQuan.getFileParam(), FileParam.class);
						if(fileParam != null){
						onceFile.setConstractNum(fileParam.getConstractNum());
						onceFile.setFirstParty(StringUtil.kickstarOnRealname(fileParam.getFirstParty()));
						onceFile.setFirstPartyIdNum(
								this.sonAccountService.newKickstarOnIdNum(fileParam.getFirstPartyIdNum()));
						onceFile.setSecondParty(fileParam.getSecondParty());
						onceFile.setTradeTime(fileParam.getTradeTime());
						}
					}

					List<Integer> typeList2 = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE,
							EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH);
					EbaoquanRecord eBaoQuan2 = this.recordDao.findByRelateKey(planOid, typeList2);
					if (eBaoQuan2 != null) {
						FileParam fileParam2 = JSON.parseObject(eBaoQuan2.getFileParam(), FileParam.class);
						if(fileParam2 != null){	
						onceFile.setConstractNum2(fileParam2.getConstractNum());// 定向委托协议合同编号
						onceFile.setDXSecondParty(fileParam2.getSecondParty());
						}
					}
					if(eBaoQuan == null && eBaoQuan2 == null){
						InvestorTradeOrderEntity tradeEntity = this.investorTradeOrderDao.findByOrderCode(orderCode);
						if (tradeEntity != null) {
							onceFile.setTradeTime(DateUtil.getTimestampFormated(tradeEntity.getOrderTime()));// 合同签署时间
							onceFile.setFirstParty(
									StringUtil.kickstarOnRealname(tradeEntity.getInvestorBaseAccount().getRealName()));// 甲方姓名
							onceFile.setFirstPartyIdNum(this.sonAccountService
									.newKickstarOnIdNum(tradeEntity.getInvestorBaseAccount().getIdNum()));// 甲方身份证号
							//onceFile.setSecondParty(this.queryPublisher(tradeEntity.getWishplanOid()));//乙方
							onceFile.setSecondParty(Html2PdfService.COMPANY_NAME);//乙方
							onceFile.setConstractNum(this.getConstractNum("GENXYPBUY", tradeEntity,null));// 心愿计划产品转入合同编号
							onceFile.setConstractNum2(this.getConstractNum("GENDXWT", tradeEntity,null));// 定向委托协议合同编号
							onceFile.setDXSecondParty(Html2PdfService.COMPANY_NAME);//定向委托的乙方
						}
					}
				}
				return onceFile;
			}

			// 心愿计划产品转入协议（按月定投）
			if (typeCode.equals("10005")) {
				WishPlanMonthFile monthFile = new WishPlanMonthFile();
				if (!StringUtil.isEmpty(orderCode)) {
						String planOid = this.investorTradeOrderDao.findMonthPlanOidByOrdeCode(orderCode);
						List<Integer> typeList = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_WISHPLAN_MONTH);
						EbaoquanRecord eBaoQuan = this.recordDao.findByRelateKey(planOid, typeList);
						if (eBaoQuan != null) {
							FileParam fileParam = JSON.parseObject(eBaoQuan.getFileParam(), FileParam.class);
							if(fileParam != null){	
							monthFile.setConstractNum(fileParam.getConstractNum());
							monthFile.setFirstParty(StringUtil.kickstarOnRealname(fileParam.getFirstParty()));
							monthFile.setFirstPartyIdNum(
									this.sonAccountService.newKickstarOnIdNum(fileParam.getFirstPartyIdNum()));
							monthFile.setSecondParty(fileParam.getSecondParty());
							monthFile.setTradeTime(fileParam.getTradeTime());
							}
						}

						List<Integer> typeList2 = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_ONCE,
								EbaoquanRecord.EBAOQUAN_TYPE_AGREE_WISHPLAN_MONTH);
						EbaoquanRecord eBaoQuan2 = this.recordDao.findByRelateKey(planOid, typeList2);
						if (eBaoQuan2 != null) {
							FileParam fileParam2 = JSON.parseObject(eBaoQuan2.getFileParam(), FileParam.class);
							if(fileParam2 != null){	
							monthFile.setConstractNum2(fileParam2.getConstractNum());// 定向委托协议合同编号
							monthFile.setDXSecondParty(fileParam2.getSecondParty());
							}
						}

						List<Integer> typeList3 = Arrays.asList(EbaoquanRecord.EBAOQUAN_TYPE_WITHHOLD);
						EbaoquanRecord eBaoQuan3 = this.recordDao.findByRelateKey(planOid, typeList3);
						if (eBaoQuan3 != null) {
							FileParam fileParam3 = JSON.parseObject(eBaoQuan3.getFileParam(), FileParam.class);
							if(fileParam3 != null){						
							monthFile.setConstractNum3(fileParam3.getConstractNum());// 定向委托协议合同编号
							}
						}
						if(eBaoQuan == null && eBaoQuan2 == null && eBaoQuan3 == null){
							InvestorTradeOrderEntity tradeEntity = this.investorTradeOrderDao.findByOrderCode(orderCode);
							if (tradeEntity != null) {
								monthFile.setTradeTime(DateUtil.getTimestampFormated(tradeEntity.getOrderTime()));// 合同签署时间
								monthFile.setFirstParty(
										StringUtil.kickstarOnRealname(tradeEntity.getInvestorBaseAccount().getRealName()));// 甲方姓名
								monthFile.setFirstPartyIdNum(this.sonAccountService
										.newKickstarOnIdNum(tradeEntity.getInvestorBaseAccount().getIdNum()));// 甲方身份证号
								PlanInvestEntity onceInvest = this.planInvestDao.findByOid(tradeEntity.getWishplanOid());
								//monthFile.setSecondParty(this.queryPublisher(onceInvest.getMonthOid()));//乙方
								monthFile.setSecondParty(Html2PdfService.COMPANY_NAME);//乙方
								monthFile.setConstractNum(this.getConstractNum("GENXYPBUY", tradeEntity,null));
								monthFile.setConstractNum2(this.getConstractNum("GENDXWT", tradeEntity,null));
								monthFile.setConstractNum3(this.getConstractNum("GENDK", tradeEntity,null));
								monthFile.setDXSecondParty(Html2PdfService.COMPANY_NAME);
							}
						}
					
				}
				return monthFile;
			}
		}
		return null;
	}

	/**
	 * 获取合同编号
	 * 
	 * */
	public String getConstractNum(String type, InvestorTradeOrderEntity tradeEntity,
			InvestorBaseAccountEntity baseAccount) {

		String investorOid = "";
		Timestamp time = null;
		if (tradeEntity != null) {
			investorOid = tradeEntity.getInvestorBaseAccount().getMemberId();
			time = tradeEntity.getOrderTime();
		} else {
			investorOid = baseAccount.getMemberId();
			time = baseAccount.getCreateTime();
		}
		if (investorOid.length() > 6) {
			investorOid = investorOid.substring(investorOid.length() - 6, investorOid.length());
		}
		return type + "" + investorOid + "" + DateUtil.timestamp2FullStr(time);
	}
		
	/**
	 * 查询计划匹配到第一个产品时的发行人
	 * */
	public String queryPublisher(String planOid){
		List<PlanProductEntity> list = this.planBaseService.queryRelateProduct(planOid);
		if(list != null && list.size() > 0){
			String productOid = ((PlanProductEntity)list.get(0)).getProductOid();
			Product p = this.productDao.findByOid(productOid);
			return p.getPublisherBaseAccount().getRealName();					
		}
		return null;
	}
}
