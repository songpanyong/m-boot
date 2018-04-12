/**  redis sync check **/
select oid from t_money_redis_sync
where oid > #last

