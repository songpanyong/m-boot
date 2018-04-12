package com.guohuai.mmp.publisher.hold;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.file.FileResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** 我的已结清定期产品详情 */
@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MyClosedRegularInfoQueryRep extends BaseResp {

	/** 总期限 */
	private int dayNum;
	/** 投资金额 */
	private BigDecimal investAmt;
	/** 累计收益 */
	private BigDecimal totalIncome;
	/** 计息开始日 */
	private Date sDate;
	/** 计息截止日 */
	private Date eDate;
	
	/** 产品名称 */
	private String productName;
	
	private List<FileResp> files;// 附件
	private List<FileResp> investFiles;// 投资协议书
	private List<FileResp> serviceFiles;// 信息服务协议
	private String incomeCalcBasis;//收益计算基础


}
