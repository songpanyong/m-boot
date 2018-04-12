package com.guohuai.mmp.investor.bank;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.bank.bankhis.BankHisEntity;
import com.guohuai.mmp.investor.bank.bankhis.BankHisService;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.ope.api.OpeSelectApiService;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRep;
import com.guohuai.mmp.platform.accment.UserBindCardApplyRequest;
import com.guohuai.mmp.platform.accment.UserBindCardConfirmRequest;
import com.guohuai.mmp.platform.accment.UserUnBindCardRequest;
import com.guohuai.mmp.platform.payment.PayParam;
import com.guohuai.mmp.platform.payment.Payment;
import com.guohuai.mmp.sms.SMSUtils;

@Service
@Transactional
public class BankService {
	
	private static final Logger logger = LoggerFactory.getLogger(BankService.class);
	
	@Value("${investor.info.kickstar:true}")
	private boolean kickstar = true;
	
	@Autowired
	private BankDao bankDao;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	@Autowired
	private BankHisService bankHisService;
	@Autowired
	private OpeSelectApiService opeSelectApiService;
	@Autowired
	private Payment paymentServiceImpl;
	@Autowired
	private SMSUtils sMSUtils;
	@Autowired
	private InvestorBaseAccountDao investorBaseAccountDao;
	
	@Autowired
	private EbaoquanRecordDao recordDao;

	public void remove(InvestorBaseAccountEntity account) {
		this.bankDao.delete(this.bankDao.findByInvestorBaseAccount(account));
	}
	
	public List<BankEntity> findAll(Specification<BankEntity> spec){
		List<BankEntity> banks = this.bankDao.findAll(spec);	
		return banks;
	}
	
	public Page<BankEntity> findPage(Specification<BankEntity> spec, Pageable pageable){
		Page<BankEntity> page = this.bankDao.findAll(spec, pageable);	
		return page;
	}
	
	public BankEntity findByInvestorBaseAccount(InvestorBaseAccountEntity account){
		return this.bankDao.findByInvestorBaseAccount(account);
	}
	
	/**
	 * 获取绑卡申请中记录
	 * @param account
	 * @return
	 */
	public BankEntity getApplyingBank(InvestorBaseAccountEntity account) {
		BankEntity bank = this.bankDao.findByInvestorBaseAccountAndBindStatus(account, BankEntity.Bank_BindStatus_no);
		if (null == bank) {
			// 您未进行绑卡申请，无法绑卡！(CODE:80025)
			throw AMPException.getException(80025);
		}
		return bank;
	}
	
	public BankEntity getOKBankByInvestorOid(String investorOid){
		return this.bankDao.getOKBankByInvestorOid(investorOid);
	}
	
	/**
	 * 新增
	 * @param entity
	 * @return
	 */
	public BankEntity saveEntity(BankEntity entity){
		entity.setCreateTime(DateUtil.getSqlCurrentDate());
		return this.updateEntity(entity);
	}
	
	/**
	 * 修改
	 * @param entity
	 * @return
	 */
	private BankEntity updateEntity(BankEntity entity) {
		entity.setUpdateTime(DateUtil.getSqlCurrentDate());
		return this.bankDao.save(entity);
	}
	
	public BankEntity saveEnity(BindBankCardApplyReq req, InvestorBaseAccountEntity account) {
		BankEntity bank = this.findByInvestorBaseAccount(account);        
		if (null == bank) {
			bank = new BankEntity();
		}      
		bank.setInvestorBaseAccount(account);
		bank.setName(req.getRealName());
		bank.setIdCard(req.getCertificateNo());
		bank.setBankName(req.getBankName());
		bank.setDebitCard(req.getCardNo());
		bank.setPhoneNo(req.getPhone());
		bank.setBindStatus(BankEntity.Bank_BindStatus_no);
		
		return this.saveEntity(bank);
	}
	
