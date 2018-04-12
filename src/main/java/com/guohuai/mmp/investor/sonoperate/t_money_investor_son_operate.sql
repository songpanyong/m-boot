/*
SQLyog Ultimate v12.09 (64 bit)
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

/*Table structure for table `t_money_investor_son_operate` */

DROP TABLE IF EXISTS `t_money_investor_son_operate`;

CREATE TABLE `t_money_investor_son_operate` (
  `oid` varchar(32) NOT NULL,
  `bankorderoid` varchar(32) DEFAULT NULL,
  `action` varchar(30) DEFAULT NULL COMMENT '操作行为',
  `sid` varchar(32) DEFAULT NULL COMMENT '操作子ID',
  `pid` varchar(32) DEFAULT NULL COMMENT '操作主ID',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
