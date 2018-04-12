-- #product:产品
DELIMITER $$
CREATE TRIGGER trigger_after_insert_product AFTER INSERT
ON t_gam_product FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'product');
END $$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER trigger_after_update_product AFTER UPDATE
ON t_gam_product FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'product');
END $$
DELIMITER ;


-- tradeorder:委托单
DELIMITER $$
CREATE TRIGGER trigger_after_insert_tradeorder AFTER INSERT
ON t_money_investor_tradeorder FOR EACH ROW
BEGIN
	IF (new.orderStatus ='paySuccess' OR new.orderStatus ='accepted' OR new.orderStatus ='confirmed') THEN
		INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'tradeorder');
	END IF;
END $$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER trigger_after_update_tradeorder AFTER UPDATE
ON t_money_investor_tradeorder FOR EACH ROW
BEGIN
 IF (new.orderStatus ='paySuccess' OR new.orderStatus ='accepted' OR new.orderStatus ='confirmed') THEN
		INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'tradeorder');
 END IF;
END $$
DELIMITER ;


-- investorHold:投资者持仓,spvHold:SPV持仓
DELIMITER $$
CREATE TRIGGER trigger_after_update_hold AFTER UPDATE
ON t_money_publisher_hold FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,IF(new.accountType='INVESTOR','investorHold','spvHold'));
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trigger_after_insert_hold AFTER INSERT
ON t_money_publisher_hold FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,IF(new.accountType='INVESTOR','investorHold','spvHold'));
END $$
DELIMITER ;

-- investor:投资人基本账户
DELIMITER $$
CREATE TRIGGER trigger_after_insert_investor_baseAccount AFTER INSERT
ON t_money_investor_baseAccount FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'investorBaseAccount');
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trigger_after_update_investor_baseAccount AFTER UPDATE
ON t_money_investor_baseAccount FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'investorBaseAccount');
END $$
DELIMITER ;


-- investorStatistics:投资人统计
DELIMITER $$
CREATE TRIGGER trigger_after_insert_investor_statistics AFTER INSERT
ON t_money_investor_statistics FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'investorStatistic');
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trigger_after_update_investor_statistics AFTER UPDATE
ON t_money_investor_statistics FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'investorStatistic');
END $$
DELIMITER ;

-- #channel:渠道
DELIMITER $$
CREATE TRIGGER trigger_after_insert_channel AFTER INSERT
ON T_GAM_PRODUCT_CHANNEL FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'channel');
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trigger_after_update_channel AFTER UPDATE
ON T_GAM_PRODUCT_CHANNEL FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'channel');
END $$
DELIMITER ;


-- #label:标签
DELIMITER $$
CREATE TRIGGER trigger_after_insert_label AFTER INSERT
ON T_MONEY_PLATFORM_LABEL_PRODUCT FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'label');
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER trigger_after_update_label AFTER UPDATE
ON T_MONEY_PLATFORM_LABEL_PRODUCT FOR EACH ROW
BEGIN
 INSERT INTO T_MONEY_REDIS_SYNC(syncOid,syncOidType) VALUES(new.oid,'label');
END $$
DELIMITER ;

