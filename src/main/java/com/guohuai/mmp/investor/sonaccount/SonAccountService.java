package com.guohuai.mmp.investor.sonaccount;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guohuai.basic.component.ext.web.BaseController;
import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.component.exception.AMPException;
import com.guohuai.component.util.DateUtil;
import com.guohuai.component.util.StringUtil;
import com.guohuai.mmp.investor.bank.BankDao;
import com.guohuai.mmp.investor.bank.BankEntity;
import com.guohuai.mmp.investor.bank.BankService;
import com.guohuai.mmp.investor.baseaccount.BaseAccountInfoRep;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountAddReq;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountDao;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountEntity;
import com.guohuai.mmp.investor.baseaccount.InvestorBaseAccountService;
import com.guohuai.mmp.investor.baseaccount.statistics.InvestorStatisticsService;
import com.guohuai.mmp.investor.baseaccount.statistics.MyHomeQueryRep;
import com.guohuai.mmp.jiajiacai.ebaoquan.EbaoquanRecordDao;
import com.guohuai.mmp.jiajiacai.wishplan.risklevel.RiskLevelDao;
import com.guohuai.mmp.jiajiacai.wishplan.risklevel.RiskLevelEntity;
import com.guohuai.verify.VerifyReq;
import com.guohuai.verify.VerifySdk;

@Service
@Transactional
public class SonAccountService extends BaseController {
	@Value("${iPayNow.appId}")
	private String appId;
	@Value("${iPayNow.md5Key}")
	private String md5Key;
	@Value("${iPayNow.des3Key}")
	private String des3Key;
	@Value("${iPayNow.url}")
	private String url;

	@Autowired
	private SonAccountDao sonAccountDao;	
	@Autowired
	InvestorBaseAccountDao investorBaseAccountDao;	
	@Autowired
	InvestorStatisticsService investorStatisticsService;	
	@Autowired
	BankDao bankDao;
	@Autowired
	BankService bankService;
	@Autowired
	InvestorBaseAccountService investorBaseAccountService;	
	@Autowired
	RiskLevelDao riskLevelDao;
	
	@Autowired
	private EbaoquanRecordDao recordDao;

	/*public CheckNicknameRep checkNickname(String pid,String nickname) {
		CheckNicknameRep rep = new CheckNicknameRep();
		SonAccountEntity accountEntity = this.sonAccountDao.findByPidAndNickname(pid,nickname);
		
		if(accountEntity != null){
			rep.setIsexist(true);
		}
		rep.setPid(pid);
		
		return rep;
	}*/

	/*public RegistSonAccountRep registSonAccount(RegistSonAccountReq req) {
		RegistSonAccountRep rep = new RegistSonAccountRep();
		//从数据库表中获取数据，进行姓名和身份证的校验。
		InvestorBaseAccountEntity basicAccount = this.investorBaseAccountDao.findByIdNum(req.getIdCard());;
		//进行校验。
		if(basicAccount!=null){
			throw new AMPException("您的身份证号或姓名已被注册");
		}
		basicAccount = new InvestorBaseAccountEntity();
		//赋值
		basicAccount.setOid(StringUtil.uuid()); //获得唯一标识oid(子账户 的id)
		basicAccount.setMemberId(basicAccount.getOid());
		basicAccount.setIdNum(req.getIdCard());//设置身份证号
		basicAccount.setRealName(req.getRealName());//设置姓名
		this.investorBaseAccountDao.save(basicAccount);
		//为关联表进行赋值。
		SonAccountEntity sonAccountEntity = new SonAccountEntity();
		//sonAccountEntity.getInvestorBaseAccountEntity().setOid(basicAccount.getOid());//设置子账户的id
		sonAccountEntity.setSid(basicAccount.getOid());
		sonAccountEntity.setPid(super.getLoginUser());//设置主账户的id
		sonAccountEntity.setNickname(req.getNackName());//设置昵称。
		sonAccountEntity.setRelation(req.getRelation());//设置关系
		sonAccountEntity.setStatus(0);//设置status为0，则表示为子账户，1为关联账号。
		sonAccountEntity.setOid(StringUtil.uuid(
		));//设置主键
		this.sonAccountDao.save(sonAccountEntity);
	
		rep.setMemberId(basicAccount.getOid());

		return rep ;
		// TODO Auto-generated method stub
		
	}*/

	public void sonBind(SonBindReq req) {
		SonAccountEntity sonAccountEntity = null;
		sonAccountEntity.setOid(StringUtil.uuid());
		sonAccountEntity.setPid(super.getLoginUser());
		sonAccountEntity.setSid(req.getSMemberId());
		sonAccountEntity.setNickname(req.getNickname());
		sonAccountEntity.setRelation(req.getRelation());
		sonAccountEntity.setStatus(1);
		this.sonAccountDao.save(sonAccountEntity);
		
		
		
	}
	/**   获取子账户的昵称、姓名、银行卡、银行卡号和账户余额   
	 * 
	 * @throws ParseException
	 *  */
	public SonInfoRep SonInfo(String sMemberId) {		
		if(super.getLoginUser().equals(sMemberId)){
			SonAccountEntity sonAccount =  this.sonAccountDao.findBySid(sMemberId);		
				return this.getInfoBySonAccount(sonAccount,sMemberId);
		}else{
			SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(super.getLoginUser(),sMemberId);
			if(sonAccount==null){
				throw new AMPException("不是主、子关系，无法进入到该账户");
			}			 
			return this.getInfoBySonAccount(sonAccount,sMemberId);
		}
	}
	
