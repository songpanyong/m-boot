/**  redis sync check **/
select max(oid) from t_money_redis_sync;