	public BankEntity saveBindCardEnity(BindBankCardApplyReq req, InvestorBaseAccountEntity account) {
		BankEntity bank = this.findByInvestorBaseAccount(account);        
		if (null == bank) {
			bank = new BankEntity();
		}      
		bank.setInvestorBaseAccount(account);
		bank.setName(req.getRealName());
		bank.setIdCard(req.getCertificateNo());
		bank.setBankName(req.getBankName());
		bank.setDebitCard(req.getCardNo());
		bank.setPhoneNo(req.getPhone());
		bank.setBindStatus(BankEntity.Bank_BindStatus_ok);
		
		return this.saveEntity(bank);
	}
	
	public BankEntity updateEntity(BankEntity entity, String bindStatus) {
		entity.setBindStatus(bindStatus);
		return this.updateEntity(entity);
	}
	
	/**
	 * 身份证/银行卡号唯一判断
	 * @param req
	 * @param investorOid
	 * @return
	 */
	public InvestorBaseAccountEntity isBind(BindBankCardApplyReq req, String investorOid) {
		
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);	
		
		BankEntity bank = this.bankDao.getOKBankByInvestorOid(investorOid);
		
		if (null != bank) {
			if (!StringUtil.isEmpty(bank.getPhoneNo()) && !StringUtil.isEmpty(bank.getDebitCard())) {
				// error.define[80034]=您已绑卡，无法再次绑卡！(CODE:80034)
				throw GHException.getException(80034);
			}
			if (!bank.getName().contains(req.getRealName().replace("*", "")) ||
					!bank.getIdCard().contains(req.getCertificateNo().replace("*", ""))) {
				// error.define[80022]=您提交的姓名或身份证号和之前提交的不一致！(CODE:80022)
				throw GHException.getException(80022);
			}
		} else {
			BankEntity idBank = this.bankDao.findByIdCardAndBindStatus(req.getCertificateNo(), BankEntity.Bank_BindStatus_ok);
			if (null != idBank) {
				// error.define[80023]=该身份证已经绑定过了！(CODE:80023)
				throw GHException.getException(80023);
			}
		}
		
		BankEntity cardBank = this.bankDao.findByDebitCardAndBindStatus(req.getCardNo(), BankEntity.Bank_BindStatus_ok);
		
		if (null != cardBank) {
			// error.define[80024]=此银行卡号已被绑定(CODE:80024)
			throw GHException.getException(80024);
		}		
		return account;
	}
	
	/**
	 * 绑卡申请
	 * @param req
	 * @param investorOid
	 * @return
	 */
	public BaseResp bindApply(BindBankCardApplyReq req, String investorOid) {
		// 是否绑卡
		InvestorBaseAccountEntity baseAccount = this.isBind(req, investorOid);
		
		UserBindCardApplyRequest ireq = new UserBindCardApplyRequest();
		ireq.setMemberId(baseAccount.getMemberId());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setPhone(req.getPhone());
		ireq.setRealName(req.getRealName());
		ireq.setCertificateNo(req.getCertificateNo());
		ireq.setBankName(req.getBankName());
		ireq.setCardNo(req.getCardNo());
		UserBindCardApplyRep rep = this.paymentServiceImpl.bindCardApply(ireq);
		if (0 == rep.getErrorCode()) {
			try {
				this.saveEnity(req, baseAccount);
			} catch (Exception e) {
				Throwable cause = e.getCause();
			    if(cause instanceof ConstraintViolationException) {
			        String errMsg = ((ConstraintViolationException) cause).getSQLException().getMessage();
			        if(!StringUtil.isEmpty(errMsg) && errMsg.indexOf("investorOid") != -1) {
						throw new AMPException("您已经提交了绑卡申请，请勿再次提交！");
			        }
			    }
				logger.error("用户：{}，绑卡失败，原因：{}", baseAccount.getOid(), AMPException.getStacktrace(e));
				throw new AMPException("绑卡失败！");
			}
		}
		return rep;
	}
	
	/**
	 * 绑卡
	 * @param req
	 * @param investorOid
	 * @return
	 */
	@Transactional
	public BaseResp add(BankAddReq bankAddReq, String investorOid) {
		
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);
		// 查看是否进行过绑卡申请
		BankEntity bank = this.getApplyingBank(account);
		
		UserBindCardConfirmRequest req = new UserBindCardConfirmRequest();
		req.setMemberId(account.getMemberId());
		req.setRequestNo(StringUtil.uuid());
		req.setCardOrderId(bankAddReq.getCardOrderId());
		req.setPhone(bank.getPhoneNo());
		req.setSmsCode(bankAddReq.getSmsCode());
		// 同步银行卡到结算系统
		return this.addAccBank(req, account, bank);
	}
	
	/**
	 * 认证支付回来更新绑卡状态
	 * @param req
	 * @param investorOid
	 * @return
	 */
	@Transactional
	public void payAndAdd(String investorOid) {
		
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);
		// 查看是否进行过绑卡申请
		BankEntity bank = this.getApplyingBank(account);
		
		UserBindCardConfirmRequest req = new UserBindCardConfirmRequest();
		req.setMemberId(account.getMemberId());
		req.setRequestNo(StringUtil.uuid());
		req.setPhone(bank.getPhoneNo());
		this.updateEntity(bank, BankEntity.Bank_BindStatus_ok);
	}
	
	/**
	 * 同步银行卡到结算系统
	 * @param req
	 * @param account
	 * @param bank
	 * @return
	 */
	public BaseResp addAccBank(UserBindCardConfirmRequest req, InvestorBaseAccountEntity account, BankEntity bank){
		saveFailCard(account);	// 添加未绑定银行卡
		BaseResp rep = this.paymentServiceImpl.bindCardConfirm(req);
		if (0 == rep.getErrorCode()) {
			
			this.updateEntity(bank, BankEntity.Bank_BindStatus_ok);
			// 更新用户实名认证信息
			this.investorBaseAccountService.updateAccountRealName(account, bank.getName(), bank.getIdCard());
			//baoquan
//			baoquan(account.getOid());
		} else {
			saveFailCardError(account.getOid(), rep.getErrorMessage());	// 保存绑卡失败信息
		}
		return rep;
	}
	
	/**
	 * baoquan
	 * @param uid
	 */
