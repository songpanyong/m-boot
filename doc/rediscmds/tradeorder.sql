select 
if(
	o.orderStatus='paySuccess',
	concat('HMSET m:i:ps:', o.investorOid, ' ', o.orderCode, ' ',o.orderAmount,
		'\n',
		concat('EXPIRE m:i:ps:', o.investorOid, ' 432000')
		),
	concat('HDEL  m:i:ps:', o.investorOid, ' ', o.orderCode)
)
FROM t_money_investor_tradeorder o
where o.oid in ('#oids') 
or ('#xoids'='x' and o.orderStatus='paySuccess');