	/**解绑子账户  */
	public void sonUnbind(String sMemberId) {
		MyHomeQueryRep  rep = this.getCapitalAmount(sMemberId);
		if(rep!=null&&rep.getCapitalAmount().equals(BigDecimal.ZERO)){
			this.sonAccountDao.modifyBySid(sMemberId);
		}else{
			throw new AMPException("此账户有在投产品或余额，无法解绑");
		}
		
		
		
	}
	/** 通过昵称来创建子账户  */
	public CheckNicknameRep checkNicknames(AddNickNameReq req) {
		
		CheckNicknameRep rep = new CheckNicknameRep();
		String investorOid = super.getLoginUser();
		if((!investorOid.equals(""))&&investorOid!=null){
			List<SonAccountEntity> list = this.sonAccountDao.findByPidAndStatus(investorOid);
			if(list.size()>10){
				throw new AMPException("您创建的子账户数量已达上限");
			}else{
				SonAccountEntity accountEntity = this.sonAccountDao.findByNicknameAndStatus(req.getNickname());
				if(accountEntity != null){

					throw new AMPException("该昵称已被使用");
				}
				InvestorBaseAccountAddReq ibaareq = new InvestorBaseAccountAddReq();
				ibaareq.setUserAcc(StringUtil.uuid());
				ibaareq.setVericode("000000");
			    ibaareq.setPlatform(req.getPlatform());

				//将子账户的交易密码设置为主账户的交易密码
			    InvestorBaseAccountEntity parentAccount =  this.investorBaseAccountDao.findByOid(investorOid);
			    ibaareq.setPaypwd(parentAccount.getPayPwd());
			    ibaareq.setPaySalt(parentAccount.getPaySalt());
			 
			    
			 
				this.investorBaseAccountService.addBaseAccount(ibaareq, false);
				InvestorBaseAccountEntity baseAccount =  this.investorBaseAccountDao.findByPhoneNum(ibaareq.getUserAcc());//通过手机号来查找基本用户
				if(baseAccount!=null){
					SonAccountEntity sonAccount = new SonAccountEntity();
					sonAccount.setOid(StringUtil.uuid());
					sonAccount.setPid(super.getLoginUser());
					sonAccount.setSid(baseAccount.getOid());
					sonAccount.setNickname(req.getNickname());
					sonAccount.setRelation(req.getRelation());
					sonAccount.setStatus(0);
					this.sonAccountDao.save(sonAccount);
					
					
					//同时设置子账户的风险评测
					RiskLevelEntity riskEntity =   this.riskLevelDao.findByUserOid(investorOid);
					if(riskEntity!=null){
						RiskLevelEntity sonRisk = new RiskLevelEntity();
						sonRisk.setOid(StringUtil.uuid());
						sonRisk.setRiskLevel(riskEntity.getRiskLevel());
						sonRisk.setUserOid(sonAccount.getSid());
						this.riskLevelDao.save(sonRisk);
					}
				}
				rep.setSid(baseAccount.getOid());
				return rep;
			}
		}else{
			throw new AMPException("用户未进行登录或登录超时");
		}
		
	}

	/**
	 * 为账户实名身份证和姓名的信息
	 * 
	 * @throws ParseException
	 */
	public RegistSonAccountRep createSonAccount(RegistSonAccountReq req) {
		
		if(req.getId().equals(super.getLoginUser())){
			//为主账号实名
			return this.addRealNameForMaster(req);
		}else{
			//为子账户实名,req为子账户的信息
			return this.addRealNameForSon(req);
		}

	}
	
	@Autowired
	private VerifySdk verifySdk;
	/**
	 * 二要素接口查询姓名和身份证是否匹配
	 * @param req
	 * @return
	 */
	public Boolean checkCardId(RegistSonAccountReq req) {
		VerifyReq request = new VerifyReq();
		request.setCertNo(req.getIdCard());
		request.setName(req.getRealName());
		return verifySdk.idCardVerify(request);
	}
			
	/**
	 * 为子账户实名方法
	 * 
	 * */
	public RegistSonAccountRep addRealNameForSon(RegistSonAccountReq req){
		
		InvestorBaseAccountEntity sonBaseAccount = this.investorBaseAccountDao.findByOid(req.getId());
		if(sonBaseAccount!=null){
			RegistSonAccountRep rep = new RegistSonAccountRep();
				SonAccountEntity sonAccount = this.sonAccountDao.findByPidAndSid(super.getLoginUser(), req.getId());
				if(sonAccount!=null){
						//查看是否已经实名
						if(sonBaseAccount.getIdNum()!=null&&(!sonBaseAccount.equals(""))){
							throw new AMPException("您已完成实名认证");
						}else{
							
							/**正则校验姓名的格式*/
							if(this.isChineseName(req.getRealName()) == false){
								throw new AMPException("请输入正确格式的姓名");
							}
							/** 正则校验身份证号 */
							if(this.isIdCard(req.getIdCard()) == false){
								throw new AMPException("请输入 正确的身份证号码");
							}
							
							/** 新增判断主子账户的关系逻辑  */
							InvestorBaseAccountEntity parentAccount = this.investorBaseAccountDao.findByOid(sonAccount.getPid());
							this.judgePSRelation(req, parentAccount);
							
						
								InvestorBaseAccountEntity investorBaseAccountEntity =  this.investorBaseAccountDao.findByIdNum(req.getIdCard());
								if(investorBaseAccountEntity!=null){
									throw new AMPException("身份证号已被使用");
								}else{
									//进行操作
									// 判断是否成年
									Boolean isAdult = this.ifGrowup(req.getIdCard());
									if (isAdult) {
										rep.setIsAdult(true);
									} else {
										rep.setIsAdult(false);
									}
									//二要素验证
									if(this.checkCardId(req) == false){
										throw new AMPException("姓名与身份证号码不匹配，请重新输入");
									}
									sonBaseAccount.setIdNum(req.getIdCard());
									sonBaseAccount.setRealName(req.getRealName());
									
									this.investorBaseAccountDao.save(sonBaseAccount);
									
									rep.setMemberId(sonBaseAccount.getMemberId());
									return rep;									
								}
						}
				}else{
					throw new AMPException("当前账户无法为子账号进行操作");
				}
			
		}else{
			throw new AMPException("当前账户不存在");
		}
		
		
		
	}
	
	/**
	 * 为主账号实名的方法
	 * 
	 * */
	public RegistSonAccountRep addRealNameForMaster(RegistSonAccountReq req){
		InvestorBaseAccountEntity investor =  this.investorBaseAccountDao.findByOid(req.getId());
		if(investor!=null){
			if(investor.getIdNum()!=null&&(!investor.getIdNum().equals(""))){
				throw new AMPException("您已经实名过");
			}else{
				RegistSonAccountRep rep = new RegistSonAccountRep();
				InvestorBaseAccountEntity investorBaseAccount = this.investorBaseAccountDao.findByIdNum(req.getIdCard());
				if(investorBaseAccount!=null){
					throw new AMPException("该身份证已经被使用");
				}
				
				// 判断是否成年
				Boolean isAdult = this.ifGrowup(req.getIdCard());
				if (isAdult) {
					rep.setIsAdult(true);
				} else {
					rep.setIsAdult(false);
					throw new AMPException("本平台不支持未满18周岁的用户进行投资");
				}
				//进行二要素验证
				if(this.checkCardId(req) == false){
					throw new AMPException("姓名与身份证不匹配!");
				}
				investor.setRealName(req.getRealName());
				investor.setIdNum(req.getIdCard());
				this.investorBaseAccountDao.save(investor);
				//eBaoquan
				baoquan(investor.getOid());
				return rep;
			}
		}else{
			throw new AMPException("该账户不存在");
		}
	}
	
