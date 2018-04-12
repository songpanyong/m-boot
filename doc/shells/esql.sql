-- scheduleLog
INSERT INTO T_MONEY_JOB_LOG_HIS SELECT * FROM T_MONEY_JOB_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 3 DAY);
delete from T_MONEY_JOB_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 3 DAY);

-- MQ
INSERT INTO t_money_serialtask_his SELECT * FROM t_money_serialtask WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 12 DAY) and oid != 'mainTask';
delete from t_money_serialtask WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 12 DAY) and oid != 'mainTask';

-- redisLog
INSERT INTO t_money_cache_execute_log_his SELECT * FROM t_money_cache_execute_log WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 12 DAY);
delete from t_money_cache_execute_log WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 3 DAY);

-- snapshot
INSERT INTO t_money_publisher_investor_hold_snapshot_his SELECT * FROM t_money_publisher_investor_hold_snapshot WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 12 DAY);
delete from t_money_publisher_investor_hold_snapshot WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 3 DAY);

-- coupon_log
INSERT INTO T_MONEY_COUPON_LOG_HIS SELECT * FROM T_MONEY_COUPON_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 15 DAY);
delete from T_MONEY_COUPON_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 15 DAY);

-- tulip_log
INSERT INTO T_MONEY_TULIP_LOG_HIS SELECT * FROM T_MONEY_TULIP_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 15 DAY);
delete from T_MONEY_TULIP_LOG WHERE createTime < DATE_SUB(DATE_FORMAT(SYSDATE(), '%Y%m%d'), INTERVAL 15 DAY);