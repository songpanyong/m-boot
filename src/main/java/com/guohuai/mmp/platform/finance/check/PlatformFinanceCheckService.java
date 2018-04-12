package com.guohuai.mmp.platform.finance.check;



import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Collections3;
import com.guohuai.component.util.DateUtil;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderDao;
import com.guohuai.mmp.investor.bankorder.InvestorBankOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderDao;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderEntity;
import com.guohuai.mmp.investor.tradeorder.InvestorTradeOrderRep;
import com.guohuai.mmp.job.JobEnum;
import com.guohuai.mmp.job.lock.JobLockService;
import com.guohuai.mmp.job.log.JobLogEntity;
import com.guohuai.mmp.job.log.JobLogFactory;
import com.guohuai.mmp.job.log.JobLogService;
import com.guohuai.mmp.platform.finance.data.PlatformFinanceCompareDataDao;
import com.guohuai.mmp.platform.finance.data.PlatformFinanceCompareDataEntity;
import com.guohuai.mmp.platform.finance.data.PlatformFinanceCompareDataNewService;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultEntity;
import com.guohuai.mmp.platform.finance.result.PlatformFinanceCompareDataResultNewService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PlatformFinanceCheckService {
	@Autowired
	private PlatformFinanceCheckNewService platformFinanceCheckNewService;
	@Autowired
	private PlatformFinanceCheckDao platformFinanceCheckDao;
	@Autowired
	private InvestorTradeOrderDao investorTradeOrderDao;
	@Autowired
	private PlatformFinanceCompareDataResultNewService platformFinanceCompareDataResultNewService;
	@Autowired
	private InvestorBankOrderDao investorBankOrderDao;
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private JobLockService jobLockService;
	
	
	/**
	 * 查询对账列表
	 */
	public PageResp<PlatformFinanceCheckRep> checkDataList(Specification<PlatformFinanceCheckEntity> spec,
			Pageable pageable) {
		Page<PlatformFinanceCheckEntity> cas = this.platformFinanceCheckDao.findAll(spec, pageable);
		PageResp<PlatformFinanceCheckRep> pagesRep = new PageResp<PlatformFinanceCheckRep>();
		for (PlatformFinanceCheckEntity entity : cas) {
			PlatformFinanceCheckRep rep = new PlatformFinanceCheckRep();
			rep.setCheckOid(entity.getOid());
			rep.setCheckCode(entity.getCheckCode());
			rep.setCheckDate(entity.getCheckDate());
			rep.setCheckStatus(entity.getCheckStatus());
			rep.setCheckStatusDisp(this.checkStatusEn2Ch(entity.getCheckStatus()));
			rep.setCheckDataSyncStatus(entity.getCheckDataSyncStatus());
			rep.setCheckDataSyncStatusDisp(this.checkDataSyncStatusEn2Ch(entity.getCheckDataSyncStatus()));
			rep.setLdataStatus(entity.getLdataStatus());
			rep.setLdataStatusDisp(this.ldataStatusEn2Ch(entity.getLdataStatus()));
			rep.setGaStatus(entity.getGaStatus());
			rep.setGaStatusDisp(this.gaStatusEn2Ch(entity.getGaStatus()));
			rep.setWrongCount(entity.getWrongCount());
			rep.setWrongLeftCount(entity.getWrongLeftCount());
			rep.setBeginTime(entity.getBeginTime());
			rep.setEndTime(entity.getEndTime());
			
			rep.setCreateTime(entity.getCreateTime());
			rep.setUpdateTime(entity.getUpdateTime());
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	
	private String gaStatusEn2Ch(String gaStatus) {
		if (PlatformFinanceCheckEntity.CHECK_gaStatus_toGa.equals(gaStatus)) {
			return "待对账";
		}
		if (PlatformFinanceCheckEntity.CHECK_gaStatus_gaing.equals(gaStatus)) {
			return "对账中";
		}
		if (PlatformFinanceCheckEntity.CHECK_gaStatus_gaOk.equals(gaStatus)) {
			return "对账完成";
		}
		if (PlatformFinanceCheckEntity.CHECK_gaStatus_gaFailed.equals(gaStatus)) {
			return "对账失败";
		}
		
		return gaStatus;
	}
	
	
//	/** 对账数据同步状态--待同步 */
//	public static final String  CHECKDATASYNCSTATUS_toSync = "toSync";
//	/** 对账数据同步状态--同步失败 */
//	public static final String  CHECKDATASYNCSTATUS_syncFailed = "syncFailed";
//	/** 对账数据同步状态--同步成功 */
//	public static final String  CHECKDATASYNCSTATUS_syncOK = "syncOK";
//	/** 对账数据同步状态--导入中 */
//	public static final String  CHECKDATASYNCSTATUS_syncing = "syncing";
	
	private String checkDataSyncStatusEn2Ch(String checkDataSyncStatus) {
		if (PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_toSync.equals(checkDataSyncStatus)) {
			return "待同步";
		}
		if (PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_syncFailed.equals(checkDataSyncStatus)) {
			return "同步失败";
		}
		if (PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_syncOK.equals(checkDataSyncStatus)) {
			return "同步成功";
		}
		if (PlatformFinanceCheckEntity.CHECKDATASYNCSTATUS_syncing.equals(checkDataSyncStatus)) {
			return "同步中";
		}
		return checkDataSyncStatus;
	}
	
//	/** 本地对账数据--待准备 */
//	public static final String  CHECK_ldataStatus_toPrepare = "toPrepare";
//	/** 本地对账数据--ing */
//	public static final String  CHECK_ldataStatus_prepareing = "prepareing";
//	/** 本地对账数据--已准备 */
//	public static final String  CHECK_ldataStatus_prepared = "prepared";
//	/** 本地对账数据--准备失败 */
//	public static final String  CHECK_ldataStatus_prepareFailed = "prepareFailed";
	
	private String ldataStatusEn2Ch(String ldataStatus) {
		if (PlatformFinanceCheckEntity.CHECK_ldataStatus_toPrepare.equals(ldataStatus)) {
			return "待准备";
		}
		if (PlatformFinanceCheckEntity.CHECK_ldataStatus_prepareing.equals(ldataStatus)) {
			return "准备中";
		}
		if (PlatformFinanceCheckEntity.CHECK_ldataStatus_prepared.equals(ldataStatus)) {
			return "已准备";
		}
		if (PlatformFinanceCheckEntity.CHECK_ldataStatus_prepareFailed.equals(ldataStatus)) {
			return "准备失败";
		}
		return ldataStatus;
		
	}
	
	
	private String checkStatusEn2Ch(String checkStatus) {
		if (PlatformFinanceCheckEntity.CHECKSTATUS_TOCHECK.equals(checkStatus)) {
			return "待处理";
		}
		if (PlatformFinanceCheckEntity.CHECKSTATUS_CHECKING.equals(checkStatus)) {
			return "处理中";
		}
		if (PlatformFinanceCheckEntity.CHECKSTATUS_CHECKSUCCESS.equals(checkStatus)) {
			return "处理成功";
		}
		
		return checkStatus;
	}
	
	
	/**
	 * 对账
	 * @param checkOid
	 * @param checkDate
	 * @param operator
	 * @return
	 */
	public BaseResp checkOrder(String checkOid, String checkDate,String operator){
		BaseResp rep = new BaseResp();
		try{
			Long count=platformFinanceCompareDataResultNewService.countByCheckOid(checkOid);
			if(count > 0){
				throw new AMPException("请先处理对账结果里处理状态为【处理中】的数据！");
			}
			//更新状态为对账中
			platformFinanceCheckNewService.checking(checkOid,operator);
			//线程独立处理逻辑
			new Thread(new Runnable() {
				@Override
				public void run() {
					checkOrderDo(checkOid,checkDate,operator);
				}
			}).start();
		}catch(AMPException e){
			e.printStackTrace();
			rep.setErrorCode(-1);
			rep.setErrorMessage(e.getMessage());
		}
		return rep;
	}
	/**
	 * 对账处理
	 * @param checkOid
	 * @param checkDate
	 * @param operator
	 */
	public void checkOrderDo(String checkOid, String checkDate,String operator) {
		int errorCount=0;//错误数
		String orderTradeCode="0";// 申购赎回
		String orderBankCode="0";//提现充值
		//查询对账批次
		PlatformFinanceCheckEntity chekEntity=platformFinanceCheckDao.findOne(checkOid);
		try{
			/** 删除历史对账数据 */
			platformFinanceCompareDataResultNewService.deleteByCheckOid(checkOid);
			List<PlatformFinanceCompareDataResultEntity> compareDataResultList=new ArrayList<PlatformFinanceCompareDataResultEntity>();
			//获取订单查询开始时间
			String beginTime=DateUtil.convertDate2String(DateUtil.fullDatePattern, chekEntity.getBeginTime());
			//获取订单查询结束时间
			String endTime=DateUtil.convertDate2String(DateUtil.fullDatePattern, chekEntity.getEndTime());
			while (true) {
				/** 1.查询充值提现数据 */
				List<InvestorTradeOrderRep> investorBankOrderList = handlerArray2Obj(investorBankOrderDao.findBankOrderByOrderTime(beginTime , endTime , orderBankCode));
				/** 2.查询申购赎回数据 */
				List<InvestorTradeOrderRep> investorTradeOrderList = handlerArray2Obj(investorTradeOrderDao.findInvestorOrderByOrderTime(beginTime , endTime , orderTradeCode));
				/**  充值提现，申购赎回数据，库里都没有时 */
				if (Collections3.isEmpty(investorBankOrderList) && Collections3.isEmpty(investorTradeOrderList) ) {
					break;
				}
				//对账处理 (提现跟充值)
				if(!Collections3.isEmpty(investorBankOrderList)){
					compareDataResultList=handlerCheckOrder(investorBankOrderList,checkOid,checkDate);
					platformFinanceCompareDataResultNewService.save(compareDataResultList);
					for(PlatformFinanceCompareDataResultEntity entity : compareDataResultList){
//						if(!entity.getCheckStatus().equals(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS)){
//							errorCount++;
//						}
					}
					InvestorTradeOrderRep e=investorBankOrderList.get(investorBankOrderList.size()-1);
					orderBankCode=e.getOrderCode();
				}
				//对账处理 (申购跟赎回)
				if(!Collections3.isEmpty(investorTradeOrderList)){
					compareDataResultList=handlerCheckOrder(investorTradeOrderList,checkOid,checkDate);
					platformFinanceCompareDataResultNewService.save(compareDataResultList);
					for(PlatformFinanceCompareDataResultEntity entity : compareDataResultList){
//						if(!PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS.equals(entity.getCheckStatus())){
//							errorCount++;
//						}
					}
					InvestorTradeOrderRep e=investorTradeOrderList.get(investorTradeOrderList.size()-1);
					orderTradeCode=e.getOrderCode();
				}
			
			}
//			Integer lessThenCount=handlerLessThenOrder(checkDate,checkOid);
			PlatformFinanceCheckEntity pfcEntity=platformFinanceCheckNewService.findByCheckDate(DateUtil.parseToSqlDate(checkDate));
//			errorCount=errorCount+lessThenCount;
			pfcEntity.setWrongCount(errorCount);
			if(errorCount == 0){
				pfcEntity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_CHECKSUCCESS);
			}else{
//				pfcEntity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_CHECKFAILED);
			}
//			pfcEntity.setOperator(operator);
			pfcEntity.setUpdateTime(DateUtil.getSqlCurrentDate());
			platformFinanceCheckNewService.save(pfcEntity);
		}catch(Exception e){
			
			chekEntity.setCheckStatus(PlatformFinanceCheckEntity.CHECKSTATUS_TOCHECK);
			platformFinanceCheckNewService.save(chekEntity);
			throw e;
		}
	}
	/**
	 * 封装查询结果集 
	 * @param findInvestorOrderByOrderTime
	 * @return
	 */
	private List<InvestorTradeOrderRep> handlerArray2Obj(List<Object[]> findInvestorOrderByOrderTime) {
		List<InvestorTradeOrderRep> returnList=new ArrayList<InvestorTradeOrderRep>();
		InvestorTradeOrderRep rep=null;
		try {
			for(Object[] obj : findInvestorOrderByOrderTime){
				rep=new InvestorTradeOrderRep();
				rep.setInvestorOid(obj[4]+"");
				rep.setOrderAmount(new BigDecimal(obj[3]+""));
				rep.setOrderType(obj[2]+"");
				rep.setOrderStatus(obj[1]+"");
				rep.setOrderCode(obj[0]+"");
				rep.setOrderTime(DateUtil.fetchTimestamp(obj[5]+""));
				if(obj.length>=6 && null != obj[6]){
					rep.setProductType(obj[6]+"");
				}
				returnList.add(rep);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnList;
	}
//	/**
//	 * 少帐处理--返回少帐记录数
//	 * @param checkDate
//	 * @return
//	 */
//	private Integer handlerLessThenOrder(String checkDate,String checkOid) {
//		List<PlatformFinanceCompareDataResultEntity> compareDataResultList=new ArrayList<PlatformFinanceCompareDataResultEntity>();
//		/** 数据库数据轮询一次后，对账数据里还是有没被检查过的数据，这是我们少账了，因为拉取过来有，自己库里没有 */
//		List<PlatformFinanceCompareDataEntity> listCompareData=platformFinanceCompareDataDao.findByCheckDateAndCheckStatus(checkDate);
//		PlatformFinanceCompareDataResultEntity compareDataResultEntity=null;
//		for(PlatformFinanceCompareDataEntity cdEntity : listCompareData){
//			compareDataResultEntity=new PlatformFinanceCompareDataResultEntity();
//			compareDataResultEntity.setBuzzDate(DateUtil.parseToSqlDate(checkDate));
//			compareDataResultEntity.setCheckInvestorOid(cdEntity.getInvestorOid());
//			compareDataResultEntity.setCheckOid(checkOid);
//			compareDataResultEntity.setCheckOrderAmount(cdEntity.getTradeAmount());
//			compareDataResultEntity.setOrderAmount(null);
//			compareDataResultEntity.setOrderTime(cdEntity.getOrderTime());
//			compareDataResultEntity.setCheckOrderCode(cdEntity.getOrderCode());
//			compareDataResultEntity.setCheckOrderStatus(cdEntity.getOrderStatus());
//			compareDataResultEntity.setCheckOrderType(cdEntity.getOrderType());
//			compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_LESSTHEN);
//			compareDataResultEntity.setCreateTime(DateUtil.getSqlCurrentDate());
//			compareDataResultEntity.setUpdateTime(DateUtil.getSqlCurrentDate());
//			compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//			compareDataResultList.add(compareDataResultEntity);
//		}
//		platformFinanceCompareDataResultNewService.save(compareDataResultList);
//		return compareDataResultList.size();
//	}
	/**
	 * 除少帐外的一致，多帐，异常等对账处理
	 * @param investorTradeOrderList
	 * @param checkOid
	 * @param checkDate
	 * @return
	 */
	private List<PlatformFinanceCompareDataResultEntity> handlerCheckOrder(
		List<InvestorTradeOrderRep> investorTradeOrderList, String checkOid, String checkDate) {
		/** 初始化对账数据 */
		List<PlatformFinanceCompareDataResultEntity> compareDataResultList = new ArrayList<PlatformFinanceCompareDataResultEntity>();
		/** 始化对账结果 */
//		List<PlatformFinanceCompareDataEntity> listCompareData = new ArrayList<PlatformFinanceCompareDataEntity>();
//		for (InvestorTradeOrderRep itEntity : investorTradeOrderList) {
//			/** 通过数据库里的订单orderCode去查询同步的数据 */
//			PlatformFinanceCompareDataEntity compareDataEntity = platformFinanceCompareDataDao.findByOrderCode(itEntity.getOrderCode());
//			/** 封装对账结果 compareDataEntity TO  compareDataResultEntity ***/
//			PlatformFinanceCompareDataResultEntity compareDataResultEntity = initCompareDataResultEntity(checkOid,checkDate, itEntity);
//			/** 自己库里有数据，同步过来查询为空，则为多账 **/
//			if (null == compareDataEntity) {
//				compareDataResultEntity.setCheckOrderAmount(null);
//				compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//				compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_MORETHEN);
//			} else {
//				compareDataResultEntity.setCheckOrderAmount(compareDataEntity.getTradeAmount());
//				boolean excpetionFlag = false;
//				logger.info("订单金额"+itEntity.getOrderAmount()+" |对账订单金额 "+compareDataEntity.getTradeAmount());
//				logger.info("订单投资人"+itEntity.getInvestorOid()+" |对账订单投资人 "+compareDataEntity.getInvestorOid());
//				/******************* 充值,提现 ,申购,赎回 订单金额跟投资人需一致  同步过来数据 【1-成功，0-失败】*************************************************/
//				if (itEntity.getOrderAmount().compareTo(compareDataEntity.getTradeAmount()) == 0
//						&& itEntity.getInvestorOid().equals(compareDataEntity.getInvestorOid())) {//钱跟人需要一致对外
//					
//					/******************* 充值逻辑   **********************/
//					if (InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(itEntity.getOrderType())
//							&& InvestorBankOrderEntity.BANKORDER_orderType_deposit.equals(compareDataResultEntity.getOrderType())) {//对比同一个订单状态都为充值
//						/** 充值成功，数据库done代表成功,同步数据1代表成功 */
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorBankOrderEntity.BANKORDER_orderStatus_done)) {//成功充值数据一致
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						/** 充值失败，数据库【submitted-已申请】 【submitFailed-申请失败】 【toPay-待支付】 【payFailed-支付失败】 四种状态都是失败,同步数据0代笔失败  */
//						}else if ("2".equals(compareDataEntity.getOrderStatus()) && (
//								itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted)//已申请
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed) //申请失败
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay)//待支付
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed)
//								)){//
//							//支付失败 
//								compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//								compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//								compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//								setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						}
//					}
//					/******************* 提现逻辑，同上 **************************/
//					if (InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(itEntity.getOrderType())
//							&& InvestorBankOrderEntity.BANKORDER_orderType_withdraw.equals(compareDataResultEntity.getOrderType())) {//提现
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorBankOrderEntity.BANKORDER_orderStatus_done)) {//提现成功
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else if (("2".equals(compareDataEntity.getOrderStatus()) || "3".equals(compareDataEntity.getOrderStatus())) && (
//								itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted)//已申请
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed) //申请失败
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay)//待支付
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed)//支付失败
//								)) {//提现失败
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						} 
//					}
//					/***************************************  冲正逻辑同上   ********************************************************/
//					if(InvestorBankOrderEntity.BANKORDER_orderType_offsetPositive.equals(itEntity.getOrderType())
//							&& InvestorBankOrderEntity.BANKORDER_orderType_offsetPositive.equals(compareDataResultEntity.getOrderType())){//冲正
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorBankOrderEntity.BANKORDER_orderStatus_done)) {//冲正成功
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else if ("2".equals(compareDataEntity.getOrderStatus()) && (
//								itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted)//已申请
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed) //申请失败
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay)//待支付
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed)//支付失败
//								)) {//提现失败
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						} 
//					}
//					/***************************************  冲负逻辑同上   ********************************************************/
//					if(InvestorBankOrderEntity.BANKORDER_orderType_offsetNegative.equals(itEntity.getOrderType())
//							&& InvestorBankOrderEntity.BANKORDER_orderType_offsetNegative.equals(compareDataResultEntity.getOrderType())){//冲负
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorBankOrderEntity.BANKORDER_orderStatus_done)) {//冲负成功
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else if ("2".equals(compareDataEntity.getOrderStatus()) && (
//								itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted)//已申请
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed) //申请失败
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay)//待支付
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed)//支付失败
//								)) {//提现失败
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						} 
//					}
//					/***************************************  红包逻辑同上   ********************************************************/
//					if(InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope.equals(itEntity.getOrderType())
//							&& InvestorBankOrderEntity.BNAKORDER_orderType_redEnvelope.equals(compareDataResultEntity.getOrderType())){//红包
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorBankOrderEntity.BANKORDER_orderStatus_done)) {//红包成功
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else if ("2".equals(compareDataEntity.getOrderStatus()) && (
//								itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitted)//已申请
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_submitFailed) //申请失败
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_toPay)//待支付
//								|| itEntity.getOrderStatus().equals(InvestorBankOrderEntity.BANKORDER_orderStatus_payFailed)//支付失败
//								)) {//提现失败
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						} 
//					}
//					
//					
//					
//					
//					/***************************************  投资逻辑同上   ********************************************************/
//					if ((InvestorTradeOrderEntity.TRADEORDER_orderType_invest.equals(itEntity.getOrderType()) || InvestorTradeOrderEntity.TRADEORDER_orderType_reInvest.equals(itEntity.getOrderType()))
//							&& "invest".equals(compareDataEntity.getOrderType())) {
//						if ("1".equals(compareDataEntity.getOrderStatus()) && 
//								(itEntity.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_accepted)
//								|| itEntity.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed))) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						} 
//						else if ("2".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus().equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_payFailed)) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						}
//					}
//					/****************************************** 赎回逻辑同上   **************************************************************/
//					if ((InvestorTradeOrderEntity.TRADEORDER_orderType_normalRedeem.equals(itEntity.getOrderType()) || 
//							InvestorTradeOrderEntity.TRADEORDER_orderType_reRedeem.equals(itEntity.getOrderType()))
//							&& "redeem".equals(compareDataEntity.getOrderType())) {
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed)) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						} else if ("2".equals(compareDataEntity.getOrderStatus()) && !itEntity.getOrderStatus()
//								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed)) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						}
//					}
//					/****************************************** 清盘，还本付息，募集失败退款   **************************************************************/
//					if ((InvestorTradeOrderEntity.TRADEORDER_orderType_clearRedeem.equals(itEntity.getOrderType()) || 
//							InvestorTradeOrderEntity.TRADEORDER_orderType_cash.equals(itEntity.getOrderType())  ||
//							InvestorTradeOrderEntity.TRADEORDER_orderType_cashFailed.equals(itEntity.getOrderType()))
//							&& "redeem".equals(compareDataEntity.getOrderType())) {
//						if ("1".equals(compareDataEntity.getOrderStatus()) && itEntity.getOrderStatus()
//								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed)) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						} else if ("2".equals(compareDataEntity.getOrderStatus()) && !itEntity.getOrderStatus()
//								.equals(InvestorTradeOrderEntity.TRADEORDER_orderStatus_confirmed)) {
//							compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//							compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EQUALS);
//							compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//							setProperties(compareDataResultEntity, compareDataEntity);
//						}else{
//							excpetionFlag = true;
//						}
//					}
//				}else{
//					excpetionFlag = true;
//				}
//				if (excpetionFlag) {
//					compareDataResultEntity.setOrderTime(itEntity.getOrderTime());
//					compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
//					compareDataResultEntity.setCheckStatus(PlatformFinanceCompareDataResultEntity.CHECKSTATUS_EXCEPTION);
//					setProperties(compareDataResultEntity, compareDataEntity);
//				}
//			}
//			if(null != compareDataEntity){
//				compareDataEntity.setCheckStatus(PlatformFinanceCompareDataEntity.CHECKSTATUS_YES);
//				listCompareData.add(compareDataEntity);
//			}
//			compareDataResultList.add(compareDataResultEntity);
//		}
//		platformFinanceCompareDataNewService.save(listCompareData);
		return compareDataResultList;
	}
	/**
	 * 初始化对账结果数据
	 * @param checkOid
	 * @param checkDate
	 * @param itEntity
	 * @return
	 */
	private PlatformFinanceCompareDataResultEntity initCompareDataResultEntity(String checkOid, String checkDate, InvestorTradeOrderRep itEntity) {
		PlatformFinanceCompareDataResultEntity compareDataResultEntity = new PlatformFinanceCompareDataResultEntity();
//		compareDataResultEntity.setBuzzDate(DateUtil.parseToSqlDate(checkDate));
//		compareDataResultEntity.setCheckOid(checkOid);
//		compareDataResultEntity.setInvestorOid(itEntity.getInvestorOid());
//		compareDataResultEntity.setOrderAmount(itEntity.getOrderAmount());
//		compareDataResultEntity.setProductType(itEntity.getProductType());
		compareDataResultEntity.setOrderCode(itEntity.getOrderCode());
		compareDataResultEntity.setOrderStatus(itEntity.getOrderStatus());
		compareDataResultEntity.setOrderType(itEntity.getOrderType());
//		compareDataResultEntity.setDealStatus(PlatformFinanceCompareDataResultEntity.DEALSTATUS_TODEAL);
		return compareDataResultEntity;
	}
	/**
	 * copy属性
	 * @param compareDataResultEntity
	 * @param compareDataEntity
	 */
	private void setProperties(PlatformFinanceCompareDataResultEntity compareDataResultEntity,
			PlatformFinanceCompareDataEntity compareDataEntity) {
//		compareDataResultEntity.setCheckInvestorOid(compareDataEntity.getInvestorOid());
//		compareDataResultEntity.setCheckOrderAmount(compareDataEntity.getTradeAmount());
//		compareDataResultEntity.setCheckOrderCode(compareDataEntity.getOrderCode());
//		compareDataResultEntity.setCheckOrderStatus(compareDataEntity.getOrderStatus());
//		compareDataResultEntity.setCheckOrderType(compareDataEntity.getOrderType());
		
	}
	/**
	 * 对账数据确认
	 * @param oid
	 * @param operator
	 * @return
	 */
	public BaseResp checkDataConfirm(String oid, String operator) {
		BaseResp rep=new BaseResp();
		try {
			platformFinanceCheckNewService.checkDataConfirm(oid,operator);
		}catch(AMPException e){
			e.printStackTrace();
			rep.setErrorCode(-1);
			rep.setErrorMessage("对账数据确认失败!");
		}
		return rep;
	}
	/**
	 * 查询对账批次详情
	 */
	public PlatformFinanceCheckEntity findByCheckCode(String checkCode) {
		return this.platformFinanceCheckDao.findByCheckCode(checkCode);
	}
	
	/**
	 * 对账批次
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public void createCheckOrderBatch() {
		
		if (this.jobLockService.getRunPrivilege(JobEnum.JOB_jobId_createCheckOrderBatch.getJobId())) {
			this.createCheckOrderBatchLog();
		}
		
	}
	
	
	public void createCheckOrderBatchLog() {
		JobLogEntity jobLog =  JobLogFactory.getInstance(JobEnum.JOB_jobId_createCheckOrderBatch.getJobId());
		
		try {
			createCheckOrderBatchDo();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			jobLog.setJobMessage(AMPException.getStacktrace(e));
			jobLog.setJobStatus(JobLogEntity.JOB_jobStatus_failed);
		}
		jobLog.setBatchEndTime(DateUtil.getSqlCurrentDate());
		this.jobLogService.saveEntity(jobLog);
		this.jobLockService.resetJob(JobEnum.JOB_jobId_createCheckOrderBatch.getJobId());
		
	}
	private void createCheckOrderBatchDo() throws ParseException {
		Date curDate = DateUtil.getSqlDate();
		String offsetCode = DateUtil.defaultFormat(curDate);
		PlatformFinanceCheckEntity checkEntity = this.findByCheckCode(PlatformFinanceCheckEntity.PREFIX + offsetCode);
		//开始时间
		String beginTime= DateUtil.getDay24SysBeginTime(curDate);
		//结束时间
		String endTime = DateUtil.getDay24SysEndTime(curDate);
		if (null == checkEntity) {
			try {
				platformFinanceCheckNewService.createEntity(DateUtil.fetchTimestamp(beginTime), DateUtil.fetchTimestamp(endTime),offsetCode);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}
	}
	public PlatformFinanceCheckEntity findByOid(String checkOid) {
		return this.platformFinanceCheckDao.findOne(checkOid);
	}
	
	public int updateLdataStatusPrepareing(String checkOid) {
		int i = this.platformFinanceCheckDao.updateLdataStatusPrepareing(checkOid);
		if (i < 1) {
			throw new AMPException("准备ING");
		}
		return i;
		
	}
	public int updateLdataStatusPrepared(String checkOid) {
		int i = this.platformFinanceCheckDao.updateLdataStatusPrepared(checkOid);
		if (i < 1) {
			throw new AMPException("准备好");
		}
		return i;
	}
	
	public int gaing(String checkOid) {
		int i = this.platformFinanceCheckDao.gaing(checkOid);
		if (i < 1) {
			throw new AMPException("准备ING");
		}
		return i;
		
	}
	public int gaed(String checkOid, int wrongCount) {
		int i = this.platformFinanceCheckDao.gaed(checkOid, wrongCount);
		if (i < 1) {
			throw new AMPException("准备好");
		}
		return i;
		
	}
	public PlatformFinanceCheckEntity saveEntity(PlatformFinanceCheckEntity pfcEntity) {
		return this.platformFinanceCheckDao.save(pfcEntity);
		
	}
	
	
}
