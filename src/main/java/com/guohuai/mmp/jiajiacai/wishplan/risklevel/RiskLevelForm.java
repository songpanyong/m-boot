package com.guohuai.mmp.jiajiacai.wishplan.risklevel;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskLevelForm extends BaseResp{
	private String userOid;
	private String riskLevel;
	private String riskName;

}
