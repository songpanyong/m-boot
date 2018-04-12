-- 超级用户
INSERT INTO `T_MONEY_INVESTOR_BASEACCOUNT` (`oid`, `userOid`, `memberId`, `phoneNum`, `realName`, `uid`, `status`, `balance`, `owner`, `updateTime`, `createTime`, `isFreshMan`) VALUES('superaccount','superaccount', NULL, '13641870385','超级买卖单户',NULL,'normal','0','platform','2016-08-31 11:00:01','2016-08-20 14:01:16',NULL);
-- 超级用户统计
insert into `T_MONEY_INVESTOR_STATISTICS` (`oid`, `investorOid`, `totalDepositAmount`, `totalWithdrawAmount`, `totalInvestAmount`, `totalRedeemAmount`, `totalIncomeAmount`, `totalRepayLoan`, `t0YesterdayIncome`, `tnTotalIncome`, `t0TotalIncome`, `t0CapitalAmount`, `tnCapitalAmount`, `totalInvestProducts`, `totalDepositCount`, `totalWithdrawCount`, `totalInvestCount`, `totalRedeemCount`, `todayDepositCount`, `todayWithdrawCount`, `todayInvestCount`, `todayRedeemCount`, `firstInvestTime`, `incomeConfirmDate`, `updateTime`, `createTime`, `todayDepositAmount`, `todayWithdrawAmount`, `todayInvestAmount`, `todayRedeemAmount`) values('superaccount','superaccount','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0.0000','0','0','0','0','0','0','0','0','0',NULL,'2016-08-31','2016-09-01 10:46:58','2016-08-20 14:04:57','0.0000','0.0000','0.0000','0.0000');

-- 平冶用户
insert into `T_MONEY_PLATFORM_BASEACCOUNT` (`oid`, `platformUid`, `balance`, `status`, `updateTime`, `createTime`, `superAccBorrowAmount`) values('platformoid', NULL,'0','normal','2016-09-01 17:20:48','2016-08-15 18:30:29','0');
-- 平台用户 统计
insert into `T_MONEY_PLATFORM_STATISTICS` (`oid`, `platformOid`, `totalTradeAmount`, `totalLoanAmount`, `totalReturnAmount`, `totalInterestAmount`, `investorTotalDepositAmount`, `investorTotalWithdrawAmount`, `publisherTotalDepositAmount`, `publisherTotalWithdrawAmount`, `registerAmount`, `investorAmount`, `investorHoldAmount`, `overdueTimes`, `productAmount`, `closedProductAmount`, `toCloseProductAmount`, `onSaleProductAmount`, `publisherAmount`, `verifiedInvestorAmount`, `activeInvestorAmount`, `updateTime`, `createTime`) values('platformsta','platformoid','0','0.0000','0.0000','0.0000','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','2016-09-04 12:51:00','2016-08-19 09:41:25');


-- 备付金
insert into `T_MONEY_PLATFORM_RESERVEDACCOUNT` (`oid`, `platformOid`, `reservedId`, `balance`, `totalDepositAmount`, `totalWithdrawAmount`,`lastBorrowTime`, `lastReturnTime`, `updateTime`, `createTime`, `basicAccBorrowAmount`, `superAccBorrowAmount`, `operationId`, `operationAccBorrowAmount`) values('reservedoid','platformoid', NULL,'8000000','8000000.0000','0.0000','2016-09-01 17:20:48','2016-08-24 11:10:53','2016-09-01 17:20:48','2016-08-19 09:41:38','0','0','operationId', '0');