	/**
	 * baoquan
	 * @param uid
	 */
	@Transactional
	private void baoquan(String uid) {
		recordDao.toHtml(uid);
	}
	/**
	 * 主账户切换到子账户切换时的判断
	 * @throws ParseException 
	 * 
	 * 最新版本
	 * */
	public PAccountChangeToSonRep change(ChangeAccountReq req) {
		PAccountChangeToSonRep rep = new PAccountChangeToSonRep();
		//查询是否有该子用户
		InvestorBaseAccountEntity basicAccount =  this.investorBaseAccountDao.findByOid(req.getUserId());
		if(basicAccount!=null){
				//对是否是实名进行判断
				if(basicAccount.getRealName()!=null&&(basicAccount.getIdNum()!=null||basicAccount.getIdNum()!="")){
					//如果姓名和身份证号不为空，查看是否是未成年。
					if(ifGrowup(basicAccount.getIdNum())){
							//根据身份证号来判断是否成年
						//成年
						//判断是否绑卡
						BankEntity bankEntity =  this.bankDao.findByInvestorBaseAccount(basicAccount);
						if(bankEntity!=null&&bankEntity.getDebitCard()!=null){
							//已绑卡
							//查询关联表中是否是主、子关联
							SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(super.getLoginUser(),req.getUserId());
							if(sonAccount==null){
								rep.setErrorCode(3);
								rep.setErrorMessage("当前账户无法查看子账户的信息");
							}
							basicAccount.setMarkId(super.getLoginUser());
							this.investorBaseAccountDao.save(basicAccount);
							rep.setMarkId(basicAccount.getMarkId());
							rep.setRelation(sonAccount.getRelation());
							
					}else{
							//未绑卡--需要获取子账号的姓名
							SonAccountEntity son =  this.sonAccountDao.findBySid(req.getUserId());
							rep.setRelation(son.getRelation());
							rep.setRealName(basicAccount.getRealName());
							rep.setErrorCode(4);
							rep.setErrorMessage("该账户尚未绑卡，请先绑卡");
							
					}
								
					}else{
						//未成年
						//查询关联表中是否是主、子关联
						SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(super.getLoginUser(),req.getUserId());
						if(sonAccount==null){				
							rep.setErrorCode(3);
							rep.setErrorMessage("当前账户无法查看子账户的信息");
						}
						basicAccount.setMarkId(super.getLoginUser());
						this.investorBaseAccountDao.save(basicAccount);
						rep.setMarkId(basicAccount.getMarkId());
						rep.setRelation(sonAccount.getRelation());
						
						return rep;
							
					}	
				}else{
					
					rep.setErrorCode(2);
					rep.setErrorMessage("请先对该账户进行实名认证");
					SonAccountEntity son = this.sonAccountDao.findBySid(req.getUserId());
					if(son!=null){
						rep.setRelation(son.getRelation());
					}
	
				}
		}else{
			rep.setErrorCode(1);
			rep.setErrorMessage("账户切换失败，该子账户不存在 ");
		}
		
		return rep;
		
	}
	
	
	/**
	 * 主账户切换到子账户切换时的判断
	 * @throws ParseException 
	 * 
	 * */

