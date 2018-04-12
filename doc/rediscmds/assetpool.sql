/**投资者购买产品对应资产池信息 redis key为m:ap:{oid} **/
SELECT concat('HMSET m:ap:', a.oid, 
' purchaseLimit ', truncate(ifnull(a.purchaseLimit * 10000, 0), 0)
)
FROM t_gam_assetpool a
where a.oid in ('#oids') or '#xoids'='x';

