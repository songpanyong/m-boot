package com.guohuai.mmp.platform.msgment;

public enum MsgParam {
	
	
	/**
		 * 充值成功	rechargesuccess	["金额"]			         【家加财】恭喜您成功充值{1}元！请前往个人中心查看详情。
		   子账户充值成功    sonrechargesuccess ["昵称","金额"]    【家加财】恭喜您的子账户{1}成功充值{2}元！请前往个人中心查看详情。
	投资产品成功	buysuccess	["产品名称"]		恭喜您成功投资{1}理财产品，系统会在1-2个工作日确认，如有问题,请致电客服热线。
	为子账户投资产品成功     sonbuysuccess    ["昵称","产品名称"]      【家加财】恭喜您为子账户{1}成功投资{2}理财产品，系统会在1-2个工作日确认，如有问题请致电客服热线。
	流标	abortive	["产品名称","客服电话"]		【家加财】您投资的{1}理财产品发生流标，如有疑问，请联系客服{2}。
	子账户流标  sonabortive  ["昵称","产品名称","客服电话"]    【家加财】您的子账户{1}投资的{2}理财产品发生流标，如有疑问，请联系客服{3}。
	提现申请	withdrawapply	["时间","提现金额","手续费","预计到账金额"]		【家加财】您于{1}提交提现申请，提现金额{2}元，手续费{3}元，预计到账金额{4}元，我们会在1个工作日之内处理。
	子账户提现申请   sonwithdrawapply   ["昵称","时间","提现金额","手续费","预计到账金额"]       【家加财】您的子账号{1}于{2}提交提现申请，提现金额{3}元，手续费{4}元，预计到账金额{5}元，我们会在1个工作日之内处理。
	提现到账	withdrawsuccess	["时间","实际到账金额"]		【家加财】您于{1}提现金额{2}元（已扣除手续费）已转入您指定的银行账号，具体到账时间请参照各银行规定。
	子账户提现到账     sonwithdrawsuccess   ["昵称","时间","实际到账金额"]     【家加财】您的子账号{1}于{2}提现金额{3}元（已扣除手续费）已转入您指定的银行账号，具体到账时间请参照各银行规定。
	回款	receivedpayments	["产品名称","金额"]		【家加财】您投资的{1}理财产品本次回款{2}元，如需详情请查看资金记录。
	子账户购买产品回款     sonreceivedpayments   ["昵称","产品名称","金额"]       【家加财】您子账号{1}投资的{2}本次回款{3}元，如需详情请查看资金记录。
	充值+绑卡     depositbindcardremind [验证码、时间]    【家加财】 验证码：{1}，有效时间{2}分钟。您正在进行充值操作，请勿将验证码告知他人，如非本人操作，请致电客服热线。
	
	为主账户投资成功  sonbuysuccess     ["昵称","产品名称"]   【家加财】恭喜您为子账户{1}成功投资{2}理财产品，系统会在1-2个工作日确认，如有问题请致电客服热线。
	购买活期产品成功    buyt0success         ["金额","活期名称"]  【家加财】您已成功转入{1}至{2}活期理财产品，系统会在1-2个工作日内确认。如有问题，请致电客服热线。
	为子账户购买活期产品成功    sonbuyt0success  ["子账户昵称","金额","产品名称"]   【家加财】您的子账号{1}成功转入{2}元至{3}活期理财产品，系统会在1-2个工作日内确认。如有问题，请致电客服热线。
	购买定期产品成功   buytnsuccess    ["金额","定期产品名"]   【家加财】您已成功转入{1}至{2}定期理财产品，期限{3}个月，系统会在1-2个工作日内确认。如有问题，请致电客服热线。
	为子账户购买定期产品sonbuytnsuccess ["子账户昵称","金额","产品名称"]   【家加财】您的子账号{1}成功转入{2}元至{3}定期理财产品，期限{4}个月，系统会在1-2个工作日内确认。如有问题，请致电客服热线。
	成功加入计划：一次性购买（助学成长计划和家庭旅游计划）    successjoinplanonetime  ["计划名","金额","期限"]  【家加财】您已成功加入{1}，转入金额{2}元，期限{3}个月。如有问题，请致电客服热线。
	为子账户成功加入计划：一次性       sonsuccessjoinplanonetime   ["子账户昵称","计划名","金额"，"时间"]   【家加财】您的子账号{1}成功加入{2}，转入金额{3}元，期限{4}个月。如有问题，请致电客服热线。
	成功设置定投（助学成长计划和家庭旅游计划）  successjoinplanbymonth  ["计划名","月数"，"日","金额"]     【家加财】您已成功设置{1}按月定投，期限{2}个月，每月{3}日，转入金额{4}元。如有问题，请致电客服热线
	为子账户成功设置定投      sonsuccessjoinplanbymonth     ["昵称","计划名","月数"，"日","金额"]   【家加财】您的子账号{1}成功设置{2}按月定投，期限{3}个月，每月{4}日，转入金额{5}元。如有问题，请致电客服热线。
	薪增长计划 设置成功     setwageincreasesuccess       ["计划名","日"，"金额"]  【家加财】您已成功设置{1}按月定投，每月{2}日，转入金额{3}元。如有问题，请致电客服热线。"
	为子账户薪增长计划 设置成功   sonsetwageincreasesuccess   ["昵称","计划名","日","金额"]  【家加财】您已为子账号{1}成功设置{2}按月定投，每月{3}日，转入金额{4}元。如有问题，请致电客服热线
	定投划扣成功      bymonthbuysuccess     ["计划名","银行名称附加银行卡后四位"，"金额"]      "【家加财】您设置的{1}按月定投本次执行成功，从{2}支付{3}元。如有问题，请致电客服热线。"
	为子账户定投划扣成功  sonbymonthbuysuccess    ["昵称","计划名","银行名称附加卡号后四位"，"日","金额"]  您的子账号{1}设置的{2}按月定投本次执行成功，从{3}支付{4}元。如有问题，请致电客服热线。"
		修改定投计划（助学成长计划和家庭旅游计划   modifyplanbyonemonthsuccess    ["计划名","月数"，"日"]       "【家加财】您已成功修改{1}按月定投，期限{2}个月，每月{3}日，转入金额{4}元。如有问题，请致电客服热线
	子账户修改定投计划       sonmodifyplanbyonemonthsuccess      ["昵称","计划名","月数"，"日"，"金额"]    【家加财】您已为子账号{1}成功修改{2}按月定投，期限{3}个月，每月{4}日，转入金额{5}元。如有问题，请致电客服热线。  
	薪增长计划 修改成功      modifywageincreasesuccess       ["计划名","日"，"金额"]                 【家加财】您已成功修改{1}按月定投，每月{2}日，转入金额{3}元。如有问题，请致电客服热线。
	子账户薪增长计划 修改成功               sonmodifywageincreasesuccess    ["昵称","计划名","月数"，"日"，"金额"]      【家加财】您已为子账号{1}成功修改{2}按月定投，每月{3}日，转入金额{4}元。如有问题，请致电客服热线。
	终止定投 （薪增长计划，助学成长计划和家庭旅游计划）    stopbymonth       ["计划名"]                   【家加财】您已终止{1}按月定投方案。如有问题，请致电客服热线。
	子账户终止定投                 sonstopbymonth              ["昵称","计划名"]                                     【家加财】您已为子账号{1}终止{2}按月定投方案。如有问题，请致电客服热线。
	助学成长计划、家庭旅游计划自动赎回            wishplanreceivedpayments   ["计划名","金额"]           【家加财】您的{1}已到期，实际到期可用金额：{2}元，请查看余额账号。如有问题，请致电客服热线。
	子账户计划自动赎回                                      sonwishplanreceivedpayments   ["昵称","计划名","金额"]        【家加财】您的子账号{1}的{2}已到期，实际到期可用金额：{3}元，请查看余额账号。如有问题，请致电客服热线。
	产品成立进入存续期	计息提醒	interest	["产品名称(计划名称)"]	计息提醒	【家加财】您投资的{1}开始计息！详情请查看个人中心的收益。
	子账户购买的产品（计划）成立，进入 存续期       soninterest    ["产品名称","产品名称(计划名称)"]         【家加财】您子账号{1}投资的{2}开始计息！详情请查看个人中心的收益。
	未成年子账户定投划扣成功         sonundereighteenbymonthbuysuccess   ["昵称","计划名称","余额"]         【家加财】您的子账号{1}设置的{2}按月定投本次执行成功，从子账号余额支付{3}元。如有问题，请致电客服热线。
	未成年子账户第一次定投划扣失败      sonundereighteenfirstbymonthbuyfail   ["昵称","计划名称","余额"]      【家加财】您的子账号{1}设置的{2}按月定投将于今日转入{3}元，请在今日及时补足账户余额，确保转入成功。如有问题，请致电客服热线。
	
	//新增加的8个关于定投划扣失败的短信、站内信模板
	 主账户第一次定投划扣失败（余额不足）   firstbymonthbuyfail   ["计划名","银行名称附加银行卡后四位"，"金额"]     【家加财】您设置的{1}将于今日从{2}转入{3}，目前银行卡余额不足，请在今日及时补足银行卡余额，确保转入成功。如有问题，请致电客服热线。
	子账户第一次定投划扣失败     firstbymonthbuyfail        ["昵称","计划名","银行名称附加卡号后四位"，"金额"]    【家加财】您的子账号{1}设置的{2}按月定投将于今日从{3}转入{4}元，目前银行卡余额不足，请在今日及时补足银行卡余额，确保转入成功。如有问题，请致电客服"
	主账户划扣失败（未开通网银）  deductfailforonlinebanking  ["计划名称"，"银行名称附加银行卡后四位","金额"] 【家加财】您设置的{1}将于今日从{2}转入{3}元，您的银行卡还未开通网银，请及时开通，确保转入成功。如有问题，请致电客服热线。
	子账户划扣失败（未开通网银）     sondeductfailforonlinebanking  ["昵称"，"计划名称"，"银行名称附加银行卡后四位","金额"]    【家加财】您的子账号{1}设置的{2}将于今日从{3}转入{4}元，该银行卡还未开通网银，请及时开通，确保转入成功。如有问题，请致电客服热线。
	主账户划扣失败（系统不支持银行卡） deductfailforsystemnosupportcard  ["计划名称"，"银行名称附加银行卡后四位","金额"]     【家加财】您设置的{1}将于今日从{2}转入{3}元，目前系统暂不支持您的银行卡，请致电客服热线查询相关情况。
	子账户划扣失败（系统不支持银行卡）   sondeductfailforsystemnosupportcard  ["昵称"，"计划名称"，"银行名称附加银行卡后四位","金额"]     【家加财】您的子账号{1}设置的{2}将于今日从{3}转入{4}元，目前系统暂不支持您的银行卡，请致电客服热线查询相关情况。
	主账户划扣失败（渠道不支持银行卡）  deductfailforchannelnosupportcard   ["计划名称"，"银行名称附加银行卡后四位","金额"]        【家加财】您设置的{1}将于今日从{2}转入{3}元，目前支付渠道暂不支持您的银行卡，请致电客服热线查询相关情况。
	子账户划扣失败（渠道不支持银行卡）  sondeductfailforchannelnosupportcard   ["昵称"，"计划名称"，"银行名称附加银行卡后四位","金额"]  【家加财】您的子账号{1}设置的{2}将于今日从{3}转入{4}元，目前支付渠道暂不支持您的银行卡，请致电客服热线查询相关情况。
	主账户划扣失败（其他失败原因） deductfailforotherreason   【家加财】您设置的{1}将于今日从{2}转入{3}元，目前支付失败，请致电客服热线查询相关情况。
	主账户划扣失败（其他失败原因）  sondeductfailforotherreason  【家加财】您的子账号{1}设置的{2}将于今日从{3}转入{4}元，目前支付失败，请致电客服热线查询相关情况。
	
	
	 */
	
	
	