	/*public ChangeToSonRep change(ChangeAccountReq req) {
		//查询是否有该子用户
		InvestorBaseAccountEntity basicAccount =  this.investorBaseAccountDao.findByOid(req.getUserId());
		if(basicAccount!=null){
				//对是否是实名进行判断
				if(basicAccount.getRealName()!=null&&(basicAccount.getIdNum()!=null||basicAccount.getIdNum()!="")){
					//如果姓名和身份证号不为空，查看是否是未成年。
					if(ifGrowup(basicAccount.getIdNum())){
							//根据身份证号来判断是否成年
						//成年
						//判断是否绑卡
						BankEntity bankEntity =  this.bankDao.findByInvestorBaseAccount(basicAccount);
						if(bankEntity!=null&&bankEntity.getDebitCard()!=null){
							//已绑卡
							//查询关联表中是否是主、子关联
							SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(super.getLoginUser(),req.getUserId());
							if(sonAccount==null){
								throw new AMPException("当前账户无法查看子账户的信息");
							}
							basicAccount.setMarkId(super.getLoginUser());
							this.investorBaseAccountDao.save(basicAccount);
							ChangeToSonRep rep = new ChangeToSonRep();
							rep.setMarkId(basicAccount.getMarkId());
							rep.setNickName(sonAccount.getNickname());
							return rep;
					}else{
							//未绑卡
							throw new AMPException("该账户尚未绑卡，请先绑卡");
					}
								
					}else{
						//未成年
						//查询关联表中是否是主、子关联
						SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(super.getLoginUser(),req.getUserId());
						if(sonAccount==null){
							throw new AMPException("当前账户无法查看子账户的信息");
						}
						basicAccount.setMarkId(super.getLoginUser());
						this.investorBaseAccountDao.save(basicAccount);
						ChangeToSonRep rep = new ChangeToSonRep();
						rep.setMarkId(basicAccount.getMarkId());
						rep.setNickName(sonAccount.getNickname());
						return rep;
							
					}	
				}else{
					//姓名和身份证号为空，则提示请注册。
					throw new AMPException("请先对该账户进行实名认证");
				}
		}else{
			throw new AMPException("账户切换失败，该子账户不存在");
		}
		
	}*/
	/**
	 * 子账户切换到主账户的判断
	 * */
	public ChangeToBasicRep changeTOBasicAccount(String investorOid) {
		ChangeToBasicRep rep = new ChangeToBasicRep();
		//从当前登录的状态中获得主账户的id
		InvestorBaseAccountEntity basicaAccount =  this.investorBaseAccountDao.findByOid(investorOid);
		rep.setPid(basicaAccount.getMarkId());
		basicaAccount.setMarkId(null);
		
		return rep;
		
		
	}
	
	
	/**
	 * 通过身份证判断是否是为成年人
	 * @throws ParseException 
	 * 
	 * */
	public Boolean ifGrowup(String idNum) {
		int year = Integer.parseInt(idNum.substring(6,10));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date update;
		try {
			update = sdf.parse(String.valueOf(year+18)+idNum.substring(10,14));
			Date today = new Date();
			return today.after(update);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

		
	}
	/**   判断主、子账号登录和主账户下是否有子账户   
	 * @throws ParseException */
	public JudgeRep judge(String investorOid)  {
		JudgeRep rep = new JudgeRep();
		InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(investorOid);
		rep.setHasSon(false);
		rep.setIslogin(false);
		if(baseAccount!=null){
				rep.setIslogin(true);
				rep.setInvestorOid(baseAccount.getOid());
				if(baseAccount.getMarkId()!=null){
					//子账户登录
					rep.setIsSon(true);
					SonAccountEntity son = this.sonAccountDao.findBySid(investorOid);
					rep.setNackName(son.getNickname());
					
				}else{
					//主账户登录
					rep.setIsSon(false);
					List<SonAccountEntity> list=  this.sonAccountDao.findByPidAndStatus(investorOid);
						if(list.size()>0){
							rep.setHasSon(true);
						}
					
				}
				
				//判断是否成年
				if((baseAccount.getIdNum()!=null&&baseAccount.getIdNum().equals(""))&&(baseAccount.getRealName()!=null&&!baseAccount.getRealName().equals(""))){
						if(ifGrowup(baseAccount.getIdNum())){
							//成年人
							rep.setIsAdult("yes");
						}else{
							rep.setIsAdult("no");
						}
						
				}else{
					rep.setIsAdult(null);
				}
			
		}
		return rep;
		
	}
	
	/**
	 * 获取主子账号的列表
	 * 
	 * */

	public Map<String,Object> accountLists(String investor) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		if(investor==null||investor.isEmpty()){
			throw new AMPException("您未登录，请登录");
		}
		InvestorBaseAccountEntity basicAccount =  this.investorBaseAccountDao.findByOid(investor);
		if(basicAccount==null){
			throw new AMPException("请登录");
		}else{
			
			if(basicAccount.getMarkId() == null || basicAccount.getMarkId() == ""){
				//主
					BasicBasicAccountRep basic = this.getPAccountMsg(basicAccount);
					basic.setIsLogin(true);		
					
					//总家底
					basic.setSumCapitalAmount(basic.getCapitalAmount());
					basic.setSumTotalIncomeAmount(basic.getTotalIncomeAmount());
					
					map.put("parent", basic);
					//获得子账号的信息
					List<SonAccountEntity> sonList = this.sonAccountDao.findByPidAndStatus(basicAccount.getOid());
					List<SonBasicAccountRep> lists  = new ArrayList<SonBasicAccountRep>();
					if(sonList!=null){
						for(SonAccountEntity s:sonList){
							SonBasicAccountRep son = this.getSonAccountMsg(s);	
					
							//总家底
							basic.setSumCapitalAmount(basic.getSumCapitalAmount().add(son.getCapitalAmount()));
							basic.setSumTotalIncomeAmount(basic.getSumTotalIncomeAmount().add(son.getTotalIncomeAmount()));
							
							lists.add(son);								
						}
						
						if(lists.size()>1){
							Collections.sort(lists,new Comparator<SonBasicAccountRep>(){
								@Override
								public int compare(SonBasicAccountRep o1, SonBasicAccountRep o2) {
									int flag = o1.getCreateTime().compareTo(o2.getCreateTime());
									 return flag;
								}});
						}						
					}				
					map.put("son", lists);
					
				return map;
				
			}else{
				//当前登录的是子账号
					//获取主账号的信息。
					String pid = basicAccount.getMarkId();//主账号的Id
					InvestorBaseAccountEntity investorBaseAccountEntity = this.investorBaseAccountDao.findByOid(pid);
					if(investorBaseAccountEntity!=null){
						BasicBasicAccountRep pLogp = this.getPAccountMsg(investorBaseAccountEntity);
						map.put("parent", pLogp);
					}
				
					//获取子账号的信息。investor是子账号的Id
					SonAccountEntity sonAccount =  this.sonAccountDao.findBySid(investor);
					SonBasicAccountRep sonLog = this.getSonAccountMsg(sonAccount);
					sonLog.setIsLogin(true);					
				List<SonBasicAccountRep> listSon = new ArrayList<SonBasicAccountRep>();
				listSon.add(sonLog);
				map.put("son", listSon);
				
				
				return map;
			}
		}
	}
	
	/** 获取登录账户下的非心愿计划的总资产*/
	public  MyHomeQueryRep getCapitalAmount(String investorOid){
		return this.investorStatisticsService.myHome(investorOid);
	}
	
	/**  通过id获取主账户的信息   */
	public MainAccountInfoRep accountInfo(String investorOid) {
		if(investorOid.equals(super.getLoginUser())){
			BaseAccountInfoRep baseRep =  this.investorBaseAccountService.getAccountInfo(investorOid);
			MainAccountInfoRep rep = new MainAccountInfoRep();
			rep.setUserAcc(StringUtil.kickstarOnPhoneNum(baseRep.getUserAcc()));//手机号
			rep.setName(baseRep.getName());//姓名
			rep.setIdNumb(this.newKickstarOnIdNum(baseRep.getFullIdNumb()));//身份证号
			rep.setBankName(baseRep.getBankName());//银行名称
			rep.setBankCardNum(baseRep.getBankCardNum());//银行卡号
			rep.setBalance(baseRep.getBalance());//余额
			InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(investorOid);
			rep.setApplyAvailableBalance(baseAccount.getApplyAvailableBalance());//可用余额
			
			return rep;
		}else{
			SonAccountEntity sonAccount =  this.sonAccountDao.findByPidAndSid(investorOid, super.getLoginUser());
			if(sonAccount!=null){
				BaseAccountInfoRep baseRep =  this.investorBaseAccountService.getAccountInfo(investorOid);
				MainAccountInfoRep rep = new MainAccountInfoRep();
				rep.setUserAcc(StringUtil.kickstarOnPhoneNum(baseRep.getUserAcc()));//手机号
				rep.setName(baseRep.getName());//姓名
				rep.setIdNumb(this.newKickstarOnIdNum(baseRep.getFullIdNumb()));//身份证号
				rep.setBankName(baseRep.getBankName());//银行名称
				rep.setBankCardNum(baseRep.getBankCardNum());//银行卡号
				rep.setBalance(baseRep.getBalance());//余额
				InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(investorOid);
				rep.setApplyAvailableBalance(baseAccount.getApplyAvailableBalance());//可用余额
				
				return rep;
			}else{
				throw new AMPException("当前账户无法查看主账户的信息");
			}
		}
		
	}
	
	
	
