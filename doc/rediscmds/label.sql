SELECT ifnull(concat("HMSET m:p:", a.productOid, 
' productLabel ', '"', GROUP_CONCAT(b.labelCode), '"'), '')
FROM t_money_platform_label_product a
LEFT JOIN t_money_platform_label b
ON a.labelOid = b.oid
WHERE a.oid in ('#oids')  or '#xoids'='x'
GROUP BY a.productOid;

