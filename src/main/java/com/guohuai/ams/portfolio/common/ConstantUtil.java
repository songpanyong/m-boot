package com.guohuai.ams.portfolio.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ConstantUtil {

	/**
	 * 初始化数值
	 */
	public static final BigDecimal init0 	= new BigDecimal("0");
	public static final BigDecimal NUM1 	= new BigDecimal("1");
	public static final BigDecimal NUM1_1 	= new BigDecimal("1.1");
	public static final BigDecimal NUM100 	= new BigDecimal("100");
	public static final BigDecimal NUM10000 = new BigDecimal("10000");
	public static final BigDecimal NUM365 	= new BigDecimal("365");
	
	/**
	 * 申赎类型
	 */
	public static final String PURCHASE 	= "purchase";	// 申购
	public static final String REDEEM 		= "redeem";		// 赎回
	
	/**
	 * 资产池操作
	 */
	public static final String IN 					= "划入";
	public static final String INVESTMENT 			= "投资";
	public static final String OUT 					= "划出";
	public static final String EDIT_SELF_USASSET 	= "编辑自有非标资产";
	public static final String EDIT_USASSET 		= "编辑非标资产";
	public static final String DELETE 				= "删除";
	public static final String DELETE_USASSET 		= "删除非标资产";
	public static final String REFUNDMENT 			= "退款";
	public static final String EXPIRED 				= "已到期";
	public static final String ADD_BANK 			= "新增银行资产";
	public static final String EDIT_BANK 			= "调整银行资产";
	
	/**
	 * 申赎
	 */
	public static final String SG 					= "PURCHASE";
	public static final String SH 					= "REDEEM";
	
	/**
	 * 交易资产的状态
	 */
	public static final String trade_create 		= "0";	// 申请待审核
	public static final String trade_audit 			= "1";	// 审核通过待确认
	public static final String trade_audit_reject 	= "-1";	// 审核未通过
	public static final String trade_confirm 		= "2";	// 确认成功
	public static final String trade_confirm_reject = "-2";	// 确认失败
	
	/**
	 * 持仓标的状态
	 */
	public static final String hold_hold 		= "HOLD";		// 持仓
	public static final String hold_fire 		= "FIRE";		// 清仓
	public static final String hold_part_fire 	= "PART_FIRE";	// 部分清仓
	
	/**
	 * 标的资产类型
	 */
	public static final String classify_liquid	= "LIQUID";			// 现金类资产
	public static final String classify_illiquid	= "ILLIQUID";	// 非现金类资产
	
	/**
	 * 交易资产的类型
	 */
	public static final String trade_purchase 		= "PURCHASE";		// 申购
	public static final String trade_redeem 		= "REDEEM";			// 赎回
	public static final String trade_transIn 		= "TRANSIN";		// 转入
	public static final String trade_transOut 		= "TRANSOUT";		// 转出
	public static final String trade_subscribe 		= "SUBSCRIBE";		// 认购
	public static final String trade_calibration 	= "CALIBRATION"; 	// 净值校准
	
	/**
	 * 资产来源
	 */
	public static final String PT 					= "PLATFORM";
	public static final String ZY 					= "SELF";
	
	/**
	 * 付息操作
	 */
	public static final String FULL 	= "一次性还本付息";
	// 一次性
	public static final String OTP 		= "OTP";
	// 周期
	public static final String CYCLE 	= "CYCLE";
	
	public static final Map<String, String> PAY_MAP = new HashMap<String, String>();
	static {
		PAY_MAP.put("按周期付息", "CYCLE");
		PAY_MAP.put("按月付息", "M");
		PAY_MAP.put("按季付息", "Q");
		PAY_MAP.put("按半年付息", "H_Y");
		PAY_MAP.put("按年付息", "Y");
		PAY_MAP.put("到期一次性", "OTP");
		PAY_MAP.put("NATURAL_YEAR", "NATURAL");
		PAY_MAP.put("CONTRACT_YEAR", "CONTRACT");
	}
	
	/**
	 * 审核状态
	 */
	public static final String audit_init 		= "INIT";	// 待审核
	public static final String audit_pass 		= "PASS";	// 审核通过
	public static final String audit_reject 	= "REJECT";	// 审核未通过
	
	/**
	 * 投资组合状态
	 */
	public static final String state_create 	= "CREATE";		// 新建待审核
	public static final String state_duration 	= "DURATION";	// 存续期
	public static final String state_reject 	= "REJECT";		// 审核未通过
	public static final String state_invalid 	= "INVALID";	// 作废

	/**
	 * 资产生命周期状态
	 */
	public static final String lifeState_init 		= "INIT";		// 初始化
	public static final String lifeState_collect 	= "COLLECT";	// 募集期
	public static final String lifeState_fail 		= "FAIL";		// 成立失败
	public static final String lifeState_normal 	= "NORMAL";		// 正常还款/存续期
	public static final String lifeState_overTime 	= "OVERTIME";	// 逾期
	public static final String lifeState_overdue 	= "OVERDUE";	// 逾期还款
	public static final String lifeState_close 		= "CLOSE";		// 坏账核销
	public static final String lifeState_end 		= "END";		// 结束
	
	/**
	 * 系统估值
	 */
	public static final String calc_system = "SYSTEM";	// 系统
	
	/**
	 * 每日定时任务状态(未计算，已计算，部分计算，当日不计算)
	 */
	public static final String schedule_init 	= "INIT";	// 未执行
	public static final String schedule_wjs 	= "WJS";	// 未计算
	public static final String schedule_yjs 	= "YJS";	// 已计算
	public static final String schedule_bfjs 	= "BFJS";	// 部分计算
	public static final String schedule_drbjs 	= "DRBJS";	// 当日不计
	
	/**
	 * 当日收益分配状态(未分配，已分配)
	 */
	public static final String income_init 	= "INIT";	// 未执行
	public static final String income_wfp 	= "WFP";	// 未分配
	public static final String income_yfp 	= "YFP";	// 已分配
	
	/**
	 * 费金提取类型
	 */
	public static final String fee_count	= "COUNTFEE";	// 费金累计
	public static final String fee_draw		= "DRAWFEE";	// 费金计提
	public static final String fee_trustee 	= "TRUSTEEFEE";	// 托管费
	public static final String fee_manage	= "MANAGEFEE";	// 管理费
	public static final String fee_both		= "BOTH";		// 托管费和管理费
	
	/**
	 * 投资组合校准类型
	 */
	public static final String adjust_cash		= "CASH";		// 现金校准
	public static final String adjust_deviation	= "DEVIATION"; 	// 损益校准
}
