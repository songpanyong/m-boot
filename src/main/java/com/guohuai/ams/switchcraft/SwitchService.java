package com.guohuai.ams.switchcraft;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.guohuai.ams.switchcraft.SwitchQueryCTRep.SwitchQueryCTRepBuilder;
import com.guohuai.ams.switchcraft.SwitchQueryRep.SwitchQueryRepBuilder;
import com.guohuai.ams.switchcraft.black.SwitchBlackEntity;
import com.guohuai.ams.switchcraft.black.SwitchBlackService;
import com.guohuai.ams.switchcraft.white.SwitchWhiteEntity;
import com.guohuai.ams.switchcraft.white.SwitchWhiteService;
import com.guohuai.basic.component.exception.GHException;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.basic.component.ext.web.PageResp;
import com.guohuai.calendar.TradeCalendarService;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.Clock;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StrRedisUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;

@Service
@Transactional
public class SwitchService {

	@Autowired
	private SwitchDao switchDao;
	@Autowired
	private SwitchWhiteService switchWhiteService;
	@Autowired
	private SwitchBlackService switchBlackService;
	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private TradeCalendarService tradeCalendarService;
	@Autowired
	private InvestorBaseAccountService investorBaseAccountService;
	
	/**
	 * 根据OID获取
	 * @param oid
	 * @return
	 */
	public SwitchEntity getByOid(String oid){
		SwitchEntity en = this.switchDao.findOne(oid);
		if(en==null){
			//系统开关不存在！(CODE:130001)
			throw GHException.getException(130001);
		}
		return en;
	}
	
	
	/**
	 * 列表查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	@Transactional
	public PageResp<SwitchQueryRep> query(Specification<SwitchEntity> spec, Pageable pageable) {		
		Page<SwitchEntity> ens = this.switchDao.findAll(spec, pageable);

		PageResp<SwitchQueryRep> pagesRep = new PageResp<SwitchQueryRep>();

		for (SwitchEntity en : ens) {
			SwitchQueryRep rep = new SwitchQueryRepBuilder()
					.oid(en.getOid())
					.code(en.getCode())
					.name(en.getName())
					.status(en.getStatus())
					.whiteStatus(en.getWhiteStatus())
					.requester(en.getRequester())
					.approver(en.getApprover())
					.approveRemark(en.getApproveRemark())
					.createTime(en.getCreateTime())
					.updateTime(en.getUpdateTime())
					.type(en.getType())
					.content(en.getContent())
					.build();
			pagesRep.getRows().add(rep);
		}
		pagesRep.setTotal(ens.getTotalElements());	
		return pagesRep;
	}
	
	/**
	 * 新增/修改Banner
	 * @param req
	 * @param operator
	 * @return
	 */
	public BaseResp add(SwitchAddReq req, String operator){
		BaseResp rep = new BaseResp();
		Timestamp now = new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis());
		SwitchEntity en;
		if(null != req.getOid() && !"".equals(req.getOid())){
			en = this.getByOid(req.getOid());
			
			checkCode(req.getCode(), en.getOid());
		}else{
			checkCode(req.getCode(), null);
			
			en = new SwitchEntity();
		}			
		en.setCode(req.getCode());
		en.setName(req.getName());
		en.setType(req.getType());
		en.setContent(req.getContent());
		en.setStatus(SwitchEntity.SWITCH_Status_toApprove);
		en.setWhiteStatus(SwitchEntity.SWITCH_WhiteStatus_no);
		en.setRequester(operator);
		en.setCreateTime(now);
		en.setUpdateTime(now);
		this.switchDao.save(en);
		return rep;
	}
	
	/**
	 * 检测code是否存在
	 * @param code
	 * @param userOid 
	 */
	private void checkCode(String code, String oid) {
		SwitchEntity en = this.switchDao.findByCode(code);
		
		if (en != null){
			if (oid != null){
				if (!en.getOid().equals(oid)){
					//系统级开关Code已存在！(CODE:130002)
					throw GHException.getException(130002);
				}
			}else{
				//系统级开关Code已存在！(CODE:130002)
				throw GHException.getException(130002);
			}
		}
	}


	/**
	 * 删除
	 * @param oid
	 * @return
	 */
	public BaseResp del(String oid){
		BaseResp rep = new BaseResp();
		SwitchEntity en = this.getByOid(oid);
		this.switchWhiteService.delBySwitchOid(en.getOid());
		this.switchBlackService.delBySwitchOid(en.getOid());
		this.switchDao.delete(en);
		return rep;
	}
	
	/**
	 * 审批处理	
	 * @param req
	 * @param operaotr
	 * @return
	 */
	public BaseResp dealApprove(SwitchApproveReq req, String operator){
		Timestamp now = new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis());
		BaseResp rep = new BaseResp();
		SwitchEntity en = this.getByOid(req.getOid());
		en.setStatus(req.getApproveStatus());
		en.setApproveRemark(req.getRemark());
		en.setApprover(operator);
		en.setUpdateTime(now);
		this.switchDao.save(en);
		return rep;
	}
	
	/**
	 * 停启用
	 * @param oid
	 * @return
	 */
	public BaseResp enable(String oid, String status){
		BaseResp rep = new BaseResp();
		SwitchEntity en = this.getByOid(oid);
		if (en.getStatus().equals(SwitchEntity.SWITCH_Status_refused) 
				|| en.getStatus().equals(SwitchEntity.SWITCH_Status_toApprove)){
			//状态错误！(CODE:130003)
			throw GHException.getException(130003);
		}
		
		en.setWhiteStatus(SwitchEntity.SWITCH_WhiteStatus_no);
		
		en.setStatus(status);
		en.setUpdateTime(new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
		this.switchDao.save(en);
		
		return rep;
	}

	/**
	 * 添加白名单
	 * @param req
	 */
	public void addWhite(SwitchAddWhiteReq req, String operator) {
		SwitchEntity en = this.getByOid(req.getOid());
		InvestorBaseAccountEntity account = investorBaseAccountService.findByPhone(req.getPhone());
		String userOid = account == null ? "" : account.getOid();
//		if (userOid == null || userOid.isEmpty()){
//			// 会员不存在！(CODE:130000)
//			throw GHException.getException(130000);
//		}
		
		SwitchWhiteEntity white = switchWhiteService.findBySwitchOidAndUserAcc(en.getOid(), req.getPhone());
		if (white == null){
			switchWhiteService.add(en.getOid(), userOid, req.getPhone(), req.getNote(), operator);
		}else{
			// 白名单已存在该用户！(CODE:130005)
			throw GHException.getException(130005);
		}
	}
	
	/**
	 * 添加黑名单
	 * @param req
	 */
	public void addBlack(SwitchAddBlackReq req, String operator) {
		SwitchEntity en = this.getByOid(req.getOid());
		InvestorBaseAccountEntity account = investorBaseAccountService.findByPhone(req.getPhone());
		String userOid = account == null ? "" : account.getOid();
//		if (userOid == null || userOid.isEmpty()){
//			// 会员不存在！(CODE:130000)
//			throw GHException.getException(130000);
//		}
		
		SwitchBlackEntity black = switchBlackService.findBySwitchOidAndUserAcc(en.getOid(), req.getPhone());
		if (black == null){
			switchBlackService.add(en.getOid(), userOid, req.getPhone(), req.getNote(), operator);
		}else{
			// 黑名单已存在该用户！(CODE:130007)
			throw GHException.getException(130007);
		}
	}

	/**
	 * 白名单停启用
	 * @param oid
	 * @param switchStatusDisable
	 */
	public void whiteEnable(String oid, String switchStatusDisable) {
		SwitchEntity en = this.getByOid(oid);
		if (en.getStatus().equals(SwitchEntity.SWITCH_Status_refused) 
				|| en.getStatus().equals(SwitchEntity.SWITCH_Status_toApprove)){
			//状态错误！(CODE:130003)
			throw GHException.getException(130003);
		}
		
		en.setUpdateTime(new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
		en.setWhiteStatus(switchStatusDisable);
		this.switchDao.save(en);
	}

	/**
	 * 后台使用，获取系统配置，可真对某用户(手机号和用户oid选一个填， 若都填，以用户oid为准，可都为null)
	 * 若配置存在并可用则返回对象，若配置不存在或不可用返回null
	 * @param code
	 * @param phone
	 * @param userOid
	 * @return
	 */
	public SwitchEntity findSwitch2BackStage(String code, String phone, String userOid) {
		if (code == null || code.isEmpty()){
			//系统配置code为空！(CODE:130006)
			throw GHException.getException(130006);
		}
		SwitchEntity en = this.switchDao.findByCode(code);
		boolean flag = findCodeIsEnable(en, phone, userOid);
		if (en != null && flag){
			return en;
		}else{
			return null;
		}
	}
	
	/**
	 * 客户端根据code获取开关是否开启过滤白名单
	 * @param code
	 * @param phone	
	 * @param userOid 
	 * @return
	 */
	public SwitchQueryCTRep findCode(String code, String phone, String userOid) {
		if (code == null || code.isEmpty()){
			//系统配置code为空！(CODE:130006)
			throw GHException.getException(130006);
		}
		SwitchQueryCTRep rep = null;
		SwitchEntity en = this.switchDao.findByCode(code);
		boolean flag = findCodeIsEnable(en, phone, userOid);
		
		if (en != null){
			if (flag){
				rep = new SwitchQueryCTRepBuilder().code(code).status(SwitchEntity.SWITCH_Status_enable)
						.type(en.getType()).content(en.getContent()).build();
			}else{
				rep = new SwitchQueryCTRepBuilder().code(code).status(SwitchEntity.SWITCH_Status_disable).build();
			}
		}else{
			if (flag){
				rep = new SwitchQueryCTRepBuilder().code(code).status(SwitchEntity.SWITCH_Status_enable).build();
			}else{
				rep = new SwitchQueryCTRepBuilder().code(code).status(SwitchEntity.SWITCH_Status_disable).build();
			}
		}
		
		return rep;
	}
	
	/**
	 * 判断开关是否开启 或 对某用户开启(手机号和用户oid选一个填， 若都填，以用户oid为准，可都为null)
	 * @param code
	 * @param phone	 手机号  可为null
	 * @param userOid 用户oid   可为null
	 * @return
	 */
	public boolean findCodeIsEnable(SwitchEntity en, String phone, String userOid) {
		boolean flag = false;
		if (en != null && en.getStatus().equals(SwitchEntity.SWITCH_Status_disable)){
			if(en.getWhiteStatus().equals(SwitchEntity.SWITCH_WhiteStatus_white) && userOid != null&& !userOid.isEmpty()){
				SwitchWhiteEntity white = switchWhiteService.findBySwitchOidAndUserOid(en.getOid(), userOid);
				
				flag = white != null ? true : false;
			}else if (en.getWhiteStatus().equals(SwitchEntity.SWITCH_WhiteStatus_white) && phone != null&& !phone.isEmpty()){
				SwitchWhiteEntity white = switchWhiteService.findBySwitchOidAndUserAcc(en.getOid(), phone);
				
				flag = white != null ? true : false;
			}else{
				flag = false;
			}
		}else if(en != null && en.getStatus().equals(SwitchEntity.SWITCH_Status_enable)){
			if(en.getWhiteStatus().equals(SwitchEntity.SWITCH_WhiteStatus_black) && userOid != null&& !userOid.isEmpty()){
				SwitchBlackEntity black = switchBlackService.findBySwitchOidAndUserOid(en.getOid(), userOid);
				
				flag = black != null ? false : true;
			}else if (en.getWhiteStatus().equals(SwitchEntity.SWITCH_WhiteStatus_black) && phone != null&& !phone.isEmpty()){
				SwitchBlackEntity black = switchBlackService.findBySwitchOidAndUserAcc(en.getOid(), phone);
				
				flag = black != null ? false : true;
			}else{
				flag = true;
			}
		}else{
			flag = true;
		}
		
		return flag;
	}
	
	/**
	 * 根据CODE获取
	 * @author yuechao
	 */
	public String findSwitch2BackStage(String code) {
		SwitchEntity en = this.findSwitch2BackStage(code, null, null);
		
		if (null == en || StringUtil.isEmpty(en.getContent())) {
			throw new AMPException(code + ":参数设置为空");
		}
		return en.getContent();
	}
	
	/**
	 * 获取平台单日提现限额
	 */
	public BigDecimal getWithdrawDayLimit() {
		BigDecimal withdrawDayLimit = new BigDecimal(this.findSwitch2BackStage("WithdrawDayLimit"));
		return withdrawDayLimit;
	}

	/**
	 * 获取平台免费提现次数
	 * @author yuechao
	 */
	public int getWithdrawTimes() {
		
		return Integer.parseInt(this.findSwitch2BackStage("WithdrawNum"));
	}

	/**
	 * 获取手续费
	 * @author yuechao
	 */
	public BigDecimal getFee(Timestamp orderTime) {
		java.util.Date nextDate = DateUtil.addDay(orderTime, 1);
		boolean isTrade = this.tradeCalendarService.isTrade(new java.sql.Date(nextDate.getTime()));
		BigDecimal fee = null;
		if (isTrade) {
			fee = new BigDecimal(this.findSwitch2BackStage("TradingDayWithdrawFee"));
		} else {
			fee = new BigDecimal(this.findSwitch2BackStage("NoTradingDayWithdrawFee"));
		}
		return fee;
	}
}
