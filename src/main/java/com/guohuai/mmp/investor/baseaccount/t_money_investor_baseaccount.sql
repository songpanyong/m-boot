/*
SQLyog v10.2 
MySQL - 5.6.36 : Database - gh_mimosa
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gh_mimosa` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `gh_mimosa`;

/*Table structure for table `t_money_investor_baseaccount` */

DROP TABLE IF EXISTS `t_money_investor_baseaccount`;

CREATE TABLE `t_money_investor_baseaccount` (
  `oid` varchar(32) NOT NULL,
  `memberId` varchar(32) DEFAULT NULL,
  `phoneNum` varchar(32) DEFAULT NULL,
  `realName` varchar(50) DEFAULT '000',
  `idNum` varchar(32) DEFAULT '000',
  `uid` varchar(32) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL COMMENT 'normal:正常; forbidden:禁用;',
  `balance` decimal(20,2) DEFAULT '0.00',
  `owner` varchar(32) DEFAULT NULL COMMENT 'investor:投资者，platform:平台',
  `isFreshman` varchar(32) DEFAULT NULL COMMENT '是：yes，否：no',
  `userPwd` varchar(64) DEFAULT NULL,
  `salt` varchar(32) DEFAULT NULL,
  `payPwd` varchar(64) DEFAULT NULL,
  `paySalt` varchar(32) DEFAULT NULL,
  `source` varchar(32) DEFAULT NULL COMMENT 'backEnd:后台添加，frontEnd:前台注册',
  `channelid` varchar(200) DEFAULT NULL,
  `withdrawFrozenBalance` decimal(20,2) DEFAULT NULL,
  `rechargeFrozenBalance` decimal(20,2) DEFAULT NULL,
  `applyAvailableBalance` decimal(20,2) DEFAULT NULL,
  `withdrawAvailableBalance` decimal(20,2) DEFAULT NULL,
  `markId` varchar(32) DEFAULT NULL COMMENT '标记从哪个账户切换',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid`),
  UNIQUE KEY `IDX_INVESTOR_BASEACCOUNT_phoneNum` (`phoneNum`),
  UNIQUE KEY `IDX_INVESTOR_BASEACCOUNT_uid` (`uid`),
  KEY `IDX_INVESTOR_BASEACCOUNT_memberId` (`memberId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