	/**
	 * 	充值成功提醒	rechargesuccess	["金额"]	充值成功提醒	恭喜您成功充值{1}元！
	投资成功提醒	buysuccess	["产品名称"]	投资成功提醒	恭喜您成功投资{1}理财产品，请您耐心等待产品成立。
	流标提醒	abortive	["产品名称","客服电话"]	流标提醒	您投资的{1}理财产品发生流标，如有疑问，请联系客服{2}。
产品成立进入存续期	计息提醒	interest	["产品名称"，"收益位置链接"]	计息提醒	您投资的{1}理财产品开始计息！详情请查看{2}。
	提前还款提醒	prepayment	["产品名称","金额","金额","金额","金额"]	提前还款提醒	您投资的{1}发生提前还款,总额{2}元,其中提前还款本金{3}元,提前还款利息{4}元,提前还款补偿金{5}元，本次投资已回款结束。
	提现申请提醒	withdrawapply	["时间-精确到分钟","金额"]	提现申请提醒	您于{1}申请的{2}元提现已受理,我们会在1个工作日之内处理。
	提现到账提醒	withdrawsuccess	["时间-精确到分钟","金额"]	提现到账提醒	您于{1}申请的{2}元提现已转入您指定的银行帐号,具体到账时间请参照各银行规定。
	回款提醒	receivedpayments	["产品名称","金额"]	回款提醒	您投资的{1}理财产品本次回款{2}元，如需详情请查看资金记录。
	
	站内信和短信模板的内容类似
	
	 */
	
