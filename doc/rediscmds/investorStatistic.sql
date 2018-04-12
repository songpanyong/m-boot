SELECT concat("HMSET m:i:a:", investorOid),
ifnull(concat(' monthWithdrawCount ', monthWithdrawCount), '')
FROM t_money_investor_statistics
WHERE 
oid in ('#oids')  or '#xoids'='x';