	/**
	 * 身份证
	 * @param idNum
	 * @return
	 */
	public  String newKickstarOnIdNum(String idNum) {
		if (null == idNum || "".equals(idNum)) {
			return idNum;
		}
		idNum = idNum.replaceAll("(\\d{0,6})\\d{8}(\\d{4})", "$1****$2");
		return idNum;
	}

	/** 判断成年未成年以及是否绑卡的方法   
	 * @throws ParseException */
	public SonInfoRep getInfoBySonAccount(SonAccountEntity sonAccount,String sMemberId) {
		//首先要进行判断 子账户的年龄，再进行绑卡操作。
		InvestorBaseAccountEntity basicAccount = this.investorBaseAccountDao.findByOid(sMemberId);
		if(basicAccount!=null){
			SonInfoRep rep = new SonInfoRep();
			rep.setApplyAvailableBalance(basicAccount.getApplyAvailableBalance());//可用余额
			if(basicAccount.getIdNum()!=null){
				//判断是否输入过身份证信息
				if(ifGrowup(basicAccount.getIdNum())){
					//根据身份证号来判断是否成年
					//成年
					
					//判断是否绑卡
					BankEntity bankEntity =  this.bankDao.getOKBankByInvestorOid(basicAccount.getOid());
					if(bankEntity!=null&&bankEntity.getDebitCard()!=null){
						//已绑卡
						
						
						rep.setNickname(sonAccount.getNickname());//昵称
						rep.setRelation(sonAccount.getRelation());//关系
						rep.setRealName(StringUtil.kickstarOnRealname(bankEntity.getName()));//姓名
						rep.setBankName(bankEntity.getBankName());//银行名称
						rep.setBankCardNum(StringUtil.kickstarOnCardNum(bankEntity.getDebitCard()).substring(4,8));//银行卡号
						rep.setIdCardNum(this.newKickstarOnIdNum(bankEntity.getIdCard()));//身份证号
						rep.setIsAdult("yes");
						return rep;
				}else{
						//未绑卡
					
					rep.setNickname(sonAccount.getNickname());//昵称
					rep.setRelation(sonAccount.getRelation());//关系
					rep.setRealName(StringUtil.kickstarOnRealname(basicAccount.getRealName()));//姓名
					rep.setIdCardNum(this.newKickstarOnIdNum(basicAccount.getIdNum()));//身份证号

					rep.setIsAdult("yes");
					return rep;
				}
							
			}else{
				//未成年
				rep.setNickname(sonAccount.getNickname());//昵称
				rep.setRelation(sonAccount.getRelation());//关系
				rep.setRealName(StringUtil.kickstarOnRealname(basicAccount.getRealName()));//名字
				rep.setIdCardNum(this.newKickstarOnIdNum(basicAccount.getIdNum()));//身份证号
				//余额
				MyHomeQueryRep reps = this.investorStatisticsService.myHome(sMemberId);
				rep.setBalance(reps.getBalance());
				rep.setIsAdult("no");
				return rep;
					
				}
			}else{
				//未进行输入身份证信息
				rep.setNickname(sonAccount.getNickname());//昵称
				rep.setRelation(sonAccount.getRelation());//关系
				//余额
				MyHomeQueryRep reps = this.investorStatisticsService.myHome(sMemberId);
				rep.setBalance(reps.getBalance());
				return rep;
			}
		}else{
			throw new AMPException("该子账户不存在");
		}
	}
	
	
	public SonAccountInfoRep sonAccountInfo(String investorOid)  {
		//investorOid是当前登录的用户的id
		SonAccountInfoRep rep = new SonAccountInfoRep();
		InvestorBaseAccountEntity account = this.investorBaseAccountDao.findOne(investorOid);
		rep.setIslogin(true);
		rep.setInvestorOid(account.getOid());
		rep.setUserAcc(account.getPhoneNum());
		rep.setUserPwd(StringUtil.isEmpty(account.getUserPwd()) ? false : true);
		
		rep.setSceneid(account.getUid());
		rep.setStatus(account.getStatus()); // 冻结状态
		rep.setSource(account.getSource()); // 注册来源
		rep.setChannelid(account.getChannelid()); // 渠道来源
		rep.setCreateTime(DateUtil.formatFullPattern(account.getCreateTime())); // 注册时间
		rep.setBalance(account.getBalance());//余额 
		rep.setApplyAvailableBalance(account.getApplyAvailableBalance());//可用余额
		
		if(account.getMarkId()==null){
			rep.setIsMaster("yes");
			//主账户
				//主账户的风险测评等级
				RiskLevelEntity risk = this.riskLevelDao.findByUserOid(investorOid);
				rep.setPaypwd(StringUtil.isEmpty(account.getPayPwd()) ? false : true);
				//风险等级
				if(risk!=null){
					rep.setRiskLevel(risk.getRiskLevel());
				}
				//判断主账户是否实名
				if(account.getRealName()!=null&&account.getIdNum()!=null){
						//判断成年与否
						if(this.ifGrowup(account.getIdNum())){
							//成年人
							rep.setIsAdult("yes");
								//判断是否绑卡
								BankEntity bank = this.bankService.getOKBankByInvestorOid(investorOid);
								if (null != bank&&bank.getBankName()!=null) {
									//已经绑卡
									rep.setName(StringUtil.kickstarOnRealname(bank.getName())); // 姓名
									rep.setFullName(bank.getName()); // 全姓名
									rep.setIdNumb(this.newKickstarOnIdNum(bank.getIdCard())); // 身份证号
									rep.setFullIdNumb(bank.getIdCard()); // 全身份证号
									rep.setBankName(bank.getBankName()); // 银行名称
									rep.setBankCardNum(StringUtil.kickstarOnCardNum(bank.getDebitCard())); //　银行卡号
									rep.setFullBankCardNum(bank.getDebitCard()); // 全银行卡号
									rep.setBankPhone(StringUtil.kickstarOnPhoneNum(bank.getPhoneNo())); // 预留手机号
								}else{
									//未绑过卡或者已经解绑
									rep.setName(StringUtil.kickstarOnRealname(account.getRealName()));//姓名
									rep.setFullName(account.getRealName());//全姓名
									rep.setIdNumb(this.newKickstarOnIdNum(account.getIdNum()));//身份证号
									rep.setFullIdNumb(account.getIdNum());//全身份证号
								}
						}
					
				}else{
					//主账户未实名,默认为null
				}
		}else{
			//子账户
			rep.setIsMaster("no");
			
			//子账户的风险测评等级，默认为主账户的风险测评等级
			SonAccountEntity sonAccountEntity =  this.sonAccountDao.findBySid(investorOid);//通过子账户的id来获取主账户的实体
			/**  显示密码    */
			InvestorBaseAccountEntity investBaseAccount =  this.investorBaseAccountDao.findByOid(investorOid);
			rep.setPaypwd(StringUtil.isEmpty(investBaseAccount.getPayPwd()) ? false : true);
			rep.setNickName(sonAccountEntity.getNickname());
			rep.setRelation(sonAccountEntity.getRelation());//获取当前的主、子账户的关系
			RiskLevelEntity risk = this.riskLevelDao.findByUserOid(investorOid);//获取风险等级测评
			//风险等级
			if(risk!=null){
				rep.setRiskLevel(risk.getRiskLevel());
			}
			
			//判断成年与否
			if(this.ifGrowup(account.getIdNum())){
				//成年人
				rep.setIsAdult("yes");
					//判断是否绑卡
					BankEntity bank = this.bankService.getOKBankByInvestorOid(investorOid);
					if (null != bank&&bank.getBankName()!=null) {
						//已经绑卡
						rep.setName(StringUtil.kickstarOnRealname(bank.getName())); // 姓名
						rep.setFullName(bank.getName()); // 全姓名
						rep.setIdNumb(this.newKickstarOnIdNum(bank.getIdCard())); // 身份证号
						rep.setFullIdNumb(bank.getIdCard()); // 全身份证号
						rep.setBankName(bank.getBankName()); // 银行名称
						rep.setBankCardNum(StringUtil.kickstarOnCardNum(bank.getDebitCard())); //　银行卡号
						rep.setFullBankCardNum(bank.getDebitCard()); // 全银行卡号
						rep.setBankPhone(StringUtil.kickstarOnPhoneNum(bank.getPhoneNo())); // 预留手机号
					}else{
						//未绑过卡或者已经解绑
						rep.setName(StringUtil.kickstarOnRealname(account.getRealName()));//姓名
						rep.setFullName(account.getRealName());//全姓名
						rep.setIdNumb(this.newKickstarOnIdNum(account.getIdNum()));//身份证号
						rep.setFullIdNumb(account.getIdNum());//全身份证号
					}
			}else{
				//未成年
				rep.setIsAdult("no");
				rep.setName(StringUtil.kickstarOnRealname(account.getRealName()));//姓名
				rep.setFullName(account.getRealName());//全姓名
				rep.setIdNumb(this.newKickstarOnIdNum(account.getIdNum()));//身份证号
				rep.setFullIdNumb(account.getIdNum());//全身份证号
				
				
			}
		}
	
		return rep;
	}
	