	msgDeductFailForOnlineBanking("deductfailforonlinebanking",10,"","msgDeductFailForOnlineBanking"),
	msgsonDeductFailForOnlineBanking("sondeductfailforonlinebanking",10,"","msgSonDeductFailForOnlineBanking"),
	msgDeductFailForSystemNoSupportCard("deductfailforsystemnosupportcard",10,"","msgDeductFailForSystemNoSupportCard"),
	msgsonDeductFailForSystemNoSupportCard("sondeductfailforsystemnosupportcard",10,"","msgSonDeductFailForSystemNoSupportCard"),
	msgDeductFailForChannelNoSupportCard("deductfailforchannelnosupportcard",10,"","msgDeductFailForChannelNoSupportCard"),
	msgsonDeductFailForChannelNoSupportCard("sondeductfailforchannelnosupportcard",10,"","msgSonDeductFailForChannelNoSupportCard"),
	msgDeductFailForOtherReason("deductfailforotherreason",10,"","msgDeductFailForOtherReason"),
	msgsonDeductFailForOtherReason("sondeductfailforotherreason",10,"","msgSonDeductFailForOtherReason"),
	
	
	
	
	
	
	
	
	
	msgRechargeSuccess("rechargesuccess", 10, "", "msgRechargeSuccess"),
	msgSonRechargeSuccess("sonrechargesuccess",10,"","msgSonRechargeSuccess"),
	msgBuySuccess("buysuccess", 10, "", "msgBuySuccess"),
	msgSonBuySuccess("sonbuysuccess", 10, "", "msgSonBuySuccess"),
	msgAbortive("abortive", 10, "", "msgAbortive"),
	msgSonAbortive("sonabortive", 10, "", "msgSonAbortive"),
	msgWithdrawApply("withdrawapply", 10, "", "msgWithdrawApply"),
	msgSonWithdrawApply("sonwithdrawapply", 10, "", "msgSonWithdrawApply"),	
	msgWithdrawSuccess("withdrawsuccess", 10, "", "msgWithdrawSuccess"),
	msgSonWithdrawSuccess("sonwithdrawsuccess", 10, "", "msgSonWithdrawSuccess"),
	msgReceivedpayments("receivedpayments", 10, "", "msgReceivedpayments"),
	msgSonReceivedpayments("sonreceivedpayments", 10, "", "msgSonReceivedpayments"),
	msgInterest("interest",10,"","msgInterest"),
	msgSonInterest("soninterest",10,"","msgSonInterest"),
	msgIncomedistrinotice("incomedistrinotice", 10, "", "msgIncomedistrinotice"),
	
