package com.guohuai.mmp.publisher.baseaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.guohuai.ams.illiquidAsset.IlliquidAsset;
import com.guohuai.basic.common.StringUtil;
import com.guohuai.basic.component.collectinfo.CollectInfoSdk;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.basic.component.proactive.ProActiveAware;
import com.guohuai.basic.component.proactive.ProActive.Execution;
import com.guohuai.basic.component.proactive.ProActive.Result;
import com.guohuai.component.exception.AMPException;
import com.guohuai.ext.PublisherAddExt.PublisherAddExt;
import com.guohuai.ext.illiquidAsset.IlliquidAssetAdd;
import com.guohuai.ext.publisherBaseAccount.PublisherQueryDataExt;
import com.guohuai.mmp.investor.sonaccount.SonAccountService;
import com.guohuai.mmp.platform.accment.AccParam;
import com.guohuai.mmp.platform.accment.Accment;
import com.guohuai.mmp.platform.accment.CreateUserReq;
import com.guohuai.mmp.platform.accment.PublisherBalanceRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRequest;
import com.guohuai.mmp.platform.accment.UserBindCardConfirmRequest;
import com.guohuai.mmp.platform.accment.UserUnBindCardRequest;
import com.guohuai.mmp.platform.baseaccount.statistics.PlatformStatisticsService;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccEntity;
import com.guohuai.mmp.publisher.baseaccount.loginacc.PublisherLoginAccService;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsEntity;
import com.guohuai.mmp.publisher.baseaccount.statistics.PublisherStatisticsService;
import com.guohuai.operate.api.AdminSdk;
import com.guohuai.operate.api.objs.admin.AdminObj;
import com.guohuai.basic.component.proactive.ProActive.Execution;
import com.guohuai.basic.component.proactive.ProActive.Result;
import com.guohuai.basic.component.proactive.ProActiveAware;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PublisherBaseAccountService {
	@Autowired
	private PublisherStatisticsService publisherStatisticsService;
	@Autowired
	private PublisherBaseAccountDao publisherBaseAccountDao;
	@Autowired
	private PublisherLoginAccService publisherLoginAccService;
	@Autowired
	private Accment accment;
	@Autowired
	private AdminSdk adminSdk;
	@Autowired
	private PlatformStatisticsService platformStatisticsService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private ProActiveAware proActiveAware;
	@Autowired
	private SonAccountService sonAccountService;
	
	
	public PublisherBaseAccountEntity findByLoginAcc(String uid) {
		PublisherBaseAccountEntity baseAccount = publisherLoginAccService.findByLoginAcc(uid);
		return baseAccount;
	}
	
	public PublisherBaseAccountRep userInfo(String investorOid) {
		
		PublisherBaseAccountEntity baseAccount = this.findByLoginAcc(investorOid);
		
		return getUserInfo(baseAccount);
	}
	
	public PublisherBaseAccountRep suserInfo(String publisherOid) {
		
		PublisherBaseAccountEntity baseAccount = this.findByOid(publisherOid);
		
		return getUserInfo(baseAccount);
	}

	private PublisherBaseAccountRep getUserInfo(PublisherBaseAccountEntity baseAccount) {
		PublisherBaseAccountRep rep = new PublisherBaseAccountRep();
		PublisherStatisticsEntity sta = publisherStatisticsService.findByPublisherBaseAccount(baseAccount);
		rep.setBaseAccountOid(baseAccount.getOid()); // 发行人OID
		rep.setPhone(baseAccount.getPhone());
		rep.setRealName(baseAccount.getRealName());
		rep.setCertificateNo(baseAccount.getCertificateNo());
		rep.setBankName(baseAccount.getBankName());
		rep.setCardNo(baseAccount.getCardNo());
		rep.setBasicBalance(baseAccount.getBasicBalance());
		rep.setCollectionSettlementBalance(baseAccount.getCollectionSettlementBalance());
		rep.setAvailableAmountBalance(baseAccount.getAvailableAmountBalance());
		rep.setFrozenAmountBalance(baseAccount.getFrozenAmountBalance());
		rep.setWithdrawAvailableAmountBalance(baseAccount.getWithdrawAvailableAmountBalance());
		rep.setTotalDepositAmount(sta.getTotalDepositAmount());
		rep.setTotalWithdrawAmount(sta.getTotalWithdrawAmount());
		rep.setTotalReturnAmount(sta.getTotalReturnAmount());
		rep.setTotalLoanAmount(sta.getTotalLoanAmount());
		rep.setTotalInterestAmount(sta.getTotalInterestAmount());
		rep.setOverdueTimes(sta.getOverdueTimes());
		
		rep.setOverdueTimes(sta.getOverdueTimes()); //逾期次数
		rep.setCreateTime(baseAccount.getCreateTime());
		
		return rep;
	}
	
	public List<PublisherBaseAccountEntity> findAll(){
		return this.publisherBaseAccountDao.findAll();
	}

	public void balanceEnough(BigDecimal orderAmount, String baseAccountOid) {
		
		PublisherBaseAccountEntity baseAccount = this.findOne(baseAccountOid);
		if (baseAccount.getWithdrawAvailableAmountBalance().compareTo(orderAmount) < 0) {
			// error.define[30057]=账户余额不足(CODE:30057)
			throw new AMPException(30057);
		}
	
	}
	
	private PublisherBaseAccountEntity saveEntity(PublisherBaseAccountEntity entity) {
		return this.publisherBaseAccountDao.save(entity);
	}

	public PublisherBaseAccountEntity findOne(String oid) {
		PublisherBaseAccountEntity entity = this.publisherBaseAccountDao.findOne(oid);
		if (null == entity) {
			throw new AMPException("发行人不存在");
		}
		return entity;
	}

	/**
	 * 发行人选择列表
	 * @author star.zhu
	 * @return
	 */
	@Transactional
	public List<JSONObject> getAllSPV() {
		List<JSONObject> objList = Lists.newArrayList();
		List<PublisherBaseAccountEntity> list = publisherBaseAccountDao.findByStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_confirmed);
		if (null != list && !list.isEmpty()) {
			JSONObject obj = null;
			for (PublisherBaseAccountEntity entity : list) {
				obj = new JSONObject();
				obj.put("spvId", entity.getOid());
				obj.put("spvName", entity.getRealName());
				objList.add(obj);
			}
		}

		return objList;
	}
	
	public List<PublisherDetailRep> options() {
		List<PublisherBaseAccountEntity> list = publisherBaseAccountDao.findByStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_confirmed);
		List<PublisherDetailRep> r = new ArrayList<PublisherDetailRep>();
		if (null != list && list.size() > 0) {
			for (PublisherBaseAccountEntity entity : list) {
				r.add(new PublisherDetailRep(entity));
			}
		}
		return r;
	}

	

	public List<Object[]> findOneOid(){
		return this.publisherBaseAccountDao.findOneOid();
	}
	
	@Scheduled(cron = "1 1 * * * ?")
	public void getLatestTen() {
		List<PublisherBaseAccountEntity> tmp =  this.publisherBaseAccountDao.getLatestTen();
		CollectInfoSdk.collectInfo(JSONObject.toJSONString(tmp));
	}
	
	public BaseAccountAddRep add(PublisherBaseAccountReq req) {
		// 添加添加标的的扩展业务调用
		if (this.proActiveAware.achieved(PublisherAddExt.class)) {
			 List<Result<BaseAccountAddRep>> resultSet = this.proActiveAware.invoke(new Execution<PublisherAddExt, BaseAccountAddRep>() {
				@Override
				public BaseAccountAddRep execute(PublisherAddExt arg0) {
					return arg0.publisherAddExt(req);
				}
			}, PublisherAddExt.class);
			return resultSet.get(0).getResult();
		} else{
		
				BaseAccountAddRep rep = new BaseAccountAddRep();
				isPhoneExists(req.getPhone());
				
				String[] userOids = req.getAdminInvestorOids();
				if (null != userOids && 0 != userOids.length) {
					for (String userOid : userOids) {
						AdminObj adminObj = adminSdk.getAdmin(userOid);
						if (adminObj == null) {
							throw new AMPException(userOid + "不存在");
						}
						if (adminObj.getErrorCode() != 0) {
							throw new AMPException(adminObj.getErrorMessage() + "(" + adminObj.getErrorCode() + ")");
						}
						boolean isExsits = this.publisherLoginAccService.isExistsLoginAcc(userOid);
						if (isExsits) {
							// error.define[30059]=用户已有所属发行人(CODE:30059)
							throw new AMPException(30059);
						}
					}
				}
				
				PublisherBaseAccountEntity baseAccount = new PublisherBaseAccountEntity();
				baseAccount.setPhone(req.getPhone());
				baseAccount.setStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_created);
				this.saveEntity(baseAccount);
				baseAccount.setMemberId(baseAccount.getOid());
				this.saveEntity(baseAccount);
				
				sendToAcc(baseAccount);
				
				
				PublisherStatisticsEntity st = new PublisherStatisticsEntity();
				st.setPublisherBaseAccount(baseAccount);
				this.publisherStatisticsService.saveEntity(st);
				
				for (String userOid : userOids) {
					PublisherLoginAccEntity loginAcc = new PublisherLoginAccEntity();
					loginAcc.setPublisherBaseAccount(baseAccount);
					loginAcc.setLoginAcc(userOid);
					this.publisherLoginAccService.save(loginAcc);
				}
				
		//		platformStatisticsService.increasePublisherAmount();
				
				rep.setBaseAccountOid(baseAccount.getOid());
				
				return rep;
		}
	}
	
	
	private void sendToAcc(PublisherBaseAccountEntity baseAccount) {
		CreateUserReq ireq = new CreateUserReq();
		ireq.setSystemUid(baseAccount.getOid());
		ireq.setPhone(baseAccount.getPhone());
		ireq.setUserType(AccParam.UserType.SPV.toString());
		ireq.setRemark("创建发行人");
		ireq.setSystemSource(AccParam.SystemSource.MIMOSA.toString());
		this.accment.addUser(ireq);
		
	}

	public void isPhoneExists(String phone) {
		if (null != this.findByPhone(phone)) {
			throw new AMPException("手机号已经存在");
		}
	}

	public PublisherBaseAccountEntity findByPhone(String phone) {
		PublisherBaseAccountEntity baseAccount = this.publisherBaseAccountDao.findByPhone(phone);
		return baseAccount;
	}
	
	public PublisherBaseAccountEntity findByOid(String oid) {
		PublisherBaseAccountEntity baseAccount = this.publisherBaseAccountDao.findOne(oid);
		if (null == baseAccount) {
			throw new AMPException("发行人不存在");
		}
		return baseAccount;
	}
	

	public UserBindCardApplyRep bindCardApply(PublisherBaseAccountBindApplyReq req) {
			// 添加添加标的的扩展业务调用
				if (this.proActiveAware.achieved(PublisherQueryDataExt.class)) {
					 List<Result<UserBindCardApplyRep>> resultSet = this.proActiveAware.invoke(new Execution<PublisherQueryDataExt, UserBindCardApplyRep>() {
						@Override
						public UserBindCardApplyRep execute(PublisherQueryDataExt arg0) {
							return arg0.bindCardApplyExt(req);
						}
					}, PublisherQueryDataExt.class);
					
					return resultSet.get(0).getResult();
				} else {
						PublisherBaseAccountEntity baseAccount = this.findByOid(req.getBaseAccountOid());
						if (!PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_created.equals(baseAccount.getStatus())
								&& !PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_applied.equals(baseAccount.getStatus())) {
							throw new AMPException("账户状态异常");
						}
						UserBindCardApplyRequest ireq = new UserBindCardApplyRequest();
						ireq.setMemberId(baseAccount.getMemberId());
						ireq.setRequestNo(StringUtil.uuid());
						ireq.setPhone(baseAccount.getPhone());
						ireq.setRealName(req.getRealName());
						ireq.setCertificateNo(req.getCertificateNo());
						ireq.setBankName(req.getBankName());
						ireq.setCardNo(req.getCardNo());
						UserBindCardApplyRep rep = this.paymentServiceImpl.bindCardApply(ireq);
						if (0 == rep.getErrorCode()) {
							baseAccount.setRealName(ireq.getRealName());
							baseAccount.setCertificateNo(ireq.getCertificateNo());
							baseAccount.setBankName(ireq.getBankName());
							baseAccount.setCardNo(req.getCardNo());
							baseAccount.setStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_applied);
							this.saveEntity(baseAccount);
						}
						
						return rep;
		           }
	      }

	public BaseResp bindCardConfirm(PublisherBaseAccountBindConfirmReq req) {
		PublisherBaseAccountEntity baseAccount = this.findByOid(req.getBaseAccountOid());
		if (!PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_applied.equals(baseAccount.getStatus())) {
			throw new AMPException("绑卡未申请");
		}
		UserBindCardConfirmRequest ireq = new UserBindCardConfirmRequest();
		ireq.setMemberId(baseAccount.getMemberId());
		ireq.setPhone(baseAccount.getPhone());
		ireq.setSmsCode(req.getSmsCode());
		ireq.setCardOrderId(req.getCardOrderId());
		ireq.setRequestNo(StringUtil.uuid());
		BaseResp rep = paymentServiceImpl.bindCardConfirm(ireq);
		baseAccount.setStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_confirmed);
		this.saveEntity(baseAccount);
		return rep;
	}

	public BaseResp unbindCard(String baseAccountOid) {
		PublisherBaseAccountEntity baseAccount = this.findByOid(baseAccountOid);
		
		
		UserUnBindCardRequest ireq = new UserUnBindCardRequest();
		ireq.setMemberId(baseAccount.getMemberId());
		ireq.setCardNo(baseAccount.getCardNo());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		
		BaseResp rep = this.paymentServiceImpl.unbindCard(ireq);
		baseAccount.setRealName(null);
		baseAccount.setCertificateNo(null);
		baseAccount.setBankName(null);
		baseAccount.setCardNo(null);
		baseAccount.setStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_created);
		this.saveEntity(baseAccount);
		return rep;
	}
	
	
	/**
	 * 更新账户余额
	 */
	public void updateBalance(PublisherBaseAccountEntity baseAccount) {
		PublisherBalanceRep irep = accment.queryPublisherBalance(baseAccount.getMemberId());
		
		log.info("updateBalance={}", JSONObject.toJSONString(irep));
		if (0 == irep.getErrorCode()) {
			log.info("syncUserBalance:investorOid={},memberId={},irep={}", baseAccount.getOid(), 
					baseAccount.getMemberId(), JSONObject.toJSONString(baseAccount));
			this.publisherBaseAccountDao.updateBalance(baseAccount.getOid(), 
					irep.getBasicBalance(), irep.getCollectionSettlementBalance(), 
					irep.getAvailableAmountBalance(), irep.getFrozenAmountBalance(), irep.getWithdrawAvailableAmountBalance());
		}
	}

	public PageResp<PublisherQueryRep> mng(Specification<PublisherBaseAccountEntity> spec, Pageable pageable) {
		Page<PublisherBaseAccountEntity> cas = this.publisherBaseAccountDao.findAll(spec, pageable);
		PageResp<PublisherQueryRep> pagesRep = new PageResp<PublisherQueryRep>();
		if (cas != null && cas.getContent() != null && cas.getTotalElements() > 0) {
			for (PublisherBaseAccountEntity entity : cas) {
				PublisherQueryRep queryRep = new PublisherQueryRep();
				queryRep.setBaseAccountOid(entity.getOid());
				queryRep.setMemberId(entity.getMemberId());
				queryRep.setPhone(entity.getPhone() != null ? this.sonAccountService.kickstarOnPhoneNum(entity.getPhone()) : null);
				queryRep.setRealName(entity.getRealName() != null ? this.sonAccountService.kickstarOnRealname(entity.getRealName()) : null);
				queryRep.setCertificateNo(entity.getCertificateNo() != null ? this.sonAccountService.newKickstarOnIdNum(entity.getCertificateNo()) : null);
				queryRep.setBankName(entity.getBankName());
				queryRep.setCardNo(entity.getCardNo() != null ? this.sonAccountService.kickstarOnCardNum(entity.getCardNo()) : null);
				queryRep.setBasicBalance(entity.getBasicBalance());
				queryRep.setCollectionSettlementBalance(entity.getCollectionSettlementBalance());
				queryRep.setAvailableAmountBalance(entity.getAvailableAmountBalance());
				queryRep.setFrozenAmountBalance(entity.getFrozenAmountBalance());
				queryRep.setWithdrawAvailableAmountBalance(entity.getWithdrawAvailableAmountBalance());
				queryRep.setStatus(entity.getStatus());
				queryRep.setStatusDisp(statusEn2Ch(entity.getStatus()));
				queryRep.setCreateTime(entity.getCreateTime());
				queryRep.setUpdateTime(entity.getUpdateTime());
				pagesRep.getRows().add(queryRep);
			}
		}
		pagesRep.setTotal(cas.getTotalElements());
		return pagesRep;
	}
	
	private String statusEn2Ch(String status) {
		if (PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_created.equals(status)) {
			return "账户已生成";
		}
		if (PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_applied.equals(status)) {
			return "绑卡已申请";
		}
		if (PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_confirmed.equals(status)) {
			return "绑卡已确认";
		}
		return status;
	}

	public long getAllCount(){
		return this.publisherBaseAccountDao.count();
	}
	
	public PublisherBaseAccountEntity findOneNoEx(String oid) {
		return this.publisherBaseAccountDao.findOne(oid);
	}
	
	public UserBindCardApplyRep bindCard(PublisherBaseAccountBindApplyReq req) {
		// 添加添加标的的扩展业务调用
			if (this.proActiveAware.achieved(PublisherQueryDataExt.class)) {
				 List<Result<UserBindCardApplyRep>> resultSet = this.proActiveAware.invoke(new Execution<PublisherQueryDataExt, UserBindCardApplyRep>() {
					@Override
					public UserBindCardApplyRep execute(PublisherQueryDataExt arg0) {
						return arg0.bindCardApplyExt(req);
					}
				}, PublisherQueryDataExt.class);
				
				return resultSet.get(0).getResult();
			} else {
					PublisherBaseAccountEntity baseAccount = this.findByOid(req.getBaseAccountOid());
					if (!PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_created.equals(baseAccount.getStatus())) {
						throw new AMPException("账户状态异常");
					}
					UserBindCardApplyRequest ireq = new UserBindCardApplyRequest();
					ireq.setMemberId(baseAccount.getMemberId());
					ireq.setRequestNo(StringUtil.uuid());
					ireq.setPhone(baseAccount.getPhone());
					ireq.setRealName(req.getRealName());
					ireq.setCertificateNo(req.getCertificateNo());
					ireq.setBankName(req.getBankName());
					ireq.setCardNo(req.getCardNo());
					UserBindCardApplyRep rep = this.paymentServiceImpl.bindBankCard(ireq);
					if (0 == rep.getErrorCode()) {
						baseAccount.setRealName(ireq.getRealName());
						baseAccount.setCertificateNo(ireq.getCertificateNo());
						baseAccount.setBankName(ireq.getBankName());
						baseAccount.setCardNo(req.getCardNo());
						baseAccount.setStatus(PublisherBaseAccountEntity.PUBLISHER_BASE_ACCOUNT_STATUS_confirmed);
						this.saveEntity(baseAccount);
					}
					
					return rep;
	           }
      }
}
