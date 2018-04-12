package com.guohuai.mmp.publisher.baseaccount.loginacc;

import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AllAccRep extends BaseResp {
	List<String> allAcc;
	List<String> selectedAcc;
}