	msgdepositbindcardremind("depositbindcardremind",10,"","msgDepositBindcardRemind"),
	/**
	 * msgbuyt0success("buyt0success",10,"","msgBuyT0Success"),
	msgsonbuyt0success("sonbuyt0success",10,"","msgSonBuyT0Success"),
	msgbuytnsuccess("buytnsuccess",10,"","msgBuyTnSuccess"),
	msgsonbuytnsuccess("sonbuytnsuccess",10,"","msgSonBuyTnSuccess"),
	*/
	msgsonBuySuccess("sonbuysuccess", 10, "", "msgSonBuySuccess"),
	msgsuccessjoinplanonetime("successjoinplanonetime",10,"","msgSuccessJoinPlanOneTime"),
	msgsonsuccessjoinplanonetime("sonsuccessjoinplanonetime",10,"","msgSonSuccessJoinPlanOneTime"),
	msgsuccessjoinplanbymonth("successjoinplanbymonth",10,"","msgSuccessJoinPlanByMonth"),
	msgsonsuccessjoinplanbymonth("sonsuccessjoinplanbymonth",10,"","msgSonSuccessJoinPlanByMonth"),
	msgsetwageincreasesuccess("setwageincreasesuccess",10,"","msgSetWageIncreaseSuccess"),
	msgsonsetwageincreasesuccess("sonsetwageincreasesuccess",10,"","msgSonSetWageIncreaseSuccess"),
	msgbymonthbuysuccess("bymonthbuysuccess",10,"","msgByMonthBuySuccess"),
	msgsonbymonthbuysuccess("sonbymonthbuysuccess",10,"","msgSonByMonthBuySuccess"),
	msgSonUnderEighteenByMonthBuySuccess("sonundereighteenbymonthbuysuccess",10,"","msgSonUnderEighteenByMonthBuySuccess"),
	msgSonUnderEighteenFirstByMonthBuyFail("sonundereighteenfirstbymonthbuyfail",10,"","msgSonUnderEighteenFirstByMonthBuyFail"),
	msgfirstbymonthbuyfail("firstbymonthbuyfail",10,"","msgFirstByMonthBuyFail"),
	msgsonfirstbymonthbuyfail("sonfirstbymonthbuyfail",10,"","msgSonFirstbyMonthBuyFail"),
	msgmodifyplanbyonemonthsuccess("modifyplanbyonemonthsuccess",10,"","msgModifyPlanByOneMonthSuccess"),
	msgsonmodifyplanbyonemonthsuccess("sonmodifyplanbyonemonthsuccess",10,"","msgSonModifyPlanByOneMonthSuccess"),
	msgmodifywageincreasesuccess("modifywageincreasesuccess",10,"","msgModifyWageIncreaseSuccess"),
	msgsonmodifywageincreasesuccess("sonmodifywageincreasesuccess",10,"","msgSonModifyWageIncreaseSuccess"),
	msgstopbymonth("stopbymonth",10,"","msgStopByMonth"),
	msgsonstopbymonth("sonstopbymonth",10,"","msgSonStopByMonth"),
	msgwishplanreceivedpayments("wishplanreceivedpayments",10,"","msgWishpPlanReceivedPayments"),
	msgsonwishplanreceivedpayments("sonwishplanreceivedpayments",10,"","msgSonWishPlanReceivedPayments"),
	//Added for balance pay
	msgbalancemonthbuysuccess("balancemonthbuysuccess", 10, "", "msgBalanceMonthBuySuccess"),
	msgsonbalancemonthbuysuccess("sonbalancemonthbuysuccess", 10, "", "msgSonBalanceMonthBuySuccess"),
	//Added for month plan receive
	msgmonthplanreceivedpayments("monthplanreceivedpayments", 10, "", "msgMonthPlanReceivedPayments"),
	msgsonmonthplanreceivedpayments("sonmonthplanreceivedpayments", 10, "", "msgSonMonthPlanReceivedPayments"),
	
	
	