	/**
	 * 
	 * 在子账户中获取主账户的余额和id
	 * */
	public GetMasterInfoRep getMasterInfo() {
		  String investor = super.getLoginUser();
		  InvestorBaseAccountEntity sonAccount =  this.investorBaseAccountDao.findByOid(investor);
		  if(sonAccount!=null&&sonAccount.getMarkId()!=null){
			  GetMasterInfoRep rep = new GetMasterInfoRep();
			  rep.setPid(sonAccount.getMarkId()); //主账户的id
			  InvestorBaseAccountEntity baseAccount = this.investorBaseAccountDao.findByOid(sonAccount.getMarkId());
			  rep.setApplyAvailableBalance(baseAccount.getApplyAvailableBalance());//主账户的可用余额
			  
			  return rep;
		  }else{
			  throw new AMPException("请切换账户");
		  }
	
	} 	
	/**
	 * 获取主账户的基本信息和资产、收益
	 * 参数：InvestorBaseAccountEntity
	 * 
	 * */
	public BasicBasicAccountRep getPAccountMsg(InvestorBaseAccountEntity basicAccount){
		BasicBasicAccountRep basic = new BasicBasicAccountRep();
		basic.setPhoneNum(StringUtil.kickstarOnPhoneNum(basicAccount.getPhoneNum()));
		basic.setId(basicAccount.getOid());
		basic.setBalance(basicAccount.getBalance());//余额
		MyHomeQueryRep  accountMoney = getCapitalAmount(basicAccount.getOid());
		//账户可用余额
		basic.setApplyAvailableBalance(accountMoney.getApplyAvailableBalance());
		//账户总资产
		basic.setCapitalAmount(accountMoney.getAllCapitalAmount());
		//账户总收益
		basic.setTotalIncomeAmount(accountMoney.getAllTotalIncomeAmount());
		return basic;
	}
	
	/**
	 * 
	 * 获取子账户的基本信息、资产、收益
	 * 参数： SonAccountEntity
	 * */
	public SonBasicAccountRep getSonAccountMsg(SonAccountEntity sonAccount){
		SonBasicAccountRep son = new SonBasicAccountRep();//封装类。封装了两个表中的属性
		son.setId(sonAccount.getSid());//子账户的id
		son.setNickName(sonAccount.getNickname());//子账户的昵称
		son.setRelation(sonAccount.getRelation());//主、子关系
		/** 获取子账户的创建时间  */
		InvestorBaseAccountEntity sonBasicAccount = this.investorBaseAccountService.findOne(sonAccount.getSid());
		son.setCreateTime(sonBasicAccount.getCreateTime());
		//获取子账户的资产信息
		//AccountMoney accountMoney = this.getAmountByOid(sonAccount.getSid());
		MyHomeQueryRep  accountMoney = getCapitalAmount(sonAccount.getSid());
		son.setApplyAvailableBalance(accountMoney.getApplyAvailableBalance());
		son.setCapitalAmount(accountMoney.getAllCapitalAmount());
		son.setTotalIncomeAmount(accountMoney.getAllTotalIncomeAmount());
		
		return son;
	}
	