//	@Transactional
//	private void baoquan(String uid) {
//		recordDao.toHtml(uid);
//	}
	// 保存绑卡失败信息
	private void saveFailCardError(String oid, String errorMessage) {
		try{
			opeSelectApiService.failCard(oid, errorMessage);
		}catch(Exception e){
			logger.error("运营查询：保存绑卡失败接口出错：", e.getMessage());
		}
	}
	
	// 生成绑卡未成功表
	public void saveFailCard(InvestorBaseAccountEntity account) {
		try{
			opeSelectApiService.createFailCard(account.getOid(), account.getPhoneNum(), account.getChannelid(), "提交待成功");
		}catch(Exception e){
			logger.error("运营查询：保存绑卡未成功接口出错：", e.getMessage());
		}
	}

	/**
	 * 获取银行卡信息
	 * @param oid 用户Oid
	 * @return
	 */
	public BankInfoRep getBankInfo(String investorOid) {
		BankInfoRep rep = new BankInfoRep();
		
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);
		
		BankEntity bank = this.bankDao.getOKBankByInvestorOid(investorOid);
		
		if (null != bank) {
			rep.setName(this.kickstar ? StringUtil.kickstarOnRealname(bank.getName()) : bank.getName());
			rep.setIdNumb(this.kickstar ? StringUtil.kickstarOnIdNum(bank.getIdCard()) : bank.getIdCard());
			rep.setBankName(bank.getBankName());
			rep.setCardNumb(this.kickstar ? StringUtil.kickstarOnCardNum(bank.getDebitCard()) : bank.getDebitCard());
			rep.setPhoneNo(this.kickstar ? StringUtil.kickstarOnPhoneNum(bank.getPhoneNo()) : bank.getPhoneNo());
			rep.setCreateTime(DateUtil.formatFullPattern(bank.getCreateTime()));
			rep.setIsbind(StringUtil.isEmpty(bank.getDebitCard()) ? false : true);
		} else {
			rep.setIsbind(false);
		}
		
		return rep;
	}
	
	/**
	 * 解绑银行卡
	 * @param bank
	 * @param account
	 * @param operator
	 * @return
	 */
	public void removeBank(BankEntity bank, InvestorBaseAccountEntity account, String operator) {
		BankHisEntity bankHis = new BankHisEntity();
		bankHis.setInvestorOid(account.getOid());
		bankHis.setName(bank.getName());
		bankHis.setIdNumb(bank.getIdCard());
		bankHis.setBankName(bank.getBankName());
		bankHis.setCardNumb(bank.getDebitCard());
		bankHis.setPhoneNo(bank.getPhoneNo());
		bankHis.setOperator(operator);
		this.bankHisService.saveEntity(bankHis);
		
		bank.setBankName(null);
		bank.setDebitCard(null);
		bank.setPhoneNo(null);
		bank = this.updateEntity(bank);
	}
	
	/**
	 * 同步结算系统解绑银行卡
	 * @param investorOid
	 * @param operator
	 * @return
	 */
	public BaseResp syncRemoveSettleBank(String investorOid, String operator) {
		
	
		
		InvestorBaseAccountEntity account = this.investorBaseAccountService.findOne(investorOid);
		
		BankEntity bank = this.bankDao.getOKBankByInvestorOid(investorOid);
		if (null == bank) {
			throw new GHException("用户未绑定银行卡");
		}
		
		UserUnBindCardRequest ireq = new UserUnBindCardRequest();
		ireq.setMemberId(account.getMemberId());
		ireq.setCardNo(bank.getDebitCard());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setSystemSource(PayParam.SystemSource.MIMOSA.toString());
		
		BaseResp rep = this.paymentServiceImpl.unbindCard(ireq);
		if (0 == rep.getErrorCode()) {
			// 业务系统解绑银行卡
			this.removeBank(bank, account, operator);
		}
		return rep;
	}
	
	/**
	 * 删除用户银行卡，不需要轻易使用，仅限测试人员使用
	 * @param account
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public boolean delBankCard(InvestorBaseAccountEntity account) {
		BankEntity bank = this.bankDao.findByInvestorBaseAccount(account);
		if (null != bank) {
			this.bankDao.delete(bank);
			logger.info("管理员删除了用户{}，的银行卡号。", account.getOid());
			return true;
		}
		return false;
	}
	
	/**
	 * 绑定银行卡
	 * @author wq
	 * @param req
	 * @param investorOid
	 * @return
	 */
	public BaseResp bindBankCard(BindBankCardReq req) {
		InvestorBaseAccountEntity accountEntity = investorBaseAccountDao.findByMemberId(req.getUserOid());
		if(accountEntity == null){
			throw new AMPException("缺少用户ID");
		}
		//校验短信验证码
		sMSUtils.checkVeriCode(req.getPhone(),"bindcard", req.getSmsCode());
		// 是否绑卡
		BindBankCardApplyReq bbcareq = new BindBankCardApplyReq();
		bbcareq.setRealName(accountEntity.getRealName());
		bbcareq.setCertificateNo(accountEntity.getIdNum());
		bbcareq.setBankName(req.getBankName());
		bbcareq.setCardNo(req.getCardNo());
		bbcareq.setPhone(req.getPhone());
		InvestorBaseAccountEntity baseAccount = this.isBind(bbcareq, req.getUserOid());
		
		UserBindCardApplyRequest ireq = new UserBindCardApplyRequest();
		ireq.setMemberId(baseAccount.getMemberId());
		ireq.setRequestNo(StringUtil.uuid());
		ireq.setPhone(req.getPhone());
		ireq.setRealName(accountEntity.getRealName());
		ireq.setCertificateNo(accountEntity.getIdNum());
		ireq.setBankName(req.getBankName());
		ireq.setCardNo(req.getCardNo());
		UserBindCardApplyRep rep = this.paymentServiceImpl.bindBankCard(ireq);
		if (0 == rep.getErrorCode()) {
			try {
				this.saveBindCardEnity(bbcareq, baseAccount);
			} catch (Exception e) {
				throw new AMPException("绑卡失败！");
			}
		}
		return rep;
	}
}