	mailRechargeSuccess("rechargesuccess", 10, "", "mailRechargeSuccess"),
	mailSonRechargeSuccess("sonrechargesuccess",10,"","mailSonRechargeSuccess"),
	mailBuySuccess("buysuccess", 10, "", "mailBuySuccess"),
	mailSonBuySuccess("sonbuysuccess", 10, "", "mailSonBuySuccess"),
	mailAbortive("abortive", 10, "", "mailAbortive"),
	mailSonAbortive("sonabortive", 10, "", "mailSonAbortive"),
	mailWithdrawApply("withdrawapply", 10, "", "mailWithdrawApply"),
	mailSonWithdrawApply("sonwithdrawapply", 10, "", "mailSonWithdrawApply"),	
	mailWithdrawSuccess("withdrawsuccess", 10, "", "mailWithdrawSuccess"),
	mailSonWithdrawSuccess("sonwithdrawsuccess", 10, "", "mailSonWithdrawSuccess"),
	mailReceivedPayments("receivedpayments", 10, "", "mailReceivedPayments"),
	mailSonReceivedpayments("sonreceivedpayments", 10, "", "mailSonReceivedpayments"),
	mailInterest("interest",10,"","mailInterest"),
	mailSonInterest("soninterest",10,"","mailSonInterest"),
	mailPrepayment("prepayment", 10, "", "mailPrepayment"),
	
