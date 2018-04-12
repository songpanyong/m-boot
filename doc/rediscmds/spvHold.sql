SELECT concat('HMSET m:spv:p:', productOid),
ifnull(concat(' productOid ', productOid), ''),
ifnull(concat(' totalVolume ', truncate(totalVolume*10000, 0)), ''),
ifnull(concat(' lockRedeemHoldVolume ',  truncate(lockRedeemHoldVolume*10000, 0)), '')
FROM t_money_publisher_hold 
WHERE accountType='SPV' and productOid is not null and
(oid in ('#oids') or '#xoids'='x');

