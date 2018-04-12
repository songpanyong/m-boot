SELECT CONCAT("HMSET m:i:a:", t1.oid),
IFNULL(CONCAT(' investorOid ', t1.oid), ''),
IFNULL(CONCAT(' isFreshman ', t1.isFreshman), ''),
IFNULL(CONCAT(' status ', t1.status), ''),
IFNULL(CONCAT(' balance ', TRUNCATE(t1.balance * 10000, 0)), ''),
IFNULL(CONCAT(' withdrawFrozenBalance ', TRUNCATE(t1.withdrawFrozenBalance * 10000, 0)), ''),
IFNULL(CONCAT(' rechargeFrozenBalance ', TRUNCATE(t1.rechargeFrozenBalance * 10000, 0)), ''),
IFNULL(CONCAT(' applyAvailableBalance ', TRUNCATE(t1.applyAvailableBalance * 10000, 0)), ''),
IFNULL(CONCAT(' withdrawAvailableBalance ', TRUNCATE(t1.withdrawAvailableBalance * 10000, 0)), '')
FROM t_money_investor_baseaccount t1
where t1.oid in ('#oids')  or '#xoids'='x';

