SELECT CONCAT(
	CONCAT("HMSET m:i:p:", investorOid, ":", productOid),
	' oid ', oid,
	IFNULL(CONCAT(' productOid ', productOid), ''),
	IFNULL(CONCAT(' investorOid ', investorOid), ''),
	IFNULL(CONCAT(' totalVolume ', TRUNCATE(totalVolume*10000, 0)), ''),
	IFNULL(CONCAT(' holdVolume ',  TRUNCATE(holdVolume*10000, 0)), ''),
	IFNULL(CONCAT(' toConfirmInvestVolume ',  TRUNCATE(toConfirmInvestVolume*10000, 0)), ''),
	IFNULL(CONCAT(' toConfirmRedeemVolume ',  TRUNCATE(toConfirmRedeemVolume*10000, 0)), ''),
	IFNULL(CONCAT(' totalInvestVolume ',  TRUNCATE(totalInvestVolume*10000, 0)), ''),
	IFNULL(CONCAT(' totalVoucherAmount ',  TRUNCATE(totalVoucherAmount*10000, 0)), ''),
	IFNULL(CONCAT(' lockRedeemHoldVolume ',  TRUNCATE(lockRedeemHoldVolume*10000, 0)), ''),
	IFNULL(CONCAT(' redeemableHoldVolume ',  TRUNCATE(redeemableHoldVolume*10000, 0)), ''),
	IFNULL(CONCAT(' accruableHoldVolume ',  TRUNCATE(accruableHoldVolume*10000, 0)), ''),
	IFNULL(CONCAT(' value ',  TRUNCATE(VALUE*10000, 0)), ''),
	IFNULL(CONCAT(' expGoldVolume ',  TRUNCATE(expGoldVolume * 10000, 0)), ''),
	IFNULL(CONCAT(' holdTotalIncome ',  TRUNCATE(holdTotalIncome * 10000, 0)), ''),
	IFNULL(CONCAT(' holdYesterdayIncome ',  TRUNCATE(holdYesterdayIncome * 10000, 0)), ''),
	IFNULL(CONCAT(' confirmDate ',  '"', confirmDate, '"'), ''),
	IFNULL(CONCAT(' expectIncomeExt ',  TRUNCATE(expectIncomeExt * 10000, 0)), ''),
	IFNULL(CONCAT(' expectIncome ',  TRUNCATE(expectIncome * 10000, 0)), ''),
	IFNULL(CONCAT(' dayRedeemVolume ',  TRUNCATE(dayRedeemVolume*10000, 0)), ''),
	IFNULL(CONCAT(' dayInvestVolume ',  TRUNCATE(dayInvestVolume*10000, 0)), ''),
	IFNULL(CONCAT(' dayRedeemCount ',  dayRedeemCount), ''),
	IFNULL(CONCAT(' maxHoldVolume ',  TRUNCATE(maxHoldVolume*10000, 0)), ''),
	IFNULL(CONCAT(' holdStatus ', '"', holdStatus, '"'), ''),
	IFNULL(CONCAT(' latestOrderTime ',  '"', latestOrderTime, '"'), ''),
	'\n',
	CONCAT('HMSET m:i:ap:', investorOid, ':', assetpoolOid, 
	' ', productOid, ' ', truncate(maxHoldVolume*10000, 0)),
	'\n',
	CONCAT('zadd m:h:i:', investorOid, ' ', UNIX_TIMESTAMP(createTime), ' ', productOid)
)
FROM t_money_publisher_hold 
WHERE accountType='INVESTOR' and investorOid in ('#oids')
union
SELECT concat(CONCAT("HMSET m:i:a:", t1.oid),
IFNULL(CONCAT(' investorOid ', t1.oid), ''),
IFNULL(CONCAT(' isFreshman ', t1.isFreshman), ''),
IFNULL(CONCAT(' status ', t1.status), ''),
IFNULL(CONCAT(' balance ', TRUNCATE(t1.balance * 10000, 0)), ''),
IFNULL(CONCAT(' withdrawFrozenBalance ', TRUNCATE(t1.withdrawFrozenBalance * 10000, 0)), ''),
IFNULL(CONCAT(' rechargeFrozenBalance ', TRUNCATE(t1.rechargeFrozenBalance * 10000, 0)), ''),
IFNULL(CONCAT(' applyAvailableBalance ', TRUNCATE(t1.applyAvailableBalance * 10000, 0)), ''),
IFNULL(CONCAT(' withdrawAvailableBalance ', TRUNCATE(t1.withdrawAvailableBalance * 10000, 0)), ''))
FROM t_money_investor_baseaccount t1 where t1.oid in ('#oids')
union
SELECT concat(concat("HMSET m:i:a:", investorOid),
ifnull(concat(' monthWithdrawCount ', monthWithdrawCount), ''))
FROM t_money_investor_statistics
WHERE 
investorOid in ('#oids')  or '#xoids'='x'
union
SELECT CONCAT(
CONCAT("HMSET m:p:", oid), 
IFNULL(CONCAT(' productOid ', '"', oid,'"'), ''),
IFNULL(CONCAT(' name ', '"',NAME,'"'), ''),
IFNULL(CONCAT(' type ', '"',TYPE,'"'), ''),
IFNULL(CONCAT(' incomeCalcBasis ', '"',incomeCalcBasis,'"'), ''),
IFNULL(CONCAT(' raiseStartDateType ',  '"',raiseStartDateType,'"'), ''),
IFNULL(CONCAT(' raiseEndDate ', '"',raiseEndDate, '"'), ''),
IFNULL(CONCAT(' expAror ', '"',expAror,'"'), ''),
IFNULL(CONCAT(' expArorSec ', '"',expArorSec,'"'), ''),
IFNULL(CONCAT(' rewardInterest ', '"',rewardInterest,'"'), ''),
IFNULL(CONCAT(' netUnitShare ', '"', truncate(netUnitShare * 10000, 0), '"'), ''),
CONCAT(' investMin ', '"', truncate(IFNULL(investMin * 10000, 0), 0), '"'),
CONCAT(' investAdditional ', '"', truncate(IFNULL(investAdditional * 10000, 0), 0) ,'"'),
CONCAT(' investMax ', '"', truncate(IFNULL(investMax * 10000, 0), 0) , '"'), 
CONCAT(' minRredeem ', '"', truncate(IFNULL(minRredeem * 10000, 0), 0) , '"'),
CONCAT(' maxRredeem ', '"', truncate(IFNULL(maxRredeem * 10000, 0), 0) ,'"'),
CONCAT(' additionalRredeem ', '"', truncate(IFNULL(additionalRredeem * 10000, 0), 0),'"'),
CONCAT(' netMaxRredeemDay ', '"', truncate(IFNULL(netMaxRredeemDay*10000, 0), 0) ,'"'),
CONCAT(' dailyNetMaxRredeem ', '"', truncate(IFNULL(dailyNetMaxRredeem*10000, 0), 0) ,'"'),
IFNULL(CONCAT(' maxHold ', '"', truncate(IFNULL(maxHold * 10000, 0), 0),'"'), ''),
CONCAT(' singleDailyMaxRedeem ', '"', truncate(IFNULL(singleDailyMaxRedeem, 0), 0), '"'),
IFNULL(CONCAT(' setupDate ', '"', setupDate, '"'), ''),
IFNULL(CONCAT(' state ', '"',state,'"'), ''),
IFNULL(CONCAT(' raiseFailDate ', '"', raiseFailDate, '"'), ''),
IFNULL(CONCAT(' durationPeriodEndDate ', '"', durationPeriodEndDate, '"'), ''),
IFNULL(CONCAT(' currentVolume ', '"', truncate(IFNULL(currentVolume*10000, 0), 0), '"'), ''),
IFNULL(CONCAT(' collectedVolume ', '"', truncate(IFNULL(collectedVolume*10000, 0), 0),'"'), ''),
IFNULL(CONCAT(' maxSaleVolume ', '"', truncate(IFNULl(maxSaleVolume*10000, 0), 0), '"'), ''),
IFNULL(CONCAT(' lockCollectedVolume ', '"', truncate(IFNULL(lockCollectedVolume*10000, 0), 0), '"'), ''),
IFNULL(CONCAT(' repayDate ', '"', repayDate, '"'), ''),
IFNULL(CONCAT(' isOpenPurchase ', '"',isOpenPurchase ,'"'), ''),
IFNULL(CONCAT(' isOpenRemeed ', '"',isOpenRemeed,'"'), ''),
CONCAT(' dealStartTime ', '"', case when dealStartTime is null then '000000' when 0 = LENGTH(dealStartTime) then '000000' else dealStartTime end, '"'),
CONCAT(' dealEndTime ', '"', case when dealEndTime is null then '000000' when 0 = LENGTH(dealEndTime) then '000000' else dealEndTime end, '"'),
IFNULL(CONCAT(' productLabel ', '"', productLabel,'"'), ''),
IFNULL(CONCAT(' previousCurVolume ', '"', truncate(IFNULL(previousCurVolume*10000, 0), 0),'"'), ''),
IFNULL(CONCAT(' previousCurVolumePercent ', '"', truncate(IFNULl(previousCurVolumePercent, 0), 0), '"'), ''),
IFNULL(CONCAT(' isPreviousCurVolume ', '"',isPreviousCurVolume,'"'), ''),
IFNULL(CONCAT(' singleDayRedeemCount ', '"',singleDayRedeemCount,'"'), '')
)
FROM t_gam_product
where oid in ('#poids')
union
SELECT concat(
concat('HMSET m:spv:p:', productOid),
ifnull(concat(' productOid ', productOid), ''),
ifnull(concat(' totalVolume ', truncate(totalVolume*10000, 0)), ''),
ifnull(concat(' lockRedeemHoldVolume ',  truncate(lockRedeemHoldVolume*10000, 0)), '')
)
FROM t_money_publisher_hold 
WHERE accountType='SPV' and productOid in ('#poids');
;
