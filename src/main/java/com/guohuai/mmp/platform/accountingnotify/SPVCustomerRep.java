package com.guohuai.mmp.platform.accountingnotify;


import java.util.List;

import com.guohuai.basic.component.ext.web.BaseResp;
import com.guohuai.mmp.publisher.baseaccount.PublisherDetailRep;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SPVCustomerRep extends BaseResp{
	List<PublisherDetailRep> SPVList;
	LeAccounting  customer;
}