	/**主子账户列表按照资产进行排序*/
	public Map<String,Object> accountAmountList(String investorOid) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(investorOid==null||investorOid.isEmpty()){
			throw new AMPException("您未登录，请登录");
		}
		InvestorBaseAccountEntity basicAccount =  this.investorBaseAccountDao.findByOid(investorOid);
		if(basicAccount==null){
			throw new AMPException("请登录");
		}else{
			
			if(basicAccount.getMarkId() == null || basicAccount.getMarkId() == ""){
				//主
					BasicBasicAccountRep basic = this.getPAccountMsg(basicAccount);
					basic.setIsLogin(true);		
					
					//总家底
					basic.setSumCapitalAmount(basic.getCapitalAmount());
					basic.setSumTotalIncomeAmount(basic.getTotalIncomeAmount());
					
					map.put("parent", basic);
					//获得子账号的信息
					List<SonAccountEntity> sonList = this.sonAccountDao.findByPidAndStatus(basicAccount.getOid());
					List<SonBasicAccountRep> lists  = new ArrayList<SonBasicAccountRep>();
					if(sonList!=null){
						for(SonAccountEntity s:sonList){
							SonBasicAccountRep son = this.getSonAccountMsg(s);	
					
							//总家底
							basic.setSumCapitalAmount(basic.getSumCapitalAmount().add(son.getCapitalAmount()));
							basic.setSumTotalIncomeAmount(basic.getSumTotalIncomeAmount().add(son.getTotalIncomeAmount()));
							
							lists.add(son);								
						}
						if(lists.size()>1){
							Collections.sort(lists,new Comparator<SonBasicAccountRep>(){
								@Override
								public int compare(SonBasicAccountRep o1, SonBasicAccountRep o2) {
									int flag =o1.getCreateTime().compareTo(o2.getCreateTime());
									 return flag;
								}});
						}						
					}				
					map.put("son", lists);
					
					/** 按照主子的资产金额进行排序  */
					List<AccountAmountSort>  list = getAmountListByInvetorOid(investorOid);
					map.put("ps", list);
					
				return map;
				
			}else{
				//当前登录的是子账号
					//获取主账号的信息。
					String pid = basicAccount.getMarkId();//主账号的Id
					InvestorBaseAccountEntity investorBaseAccountEntity = this.investorBaseAccountDao.findByOid(pid);
					if(investorBaseAccountEntity!=null){
						BasicBasicAccountRep pLogp = this.getPAccountMsg(investorBaseAccountEntity);
						map.put("parent", pLogp);
					}
				
					//获取子账号的信息。investor是子账号的Id
					SonAccountEntity sonAccount =  this.sonAccountDao.findBySid(investorOid);
					SonBasicAccountRep sonLog = this.getSonAccountMsg(sonAccount);
					sonLog.setIsLogin(true);					
				List<SonBasicAccountRep> listSon = new ArrayList<SonBasicAccountRep>();
				listSon.add(sonLog);
				map.put("son", listSon);
				
				
				return map;
			}
		}
	}
	
	
	public List<AccountAmountSort> getAmountListByInvetorOid(String investorOid) {
		
		InvestorBaseAccountEntity basicAccount =  this.investorBaseAccountDao.findByOid(investorOid);
		if(basicAccount==null){
			throw new AMPException("请登录");
		}else{
			List<AccountAmountSort> list = new  ArrayList<AccountAmountSort>();
			
			if(basicAccount.getMarkId() == null || basicAccount.getMarkId() == ""){
				//主
					AccountAmountSort pAccount = new AccountAmountSort();
					BasicBasicAccountRep basic = this.getPAccountMsg(basicAccount);	
					pAccount.setCapitalAmount(basic.getCapitalAmount());
					pAccount.setIsSonAccount(false);
					pAccount.setOid(basic.getId());
					list.add(pAccount);
					
					//获得子账号的信息
					List<SonAccountEntity> sonList = this.sonAccountDao.findByPidAndStatus(basicAccount.getOid());
					if(sonList!=null){
						for(SonAccountEntity s:sonList){
							AccountAmountSort sAccount = new AccountAmountSort();
							SonBasicAccountRep son = this.getSonAccountMsg(s);
							sAccount.setCapitalAmount(son.getCapitalAmount());
							sAccount.setNickname(son.getNickName());	
							sAccount.setOid(son.getId());
							sAccount.setIsSonAccount(true);
							list.add(sAccount);								
						}
						//对所有的子账户按照资产金额进行排序
						if(list.size()>1){
							Collections.sort(list,new Comparator<AccountAmountSort>(){
								@Override
								public int compare(AccountAmountSort o1, AccountAmountSort o2) {
									int flag =o1.getCapitalAmount().compareTo(o2.getCapitalAmount());
									 return -flag;
								}});
						}						
					}							
			}
				return list;
			}
		}
	
	/**
	 * 通过身份证判断账户的性别
	 * 
	 * */
	public String judgeSexByIdNum(String idNum){
		String sex ="";
		if (Integer.parseInt(idNum.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别  
            sex = "女";  
        } else {  
            sex = "男";  
        } 
		return sex;
	}
	
	/**
	 * 判断主账户和子账户年龄差
	 * 
	 * */
	public int judgeAgeByIdNum(String idNum){
		String year = idNum.substring(6).substring(0, 4);// 得到年份  
        String yue = idNum.substring(10).substring(0, 2);// 得到月份  
        String day=idNum.substring(12).substring(0,2);//得到日  
        
        Date date = new Date();// 得到当前的系统时间  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String fyear = format.format(date).substring(0, 4);// 当前年份  
        String fyue = format.format(date).substring(5, 7);// 月份  
        String fday=format.format(date).substring(8,10);  //当前日
        
        int age = 0;  
        if (Integer.parseInt(yue) < Integer.parseInt(fyue)) { // 当前月份大于用户出身的月份表示已过生  
            age = Integer.parseInt(fyear) - Integer.parseInt(year) ;  
        } else if(Integer.parseInt(yue) > Integer.parseInt(fyue)){// 当前用户还没过生  
            age = Integer.parseInt(fyear) - Integer.parseInt(year)-1;  
        }else{
        	if(Integer.parseInt(day) > Integer.parseInt(fday)){
        		age = Integer.parseInt(fyear) - Integer.parseInt(year)-1; 
        	}else{
        		age = Integer.parseInt(fyear) - Integer.parseInt(year); 
        	}
        }
        
        return age;
	}
	
	/**
	 * 判断主子账户的关系方法
	 * 
	 * */
	public BaseResp judgePSRelation(RegistSonAccountReq req, InvestorBaseAccountEntity parentAccount) {
		BaseResp resp = new BaseResp();
		SonAccountEntity sonAccount = this.sonAccountDao.findByPidAndSid(parentAccount.getOid(), req.getId());
		// 判断主账户的性别
		if (this.judgeSexByIdNum(parentAccount.getIdNum()).equals("男")) {

			// 判断 子账户性别
			if (this.judgeSexByIdNum(req.getIdCard()).equals("男")) {
				// 判断年龄关系
				if (sonAccount.getRelation().equals("儿子")) {					
					if (DateUtil.JudgePAndS(parentAccount.getIdNum(),req.getIdCard(),22)) {
						throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是父子关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("父亲")) {					
					if (DateUtil.JudgePAndS(req.getIdCard(), parentAccount.getIdNum(), 22)) {
						throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是父子关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("女儿")) {
					throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是父女关系，请检查后重新输入(CODE:10035)");
				} else if (sonAccount.getRelation().equals("母亲")) {
					throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是母子关系，请检查后重新输入(CODE:10035)");
				}

			} else {
				// 子账户性别为女
				// 判断年龄关系
				if (sonAccount.getRelation().equals("女儿")) {		
					if (DateUtil.JudgePAndS(parentAccount.getIdNum(), req.getIdCard(), 22)) {
						throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是父女关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("母亲")) {					
					if (DateUtil.JudgePAndS(req.getIdCard(), parentAccount.getIdNum(), 20)) {
						throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是母子关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("儿子")) {
					throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是父子关系，请检查后重新输入(CODE:10035)");
				} else if (sonAccount.getRelation().equals("父亲")) {
					throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是父子关系，请检查后重新输入(CODE:10035)");
				}
			}
		} else if (this.judgeSexByIdNum(parentAccount.getIdNum()).equals("女")) {// 主账户为女性
			// 判断 子账户性别
			if (this.judgeSexByIdNum(req.getIdCard()).equals("男")) {
				// 判断年龄关系
				if (sonAccount.getRelation().equals("儿子")) {
					if (DateUtil.JudgePAndS(parentAccount.getIdNum(), req.getIdCard(), 20)) {
						throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是母子关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("父亲")) {
					if (DateUtil.JudgePAndS(req.getIdCard(), parentAccount.getIdNum(), 22)) {
						throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是父女关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("女儿")) {
					throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是母女关系，请检查后重新输入(CODE:10035)");
				} else if (sonAccount.getRelation().equals("母亲")) {
					throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是母女关系，请检查后重新输入(CODE:10035)");
				}
			} else {
				// 子账户性别为女
				// 判断年龄关系
				if (sonAccount.getRelation().equals("女儿")) {
					if (DateUtil.JudgePAndS(parentAccount.getIdNum(), req.getIdCard(), 20)) {
						throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是母女关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("母亲")) {					
					if (DateUtil.JudgePAndS(req.getIdCard(), parentAccount.getIdNum(), 20)) {
						throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是母女关系，请检查后重新输入(CODE:10035)");
					}
				} else if (sonAccount.getRelation().equals("儿子")) {
					throw new AMPException("系统依据子账户的性别或年龄判断您与子账户不是母子关系，请检查后重新输入(CODE:10035)");
				} else if (sonAccount.getRelation().equals("父亲")) {
					throw new AMPException("系统依据子账户的性别或年龄判断子账户与您不是父女关系，请检查后重新输入(CODE:10035)");
				}
			}
		}

		return resp;
	}
	
	/**
	 * 校验姓名是不是中文组成且为2~4个汉字
	 * @param name
	 * @return
	 */
	public  boolean isChineseName(String name) {
        if (!name.matches("[\u4e00-\u9fa5]{2,4}")) {
            return false;
        }
        return true;
    }
	
	/**
	 * 判断字段是否为身份证 符合返回ture
	 * 
	 * @param str
	 * @return boolean
	 */
	public boolean isIdCard(String str) {
		if (StrisNull(str))
			return false;
		if (str.trim().length() == 15 || str.trim().length() == 18) {
			return Regular(str,
					"((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65)[0-9]{4})"
							+ "(([1|2][0-9]{3}[0|1][0-9][0-3][0-9][0-9]{3}"
							+ "[Xx0-9])|([0-9]{2}[0|1][0-9][0-3][0-9][0-9]{3}))");
		} else {
			return false;
		}

	}
	
	/**
	 * 判断字段是否为空 符合返回ture
	 * 
	 * @param str
	 * @return boolean
	 */
	public synchronized boolean StrisNull(String str) {
		return null == str || str.trim().length() <= 0 ? true : false;
	}
	
	/**
	 * 匹配是否符合正则表达式pattern 匹配返回true
	 * 
	 * @param str
	 *            匹配的字符串
	 * @param pattern
	 *            匹配模式
	 * @return boolean
	 */
	private  boolean Regular(String str, String pattern) {
		if (null == str || str.trim().length() <= 0)
			return false;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
	public  String kickstarOnRealname(String realName) {
		if (null == realName) {
			return StringUtil.EMPTY;
		}
		realName = realName.replaceAll("([\u4e00-\u9fa5 a-z A-Z 1-9]{1})[\u4e00-\u9fa5 a-z A-Z 1-9]*", "$1*");
		return realName;
	}
	
	public String kickstarOnPhoneNum(String phoneNum) {
		if (null == phoneNum || 11 != phoneNum.length()) {
			return phoneNum;
		}
		phoneNum = phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		return phoneNum;
	}
	
	public String kickstarOnCardNum(String cardNum) {
		if (null == cardNum || "".equals(cardNum)) {
			return cardNum;
		}
		cardNum = cardNum.replaceAll("\\d{0,15}(\\d{4})", "****$1");
		return cardNum;
	}
	
	/**
	 * 身份证
	 * @param idNum
	 * @return
	 */
	public  String kickstarOnIdNum(String idNum) {
		if (null == idNum || "".equals(idNum)) {
			return idNum;
		}
		idNum = idNum.replaceAll("\\d{0,14}(\\d{4})", "****$1");
		return idNum;
	}
}