	maildepositbindcardremind("depositbindcardremind",10,"","mailDepositBindcardRemind"),
	//mailbuyt0success("buyt0success",10,"","mailBuyT0Success"),
	//mailsonbuyt0success("sonbuyt0success",10,"","mailSonBuyT0Success"),
	//mailbuytnsuccess("buytnsuccess",10,"","mailBuyTnSuccess"),
	//mailsonbuytnsuccess("sonbuytnsuccess",10,"","mailSonBuyTnSuccess"),
	mailsonBuySuccess("sonbuysuccess", 10, "", "mailSonBuySuccess"),
	mailsuccessjoinplanonetime("successjoinplanonetime",10,"","mailSuccessJoinPlanOneTime"),
	mailsonsuccessjoinplanonetime("sonsuccessjoinplanonetime",10,"","mailSonSuccessJoinPlanOneTime"),
	mailsuccessjoinplanbymonth("successjoinplanbymonth",10,"","mailSuccessJoinPlanByMonth"),
	mailsonsuccessjoinplanbymonth("sonsuccessjoinplanbymonth",10,"","mailSonSuccessJoinPlanByMonth"),
	mailsetwageincreasesuccess("setwageincreasesuccess",10,"","mailSetWageIncreaseSuccess"),
	mailsonsetwageincreasesuccess("sonsetwageincreasesuccess",10,"","mailSonSetWageIncreaseSuccess"),
	mailbymonthbuysuccess("bymonthbuysuccess",10,"","mailByMonthBuySuccess"),
	mailsonbymonthbuysuccess("sonbymonthbuysuccess",10,"","mailSonByMonthBuySuccess"),
	mailfirstbymonthbuyfail("firstbymonthbuyfail",10,"","mailFirstByMonthBuyFail"),
	mailsonfirstbymonthbuyfail("sonfirstbymonthbuyfail",10,"","mailSonFirstbyMonthBuyFail"),
	mailmodifyplanbyonemonthsuccess("modifyplanbyonemonthsuccess",10,"","mailModifyPlanByOneMonthSuccess"),
	mailsonmodifyplanbyonemonthsuccess("sonmodifyplanbyonemonthsuccess",10,"","mailSonModifyPlanByOneMonthSuccess"),
	mailmodifywageincreasesuccess("modifywageincreasesuccess",10,"","mailModifyWageIncreaseSuccess"),
	mailsonmodifywageincreasesuccess("sonmodifywageincreasesuccess",10,"","mailSonModifyWageIncreaseSuccess"),
	mailstopbymonth("stopbymonth",10,"","mailStopByMonth"),
	mailsonstopbymonth("sonstopbymonth",10,"","mailSonStopByMonth"),
	mailwishplanreceivedpayments("wishplanreceivedpayments",10,"","mailWishpPlanReceivedPayments"),
	mailsonwishplanreceivedpayments("sonwishplanreceivedpayments",10,"","mailSonWishPlanReceivedPayments"),
	
	mailDeductFailForOnlineBanking("deductfailforonlinebanking",10,"","mailDeductFailForOnlineBanking"),
	mailsonDeductFailForOnlineBanking("sondeductfailforonlinebanking",10,"","mailSonDeductFailForOnlineBanking"),
	mailDeductFailForSystemNoSupportCard("deductfailforsystemnosupportcard",10,"","mailDeductFailForSystemNoSupportCard"),
	mailsonDeductFailForSystemNoSupportCard("sondeductfailforsystemnosupportcard",10,"","mailSonDeductFailForSystemNoSupportCard"),
	mailDeductFailForChannelNoSupportCard("deductfailforchannelnosupportcard",10,"","mailDeductFailForChannelNoSupportCard"),
	mailsonDeductFailForChannelNoSupportCard("sondeductfailforchannelnosupportcard",10,"","mailSonDeductFailForChannelNoSupportCard"),
	mailDeductFailForOtherReason("deductfailforotherreason",10,"","mailDeductFailForOtherReason"),
	mailsonDeductFailForOtherReason("sondeductfailforotherreason",10,"","mailSonDeductFailForOtherReason"),
	
	pushInterest("interest", 10, "", "pushInterest")
	;
	
	String interfaceName;
	
	int limitSendTimes;
	String ireq;
	String innerFaceName;
	
	
	
	private MsgParam(String interfaceName, int limitSendTimes) {
		this.interfaceName = interfaceName;
		this.limitSendTimes = limitSendTimes;
	}
	
	private MsgParam(String interfaceName, int limitSendTimes, String ireq, String innerFaceName) {
		this.interfaceName = interfaceName;
		this.limitSendTimes = limitSendTimes;
		this.ireq = ireq;
		this.innerFaceName = innerFaceName;
	}
	
	public static int getTimes(String interfaceName) {
		for (MsgParam tmp : MsgParam.values()) {
			if (interfaceName.equals(tmp.getInnerFaceName())) {
				return tmp.limitSendTimes;
			}
		}
		throw new IllegalArgumentException("interfaceName does not exist ");
	}
	
	public static String getIReq(String interfaceName) {
		for (MsgParam tmp : MsgParam.values()) {
			if (tmp.getInterfaceName().equals(interfaceName)) {
				return tmp.ireq;
			}
		}
		throw new IllegalArgumentException("interfaceName does not exist ");
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public String getInnerFaceName() {
		return innerFaceName;
	}
	
	
	@Override
	public String toString() {
		return this.interfaceName;
	}
	
}
