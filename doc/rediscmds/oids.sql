set session group_concat_max_len = 65536;
select 
syncOidType,
group_concat(oid separator ',') as ids,
group_concat(syncOid separator '\',\'') as oids,
group_concat(productOid separator '\',\'') as poids
from t_money_redis_sync 
where ((oid > #start and oid <= #end) or (xid is null and oid<= #end)) and syncOid is not null
group by syncOidType;

